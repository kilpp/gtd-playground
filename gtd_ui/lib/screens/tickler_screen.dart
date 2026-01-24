import 'package:flutter/material.dart';
import '../models/task.dart';
import '../services/task_service.dart';
import 'task_detail_screen.dart';

class TicklerScreen extends StatefulWidget {
  final int userId;

  const TicklerScreen({super.key, required this.userId});

  @override
  State<TicklerScreen> createState() => _TicklerScreenState();
}

class _TicklerScreenState extends State<TicklerScreen> {
  final TaskService _taskService = TaskService();
  List<Task> _tasks = [];
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadTicklerTasks();
  }

  Future<void> _loadTicklerTasks() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // Get all tasks for this user
      final allTasks = await _taskService.getTasksByUserId(widget.userId);

      // Filter for deferred tasks (those with deferUntil in the future)
      final now = DateTime.now();
      final deferredTasks = allTasks.where((task) {
        return task.deferUntil != null &&
            task.deferUntil!.isAfter(now) &&
            task.status != 'done' &&
            task.status != 'dropped';
      }).toList();

      // Sort by defer date
      deferredTasks.sort((a, b) {
        if (a.deferUntil == null && b.deferUntil == null) return 0;
        if (a.deferUntil == null) return 1;
        if (b.deferUntil == null) return -1;
        return a.deferUntil!.compareTo(b.deferUntil!);
      });

      setState(() {
        _tasks = deferredTasks;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _activateNow(Task task) async {
    try {
      await _taskService.updateTask(task.id, {
        'status': 'next',
        'deferUntil': null,
      });
      _loadTicklerTasks();
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(const SnackBar(content: Text('Moved to Next Actions')));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
  }

  String _formatDeferDate(DateTime? date) {
    if (date == null) return 'No date';
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final taskDate = DateTime(date.year, date.month, date.day);

    final difference = taskDate.difference(today).inDays;

    if (difference == 0) {
      return 'Today';
    } else if (difference == 1) {
      return 'Tomorrow';
    } else if (difference > 0 && difference < 7) {
      return 'in $difference days (${_getDayName(date)})';
    } else if (difference >= 7 && difference < 30) {
      final weeks = (difference / 7).floor();
      return 'in $weeks ${weeks == 1 ? 'week' : 'weeks'}';
    } else if (difference >= 30 && difference < 365) {
      final months = (difference / 30).floor();
      return 'in $months ${months == 1 ? 'month' : 'months'}';
    } else {
      return '${date.month}/${date.day}/${date.year}';
    }
  }

  String _getDayName(DateTime date) {
    const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    return days[date.weekday - 1];
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Tickler'),
        backgroundColor: colorScheme.primaryContainer,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadTicklerTasks,
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
                onPressed: _loadTicklerTasks,
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
                Icons.alarm_outlined,
                size: 80,
                color: colorScheme.primary.withValues(alpha: 0.3),
              ),
              const SizedBox(height: 16),
              Text('No deferred tasks', style: theme.textTheme.titleLarge),
              const SizedBox(height: 8),
              Text(
                'Deferred actions that will become available later will appear here',
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
      onRefresh: _loadTicklerTasks,
      child: ListView.builder(
        padding: const EdgeInsets.all(8.0),
        itemCount: _tasks.length,
        itemBuilder: (context, index) {
          final task = _tasks[index];

          return Card(
            margin: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 8.0),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: colorScheme.secondaryContainer,
                child: Icon(
                  Icons.schedule_outlined,
                  color: colorScheme.onSecondaryContainer,
                  size: 20,
                ),
              ),
              title: Text(task.title, style: theme.textTheme.bodyLarge),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Icon(
                        Icons.timer_outlined,
                        size: 14,
                        color: colorScheme.primary,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        'Available ${_formatDeferDate(task.deferUntil)}',
                        style: theme.textTheme.bodySmall?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: colorScheme.primary,
                        ),
                      ),
                    ],
                  ),
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
                  Text(
                    'Status: ${task.status}',
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: colorScheme.onSurface.withValues(alpha: 0.5),
                    ),
                  ),
                ],
              ),
              trailing: PopupMenuButton<String>(
                onSelected: (value) {
                  if (value == 'activate') {
                    _activateNow(task);
                  }
                },
                itemBuilder: (context) => [
                  const PopupMenuItem(
                    value: 'activate',
                    child: Row(
                      children: [
                        Icon(Icons.play_arrow),
                        SizedBox(width: 8),
                        Text('Activate Now'),
                      ],
                    ),
                  ),
                ],
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
}
