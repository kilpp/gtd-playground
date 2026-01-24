import 'package:flutter/material.dart';
import '../models/task.dart';
import '../services/task_service.dart';
import 'task_detail_screen.dart';

class NextActionsScreen extends StatefulWidget {
  final int userId;

  const NextActionsScreen({super.key, required this.userId});

  @override
  State<NextActionsScreen> createState() => _NextActionsScreenState();
}

class _NextActionsScreenState extends State<NextActionsScreen> {
  final TaskService _taskService = TaskService();
  List<Task> _tasks = [];
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadNextActions();
  }

  Future<void> _loadNextActions() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // Get tasks with status 'next' for this user
      final tasks = await _taskService.getTasksByStatus('next');

      // Filter for this user and exclude deferred/blocked tasks
      final now = DateTime.now();
      final filteredTasks = tasks.where((task) {
        // Must belong to this user
        if (task.userId != widget.userId) return false;

        // Exclude if deferred to future
        if (task.deferUntil != null && task.deferUntil!.isAfter(now)) {
          return false;
        }

        // Could add dependency checking here if needed
        return true;
      }).toList();

      setState(() {
        _tasks = filteredTasks;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _markAsDone(Task task) async {
    try {
      await _taskService.updateTask(task.id, {'status': 'done'});
      _loadNextActions();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Next Actions'),
        backgroundColor: colorScheme.primaryContainer,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadNextActions,
            tooltip: 'Refresh',
          ),
        ],
      ),
      body: _buildBody(theme, colorScheme),
    );
  }

  Widget _buildBody(ThemeData theme, ColorScheme colorScheme) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_errorMessage != null) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.error_outline, size: 64, color: colorScheme.error),
              const SizedBox(height: 16),
              Text('Error', style: theme.textTheme.titleLarge),
              const SizedBox(height: 8),
              Text(_errorMessage!, textAlign: TextAlign.center),
              const SizedBox(height: 16),
              FilledButton.icon(
                onPressed: _loadNextActions,
                icon: const Icon(Icons.refresh),
                label: const Text('Retry'),
              ),
            ],
          ),
        ),
      );
    }

    if (_tasks.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.task_alt,
                size: 80,
                color: colorScheme.primary.withValues(alpha: 0.3),
              ),
              const SizedBox(height: 16),
              Text('No next actions', style: theme.textTheme.titleLarge),
              const SizedBox(height: 8),
              Text(
                'Available next actions will appear here',
                textAlign: TextAlign.center,
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: colorScheme.onSurface.withValues(alpha: 0.7),
                ),
              ),
            ],
          ),
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadNextActions,
      child: ListView.builder(
        padding: const EdgeInsets.all(8.0),
        itemCount: _tasks.length,
        itemBuilder: (context, index) {
          final task = _tasks[index];
          return Card(
            margin: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 8.0),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: _getPriorityColor(task.priority, colorScheme),
                child: Text(
                  task.priority?.toString() ?? '—',
                  style: const TextStyle(color: Colors.white, fontSize: 12),
                ),
              ),
              title: Text(task.title, style: theme.textTheme.bodyLarge),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  if (task.notes?.isNotEmpty == true) ...[
                    const SizedBox(height: 4),
                    Text(
                      task.notes!,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: colorScheme.onSurface.withValues(alpha: 0.7),
                      ),
                    ),
                  ],
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      if (task.energy != null) ...[
                        Icon(Icons.bolt, size: 14, color: colorScheme.primary),
                        Text(' ${task.energy}'),
                        const SizedBox(width: 8),
                      ],
                      if (task.durationEstMin != null) ...[
                        Icon(
                          Icons.access_time,
                          size: 14,
                          color: colorScheme.primary,
                        ),
                        Text(' ${task.durationEstMin}min'),
                      ],
                    ],
                  ),
                ],
              ),
              trailing: IconButton(
                icon: const Icon(Icons.check_circle_outline),
                onPressed: () => _markAsDone(task),
                tooltip: 'Mark as done',
              ),
              onTap: () {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (context) =>
                        TaskDetailScreen(task: task, userId: widget.userId),
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }

  Color _getPriorityColor(int? priority, ColorScheme colorScheme) {
    if (priority == null) return colorScheme.outline;
    if (priority == 1) return Colors.red;
    if (priority == 2) return Colors.orange;
    if (priority == 3) return Colors.blue;
    return colorScheme.outline;
  }
}
