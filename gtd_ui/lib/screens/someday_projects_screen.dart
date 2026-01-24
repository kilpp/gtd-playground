import 'package:flutter/material.dart';
import '../models/project.dart';
import '../services/project_service.dart';

class SomedayProjectsScreen extends StatefulWidget {
  final int userId;

  const SomedayProjectsScreen({super.key, required this.userId});

  @override
  State<SomedayProjectsScreen> createState() => _SomedayProjectsScreenState();
}

class _SomedayProjectsScreenState extends State<SomedayProjectsScreen> {
  final ProjectService _projectService = ProjectService();
  List<Project> _projects = [];
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadSomedayProjects();
  }

  Future<void> _loadSomedayProjects() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // Get all projects for this user
      final projects = await _projectService.getProjectsByUserId(widget.userId);
      
      // Filter for someday status
      final somedayProjects = projects.where((p) => p.status == 'someday').toList();
      
      setState(() {
        _projects = somedayProjects;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _activateProject(Project project) async {
    try {
      await _projectService.updateProject(project.id, {'status': 'active'});
      _loadSomedayProjects();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Project activated')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    }
  }

  Future<void> _deleteProject(Project project) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Project'),
        content: Text('Delete "${project.title}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.of(context).pop(true),
            child: const Text('Delete'),
          ),
        ],
      ),
    );

    if (confirm == true) {
      try {
        await _projectService.deleteProject(project.id);
        _loadSomedayProjects();
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Error: $e')),
          );
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Someday/Maybe Projects'),
        backgroundColor: colorScheme.primaryContainer,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadSomedayProjects,
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
                onPressed: _loadSomedayProjects,
                icon: const Icon(Icons.refresh),
                label: const Text('Retry'),
              ),
            ],
          ),
        ),
      );
    }

    if (_projects.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.folder_off_outlined,
                size: 80,
                color: colorScheme.primary.withValues(alpha: 0.3),
              ),
              const SizedBox(height: 16),
              Text('No someday projects', style: theme.textTheme.titleLarge),
              const SizedBox(height: 8),
              Text(
                'Projects not being pursued now will appear here',
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
      onRefresh: _loadSomedayProjects,
      child: ListView.builder(
        padding: const EdgeInsets.all(8.0),
        itemCount: _projects.length,
        itemBuilder: (context, index) {
          final project = _projects[index];
          return Card(
            margin: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 8.0),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: colorScheme.tertiaryContainer,
                child: Icon(
                  Icons.folder_outlined,
                  color: colorScheme.onTertiaryContainer,
                  size: 20,
                ),
              ),
              title: Text(
                project.title,
                style: theme.textTheme.bodyLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  if (project.outcome?.isNotEmpty == true) ...[
                    const SizedBox(height: 4),
                    Text(
                      project.outcome!,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: theme.textTheme.bodySmall?.copyWith(
                        fontStyle: FontStyle.italic,
                        color: colorScheme.onSurface.withValues(alpha: 0.8),
                      ),
                    ),
                  ],
                  if (project.notes?.isNotEmpty == true) ...[
                    const SizedBox(height: 4),
                    Text(
                      project.notes!,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: colorScheme.onSurface.withValues(alpha: 0.6),
                      ),
                    ),
                  ],
                ],
              ),
              trailing: PopupMenuButton<String>(
                onSelected: (value) {
                  if (value == 'activate') {
                    _activateProject(project);
                  } else if (value == 'delete') {
                    _deleteProject(project);
                  }
                },
                itemBuilder: (context) => [
                  const PopupMenuItem(
                    value: 'activate',
                    child: Row(
                      children: [
                        Icon(Icons.play_arrow),
                        SizedBox(width: 8),
                        Text('Activate'),
                      ],
                    ),
                  ),
                  const PopupMenuItem(
                    value: 'delete',
                    child: Row(
                      children: [
                        Icon(Icons.delete_outline),
                        SizedBox(width: 8),
                        Text('Delete'),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
