package com.example.fit5046a2.data.repository

import android.app.Application
import com.example.fit5046a2.data.DAO.TaskDAO
import com.example.fit5046a2.data.database.AppDatabase
import com.example.fit5046a2.data.entity.Tag
import com.example.fit5046a2.data.entity.Task
import com.example.fit5046a2.data.entity.TaskTag
import com.example.fit5046a2.data.entity.ToDo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskRepository(application: Application) {

    private val taskDao: TaskDAO = AppDatabase.getDatabase(application).taskDAO()

    // --- Task Operations ---
    fun getTaskById(taskId: Int): Flow<Task> = taskDao.getTaskById(taskId)
    fun getTasksByUser(email: String): Flow<List<Task>> {
        android.util.Log.d("TaskRepository", "Getting tasks for user: $email")
        kotlinx.coroutines.GlobalScope.launch {
            val totalTasks = taskDao.getTaskCount()
            android.util.Log.d("TaskRepository", "Total tasks in database: $totalTasks")
        }
        return taskDao.getTasksByUser(email).also { flow ->
            kotlinx.coroutines.GlobalScope.launch {
                flow.collect { tasks ->
                    android.util.Log.d("TaskRepository", "Found ${tasks.size} tasks for user $email")
                    if (tasks.isEmpty()) {
                        android.util.Log.d("TaskRepository", "No tasks found for user $email")
                    } else {
                        tasks.forEach { task ->
                            android.util.Log.d("TaskRepository", "Task: ${task.title}, Status: ${task.status}, ProjectId: ${task.projectId}")
                        }
                    }
                }
            }
        }
    }
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    fun getTasksByProject(projectId: Int): Flow<List<Task>> = taskDao.getTasksByProject(projectId)

    // New queries for completed tasks by time frame
    fun getTodayCompletedTasks(): Flow<List<Task>> = taskDao.getTodayCompletedTasks()
    fun getThisYearCompletedTasks(): Flow<List<Task>> = taskDao.getThisYearCompletedTasks()
    fun getThisMonthCompletedTasks(): Flow<List<Task>> = taskDao.getThisMonthCompletedTasks()

    fun getTodayCompletedTasksByUser(email: String): Flow<List<Task>> {
        android.util.Log.d("TaskRepository", "Getting today's completed tasks for user: $email")
        return taskDao.getTodayCompletedTasksByUser(email)
    }

    fun getThisMonthCompletedTasksByUser(email: String): Flow<List<Task>> =
        taskDao.getThisMonthCompletedTasksByUser(email)

    fun getThisYearCompletedTasksByUser(email: String): Flow<List<Task>> =
        taskDao.getThisYearCompletedTasksByUser(email)
    
    suspend fun insertTask(task: Task): Long {
        val result = taskDao.insertTask(task)
        android.util.Log.d("TaskRepository", "Task inserted with ID: $result, Title: ${task.title}")
        return result
    }
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
        android.util.Log.d("TaskRepository", "Task updated - ID: ${task.taskId}, Title: ${task.title}")
    }
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
        android.util.Log.d("TaskRepository", "Task deleted - ID: ${task.taskId}, Title: ${task.title}")
    }

    // --- Tag Operations ---
    fun getAllTags(): Flow<List<Tag>> = taskDao.getAllTags()
    fun getTagById(tagId: Int): Flow<Tag> = taskDao.getTagById(tagId)
    fun getTagsByTaskId(taskId: Int): Flow<List<Tag>> = taskDao.getTagsByTask(taskId)

    suspend fun insertTaskTag(taskTag: TaskTag): Long = taskDao.insertTaskTag(taskTag)

    suspend fun insertTag(tag: Tag): Long = taskDao.insertTag(tag)
    suspend fun updateTag(tag: Tag) = taskDao.updateTag(tag)
    suspend fun deleteTag(tag: Tag) = taskDao.deleteTag(tag)

    // --- ToDo Operations ---
    fun getTodosForTask(taskId: Int): Flow<List<ToDo>> {
        android.util.Log.d("TaskRepository", "Getting todos for task: $taskId")
        kotlinx.coroutines.GlobalScope.launch {
            val todoCount = taskDao.getTodoCountForTask(taskId)
            android.util.Log.d("TaskRepository", "Total todos in database for task $taskId: $todoCount")
        }
        return taskDao.getTodosForTask(taskId).also { flow ->
            kotlinx.coroutines.GlobalScope.launch {
                flow.collect { todos ->
                    android.util.Log.d("TaskRepository", "Found ${todos.size} todos for task $taskId")
                    if (todos.isEmpty()) {
                        android.util.Log.d("TaskRepository", "No todos found for task $taskId")
                    } else {
                        todos.forEach { todo ->
                            android.util.Log.d("TaskRepository", "Todo: ${todo.title}, ID: ${todo.todoId}, Completed: ${todo.isCompleted}")
                        }
                    }
                }
            }
        }
    }
    fun getTodoById(todoId: Int): Flow<ToDo> = taskDao.getTodoById(todoId)

    suspend fun insertToDo(todo: ToDo): Long = taskDao.insertToDo(todo)
    suspend fun updateToDo(todo: ToDo) = taskDao.updateToDo(todo)
    suspend fun deleteToDo(todo: ToDo) = taskDao.deleteToDo(todo)
}
