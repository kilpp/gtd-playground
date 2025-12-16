# gtd_ui

A Flutter project for GTD (Getting Things Done) UI.

## Prerequisites

Before you begin, ensure you have met the following requirements:

*   **Flutter SDK**: Install the Flutter SDK by following the instructions for your operating system: [Flutter Installation Guide](https://docs.flutter.dev/get-started/install).
*   **Editor**: VS Code (recommended) or Android Studio.

## Setup

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd gtd_ui
    ```

2.  **Install dependencies**:
    ```bash
    flutter pub get
    ```

## How to Run

You can run the application on various platforms. Ensure you have the necessary build tools installed for your target platform (e.g., Xcode for iOS/macOS, Android Studio for Android, Visual Studio for Windows, etc.).

### 📱 Android

#### 1. Install Android Studio & SDK
1.  Download and install [Android Studio](https://developer.android.com/studio).
2.  Run the setup wizard. Ensure **Android SDK**, **Android SDK Platform-Tools**, and **Android Virtual Device** are selected.
3.  **Important**: Install Command-line Tools & NDK.
    *   Open Android Studio > **Settings** (or Preferences) > **Languages & Frameworks** > **Android SDK** > **SDK Tools**.
    *   Check **Android SDK Command-line Tools (latest)** and **NDK (Side by side)**.
    *   Click **Apply** to install.

#### 2. Configure Flutter
1.  Accept Android licenses:
    ```bash
    flutter doctor --android-licenses
    ```
    (Press `y` to accept all).
2.  Verify setup:
    ```bash
    flutter doctor
    ```

#### 3. Run the App
*   **Emulator**: Open Android Studio > **Device Manager** > Create/Start a virtual device.
*   **Physical Device**: Enable **Developer Options** & **USB Debugging** on your phone, then connect via USB.
*   **Run Command**:
    ```bash
    flutter run
    ```
    Select your Android device if prompted.

### 🍎 iOS (macOS only)

1.  **Set up a Simulator or Device**:
    *   **Simulator**: Run `open -a Simulator`.
    *   **Physical Device**: Connect your iPhone/iPad via USB. You may need to trust the computer on your device and sign the app in Xcode.
2.  **Run the app**:
    ```bash
    flutter run
    ```

### 🌐 Web

1.  **Run on Chrome**:
    ```bash
    flutter run -d chrome
    ```
    This will launch the app in a Chrome browser window.

### 💻 Desktop

#### Windows
*   **Requirements**: Visual Studio 2022 with "Desktop development with C++" workload.
*   **Run**:
    ```bash
    flutter run -d windows
    ```

#### macOS
*   **Requirements**: Xcode.
*   **Run**:
    ```bash
    flutter run -d macos
    ```

#### Linux
*   **Requirements**: Clang, CMake, GTK development headers, Ninja build, pkg-config.
    *   Ubuntu/Debian: `sudo apt-get install clang cmake git ninja-build pkg-config libgtk-3-dev liblzma-dev libstdc++-12-dev`
*   **Run**:
    ```bash
    flutter run -d linux
    ```

## Troubleshooting

*   **Check Environment**: Run `flutter doctor` to see if there are any missing dependencies or configuration issues.
*   **Clean Build**: If you encounter weird build errors, try cleaning the build cache:
    ```bash
    flutter clean
    flutter pub get
    ```
*   **Linux Build Error (cannot find -lstdc++)**:
    If you see an error like `/usr/bin/ld: cannot find -lstdc++` or `CMake Error ... The C++ compiler ... is not able to compile a simple test program`, it means you are missing the C++ standard library development files for your GCC version (often GCC 14 on newer Ubuntu versions).
    Fix it by running:
    ```bash
    sudo apt-get install g++-14 libstdc++-14-dev
    ```
*   **Android License Error (sdkmanager not found)**:
    If `flutter doctor --android-licenses` fails, open Android Studio > Settings > Languages & Frameworks > Android SDK > SDK Tools, and check **Android SDK Command-line Tools (latest)**. Then run the license command again.
*   **Android NDK Error**:
    *   **Missing NDK**: Install it via Android Studio > SDK Tools > **NDK (Side by side)**.
    *   **Corrupted NDK ([CXX1101] ... did not have a source.properties file)**: Delete the corrupted NDK folder (e.g., `rm -rf ~/Android/Sdk/ndk/<version>`) and run `flutter run` again to let Gradle re-download it.
