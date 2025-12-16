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

1.  **Set up an Android device**:
    *   **Emulator**: Open Android Studio, go to Device Manager, and start a virtual device.
    *   **Physical Device**: Enable Developer Options and USB Debugging on your phone, then connect it via USB.
2.  **Run the app**:
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
