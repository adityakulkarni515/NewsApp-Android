# News App 📰

![Android CI](https://github.com/adityakulkarni515/NewsApp-Android/workflows/Android%20CI%2FCD%20Pipeline/badge.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg)

A modern Android news application built with Jetpack Compose, following clean architecture principles.

## Features

- 📰 Browse latest news articles
- 🔍 Search functionality
- 📱 Modern UI with Jetpack Compose
- 🏗️ Clean Architecture with MVVM
- 💉 Dependency Injection with Hilt
- 🗄️ Local caching with Room Database
- 🌐 Network calls with Retrofit
- 🎨 Material 3 Design

## Download

### Latest Release
Download the latest APK from the [Releases](https://github.com/adityakulkarni515/NewsApp-Android/releases) page.

### Installation
1. Download the APK file from releases
2. Enable "Install from Unknown Sources" in your Android settings
3. Open the downloaded APK and install

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM + Clean Architecture
- **Dependency Injection:** Hilt
- **Networking:** Retrofit + OkHttp
- **Local Database:** Room
- **Image Loading:** Coil
- **Async:** Kotlin Coroutines + Flow

## Requirements

- Android 7.0 (API 24) or higher
- Minimum SDK: 24
- Target SDK: 36
- Compile SDK: 36

## Building the Project

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/adityakulkarni515/NewsApp-Android.git
cd NewsApp-Android

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedCheck
```

## CI/CD Pipeline

This project uses GitHub Actions for continuous integration and deployment. Every push triggers automated builds, tests, and deployments.

### What's Automated
- ✅ Unit tests
- ✅ Instrumented tests
- ✅ Lint checks
- ✅ APK generation
- ✅ Artifact uploads
- ✅ Release creation

For detailed CI/CD setup and usage, see [CI/CD Setup Guide](.github/CICD_SETUP.md).

### Accessing Builds
- **Development Builds:** Check the [Actions](https://github.com/adityakulkarni515/NewsApp-Android/actions) tab for artifacts from recent commits
- **Release Builds:** Download from the [Releases](https://github.com/adityakulkarni515/NewsApp-Android/releases) page

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/newsapp/
│   │   │   ├── ui/              # Compose UI screens
│   │   │   ├── viewmodel/       # ViewModels
│   │   │   ├── data/            # Data layer (repositories, models)
│   │   │   ├── network/         # API services
│   │   │   └── di/              # Dependency injection modules
│   │   └── res/                 # Resources
│   ├── test/                    # Unit tests
│   └── androidTest/             # Instrumented tests
└── build.gradle.kts
```

## Development

### Running the App
1. Open the project in Android Studio
2. Sync Gradle files
3. Run on emulator or physical device

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires emulator/device)
./gradlew connectedCheck

# Lint checks
./gradlew lint
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

- GitHub: [@adityakulkarni515](https://github.com/adityakulkarni515)
- Repository: [NewsApp-Android](https://github.com/adityakulkarni515/NewsApp-Android)

## Acknowledgments

- News API for providing news data
- Android Jetpack team for amazing libraries
- Open source community
