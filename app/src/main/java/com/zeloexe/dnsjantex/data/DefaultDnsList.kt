package com.zeloexe.dnsjantex.data

import com.zeloexe.dnsjantex.model.DnsServer

object DefaultDnsList {
    fun get(): List<DnsServer> = listOf(
        DnsServer(
            id = "shecan",
            name = "Shecan (شکن)",
            primaryIp = "178.22.122.100",
            secondaryIp = "185.51.200.2",
            tags = listOf("Bypass", "Anti-Sanction"),
            isFavorite = true
        ),
        DnsServer(
            id = "electro",
            name = "Electro (الکترو)",
            primaryIp = "78.157.42.101",
            secondaryIp = "78.157.42.100",
            tags = listOf("Gaming", "Bypass"),
            isFavorite = true
        ),
        DnsServer(
            id = "radar",
            name = "Radar Game (رادار گیم)",
            primaryIp = "10.202.10.10",
            secondaryIp = "10.202.10.11",
            tags = listOf("Gaming", "Anti-Sanction"),
            isFavorite = true
        ),
        DnsServer(
            id = "403online",
            name = "403 Online",
            primaryIp = "10.202.10.202",
            secondaryIp = "10.202.10.102",
            tags = listOf("Bypass", "Dev"),
            isFavorite = false
        ),
        DnsServer(
            id = "cloudflare",
            name = "Cloudflare DNS",
            primaryIp = "1.1.1.1",
            secondaryIp = "1.0.0.1",
            tags = listOf("Global", "Fast", "Privacy"),
            isFavorite = true
        ),
        DnsServer(
            id = "google",
            name = "Google Public DNS",
            primaryIp = "8.8.8.8",
            secondaryIp = "8.8.4.4",
            tags = listOf("Global", "Reliable"),
            isFavorite = true
        ),
        DnsServer(
            id = "quad9",
            name = "Quad9",
            primaryIp = "9.9.9.9",
            secondaryIp = "149.112.112.112",
            tags = listOf("Global", "Security"),
            isFavorite = false
        ),
        DnsServer(
            id = "opendns",
            name = "OpenDNS",
            primaryIp = "208.67.222.222",
            secondaryIp = "208.67.220.220",
            tags = listOf("Global", "Family"),
            isFavorite = false
        ),
        DnsServer(
            id = "adguard",
            name = "AdGuard DNS",
            primaryIp = "94.140.14.14",
            secondaryIp = "94.140.15.15",
            tags = listOf("AdBlock", "Security"),
            isFavorite = false
        ),
        DnsServer(
            id = "controld",
            name = "Control D",
            primaryIp = "76.76.2.0",
            secondaryIp = "76.76.10.0",
            tags = listOf("Global", "Fast"),
            isFavorite = false
        )
    )
}
