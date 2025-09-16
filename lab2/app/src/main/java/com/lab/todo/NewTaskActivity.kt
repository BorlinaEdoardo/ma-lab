package com.lab.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText


class NewTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        val userDate = findViewById<EditText>(R.id.editTextDate)

        val enteredText = userDate.text.toString()

    }
}