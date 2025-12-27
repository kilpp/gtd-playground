import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/project.dart';
import '../models/area.dart';
import '../services/project_service.dart';
import '../services/area_service.dart';

class ProjectsScreen extends StatefulWidget {
  final int userId;

  const ProjectsScreen({super.key, required this.userId});

  @override
  State<ProjectsScreen> createState() => _ProjectsScreenState();
}

class _ProjectsScreenState extends State<ProjectsScreen> {
  final ProjectService _projectService = ProjectService();
  final AreaService _areaService = AreaService();
  List<Project> _projects = [];
  List<Area> _areas = [];
  bool _isLoading = false;
  String? _errorMessage;
  String _filterStatus = 'all';

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final projects = await _projectService.getProjectsByUserId(widget.userId);
      final areas = await _areaService.getAreasByUserId(widget.userId);
      setState(() {
        _projects = projects;
        _areas = areas;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  List<Project> get _filteredProjects {
    if (_filterStatus == 'all') {
      return _projects;
    }
    return _projects.where((p) => p.status == _filterStatus).toList();
  }

  Future<void> _showCreateProjectDialog() async {
    final titleController = TextEditingController();
    final outcomeController = TextEditingController();
    final notesController = TextEditingController();
    Area? selectedArea;
    String selectedStatus = 'active';
    DateTime? selectedDueDate;

    final result = await showDialog<bool>(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              title: const Text('Create Project'),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    TextField(
                      controller: titleController,
                      decoration: const InputDecoration(
                        labelText: 'Project Title *',
                        hintText: 'What are you working on?',
                        border: OutlineInputBorder(),
                      ),
                      autofocus: true,
                      textCapitalization: TextCapitalization.sentences,
                    ),
                    const SizedBox(height: 16),
                    DropdownButtonFormField<Area?>(
                      value: selectedArea,
                      decoration: const InputDecoration(
                        labelText: 'Area (optional)',
                        border: OutlineInputBorder(),
                      ),
                      items: [
                        const DropdownMenuItem<Area?>(
                          value: null,
                          child: Text('No Area'),
                        ),
                        ..._areas.map(
                          (area) => DropdownMenuItem<Area?>(
                            value: area,
                            child: Text(area.name),
                          ),
                        ),
                      ],
                      onChanged: (value) {
                        setDialogState(() {
                          selectedArea = value;
                        });
                      },
                    ),
                    const SizedBox(height: 16),
                    TextField(
                      controller: outcomeController,
                      decoration: const InputDecoration(
                        labelText: 'Desired Outcome (optional)',
                        hintText: 'What does success look like?',
                        border: OutlineInputBorder(),
                      ),
                      maxLines: 2,
                      textCapitalization: TextCapitalization.sentences,
                    ),
                    const SizedBox(height: 16),
                    TextField(
                      controller: notesController,
                      decoration: const InputDecoration(
                        labelText: 'Notes (optional)',
                        border: OutlineInputBorder(),
                      ),
                      maxLines: 3,
                      textCapitalization: TextCapitalization.sentences,
                    ),
                    const SizedBox(height: 16),
                    DropdownButtonFormField<String>(
                      value: selectedStatus,
                      decoration: const InputDecoration(
                        labelText: 'Status *',
                        border: OutlineInputBorder(),
                      ),
                      items: const [
                        DropdownMenuItem(
                          value: 'active',
                          child: Text('Active'),
                        ),
                        DropdownMenuItem(
                          value: 'on_hold',
                          child: Text('On Hold'),
                        ),
                        DropdownMenuItem(
                          value: 'someday',
                          child: Text('Someday/Maybe'),
                        ),
                        DropdownMenuItem(
                          value: 'completed',
                          child: Text('Completed'),
                        ),
                        DropdownMenuItem(
                          value: 'dropped',
                          child: Text('Dropped'),
                        ),
                      ],
                      onChanged: (value) {
                        if (value != null) {
                          setDialogState(() {
                            selectedStatus = value;
                          });
                        }
                      },
                    ),
                    const SizedBox(height: 16),
                    ListTile(
                      contentPadding: EdgeInsets.zero,
                      title: Text(
                        selectedDueDate == null
                            ? 'Due Date (optional)'
                            : 'Due: ${DateFormat.yMMMd().format(selectedDueDate!)}',
                      ),
                      trailing: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          if (selectedDueDate != null)
                            IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                setDialogState(() {
                                  selectedDueDate = null;
                                });
                              },
                            ),
                          IconButton(
                            icon: const Icon(Icons.calendar_today),
                            onPressed: () async {
                              final date = await showDatePicker(
                                context: context,
                                initialDate: selectedDueDate ?? DateTime.now(),
                                firstDate: DateTime.now(),
                                lastDate: DateTime.now().add(
                                  const Duration(days: 3650),
                                ),
                              );
                              if (date != null) {
                                setDialogState(() {
                                  selectedDueDate = date;
                                });
                              }
                            },
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(false),
                  child: const Text('Cancel'),
                ),
                FilledButton(
                  onPressed: () async {
                    if (titleController.text.trim().isEmpty) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                          content: Text('Please enter a project title'),
                          backgroundColor: Colors.red,
                        ),
                      );
                      return;
                    }
                    Navigator.of(context).pop(true);
                  },
                  child: const Text('Create'),
                ),
              ],
            );
          },
        );
      },
    );

    if (result == true && mounted) {
      try {
        await _projectService.createProject(
          userId: widget.userId,
          areaId: selectedArea?.id,
          title: titleController.text.trim(),
          outcome: outcomeController.text.trim().isNotEmpty
              ? outcomeController.text.trim()
              : null,
          notes: notesController.text.trim().isNotEmpty
              ? notesController.text.trim()
              : null,
          status: selectedStatus,
          dueDate: selectedDueDate,
        );
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Project created successfully')),
          );
          _loadData();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to create project: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  Future<void> _showEditProjectDialog(Project project) async {
    final titleController = TextEditingController(text: project.title);
    final outcomeController = TextEditingController(
      text: project.outcome ?? '',
    );
    final notesController = TextEditingController(text: project.notes ?? '');
    Area? selectedArea = _areas
        .where((a) => a.id == project.areaId)
        .firstOrNull;
    String selectedStatus = project.status;
    DateTime? selectedDueDate = project.dueDate;

    final result = await showDialog<bool>(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              title: const Text('Edit Project'),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    TextField(
                      controller: titleController,
                      decoration: const InputDecoration(
                        labelText: 'Project Title *',
                        border: OutlineInputBorder(),
                      ),
                      autofocus: true,
                      textCapitalization: TextCapitalization.sentences,
                    ),
                    const SizedBox(height: 16),
                    DropdownButtonFormField<Area?>(
                      value: selectedArea,
                      decoration: const InputDecoration(
                        labelText: 'Area (optional)',
                        border: OutlineInputBorder(),
                      ),
                      items: [
                        const DropdownMenuItem<Area?>(
                          value: null,
                          child: Text('No Area'),
                        ),
                        ..._areas.map(
                          (area) => DropdownMenuItem<Area?>(
                            value: area,
                            child: Text(area.name),
                          ),
                        ),
                      ],
                      onChanged: (value) {
                        setDialogState(() {
                          selectedArea = value;
                        });
                      },
                    ),
                    const SizedBox(height: 16),
                    TextField(
                      controller: outcomeController,
                      decoration: const InputDecoration(
                        labelText: 'Desired Outcome (optional)',
                        border: OutlineInputBorder(),
                      ),
                      maxLines: 2,
                      textCapitalization: TextCapitalization.sentences,
                    ),
                    const SizedBox(height: 16),
                    TextField(
                      controller: notesController,
                      decoration: const InputDecoration(
                        labelText: 'Notes (optional)',
                        border: OutlineInputBorder(),
                      ),
                      maxLines: 3,
                      textCapitalization: TextCapitalization.sentences,
                    ),
                    const SizedBox(height: 16),
                    DropdownButtonFormField<String>(
                      value: selectedStatus,
                      decoration: const InputDecoration(
                        labelText: 'Status *',
                        border: OutlineInputBorder(),
                      ),
                      items: const [
                        DropdownMenuItem(
                          value: 'active',
                          child: Text('Active'),
                        ),
                        DropdownMenuItem(
                          value: 'on_hold',
                          child: Text('On Hold'),
                        ),
                        DropdownMenuItem(
                          value: 'someday',
                          child: Text('Someday/Maybe'),
                        ),
                        DropdownMenuItem(
                          value: 'completed',
                          child: Text('Completed'),
                        ),
                        DropdownMenuItem(
                          value: 'dropped',
                          child: Text('Dropped'),
                        ),
                      ],
                      onChanged: (value) {
                        if (value != null) {
                          setDialogState(() {
                            selectedStatus = value;
                          });
                        }
                      },
                    ),
                    const SizedBox(height: 16),
                    ListTile(
                      contentPadding: EdgeInsets.zero,
                      title: Text(
                        selectedDueDate == null
                            ? 'Due Date (optional)'
                            : 'Due: ${DateFormat.yMMMd().format(selectedDueDate!)}',
                      ),
                      trailing: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          if (selectedDueDate != null)
                            IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                setDialogState(() {
                                  selectedDueDate = null;
                                });
                              },
                            ),
                          IconButton(
                            icon: const Icon(Icons.calendar_today),
                            onPressed: () async {
                              final date = await showDatePicker(
                                context: context,
                                initialDate: selectedDueDate ?? DateTime.now(),
                                firstDate: DateTime.now(),
                                lastDate: DateTime.now().add(
                                  const Duration(days: 3650),
                                ),
                              );
                              if (date != null) {
                                setDialogState(() {
                                  selectedDueDate = date;
                                });
                              }
                            },
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(false),
                  child: const Text('Cancel'),
                ),
                FilledButton(
                  onPressed: () async {
                    if (titleController.text.trim().isEmpty) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                          content: Text('Please enter a project title'),
                          backgroundColor: Colors.red,
                        ),
                      );
                      return;
                    }
                    Navigator.of(context).pop(true);
                  },
                  child: const Text('Update'),
                ),
              ],
            );
          },
        );
      },
    );

    if (result == true && mounted) {
      try {
        await _projectService.updateProject(
          id: project.id,
          userId: widget.userId,
          areaId: selectedArea?.id,
          title: titleController.text.trim(),
          outcome: outcomeController.text.trim().isNotEmpty
              ? outcomeController.text.trim()
              : null,
          notes: notesController.text.trim().isNotEmpty
              ? notesController.text.trim()
              : null,
          status: selectedStatus,
          dueDate: selectedDueDate,
        );
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Project updated successfully')),
          );
          _loadData();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to update project: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  Future<void> _deleteProject(Project project) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Delete Project'),
          content: Text(
            'Are you sure you want to delete "${project.title}"? This action cannot be undone.',
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: const Text('Cancel'),
            ),
            FilledButton(
              style: FilledButton.styleFrom(backgroundColor: Colors.red),
              onPressed: () => Navigator.of(context).pop(true),
              child: const Text('Delete'),
            ),
          ],
        );
      },
    );

    if (confirm == true && mounted) {
      try {
        await _projectService.deleteProject(project.id);
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Project deleted successfully')),
          );
          _loadData();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to delete project: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'active':
        return Colors.green;
      case 'on_hold':
        return Colors.orange;
      case 'someday':
        return Colors.blue;
      case 'completed':
        return Colors.grey;
      case 'dropped':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Projects'),
        actions: [
          PopupMenuButton<String>(
            icon: const Icon(Icons.filter_list),
            tooltip: 'Filter by status',
            onSelected: (value) {
              setState(() {
                _filterStatus = value;
              });
            },
            itemBuilder: (context) => [
              const PopupMenuItem(value: 'all', child: Text('All Projects')),
              const PopupMenuItem(value: 'active', child: Text('Active')),
              const PopupMenuItem(value: 'on_hold', child: Text('On Hold')),
              const PopupMenuItem(
                value: 'someday',
                child: Text('Someday/Maybe'),
              ),
              const PopupMenuItem(value: 'completed', child: Text('Completed')),
              const PopupMenuItem(value: 'dropped', child: Text('Dropped')),
            ],
          ),
          IconButton(
            icon: const Icon(Icons.refresh),
            tooltip: 'Refresh',
            onPressed: _loadData,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _errorMessage != null
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 48, color: Colors.red),
                  const SizedBox(height: 16),
                  Text(
                    'Error loading projects',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  const SizedBox(height: 8),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 32),
                    child: Text(
                      _errorMessage!,
                      textAlign: TextAlign.center,
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ),
                  const SizedBox(height: 16),
                  FilledButton.icon(
                    onPressed: _loadData,
                    icon: const Icon(Icons.refresh),
                    label: const Text('Retry'),
                  ),
                ],
              ),
            )
          : _filteredProjects.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.folder_open, size: 64, color: Colors.grey[400]),
                  const SizedBox(height: 16),
                  Text(
                    _filterStatus == 'all'
                        ? 'No projects yet'
                        : 'No ${_filterStatus == 'on_hold' ? 'on hold' : _filterStatus} projects',
                    style: Theme.of(
                      context,
                    ).textTheme.titleLarge?.copyWith(color: Colors.grey[600]),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Create your first project to get started',
                    style: Theme.of(
                      context,
                    ).textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
                  ),
                ],
              ),
            )
          : ListView.builder(
              padding: const EdgeInsets.all(8),
              itemCount: _filteredProjects.length,
              itemBuilder: (context, index) {
                final project = _filteredProjects[index];
                final area = _areas
                    .where((a) => a.id == project.areaId)
                    .firstOrNull;
                final isOverdue =
                    project.dueDate != null &&
                    project.dueDate!.isBefore(DateTime.now()) &&
                    project.status != 'completed' &&
                    project.status != 'dropped';

                return Card(
                  margin: const EdgeInsets.symmetric(
                    horizontal: 8,
                    vertical: 4,
                  ),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: _getStatusColor(project.status),
                      child: Icon(
                        project.status == 'completed'
                            ? Icons.check
                            : project.status == 'dropped'
                            ? Icons.close
                            : Icons.folder,
                        color: Colors.white,
                      ),
                    ),
                    title: Text(
                      project.title,
                      style: TextStyle(
                        decoration:
                            project.status == 'completed' ||
                                project.status == 'dropped'
                            ? TextDecoration.lineThrough
                            : null,
                      ),
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        if (area != null) ...[
                          const SizedBox(height: 4),
                          Row(
                            children: [
                              Icon(
                                Icons.category,
                                size: 14,
                                color: Colors.grey[600],
                              ),
                              const SizedBox(width: 4),
                              Text(
                                area.name,
                                style: TextStyle(
                                  fontSize: 12,
                                  color: Colors.grey[600],
                                ),
                              ),
                            ],
                          ),
                        ],
                        if (project.outcome != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            project.outcome!,
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(fontSize: 12),
                          ),
                        ],
                        const SizedBox(height: 4),
                        Row(
                          children: [
                            Container(
                              padding: const EdgeInsets.symmetric(
                                horizontal: 8,
                                vertical: 2,
                              ),
                              decoration: BoxDecoration(
                                color: _getStatusColor(
                                  project.status,
                                ).withOpacity(0.2),
                                borderRadius: BorderRadius.circular(12),
                              ),
                              child: Text(
                                project.statusDisplay,
                                style: TextStyle(
                                  fontSize: 11,
                                  color: _getStatusColor(project.status),
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                            if (project.dueDate != null) ...[
                              const SizedBox(width: 8),
                              Icon(
                                Icons.calendar_today,
                                size: 12,
                                color: isOverdue
                                    ? Colors.red
                                    : Colors.grey[600],
                              ),
                              const SizedBox(width: 4),
                              Text(
                                DateFormat.yMMMd().format(project.dueDate!),
                                style: TextStyle(
                                  fontSize: 11,
                                  color: isOverdue
                                      ? Colors.red
                                      : Colors.grey[600],
                                  fontWeight: isOverdue
                                      ? FontWeight.bold
                                      : null,
                                ),
                              ),
                            ],
                          ],
                        ),
                      ],
                    ),
                    trailing: PopupMenuButton<String>(
                      icon: const Icon(Icons.more_vert),
                      onSelected: (value) {
                        switch (value) {
                          case 'edit':
                            _showEditProjectDialog(project);
                            break;
                          case 'delete':
                            _deleteProject(project);
                            break;
                        }
                      },
                      itemBuilder: (context) => [
                        const PopupMenuItem(
                          value: 'edit',
                          child: Row(
                            children: [
                              Icon(Icons.edit),
                              SizedBox(width: 8),
                              Text('Edit'),
                            ],
                          ),
                        ),
                        const PopupMenuItem(
                          value: 'delete',
                          child: Row(
                            children: [
                              Icon(Icons.delete, color: Colors.red),
                              SizedBox(width: 8),
                              Text(
                                'Delete',
                                style: TextStyle(color: Colors.red),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _showCreateProjectDialog,
        icon: const Icon(Icons.add),
        label: const Text('New Project'),
      ),
    );
  }
}
