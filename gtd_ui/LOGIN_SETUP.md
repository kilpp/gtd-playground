# GTD Login & Authentication Setup

This document describes the login and authentication implementation for the GTD (Getting Things Done) application.

## Features

- **Modern Login Screen**: Beautiful, responsive login interface with gradient backgrounds
- **Google Single Sign-On (SSO)**: Sign in with your Google account
- **Username/Email Login**: Traditional login with username or email
- **User Registration**: Create new accounts directly from the login screen
- **Session Persistence**: Stay logged in across app restarts
- **Secure Storage**: User credentials stored securely using flutter_secure_storage
- **Logout Functionality**: Clean logout with session cleanup

## Architecture

### Services

1. **AuthService** (`lib/services/auth_service.dart`)
   - Singleton service managing authentication state
   - Handles Google Sign-In integration
   - Manages user sessions and local storage
   - Methods:
     - `initialize()`: Check for existing session
     - `signInWithGoogle()`: Google SSO authentication
     - `signInWithCredentials()`: Traditional login
     - `createAccount()`: New user registration
     - `logout()`: Clean session termination

2. **UserService** (`lib/services/user_service.dart`)
   - Communicates with backend API
   - CRUD operations for user management
   - Login validation

### Screens

1. **SplashScreen** (`lib/screens/splash_screen.dart`)
   - Initial screen shown on app launch
   - Checks for existing session
   - Routes to LoginScreen or InboxScreen

2. **LoginScreen** (`lib/screens/login_screen.dart`)
   - Modern UI with Material 3 design
   - Toggle between Sign In and Sign Up modes
   - Google Sign-In button
   - Form validation
   - Error handling and display

3. **InboxScreen** (Updated)
   - Added user account menu
   - Logout functionality
   - User profile display

## Google Sign-In Setup

To enable Google Sign-In, you need to configure it for each platform:

### Android Setup

1. **Create Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use existing one
   - Add Android app to your project

2. **Get SHA-1 Certificate**:
   ```bash
   # Debug certificate
   keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android -keypass android
   
   # Release certificate (for production)
   keytool -list -v -alias <your-key-alias> -keystore <path-to-keystore>
   ```

3. **Configure Firebase**:
   - In Firebase Console, add the SHA-1 fingerprint
   - Download `google-services.json`
   - Place it in `gtd_ui/android/app/`

4. **Update Build Files**:
   - Add to `android/build.gradle.kts`:
     ```kotlin
     buildscript {
         dependencies {
             classpath("com.google.gms:google-services:4.4.0")
         }
     }
     ```
   
   - Add to `android/app/build.gradle.kts`:
     ```kotlin
     plugins {
         id("com.google.gms.google-services")
     }
     ```

5. **Update AndroidManifest.xml**:
   ```xml
   <meta-data
       android:name="com.google.android.gms.version"
       android:value="@integer/google_play_services_version" />
   ```

### iOS Setup

1. **Configure Firebase**:
   - In Firebase Console, add iOS app
   - Download `GoogleService-Info.plist`
   - Add to `gtd_ui/ios/Runner/` in Xcode

2. **Update Info.plist**:
   Add URL scheme (get from `GoogleService-Info.plist`):
   ```xml
   <key>CFBundleURLTypes</key>
   <array>
       <dict>
           <key>CFBundleURLSchemes</key>
           <array>
               <string>com.googleusercontent.apps.YOUR-CLIENT-ID</string>
           </array>
       </dict>
   </array>
   ```

3. **Update Podfile** (if needed):
   ```ruby
   platform :ios, '12.0'
   ```

### Web Setup

1. **Get Web Client ID**:
   - In Google Cloud Console
   - Create OAuth 2.0 Client ID for Web application
   - Add authorized JavaScript origins: `http://localhost:port`

2. **Update index.html**:
   Add meta tag in `web/index.html`:
   ```html
   <meta name="google-signin-client_id" content="YOUR_WEB_CLIENT_ID.apps.googleusercontent.com">
   ```

### Linux/macOS/Windows

Desktop platforms use the web-based OAuth flow. Ensure you have the web client ID configured.

## Configuration

### Google Sign-In Configuration

Update the GoogleSignIn instance in `auth_service.dart` if you need specific scopes:

```dart
final GoogleSignIn _googleSignIn = GoogleSignIn(
  scopes: [
    'email',
    'profile',
    // Add more scopes as needed
  ],
);
```

### Backend API URL

Update the base URL in `user_service.dart`:

```dart
static const String baseUrl = 'http://localhost:8080/api/users';
```

For production, use your actual backend URL.

## Usage Flow

1. **App Launch**:
   - SplashScreen checks for existing session
   - If logged in → Navigate to InboxScreen
   - If not logged in → Navigate to LoginScreen

2. **Login Options**:
   - **Google Sign-In**: Click "Continue with Google" button
   - **Username/Email**: Enter credentials and click "Sign In"
   - **New User**: Toggle to "Sign Up" and fill registration form

3. **Session Persistence**:
   - User ID and info saved in SharedPreferences
   - Automatic login on app restart

4. **Logout**:
   - Click account icon in InboxScreen
   - Select "Logout" from menu
   - Confirms and clears session

## Dependencies

```yaml
dependencies:
  google_sign_in: ^6.2.2
  shared_preferences: ^2.3.3
  flutter_secure_storage: ^9.2.2
  http: ^1.2.2
```

## Security Considerations

1. **Secure Storage**: Sensitive data stored using flutter_secure_storage
2. **HTTPS**: Use HTTPS for backend API in production
3. **Token Management**: Implement JWT tokens for production use
4. **OAuth Scopes**: Request only necessary Google account permissions
5. **Session Timeout**: Consider implementing session expiration

## Testing

### Test Google Sign-In

1. **Android**:
   ```bash
   flutter run -d android
   ```

2. **iOS**:
   ```bash
   flutter run -d ios
   ```

3. **Web**:
   ```bash
   flutter run -d chrome
   ```

### Test Traditional Login

- Use existing users from backend
- Create new test accounts through Sign Up form

## Troubleshooting

### Google Sign-In Not Working

1. **Check SHA-1**: Ensure correct debug/release SHA-1 in Firebase
2. **Package Name**: Verify package name matches Firebase configuration
3. **OAuth Consent**: Configure OAuth consent screen in Google Cloud Console
4. **Client IDs**: Ensure all platform client IDs are correctly configured

### Backend Connection Issues

1. **URL**: Verify backend URL is correct
2. **CORS**: Ensure backend allows requests from Flutter app
3. **Network**: Check device/emulator can reach backend

### Common Errors

- **PlatformException**: Usually OAuth configuration issue
- **Exception: User not found**: Backend doesn't have the user
- **Network error**: Backend not running or unreachable

## Future Enhancements

- [ ] Password-based authentication
- [ ] JWT token implementation
- [ ] Refresh token handling
- [ ] Biometric authentication
- [ ] Multi-factor authentication (MFA)
- [ ] Social login (Facebook, Apple, etc.)
- [ ] Email verification
- [ ] Password reset functionality

## Backend Requirements

The backend should provide these endpoints:

- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

Future authentication endpoints:
- `POST /api/auth/login` - Login with credentials
- `POST /api/auth/google` - Login with Google token
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout

## Notes

- Current implementation doesn't use passwords (simplified for MVP)
- Google Sign-In creates user automatically if not exists
- Session persists until explicit logout
- Backend should be running at localhost:8080 for local development
