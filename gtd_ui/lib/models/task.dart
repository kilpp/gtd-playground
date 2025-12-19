class Task {
  final int id;
  final int userId;
  final int? projectId;
  final int? contextId;
  final String title;
  final String? notes;
  final String status;
  final int? priority;
  final int? energy;
  final int? durationEstMin;
  final DateTime? dueAt;
  final DateTime? deferUntil;
  final String? waitingOn;
  final DateTime? waitingSince;
  final DateTime createdAt;
  final DateTime? completedAt;
  final int? orderIndex;

  Task({
    required this.id,
    required this.userId,
    this.projectId,
    this.contextId,
    required this.title,
    this.notes,
    required this.status,
    this.priority,
    this.energy,
    this.durationEstMin,
    this.dueAt,
    this.deferUntil,
    this.waitingOn,
    this.waitingSince,
    required this.createdAt,
    this.completedAt,
    this.orderIndex,
  });

  factory Task.fromJson(Map<String, dynamic> json) {
    return Task(
      id: json['id'] as int,
      userId: json['userId'] as int,
      projectId: json['projectId'] as int?,
      contextId: json['contextId'] as int?,
      title: json['title'] as String,
      notes: json['notes'] as String?,
      status: json['status'] as String,
      priority: json['priority'] as int?,
      energy: json['energy'] as int?,
      durationEstMin: json['durationEstMin'] as int?,
      dueAt: json['dueAt'] != null ? DateTime.parse(json['dueAt'] as String) : null,
      deferUntil: json['deferUntil'] != null ? DateTime.parse(json['deferUntil'] as String) : null,
      waitingOn: json['waitingOn'] as String?,
      waitingSince: json['waitingSince'] != null ? DateTime.parse(json['waitingSince'] as String) : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
      completedAt: json['completedAt'] != null ? DateTime.parse(json['completedAt'] as String) : null,
      orderIndex: json['orderIndex'] as int?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'projectId': projectId,
      'contextId': contextId,
      'title': title,
      'notes': notes,
      'status': status,
      'priority': priority,
      'energy': energy,
      'durationEstMin': durationEstMin,
      'dueAt': dueAt?.toIso8601String(),
      'deferUntil': deferUntil?.toIso8601String(),
      'waitingOn': waitingOn,
      'waitingSince': waitingSince?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
      'completedAt': completedAt?.toIso8601String(),
      'orderIndex': orderIndex,
    };
  }
}
