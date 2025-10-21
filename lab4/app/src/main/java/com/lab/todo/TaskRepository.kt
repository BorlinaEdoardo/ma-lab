package com.lab.todo

object TaskRepository {
    private val _tasks = mutableListOf<Task>()
    val tasks: List<Task> get() = _tasks

    fun add(task: Task) {
        _tasks.add(0, task) // add to top
    }

    fun clear() {
        _tasks.clear()
    }
}
