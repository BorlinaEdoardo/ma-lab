package com.lab.todo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private lateinit var dbHelper: TaskDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val addTaskBtn: Button = findViewById(R.id.addTaskBtn)
        val searchBox: EditText = findViewById(R.id.searchBox)
        val rv: RecyclerView = findViewById(R.id.tasksRecycler)

        adapter = TaskAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        addTaskBtn.setOnClickListener {
            startActivity(Intent(this, NewTaskActivity::class.java))
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        //to open edit screen with the selected task id ~GK
        adapter.onItemClick = { task ->
            task.id?.let {
                val i = Intent(this, EditTaskActivity::class.java)
                i.putExtra("task_id", it)
                startActivity(i)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        dbHelper = TaskDatabaseHelper(this)

        // refresh list and show newly added items immediately
        //adapter.submit(TaskRepository.tasks)
        adapter.submit(dbHelper.getAllTasks())

    }
}
