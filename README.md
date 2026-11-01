# 🛡️ DNS Jantex - Android Edition (v4.0.2)

<div align="center">

[![Live Interactive Demo](https://img.shields.io/badge/📱%20Live%20Demo-Try%20In%20Browser-00F2FE?style=for-the-badge)](https://zeloexe.github.io/dns-jantex-android/)
[![Download APK](https://img.shields.io/badge/📥%20Download%20APK-v4.0.2%20Release-00F5A0?style=for-the-badge)](https://github.com/ZeLoExE/dns-jantex-android/releases)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-blue?style=for-the-badge&logo=android)](https://github.com/ZeLoExE/dns-jantex-android)

<br/>

> **The modern Android counterpart to [DNS Jantex PC](https://github.com/ZeLoExE/dns-jantex).**  
> Engineered with **Kotlin**, **Jetpack Compose**, and Android **VpnService** API for seamless, non-root DNS switching and gaming / anti-sanction acceleration.

</div>

---

### 🌐 [👉 Click Here to Test Live Interactive Mobile Simulator](https://zeloexe.github.io/dns-jantex-android/)
You can test the exact UI, animated central connect orb, sonar ripples, live timer, and DNS switcher directly in your phone or PC browser!

---

## ✨ Features & Architecture

### 🔘 1. VPN-Style Central Connect Orb
- **Dynamic Multi-Ring Sonar Waves:** Expanding pulse rings radiating when connected.
- **Orbital Sweep:** Rotating cyan/emerald orbit spinner during handshake.
- **Live Session Uptime:** Microsecond accurate ticker (`00:14:32`).
- **Tactile Haptic Pulses:** Smooth subtle vibration upon toggling.

### ⚡ 2. Top DNS Profile Selector
- **One-Tap Quick Switch Card:** Positioned at the top of the dashboard.
- **Real-Time RFC 1035 UDP Port 53 Latency:** Real query benchmark against `google.com`.
- **Preconfigured Resolvers:**
  - 🇮🇷 **Shecan (شکن):** `178.22.122.100` / `185.51.200.2` (Anti-Sanction & Bypass)
  - 🎮 **Electro (الکترو):** `78.157.42.101` / `78.157.42.100` (Gaming & Bypass)
  - 🚀 **Radar Game (رادار گیم):** `10.202.10.10` / `10.202.10.11` (Gaming Low Ping)
  - 💻 **403 Online:** `10.202.10.202` / `10.202.10.102` (Dev & Bypass)
  - 🌍 **Cloudflare DNS:** `1.1.1.1` / `1.0.0.1` (Fast & Privacy)
  - 🌐 **Google Public DNS:** `8.8.8.8` / `8.8.4.4` (Reliable Global)
  - 🛡️ **AdGuard DNS:** `94.140.14.14` / `94.140.15.15` (AdBlock & Privacy)
  - 🔒 **Quad9, OpenDNS, Control D** + Unlimited Custom DNS support.

### 📊 3. High-Speed Ping Benchmark
- **Concurrent Bulk Ping:** Coroutines test all DNS servers simultaneously in under 1 second.
- **Auto-Fastest Mode:** Automatically routes through the lowest-latency DNS server.

### 🔒 4. 100% Non-Root Android VpnService
- Creates a local TUN interface that only routes DNS (port 53 UDP) without touching regular data or slowing down your connection.
- Foreground service with notification controls and one-click Disconnect.

---

## 📥 Download & Install

Download the latest compiled APK from the **[Releases](https://github.com/ZeLoExE/dns-jantex-android/releases)** page and install it on your device.

---

## 🛠️ Build from Source

### Android Studio:
1. Open Android Studio.
2. Select **Open** and choose this project folder.
3. Click **Run (▶)** to deploy to an emulator or connected phone.

### Command Line:
```bash
./gradlew assembleDebug
```

---

<div align="center">
Developed with ❤️ by <a href="https://github.com/ZeLoExE">ZeLoExE</a> & Antigravity.
</div>
