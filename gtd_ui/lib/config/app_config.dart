/// Configuration for the GTD app
/// Use this to toggle between mock and real services for testing
class AppConfig {
  // Set this to true to use mock authentication (no Google Sign-In required)
  // Set to false to use real Google Sign-In
  static const bool useMockAuth = true;

  // Backend API URL
  static const String backendUrl = 'http://localhost:8080';

  // Debug mode
  static const bool debugMode = true;
}
