package com.lab.todo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val addTaskBtn: Button = findViewById(R.id.addTaskBtn)

        // Navigate to NewTaskActivity
      //  addTaskBtn.setOnClickListener {
      //      val intent = Intent(this, NewTaskActivity::class.java)
      //      startActivity(intent)
    //    }
    }
}