package com.lab.todo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


// added imageUri to onCreate ~GK
class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "tasks.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            
            CREATE TABLE IF NOT EXISTS Tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                color INTEGER NOT NULL,
                createdAt LONG NOT NULL,
                deadline LONG,
                imageUri TEXT 
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //for migration ~GK
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE Tasks ADD COLUMN imageUri TEXT")
        } else {
            db?.execSQL("DROP TABLE IF EXISTS Tasks")
            onCreate(db!!)
        }
    }

    fun insertTask(task: Task): Long {
        val title = task.title
        val description = task.description
        val color = task.color
        val createdAt = task.createdAt
        val deadline = task.deadline

        val db = writableDatabase
        db.execSQL(  //added imageUri ~GK
            "INSERT INTO Tasks (title, description, color, createdAt, deadline, imageUri) VALUES (?, ?, ?, ?, ?, ?)",
            arrayOf(title, description, color, createdAt, deadline, task.imageUri)
        )
        //to get rowid ~GK
        val c = db.rawQuery("SELECT last_insert_rowid()", null)
        var id = -1L
        if (c.moveToFirst()) id = c.getLong(0)
        c.close()
        return id
    }

    //edited the getAllTasks function - added imageUri ~GK
    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, title, description, color, createdAt, deadline, imageUri FROM Tasks ORDER BY createdAt DESC",
            null
        )
        cursor.use {
            val idxId = it.getColumnIndexOrThrow("id")
            val idxTitle = it.getColumnIndexOrThrow("title")
            val idxDesc = it.getColumnIndexOrThrow("description")
            val idxColor = it.getColumnIndexOrThrow("color")
            val idxCreated = it.getColumnIndexOrThrow("createdAt")
            val idxDeadline = it.getColumnIndexOrThrow("deadline")
            val idxImage = it.getColumnIndexOrThrow("imageUri")
            while (it.moveToNext()) {
                tasks.add(
                    Task(
                        id = it.getLong(idxId),
                        title = it.getString(idxTitle),
                        description = it.getString(idxDesc),
                        color = it.getInt(idxColor),
                        createdAt = it.getLong(idxCreated),
                        deadline = if (it.isNull(idxDeadline)) null else it.getLong(idxDeadline),
                        imageUri = if (it.isNull(idxImage)) null else it.getString(idxImage)
                    )
                )
            }
        }
        cursor.close()
        return tasks
    }

    //edit screen ~GK
    fun getTaskById(id: Long): Task? {
        val db = readableDatabase
        val c = db.rawQuery(
            "SELECT id, title, description, color, createdAt, deadline, imageUri FROM Tasks WHERE id = ?",
            arrayOf(id.toString())
        )
        c.use {
            if (it.moveToFirst()) {
                return Task(
                    id = it.getLong(0),
                    title = it.getString(1),
                    description = it.getString(2),
                    color = it.getInt(3),
                    createdAt = it.getLong(4),
                    deadline = if (it.isNull(5)) null else it.getLong(5),
                    imageUri = if (it.isNull(6)) null else it.getString(6)
                )
            }
        }
        return null
    }

    //update ~GK
    fun updateTask(task: Task): Int {
        requireNotNull(task.id) { "Task.id required for update" }
        val db = writableDatabase
        db.execSQL(
            "UPDATE Tasks SET title = ?, description = ?, color = ?, createdAt = ?, deadline = ?, imageUri = ? WHERE id = ?",
            arrayOf(task.title, task.description, task.color, task.createdAt, task.deadline, task.imageUri, task.id)
        )
        return 1
    }

    //delete ~GK
    fun deleteTask(id: Long): Int {
        val db = writableDatabase
        db.execSQL("DELETE FROM Tasks WHERE id = ?", arrayOf(id))
        return 1
    }

}
