import 'package:flutter/material.dart';
import '../models/context.dart';
import '../services/context_service.dart';

class ContextsScreen extends StatefulWidget {
  final int userId;

  const ContextsScreen({super.key, required this.userId});

  @override
  State<ContextsScreen> createState() => _ContextsScreenState();
}

class _ContextsScreenState extends State<ContextsScreen> {
  final ContextService _contextService = ContextService();
  List<Context> _contexts = [];
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadContexts();
  }

  Future<void> _loadContexts() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final contexts = await _contextService.getContextsByUserId(widget.userId);
      setState(() {
        _contexts = contexts;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _showCreateContextDialog() async {
    final nameController = TextEditingController();
    final descriptionController = TextEditingController();
    bool isLocation = false;

    final result = await showDialog<bool>(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              title: const Text('Create Context'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextField(
                    controller: nameController,
                    decoration: const InputDecoration(
                      labelText: 'Context Name',
                      hintText: 'e.g., Office, Home, Phone',
                      border: OutlineInputBorder(),
                    ),
                    autofocus: true,
                    textCapitalization: TextCapitalization.words,
                  ),
                  const SizedBox(height: 16),
                  TextField(
                    controller: descriptionController,
                    decoration: const InputDecoration(
                      labelText: 'Description (optional)',
                      hintText: 'What is this context for?',
                      border: OutlineInputBorder(),
                    ),
                    maxLines: 3,
                    textCapitalization: TextCapitalization.sentences,
                  ),
                  const SizedBox(height: 16),
                  CheckboxListTile(
                    title: const Text('Is this a physical location?'),
                    subtitle: const Text(
                      'e.g., Office, Home, Store (vs. Phone, Computer)',
                    ),
                    value: isLocation,
                    onChanged: (value) {
                      setDialogState(() {
                        isLocation = value ?? false;
                      });
                    },
                    controlAffinity: ListTileControlAffinity.leading,
                  ),
                ],
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(false),
                  child: const Text('Cancel'),
                ),
                FilledButton(
                  onPressed: () {
                    if (nameController.text.trim().isEmpty) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Please enter a name')),
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
        await _contextService.createContext(
          userId: widget.userId,
          name: nameController.text.trim(),
          description: descriptionController.text.trim().isEmpty
              ? null
              : descriptionController.text.trim(),
          isLocation: isLocation,
        );
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Context created successfully')),
          );
          _loadContexts();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(
            context,
          ).showSnackBar(SnackBar(content: Text('Error creating context: $e')));
        }
      }
    }
  }

  Future<void> _showEditContextDialog(Context contextToEdit) async {
    final nameController = TextEditingController(text: contextToEdit.name);
    final descriptionController = TextEditingController(
      text: contextToEdit.description ?? '',
    );
    bool isLocation = contextToEdit.isLocation;

    final result = await showDialog<bool>(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              title: const Text('Edit Context'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextField(
                    controller: nameController,
                    decoration: const InputDecoration(
                      labelText: 'Context Name',
                      border: OutlineInputBorder(),
                    ),
                    autofocus: true,
                    textCapitalization: TextCapitalization.words,
                  ),
                  const SizedBox(height: 16),
                  TextField(
                    controller: descriptionController,
                    decoration: const InputDecoration(
                      labelText: 'Description (optional)',
                      border: OutlineInputBorder(),
                    ),
                    maxLines: 3,
                    textCapitalization: TextCapitalization.sentences,
                  ),
                  const SizedBox(height: 16),
                  CheckboxListTile(
                    title: const Text('Is this a physical location?'),
                    subtitle: const Text(
                      'e.g., Office, Home, Store (vs. Phone, Computer)',
                    ),
                    value: isLocation,
                    onChanged: (value) {
                      setDialogState(() {
                        isLocation = value ?? false;
                      });
                    },
                    controlAffinity: ListTileControlAffinity.leading,
                  ),
                ],
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(false),
                  child: const Text('Cancel'),
                ),
                FilledButton(
                  onPressed: () {
                    if (nameController.text.trim().isEmpty) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Please enter a name')),
                      );
                      return;
                    }
                    Navigator.of(context).pop(true);
                  },
                  child: const Text('Save'),
                ),
              ],
            );
          },
        );
      },
    );

    if (result == true && mounted) {
      try {
        await _contextService.updateContext(
          id: contextToEdit.id,
          userId: widget.userId,
          name: nameController.text.trim(),
          description: descriptionController.text.trim().isEmpty
              ? null
              : descriptionController.text.trim(),
          isLocation: isLocation,
        );
        if (mounted) {
          ScaffoldMessenger.of(this.context).showSnackBar(
            const SnackBar(content: Text('Context updated successfully')),
          );
          _loadContexts();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(
            this.context,
          ).showSnackBar(SnackBar(content: Text('Error updating context: $e')));
        }
      }
    }
  }

  Future<void> _confirmDelete(Context contextToDelete) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Context'),
        content: Text(
          'Are you sure you want to delete "${contextToDelete.name}"?',
        ),
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
        await _contextService.deleteContext(contextToDelete.id);
        if (mounted) {
          ScaffoldMessenger.of(
            this.context,
          ).showSnackBar(const SnackBar(content: Text('Context deleted')));
          _loadContexts();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(
            this.context,
          ).showSnackBar(SnackBar(content: Text('Error deleting context: $e')));
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
        title: const Text('Contexts'),
        backgroundColor: colorScheme.primaryContainer,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadContexts,
            tooltip: 'Refresh',
          ),
        ],
      ),
      body: _buildBody(theme, colorScheme),
      floatingActionButton: FloatingActionButton(
        onPressed: _showCreateContextDialog,
        child: const Icon(Icons.add),
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
                'Error loading contexts',
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
                onPressed: _loadContexts,
                icon: const Icon(Icons.refresh),
                label: const Text('Retry'),
              ),
            ],
          ),
        ),
      );
    }

    if (_contexts.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.place_outlined,
                size: 80,
                color: colorScheme.primary.withValues(alpha: 0.3),
              ),
              const SizedBox(height: 16),
              Text('No contexts yet', style: theme.textTheme.titleLarge),
              const SizedBox(height: 8),
              Text(
                'Create contexts like "Office", "Home", or "Phone" to organize your tasks',
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
      onRefresh: _loadContexts,
      child: ListView.builder(
        padding: const EdgeInsets.all(8.0),
        itemCount: _contexts.length,
        itemBuilder: (context, index) {
          final ctx = _contexts[index];
          return _buildContextCard(ctx, theme, colorScheme);
        },
      ),
    );
  }

  Widget _buildContextCard(
    Context ctx,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      child: ListTile(
        leading: Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(
            color: colorScheme.secondaryContainer,
            borderRadius: BorderRadius.circular(8),
          ),
          child: Icon(Icons.place, color: colorScheme.secondary),
        ),
        title: Text(
          ctx.name,
          style: theme.textTheme.titleMedium?.copyWith(
            fontWeight: FontWeight.w600,
          ),
        ),
        subtitle: ctx.description != null && ctx.description!.isNotEmpty
            ? Padding(
                padding: const EdgeInsets.only(top: 4),
                child: Text(
                  ctx.description!,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: theme.textTheme.bodySmall,
                ),
              )
            : null,
        trailing: PopupMenuButton<String>(
          onSelected: (value) {
            if (value == 'edit') {
              _showEditContextDialog(ctx);
            } else if (value == 'delete') {
              _confirmDelete(ctx);
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
          // TODO: Navigate to tasks filtered by this context
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Viewing tasks for: ${ctx.name}')),
          );
        },
      ),
    );
  }
}
