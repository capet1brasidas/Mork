package com.example.fit5046a2.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit5046a2.data.entity.Project
import com.example.fit5046a2.data.entity.UserProject
import com.example.fit5046a2.data.repository.ProjectTaskRepository
import com.example.fit5046a2.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ProjectTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectTaskRepository = ProjectTaskRepository(application)
    // Projects
    fun getAllProjects(): Flow<List<Project>> = repository.getAllProjects()
    suspend fun getProjectById(id: String): Flow<Project?> = repository.getProjectById(id).let { flowOf(it) }

    fun insertProject(project: Project) = viewModelScope.launch {
        repository.insertProject(project)
    }

    fun updateProject(project: Project) = viewModelScope.launch {
        repository.updateProject(project)
    }

    fun deleteProject(project: Project) = viewModelScope.launch {
        repository.deleteProject(project)
    }


    // UserProject
    fun getTasksByProject(projectId: Int): Flow<List<UserProject>> = repository.getAllUserProjects()
    fun getAllUserProjects(): Flow<List<UserProject>> = repository.getAllUserProjects()
    fun getProjectsByUser(email: String): Flow<List<UserProject>> = repository.getProjectsByUser(email)
    fun getUsersByProject(projectId: Int): Flow<List<UserProject>> = repository.getUsersByProject(projectId)

    fun insertUserProject(userProject: UserProject) = viewModelScope.launch {
        repository.insertUserProject(userProject)
    }

    fun updateUserProject(userProject: UserProject) = viewModelScope.launch {
        repository.updateUserProject(userProject)
    }

    fun deleteUserProject(userProject: UserProject) = viewModelScope.launch {
        repository.deleteUserProject(userProject)
    }
}