package com.zeloexe.dnsjantex

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.zeloexe.dnsjantex.data.DefaultDnsList
import com.zeloexe.dnsjantex.data.PreferencesManager
import com.zeloexe.dnsjantex.model.ConnectionState
import com.zeloexe.dnsjantex.model.DnsServer
import com.zeloexe.dnsjantex.service.JantexVpnService
import com.zeloexe.dnsjantex.ui.screens.MainDashboardScreen
import com.zeloexe.dnsjantex.ui.theme.BackgroundObsidian
import com.zeloexe.dnsjantex.ui.theme.DNSJantexTheme

class MainActivity : ComponentActivity() {

    private lateinit var prefsManager: PreferencesManager
    private var pendingConnectDns: DnsServer? = null

    // VPN Preparation launcher
    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            pendingConnectDns?.let { dns ->
                startVpn(dns)
            }
        } else {
            Toast.makeText(this, "VPN permission is required to route DNS traffic", Toast.LENGTH_SHORT).show()
        }
    }

    // Notification Permission launcher (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = PreferencesManager(this)

        requestNotificationPermissionIfNeeded()

        setContent {
            DNSJantexTheme {
                val connectionState by JantexVpnService.connectionState.collectAsState()
                val connectionStartTime by JantexVpnService.connectionStartTime.collectAsState()

                // DNS List state: default + custom
                var customList by remember { mutableStateOf(prefsManager.getCustomDnsList()) }
                var favoriteIds by remember { mutableStateOf(prefsManager.getFavoriteIds()) }
                val fullDnsList = remember(customList) {
                    customList + DefaultDnsList.get()
                }

                // Selected DNS state
                var selectedDnsId by remember { mutableStateOf(prefsManager.getSelectedDnsId()) }
                val selectedDns = fullDnsList.find { it.id == selectedDnsId } ?: fullDnsList.first()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundObsidian
                ) {
                    MainDashboardScreen(
                        dnsList = fullDnsList,
                        selectedDns = selectedDns,
                        connectionState = connectionState,
                        connectionStartTime = connectionStartTime,
                        favoriteIds = favoriteIds,
                        onToggleConnection = {
                            triggerHapticPulse()
                            if (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.CONNECTING) {
                                JantexVpnService.stopService(this)
                            } else {
                                initiateVpnConnection(selectedDns)
                            }
                        },
                        onSelectDns = { newDns ->
                            selectedDnsId = newDns.id
                            prefsManager.setSelectedDnsId(newDns.id)

                            // If currently connected, reconnect smoothly to new DNS
                            if (connectionState == ConnectionState.CONNECTED) {
                                startVpn(newDns)
                            }
                        },
                        onToggleFavorite = { id ->
                            prefsManager.toggleFavorite(id)
                            favoriteIds = prefsManager.getFavoriteIds()
                        },
                        onAddCustomDns = { newDns ->
                            prefsManager.addCustomDns(newDns)
                            customList = prefsManager.getCustomDnsList()
                            selectedDnsId = newDns.id
                            prefsManager.setSelectedDnsId(newDns.id)
                        },
                        onDeleteCustomDns = { id ->
                            prefsManager.removeCustomDns(id)
                            customList = prefsManager.getCustomDnsList()
                            if (selectedDnsId == id) {
                                selectedDnsId = "shecan"
                                prefsManager.setSelectedDnsId("shecan")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun initiateVpnConnection(dns: DnsServer) {
        val vpnIntent = VpnService.prepare(this)
        if (vpnIntent != null) {
            pendingConnectDns = dns
            vpnPermissionLauncher.launch(vpnIntent)
        } else {
            startVpn(dns)
        }
    }

    private fun startVpn(dns: DnsServer) {
        JantexVpnService.startService(
            context = this,
            dnsName = dns.name,
            primaryIp = dns.primaryIp,
            secondaryIp = dns.secondaryIp
        )
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun triggerHapticPulse() {
        try {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (_: Exception) {}
    }
}
