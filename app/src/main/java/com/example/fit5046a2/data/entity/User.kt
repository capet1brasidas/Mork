package com.example.fit5046a2.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    var email: String,
    var first_name: String?,
    var last_name: String?,
    var password: String?,
    var position: String?,
    var DOB: Long?,
    var image: String?,
    var joinDate: Long?,
){
    constructor() : this(
        first_name = null,
        last_name = null,
        email = "",
        password = "",
        DOB = null,
        position = "employee",
        image = null,
        joinDate = null
    )
}

