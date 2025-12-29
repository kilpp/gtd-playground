import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:intl/intl.dart';
import '../services/task_service.dart';
import '../services/project_service.dart';
import '../services/context_service.dart';
import '../models/project.dart';
import '../models/context.dart';

class CreateTaskScreen extends StatefulWidget {
  final int userId;

  const CreateTaskScreen({super.key, required this.userId});

  @override
  State<CreateTaskScreen> createState() => _CreateTaskScreenState();
}

class _CreateTaskScreenState extends State<CreateTaskScreen> {
  final _formKey = GlobalKey<FormState>();
  final TaskService _taskService = TaskService();
  final ProjectService _projectService = ProjectService();
  final ContextService _contextService = ContextService();

  // Form controllers
  final _titleController = TextEditingController();
  final _notesController = TextEditingController();
  final _waitingOnController = TextEditingController();

  // Form values
  String _status = 'inbox';
  int? _priority;
  int? _energy;
  int? _durationEstMin;
  Project? _selectedProject;
  Context? _selectedContext;
  DateTime? _dueDate;
  DateTime? _deferUntil;
  DateTime? _waitingSince;

  // Lists for dropdowns
  List<Project> _projects = [];
  List<Context> _contexts = [];
  bool _isLoading = false;
  bool _isLoadingData = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() {
      _isLoadingData = true;
    });

    try {
      final projects = await _projectService.getProjectsByUserId(widget.userId);
      final contexts = await _contextService.getContextsByUserId(widget.userId);
      setState(() {
        _projects = projects.where((p) => p.status == 'active').toList();
        _contexts = contexts;
        _isLoadingData = false;
      });
    } catch (e) {
      setState(() {
        _isLoadingData = false;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Error loading data: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  @override
  void dispose() {
    _titleController.dispose();
    _notesController.dispose();
    _waitingOnController.dispose();
    super.dispose();
  }

  Future<void> _submitForm() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    // Validate waiting status fields
    if (_status == 'waiting' && _waitingOnController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please specify who/what you are waiting on'),
          backgroundColor: Colors.red,
        ),
      );
      return;
    }

    setState(() {
      _isLoading = true;
    });

    try {
      final taskData = {
        'userId': widget.userId,
        'title': _titleController.text.trim(),
        'notes': _notesController.text.trim().isEmpty
            ? null
            : _notesController.text.trim(),
        'status': _status,
        'priority': _priority,
        'energy': _energy,
        'durationEstMin': _durationEstMin,
        'projectId': _selectedProject?.id,
        'contextId': _selectedContext?.id,
        'dueAt': _dueDate?.toIso8601String(),
        'deferUntil': _deferUntil?.toIso8601String(),
        'waitingOn':
            _status == 'waiting' && _waitingOnController.text.trim().isNotEmpty
            ? _waitingOnController.text.trim()
            : null,
        'waitingSince': _status == 'waiting' && _waitingSince != null
            ? _waitingSince!.toIso8601String()
            : null,
      };

      await _taskService.createTask(taskData);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Task created successfully!'),
            backgroundColor: Colors.green,
          ),
        );
        Navigator.of(context).pop(true); // Return true to indicate success
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Error creating task: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    if (_isLoadingData) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('Create Task'),
          backgroundColor: colorScheme.primaryContainer,
        ),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Create Task'),
        backgroundColor: colorScheme.primaryContainer,
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16.0),
          children: [
            // Title field
            TextFormField(
              controller: _titleController,
              decoration: InputDecoration(
                labelText: 'Title *',
                hintText: 'What needs to be done?',
                border: const OutlineInputBorder(),
                prefixIcon: const Icon(Icons.title),
                filled: true,
                fillColor: colorScheme.surface,
              ),
              maxLength: 500,
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Title is required';
                }
                return null;
              },
              textCapitalization: TextCapitalization.sentences,
            ),
            const SizedBox(height: 16),

            // Project dropdown
            DropdownButtonFormField<Project?>(
              value: _selectedProject,
              decoration: InputDecoration(
                labelText: 'Project (optional)',
                border: const OutlineInputBorder(),
                prefixIcon: const Icon(Icons.folder),
                filled: true,
                fillColor: colorScheme.surface,
              ),
              items: [
                const DropdownMenuItem<Project?>(
                  value: null,
                  child: Text('No Project'),
                ),
                ..._projects.map(
                  (project) => DropdownMenuItem<Project?>(
                    value: project,
                    child: Text(project.title),
                  ),
                ),
              ],
              onChanged: (value) {
                setState(() {
                  _selectedProject = value;
                });
              },
            ),
            const SizedBox(height: 16),

            // Context dropdown
            DropdownButtonFormField<Context?>(
              value: _selectedContext,
              decoration: InputDecoration(
                labelText: 'Context (optional)',
                border: const OutlineInputBorder(),
                prefixIcon: const Icon(Icons.location_on),
                filled: true,
                fillColor: colorScheme.surface,
              ),
              items: [
                const DropdownMenuItem<Context?>(
                  value: null,
                  child: Text('No Context'),
                ),
                ..._contexts.map(
                  (context) => DropdownMenuItem<Context?>(
                    value: context,
                    child: Text(context.name),
                  ),
                ),
              ],
              onChanged: (value) {
                setState(() {
                  _selectedContext = value;
                });
              },
            ),
            const SizedBox(height: 16),

            // Notes field
            TextFormField(
              controller: _notesController,
              decoration: InputDecoration(
                labelText: 'Notes',
                hintText: 'Add any additional details...',
                border: const OutlineInputBorder(),
                prefixIcon: const Icon(Icons.notes),
                filled: true,
                fillColor: colorScheme.surface,
              ),
              maxLines: 5,
              maxLength: 2000,
              textCapitalization: TextCapitalization.sentences,
            ),
            const SizedBox(height: 16),

            // Status dropdown
            DropdownButtonFormField<String>(
              value: _status,
              decoration: InputDecoration(
                labelText: 'Status',
                border: const OutlineInputBorder(),
                prefixIcon: const Icon(Icons.label),
                filled: true,
                fillColor: colorScheme.surface,
              ),
              items: const [
                DropdownMenuItem(value: 'inbox', child: Text('Inbox')),
                DropdownMenuItem(value: 'next', child: Text('Next Action')),
                DropdownMenuItem(value: 'waiting', child: Text('Waiting For')),
                DropdownMenuItem(value: 'scheduled', child: Text('Scheduled')),
                DropdownMenuItem(
                  value: 'someday',
                  child: Text('Someday/Maybe'),
                ),
              ],
              onChanged: (value) {
                setState(() {
                  _status = value!;
                  // Set waitingSince to now when changing to waiting status
                  if (_status == 'waiting' && _waitingSince == null) {
                    _waitingSince = DateTime.now();
                  }
                });
              },
            ),
            const SizedBox(height: 16),

            // Waiting fields (only show when status is "waiting")
            if (_status == 'waiting') ...[
              TextFormField(
                controller: _waitingOnController,
                decoration: InputDecoration(
                  labelText: 'Waiting On *',
                  hintText: 'Who or what are you waiting for?',
                  border: const OutlineInputBorder(),
                  prefixIcon: const Icon(Icons.hourglass_empty),
                  filled: true,
                  fillColor: colorScheme.surface,
                ),
                maxLength: 500,
                textCapitalization: TextCapitalization.sentences,
              ),
              const SizedBox(height: 16),
              ListTile(
                contentPadding: EdgeInsets.zero,
                title: Text(
                  _waitingSince == null
                      ? 'Waiting Since (optional)'
                      : 'Waiting Since: ${DateFormat.yMMMd().format(_waitingSince!)}',
                ),
                trailing: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    if (_waitingSince != null)
                      IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          setState(() {
                            _waitingSince = null;
                          });
                        },
                      ),
                    IconButton(
                      icon: const Icon(Icons.calendar_today),
                      onPressed: () async {
                        final date = await showDatePicker(
                          context: context,
                          initialDate: _waitingSince ?? DateTime.now(),
                          firstDate: DateTime.now().subtract(
                            const Duration(days: 365),
                          ),
                          lastDate: DateTime.now(),
                        );
                        if (date != null) {
                          setState(() {
                            _waitingSince = date;
                          });
                        }
                      },
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 16),
            ],

            // Due Date
            ListTile(
              contentPadding: EdgeInsets.zero,
              title: Text(
                _dueDate == null
                    ? 'Due Date (optional)'
                    : 'Due: ${DateFormat.yMMMd().format(_dueDate!)}',
              ),
              trailing: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (_dueDate != null)
                    IconButton(
                      icon: const Icon(Icons.clear),
                      onPressed: () {
                        setState(() {
                          _dueDate = null;
                        });
                      },
                    ),
                  IconButton(
                    icon: const Icon(Icons.calendar_today),
                    onPressed: () async {
                      final date = await showDatePicker(
                        context: context,
                        initialDate: _dueDate ?? DateTime.now(),
                        firstDate: DateTime.now(),
                        lastDate: DateTime.now().add(
                          const Duration(days: 3650),
                        ),
                      );
                      if (date != null) {
                        setState(() {
                          _dueDate = date;
                        });
                      }
                    },
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Defer Until Date
            ListTile(
              contentPadding: EdgeInsets.zero,
              title: Text(
                _deferUntil == null
                    ? 'Defer Until (optional)'
                    : 'Defer Until: ${DateFormat.yMMMd().format(_deferUntil!)}',
              ),
              subtitle: _deferUntil == null
                  ? const Text('Hide this task until a specific date')
                  : null,
              trailing: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (_deferUntil != null)
                    IconButton(
                      icon: const Icon(Icons.clear),
                      onPressed: () {
                        setState(() {
                          _deferUntil = null;
                        });
                      },
                    ),
                  IconButton(
                    icon: const Icon(Icons.calendar_today),
                    onPressed: () async {
                      final date = await showDatePicker(
                        context: context,
                        initialDate: _deferUntil ?? DateTime.now(),
                        firstDate: DateTime.now(),
                        lastDate: DateTime.now().add(
                          const Duration(days: 3650),
                        ),
                      );
                      if (date != null) {
                        setState(() {
                          _deferUntil = date;
                        });
                      }
                    },
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // Priority section
            Text('Priority', style: theme.textTheme.titleMedium),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              children: [
                _buildPriorityChip(null, 'None', colorScheme),
                _buildPriorityChip(
                  1,
                  'P1 - High',
                  colorScheme,
                  color: Colors.red,
                ),
                _buildPriorityChip(
                  2,
                  'P2 - Medium',
                  colorScheme,
                  color: Colors.orange,
                ),
                _buildPriorityChip(
                  3,
                  'P3 - Low',
                  colorScheme,
                  color: Colors.yellow.shade700,
                ),
              ],
            ),
            const SizedBox(height: 24),

            // Energy level section
            Text('Energy Level', style: theme.textTheme.titleMedium),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              children: [
                _buildEnergyChip(null, 'None', colorScheme),
                _buildEnergyChip(1, '1 - Low', colorScheme),
                _buildEnergyChip(2, '2', colorScheme),
                _buildEnergyChip(3, '3', colorScheme),
                _buildEnergyChip(4, '4', colorScheme),
                _buildEnergyChip(5, '5 - High', colorScheme),
              ],
            ),
            const SizedBox(height: 24),

            // Duration estimate
            TextFormField(
              decoration: InputDecoration(
                labelText: 'Duration (minutes)',
                hintText: 'How long will this take?',
                border: const OutlineInputBorder(),
                prefixIcon: const Icon(Icons.access_time),
                filled: true,
                fillColor: colorScheme.surface,
              ),
              keyboardType: TextInputType.number,
              inputFormatters: [FilteringTextInputFormatter.digitsOnly],
              onChanged: (value) {
                if (value.isNotEmpty) {
                  _durationEstMin = int.tryParse(value);
                } else {
                  _durationEstMin = null;
                }
              },
            ),
            const SizedBox(height: 32),

            // Submit button
            FilledButton.icon(
              onPressed: _isLoading ? null : _submitForm,
              icon: _isLoading
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        color: Colors.white,
                      ),
                    )
                  : const Icon(Icons.check),
              label: Text(_isLoading ? 'Creating...' : 'Create Task'),
              style: FilledButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
            ),
            const SizedBox(height: 16),

            // Cancel button
            OutlinedButton(
              onPressed: _isLoading
                  ? null
                  : () {
                      Navigator.of(context).pop();
                    },
              style: OutlinedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
              ),
              child: const Text('Cancel'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPriorityChip(
    int? priority,
    String label,
    ColorScheme colorScheme, {
    Color? color,
  }) {
    final isSelected = _priority == priority;
    return FilterChip(
      label: Text(label),
      selected: isSelected,
      onSelected: (selected) {
        setState(() {
          _priority = selected ? priority : null;
        });
      },
      backgroundColor: color?.withValues(alpha: 0.1),
      selectedColor:
          color?.withValues(alpha: 0.3) ?? colorScheme.primaryContainer,
      checkmarkColor: color ?? colorScheme.primary,
    );
  }

  Widget _buildEnergyChip(int? energy, String label, ColorScheme colorScheme) {
    final isSelected = _energy == energy;
    return FilterChip(
      label: Text(label),
      selected: isSelected,
      onSelected: (selected) {
        setState(() {
          _energy = selected ? energy : null;
        });
      },
      avatar: energy != null ? const Icon(Icons.bolt, size: 16) : null,
    );
  }
}
