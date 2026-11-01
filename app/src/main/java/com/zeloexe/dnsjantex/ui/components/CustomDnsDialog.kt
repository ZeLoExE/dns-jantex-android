package com.zeloexe.dnsjantex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeloexe.dnsjantex.model.DnsServer
import com.zeloexe.dnsjantex.ui.theme.CardBackground
import com.zeloexe.dnsjantex.ui.theme.CardBorder
import com.zeloexe.dnsjantex.ui.theme.CyanNeon
import com.zeloexe.dnsjantex.ui.theme.SurfaceDark
import com.zeloexe.dnsjantex.ui.theme.TextMuted
import com.zeloexe.dnsjantex.ui.theme.TextPrimary
import com.zeloexe.dnsjantex.ui.theme.TextSecondary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDnsDialog(
    onDismiss: () -> Unit,
    onSave: (DnsServer) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var primaryIp by remember { mutableStateOf("") }
    var secondaryIp by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("Custom") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun isValidIp(ip: String): Boolean {
        val parts = ip.trim().split(".")
        if (parts.size != 4) return false
        return parts.all { part ->
            part.toIntOrNull()?.let { it in 0..255 } ?: false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Add Custom DNS",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Configure custom primary & secondary IPv4 DNS addresses.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Server Name", color = TextSecondary) },
                    placeholder = { Text("e.g. My Private DNS", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Primary IP field
                OutlinedTextField(
                    value = primaryIp,
                    onValueChange = { primaryIp = it; errorMessage = null },
                    label = { Text("Primary IP Address *", color = TextSecondary) },
                    placeholder = { Text("e.g. 1.1.1.1", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary IP field
                OutlinedTextField(
                    value = secondaryIp,
                    onValueChange = { secondaryIp = it },
                    label = { Text("Secondary IP Address (Optional)", color = TextSecondary) },
                    placeholder = { Text("e.g. 1.0.0.1", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter a server name."
                        return@Button
                    }
                    if (!isValidIp(primaryIp)) {
                        errorMessage = "Invalid primary IP address format (e.g. 1.1.1.1)."
                        return@Button
                    }
                    if (secondaryIp.isNotBlank() && !isValidIp(secondaryIp)) {
                        errorMessage = "Invalid secondary IP address format."
                        return@Button
                    }

                    val customDns = DnsServer(
                        id = "custom_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(5)}",
                        name = name.trim(),
                        primaryIp = primaryIp.trim(),
                        secondaryIp = secondaryIp.trim(),
                        tags = listOf("Custom", tag),
                        isFavorite = true,
                        latencyMs = null
                    )
                    onSave(customDns)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color(0xFF090D16)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add DNS", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
