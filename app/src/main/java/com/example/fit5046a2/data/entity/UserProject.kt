package com.example.fit5046a2.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    tableName = "user_project_table",
    primaryKeys = ["projectId", "email"],
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["projectId"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserProject(
    var projectId: Int,
    var email: String
)