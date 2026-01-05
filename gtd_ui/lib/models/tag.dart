class Tag {
  final int id;
  final int userId;
  final String name;
  final DateTime createdAt;

  Tag({
    required this.id,
    required this.userId,
    required this.name,
    required this.createdAt,
  });

  factory Tag.fromJson(Map<String, dynamic> json) {
    return Tag(
      id: json['id'] as int,
      userId: json['userId'] as int,
      name: json['name'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'name': name,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
