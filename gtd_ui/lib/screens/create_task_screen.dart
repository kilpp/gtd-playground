import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../services/task_service.dart';

class CreateTaskScreen extends StatefulWidget {
  final int userId;

  const CreateTaskScreen({
    super.key,
    required this.userId,
  });

  @override
  State<CreateTaskScreen> createState() => _CreateTaskScreenState();
}

class _CreateTaskScreenState extends State<CreateTaskScreen> {
  final _formKey = GlobalKey<FormState>();
  final TaskService _taskService = TaskService();

  // Form controllers
  final _titleController = TextEditingController();
  final _notesController = TextEditingController();

  // Form values
  String _status = 'inbox';
  int? _priority;
  int? _energy;
  int? _durationEstMin;

  bool _isLoading = false;

  @override
  void dispose() {
    _titleController.dispose();
    _notesController.dispose();
    super.dispose();
  }

  Future<void> _submitForm() async {
    if (!_formKey.currentState!.validate()) {
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
                DropdownMenuItem(
                    value: 'waiting', child: Text('Waiting For')),
                DropdownMenuItem(
                    value: 'scheduled', child: Text('Scheduled')),
                DropdownMenuItem(value: 'someday', child: Text('Someday/Maybe')),
              ],
              onChanged: (value) {
                setState(() {
                  _status = value!;
                });
              },
            ),
            const SizedBox(height: 24),

            // Priority section
            Text(
              'Priority',
              style: theme.textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              children: [
                _buildPriorityChip(null, 'None', colorScheme),
                _buildPriorityChip(1, 'P1 - High', colorScheme,
                    color: Colors.red),
                _buildPriorityChip(2, 'P2 - Medium', colorScheme,
                    color: Colors.orange),
                _buildPriorityChip(3, 'P3 - Low', colorScheme,
                    color: Colors.yellow.shade700),
              ],
            ),
            const SizedBox(height: 24),

            // Energy level section
            Text(
              'Energy Level',
              style: theme.textTheme.titleMedium,
            ),
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
      int? priority, String label, ColorScheme colorScheme,
      {Color? color}) {
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
      selectedColor: color?.withValues(alpha: 0.3) ?? colorScheme.primaryContainer,
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
