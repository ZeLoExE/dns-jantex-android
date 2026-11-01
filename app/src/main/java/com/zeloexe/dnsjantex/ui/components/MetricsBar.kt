package com.zeloexe.dnsjantex.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeloexe.dnsjantex.model.ConnectionState
import com.zeloexe.dnsjantex.ui.theme.CardBackground
import com.zeloexe.dnsjantex.ui.theme.CardBorder
import com.zeloexe.dnsjantex.ui.theme.CyanNeon
import com.zeloexe.dnsjantex.ui.theme.CyanPrimary
import com.zeloexe.dnsjantex.ui.theme.EmeraldNeon
import com.zeloexe.dnsjantex.ui.theme.RedDisconnect
import com.zeloexe.dnsjantex.ui.theme.TextMuted
import com.zeloexe.dnsjantex.ui.theme.TextPrimary
import com.zeloexe.dnsjantex.ui.theme.TextSecondary

@Composable
fun MetricsBar(
    connectionState: ConnectionState,
    latencyMs: Long?,
    onPingAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Metric 1: Ping
        MetricItem(
            icon = Icons.Rounded.Speed,
            title = "LATENCY",
            value = when {
                latencyMs == null -> "--"
                latencyMs < 0 -> "Timeout"
                else -> "$latencyMs ms"
            },
            valueColor = when {
                latencyMs == null -> TextSecondary
                latencyMs < 0 -> RedDisconnect
                latencyMs <= 60 -> EmeraldNeon
                latencyMs <= 130 -> Color(0xFFFFD600)
                else -> Color(0xFFFF9100)
            }
        )

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(28.dp)
                .background(CardBorder)
        )

        // Metric 2: Security Status
        MetricItem(
            icon = Icons.Rounded.Security,
            title = "STATUS",
            value = if (connectionState == ConnectionState.CONNECTED) "Protected" else "Exposed",
            valueColor = if (connectionState == ConnectionState.CONNECTED) EmeraldNeon else TextMuted
        )

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(28.dp)
                .background(CardBorder)
        )

        // Action Item: Find Fastest / Ping All
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onPingAllClick)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "BENCHMARK",
                    color = CyanPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Ping All DNS",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    title: String,
    value: String,
    valueColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
