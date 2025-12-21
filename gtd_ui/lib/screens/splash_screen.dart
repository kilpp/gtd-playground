import 'package:flutter/material.dart';
import '../services/auth_service_factory.dart';
import 'login_screen.dart';
import 'inbox_screen.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  final dynamic _authService = AuthServiceFactory.getAuthService();

  @override
  void initState() {
    super.initState();
    _initialize();
  }

  Future<void> _initialize() async {
    // Add a small delay for better UX
    await Future.delayed(const Duration(milliseconds: 500));

    final isLoggedIn = await _authService.initialize();

    if (mounted) {
      if (isLoggedIn && _authService.currentUser != null) {
        // User is logged in, go to inbox
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(
            builder: (context) =>
                InboxScreen(userId: _authService.currentUser!.id),
          ),
        );
      } else {
        // User is not logged in, go to login screen
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(builder: (context) => const LoginScreen()),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              colorScheme.primaryContainer,
              colorScheme.secondaryContainer,
            ],
          ),
        ),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.check_circle_outline,
                size: 100,
                color: colorScheme.primary,
              ),
              const SizedBox(height: 24),
              Text(
                'GTD',
                style: theme.textTheme.headlineLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: colorScheme.primary,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Getting Things Done',
                style: theme.textTheme.bodyLarge?.copyWith(
                  color: colorScheme.onSurface.withValues(alpha: 0.6),
                ),
              ),
              const SizedBox(height: 48),
              CircularProgressIndicator(color: colorScheme.primary),
            ],
          ),
        ),
      ),
    );
  }
}
