import 'package:flutter/material.dart';
import '../models/task.dart';
import '../services/task_service.dart';
import '../services/auth_service_factory.dart';
import 'areas_screen.dart';
import 'create_task_screen.dart';
import 'login_screen.dart';
import 'contexts_screen.dart';

class InboxScreen extends StatefulWidget {
  final int userId;

  const InboxScreen({super.key, required this.userId});

  @override
  State<InboxScreen> createState() => _InboxScreenState();
}

class _InboxScreenState extends State<InboxScreen> {
  final TaskService _taskService = TaskService();
  final dynamic _authService = AuthServiceFactory.getAuthService();
  List<Task> _tasks = [];
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadInboxTasks();
  }

  Future<void> _loadInboxTasks() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      print(" Loading inbox tasks for user ${widget.userId}...");
      final tasks = await _taskService.getInboxTasks(userId: widget.userId);
      setState(() {
        _tasks = tasks;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _logout() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Logout'),
        content: const Text('Are you sure you want to logout?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.of(context).pop(true),
            child: const Text('Logout'),
          ),
        ],
      ),
    );

    if (confirm == true) {
      await _authService.logout();
      if (mounted) {
        Navigator.of(context).pushAndRemoveUntil(
          MaterialPageRoute(builder: (context) => const LoginScreen()),
          (route) => false,
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
        title: const Text('Inbox'),
        backgroundColor: colorScheme.primaryContainer,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadInboxTasks,
            tooltip: 'Refresh',
          ),
          PopupMenuButton<String>(
            icon: const Icon(Icons.account_circle),
            tooltip: 'Account',
            onSelected: (value) {
              if (value == 'logout') {
                _logout();
              }
            },
            itemBuilder: (context) => [
              PopupMenuItem(
                value: 'user',
                enabled: false,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _authService.currentUser?.name ?? 'User',
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Text(
                      _authService.currentUser?.email ?? '',
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: colorScheme.onSurface.withValues(alpha: 0.6),
                      ),
                    ),
                  ],
                ),
              ),
              const PopupMenuDivider(),
              const PopupMenuItem(
                value: 'logout',
                child: Row(
                  children: [
                    Icon(Icons.logout),
                    SizedBox(width: 8),
                    Text('Logout'),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
      drawer: _buildDrawer(theme, colorScheme),
      body: _buildBody(theme, colorScheme),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          final result = await Navigator.of(context).push<bool>(
            MaterialPageRoute(
              builder: (context) => CreateTaskScreen(userId: widget.userId),
            ),
          );
          // Refresh the list if a task was created
          if (result == true && mounted) {
            _loadInboxTasks();
          }
        },
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildDrawer(ThemeData theme, ColorScheme colorScheme) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          DrawerHeader(
            decoration: BoxDecoration(color: colorScheme.primaryContainer),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Icon(
                  Icons.checklist_rounded,
                  size: 48,
                  color: colorScheme.primary,
                ),
                const SizedBox(height: 8),
                Text(
                  'GTD',
                  style: theme.textTheme.headlineSmall?.copyWith(
                    color: colorScheme.onPrimaryContainer,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  'Getting Things Done',
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: colorScheme.onPrimaryContainer.withValues(
                      alpha: 0.7,
                    ),
                  ),
                ),
              ],
            ),
          ),
          ListTile(
            leading: const Icon(Icons.inbox),
            title: const Text('Inbox'),
            selected: true,
            selectedTileColor: colorScheme.primaryContainer.withValues(
              alpha: 0.3,
            ),
            onTap: () {
              Navigator.pop(context); // Close drawer
            },
          ),
          ListTile(
            leading: const Icon(Icons.place),
            title: const Text('Contexts'),
            onTap: () {
              Navigator.pop(context); // Close drawer
              Navigator.of(context).push(
                MaterialPageRoute(
                  builder: (context) => ContextsScreen(userId: widget.userId),
                ),
              );
            },
          ),
          ListTile(
            leading: const Icon(Icons.category),
            title: const Text('Areas'),
            onTap: () {
              Navigator.pop(context); // Close drawer
              Navigator.of(context).push(
                MaterialPageRoute(
                  builder: (context) => AreasScreen(userId: widget.userId),
                ),
              );
            },
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.folder_outlined),
            title: const Text('Projects'),
            enabled: false,
            onTap: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Projects - Coming soon')),
              );
            },
          ),
          ListTile(
            leading: const Icon(Icons.label_outline),
            title: const Text('Labels'),
            enabled: false,
            onTap: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Labels - Coming soon')),
              );
            },
          ),
        ],
      ),
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
              Text(
                'Error loading tasks',
                style: theme.textTheme.titleLarge?.copyWith(
                  color: colorScheme.error,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                _errorMessage!,
                textAlign: TextAlign.center,
                style: theme.textTheme.bodyMedium,
              ),
              const SizedBox(height: 24),
              FilledButton.icon(
                onPressed: _loadInboxTasks,
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
                Icons.inbox,
                size: 80,
                color: colorScheme.primary.withValues(alpha: 0.3),
              ),
              const SizedBox(height: 16),
              Text('Your inbox is empty', style: theme.textTheme.titleLarge),
              const SizedBox(height: 8),
              Text(
                'Capture tasks and ideas here to process later',
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
      onRefresh: _loadInboxTasks,
      child: ListView.builder(
        padding: const EdgeInsets.all(8.0),
        itemCount: _tasks.length,
        itemBuilder: (context, index) {
          final task = _tasks[index];
          return _buildTaskCard(task, theme, colorScheme);
        },
      ),
    );
  }

  Widget _buildTaskCard(Task task, ThemeData theme, ColorScheme colorScheme) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      child: ListTile(
        leading: Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            color: colorScheme.primaryContainer,
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(Icons.inbox, color: colorScheme.primary),
        ),
        title: Text(
          task.title,
          style: theme.textTheme.titleMedium?.copyWith(
            fontWeight: FontWeight.w600,
          ),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (task.notes != null && task.notes!.isNotEmpty) ...[
              const SizedBox(height: 4),
              Text(
                task.notes!,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: theme.textTheme.bodySmall,
              ),
            ],
            const SizedBox(height: 4),
            Row(
              children: [
                if (task.priority != null) ...[
                  Icon(
                    Icons.flag,
                    size: 16,
                    color: _getPriorityColor(task.priority!, colorScheme),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'P${task.priority}',
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: _getPriorityColor(task.priority!, colorScheme),
                    ),
                  ),
                  const SizedBox(width: 8),
                ],
                if (task.energy != null) ...[
                  Icon(Icons.bolt, size: 16, color: colorScheme.secondary),
                  const SizedBox(width: 4),
                  Text('${task.energy}', style: theme.textTheme.bodySmall),
                  const SizedBox(width: 8),
                ],
                if (task.durationEstMin != null) ...[
                  Icon(
                    Icons.access_time,
                    size: 16,
                    color: colorScheme.tertiary,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    '${task.durationEstMin}m',
                    style: theme.textTheme.bodySmall,
                  ),
                ],
              ],
            ),
          ],
        ),
        trailing: PopupMenuButton<String>(
          onSelected: (value) {
            if (value == 'delete') {
              _confirmDelete(task);
            }
          },
          itemBuilder: (context) => [
            const PopupMenuItem(
              value: 'edit',
              child: Row(
                children: [Icon(Icons.edit), SizedBox(width: 8), Text('Edit')],
              ),
            ),
            const PopupMenuItem(
              value: 'delete',
              child: Row(
                children: [
                  Icon(Icons.delete),
                  SizedBox(width: 8),
                  Text('Delete'),
                ],
              ),
            ),
          ],
        ),
        onTap: () {
          // TODO: Navigate to task details
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Task details for: ${task.title}')),
          );
        },
      ),
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

  Future<void> _confirmDelete(Task task) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Task'),
        content: Text('Are you sure you want to delete "${task.title}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: FilledButton.styleFrom(
              backgroundColor: Theme.of(context).colorScheme.error,
            ),
            child: const Text('Delete'),
          ),
        ],
      ),
    );

    if (confirmed == true && mounted) {
      try {
        await _taskService.deleteTask(task.id);
        if (mounted) {
          ScaffoldMessenger.of(
            context,
          ).showSnackBar(const SnackBar(content: Text('Task deleted')));
          _loadInboxTasks();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(
            context,
          ).showSnackBar(SnackBar(content: Text('Error deleting task: $e')));
        }
      }
    }
  }
}
