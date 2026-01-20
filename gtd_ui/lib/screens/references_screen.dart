import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import '../models/reference.dart';
import '../services/reference_service.dart';

class ReferencesScreen extends StatefulWidget {
  final int userId;

  const ReferencesScreen({super.key, required this.userId});

  @override
  State<ReferencesScreen> createState() => _ReferencesScreenState();
}

class _ReferencesScreenState extends State<ReferencesScreen> {
  final ReferenceService _referenceService = ReferenceService();
  List<Reference> _references = [];
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadReferences();
  }

  Future<void> _loadReferences() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final references =
          await _referenceService.getReferencesByUserId(widget.userId);
      setState(() {
        _references = references;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _showCreateReferenceDialog() async {
    final titleController = TextEditingController();
    final bodyController = TextEditingController();
    final urlController = TextEditingController();
    final fileHintController = TextEditingController();

    final result = await showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Create Reference'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: titleController,
                  decoration: const InputDecoration(
                    labelText: 'Title*',
                    hintText: 'e.g., GTD Weekly Review Checklist',
                    border: OutlineInputBorder(),
                  ),
                  autofocus: true,
                  textCapitalization: TextCapitalization.words,
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: bodyController,
                  decoration: const InputDecoration(
                    labelText: 'Content (optional)',
                    hintText: 'Text content or notes',
                    border: OutlineInputBorder(),
                  ),
                  maxLines: 5,
                  textCapitalization: TextCapitalization.sentences,
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: urlController,
                  decoration: const InputDecoration(
                    labelText: 'URL (optional)',
                    hintText: 'https://example.com/resource',
                    border: OutlineInputBorder(),
                  ),
                  keyboardType: TextInputType.url,
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: fileHintController,
                  decoration: const InputDecoration(
                    labelText: 'File Hint (optional)',
                    hintText: 'Local file path or reference ID',
                    border: OutlineInputBorder(),
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
            ElevatedButton(
              onPressed: () {
                if (titleController.text.trim().isEmpty) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Please enter a title'),
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

    if (result == true) {
      try {
        await _referenceService.createReference(
          userId: widget.userId,
          title: titleController.text.trim(),
          body: bodyController.text.trim().isEmpty
              ? null
              : bodyController.text.trim(),
          url: urlController.text.trim().isEmpty
              ? null
              : urlController.text.trim(),
          fileHint: fileHintController.text.trim().isEmpty
              ? null
              : fileHintController.text.trim(),
        );

        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Reference created successfully'),
              backgroundColor: Colors.green,
            ),
          );
          _loadReferences();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to create reference: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  Future<void> _showEditReferenceDialog(Reference reference) async {
    final titleController = TextEditingController(text: reference.title);
    final bodyController = TextEditingController(text: reference.body ?? '');
    final urlController = TextEditingController(text: reference.url ?? '');
    final fileHintController =
        TextEditingController(text: reference.fileHint ?? '');

    final result = await showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Edit Reference'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: titleController,
                  decoration: const InputDecoration(
                    labelText: 'Title*',
                    border: OutlineInputBorder(),
                  ),
                  autofocus: true,
                  textCapitalization: TextCapitalization.words,
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: bodyController,
                  decoration: const InputDecoration(
                    labelText: 'Content (optional)',
                    border: OutlineInputBorder(),
                  ),
                  maxLines: 5,
                  textCapitalization: TextCapitalization.sentences,
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: urlController,
                  decoration: const InputDecoration(
                    labelText: 'URL (optional)',
                    border: OutlineInputBorder(),
                  ),
                  keyboardType: TextInputType.url,
                ),
                const SizedBox(height: 16),
                TextField(
                  controller: fileHintController,
                  decoration: const InputDecoration(
                    labelText: 'File Hint (optional)',
                    border: OutlineInputBorder(),
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
            ElevatedButton(
              onPressed: () {
                if (titleController.text.trim().isEmpty) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Please enter a title'),
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

    if (result == true) {
      try {
        await _referenceService.updateReference(
          id: reference.id,
          userId: widget.userId,
          title: titleController.text.trim(),
          body: bodyController.text.trim().isEmpty
              ? null
              : bodyController.text.trim(),
          url: urlController.text.trim().isEmpty
              ? null
              : urlController.text.trim(),
          fileHint: fileHintController.text.trim().isEmpty
              ? null
              : fileHintController.text.trim(),
        );

        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Reference updated successfully'),
              backgroundColor: Colors.green,
            ),
          );
          _loadReferences();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to update reference: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  Future<void> _deleteReference(Reference reference) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Delete Reference'),
          content: Text(
            'Are you sure you want to delete "${reference.title}"?',
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () => Navigator.of(context).pop(true),
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                foregroundColor: Colors.white,
              ),
              child: const Text('Delete'),
            ),
          ],
        );
      },
    );

    if (confirmed == true) {
      try {
        await _referenceService.deleteReference(reference.id);

        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Reference deleted successfully'),
              backgroundColor: Colors.green,
            ),
          );
          _loadReferences();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Failed to delete reference: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  Future<void> _openUrl(String url) async {
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    } else {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Could not open URL: $url'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _showReferenceDetails(Reference reference) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text(reference.title),
          content: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: [
                if (reference.body != null && reference.body!.isNotEmpty) ...[
                  const Text(
                    'Content:',
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  Text(reference.body!),
                  const SizedBox(height: 16),
                ],
                if (reference.url != null && reference.url!.isNotEmpty) ...[
                  const Text(
                    'URL:',
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  InkWell(
                    onTap: () => _openUrl(reference.url!),
                    child: Text(
                      reference.url!,
                      style: const TextStyle(
                        color: Colors.blue,
                        decoration: TextDecoration.underline,
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                ],
                if (reference.fileHint != null &&
                    reference.fileHint!.isNotEmpty) ...[
                  const Text(
                    'File Hint:',
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  Text(reference.fileHint!),
                  const SizedBox(height: 16),
                ],
                const Text(
                  'Created:',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 8),
                Text(
                  '${reference.createdAt.day}/${reference.createdAt.month}/${reference.createdAt.year}',
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Close'),
            ),
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
                _showEditReferenceDialog(reference);
              },
              child: const Text('Edit'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Reference Store'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _errorMessage != null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        'Error: $_errorMessage',
                        style: const TextStyle(color: Colors.red),
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 16),
                      ElevatedButton(
                        onPressed: _loadReferences,
                        child: const Text('Retry'),
                      ),
                    ],
                  ),
                )
              : _references.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.folder_open,
                            size: 64,
                            color: Colors.grey[400],
                          ),
                          const SizedBox(height: 16),
                          Text(
                            'No references yet',
                            style: TextStyle(
                              fontSize: 18,
                              color: Colors.grey[600],
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            'Tap + to add your first reference',
                            style: TextStyle(
                              fontSize: 14,
                              color: Colors.grey[500],
                            ),
                          ),
                        ],
                      ),
                    )
                  : RefreshIndicator(
                      onRefresh: _loadReferences,
                      child: ListView.builder(
                        itemCount: _references.length,
                        itemBuilder: (context, index) {
                          final reference = _references[index];
                          return Card(
                            margin: const EdgeInsets.symmetric(
                              horizontal: 16,
                              vertical: 8,
                            ),
                            child: ListTile(
                              leading: const Icon(
                                Icons.description,
                                color: Colors.blue,
                              ),
                              title: Text(
                                reference.title,
                                style: const TextStyle(
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              subtitle: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  if (reference.body != null &&
                                      reference.body!.isNotEmpty)
                                    Text(
                                      reference.body!,
                                      maxLines: 2,
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                  if (reference.url != null)
                                    Row(
                                      children: [
                                        const Icon(
                                          Icons.link,
                                          size: 14,
                                          color: Colors.blue,
                                        ),
                                        const SizedBox(width: 4),
                                        Expanded(
                                          child: Text(
                                            reference.url!,
                                            maxLines: 1,
                                            overflow: TextOverflow.ellipsis,
                                            style: const TextStyle(
                                              color: Colors.blue,
                                              fontSize: 12,
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                ],
                              ),
                              trailing: PopupMenuButton<String>(
                                onSelected: (value) {
                                  if (value == 'edit') {
                                    _showEditReferenceDialog(reference);
                                  } else if (value == 'delete') {
                                    _deleteReference(reference);
                                  } else if (value == 'open_url' &&
                                      reference.url != null) {
                                    _openUrl(reference.url!);
                                  }
                                },
                                itemBuilder: (context) => [
                                  const PopupMenuItem(
                                    value: 'edit',
                                    child: Row(
                                      children: [
                                        Icon(Icons.edit, size: 20),
                                        SizedBox(width: 8),
                                        Text('Edit'),
                                      ],
                                    ),
                                  ),
                                  if (reference.url != null)
                                    const PopupMenuItem(
                                      value: 'open_url',
                                      child: Row(
                                        children: [
                                          Icon(Icons.open_in_new, size: 20),
                                          SizedBox(width: 8),
                                          Text('Open URL'),
                                        ],
                                      ),
                                    ),
                                  const PopupMenuItem(
                                    value: 'delete',
                                    child: Row(
                                      children: [
                                        Icon(Icons.delete,
                                            size: 20, color: Colors.red),
                                        SizedBox(width: 8),
                                        Text('Delete',
                                            style: TextStyle(color: Colors.red)),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                              onTap: () => _showReferenceDetails(reference),
                            ),
                          );
                        },
                      ),
                    ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showCreateReferenceDialog,
        tooltip: 'Create Reference',
        child: const Icon(Icons.add),
      ),
    );
  }
}
