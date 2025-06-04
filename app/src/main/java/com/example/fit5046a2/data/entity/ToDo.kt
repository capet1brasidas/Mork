package com.example.fit5046a2.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "todo_table",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("taskId")]
)
data class ToDo(
    @PrimaryKey(autoGenerate = true) val todoId: Int = 0,   // Non-nullable Int
    var title: String,
    var isCompleted: Boolean = false,
    var completedAt: Long? = null,
    var createdAt: Long,
    var taskId: Int,
)
