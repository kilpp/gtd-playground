import 'package:flutter/material.dart';
import '../models/project.dart';
import '../models/task.dart';
import '../services/project_service.dart';
import '../services/task_service.dart';

class WeeklyReviewScreen extends StatefulWidget {
  final int userId;

  const WeeklyReviewScreen({super.key, required this.userId});

  @override
  State<WeeklyReviewScreen> createState() => _WeeklyReviewScreenState();
}

class _WeeklyReviewScreenState extends State<WeeklyReviewScreen> {
  final ProjectService _projectService = ProjectService();
  final TaskService _taskService = TaskService();

  List<Project> _projectsNeedingAction = [];
  List<Task> _waitingForTasks = [];
  List<Task> _inboxTasks = [];
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadReviewData();
  }

  Future<void> _loadReviewData() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // Load projects
      final allProjects = await _projectService.getProjectsByUserId(
        widget.userId,
      );
      final activeProjects = allProjects
          .where((p) => p.status == 'active')
          .toList();

      // Load all tasks
      final allTasks = await _taskService.getTasksByUserId(widget.userId);

      // Find projects without next actions
      final projectsNeedingAction = <Project>[];
      for (final project in activeProjects) {
        final hasNextAction = allTasks.any(
          (task) =>
              task.projectId == project.id &&
              task.status == 'next' &&
              task.userId == widget.userId,
        );
        if (!hasNextAction) {
          projectsNeedingAction.add(project);
        }
      }

      // Get waiting for tasks
      final waitingTasks = allTasks
          .where(
            (task) => task.status == 'waiting' && task.userId == widget.userId,
          )
          .toList();

      // Get inbox tasks
      final inboxTasks = allTasks
          .where(
            (task) => task.status == 'inbox' && task.userId == widget.userId,
          )
          .toList();

      setState(() {
        _projectsNeedingAction = projectsNeedingAction;
        _waitingForTasks = waitingTasks;
        _inboxTasks = inboxTasks;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Weekly Review'),
        backgroundColor: colorScheme.primaryContainer,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadReviewData,
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
                onPressed: _loadReviewData,
                icon: const Icon(Icons.refresh),
                label: const Text('Retry'),
              ),
            ],
          ),
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadReviewData,
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header
            Card(
              color: colorScheme.primaryContainer,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(
                          Icons.fact_check,
                          size: 32,
                          color: colorScheme.onPrimaryContainer,
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Weekly Review',
                                style: theme.textTheme.headlineSmall?.copyWith(
                                  color: colorScheme.onPrimaryContainer,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                'Review your system and ensure everything is current',
                                style: theme.textTheme.bodySmall?.copyWith(
                                  color: colorScheme.onPrimaryContainer
                                      .withValues(alpha: 0.8),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 24),

            // Inbox Status
            _buildSection(
              theme,
              colorScheme,
              'Inbox',
              Icons.inbox,
              _inboxTasks.isEmpty
                  ? 'All clear! ✓'
                  : '${_inboxTasks.length} items need processing',
              _inboxTasks.isEmpty ? Colors.green : Colors.orange,
              showItems: _inboxTasks.isNotEmpty,
              items: _inboxTasks.map((t) => t.title).toList(),
            ),

            const SizedBox(height: 16),

            // Projects Needing Actions
            _buildSection(
              theme,
              colorScheme,
              'Projects Needing Next Actions',
              Icons.warning_amber,
              _projectsNeedingAction.isEmpty
                  ? 'All projects have next actions ✓'
                  : '${_projectsNeedingAction.length} projects need attention',
              _projectsNeedingAction.isEmpty ? Colors.green : Colors.red,
              showItems: _projectsNeedingAction.isNotEmpty,
              items: _projectsNeedingAction.map((p) => p.title).toList(),
            ),

            const SizedBox(height: 16),

            // Waiting For
            _buildSection(
              theme,
              colorScheme,
              'Waiting For',
              Icons.hourglass_bottom,
              _waitingForTasks.isEmpty
                  ? 'Nothing waiting ✓'
                  : '${_waitingForTasks.length} items waiting',
              _waitingForTasks.isEmpty ? Colors.green : Colors.blue,
              showItems: _waitingForTasks.isNotEmpty,
              items: _waitingForTasks
                  .map(
                    (t) =>
                        '${t.title}${t.waitingOn != null ? ' (from ${t.waitingOn})' : ''}',
                  )
                  .toList(),
            ),

            const SizedBox(height: 24),

            // Review Checklist
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Review Checklist',
                      style: theme.textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 12),
                    _buildChecklistItem(
                      'Process inbox to zero',
                      _inboxTasks.isEmpty,
                    ),
                    _buildChecklistItem(
                      'Review all projects for next actions',
                      _projectsNeedingAction.isEmpty,
                    ),
                    _buildChecklistItem(
                      'Check waiting for items',
                      _waitingForTasks.isEmpty,
                    ),
                    _buildChecklistItem('Review someday/maybe lists', null),
                    _buildChecklistItem(
                      'Review calendar for upcoming commitments',
                      null,
                    ),
                    _buildChecklistItem(
                      'Review tickler for future items',
                      null,
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSection(
    ThemeData theme,
    ColorScheme colorScheme,
    String title,
    IconData icon,
    String subtitle,
    Color statusColor, {
    bool showItems = false,
    List<String> items = const [],
  }) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(icon, color: statusColor),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: theme.textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        subtitle,
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: statusColor,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            if (showItems && items.isNotEmpty) ...[
              const SizedBox(height: 12),
              const Divider(),
              const SizedBox(height: 8),
              ...items
                  .take(5)
                  .map(
                    (item) => Padding(
                      padding: const EdgeInsets.symmetric(vertical: 4.0),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('• ', style: TextStyle(fontSize: 16)),
                          Expanded(
                            child: Text(
                              item,
                              style: theme.textTheme.bodyMedium,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
              if (items.length > 5)
                Padding(
                  padding: const EdgeInsets.only(top: 4.0),
                  child: Text(
                    'and ${items.length - 5} more...',
                    style: theme.textTheme.bodySmall?.copyWith(
                      fontStyle: FontStyle.italic,
                      color: colorScheme.onSurface.withValues(alpha: 0.6),
                    ),
                  ),
                ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildChecklistItem(String text, bool? isComplete) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Row(
        children: [
          Icon(
            isComplete == true
                ? Icons.check_circle
                : isComplete == false
                ? Icons.error_outline
                : Icons.radio_button_unchecked,
            size: 20,
            color: isComplete == true
                ? Colors.green
                : isComplete == false
                ? Colors.orange
                : Colors.grey,
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              text,
              style: TextStyle(
                decoration: isComplete == true
                    ? TextDecoration.lineThrough
                    : null,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
