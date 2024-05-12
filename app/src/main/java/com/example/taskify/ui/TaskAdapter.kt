import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.taskify.data.Task
import com.example.taskify.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(context: Context, tasks: MutableList<Task>) :
    ArrayAdapter<Task>(context, 0, tasks) {

    private var onDeleteClickListener: ((Task) -> Unit)? = null
    private var editClickListener: ((Task) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (Task) -> Unit) {
        onDeleteClickListener = listener
    }

    fun setOnEditClickListener(listener: (Task) -> Unit) {
        editClickListener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val binding: ItemTaskBinding

        if (view == null) {
            binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = view.tag as ItemTaskBinding
        }

        val task = getItem(position)

        binding.textTitle.text = task?.title
        binding.textDescription.text = task?.description
        binding.textDueDate.text = task?.dueDate?.let { formatDate(it) } ?: ""
        binding.textPriority.text = task?.priority

        // Set OnClickListener for the delete icon
        binding.imageDelete.setOnClickListener {
            val taskToDelete = getItem(position)
            if (taskToDelete != null) {
                onDeleteClickListener?.invoke(taskToDelete)
            }
        }

        // Set OnClickListener for the edit icon
        binding.imageEdit.setOnClickListener {
            val task = getItem(position)
            if (task != null) {
                editClickListener?.invoke(task)
            }
        }

        // Set OnClickListener for the "Mark Completed" button
        binding.buttonMarkCompleted.setOnClickListener {
            // Hide the button
            binding.buttonMarkCompleted.visibility = View.GONE
            // Perform other actions to mark the task as completed
        }

        return view
    }

    // Function to format the date as "Day Month Year"
    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun updateTasks(newTaskList: MutableList<Task>) {
        clear()
        addAll(newTaskList)
        notifyDataSetChanged()
    }
}
