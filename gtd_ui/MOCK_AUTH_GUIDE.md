# Mock Google Sign-In for Testing

This guide explains how to use the mock authentication system to test Google Sign-In without requiring actual Google authentication setup.

## Overview

The app now supports two authentication modes:
- **Mock Mode**: Simulates Google Sign-In with predefined test users (no Google setup required)
- **Real Mode**: Uses actual Google Sign-In (requires Google Cloud Console setup)

## Switching Between Mock and Real Auth

Edit the file `lib/config/app_config.dart`:

```dart
class AppConfig {
  // Set to true for mock auth, false for real Google Sign-In
  static const bool useMockAuth = true;  // Change this value
  
  static const String backendUrl = 'http://localhost:8080';
  static const bool debugMode = true;
}
```

## Mock Users Available

The mock auth service comes with 3 pre-configured test users:

1. **test.user@gmail.com** - Test User
2. **john.doe@gmail.com** - John Doe  
3. **jane.smith@gmail.com** - Jane Smith

## Using Mock Auth

### Basic Usage

When `useMockAuth = true`, clicking the "Sign in with Google" button will automatically sign you in as the first mock user (test.user@gmail.com).

### Advanced Usage - Selecting Specific Mock User

If you want to test with different users, you can modify the mock service:

1. Open `lib/services/mock_auth_service.dart`
2. In the `signInWithGoogle()` method, change the index:

```dart
// Change from _mockGoogleUsers[0] to use a different user
final mockGoogleUser = _mockGoogleUsers[1];  // Use John Doe
```

### Adding Custom Mock Users

You can add more test users programmatically:

```dart
final mockAuth = MockAuthService();
mockAuth.addMockUser(
  email: 'custom.user@gmail.com',
  name: 'Custom User',
);
```

## How Mock Auth Works

1. **No External Dependencies**: Mock auth doesn't call Google's servers
2. **Simulated Delay**: Adds a 500ms delay to simulate network latency
3. **Backend Integration**: Still creates/fetches users from your backend API
4. **Full Feature Parity**: All AuthService methods work identically
5. **Console Logging**: Prints debug messages to help track authentication flow

## Testing Workflow

### Test User Login Flow
1. Set `useMockAuth = true`
2. Run the app
3. Click "Sign in with Google"
4. User automatically signs in as test.user@gmail.com
5. Check console logs to see authentication steps

### Test User Creation
1. Delete the backend database or specific user
2. Sign in again
3. Mock service will create a new user in the backend

### Test Different Users
1. Log out from current session
2. Change mock user index in code
3. Sign in again with different mock user

### Test Logout
1. Click logout in the app
2. Should clear session and return to login screen
3. No Google Sign-Out is called (since it's mocked)

## Benefits of Mock Auth

✅ **No Google Setup**: Test immediately without Google Cloud Console  
✅ **Faster Testing**: No network calls to Google servers  
✅ **Repeatable**: Same users every time  
✅ **Offline Testing**: Works without internet  
✅ **Multiple Users**: Easy to test multi-user scenarios  
✅ **No Rate Limits**: Unlimited sign-ins  

## Production Deployment

**IMPORTANT**: Before deploying to production:

1. Set `useMockAuth = false` in `app_config.dart`
2. Ensure Google Sign-In is properly configured
3. Test real Google authentication thoroughly
4. Consider removing mock_auth_service.dart from production builds

## Troubleshooting

### Mock user not appearing in backend
- Check that backend service is running on localhost:8080
- Verify user_service.dart API calls are working
- Check console logs for error messages

### Multiple instances of same user
- This is normal - mock auth will find existing user by email
- User won't be duplicated in backend

### Want to reset testing
- Clear app data/shared preferences
- Or delete users from backend database

## Files Modified

- `lib/services/mock_auth_service.dart` - Mock authentication implementation
- `lib/config/app_config.dart` - Configuration toggle
- `lib/services/auth_service_factory.dart` - Service locator
- `lib/screens/login_screen.dart` - Uses factory instead of direct AuthService
- `lib/screens/splash_screen.dart` - Uses factory instead of direct AuthService
- `lib/screens/inbox_screen.dart` - Uses factory instead of direct AuthService

## Example Console Output

```
Mock Google Sign-In: Simulating sign-in as test.user@gmail.com
Mock Google Sign-In: User found in backend
Mock Google Sign-In: Successfully signed in as test.user@gmail.com
```

## Next Steps

Once you've tested with mock auth and are ready for real Google Sign-In:

1. Set `useMockAuth = false`
2. Follow the Google Sign-In setup guide
3. Configure your Google Cloud Console project
4. Add SHA-1 fingerprints for Android
5. Configure OAuth consent screen
6. Test with real Google accounts
