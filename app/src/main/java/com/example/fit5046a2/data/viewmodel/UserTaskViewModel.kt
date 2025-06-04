package com.example.fit5046a2.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fit5046a2.data.entity.Tag
import com.example.fit5046a2.data.entity.Task
import com.example.fit5046a2.data.entity.TaskTag
import com.example.fit5046a2.data.entity.ToDo
import com.example.fit5046a2.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository = TaskRepository(application)

    // --- Task Operations ---
    fun getTaskById(taskId: Int): Flow<Task> = repository.getTaskById(taskId)
    fun getTasksByUser(email: String): Flow<List<Task>> = repository.getTasksByUser(email)
    fun getAllTasks(): Flow<List<Task>> = repository.getAllTasks()
    fun getTasksByProject(projectId: Int): Flow<List<Task>> = repository.getTasksByProject(projectId)

    // --- Completed tasks LiveData ---
    val _dailyCompletedTasks = MutableLiveData<List<Task>>()
    val dailyCompletedTasks: LiveData<List<Task>> = _dailyCompletedTasks

    val _yearlyCompletedTasks = MutableLiveData<List<Task>>()
    val yearlyCompletedTasks: LiveData<List<Task>> = _yearlyCompletedTasks

    val _monthlyCompletedTasks = MutableLiveData<List<Task>>()
    val monthlyCompletedTasks: LiveData<List<Task>> = _monthlyCompletedTasks

    fun loadDailyCompletedTasks() = viewModelScope.launch(Dispatchers.IO) {
        repository.getTodayCompletedTasks().collect { tasks ->
            _dailyCompletedTasks.postValue(tasks)
        }
    }

    fun loadYearlyCompletedTasks() = viewModelScope.launch(Dispatchers.IO) {
        repository.getThisYearCompletedTasks().collect { tasks ->
            _yearlyCompletedTasks.postValue(tasks)
        }
    }

    fun loadMonthlyCompletedTasks() = viewModelScope.launch(Dispatchers.IO) {
        repository.getThisMonthCompletedTasks().collect { tasks ->
            _monthlyCompletedTasks.postValue(tasks)
        }
    }


    fun insertTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTask(task)
    }

    // --- Tag Operations ---
    fun getAllTags(): Flow<List<Tag>> = repository.getAllTags()
    fun getTagById(tagId: Int): Flow<Tag> = repository.getTagById(tagId)

    fun insertTaskTag(taskTag: TaskTag) = viewModelScope.launch(Dispatchers.IO){
        repository.insertTaskTag(taskTag)
    }

    fun insertTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTag(tag)
    }

    fun updateTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTag(tag)
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTag(tag)
    }

    fun getTagsByTaskId(taskId: Int):Flow<List<Tag>> = repository.getTagsByTaskId(taskId)

    // --- ToDo Operations ---
    fun getTodosForTask(taskId: Int): Flow<List<ToDo>> = repository.getTodosForTask(taskId)
    fun getTodoById(todoId: Int): Flow<ToDo> = repository.getTodoById(todoId)

    fun insertToDo(todo: ToDo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertToDo(todo)
    }

    fun updateToDo(todo: ToDo) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateToDo(todo)
    }

    fun deleteToDo(todo: ToDo) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteToDo(todo)
    }

    val _selectedUserDailyTasks = MutableLiveData<List<Task>>()
    val selectedUserDailyTasks: LiveData<List<Task>> = _selectedUserDailyTasks

    val _selectedUserMonthlyTasks = MutableLiveData<List<Task>>()
    val selectedUserMonthlyTasks: LiveData<List<Task>> = _selectedUserMonthlyTasks

    val _selectedUserYearlyTasks = MutableLiveData<List<Task>>()
    val selectedUserYearlyTasks: LiveData<List<Task>> = _selectedUserYearlyTasks

    fun loadDailyCompletedTasksForUser(email: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.getTodayCompletedTasksByUser(email).collect {
            _selectedUserDailyTasks.postValue(it)
        }
    }

    fun loadMonthlyCompletedTasksForUser(email: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.getThisMonthCompletedTasksByUser(email).collect {
            _selectedUserMonthlyTasks.postValue(it)
        }
    }

    fun loadYearlyCompletedTasksForUser(email: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.getThisYearCompletedTasksByUser(email).collect {
            _selectedUserYearlyTasks.postValue(it)
        }
    }

}

