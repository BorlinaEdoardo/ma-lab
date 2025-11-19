import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

import 'db_helper.dart';
import 'models/task_item.dart';

void main() {
  runApp(const TodoApp());
}

class TodoApp extends StatelessWidget {
  const TodoApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Tasks',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF4ECDC4)),
        useMaterial3: true,
      ),
      home: const HomeScreen(),
    );
  }
}

// ------------------------------------------------------------
// HOME SCREEN
// ------------------------------------------------------------
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final DbHelper _db = DbHelper.instance;
  List<TaskItem> _tasks = [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    _tasks = await _db.getAllTasks();
    setState(() {});
  }

  Future<void> _add() async {
    final res = await Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => const TaskScreen()),
    );
    if (res is TaskItem) {
      await _db.insertTask(res);
      _load();
    }
  }

  Future<void> _edit(TaskItem task) async {
    final res = await Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => TaskScreen(existingTask: task)),
    );
    if (res is TaskItem) {
      await _db.updateTask(res);
      _load();
    }
  }

  Future<void> _delete(TaskItem task) async {
    if (task.id != null) {
      await _db.deleteTask(task.id!);
      _load();
    }
  }

  // ------------------------------------------------------------
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("My Tasks"),
        centerTitle: true,
        elevation: 0,
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _add,
        child: const Icon(Icons.add),
      ),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFFE9FDF7), Color(0xFFC8F4EF)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
        ),
        child: _tasks.isEmpty
            ? const Center(
          child: Text(
            "No tasks yet.\nTap + to add.",
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 18),
          ),
        )
            : ListView.builder(
          padding: const EdgeInsets.all(16),
          itemCount: _tasks.length,
          itemBuilder: (context, i) {
            final task = _tasks[i];
            return _taskTile(task);
          },
        ),
      ),
    );
  }

  Widget _taskTile(TaskItem task) {
    return GestureDetector(
      onTap: () => _edit(task),
      child: Container(
        margin: const EdgeInsets.only(bottom: 16),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.95),
          borderRadius: BorderRadius.circular(18),
          boxShadow: [
            BoxShadow(
              offset: const Offset(0, 4),
              color: Colors.black.withOpacity(0.08),
              blurRadius: 10,
            )
          ],
        ),
        child: Row(
          children: [
            _leading(task),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(task.title,
                      style: const TextStyle(
                          fontSize: 17, fontWeight: FontWeight.w600)),
                  const SizedBox(height: 4),
                  Text(
                    task.description,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 6),
                  Row(
                    children: [
                      const Icon(Icons.calendar_today, size: 14),
                      const SizedBox(width: 4),
                      Text(
                        task.deadline,
                        style:
                        const TextStyle(fontSize: 12, color: Colors.black54),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            IconButton(
              icon: const Icon(Icons.delete_outline),
              onPressed: () => _delete(task),
              color: Colors.redAccent,
            )
          ],
        ),
      ),
    );
  }

  Widget _leading(TaskItem task) {
    if (task.imagePath != null &&
        task.imagePath!.isNotEmpty &&
        !kIsWeb &&
        File(task.imagePath!).existsSync()) {
      return ClipRRect(
        borderRadius: BorderRadius.circular(12),
        child: Image.file(
          File(task.imagePath!),
          width: 55,
          height: 55,
          fit: BoxFit.cover,
        ),
      );
    }
    return Container(
      width: 55,
      height: 55,
      decoration: BoxDecoration(
        color: const Color(0xFF4ECDC4),
        borderRadius: BorderRadius.circular(12),
      ),
      child: const Icon(Icons.check, color: Colors.white, size: 28),
    );
  }
}

// ------------------------------------------------------------
// TASK SCREEN
// ------------------------------------------------------------
class TaskScreen extends StatefulWidget {
  final TaskItem? existingTask;
  const TaskScreen({super.key, this.existingTask});

  @override
  State<TaskScreen> createState() => _TaskScreenState();
}

class _TaskScreenState extends State<TaskScreen> {
  final _form = GlobalKey<FormState>();
  final _title = TextEditingController();
  final _desc = TextEditingController();
  final _deadline = TextEditingController();
  String? _imagePath;

  final picker = ImagePicker();

  @override
  void initState() {
    super.initState();
    final t = widget.existingTask;
    if (t != null) {
      _title.text = t.title;
      _desc.text = t.description;
      _deadline.text = t.deadline;
      _imagePath = t.imagePath;
    }
  }

  Future<void> _pickDate() async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      firstDate: DateTime(2020),
      lastDate: DateTime(2100),
      initialDate: now,
    );

    if (picked != null) {
      _deadline.text =
      "${picked.year}-${picked.month.toString().padLeft(2, '0')}-${picked.day.toString().padLeft(2, '0')}";
    }
  }

  Future<void> _pickImg(ImageSource src) async {
    final file = await picker.pickImage(source: src);
    if (file != null) setState(() => _imagePath = file.path);
  }

  void _save() {
    if (!_form.currentState!.validate()) return;

    final data = TaskItem(
      id: widget.existingTask?.id,
      title: _title.text.trim(),
      description: _desc.text.trim(),
      deadline: _deadline.text.trim(),
      imagePath: _imagePath,
    );

    Navigator.pop(context, data);
  }

  // ------------------------------------------------------------
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.existingTask == null ? "New Task" : "Edit Task"),
      ),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFFE9FDF7), Color(0xFFC8F4EF)],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
        ),
        child: Form(
          key: _form,
          child: ListView(
            padding: const EdgeInsets.all(18),
            children: [
              TextFormField(
                controller: _title,
                decoration: const InputDecoration(
                  labelText: "Title",
                  border: OutlineInputBorder(),
                ),
                validator: (v) =>
                v == null || v.isEmpty ? "Required" : null,
              ),
              const SizedBox(height: 14),

              // DATE PICKER FIELD
              TextFormField(
                controller: _deadline,
                readOnly: true,
                decoration: const InputDecoration(
                  labelText: "Deadline",
                  border: OutlineInputBorder(),
                  suffixIcon: Icon(Icons.calendar_month),
                ),
                onTap: _pickDate,
                validator: (v) =>
                v == null || v.isEmpty ? "Required" : null,
              ),
              const SizedBox(height: 14),

              TextFormField(
                controller: _desc,
                maxLines: 3,
                decoration: const InputDecoration(
                  labelText: "Description",
                  border: OutlineInputBorder(),
                ),
                validator: (v) =>
                v == null || v.isEmpty ? "Required" : null,
              ),
              const SizedBox(height: 16),

              Row(
                children: [
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () => _pickImg(ImageSource.gallery),
                      icon: const Icon(Icons.photo),
                      label: const Text("Gallery"),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () => _pickImg(ImageSource.camera),
                      icon: const Icon(Icons.camera_alt),
                      label: const Text("Camera"),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 14),

              if (_imagePath != null && !kIsWeb)
                ClipRRect(
                  borderRadius: BorderRadius.circular(14),
                  child: Image.file(File(_imagePath!), height: 200, fit: BoxFit.cover),
                ),

              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _save,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: Text(
                  widget.existingTask == null ? "Save Task" : "Save Changes",
                  style: const TextStyle(fontSize: 16),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
