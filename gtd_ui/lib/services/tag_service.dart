import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/tag.dart';

class TagService {
  // Update this to match your backend URL
  static const String baseUrl = 'http://localhost:8080/api/tags';

  /// Fetches all tags for a specific user
  Future<List<Tag>> getTagsByUserId(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?userId=$userId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map((json) => Tag.fromJson(json as Map<String, dynamic>))
            .toList();
      } else {
        throw Exception('Failed to load tags: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching tags: $e');
    }
  }

  /// Creates a new tag
  Future<Tag> createTag({required int userId, required String name}) async {
    try {
      final response = await http.post(
        Uri.parse(baseUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({'userId': userId, 'name': name}),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        return Tag.fromJson(json.decode(response.body) as Map<String, dynamic>);
      } else {
        throw Exception('Failed to create tag: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error creating tag: $e');
    }
  }

  /// Updates an existing tag
  Future<Tag> updateTag({
    required int id,
    required int userId,
    required String name,
  }) async {
    try {
      final response = await http.put(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({'userId': userId, 'name': name}),
      );

      if (response.statusCode == 200) {
        return Tag.fromJson(json.decode(response.body) as Map<String, dynamic>);
      } else {
        throw Exception('Failed to update tag: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error updating tag: $e');
    }
  }

  /// Deletes a tag
  Future<void> deleteTag(int id) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode != 204 && response.statusCode != 200) {
        throw Exception('Failed to delete tag: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error deleting tag: $e');
    }
  }

  /// Gets a single tag by ID
  Future<Tag> getTagById(int id) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        return Tag.fromJson(json.decode(response.body) as Map<String, dynamic>);
      } else {
        throw Exception('Failed to load tag: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching tag: $e');
    }
  }
}
