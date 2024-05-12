package com.example.taskify.data

import android.provider.BaseColumns

object TaskContract {

    object TaskEntry : BaseColumns {
        const val ID = BaseColumns._ID
        const val TABLE_NAME = "tasks"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DUE_DATE = "due_date"
        const val COLUMN_PRIORITY = "priority"
        const val COLUMN_COMPLETED = "completed"


    }
}

