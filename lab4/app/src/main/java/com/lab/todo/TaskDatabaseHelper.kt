package com.lab.todo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "tasks.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            
            CREATE TABLE IF NOT EXISTS Tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                color INTEGER NOT NULL,
                createdAt LONG NOT NULL,
                deadline LONG
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Tasks")
        onCreate(db!!)
    }

    fun insertTask(Task: Task) {
        val title = Task.title
        val description = Task.description
        val color = Task.color
        val createdAt = Task.createdAt
        val deadline = Task.deadline

        val db = writableDatabase
        db.execSQL(
            "INSERT INTO Tasks (title, description, color, createdAt, deadline) VALUES (?, ?, ?, ?, ?)",
            arrayOf(title, description, color, createdAt, deadline)
        )
    }

    fun getAllTasks(): List<Task> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, title, description, color, createdAt, deadline FROM Tasks", null)
        val tasks = mutableListOf<Task>()

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val color = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
            val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("createdAt"))
            val deadline = if (!cursor.isNull(cursor.getColumnIndexOrThrow("deadline"))) {
                cursor.getLong(cursor.getColumnIndexOrThrow("deadline"))
            } else {
                null
            }
            tasks.add(Task(title, description, color, createdAt, deadline))
        }

        cursor.close()
        return tasks
    }

}