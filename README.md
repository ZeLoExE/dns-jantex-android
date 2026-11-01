# 🛡️ DNS Jantex - Android Edition (v4.0.2)

> **Official Android Version of [DNS Jantex PC](https://github.com/ZeLoExE/dns-jantex)**  
> Developed with modern **Native Kotlin**, **Jetpack Compose**, and Android **VpnService** API.

---

## ✨ Features & Architecture

- **🔘 VPN-Style Central Connect Orb**:
  - Immersive cybernetic aesthetic with dynamic multi-ring sonar pulses.
  - Orbiting particle sweep during connection.
  - Real-time session uptime counter (`00:12:45`).
  - Haptic feedback on tap.

- **⚡ Top DNS Profile Selector**:
  - Quick-switch card located at the top of the dashboard.
  - Real-time RFC 1035 UDP DNS ping latency benchmark.
  - Preloaded with the top Iranian Anti-Sanction & Gaming DNS profiles (`Shecan`, `Electro`, `Radar Game`, `403 Online`) alongside top global resolvers (`Cloudflare`, `Google`, `AdGuard`, `Quad9`, `Control D`, `OpenDNS`).

- **📊 High-Performance Ping Benchmark**:
  - Concurrent bulk DNS latency tester powered by Kotlin Coroutines (`Dispatchers.IO`).
  - **Auto-Fastest Mode**: Automatically detects and connects to the resolver with the lowest ping.

- **➕ Custom DNS Servers**:
  - Add, save, and delete custom DNS configurations with name, IP address, and tags.
  - Persistent storage using local encrypted preferences.

- **🔒 Native Android VpnService**:
  - 100% Non-root requirement.
  - Direct local TUN interface routing DNS queries (port 53) to target resolvers.
  - Foreground service with notification controls and quick disconnect button.

---

## 🚀 How to Run & Build

### Option 1: Android Studio (Recommended)
1. Open **Android Studio** (Hedgehog, Iguana, Jellyfish, or newer).
2. Select **Open** and choose the `dns-jantex-android` folder on your Desktop.
3. Wait for Gradle Sync to complete.
4. Connect your Android device or start an emulator.
5. Click the green **Run (▶)** button!

### Option 2: Build APK via Command Line
```bash
cd dns-jantex-android
./gradlew assembleDebug
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🎨 UI/UX Design Palette
- **Obsidian Dark Surface**: `#090D16` / `#101726`
- **Neon Electric Cyan**: `#00F2FE`
- **Emerald Pulse**: `#00F5A0`
- **Card Glass**: `#162034` with subtle radial lighting

Designed & Architected by **ZeLoExE** & Antigravity.
