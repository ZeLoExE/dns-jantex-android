package com.zeloexe.dnsjantex.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeloexe.dnsjantex.model.ConnectionState
import com.zeloexe.dnsjantex.ui.theme.CyanNeon
import com.zeloexe.dnsjantex.ui.theme.CyanPrimary
import com.zeloexe.dnsjantex.ui.theme.EmeraldGlow
import com.zeloexe.dnsjantex.ui.theme.EmeraldNeon
import com.zeloexe.dnsjantex.ui.theme.TextMuted
import com.zeloexe.dnsjantex.ui.theme.TextPrimary
import com.zeloexe.dnsjantex.ui.theme.TextSecondary

@Composable
fun CentralConnectButton(
    connectionState: ConnectionState,
    connectedDurationStr: String,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth press feedback scale
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "press_scale"
    )

    // Infinite pulse animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rings")

    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.48f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    // Connecting spinner rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Breathing glow in idle state
    val idleBreathingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(260.dp)
            .scale(pressScale)
    ) {
        // Active radar wave rings (Only when CONNECTED)
        if (connectionState == ConnectionState.CONNECTED) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = 88.dp.toPx()

                // Wave Ring 1
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EmeraldNeon.copy(alpha = pulseAlpha1 * 0.4f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * pulseScale1
                    ),
                    radius = baseRadius * pulseScale1,
                    center = center
                )
                drawCircle(
                    color = EmeraldNeon.copy(alpha = pulseAlpha1 * 0.7f),
                    radius = baseRadius * pulseScale1,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Wave Ring 2
                drawCircle(
                    color = CyanNeon.copy(alpha = pulseAlpha2 * 0.5f),
                    radius = baseRadius * pulseScale2,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        // Connecting Rotating Orbit Ring
        if (connectionState == ConnectionState.CONNECTING) {
            Canvas(modifier = Modifier.size(200.dp)) {
                rotate(rotationAngle) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color.Transparent,
                                CyanNeon.copy(alpha = 0.2f),
                                CyanNeon,
                                EmeraldNeon
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 280f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Outer Glow Ring (Static or pulsing)
        val outerBorderBrush = when (connectionState) {
            ConnectionState.CONNECTED -> Brush.linearGradient(
                listOf(EmeraldNeon, CyanNeon)
            )
            ConnectionState.CONNECTING -> Brush.linearGradient(
                listOf(CyanNeon.copy(alpha = 0.5f), CyanPrimary.copy(alpha = 0.8f))
            )
            ConnectionState.DISCONNECTED, ConnectionState.DISCONNECTING -> Brush.linearGradient(
                listOf(Color(0xFF263554), Color(0xFF192238))
            )
        }

        // Central Orb Body
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(174.dp)
                .shadow(
                    elevation = if (connectionState == ConnectionState.CONNECTED) 24.dp else 8.dp,
                    shape = CircleShape,
                    spotColor = if (connectionState == ConnectionState.CONNECTED) EmeraldNeon else CyanNeon
                )
                .clip(CircleShape)
                .background(
                    brush = when (connectionState) {
                        ConnectionState.CONNECTED -> Brush.radialGradient(
                            listOf(Color(0xFF0F3E3B), Color(0xFF082221), Color(0xFF07191B))
                        )
                        ConnectionState.CONNECTING -> Brush.radialGradient(
                            listOf(Color(0xFF132B45), Color(0xFF0C1929))
                        )
                        ConnectionState.DISCONNECTED, ConnectionState.DISCONNECTING -> Brush.radialGradient(
                            listOf(Color(0xFF172238), Color(0xFF0F1726))
                        )
                    }
                )
                .drawBehind {
                    drawCircle(
                        brush = outerBorderBrush,
                        radius = size.width / 2f - 2.dp.toPx(),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onToggleClick
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Central Icon
                when (connectionState) {
                    ConnectionState.CONNECTED -> {
                        Icon(
                            imageVector = Icons.Rounded.Shield,
                            contentDescription = "Connected",
                            tint = EmeraldNeon,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                    ConnectionState.CONNECTING -> {
                        Icon(
                            imageVector = Icons.Rounded.VpnKey,
                            contentDescription = "Connecting",
                            tint = CyanNeon,
                            modifier = Modifier
                                .size(48.dp)
                                .scale(idleBreathingAlpha)
                        )
                    }
                    ConnectionState.DISCONNECTED, ConnectionState.DISCONNECTING -> {
                        Icon(
                            imageVector = Icons.Rounded.PowerSettingsNew,
                            contentDescription = "Disconnected",
                            tint = CyanPrimary.copy(alpha = idleBreathingAlpha),
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status Label
                val statusText = when (connectionState) {
                    ConnectionState.CONNECTED -> "CONNECTED"
                    ConnectionState.CONNECTING -> "CONNECTING..."
                    ConnectionState.DISCONNECTING -> "STOPPING..."
                    ConnectionState.DISCONNECTED -> "TAP TO CONNECT"
                }

                val statusColor = when (connectionState) {
                    ConnectionState.CONNECTED -> EmeraldNeon
                    ConnectionState.CONNECTING -> CyanNeon
                    ConnectionState.DISCONNECTED, ConnectionState.DISCONNECTING -> TextSecondary
                }

                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                // If connected, show live session timer!
                if (connectionState == ConnectionState.CONNECTED && connectedDurationStr.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = connectedDurationStr,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
