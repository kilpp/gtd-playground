import 'package:shared_preferences/shared_preferences.dart';
import '../models/user.dart';
import 'user_service.dart';

/// Mock AuthService for testing Google Sign-In without actual Google authentication
/// This allows testing the app flow without requiring Google Sign-In setup
class MockAuthService {
  static final MockAuthService _instance = MockAuthService._internal();
  factory MockAuthService() => _instance;
  MockAuthService._internal();

  final UserService _userService = UserService();

  static const String _userIdKey = 'current_user_id';
  static const String _userEmailKey = 'current_user_email';
  static const String _userNameKey = 'current_user_name';
  static const String _authTypeKey = 'auth_type';

  User? _currentUser;

  User? get currentUser => _currentUser;
  bool get isLoggedIn => _currentUser != null;

  // Mock Google users for testing
  final List<Map<String, String>> _mockGoogleUsers = [
    {
      'email': 'test.user@gmail.com',
      'name': 'Test User',
      'displayName': 'Test User',
    },
    {
      'email': 'john.doe@gmail.com',
      'name': 'John Doe',
      'displayName': 'John Doe',
    },
    {
      'email': 'jane.smith@gmail.com',
      'name': 'Jane Smith',
      'displayName': 'Jane Smith',
    },
  ];

  /// Initialize and check if user is already logged in
  Future<bool> initialize() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final userId = prefs.getInt(_userIdKey);

      if (userId != null) {
        // Try to fetch user from backend
        try {
          _currentUser = await _userService.getUserById(userId);
          return true;
        } catch (e) {
          // If user not found on backend, clear local storage
          await logout();
          return false;
        }
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  /// Mock Sign in with Google - simulates Google account picker
  Future<User?> signInWithGoogle() async {
    try {
      // Simulate delay for account picker
      await Future.delayed(const Duration(milliseconds: 500));

      // For testing, we'll use the first mock user
      // In a real test environment, you could show a dialog to select which mock user to use
      final mockGoogleUser = _mockGoogleUsers[0];

      print('Mock Google Sign-In: Simulating sign-in as ${mockGoogleUser['email']}');

      // Check if user exists in our backend
      final users = await _userService.getAllUsers();
      User? user;

      try {
        user = users.firstWhere((u) => u.email == mockGoogleUser['email']);
        print('Mock Google Sign-In: User found in backend');
      } catch (e) {
        // User doesn't exist, create new user
        print('Mock Google Sign-In: Creating new user in backend');
        user = await _userService.createUser(
          username: mockGoogleUser['email']!.split('@')[0],
          email: mockGoogleUser['email']!,
          name: mockGoogleUser['name'] ?? mockGoogleUser['email']!.split('@')[0],
        );
      }

      // Save user info
      await _saveUserLocally(user, 'google');
      _currentUser = user;

      print('Mock Google Sign-In: Successfully signed in as ${user.email}');
      return user;
    } catch (e) {
      print('Error in mock Google sign-in: $e');
      rethrow;
    }
  }

  /// Mock Sign in with Google - with user selection
  /// This version allows you to specify which mock user to sign in as
  Future<User?> signInWithGoogleAs(int mockUserIndex) async {
    try {
      if (mockUserIndex < 0 || mockUserIndex >= _mockGoogleUsers.length) {
        throw Exception('Invalid mock user index');
      }

      // Simulate delay for account picker
      await Future.delayed(const Duration(milliseconds: 500));

      final mockGoogleUser = _mockGoogleUsers[mockUserIndex];
      print('Mock Google Sign-In: Simulating sign-in as ${mockGoogleUser['email']}');

      // Check if user exists in our backend
      final users = await _userService.getAllUsers();
      User? user;

      try {
        user = users.firstWhere((u) => u.email == mockGoogleUser['email']);
        print('Mock Google Sign-In: User found in backend');
      } catch (e) {
        // User doesn't exist, create new user
        print('Mock Google Sign-In: Creating new user in backend');
        user = await _userService.createUser(
          username: mockGoogleUser['email']!.split('@')[0],
          email: mockGoogleUser['email']!,
          name: mockGoogleUser['name'] ?? mockGoogleUser['email']!.split('@')[0],
        );
      }

      // Save user info
      await _saveUserLocally(user, 'google');
      _currentUser = user;

      print('Mock Google Sign-In: Successfully signed in as ${user.email}');
      return user;
    } catch (e) {
      print('Error in mock Google sign-in: $e');
      rethrow;
    }
  }

  /// Sign in with username/email
  Future<User?> signInWithCredentials(String identifier) async {
    try {
      final users = await _userService.getAllUsers();

      // Try to find user by username or email
      User? user;
      try {
        user = users.firstWhere(
          (u) => u.username == identifier || u.email == identifier,
        );
      } catch (e) {
        throw Exception(
          'User not found. Please check your credentials or create an account.',
        );
      }

      await _saveUserLocally(user, 'credentials');
      _currentUser = user;

      return user;
    } catch (e) {
      print('Error signing in: $e');
      rethrow;
    }
  }

  /// Create new user account
  Future<User> createAccount({
    required String username,
    required String email,
    String? name,
  }) async {
    try {
      final user = await _userService.createUser(
        username: username,
        email: email,
        name: name ?? username,
      );

      await _saveUserLocally(user, 'credentials');
      _currentUser = user;

      return user;
    } catch (e) {
      print('Error creating account: $e');
      rethrow;
    }
  }

  /// Save user info locally
  Future<void> _saveUserLocally(User user, String authType) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt(_userIdKey, user.id);
    await prefs.setString(_userEmailKey, user.email);
    await prefs.setString(_userNameKey, user.name);
    await prefs.setString(_authTypeKey, authType);
  }

  /// Logout
  Future<void> logout() async {
    try {
      final prefs = await SharedPreferences.getInstance();

      // Clear local storage
      await prefs.remove(_userIdKey);
      await prefs.remove(_userEmailKey);
      await prefs.remove(_userNameKey);
      await prefs.remove(_authTypeKey);

      _currentUser = null;
      print('Mock Google Sign-In: User logged out');
    } catch (e) {
      print('Error logging out: $e');
      rethrow;
    }
  }

  /// Get stored user ID
  Future<int?> getStoredUserId() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getInt(_userIdKey);
  }

  /// Get list of available mock users for testing
  List<Map<String, String>> getMockUsers() {
    return List.from(_mockGoogleUsers);
  }

  /// Add a custom mock user for testing
  void addMockUser({
    required String email,
    required String name,
  }) {
    _mockGoogleUsers.add({
      'email': email,
      'name': name,
      'displayName': name,
    });
  }
}
