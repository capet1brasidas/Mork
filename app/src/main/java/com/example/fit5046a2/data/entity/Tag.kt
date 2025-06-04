package com.example.fit5046a2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag_table")
data class Tag(
    @PrimaryKey(autoGenerate = true) val tagId: Int? = null,
    var name: String,
    var color: String,
) {
    companion object {
        val DefaultColors = listOf(
            "#FF0000", // Red
            "#00FF00", // Green
            "#0000FF", // Blue
            "#FFFF00", // Yellow
            "#FF00FF", // Magenta
            "#00FFFF"  // Cyan
        )
    }
}