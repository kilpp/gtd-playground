import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/area.dart';

class AreaService {
  // Update this to match your backend URL
  static const String baseUrl = 'http://localhost:8080/api/areas';

  /// Fetches all areas for a specific user
  Future<List<Area>> getAreasByUserId(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?userId=$userId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map((json) => Area.fromJson(json as Map<String, dynamic>))
            .toList();
      } else {
        throw Exception('Failed to load areas: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching areas: $e');
    }
  }

  /// Creates a new area
  Future<Area> createArea({
    required int userId,
    required String name,
    String? description,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(baseUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': userId,
          'name': name,
          'description': description,
        }),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        return Area.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to create area: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error creating area: $e');
    }
  }

  /// Updates an existing area
  Future<Area> updateArea({
    required int id,
    required int userId,
    required String name,
    String? description,
  }) async {
    try {
      final response = await http.put(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': userId,
          'name': name,
          'description': description,
        }),
      );

      if (response.statusCode == 200) {
        return Area.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to update area: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error updating area: $e');
    }
  }

  /// Deletes an area
  Future<void> deleteArea(int id) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode != 204 && response.statusCode != 200) {
        throw Exception('Failed to delete area: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error deleting area: $e');
    }
  }

  /// Fetches a specific area by ID
  Future<Area> getAreaById(int id) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        return Area.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to load area: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching area: $e');
    }
  }
}
