package com.zeloexe.dnsjantex.model

import com.google.gson.annotations.SerializedName

data class DnsServer(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("primary_ip") val primaryIp: String,
    @SerializedName("secondary_ip") val secondaryIp: String = "",
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
    @SerializedName("latency_ms") var latencyMs: Long? = null
) {
    val isCustom: Boolean
        get() = id.startsWith("custom_")
}
