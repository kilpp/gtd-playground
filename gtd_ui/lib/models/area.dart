class Area {
  final int id;
  final int userId;
  final String name;
  final String? description;
  final DateTime createdAt;

  Area({
    required this.id,
    required this.userId,
    required this.name,
    this.description,
    required this.createdAt,
  });

  factory Area.fromJson(Map<String, dynamic> json) {
    return Area(
      id: json['id'] as int,
      userId: json['userId'] as int,
      name: json['name'] as String,
      description: json['description'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'name': name,
      'description': description,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}

class CreateAreaDto {
  final int userId;
  final String name;
  final String? description;

  CreateAreaDto({required this.userId, required this.name, this.description});

  Map<String, dynamic> toJson() {
    return {'userId': userId, 'name': name, 'description': description};
  }
}
