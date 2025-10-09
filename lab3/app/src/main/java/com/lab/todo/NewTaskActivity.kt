package com.lab.todo

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class NewTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        val titleInput: EditText = findViewById(R.id.taskTitle)
        val descriptionInput: EditText = findViewById(R.id.taskDescription)
        val deadlinePicker: DatePicker = findViewById(R.id.taskDeadline)
        val doneBtn: Button = findViewById(R.id.doneBtn)

        val colorGroup: RadioGroup = findViewById(R.id.colorGroup)

        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener { finish() }

        doneBtn.setOnClickListener {
            val title = titleInput.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val desc = descriptionInput.text?.toString()?.takeIf { it.isNotBlank() }

            // Map selected radio to a color int
            val selectedId = colorGroup.checkedRadioButtonId
            val color = when (selectedId) {
                R.id.colorRed -> Color.parseColor("#F44336")
                R.id.colorGreen -> Color.parseColor("#4CAF50")
                R.id.colorBlue -> Color.parseColor("#2196F3")
                R.id.colorOrange -> Color.parseColor("#FF9800")
                else -> Color.parseColor("#000000")
            }

            TaskRepository.add(Task(title = title, description = desc, color = color))
            finish()
        }
    }
}
