package com.example.fit5046a2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_table")
data class Project(
    @PrimaryKey(autoGenerate = true) val projectId: Int? = null,
    var name: String,
    var startDate: Long,
    var endDate: Long,
    var createdAt: Long,
    var status: String,
    var createdBy: String // references User.email
) {
    companion object {
        val Status = listOf("Planning", "Active", "On Hold", "Completed", "Cancelled")
    }
}
