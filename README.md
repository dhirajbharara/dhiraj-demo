# Android Alarm App

A simple alarm application for Android with unique flashlight-based dismissal feature.

## Features

- **Simple UI**: Easy-to-use interface for setting alarm time
- **Snooze Functionality**: 5-minute snooze option when alarm rings
- **Unique Dismissal**: Turn flashlight on/off 5 times to stop the alarm
- **Sound & Vibration**: Plays alarm sound and vibrates when triggered
- **Persistent Alarms**: Alarms survive device restarts
- **Full-screen Alert**: Alarm activity appears over lock screen

## Project Structure

```
AlarmApp/
├── app/
│   ├── build.gradle                    # App-level build configuration
│   ├── src/main/
│   │   ├── AndroidManifest.xml         # App permissions and components
│   │   ├── java/com/alarmapp/
│   │   │   ├── MainActivity.kt         # Main activity for setting alarms
│   │   │   ├── AlarmRingingActivity.kt # Alarm ringing screen
│   │   │   ├── AlarmReceiver.kt        # Broadcast receiver for alarms
│   │   │   ├── AlarmService.kt         # Foreground service for alarm
│   │   │   └── BootReceiver.kt         # Receiver for device restart
│   │   └── res/
│   │       ├── layout/
│   │       │   ├── activity_main.xml           # Main screen layout
│   │       │   └── activity_alarm_ringing.xml  # Alarm screen layout
│   │       ├── values/
│   │       │   ├── strings.xml         # String resources
│   │       │   ├── colors.xml          # Color definitions
│   │       │   └── themes.xml          # App themes
│   │       ├── mipmap-*/               # App icons for different densities
│   │       └── xml/                    # Backup and data extraction rules
├── build.gradle                        # Project-level build configuration
├── settings.gradle                     # Project settings
├── gradlew                             # Gradle wrapper script
└── gradle/wrapper/                     # Gradle wrapper files
```

## How to Build

### Prerequisites

1. **Android Studio** (recommended) or Android SDK command-line tools
2. **Java Development Kit (JDK)** 8 or higher
3. **Android SDK** with:
   - Platform API 34 (Android 14)
   - Build Tools 34.0.0
   - Platform Tools

### Building with Android Studio

1. Open Android Studio
2. Click "Open an existing project"
3. Navigate to and select the `AlarmApp` folder
4. Wait for Gradle sync to complete
5. Connect an Android device or start an emulator
6. Click the "Run" button or press `Shift + F10`

### Building with Command Line

1. Navigate to the project directory:
   ```bash
   cd AlarmApp
   ```

2. Make sure ANDROID_HOME is set:
   ```bash
   export ANDROID_HOME=/path/to/your/android/sdk
   ```

3. Build the APK:
   ```bash
   ./gradlew assembleDebug
   ```

4. The APK will be generated at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

## Installation

1. Enable "Unknown Sources" in your device settings
2. Transfer the APK to your device
3. Install by tapping the APK file

## How to Use

### Setting an Alarm

1. Open the Alarm App
2. Use the time picker to select your desired alarm time
3. Tap "Set Alarm"
4. The app will show "Alarm set for [time]"

### When the Alarm Rings

1. The alarm screen will appear with a red background
2. You'll see the current time and instructions
3. **To stop the alarm**: Press the flashlight button 5 times to toggle it on/off
4. **To snooze**: Tap the "Snooze" button (delays alarm by 5 minutes)
5. The "Stop Alarm" button becomes enabled only after 5 flashlight toggles

### Canceling an Alarm

1. From the main screen, tap "Cancel Alarm"
2. The alarm will be removed

## Permissions Required

- `CAMERA` - For flashlight control
- `FLASHLIGHT` - For flashlight access
- `WAKE_LOCK` - To keep device awake during alarm
- `VIBRATE` - For vibration when alarm rings
- `SCHEDULE_EXACT_ALARM` - For precise alarm timing
- `USE_EXACT_ALARM` - Alternative for exact alarms
- `RECEIVE_BOOT_COMPLETED` - To restore alarms after device restart

## Technical Details

### Architecture

- **MVVM Pattern**: Uses Android Architecture Components
- **Broadcast Receivers**: Handle alarm triggers and system events
- **Foreground Service**: Keeps alarm running with notification
- **Shared Preferences**: Stores alarm state persistently

### Key Components

1. **MainActivity**: Main interface for setting/canceling alarms
2. **AlarmRingingActivity**: Full-screen alarm interface with flashlight control
3. **AlarmReceiver**: Handles alarm broadcast and starts ringing activity
4. **AlarmService**: Foreground service that plays sound and vibrates
5. **BootReceiver**: Restores alarms after device restart

### Flashlight Implementation

The app uses Camera2 API to control the device flashlight:
- Detects flashlight availability on device startup
- Toggles flashlight state when button is pressed
- Counts toggles and enables stop button after 5 toggles
- Automatically turns off flashlight when activity is destroyed

## Troubleshooting

### Common Issues

1. **Alarm doesn't ring**: Check if the app has permission to run in background
2. **Flashlight doesn't work**: Ensure camera permission is granted
3. **Alarm doesn't survive restart**: Verify RECEIVE_BOOT_COMPLETED permission
4. **Build fails**: Make sure Android SDK components are installed

### Permissions Setup

On Android 6.0+ (API 23+), you may need to manually grant permissions:
1. Go to Settings > Apps > Alarm App > Permissions
2. Enable Camera, Microphone (for flashlight), and other required permissions

## Compatibility

- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Architecture**: Supports all Android architectures (arm64-v8a, armeabi-v7a, x86, x86_64)

## Development Notes

- Built with Kotlin and Android Jetpack components
- Uses Material Design 3 theming
- Implements proper lifecycle management
- Handles edge cases like missing flashlight hardware
- Responsive design for different screen sizes

## License

This project is provided as-is for educational and personal use.
