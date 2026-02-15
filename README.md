# 🌿 **GreenSync — V2I Traffic Light Advisor**

**Surf the Green Wave. Drive smoother, smarter, and cleaner.**
GreenSync is an open-source Android/iOS app that calculates the optimal speed to catch upcoming green lights using real-time V2I (Vehicle-to-Infrastructure) data, GPS, and open city traffic APIs.

---

## 🚦 What is GreenSync?

GreenSync helps drivers time their approach to traffic lights by predicting signal phases and advising a speed that keeps them within the “green wave.”
This feature exists only in premium cars (Audi/BMW Traffic Light Information). GreenSync brings it to everyone, open-source.

---

## ✨ Key Features

* **Real-time traffic light predictions** using open-data APIs
* **Optimal speed advisory** (e.g., “Maintain 42 km/h to reach next 3 greens”)
* **Live countdowns** for red/green/yellow phases
* **GPS-based direction + lane estimation**
* **Low-distraction UI** built for safe driving
* **V2X-ready architecture** (V2I + future V2V hooks)
* **Open-source, privacy-first, no tracking**

---

## 🧠 How It Works

1. **GPS Tracking**
   Uses the device’s GPS to detect speed, heading, and exact intersection approach.

2. **Traffic Light API Integration**
   Fetches real-time data from participating smart-city endpoints
   (e.g., NYC DOT, LA ATSAC, Berlin VIZ, Helsinki TL-data, etc.)

3. **Signal Phase Prediction (SPaT)**
   Predicts red → yellow → green cycles with precision.

4. **Green-Wave Computation**
   Determines ideal speed bands for catching multiple greens in sequence.

5. **Adaptive User Feedback**
   Displays clear, non-distracting messages like:

   * “Maintain **45 km/h** to stay in the green wave.”
   * “Red light. **28 seconds** remaining.”

---

## 📱 Platform Support

* **Android** — Kotlin + Jetpack Compose
* **iOS** — SwiftUI (optional cross-platform via Flutter/React Native)
* **Backend** — Node.js or Rust service for traffic-light data normalization
* **Mapping** — Mapbox or OpenStreetMap

---

## 🗺️ Architecture Overview

```
 ┌─────────────────────────────┐
 │         GreenSync App       │
 │  (Android/iOS Frontend)     │
 └───────────────┬─────────────┘
                 │
         GPS / Sensors
                 │
 ┌───────────────▼────────────────┐
 │     Core Logic Engine          │
 │  - Speed Advisor               │
 │  - SPaT Parser                 │
 │  - Wave Prediction             │
 └───────────────┬────────────────┘
                 │
         Traffic Light APIs
                 │
 ┌───────────────▼────────────────┐
 │  GreenSync Backend (Optional)  │
 │  Standardizes SPaT Data        │
 │  Caches city traffic signals   │
 └────────────────────────────────┘
```

---

## 🔧 Installation (Developer)

```bash
git clone https://github.com/techdrivex/GreenSync
cd GreenSync
```

Mobile builds are located under:

```
/app-android
/app-ios
/backend
```

## 🧱 Project Layout

| Path | Description |
| --- | --- |
| `backend/` | Node.js + Express mock SPaT API that models a few smart-city corridors and computes optimal speed suggestions. |
| `app-android/` | Jetpack Compose client that consumes the backend, visualises the current phase, and lists the nearest signals. |
| `app-ios/` | SwiftUI client with the same feature set as Android, sharing the DTOs/flow with async/await networking. |

## 🚀 Getting Started

### 1. Backend API

```bash
cd backend
npm install
npm run dev
```

The service listens on `http://localhost:4000` by default. You can tweak intersections inside `src/data/intersections.js` or change cycle logic in `src/utils.js`.

**New pro endpoints**

| Endpoint | Description |
| --- | --- |
| `GET /api/insights/cities` | Aggregated metrics per connected city (avg delay, next signals, corridor count). |
| `GET /api/routes/forecast?city=Berlin` | Predictive planner that surfaces the best corridors and speeds for an optional city filter. |
| `POST /api/simulations/approach` | Lightweight simulator that tells a driver whether they will stay within the green wave for an intersection + starting speed. |

These additions feed both mobile apps so you can demo real-time analytics even without a live SPaT feed.

### 2. Android app (Jetpack Compose)

* Requires Android Studio Iguana+ and JDK 17.
* Update `BuildConfig.API_BASE_URL` inside `app/build.gradle.kts` if your backend runs on a different host/port. The default `http://10.0.2.2:4000` works for the Android emulator.
* Open `app-android/` in Android Studio and run the **GreenSync** configuration, or execute `./gradlew assembleDebug` after generating a Gradle wrapper locally (`gradle wrapper`).

The UI automatically fetches a recommendation plus three nearby signals and refreshes on pull-to-refresh.
New cards surface:

* **Approach Simulator** — whether you will hold the green wave + recommended adjustment.
* **City Insights** — aggregated smart-city stats for each available corridor.
* **Route Forecast** — a mini control center for planning a multi-signal approach.

### 3. iOS app (SwiftUI)

* Open `app-ios/GreenSync.xcodeproj` with Xcode 15+.
* Ensure the backend is reachable from the simulator. The client points to `http://localhost:4000`; change `baseURL` in `Services/GreenSyncAPI.swift` if you deploy elsewhere (use your machine IP for physical devices).
* Select the **GreenSync** scheme and press Run.

SwiftUI mirrors the Compose experience: simulator insights, signal list, aggregated metrics, and forecast cards all refresh with one tap.

## 🧪 Roadmap

---

## 🧪 Roadmap

* [ ] Add multi-city auto-detection
* [ ] Add CarPlay/Android Auto mode
* [ ] Add V2V extension for EcoPilot integration
* [ ] Build offline prediction model
* [ ] Add haptic feedback mode
* [ ] Add “Efficiency Score” display
* [ ] Add commuter route optimization

---

## 🌍 Why Open Source?

Because smart-city tech shouldn’t be exclusive to luxury cars.
GreenSync aims to democratize V2X intelligence with:

* Transparent data use
* Community-driven improvements
* City-level collaboration
* API documentation for developers

---

## 🤝 Contributing

Pull requests are welcome!
Check the `issues` tab for tasks, bugs, and feature ideas.

---

## 📜 License

MIT License — free for commercial and private use.

---

## 👤 Credits

**Created by Mehmet T. AKALIN**
[https://github.com/makalin](https://github.com/makalin)
[https://github.com/techdrivex](https://github.com/techdrivex)
