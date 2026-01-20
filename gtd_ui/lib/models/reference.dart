class Reference {
  final int id;
  final int userId;
  final String title;
  final String? body;
  final String? url;
  final String? fileHint;
  final DateTime createdAt;

  Reference({
    required this.id,
    required this.userId,
    required this.title,
    this.body,
    this.url,
    this.fileHint,
    required this.createdAt,
  });

  factory Reference.fromJson(Map<String, dynamic> json) {
    return Reference(
      id: json['id'] as int,
      userId: json['userId'] as int,
      title: json['title'] as String,
      body: json['body'] as String?,
      url: json['url'] as String?,
      fileHint: json['fileHint'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'title': title,
      'body': body,
      'url': url,
      'fileHint': fileHint,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}

class CreateReferenceDto {
  final int userId;
  final String title;
  final String? body;
  final String? url;
  final String? fileHint;

  CreateReferenceDto({
    required this.userId,
    required this.title,
    this.body,
    this.url,
    this.fileHint,
  });

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'title': title,
      'body': body,
      'url': url,
      'fileHint': fileHint,
    };
  }
}
