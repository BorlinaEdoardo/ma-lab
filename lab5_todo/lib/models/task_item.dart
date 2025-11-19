class TaskItem {
  final int? id; // null for new tasks, non-null when saved in DB
  final String title;
  final String description;
  final String deadline;
  final String? imagePath; // file path of image (can be null)

  TaskItem({
    this.id,
    required this.title,
    required this.description,
    required this.deadline,
    this.imagePath,
  });

  TaskItem copyWith({
    int? id,
    String? title,
    String? description,
    String? deadline,
    String? imagePath,
  }) {
    return TaskItem(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      deadline: deadline ?? this.deadline,
      imagePath: imagePath ?? this.imagePath,
    );
  }

  factory TaskItem.fromMap(Map<String, dynamic> map) {
    return TaskItem(
      id: map['id'] as int?,
      title: map['title'] as String,
      description: map['description'] as String,
      deadline: map['deadline'] as String,
      imagePath: map['imagePath'] as String?,
    );
  }

  Map<String, dynamic> toMap({bool includeId = false}) {
    final data = <String, dynamic>{
      'title': title,
      'description': description,
      'deadline': deadline,
      'imagePath': imagePath,
    };
    if (includeId && id != null) {
      data['id'] = id;
    }
    return data;
  }
}
