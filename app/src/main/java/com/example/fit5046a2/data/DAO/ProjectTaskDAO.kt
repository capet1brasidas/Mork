package com.example.fit5046a2.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fit5046a2.data.entity.Project
import com.example.fit5046a2.data.entity.Task
import com.example.fit5046a2.data.entity.UserProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectTaskDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("SELECT * FROM project_table")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM project_table WHERE projectId = :id")
    suspend fun getProjectById(id: String): Project?


    // Get tasks by project ID
    @Query("SELECT * FROM task_table WHERE projectId = :projectId")
    fun getTasksByProject(projectId: Int): Flow<List<Task>>

    // --- UserProject operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProject(userProject: UserProject): Long

    @Delete
    suspend fun deleteUserProject(userProject: UserProject)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserProject(userProject: UserProject)

    @Query("SELECT * FROM user_project_table")
    fun getAllUserProjects(): Flow<List<UserProject>>

    @Query("SELECT * FROM user_project_table WHERE email = :email")
    fun getProjectsByUser(email: String): Flow<List<UserProject>>

    @Query("SELECT * FROM user_project_table WHERE projectId = :projectId")
    fun getUsersByProject(projectId: Int): Flow<List<UserProject>>
} 