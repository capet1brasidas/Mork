package com.example.fit5046a2.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_table",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["projectId"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("projectId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int? = null,
    var title: String,
    var description: String? = null,
    var startDate: Long,
    var endDate: Long,
    var createdAt: Long,
    var completedAt: Long? = null,
    var status: String,
    var projectId: Int,  // Changed back to non-nullable Int
    var email: String
) {
    companion object {
        val Status = listOf("To Do", "In Progress", "Done")
        val Priority = listOf("Low", "Medium", "High")
    }
}
