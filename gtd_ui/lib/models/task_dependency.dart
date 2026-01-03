class TaskDependency {
  final int taskId;
  final int dependsOnTaskId;

  TaskDependency({required this.taskId, required this.dependsOnTaskId});

  factory TaskDependency.fromJson(Map<String, dynamic> json) {
    return TaskDependency(
      taskId: json['taskId'] as int,
      dependsOnTaskId: json['dependsOnTaskId'] as int,
    );
  }

  Map<String, dynamic> toJson() {
    return {'taskId': taskId, 'dependsOnTaskId': dependsOnTaskId};
  }
}
