package com.zeloexe.dnsjantex.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.zeloexe.dnsjantex.MainActivity
import com.zeloexe.dnsjantex.R
import com.zeloexe.dnsjantex.model.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JantexVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_CONNECT -> {
                val dnsName = intent.getStringExtra(EXTRA_DNS_NAME) ?: "Custom DNS"
                val primaryIp = intent.getStringExtra(EXTRA_PRIMARY_IP) ?: "1.1.1.1"
                val secondaryIp = intent.getStringExtra(EXTRA_SECONDARY_IP) ?: ""

                startVpnTunnel(dnsName, primaryIp, secondaryIp)
            }
            ACTION_DISCONNECT -> {
                stopVpnTunnel()
                stopSelf()
            }
        }

        return START_STICKY
    }

    private fun startVpnTunnel(dnsName: String, primaryIp: String, secondaryIp: String) {
        _connectionState.value = ConnectionState.CONNECTING
        _activeDnsName.value = dnsName
        _activePrimaryIp.value = primaryIp

        serviceScope.launch {
            try {
                // Close existing tunnel if any
                vpnInterface?.close()
                vpnInterface = null

                val builder = Builder()
                    .setSession("DNS Jantex: $dnsName")
                    .setMtu(1500)
                    .addAddress("10.254.1.2", 32)
                    .addDnsServer(primaryIp.trim())

                // Route primary DNS traffic through tunnel
                try {
                    builder.addRoute(primaryIp.trim(), 32)
                } catch (_: Exception) {}

                if (secondaryIp.isNotBlank()) {
                    try {
                        builder.addDnsServer(secondaryIp.trim())
                        builder.addRoute(secondaryIp.trim(), 32)
                    } catch (_: Exception) {}
                }

                // Disallow bypassing
                builder.setBlocking(false)

                vpnInterface = builder.establish()

                if (vpnInterface != null) {
                    _connectionStartTime.value = System.currentTimeMillis()
                    _connectionState.value = ConnectionState.CONNECTED

                    val notification = buildForegroundNotification(dnsName, primaryIp)
                    startForeground(NOTIFICATION_ID, notification)
                } else {
                    _connectionState.value = ConnectionState.DISCONNECTED
                    stopSelf()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _connectionState.value = ConnectionState.DISCONNECTED
                stopSelf()
            }
        }
    }

    private fun stopVpnTunnel() {
        _connectionState.value = ConnectionState.DISCONNECTING
        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _connectionState.value = ConnectionState.DISCONNECTED
            _connectionStartTime.value = null
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    override fun onDestroy() {
        stopVpnTunnel()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVpnTunnel()
        super.onRevoke()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "DNS Jantex Active Tunnel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live status and controls for active DNS Jantex tunnel"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(dnsName: String, primaryIp: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, JantexVpnService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this, 1, disconnectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("DNS Jantex: Active")
            .setContentText("Protected & routed via $dnsName ($primaryIp)")
            .setOngoing(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disconnect", disconnectPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    companion object {
        const val ACTION_CONNECT = "com.zeloexe.dnsjantex.CONNECT"
        const val ACTION_DISCONNECT = "com.zeloexe.dnsjantex.DISCONNECT"
        const val EXTRA_DNS_NAME = "extra_dns_name"
        const val EXTRA_PRIMARY_IP = "extra_primary_ip"
        const val EXTRA_SECONDARY_IP = "extra_secondary_ip"

        private const val CHANNEL_ID = "jantex_vpn_channel"
        private const val NOTIFICATION_ID = 4020

        private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
        val connectionState = _connectionState.asStateFlow()

        private val _activeDnsName = MutableStateFlow<String?>("Shecan (شکن)")
        val activeDnsName = _activeDnsName.asStateFlow()

        private val _activePrimaryIp = MutableStateFlow<String?>("178.22.122.100")
        val activePrimaryIp = _activePrimaryIp.asStateFlow()

        private val _connectionStartTime = MutableStateFlow<Long?>(null)
        val connectionStartTime = _connectionStartTime.asStateFlow()

        fun startService(context: Context, dnsName: String, primaryIp: String, secondaryIp: String) {
            val intent = Intent(context, JantexVpnService::class.java).apply {
                action = ACTION_CONNECT
                putExtra(EXTRA_DNS_NAME, dnsName)
                putExtra(EXTRA_PRIMARY_IP, primaryIp)
                putExtra(EXTRA_SECONDARY_IP, secondaryIp)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, JantexVpnService::class.java).apply {
                action = ACTION_DISCONNECT
            }
            context.startService(intent)
        }
    }
}
