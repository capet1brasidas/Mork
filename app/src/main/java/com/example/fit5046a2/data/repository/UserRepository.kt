package com.example.fit5046a2.data.repository

import android.app.Application
import com.example.fit5046a2.data.DAO.UserDAO
import com.example.fit5046a2.data.database.AppDatabase
import com.example.fit5046a2.data.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class UserRepository(application: Application) {
    private var userDAO: UserDAO = AppDatabase.getDatabase(application).userDAO()

    val allUsers: Flow<List<User>> = userDAO.getAllUsers()

    suspend fun insert(user: User) {
        userDAO.insert(user)
    }

    suspend fun update(user: User) {
        userDAO.updateUser(user)
    }

    suspend fun getCurrentUser(): User {
        // For now, return the first user as current user
        // TODO: Implement proper user session management
        return userDAO.getAllUsers().first().firstOrNull() ?: throw Exception("No user found")
    }

    fun getUserByEmail(email: String): Flow<User?> {
        return userDAO.getUserByEmail(email)
    }
}
