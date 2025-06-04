package com.example.fit5046a2.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fit5046a2.data.DAO.ProjectTaskDAO
import com.example.fit5046a2.data.DAO.TaskDAO
import com.example.fit5046a2.data.DAO.UserDAO
import com.example.fit5046a2.data.entity.*

@Database(
    entities = [
        User::class,
        Task::class,
        Project::class,
        Tag::class,
        ToDo::class,
        TaskTag::class,
        UserProject::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectTaskDAO(): ProjectTaskDAO
    abstract fun userDAO(): UserDAO
    abstract fun taskDAO(): TaskDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.d("AppDatabase", "Initializing database")
                try {
                    // Check if asset exists
                    context.assets.open("app_database.db").use { stream ->
                        Log.d("AppDatabase", "Found app_database.db in assets")
                    }
                } catch (e: Exception) {
                    Log.e("AppDatabase", "Failed to find app_database.db in assets: ${e.message}")
                }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .createFromAsset("app_database.db")
                .fallbackToDestructiveMigration()
                .build()
                
                Log.d("AppDatabase", "Database initialized successfully")
                INSTANCE = instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
