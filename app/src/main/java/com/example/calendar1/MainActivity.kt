package com.example.calendar1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter

    private var tasks = mutableListOf<Task>()
    private var selectedTaskPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)
        taskDao = database.taskDao()

        recyclerView = findViewById(R.id.tasksRecyclerView)

        setupRecyclerView()

        lifecycleScope.launch {

            val tasksFromDatabase = taskDao.getAllTasks()

            if (tasksFromDatabase.size <= 2) {

                val oldTasks = listOf(
                    Task(
                        title = "Подготовить отчёт",
                        description = "Закончить практическую работу",
                        isDone = false,
                        date = "25.09.2026",
                        time = "18:00"
                    ),
                    Task(
                        title = "Изучить RecyclerView",
                        description = "Разобраться с созданием списка задач",
                        isDone = false,
                        date = "26.09.2026",
                        time = "18:00"
                    ),
                    Task(
                        title = "Сделать домашнее задание",
                        description = "Выполнить задания по Android Studio",
                        isDone = false,
                        date = "27.09.2026",
                        time = "18:00"
                    ),
                    Task(
                        title = "Подготовить презентацию",
                        description = "Подготовить материалы для защиты",
                        isDone = false,
                        date = "28.09.2026",
                        time = "18:00"
                    ),
                    Task(
                        title = "Повторить материал",
                        description = "Повторить основные понятия",
                        isDone = false,
                        date = "29.09.2026",
                        time = "18:00"
                    )
                )

                for (task in oldTasks) {
                    taskDao.insertTask(task)
                }
            }

            val updatedTasks = taskDao.getAllTasks()

            tasks.clear()
            tasks.addAll(updatedTasks)

            // Проверяем количество задач в базе
            android.util.Log.d(
                "ROOM_COUNT",
                "Количество задач в базе: ${updatedTasks.size}"
            )

            // Выводим все задачи в Logcat
            for (task in updatedTasks) {
                android.util.Log.d(
                    "ROOM_COUNT",
                    "ID: ${task.id}, название: ${task.title}"
                )
            }

            adapter.notifyDataSetChanged()
        }

        val addTaskButton =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
                R.id.addTaskButton
            )

        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, 200)
        }
    }

    private fun setupRecyclerView() {

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter(tasks) { task ->

            selectedTaskPosition = tasks.indexOf(task)

            val intent = Intent(this, TaskDetailActivity::class.java)

            intent.putExtra("title", task.title)
            intent.putExtra("description", task.description)
            intent.putExtra("date", task.date)
            intent.putExtra("completed", task.isDone)

            startActivityForResult(intent, 100)
        }

        recyclerView.adapter = adapter
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        // Изменение статуса задачи
        if (requestCode == 100 &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            val completed = data.getBooleanExtra("completed", false)

            if (completed && selectedTaskPosition != -1) {

                val oldTask = tasks[selectedTaskPosition]

                val updatedTask = oldTask.copy(
                    isDone = true
                )

                lifecycleScope.launch {

                    taskDao.updateTask(updatedTask)

                    tasks[selectedTaskPosition] = updatedTask

                    adapter.notifyItemChanged(selectedTaskPosition)
                }
            }
        }

        // Добавление новой задачи
        if (requestCode == 200 &&
            resultCode == RESULT_OK &&
            data != null
        ) {

            val title = data.getStringExtra("title") ?: ""
            val description = data.getStringExtra("description") ?: ""
            val date = data.getStringExtra("date") ?: ""

            val newTask = Task(
                title = title,
                description = description,
                isDone = false,
                date = date,
                time = "12:00"
            )

            lifecycleScope.launch {

                taskDao.insertTask(newTask)

                val updatedTasks = taskDao.getAllTasks()

                tasks.clear()
                tasks.addAll(updatedTasks)

                android.util.Log.d(
                    "ROOM_COUNT",
                    "После добавления задач в базе: ${updatedTasks.size}"
                )

                adapter.notifyDataSetChanged()
            }
        }
    }
}