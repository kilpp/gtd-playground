import 'package:google_sign_in/google_sign_in.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user.dart';
import 'user_service.dart';

class AuthService {
  static final AuthService _instance = AuthService._internal();
  factory AuthService() => _instance;
  AuthService._internal();

  final GoogleSignIn _googleSignIn = GoogleSignIn(scopes: ['email', 'profile']);

  final UserService _userService = UserService();

  static const String _userIdKey = 'current_user_id';
  static const String _userEmailKey = 'current_user_email';
  static const String _userNameKey = 'current_user_name';
  static const String _authTypeKey = 'auth_type';

  User? _currentUser;

  User? get currentUser => _currentUser;
  bool get isLoggedIn => _currentUser != null;

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

  /// Sign in with Google
  Future<User?> signInWithGoogle() async {
    try {
      // Sign out first to ensure account picker shows
      await _googleSignIn.signOut();

      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();

      if (googleUser == null) {
        // User cancelled the sign-in
        return null;
      }

      // Check if user exists in our backend
      final users = await _userService.getAllUsers();
      User? user = users.firstWhere(
        (u) => u.email == googleUser.email,
        orElse: () => throw Exception('User not found'),
      );

      // If user doesn't exist, create one
      try {
        user = users.firstWhere((u) => u.email == googleUser.email);
      } catch (e) {
        // User doesn't exist, create new user
        user = await _userService.createUser(
          username: googleUser.email.split('@')[0],
          email: googleUser.email,
          name: googleUser.displayName ?? googleUser.email.split('@')[0],
        );
      }

      // Save user info
      await _saveUserLocally(user, 'google');
      _currentUser = user;

      return user;
    } catch (e) {
      print('Error signing in with Google: $e');
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
      // Sign out from Google if needed
      final prefs = await SharedPreferences.getInstance();
      final authType = prefs.getString(_authTypeKey);

      if (authType == 'google') {
        await _googleSignIn.signOut();
      }

      // Clear local storage
      await prefs.remove(_userIdKey);
      await prefs.remove(_userEmailKey);
      await prefs.remove(_userNameKey);
      await prefs.remove(_authTypeKey);

      _currentUser = null;
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
}
