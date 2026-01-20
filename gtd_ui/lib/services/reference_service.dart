import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/reference.dart';

class ReferenceService {
  // Update this to match your backend URL
  static const String baseUrl = 'http://localhost:8080/api/references';

  /// Fetches all references for a specific user
  Future<List<Reference>> getReferencesByUserId(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?userId=$userId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map((json) => Reference.fromJson(json as Map<String, dynamic>))
            .toList();
      } else {
        throw Exception('Failed to load references: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching references: $e');
    }
  }

  /// Fetches a single reference by ID
  Future<Reference> getReferenceById(int id) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        return Reference.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to load reference: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching reference: $e');
    }
  }

  /// Creates a new reference
  Future<Reference> createReference({
    required int userId,
    required String title,
    String? body,
    String? url,
    String? fileHint,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(baseUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': userId,
          'title': title,
          'body': body,
          'url': url,
          'fileHint': fileHint,
        }),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        return Reference.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to create reference: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error creating reference: $e');
    }
  }

  /// Updates an existing reference
  Future<Reference> updateReference({
    required int id,
    required int userId,
    required String title,
    String? body,
    String? url,
    String? fileHint,
  }) async {
    try {
      final response = await http.put(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': userId,
          'title': title,
          'body': body,
          'url': url,
          'fileHint': fileHint,
        }),
      );

      if (response.statusCode == 200) {
        return Reference.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to update reference: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error updating reference: $e');
    }
  }

  /// Deletes a reference
  Future<void> deleteReference(int id) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode != 204 && response.statusCode != 200) {
        throw Exception('Failed to delete reference: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error deleting reference: $e');
    }
  }
}
