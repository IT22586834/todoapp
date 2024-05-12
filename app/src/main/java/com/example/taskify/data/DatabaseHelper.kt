import android.app.DownloadManager.COLUMN_ID
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.taskify.data.Task
import com.example.taskify.data.TaskContract.TaskEntry
import com.example.taskify.data.TaskContract.TaskEntry.COLUMN_COMPLETED
import com.example.taskify.data.TaskContract.TaskEntry.COLUMN_DESCRIPTION
import com.example.taskify.data.TaskContract.TaskEntry.COLUMN_DUE_DATE
import com.example.taskify.data.TaskContract.TaskEntry.COLUMN_PRIORITY
import com.example.taskify.data.TaskContract.TaskEntry.COLUMN_TITLE
import com.example.taskify.data.TaskContract.TaskEntry.TABLE_NAME
import java.util.*

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_TASKS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${TaskEntry.TABLE_NAME}")
        onCreate(db)
    }

    fun addTask(task: Task) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(TaskEntry.COLUMN_TITLE, task.title)
            put(TaskEntry.COLUMN_DESCRIPTION, task.description)
            put(TaskEntry.COLUMN_DUE_DATE, task.dueDate?.time) // Store due date as milliseconds
            put(TaskEntry.COLUMN_PRIORITY, task.priority)
            put(TaskEntry.COLUMN_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        db.insert(TaskEntry.TABLE_NAME, null, values)
        db.close()
    }

    fun getAllTasks(): MutableList<Task> {
        val tasks = mutableListOf<Task>()
        val selectQuery =
            "SELECT * FROM ${TaskEntry.TABLE_NAME} ORDER BY ${TaskEntry.COLUMN_DUE_DATE}"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getLong(it.getColumnIndex(TaskEntry.ID))
                    val title = it.getString(it.getColumnIndex(TaskEntry.COLUMN_TITLE))
                    val description = it.getString(it.getColumnIndex(TaskEntry.COLUMN_DESCRIPTION))
                    val dueDateMillis = it.getLong(it.getColumnIndex(TaskEntry.COLUMN_DUE_DATE))
                    val dueDate = if (dueDateMillis > 0) Date(dueDateMillis) else Date()
                    val priority = it.getString(it.getColumnIndex(TaskEntry.COLUMN_PRIORITY))
                    val isCompleted = it.getInt(it.getColumnIndex(TaskEntry.COLUMN_COMPLETED)) == 1

                    val task = Task(id, title, description, dueDate!!, priority, isCompleted)
                    tasks.add(task)
                } while (it.moveToNext())
            }
        }
        cursor?.close()
        db.close()
        return tasks
    }




    fun updateTask(task: Task) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_DUE_DATE, task.dueDate?.time ?: 0)
            put(COLUMN_PRIORITY, task.priority)
            put(COLUMN_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        // Update the row with the given task id
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
        db.close()
    }


    fun deleteTask(task: Task) {
        val db = this.writableDatabase
        db.delete(
            TaskEntry.TABLE_NAME,
            "${TaskEntry.ID}=?",
            arrayOf(task.id.toString())
        )
        db.close()
    }


    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TaskletDB"

        private const val CREATE_TABLE_TASKS = ("CREATE TABLE ${TaskEntry.TABLE_NAME} "
                + "(${TaskEntry.ID} INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "${TaskEntry.COLUMN_TITLE} TEXT, "
                + "${TaskEntry.COLUMN_DESCRIPTION} TEXT, "
                + "${TaskEntry.COLUMN_DUE_DATE} INTEGER, " // Storing as milliseconds
                + "${TaskEntry.COLUMN_PRIORITY} TEXT, "
                + "${TaskEntry.COLUMN_COMPLETED} INTEGER)")
    }
}
