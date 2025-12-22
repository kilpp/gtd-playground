class Context {
  final int id;
  final String name;
  final String? description;
  final int userId;
  final bool isLocation;
  final DateTime createdAt;
  final DateTime? updatedAt;

  Context({
    required this.id,
    required this.name,
    this.description,
    required this.userId,
    required this.isLocation,
    required this.createdAt,
    this.updatedAt,
  });

  factory Context.fromJson(Map<String, dynamic> json) {
    return Context(
      id: json['id'] as int,
      name: json['name'] as String,
      description: json['description'] as String?,
      userId: json['userId'] as int,
      isLocation: json['isLocation'] as bool? ?? false,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'] as String)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'user_id': userId,
      'is_location': isLocation,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt?.toIso8601String(),
    };
  }
}
