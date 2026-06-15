# 🎯 Event Manager Mobile App

<div align="center">

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Active-brightgreen.svg?style=for-the-badge)](https://github.com/zineb-elgaout/Event_Manager_Mobile_App)

A powerful, feature-rich Android application for managing events with an intuitive user interface and comprehensive functionality.

[Features](#-features) • [Installation](#-installation) • [Usage](#-usage) • [Architecture](#-architecture) • [Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [Project Structure](#-project-structure)
- [Key Features](#-key-features)
- [Architecture](#-architecture)
- [Configuration](#-configuration)
- [Usage](#-usage)
- [Permissions](#-permissions)
- [Development](#-development)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

---

## 🎨 Overview

Event Manager is a comprehensive Android application designed to streamline event management. Whether you're organizing conferences, parties, weddings, or any other events, this app provides a complete solution for event creation, booking, and attendee management with modern UI/UX patterns.


<img width="1806" height="928" alt="Image4" src="https://github.com/user-attachments/assets/1b18c76f-8b8f-4c61-a0a2-9c84f1eddf05" />

<img width="1860" height="749" alt="Image1" src="https://github.com/user-attachments/assets/dfdecd6b-a6bc-4f3e-b83a-0b625d54be01" />

<img width="1819" height="928" alt="Image2" src="https://github.com/user-attachments/assets/2699f186-e0fd-4071-99b9-11e54d8dbc9d" />

<img width="1820" height="928" alt="Image3" src="https://github.com/user-attachments/assets/6d60d795-dcfa-44b2-9724-ec4fa5d56b0e" />


### Key Highlights
- 🚀 **Fast & Responsive** - Optimized performance for seamless user experience
- 🔒 **Secure** - Authentication and encrypted data transmission
- 📱 **User-Friendly** - Intuitive interface with Material Design principles
- 🌐 **Connected** - Real-time synchronization with backend services
- 🎯 **Feature-Rich** - Comprehensive event management capabilities

---

## ✨ Features

### 🔐 Authentication & User Management
- **User Registration** - Create new accounts with validation
- **Login System** - Secure authentication with session management
- **Onboarding Flow** - Guided introduction for new users
- **User Profiles** - Customizable user information

### 📅 Event Management
- **Create Events** - Detailed event creation with multiple parameters
- **Event Browsing** - Discover and explore available events
- **Event Details** - View comprehensive event information
- **Event Filtering** - Search and filter events by categories and dates

### 🎫 Booking System
- **Ticket Booking** - Easy booking interface for event attendees
- **Booking Management** - View and manage your bookings
- **Real-time Updates** - Live booking status updates
- **Confirmation** - Instant booking confirmations

### 📸 QR Code Integration
- **QR Code Generation** - Generate QR codes for events
- **QR Scanning** - Scan QR codes for quick event access
- **Event Validation** - Verify event authenticity via QR codes

### 🔔 Notifications & Alerts
- **Push Notifications** - Timely event reminders and updates
- **Boot Receiver** - App persistence after device restart
- **Vibration Alerts** - Haptic feedback for important notifications
- **Location Tracking** - Geolocation-based event discovery

### 🏠 Home Screen Widget
- **Event Widget** - Quick access to events from home screen
- **Real-time Updates** - Widget data synchronized with app
- **One-tap Access** - Direct navigation to events

### 📍 Location Services
- **GPS Integration** - Fine and coarse location tracking
- **Location-based Events** - Find events near you
- **Map Integration** - View event locations on maps

### 📞 Contact Integration
- **Contact Access** - Import contacts for quick sharing
- **Contact Sync** - Sync event attendees with contacts
- **Contact Management** - Manage event participant information

---

## 📦 Requirements

### Minimum Requirements
- **Android Version**: Android 8.0 (API level 26) or higher
- **Recommended**: Android 12.0+ (API level 31+)
- **RAM**: 2GB minimum (4GB+ recommended)
- **Storage**: 50MB free space
- **Internet**: Active internet connection required

### Development Requirements
- **Java**: JDK 11 or higher
- **Android Studio**: Arctic Fox (2020.3.1) or later
- **Gradle**: 7.0 or higher
- **Build Tools**: Android 31 or higher
- **Language**: 100% Java

---

## 🚀 Installation

### Method 1: Clone and Build from Source

1. **Clone the Repository**
   ```bash
   git clone https://github.com/zineb-elgaout/Event_Manager_Mobile_App.git
   cd Event_Manager_Mobile_App
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Click "Open"

3. **Configure Google Maps API** (Optional)
   - Obtain a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/)
   - Open `main/AndroidManifest.xml`
   - Replace the placeholder with your API key:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE" />
   ```

4. **Build the Project**
   - Select "Build" → "Make Project" from the menu
   - Or press `Ctrl+F9` (Windows) / `Cmd+F9` (Mac)

5. **Run the App**
   - Connect an Android device or use an emulator
   - Click "Run" or press `Shift+F10`

### Method 2: Direct APK Installation
- Download the latest APK from the [Releases](https://github.com/zineb-elgaout/Event_Manager_Mobile_App/releases) section
- Transfer to your Android device
- Open the APK file and tap "Install"

---

## 📁 Project Structure

```
Event_Manager_Mobile_App/
├── main/
│   ├── java/
│   │   └── com/eventmanager/
│   │       ├── activities/           # UI Activities
│   │       │   ├── SplashActivity
│   │       │   ├── OnboardingActivity
│   │       │   ├── LoginActivity
│   │       │   ├── RegisterActivity
│   │       │   ├── MainActivity
│   │       │   ├── EventDetailActivity
│   │       │   ├── QRScanActivity
│   │       │   └── BookingActivity
│   │       ├── fragments/            # UI Fragments
│   │       ├── models/               # Data Models
│   │       ├── adapters/             # RecyclerView Adapters
│   │       ├── services/             # Backend Services
│   │       ├── receivers/            # Broadcast Receivers
│   │       │   └── BootReceiver      # Device boot handling
│   │       ├── widgets/              # Home Screen Widgets
│   │       │   └── EventWidgetProvider
│   │       ├── utils/                # Utility Classes
│   │       └── network/              # API Integration
│   ├── res/
│   │   ├── layout/                   # XML Layout files
│   │   ├── drawable/                 # Drawable Resources
│   │   ├── values/                   # Styles & Strings
│   │   ├── xml/                      # Configuration files
│   │   │   └── widget_info.xml
│   │   └── mipmap/                   # App Icons
│   ├── AndroidManifest.xml           # App Configuration
│   └── ic_launcher-playstore.png     # Play Store Icon
├── test/                             # Unit Tests
├── androidTest/                      # Instrumented Tests
└── README.md                         # This file
```

---

## 🎯 Key Features in Detail

### 🔐 Authentication Flow
- **Splash Screen** - App initialization and branding
- **Onboarding** - User introduction to features
- **Registration** - Create new account with validation
- **Login** - Secure authentication with credentials

### 📅 Event Lifecycle
1. **Discovery** - Browse available events
2. **Viewing** - Access detailed event information
3. **Booking** - Reserve tickets for events
4. **Confirmation** - Receive booking confirmation
5. **Management** - Track your bookings

### 🔄 Real-time Features
- Live event updates
- Instant booking confirmations
- Real-time notification system
- Widget auto-refresh

---

## 🏗️ Architecture

### Architecture Pattern
The app follows **MVVM (Model-View-ViewModel)** architecture with clean code principles:

```
┌─────────────────────────────────────┐
│         UI Layer (Activities)        │
│         (Fragments, Adapters)       │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│      ViewModel Layer (MVVM)         │
│    (Business Logic & State)         │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│      Repository Pattern             │
│    (Data Access Abstraction)        │
└────────────────┬────────────────────┘
                 │
┌─────────┬──────▼──────┬─────────────┐
│ Network │   Database  │  Local File │
│ Services│             │   Storage   │
└─────────┴─────────────┴─────────────┘
```

### Design Patterns Used
- **Singleton Pattern** - Single instances for services
- **Observer Pattern** - LiveData and event notifications
- **Factory Pattern** - Object creation and management
- **Repository Pattern** - Data layer abstraction
- **Dependency Injection** - Loose coupling

---

## ⚙️ Configuration

### Permissions Required
The app requests the following permissions:

```xml
<!-- Network Access -->
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

<!-- Camera & Media -->
<uses-permission android:name="android.permission.CAMERA"/>

<!-- Contacts -->
<uses-permission android:name="android.permission.READ_CONTACTS"/>

<!-- Notifications -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>

<!-- Location -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

<!-- System -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
```

### API Configuration
- Configure backend API endpoints in network configuration
- Set up Google Maps API key in AndroidManifest.xml
- Configure Firebase (if using push notifications)

---

## 📱 Usage

### First Launch
1. Accept permissions when prompted
2. Complete the onboarding tutorial
3. Create an account or login
4. Grant location access for better event discovery

### Creating an Event
1. Navigate to the events section
2. Tap "Create Event"
3. Fill in event details (name, date, location, etc.)
4. Set ticket pricing and availability
5. Add event description and images
6. Tap "Publish"

### Booking an Event
1. Browse available events
2. Tap on event to view details
3. Review event information
4. Select ticket quantity
5. Proceed to checkout
6. Confirm booking
7. Receive confirmation email

### Using QR Codes
1. Generate QR code from event details
2. Share QR code with attendees
3. Attendees scan QR code to quick-access event
4. Verify authenticity via QR validation

---

## 🔒 Security Features

- ✅ **SSL/TLS Encryption** - Secure data transmission
- ✅ **Authentication Tokens** - Session management
- ✅ **Input Validation** - Prevent injection attacks
- ✅ **Secure Storage** - Encrypted local data storage
- ✅ **Permission Enforcement** - Granular permission control

---

## 🛠️ Development

### Build Variants
```bash
# Debug Build
./gradlew assembleDebug

# Release Build
./gradlew assembleRelease

# Run Tests
./gradlew test
./gradlew connectedAndroidTest
```

### Code Style
This project follows Google's Android Code Style Guidelines:
- Naming conventions for classes, methods, and variables
- Documentation through JavaDoc comments
- Proper indentation (4 spaces)
- Consistent code formatting
- **Language**: 100% Java

### Testing
- **Unit Tests** - Located in `test/` directory
- **Instrumented Tests** - Located in `androidTest/` directory
- **Test Execution** - Run via Android Studio or Gradle

---

## 🐛 Troubleshooting

### Common Issues

#### App Crashes on Startup
- **Solution**: Clear app cache → Settings → Apps → Event Manager → Storage → Clear Cache
- **Alternative**: Uninstall and reinstall the app
- **Check**: Ensure minimum Android version 8.0+

#### Permissions Not Granted
- **Solution**: Go to Settings → Apps → Event Manager → Permissions
- **Grant**: Enable required permissions manually
- **Restart**: Close and reopen the app

#### Location Services Not Working
- **Solution**: Enable location services on your device
- **Check**: Settings → Location → Toggle ON
- **Verify**: App has location permissions granted

#### QR Scanning Issues
- **Solution**: Ensure camera permission is granted
- **Lighting**: Use adequate lighting for QR code scanning
- **Distance**: Hold camera 6-8 inches from QR code

#### API Connection Issues
- **Check**: Verify internet connectivity
- **Firewall**: Check if firewalls are blocking API calls
- **Configuration**: Verify API endpoint configuration
- **Credentials**: Check authentication tokens and keys

#### Google Maps Not Displaying
- **Solution**: Verify Google Maps API key in AndroidManifest.xml
- **Quota**: Check API quota in Google Cloud Console
- **Billing**: Ensure billing is enabled for the project

---

## 🤝 Contributing

We welcome contributions to improve the Event Manager app! Here's how you can help:

### Steps to Contribute
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Make** your changes with clear, descriptive commits
4. **Push** to your branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request with detailed description

### Code Guidelines
- Follow the existing code style and conventions
- Write meaningful commit messages
- Include unit tests for new features
- Update documentation as needed
- Ensure no breaking changes

### Reporting Issues
- Use the [Issues](https://github.com/zineb-elgaout/Event_Manager_Mobile_App/issues) tab
- Provide detailed description of the problem
- Include steps to reproduce
- Add screenshots or logs if applicable

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Contact & Support

### Developer
- **GitHub**: [@zineb-elgaout](https://github.com/zineb-elgaout)
- **Email**: [Your Email Here]
- **LinkedIn**: [Your LinkedIn Profile]

### Support
- 📧 **Email Support**: support@eventmanager.app
- 🐛 **Report Bugs**: [GitHub Issues](https://github.com/zineb-elgaout/Event_Manager_Mobile_App/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/zineb-elgaout/Event_Manager_Mobile_App/discussions)

---

## 🙏 Acknowledgments

- Android Documentation and Community
- Material Design Guidelines
- Google Maps API Documentation
- Contributors and testers

---

<div align="center">

### ⭐ If you found this project helpful, please consider giving it a star! ⭐

**Made with ❤️ by [Zineb Elgaout](https://github.com/zineb-elgaout)**

</div>
