class Project {
  final int id;
  final int userId;
  final int? areaId;
  final String title;
  final String? outcome;
  final String? notes;
  final String status;
  final DateTime? dueDate;
  final DateTime createdAt;
  final DateTime? completedAt;

  Project({
    required this.id,
    required this.userId,
    this.areaId,
    required this.title,
    this.outcome,
    this.notes,
    required this.status,
    this.dueDate,
    required this.createdAt,
    this.completedAt,
  });

  factory Project.fromJson(Map<String, dynamic> json) {
    return Project(
      id: json['id'] as int,
      userId: json['userId'] as int,
      areaId: json['areaId'] as int?,
      title: json['title'] as String,
      outcome: json['outcome'] as String?,
      notes: json['notes'] as String?,
      status: json['status'] as String,
      dueDate: json['dueDate'] != null
          ? DateTime.parse(json['dueDate'] as String)
          : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
      completedAt: json['completedAt'] != null
          ? DateTime.parse(json['completedAt'] as String)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'areaId': areaId,
      'title': title,
      'outcome': outcome,
      'notes': notes,
      'status': status,
      'dueDate': dueDate?.toIso8601String().split('T')[0],
      'createdAt': createdAt.toIso8601String(),
      'completedAt': completedAt?.toIso8601String(),
    };
  }

  String get statusDisplay {
    switch (status) {
      case 'active':
        return 'Active';
      case 'on_hold':
        return 'On Hold';
      case 'someday':
        return 'Someday/Maybe';
      case 'completed':
        return 'Completed';
      case 'dropped':
        return 'Dropped';
      default:
        return status;
    }
  }
}

class CreateProjectDto {
  final int userId;
  final int? areaId;
  final String title;
  final String? outcome;
  final String? notes;
  final String status;
  final DateTime? dueDate;

  CreateProjectDto({
    required this.userId,
    this.areaId,
    required this.title,
    this.outcome,
    this.notes,
    required this.status,
    this.dueDate,
  });

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'areaId': areaId,
      'title': title,
      'outcome': outcome,
      'notes': notes,
      'status': status,
      'dueDate': dueDate?.toIso8601String().split('T')[0],
    };
  }
}
