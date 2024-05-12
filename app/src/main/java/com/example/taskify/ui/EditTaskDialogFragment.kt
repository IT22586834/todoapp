

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.taskify.R
import com.example.taskify.data.Task
import java.util.Calendar

class EditTaskDialogFragment : DialogFragment() {

    private lateinit var task: Task
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var datePickerDueDate: DatePicker
    private lateinit var spinnerPriority: Spinner

    interface OnTaskEditedListener {
        fun onTaskEdited(task: Task)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_task_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        editTextTitle = view.findViewById(R.id.TextTitle)
        editTextDescription = view.findViewById(R.id.TextDescription)
        datePickerDueDate = view.findViewById(R.id.DueDate)
        spinnerPriority = view.findViewById(R.id.textspinnerPriority)


        populateViews()

        val buttonUpdate = view.findViewById<Button>(R.id.buttonUpdate)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)

        buttonUpdate.setOnClickListener {
            saveEditedTask()
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }
    }


    private fun populateViews() {
        editTextTitle.setText(task.title)
        editTextDescription.setText(task.description)

        val calendar = Calendar.getInstance()
        task.dueDate?.let {
            calendar.time = it
            datePickerDueDate.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ) { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
            }
        }

        val priorityArray = resources.getStringArray(R.array.priority_levels)
        val priorityIndex = priorityArray.indexOf(task.priority)
        spinnerPriority.setSelection(priorityIndex)
    }

    private fun saveEditedTask() {
        task.title = editTextTitle.text.toString()
        task.description = editTextDescription.text.toString()
        val calendar = Calendar.getInstance()
        calendar.set(datePickerDueDate.year, datePickerDueDate.month, datePickerDueDate.dayOfMonth)
        task.dueDate = calendar.time

        (activity as? OnTaskEditedListener)?.onTaskEdited(task)
    }

    companion object {
        fun newInstance(task: Task): EditTaskDialogFragment {
            val fragment = EditTaskDialogFragment()
            fragment.task = task
            return fragment
        }
    }
}
