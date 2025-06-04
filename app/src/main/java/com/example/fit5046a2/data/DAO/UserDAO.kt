package com.example.fit5046a2.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fit5046a2.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(user: User): Long

        @Query("SELECT * FROM user_table")
        fun getAllUsers(): Flow<List<User>>

        @Update
        suspend fun updateUser(user: User)

        @Query("SELECT * FROM user_table WHERE email = :email")
        fun getUserByEmail(email: String): Flow<User?>
        @Query("SELECT email FROM user_table")
        suspend fun getAllUserEmails(): List<String>

}
