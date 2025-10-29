package com.lab.todo

data class Task(

    val id: Long? = null, //primary Key ~GK

    val title: String,

    val description: String? = null,

    val color: Int,

    val createdAt: Long = System.currentTimeMillis(),

    val deadline: Long? = null,

    val imageUri: String? = null          //image ~GK

)
