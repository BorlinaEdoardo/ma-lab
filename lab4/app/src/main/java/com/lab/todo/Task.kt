package com.lab.todo

data class Task(
    val title: String,
    val description: String? = null,
    val color: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val deadline: Long? = null
)
