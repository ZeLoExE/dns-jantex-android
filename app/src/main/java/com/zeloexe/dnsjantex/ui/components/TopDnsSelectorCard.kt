package com.zeloexe.dnsjantex.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeloexe.dnsjantex.model.DnsServer
import com.zeloexe.dnsjantex.ui.theme.CardBackgroundGlass
import com.zeloexe.dnsjantex.ui.theme.CardBorder
import com.zeloexe.dnsjantex.ui.theme.CyanNeon
import com.zeloexe.dnsjantex.ui.theme.CyanPrimary
import com.zeloexe.dnsjantex.ui.theme.EmeraldNeon
import com.zeloexe.dnsjantex.ui.theme.TextMuted
import com.zeloexe.dnsjantex.ui.theme.TextPrimary
import com.zeloexe.dnsjantex.ui.theme.TextSecondary

@Composable
fun TopDnsSelectorCard(
    selectedDns: DnsServer,
    isPinging: Boolean,
    onCardClick: () -> Unit,
    onQuickPing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, shape = RoundedCornerShape(20.dp), spotColor = CyanNeon.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF16233B),
                        Color(0xFF0F1728)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(CardBorder, CyanPrimary.copy(alpha = 0.35f))),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onCardClick)
            .padding(16.dp)
    ) {
        Column {
            // Header Row: Label + Quick Ping button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Dns,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTIVE DNS PROFILE",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Quick Ping Action / Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E2D4A))
                        .clickable(enabled = !isPinging, onClick = onQuickPing)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    if (isPinging) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp,
                            color = CyanNeon
                        )
                    } else {
                        val latency = selectedDns.latencyMs
                        val latencyColor = when {
                            latency == null -> TextMuted
                            latency < 0 -> Color(0xFFFF5252)
                            latency <= 60 -> EmeraldNeon
                            latency <= 130 -> Color(0xFFFFD600)
                            else -> Color(0xFFFF9100)
                        }

                        // Dot indicator
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(latencyColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = when {
                                latency == null -> "Ping"
                                latency < 0 -> "Timeout"
                                else -> "$latency ms"
                            },
                            color = latencyColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Info Row: DNS Name, IP and Dropdown Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedDns.name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${selectedDns.primaryIp}   •   ${selectedDns.secondaryIp.ifEmpty { "Single DNS" }}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Selector Arrow Pill
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E2D4A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Select DNS",
                        tint = CyanNeon,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Tags Row
            if (selectedDns.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    selectedDns.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF223555))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                color = Color(0xFF90CAF9),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
