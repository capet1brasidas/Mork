package com.example.fit5046a2.data.DAO

import android.icu.text.MessagePattern.ArgType.SELECT
import android.webkit.WebSettings.PluginState.ON
import androidx.room.*
import com.example.fit5046a2.data.entity.*
import com.sun.mail.imap.SortTerm.FROM
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {

    // Task CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM task_table WHERE taskId = :taskId")
    fun getTaskById(taskId: Int): Flow<Task>

    @Query("SELECT * FROM task_table WHERE email = :email")
    fun getTasksByUser(email: String): Flow<List<Task>>

    @Query("""
        SELECT * FROM task_table 
        WHERE email = :email 
          AND completedAt IS NOT NULL
          AND completedAt <= strftime('%s', 'now') * 1000
          AND status = 'Done'
    """)
    suspend fun getCompletedTasksByUser(email: String): List<Task>

    // Completed tasks for today
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
""")
    fun getTodayCompletedTasks(): Flow<List<Task>>

    // Completed tasks for this year
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
""")
    fun getThisYearCompletedTasks(): Flow<List<Task>>

    // Completed tasks for this month
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
""")
    fun getThisMonthCompletedTasks(): Flow<List<Task>>

    // All completed tasks today (across all users)
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
""")
    suspend fun getAllTasksCompletedToday(): List<Task>

    // All completed tasks this month (across all users)
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
""")
    suspend fun getAllTasksCompletedThisMonth(): List<Task>

    // All completed tasks this year (across all users)
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
""")
    suspend fun getAllTasksCompletedThisYear(): List<Task>

    @Query("SELECT COUNT(*) FROM task_table")
    suspend fun getTaskCount(): Int

    @Query("SELECT * FROM task_table")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE projectId = :projectId")
    fun getTasksByProject(projectId: Int): Flow<List<Task>>

    // --- Tag operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskTag(taskTag: TaskTag): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("""SELECT tag_table.*
            FROM tag_table
            INNER JOIN task_tag_table ON tag_table.tagId = task_tag_table.tagId
            WHERE task_tag_table.taskId = :taskId;""")
    fun getTagsByTask(taskId : Int): Flow<List<Tag>>

    @Query("SELECT * FROM tag_table")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * FROM tag_table WHERE tagId = :tagId")
    fun getTagById(tagId: Int): Flow<Tag>

    // --- ToDo operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDo(todo: ToDo): Long

    @Update
    suspend fun updateToDo(todo: ToDo)

    @Delete
    suspend fun deleteToDo(todo: ToDo)

    @Query("SELECT * FROM todo_table WHERE taskId = :taskId ORDER BY todoId ASC")
    fun getTodosForTask(taskId: Int): Flow<List<ToDo>>

    @Query("SELECT COUNT(*) FROM todo_table WHERE taskId = :taskId")
    suspend fun getTodoCountForTask(taskId: Int): Int

    @Query("SELECT * FROM todo_table WHERE todoId = :todoId")
    fun getTodoById(todoId: Int): Flow<ToDo>

    // Completed tasks today by specific user
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
      AND email = :email
""")
    fun getTodayCompletedTasksByUser(email: String): Flow<List<Task>>

    // Completed tasks this month by specific user
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
      AND email = :email
""")
    fun getThisMonthCompletedTasksByUser(email: String): Flow<List<Task>>

    // Completed tasks this year by specific user
    @Query("""
    SELECT * FROM task_table
    WHERE completedAt IS NOT NULL
      AND completedAt <= strftime('%s', 'now') * 1000
      AND status = 'Done'
      AND email = :email
""")
    fun getThisYearCompletedTasksByUser(email: String): Flow<List<Task>>

}
