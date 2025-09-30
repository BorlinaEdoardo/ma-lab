package com.lab.todo

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class NewTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)


        val titleInput: EditText = findViewById(R.id.taskTitle)
        val deadlinePicker: DatePicker = findViewById(R.id.taskDeadline)
        val descriptionInput: EditText = findViewById(R.id.taskDescription)
        val doneBtn: Button = findViewById(R.id.doneBtn)


        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            finish()
        }

        // Done Button
        doneBtn.setOnClickListener {
            val title = titleInput.text.toString().ifBlank { "Untitled" } // fallback title

            finish()
        }
    }
}