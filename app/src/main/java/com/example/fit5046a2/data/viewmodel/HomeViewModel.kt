package com.example.fit5046a2.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import com.example.fit5046a2.data.entity.Project
import com.example.fit5046a2.data.entity.UserProject
import com.example.fit5046a2.data.entity.Task
import com.example.fit5046a2.data.entity.ToDo
import com.example.fit5046a2.data.repository.ProjectTaskRepository
import com.example.fit5046a2.data.repository.TaskRepository
import com.example.fit5046a2.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application)
    private val taskRepository = TaskRepository(application)
    private val projectTaskRepository = ProjectTaskRepository(application)

    // User data
    private val _currentUser = MutableStateFlow<com.example.fit5046a2.data.entity.User?>(null)
    val currentUser = _currentUser.asStateFlow()

    // Project data
    private val _userProjects = MutableStateFlow<List<Project>>(emptyList())
    val userProjects = _userProjects.asStateFlow()

// Project progress data
private val _projectProgress = MutableStateFlow<Map<Int, Float>>(emptyMap())
val projectProgress = _projectProgress.asStateFlow()

// Project task status percentages
private val _projectTaskStatusPercentages = MutableStateFlow<Map<Int, Map<String, Float>>>(emptyMap())
val projectTaskStatusPercentages = _projectTaskStatusPercentages.asStateFlow()

    // Recent task's todos
    private val _recentTaskTodos = MutableStateFlow<List<ToDo>>(emptyList())
    val recentTaskTodos = _recentTaskTodos.asStateFlow()

    // Recent task progress
    private val _recentTaskProgress = MutableStateFlow(0f)
    val recentTaskProgress = _recentTaskProgress.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            firebaseUser?.email?.let { email ->
                userRepository.getUserByEmail(email).collect { user ->
                    _currentUser.value = user
                    loadUserData(email)
                }
            }
        }
    }

    private fun loadUserData(email: String) {
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "Loading user data for email: $email")
            
            // Load projects through UserProject association
            projectTaskRepository.getProjectsByUser(email).collect { userProjectEntities ->
                android.util.Log.d("HomeViewModel", "Received ${userProjectEntities.size} user projects")
                
                // Convert UserProject entities to Project objects
                val projects = mutableListOf<Project>()
                
                for (userProject in userProjectEntities) {
                    // Fetch the actual Project entity for each UserProject
                    projectTaskRepository.getProjectById(userProject.projectId.toString())?.let { project ->
                        projects.add(project)
                    }
                }
                
                _userProjects.value = projects
                calculateProjectProgress(userProjectEntities)
            }
            
            //here should be take task from each project
            // Load tasks for the current project
            userProjects.value.firstOrNull()?.let { project ->
                project.projectId?.let { projectId ->
                    taskRepository.getTasksByProject(projectId).collect { tasks ->
                        android.util.Log.d("HomeViewModel", "Received ${tasks.size} tasks for project ${projectId}")
                        if (tasks.isNotEmpty()) {
                            // Get todos for all active tasks
                            val activeTasks = tasks.filter { it.status != "Done" }
                            android.util.Log.d("HomeViewModel", "Active tasks: ${activeTasks.size}")
                            if (activeTasks.isNotEmpty()) {
                                // Load todos for the first active task
                                val firstTask = activeTasks.first()
                                android.util.Log.d("HomeViewModel", "Loading todos for task: ${firstTask.title}")
                                loadRecentTaskTodos(firstTask.taskId ?: 0)
                            } else {
                                android.util.Log.d("HomeViewModel", "No active tasks found")
                                _recentTaskTodos.value = emptyList()
                            }
                        } else {
                            android.util.Log.d("HomeViewModel", "No tasks found for project")
                            _recentTaskTodos.value = emptyList()
                        }
                    } 
                }
            } ?: run {
                android.util.Log.d("HomeViewModel", "No projects found")
                _recentTaskTodos.value = emptyList()
            }
        }
    }

    private fun loadRecentTaskTodos(taskId: Int) {
        viewModelScope.launch {
            android.util.Log.d("HomeViewModel", "Loading todos for task: $taskId")
            taskRepository.getTodosForTask(taskId).collect { todos ->
                android.util.Log.d("HomeViewModel", "Received ${todos.size} todos")
                todos.forEach { todo ->
                    android.util.Log.d("HomeViewModel", "Todo: ${todo.title}, ID: ${todo.todoId}, Completed: ${todo.isCompleted}")
                }
                _recentTaskTodos.value = todos
                calculateRecentTaskProgress(todos)
            }
        }
    }

    private fun calculateRecentTaskProgress(todos: List<ToDo>) {
        if (todos.isEmpty()) {
            _recentTaskProgress.value = 0f
            return
        }
        val completedTodos = todos.count { it.isCompleted }
        _recentTaskProgress.value = completedTodos.toFloat() / todos.size
    }

    private fun calculateProjectProgress(userProjects: List<UserProject>) {
        // Keep using UserProject for calculating progress since we need projectId
        viewModelScope.launch {
            val progressMap = mutableMapOf<Int, Float>()
            val statusPercentagesMap = mutableMapOf<Int, Map<String, Float>>()
            
            userProjects.forEach { userProject ->
                // Get tasks for this project
                taskRepository.getTasksByProject(userProject.projectId).collect { tasks ->
                    if (tasks.isNotEmpty()) {
                        // Calculate overall progress (completed tasks)
                        val completedTasks = tasks.count { it.status == "Done" }
                        progressMap[userProject.projectId] = completedTasks.toFloat() / tasks.size
                        
                        // Calculate percentages for each status
                        val doneCount = tasks.count { it.status == "Done" }
                        val inProgressCount = tasks.count { it.status == "In Progress" }
                        val todoCount = tasks.count { it.status == "To Do" }
                        
                        val donePercentage = doneCount.toFloat() / tasks.size
                        val inProgressPercentage = inProgressCount.toFloat() / tasks.size
                        val todoPercentage = todoCount.toFloat() / tasks.size
                        
                        statusPercentagesMap[userProject.projectId] = mapOf(
                            "Done" to donePercentage,
                            "In Progress" to inProgressPercentage,
                            "To Do" to todoPercentage
                        )
                        
                        android.util.Log.d("HomeViewModel", "Project ${userProject.projectId} status percentages: " +
                                "Done=${donePercentage * 100}%, " +
                                "In Progress=${inProgressPercentage * 100}%, " +
                                "To Do=${todoPercentage * 100}%")
                    } else {
                        progressMap[userProject.projectId] = 0f
                        statusPercentagesMap[userProject.projectId] = mapOf(
                            "Done" to 0f,
                            "In Progress" to 0f,
                            "To Do" to 0f
                        )
                    }
                }
            }
            
            _projectProgress.value = progressMap
            _projectTaskStatusPercentages.value = statusPercentagesMap
        }
    }

    fun updateTodoStatus(todo: ToDo, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedTodo = todo.copy(isCompleted = isCompleted)
            taskRepository.updateToDo(updatedTodo)
        }
    }

    fun deleteTodo(todo: ToDo) {
        viewModelScope.launch {
            taskRepository.deleteToDo(todo)
        }
    }

    fun onProjectSelected(projectId: Int?) {
        viewModelScope.launch {
            if (projectId == null) return@launch
            android.util.Log.d("HomeViewModel", "Loading tasks for selected project: $projectId")
            taskRepository.getTasksByProject(projectId).collect { tasks ->
                android.util.Log.d("HomeViewModel", "Received ${tasks.size} tasks for project $projectId")
                if (tasks.isNotEmpty()) {
                    // Calculate status percentages for the selected project
                    val doneCount = tasks.count { it.status == "Done" }
                    val inProgressCount = tasks.count { it.status == "In Progress" }
                    val todoCount = tasks.count { it.status == "To Do" }
                    
                    val donePercentage = doneCount.toFloat() / tasks.size
                    val inProgressPercentage = inProgressCount.toFloat() / tasks.size
                    val todoPercentage = todoCount.toFloat() / tasks.size
                    
                    val currentStatusMap = _projectTaskStatusPercentages.value.toMutableMap()
                    currentStatusMap[projectId] = mapOf(
                        "Done" to donePercentage,
                        "In Progress" to inProgressPercentage,
                        "To Do" to todoPercentage
                    )
                    _projectTaskStatusPercentages.value = currentStatusMap
                    
                    android.util.Log.d("HomeViewModel", "Selected project $projectId status percentages: " +
                            "Done=${donePercentage * 100}%, " +
                            "In Progress=${inProgressPercentage * 100}%, " +
                            "To Do=${todoPercentage * 100}%")
                    
                    val activeTasks = tasks.filter { it.status != "Done" }
                    android.util.Log.d("HomeViewModel", "Active tasks: ${activeTasks.size}")
                    if (activeTasks.isNotEmpty()) {
                        val firstTask = activeTasks.first()
                        android.util.Log.d("HomeViewModel", "Loading todos for task: ${firstTask.title}")
                        loadRecentTaskTodos(firstTask.taskId ?: 0)
                    } else {
                        android.util.Log.d("HomeViewModel", "No active tasks found")
                        _recentTaskTodos.value = emptyList()
                    }
                } else {
                    android.util.Log.d("HomeViewModel", "No tasks found for project")
                    _recentTaskTodos.value = emptyList()
                    
                    val currentStatusMap = _projectTaskStatusPercentages.value.toMutableMap()
                    currentStatusMap[projectId] = mapOf(
                        "Done" to 0f,
                        "In Progress" to 0f,
                        "To Do" to 0f
                    )
                    _projectTaskStatusPercentages.value = currentStatusMap
                }
            }
        }
    }
}
