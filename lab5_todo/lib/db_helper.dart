import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import 'models/task_item.dart';

class DbHelper {
  DbHelper._internal();
  static final DbHelper instance = DbHelper._internal();

  static const _dbName = 'tasks.db';
  static const _dbVersion = 1;
  static const _tableName = 'tasks';

  Database? _database;

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDb();
    return _database!;
  }

  Future<Database> _initDb() async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, _dbName);

    return openDatabase(
      path,
      version: _dbVersion,
      onCreate: (db, version) async {
        await db.execute('''
          CREATE TABLE $_tableName (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            description TEXT NOT NULL,
            deadline TEXT NOT NULL,
            imagePath TEXT
          )
        ''');
      },
    );
  }

  Future<List<TaskItem>> getAllTasks() async {
    final db = await database;
    final maps = await db.query(
      _tableName,
      orderBy: 'id DESC',
    );
    return maps.map((m) => TaskItem.fromMap(m)).toList();
  }

  Future<int> insertTask(TaskItem task) async {
    final db = await database;
    return db.insert(_tableName, task.toMap(includeId: false));
  }

  Future<int> updateTask(TaskItem task) async {
    final db = await database;
    return db.update(
      _tableName,
      task.toMap(includeId: false),
      where: 'id = ?',
      whereArgs: [task.id],
    );
  }

  Future<int> deleteTask(int id) async {
    final db = await database;
    return db.delete(
      _tableName,
      where: 'id = ?',
      whereArgs: [id],
    );
  }
}
