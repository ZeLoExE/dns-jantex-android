package com.zeloexe.dnsjantex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeloexe.dnsjantex.model.DnsServer
import com.zeloexe.dnsjantex.ui.theme.CardBackground
import com.zeloexe.dnsjantex.ui.theme.CardBorder
import com.zeloexe.dnsjantex.ui.theme.CyanNeon
import com.zeloexe.dnsjantex.ui.theme.CyanPrimary
import com.zeloexe.dnsjantex.ui.theme.EmeraldNeon
import com.zeloexe.dnsjantex.ui.theme.SurfaceDark
import com.zeloexe.dnsjantex.ui.theme.TextMuted
import com.zeloexe.dnsjantex.ui.theme.TextPrimary
import com.zeloexe.dnsjantex.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DnsSelectorBottomSheet(
    dnsList: List<DnsServer>,
    selectedDnsId: String,
    pingingMap: Map<String, Boolean>,
    favoriteIds: Set<String>,
    onSelectDns: (DnsServer) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onPingDns: (DnsServer) -> Unit,
    onDeleteCustomDns: (String) -> Unit,
    onOpenAddCustom: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All") }

    val tagsList = listOf("All", "Favorites", "Gaming", "Anti-Sanction", "Bypass", "Global", "Custom")

    val filteredList = dnsList.filter { dns ->
        val matchesSearch = dns.name.contains(searchQuery, ignoreCase = true) ||
                dns.primaryIp.contains(searchQuery) ||
                dns.tags.any { it.contains(searchQuery, ignoreCase = true) }

        val matchesTag = when (selectedTag) {
            "All" -> true
            "Favorites" -> favoriteIds.contains(dns.id)
            "Custom" -> dns.isCustom
            else -> dns.tags.any { it.equals(selectedTag, ignoreCase = true) }
        }

        matchesSearch && matchesTag
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DNS Servers",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredList.size} servers available",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Row {
                    // Add Custom Button
                    IconButton(
                        onClick = onOpenAddCustom,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2D4A))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add Custom DNS",
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2D4A))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, tag or IP...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanNeon,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tagsList) { tag ->
                    val isSelected = selectedTag == tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) CyanNeon else CardBackground)
                            .border(
                                1.dp,
                                if (isSelected) CyanNeon else CardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedTag = tag }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            color = if (isSelected) Color(0xFF090D16) else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DNS List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(filteredList, key = { it.id }) { dns ->
                    val isSelected = dns.id == selectedDnsId
                    val isFav = favoriteIds.contains(dns.id)
                    val isPinging = pingingMap[dns.id] == true

                    DnsListItem(
                        dns = dns,
                        isSelected = isSelected,
                        isFavorite = isFav,
                        isPinging = isPinging,
                        onSelect = { onSelectDns(dns) },
                        onToggleFavorite = { onToggleFavorite(dns.id) },
                        onPing = { onPingDns(dns) },
                        onDelete = { onDeleteCustomDns(dns.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DnsListItem(
    dns: DnsServer,
    isSelected: Boolean,
    isFavorite: Boolean,
    isPinging: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPing: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFF132238) else CardBackground)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) CyanNeon else CardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio / Selection status indicator
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = CyanNeon,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(1.5.dp, TextMuted, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dns.name,
                        color = if (isSelected) CyanNeon else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${dns.primaryIp}  •  ${dns.secondaryIp.ifEmpty { "Single" }}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                if (dns.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        dns.tags.take(3).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1F2B45))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Ping badge & Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Latency Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF101B2E))
                        .clickable(enabled = !isPinging, onClick = onPing)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPinging) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp,
                            color = CyanNeon
                        )
                    } else {
                        val latency = dns.latencyMs
                        val latencyColor = when {
                            latency == null -> TextMuted
                            latency < 0 -> Color(0xFFFF5252)
                            latency <= 60 -> EmeraldNeon
                            latency <= 130 -> Color(0xFFFFD600)
                            else -> Color(0xFFFF9100)
                        }
                        Text(
                            text = when {
                                latency == null -> "Ping"
                                latency < 0 -> "Timeout"
                                else -> "$latency ms"
                            },
                            color = latencyColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Favorite Star
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFD600) else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // If Custom, show delete
                if (dns.isCustom) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
