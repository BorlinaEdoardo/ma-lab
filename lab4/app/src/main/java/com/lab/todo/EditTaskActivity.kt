package com.lab.todo

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.appbar.MaterialToolbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var db: TaskDatabaseHelper
    private var currentTask: Task? = null
    private var currentImageUri: Uri? = null
    private var cameraImageUri: Uri? = null

    //for gallery ~GK
    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentImageUri = uri
            findViewById<ImageView>(R.id.imagePreview).setImageURI(uri)
        }
    }

    //for camera ~GK
    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            currentImageUri = cameraImageUri
            findViewById<ImageView>(R.id.imagePreview).setImageURI(cameraImageUri)
        }
    }

    private fun formatDate(ms: Long?): String =
        if (ms == null) "" else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(ms))

    private fun parseDate(s: String): Long? =
        if (s.isBlank()) null
        else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(s)?.time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        db = TaskDatabaseHelper(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { finish() }

        val taskId = intent.getLongExtra("task_id", -1L)
        if (taskId == -1L) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val titleEt = findViewById<EditText>(R.id.titleInput)
        val descEt = findViewById<EditText>(R.id.descriptionInput)
        val deadlineEt = findViewById<EditText>(R.id.deadlineInput)
        val colorGroup = findViewById<RadioGroup>(R.id.colorGroup)
        val imageView = findViewById<ImageView>(R.id.imagePreview)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val deleteBtn = findViewById<Button>(R.id.deleteBtn)
        val pickBtn = findViewById<Button>(R.id.pickImageBtn)
        val captureBtn = findViewById<Button>(R.id.captureImageBtn)

        //load ~GK
        currentTask = db.getTaskById(taskId)
        currentTask?.let { t ->
            titleEt.setText(t.title)
            descEt.setText(t.description ?: "")
            deadlineEt.setText(formatDate(t.deadline))
            currentImageUri = t.imageUri?.let(Uri::parse)
            currentImageUri?.let { imageView.setImageURI(it) }

            when (t.color) {
                android.graphics.Color.parseColor("#F44336") -> colorGroup.check(R.id.colorRed)
                android.graphics.Color.parseColor("#4CAF50") -> colorGroup.check(R.id.colorGreen)
                android.graphics.Color.parseColor("#2196F3") -> colorGroup.check(R.id.colorBlue)
                android.graphics.Color.parseColor("#FF9800") -> colorGroup.check(R.id.colorOrange)
                else -> colorGroup.clearCheck()
            }
        }

        //use gallery ~GK
        pickBtn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //use camera ~GK
        captureBtn.setOnClickListener {
            val imgFile = File.createTempFile("task_", ".jpg", cacheDir)
            cameraImageUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                imgFile
            )
            cameraImageUri?.let { takePicture.launch(it) }
        }

        //save ~GK
        saveBtn.setOnClickListener {
            val title = titleEt.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val desc = descEt.text.toString().trim().ifEmpty { null }
            val deadline = parseDate(deadlineEt.text.toString().trim())
            val selectedId = colorGroup.checkedRadioButtonId
            val color = when (selectedId) {
                R.id.colorRed -> android.graphics.Color.parseColor("#F44336")
                R.id.colorGreen -> android.graphics.Color.parseColor("#4CAF50")
                R.id.colorBlue -> android.graphics.Color.parseColor("#2196F3")
                R.id.colorOrange -> android.graphics.Color.parseColor("#FF9800")
                else -> android.graphics.Color.BLACK
            }
            val updated = currentTask!!.copy(
                title = title,
                description = desc,
                color = color,
                deadline = deadline,
                imageUri = currentImageUri?.toString()
            )
            db.updateTask(updated)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        //delete ~GK
        deleteBtn.setOnClickListener {
            db.deleteTask(taskId)
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
