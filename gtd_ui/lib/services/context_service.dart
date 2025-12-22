import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/context.dart';

class ContextService {
  // Update this to match your backend URL
  static const String baseUrl = 'http://localhost:8080/api/contexts';

  /// Fetches all contexts for a specific user
  Future<List<Context>> getContextsByUserId(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?userId=$userId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map((json) => Context.fromJson(json as Map<String, dynamic>))
            .toList();
      } else {
        throw Exception('Failed to load contexts: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching contexts: $e');
    }
  }

  /// Creates a new context
  Future<Context> createContext({
    required int userId,
    required String name,
    String? description,
    bool isLocation = false,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(baseUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': userId,
          'name': name,
          'description': description,
          'isLocation': isLocation,
        }),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        return Context.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to create context: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error creating context: $e');
    }
  }

  /// Updates an existing context
  Future<Context> updateContext({
    required int id,
    required int userId,
    required String name,
    String? description,
    bool isLocation = false,
  }) async {
    try {
      final response = await http.put(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': userId,
          'name': name,
          'description': description,
          'isLocation': isLocation,
        }),
      );

      if (response.statusCode == 200) {
        return Context.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to update context: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error updating context: $e');
    }
  }

  /// Deletes a context
  Future<void> deleteContext(int id) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode != 200 && response.statusCode != 204) {
        throw Exception('Failed to delete context: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error deleting context: $e');
    }
  }

  /// Gets a single context by ID
  Future<Context> getContextById(int id) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        return Context.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to load context: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching context: $e');
    }
  }
}
