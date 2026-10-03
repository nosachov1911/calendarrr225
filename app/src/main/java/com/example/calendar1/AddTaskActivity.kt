package com.example.calendar1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val titleInput = findViewById<EditText>(R.id.taskTitleInput)
        val descriptionInput = findViewById<EditText>(R.id.taskDescriptionInput)
        val dateInput = findViewById<EditText>(R.id.taskDateInput)
        val saveButton = findViewById<Button>(R.id.saveTaskButton)

        saveButton.setOnClickListener {

            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val date = dateInput.text.toString()

            val resultIntent = Intent()

            resultIntent.putExtra("title", title)
            resultIntent.putExtra("description", description)
            resultIntent.putExtra("date", date)

            setResult(RESULT_OK, resultIntent)

            finish()
        }
    }
}