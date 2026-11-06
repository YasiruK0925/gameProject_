[![Build Android APK](https://github.com/YasiruK0925/gameProject_/actions/workflows/blank.yml/badge.svg)](https://github.com/YasiruK0925/gameProject_/actions/workflows/blank.yml)
# Mini Arcade Game Project 🎮

A lightweight, Gradle-based mobile game project configured for Android deployment, built and deployed using automated CI/CD pipelines with the assistance of AI.

## 🚀 About The Project
This project is an arcade-style mobile game built with a standard Android/Gradle layout. Portions of this project—including the build automation, CI/CD workflow configuration, and error troubleshooting—were developed with the help of AI collaboration. 

It is optimized to be built via GitHub Actions without requiring heavy local IDE installations like Android Studio.

## 📁 Project Structure
* `app/` - Contains the core game source code, assets, and Android manifest (with the compiled `app-debug.apk` output path located at `app/build/outputs/apk/debug/app-debug.apk`).
* `gradle/` - Gradle wrapper and dependency configuration files.
* `.github/workflows/` - CI/CD pipeline configurations (`blank.yml`) for automated APK compilation via GitHub Actions.

## 🛠️ Built With
* **Java / Android SDK** - Core framework and build tools.
* **Gradle** - Build automation system.
* **GitHub Actions** - Cloud-based automated APK compilation.
* **AI Assistance** - Used for troubleshooting build configurations, writing GitHub Actions workflows, and project documentation.

## 📱 How to Get & Use the APK

### 1. Downloading from GitHub Actions
1. Navigate to the **Actions** tab in this repository.
2. Click on the latest successful build (marked with a green checkmark `✅`).
3. Scroll down to the **Artifacts** section and click **app-debug** to download the ZIP file.
4. Extract the ZIP file on your computer to find your `app-debug.apk` file inside.

### 2. Installing and Playing on Your Android Phone
1. **Transfer the APK:** Send the `app-debug.apk` file from your computer to your Android phone via a USB cable, Bluetooth, Google Drive, or messaging apps like Telegram or WhatsApp.
2. **Install the App:** Open your phone's file manager, tap on `app-debug.apk`. If prompted with a security warning about installing apps from unknown sources, tap **Settings** and allow installation from your file manager or browser.
3. **Play:** Tap **Install**, open the game from your app drawer, and enjoy playing!

### 3. Building Future Updates
Whenever you make code changes and push them to the `main` branch, GitHub Actions will automatically compile a new version. Simply repeat the steps in the Actions tab to grab your updated APK!

## 📄 License
This project is open-source and available for personal and educational use.