import 'package:flutter/material.dart';
import '../models/task.dart';
import '../services/task_service.dart';
import '../services/task_dependency_service.dart';

class TaskDetailScreen extends StatefulWidget {
  final Task task;
  final int userId;

  const TaskDetailScreen({super.key, required this.task, required this.userId});

  @override
  State<TaskDetailScreen> createState() => _TaskDetailScreenState();
}

class _TaskDetailScreenState extends State<TaskDetailScreen> {
  final TaskService _taskService = TaskService();
  final TaskDependencyService _dependencyService = TaskDependencyService();

  List<Task> _dependencyTasks = [];
  List<Task> _blockerTasks = [];
  List<Task> _availableTasks = [];
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadDependencies();
  }

  Future<void> _loadDependencies() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // Load dependencies for this task
      final deps = await _dependencyService.getDependenciesForTask(
        widget.task.id,
      );
      final blocks = await _dependencyService.getBlockingDependencies(
        widget.task.id,
      );

      // Load the actual task objects
      final depTasks = <Task>[];
      for (var dep in deps) {
        try {
          final task = await _taskService.getTaskById(dep.dependsOnTaskId);
          depTasks.add(task);
        } catch (e) {
          print('Error loading dependency task: $e');
        }
      }

      final blockTasks = <Task>[];
      for (var block in blocks) {
        try {
          final task = await _taskService.getTaskById(block.taskId);
          blockTasks.add(task);
        } catch (e) {
          print('Error loading blocker task: $e');
        }
      }

      // Load all tasks for adding dependencies
      final allTasks = await _taskService.getTasksByUserId(widget.userId);
      final available = allTasks
          .where(
            (t) =>
                t.id != widget.task.id && // Not self
                !deps.any(
                  (d) => d.dependsOnTaskId == t.id,
                ), // Not already a dependency
          )
          .toList();

      setState(() {
        _dependencyTasks = depTasks;
        _blockerTasks = blockTasks;
        _availableTasks = available;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _addDependency() async {
    if (_availableTasks.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('No available tasks to add as dependencies'),
        ),
      );
      return;
    }

    final selectedTask = await showDialog<Task>(
      context: context,
      builder: (context) => _SelectTaskDialog(tasks: _availableTasks),
    );

    if (selectedTask != null && mounted) {
      try {
        await _dependencyService.createDependency(
          taskId: widget.task.id,
          dependsOnTaskId: selectedTask.id,
        );
        if (mounted) {
          ScaffoldMessenger.of(
            context,
          ).showSnackBar(const SnackBar(content: Text('Dependency added')));
          _loadDependencies();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Error adding dependency: $e')),
          );
        }
      }
    }
  }

  Future<void> _removeDependency(int dependsOnTaskId) async {
    try {
      await _dependencyService.deleteDependency(
        taskId: widget.task.id,
        dependsOnTaskId: dependsOnTaskId,
      );
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(const SnackBar(content: Text('Dependency removed')));
        _loadDependencies();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error removing dependency: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Task Details'),
        backgroundColor: colorScheme.primaryContainer,
        actions: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () {
              // TODO: Navigate to edit screen
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Edit - Coming soon')),
              );
            },
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _errorMessage != null
          ? _buildError(theme, colorScheme)
          : _buildContent(theme, colorScheme),
    );
  }

  Widget _buildError(ThemeData theme, ColorScheme colorScheme) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 64, color: colorScheme.error),
            const SizedBox(height: 16),
            Text(
              'Error loading dependencies',
              style: theme.textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text(_errorMessage!, textAlign: TextAlign.center),
            const SizedBox(height: 24),
            FilledButton.icon(
              onPressed: _loadDependencies,
              icon: const Icon(Icons.refresh),
              label: const Text('Retry'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContent(ThemeData theme, ColorScheme colorScheme) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Task Info Card
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    widget.task.title,
                    style: theme.textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  if (widget.task.notes != null &&
                      widget.task.notes!.isNotEmpty) ...[
                    const SizedBox(height: 12),
                    Text(widget.task.notes!, style: theme.textTheme.bodyMedium),
                  ],
                  const SizedBox(height: 16),
                  Wrap(
                    spacing: 12,
                    runSpacing: 8,
                    children: [
                      _buildChip(
                        icon: Icons.label,
                        label: widget.task.status.toUpperCase(),
                        color: colorScheme.primary,
                      ),
                      if (widget.task.priority != null)
                        _buildChip(
                          icon: Icons.flag,
                          label: 'Priority ${widget.task.priority}',
                          color: _getPriorityColor(
                            widget.task.priority!,
                            colorScheme,
                          ),
                        ),
                      if (widget.task.energy != null)
                        _buildChip(
                          icon: Icons.bolt,
                          label: 'Energy ${widget.task.energy}',
                          color: colorScheme.secondary,
                        ),
                      if (widget.task.durationEstMin != null)
                        _buildChip(
                          icon: Icons.access_time,
                          label: '${widget.task.durationEstMin} min',
                          color: colorScheme.tertiary,
                        ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 24),

          // Dependencies Section
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Dependencies',
                style: theme.textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              IconButton.filledTonal(
                icon: const Icon(Icons.add),
                onPressed: _addDependency,
                tooltip: 'Add dependency',
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            'Tasks that must be completed before this task',
            style: theme.textTheme.bodySmall?.copyWith(
              color: colorScheme.onSurface.withValues(alpha: 0.7),
            ),
          ),
          const SizedBox(height: 12),

          if (_dependencyTasks.isEmpty)
            Card(
              child: Padding(
                padding: const EdgeInsets.all(24.0),
                child: Center(
                  child: Column(
                    children: [
                      Icon(
                        Icons.link_off,
                        size: 48,
                        color: colorScheme.onSurface.withValues(alpha: 0.3),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'No dependencies',
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: colorScheme.onSurface.withValues(alpha: 0.6),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            )
          else
            ..._dependencyTasks.map(
              (task) => Card(
                margin: const EdgeInsets.only(bottom: 8),
                child: ListTile(
                  leading: Icon(
                    task.status == 'completed'
                        ? Icons.check_circle
                        : Icons.radio_button_unchecked,
                    color: task.status == 'completed'
                        ? Colors.green
                        : colorScheme.primary,
                  ),
                  title: Text(task.title),
                  subtitle: Text('Status: ${task.status}'),
                  trailing: IconButton(
                    icon: const Icon(Icons.delete_outline),
                    onPressed: () => _removeDependency(task.id),
                    tooltip: 'Remove dependency',
                  ),
                ),
              ),
            ),

          const SizedBox(height: 24),

          // Blockers Section
          Text(
            'Blocking Tasks',
            style: theme.textTheme.titleLarge?.copyWith(
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Tasks that depend on this task being completed',
            style: theme.textTheme.bodySmall?.copyWith(
              color: colorScheme.onSurface.withValues(alpha: 0.7),
            ),
          ),
          const SizedBox(height: 12),

          if (_blockerTasks.isEmpty)
            Card(
              child: Padding(
                padding: const EdgeInsets.all(24.0),
                child: Center(
                  child: Column(
                    children: [
                      Icon(
                        Icons.block,
                        size: 48,
                        color: colorScheme.onSurface.withValues(alpha: 0.3),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'No blocking tasks',
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: colorScheme.onSurface.withValues(alpha: 0.6),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            )
          else
            ..._blockerTasks.map(
              (task) => Card(
                margin: const EdgeInsets.only(bottom: 8),
                child: ListTile(
                  leading: Icon(Icons.lock, color: colorScheme.error),
                  title: Text(task.title),
                  subtitle: Text('Waiting for this task to complete'),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildChip({
    required IconData icon,
    required String label,
    required Color color,
  }) {
    return Chip(
      avatar: Icon(icon, size: 16, color: color),
      label: Text(label),
      labelStyle: TextStyle(color: color, fontSize: 12),
      backgroundColor: color.withValues(alpha: 0.1),
    );
  }

  Color _getPriorityColor(int priority, ColorScheme colorScheme) {
    switch (priority) {
      case 1:
        return Colors.red;
      case 2:
        return Colors.orange;
      case 3:
        return Colors.yellow.shade700;
      default:
        return colorScheme.onSurface;
    }
  }
}

class _SelectTaskDialog extends StatefulWidget {
  final List<Task> tasks;

  const _SelectTaskDialog({required this.tasks});

  @override
  State<_SelectTaskDialog> createState() => _SelectTaskDialogState();
}

class _SelectTaskDialogState extends State<_SelectTaskDialog> {
  String _searchQuery = '';

  @override
  Widget build(BuildContext context) {
    final filteredTasks = widget.tasks.where((task) {
      return task.title.toLowerCase().contains(_searchQuery.toLowerCase()) ||
          (task.notes?.toLowerCase().contains(_searchQuery.toLowerCase()) ??
              false);
    }).toList();

    return AlertDialog(
      title: const Text('Select Task'),
      content: SizedBox(
        width: double.maxFinite,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              decoration: const InputDecoration(
                labelText: 'Search tasks',
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(),
              ),
              onChanged: (value) {
                setState(() {
                  _searchQuery = value;
                });
              },
            ),
            const SizedBox(height: 16),
            Expanded(
              child: filteredTasks.isEmpty
                  ? const Center(child: Text('No tasks found'))
                  : ListView.builder(
                      shrinkWrap: true,
                      itemCount: filteredTasks.length,
                      itemBuilder: (context, index) {
                        final task = filteredTasks[index];
                        return ListTile(
                          leading: Icon(
                            task.status == 'completed'
                                ? Icons.check_circle
                                : Icons.radio_button_unchecked,
                          ),
                          title: Text(task.title),
                          subtitle: Text('Status: ${task.status}'),
                          onTap: () => Navigator.of(context).pop(task),
                        );
                      },
                    ),
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(),
          child: const Text('Cancel'),
        ),
      ],
    );
  }
}
