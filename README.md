# Krishi Mitra - Crop Recommendation App

Jetpack Compose Android app built with **MVVM + Clean Architecture**.

## Features
- Farmer-friendly flow with 7 screens: Splash, Home, Input Form, Upload, Loading, Result, History
- Retrofit API integration for `POST /api/recommend`
- StateFlow-driven UI state handling (`Idle`, `Loading`, `Success`, `Error`)
- Offline fallback to cached last recommendation
- Retry support on failures
- Loading shimmer placeholders

## Architecture
- `presentation/` - screens, navigation, viewmodel
- `domain/` - models, repository contract, use cases
- `data/` - retrofit API, DTOs, mappers, repository implementation, network monitor

## Backend URL
`app/build.gradle.kts` currently uses:

```kotlin
buildConfigField("String", "BASE_URL", "\"https://example.com/\"")
```

Replace with your Node.js server base URL (must end with `/`).

## Run
```powershell
cd "D:\Krishi Mitra\Frontend"
.\gradlew.bat :app:assembleDebug
```

