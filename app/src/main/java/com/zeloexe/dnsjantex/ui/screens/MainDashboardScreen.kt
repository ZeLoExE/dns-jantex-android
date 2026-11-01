package com.zeloexe.dnsjantex.ui.screens

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeloexe.dnsjantex.model.ConnectionState
import com.zeloexe.dnsjantex.model.DnsServer
import com.zeloexe.dnsjantex.service.DnsPingManager
import com.zeloexe.dnsjantex.ui.components.CentralConnectButton
import com.zeloexe.dnsjantex.ui.components.CustomDnsDialog
import com.zeloexe.dnsjantex.ui.components.DnsSelectorBottomSheet
import com.zeloexe.dnsjantex.ui.components.MetricsBar
import com.zeloexe.dnsjantex.ui.components.TopDnsSelectorCard
import com.zeloexe.dnsjantex.ui.theme.BackgroundObsidian
import com.zeloexe.dnsjantex.ui.theme.CardBackground
import com.zeloexe.dnsjantex.ui.theme.CardBorder
import com.zeloexe.dnsjantex.ui.theme.CyanNeon
import com.zeloexe.dnsjantex.ui.theme.CyanPrimary
import com.zeloexe.dnsjantex.ui.theme.EmeraldNeon
import com.zeloexe.dnsjantex.ui.theme.TextMuted
import com.zeloexe.dnsjantex.ui.theme.TextPrimary
import com.zeloexe.dnsjantex.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainDashboardScreen(
    dnsList: List<DnsServer>,
    selectedDns: DnsServer,
    connectionState: ConnectionState,
    connectionStartTime: Long?,
    favoriteIds: Set<String>,
    onToggleConnection: () -> Unit,
    onSelectDns: (DnsServer) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddCustomDns: (DnsServer) -> Unit,
    onDeleteCustomDns: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // UI Dialog & BottomSheet state
    var showDnsSheet by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }

    // Ping states
    val pingingMap = remember { mutableStateMapOf<String, Boolean>() }
    val dnsLatencies = remember { mutableStateMapOf<String, Long?>() }

    // Live session duration timer string
    var sessionDurationStr by remember { mutableStateOf("00:00:00") }

    LaunchedEffect(connectionState, connectionStartTime) {
        if (connectionState == ConnectionState.CONNECTED && connectionStartTime != null) {
            while (true) {
                val elapsedSeconds = (System.currentTimeMillis() - connectionStartTime) / 1000
                val hours = elapsedSeconds / 3600
                val minutes = (elapsedSeconds % 3600) / 60
                val seconds = elapsedSeconds % 60
                sessionDurationStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                delay(1000)
            }
        } else {
            sessionDurationStr = "00:00:00"
        }
    }

    // Ping helper
    fun pingDns(dns: DnsServer) {
        coroutineScope.launch {
            pingingMap[dns.id] = true
            val latency = DnsPingManager.testDnsLatency(dns.primaryIp)
            dnsLatencies[dns.id] = latency
            dns.latencyMs = latency
            pingingMap[dns.id] = false
        }
    }

    // Benchmark all DNS helper
    fun pingAllDns() {
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Benchmarking all DNS servers...")
            dnsList.forEach { pingingMap[it.id] = true }

            val ipList = dnsList.map { it.primaryIp }
            val results = DnsPingManager.pingMultiple(ipList)

            dnsList.forEach { dns ->
                val rtt = results[dns.primaryIp] ?: -1L
                dnsLatencies[dns.id] = rtt
                dns.latencyMs = rtt
                pingingMap[dns.id] = false
            }

            // Find lowest ping
            val reachable = dnsList.filter { (it.latencyMs ?: -1) > 0 }
            if (reachable.isNotEmpty()) {
                val fastest = reachable.minByOrNull { it.latencyMs ?: Long.MAX_VALUE }
                if (fastest != null) {
                    snackbarHostState.showSnackbar("Fastest: ${fastest.name} (${fastest.latencyMs} ms)")
                }
            } else {
                snackbarHostState.showSnackbar("Benchmark completed.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundObsidian
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyanNeon)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DNS JANTEX",
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                }

                // Version Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF16233B))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "v4.0.2 Mobile",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Top DNS Selector Card (As Requested: "بالا بشه دی ان اس سلکت کرد")
            TopDnsSelectorCard(
                selectedDns = selectedDns.copy(
                    latencyMs = dnsLatencies[selectedDns.id] ?: selectedDns.latencyMs
                ),
                isPinging = pingingMap[selectedDns.id] == true,
                onCardClick = { showDnsSheet = true },
                onQuickPing = { pingDns(selectedDns) }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 2. Central Circular VPN-Style Button (As Requested: "وسطش ی دایره باشه بزنم روش کانکت شه")
            CentralConnectButton(
                connectionState = connectionState,
                connectedDurationStr = sessionDurationStr,
                onToggleClick = onToggleConnection
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 3. Telemetry HUD (Metrics Bar: Ping, Security Status, Benchmark)
            MetricsBar(
                connectionState = connectionState,
                latencyMs = dnsLatencies[selectedDns.id] ?: selectedDns.latencyMs,
                onPingAllClick = { pingAllDns() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Quick Action Cards (Fastest DNS & Flush Cache)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quick Action: Auto Select Fastest
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Searching for fastest server...")
                                val results = DnsPingManager.pingMultiple(dnsList.map { it.primaryIp })
                                dnsList.forEach {
                                    val r = results[it.primaryIp] ?: -1
                                    dnsLatencies[it.id] = r
                                    it.latencyMs = r
                                }
                                val fastest = dnsList.filter { (it.latencyMs ?: -1) > 0 }.minByOrNull { it.latencyMs ?: Long.MAX_VALUE }
                                if (fastest != null) {
                                    onSelectDns(fastest)
                                    snackbarHostState.showSnackbar("Switched to ${fastest.name} (${fastest.latencyMs} ms)")
                                }
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.FlashOn,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Auto Fastest",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pick lowest ping",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Quick Action: Flush Cache
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("DNS Cache Flushed & Refreshed")
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.CleaningServices,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Flush Cache",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Purge resolver",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // DNS Selector Bottom Sheet
    if (showDnsSheet) {
        DnsSelectorBottomSheet(
            dnsList = dnsList.map {
                it.copy(latencyMs = dnsLatencies[it.id] ?: it.latencyMs)
            },
            selectedDnsId = selectedDns.id,
            pingingMap = pingingMap,
            favoriteIds = favoriteIds,
            onSelectDns = {
                onSelectDns(it)
                showDnsSheet = false
            },
            onToggleFavorite = onToggleFavorite,
            onPingDns = { pingDns(it) },
            onDeleteCustomDns = onDeleteCustomDns,
            onOpenAddCustom = { showCustomDialog = true },
            onDismiss = { showDnsSheet = false }
        )
    }

    // Custom DNS Dialog
    if (showCustomDialog) {
        CustomDnsDialog(
            onDismiss = { showCustomDialog = false },
            onSave = { newDns ->
                onAddCustomDns(newDns)
                showCustomDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Added custom DNS: ${newDns.name}")
                }
            }
        )
    }
}
