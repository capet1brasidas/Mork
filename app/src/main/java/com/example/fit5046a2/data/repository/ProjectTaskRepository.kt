package com.example.fit5046a2.data.repository

import android.app.Application
import com.example.fit5046a2.data.DAO.ProjectTaskDAO
import com.example.fit5046a2.data.DAO.TaskDAO
import com.example.fit5046a2.data.database.AppDatabase
import com.example.fit5046a2.data.entity.Project
import com.example.fit5046a2.data.entity.Task
import com.example.fit5046a2.data.entity.UserProject
import kotlinx.coroutines.flow.Flow

class ProjectTaskRepository(application: Application) {
    private var projectTaskDAO: ProjectTaskDAO =
        AppDatabase.getDatabase(application).projectTaskDAO()

    // Projects
    suspend fun insertProject(project: Project): Long = projectTaskDAO.insertProject(project)
    suspend fun updateProject(project: Project) = projectTaskDAO.updateProject(project)
    suspend fun deleteProject(project: Project) = projectTaskDAO.deleteProject(project)
    fun getAllProjects(): Flow<List<Project>> = projectTaskDAO.getAllProjects()
    suspend fun getProjectById(id: String): Project? = projectTaskDAO.getProjectById(id)

    fun getTasksByProject(projectId: Int): Flow<List<Task>> = projectTaskDAO.getTasksByProject(projectId)

    // UserProject
    suspend fun insertUserProject(userProject: UserProject): Long = projectTaskDAO.insertUserProject(userProject)
    suspend fun updateUserProject(userProject: UserProject) = projectTaskDAO.updateUserProject(userProject)
    suspend fun deleteUserProject(userProject: UserProject) = projectTaskDAO.deleteUserProject(userProject)
    fun getAllUserProjects(): Flow<List<UserProject>> = projectTaskDAO.getAllUserProjects()
    fun getProjectsByUser(email: String): Flow<List<UserProject>> = projectTaskDAO.getProjectsByUser(email)
    fun getUsersByProject(projectId: Int): Flow<List<UserProject>> = projectTaskDAO.getUsersByProject(projectId)
}