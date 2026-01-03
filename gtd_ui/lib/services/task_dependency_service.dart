import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/task_dependency.dart';

class TaskDependencyService {
  static const String baseUrl = 'http://localhost:8080/api/task-dependencies';

  /// Get all task dependencies
  Future<List<TaskDependency>> getAllDependencies() async {
    try {
      final response = await http.get(
        Uri.parse(baseUrl),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map(
              (json) => TaskDependency.fromJson(json as Map<String, dynamic>),
            )
            .toList();
      } else {
        throw Exception('Failed to load dependencies: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching dependencies: $e');
    }
  }

  /// Get dependencies for a specific task (tasks that this task depends on)
  Future<List<TaskDependency>> getDependenciesForTask(int taskId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?taskId=$taskId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map(
              (json) => TaskDependency.fromJson(json as Map<String, dynamic>),
            )
            .toList();
      } else {
        throw Exception('Failed to load dependencies: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error fetching dependencies: $e');
    }
  }

  /// Get tasks that depend on a specific task (blockers)
  Future<List<TaskDependency>> getBlockingDependencies(
    int dependsOnTaskId,
  ) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl?dependsOnTaskId=$dependsOnTaskId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> jsonList = json.decode(response.body) as List;
        return jsonList
            .map(
              (json) => TaskDependency.fromJson(json as Map<String, dynamic>),
            )
            .toList();
      } else {
        throw Exception(
          'Failed to load blocking dependencies: ${response.statusCode}',
        );
      }
    } catch (e) {
      throw Exception('Error fetching blocking dependencies: $e');
    }
  }

  /// Create a new task dependency
  Future<TaskDependency> createDependency({
    required int taskId,
    required int dependsOnTaskId,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(baseUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'taskId': taskId,
          'dependsOnTaskId': dependsOnTaskId,
        }),
      );

      if (response.statusCode == 201) {
        return TaskDependency.fromJson(
          json.decode(response.body) as Map<String, dynamic>,
        );
      } else {
        throw Exception('Failed to create dependency: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error creating dependency: $e');
    }
  }

  /// Delete a specific task dependency
  Future<void> deleteDependency({
    required int taskId,
    required int dependsOnTaskId,
  }) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/$taskId/$dependsOnTaskId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode != 204) {
        throw Exception('Failed to delete dependency: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Error deleting dependency: $e');
    }
  }

  /// Delete all dependencies for a task
  Future<void> deleteDependenciesForTask(int taskId) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/task/$taskId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode != 204) {
        throw Exception(
          'Failed to delete dependencies: ${response.statusCode}',
        );
      }
    } catch (e) {
      throw Exception('Error deleting dependencies: $e');
    }
  }
}
