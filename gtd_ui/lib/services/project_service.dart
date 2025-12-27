import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/project.dart';

class ProjectService {
  // Update this to match your backend URL
  static const String baseUrl = 'http://localhost:8080/api/projects';

  /// Fetches all projects for a specific user
  Future<List<Project>> getProjectsByUserId(int userId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?userId=$userId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map((json) => Project.fromJson(json as Map<String, dynamic>))
            .toList();
      } else {
        throw Exception('Failed to load projects: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching projects: $e');
    }
  }

  /// Fetches all projects for a specific area
  Future<List<Project>> getProjectsByAreaId(int areaId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?areaId=$areaId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map((json) => Project.fromJson(json as Map<String, dynamic>))
            .toList();
      } else {
        throw Exception('Failed to load projects: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching projects: $e');
    }
  }

  /// Fetches all projects with a specific status
  Future<List<Project>> getProjectsByStatus(String status) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?status=$status'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map((json) => Project.fromJson(json as Map<String, dynamic>))
            .toList();
      } else {
        throw Exception('Failed to load projects: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching projects: $e');
    }
  }

  /// Creates a new project
  Future<Project> createProject({
    required int userId,
    int? areaId,
    required String title,
    String? outcome,
    String? notes,
    required String status,
    DateTime? dueDate,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(baseUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': userId,
          'areaId': areaId,
          'title': title,
          'outcome': outcome,
          'notes': notes,
          'status': status,
          'dueDate': dueDate?.toIso8601String().split('T')[0],
        }),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        return Project.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to create project: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error creating project: $e');
    }
  }

  /// Updates an existing project
  Future<Project> updateProject({
    required int id,
    required int userId,
    int? areaId,
    required String title,
    String? outcome,
    String? notes,
    required String status,
    DateTime? dueDate,
  }) async {
    try {
      final response = await http.put(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'userId': userId,
          'areaId': areaId,
          'title': title,
          'outcome': outcome,
          'notes': notes,
          'status': status,
          'dueDate': dueDate?.toIso8601String().split('T')[0],
        }),
      );

      if (response.statusCode == 200) {
        return Project.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to update project: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error updating project: $e');
    }
  }

  /// Deletes a project
  Future<void> deleteProject(int id) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode != 204 && response.statusCode != 200) {
        throw Exception('Failed to delete project: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error deleting project: $e');
    }
  }
}
