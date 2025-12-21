import '../config/app_config.dart';
import 'auth_service.dart';
import 'mock_auth_service.dart';

/// Service locator to get the appropriate auth service based on configuration
class AuthServiceFactory {
  static dynamic getAuthService() {
    if (AppConfig.useMockAuth) {
      return MockAuthService();
    } else {
      return AuthService();
    }
  }
}
