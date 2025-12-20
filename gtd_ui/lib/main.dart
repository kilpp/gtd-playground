import 'package:flutter/material.dart';
import 'screens/splash_screen.dart';

void main() {
  runApp(const GTDApp());
}

class GTDApp extends StatelessWidget {
  const GTDApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'GTD - Getting Things Done',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
          brightness: Brightness.light,
        ),
        useMaterial3: true,
      ),
      darkTheme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
      ),
      themeMode: ThemeMode.system,
      home: const SplashScreen(),
    );
  }
}
