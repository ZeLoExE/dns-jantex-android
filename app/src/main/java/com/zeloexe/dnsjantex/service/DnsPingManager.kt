package com.zeloexe.dnsjantex.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Random

object DnsPingManager {

    private val random = Random()

    /**
     * Constructs a standard RFC 1035 DNS Query packet for type A (IPv4) domain.
     */
    private fun buildDnsQueryPacket(domain: String, txId: Short): ByteArray {
        val out = ByteArrayOutputStream()
        // Transaction ID (2 bytes)
        out.write((txId.toInt() ushr 8) and 0xFF)
        out.write(txId.toInt() and 0xFF)
        // Flags: 0x0100 (Standard query, Recursion Desired)
        out.write(0x01)
        out.write(0x00)
        // QDCOUNT: 1
        out.write(0x00)
        out.write(0x01)
        // ANCOUNT: 0
        out.write(0x00)
        out.write(0x00)
        // NSCOUNT: 0
        out.write(0x00)
        out.write(0x00)
        // ARCOUNT: 0
        out.write(0x00)
        out.write(0x00)

        // QNAME (labels)
        for (label in domain.split(".")) {
            if (label.isEmpty()) continue
            val bytes = label.toByteArray(Charsets.US_ASCII)
            out.write(bytes.size)
            out.write(bytes)
        }
        out.write(0x00) // Root label null terminator

        // QTYPE: 0x0001 (A record)
        out.write(0x00)
        out.write(0x01)
        // QCLASS: 0x0001 (IN)
        out.write(0x00)
        out.write(0x01)

        return out.toByteArray()
    }

    /**
     * Measures real DNS query Round-Trip Time (RTT) in milliseconds over UDP port 53.
     * Returns RTT in ms, or -1 if unreachable/timeout.
     */
    suspend fun testDnsLatency(dnsIp: String, timeoutMs: Int = 1500): Long = withContext(Dispatchers.IO) {
        val trimmedIp = dnsIp.trim()
        if (trimmedIp.isEmpty()) return@withContext -1L

        var socket: DatagramSocket? = null
        try {
            val address = InetAddress.getByName(trimmedIp)
            socket = DatagramSocket().apply {
                soTimeout = timeoutMs
            }

            val txId = (random.nextInt(0xFFFF) and 0x7FFF).toShort()
            val queryBytes = buildDnsQueryPacket("google.com", txId)
            val sendPacket = DatagramPacket(queryBytes, queryBytes.size, address, 53)

            val startTime = System.nanoTime()
            socket.send(sendPacket)

            val recvBuffer = ByteArray(512)
            val recvPacket = DatagramPacket(recvBuffer, recvBuffer.size)

            while (true) {
                socket.receive(recvPacket)
                val durationMs = (System.nanoTime() - startTime) / 1_000_000

                if (recvPacket.length >= 12 && recvPacket.address == address) {
                    val respTxId = (((recvBuffer[0].toInt() and 0xFF) shl 8) or (recvBuffer[1].toInt() and 0xFF)).toShort()
                    if (respTxId == txId) {
                        return@withContext durationMs.coerceAtLeast(1L)
                    }
                }
            }
        } catch (e: Exception) {
            -1L
        } finally {
            socket?.close()
        }
    }

    /**
     * Concurrently pings a list of DNS IPs and returns a map of IP -> latency in ms.
     */
    suspend fun pingMultiple(ipList: List<String>, timeoutMs: Int = 1500): Map<String, Long> = withContext(Dispatchers.IO) {
        val jobs = ipList.distinct().map { ip ->
            async {
                val latency = testDnsLatency(ip, timeoutMs)
                ip to latency
            }
        }
        jobs.awaitAll().toMap()
    }
}
