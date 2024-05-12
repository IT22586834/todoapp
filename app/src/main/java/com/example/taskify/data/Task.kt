package com.example.taskify.data

import java.util.Date


class Task(
    val id: Long?,
    var title: String,
    var description: String,
    var dueDate: Date,
    val priority: String,
    var isCompleted: Boolean,

    )
enum class Priority {
    HIGH, MEDIUM, LOW
}

