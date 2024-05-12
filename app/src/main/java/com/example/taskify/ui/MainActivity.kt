package com.example.taskify.ui

import DatabaseHelper
import EditTaskDialogFragment
import TaskAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskify.R
import com.example.taskify.data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() , EditTaskDialogFragment.OnTaskEditedListener{
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        taskAdapter = TaskAdapter(this, dbHelper.getAllTasks())
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = taskAdapter

        // Set onClickListener for the delete icon in TaskAdapter
        taskAdapter.setOnDeleteClickListener { task ->
            // Delete the task from the database
            dbHelper.deleteTask(task)
            // Refresh the ListView by updating the task list in the adapter
            taskAdapter.updateTasks(dbHelper.getAllTasks())
            // Notify the user that the task was deleted successfully
            Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
        }

        // Set onClickListener for the edit icon in TaskAdapter
        taskAdapter.setOnEditClickListener { task ->
            val fragmentManager = supportFragmentManager
            val fragment = EditTaskDialogFragment.newInstance(task)
            fragment.show(fragmentManager, "EditTaskDialog")
        }

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            createTask()
        }

        val datePicker = findViewById<DatePicker>(R.id.datePickerDueDate)
        val calendar = Calendar.getInstance()
        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, dayOfMonth ->
            // This is for updating the EditText with the selected date
            Log.d("MainActivity", "Selected date: $year-$month-$dayOfMonth")
            val selectedDate = Date(year - 1900, month, dayOfMonth)
            val formattedDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
            Log.d("MainActivity", "Formatted date: $formattedDate")
            findViewById<TextView>(R.id.editTextDueDate).text = formattedDate
        }

    }

    override fun onTaskEdited(task: Task) {
        // Update the edited task in the database
        dbHelper.updateTask(task)

        // Update the ListView
        taskAdapter.updateTasks(dbHelper.getAllTasks())

        // Notify the user that the task was edited successfully
        Toast.makeText(this, "Task edited successfully", Toast.LENGTH_SHORT).show()
    }


    private fun createTask() {
        // Retrieve task details from UI components
        val titleEditText = findViewById<EditText>(R.id.editTextTitle)
        val descriptionEditText = findViewById<EditText>(R.id.editTextDescription)
        val dueDateTextView = findViewById<TextView>(R.id.editTextDueDate)
        val prioritySpinner = findViewById<Spinner>(R.id.editTextPriority)

        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val priority = prioritySpinner.selectedItem.toString()
        val isCompleted = false // Default value for a new task

        // Get the due date from the TextView and parse it to a Date object
        val dueDateString = dueDateTextView.text.toString()

        if (title.isBlank() || description.isBlank() || dueDateString.isBlank()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (dueDateString.isNotBlank()) {
            val dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dueDateString)

            // Validate if the due date is in the future
            val currentDate = Calendar.getInstance().time
            if (dueDate.before(currentDate)) {
                Toast.makeText(this, "Due date must be in the future", Toast.LENGTH_SHORT).show()
                return
            }

            // Create a new Task object
            val newTask = Task(
                id = null,
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority,
                isCompleted = isCompleted
            )

            // Add the new task to the database
            dbHelper.addTask(newTask)

            // Clear the EditText and Spinner fields
            titleEditText.text.clear()
            descriptionEditText.text.clear()
            dueDateTextView.text = ""
            prioritySpinner.setSelection(0)

            // Update the ListView
            taskAdapter.updateTasks(dbHelper.getAllTasks())

            // Notify the user that the task was added successfully
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
        } else {
            // Handle the case when the due date is empty
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show()
        }
    }





}
