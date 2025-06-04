package com.example.fit5046a2.AI

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.example.fit5046a2.data.repository.UserRepository
import com.example.fit5046a2.data.repository.TaskRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val geminiAPI = GeminiAPI()
    private val userRepository = UserRepository(application)
    private val taskRepository = TaskRepository(application)
    private val _aiMessage = MutableStateFlow("")
    val aiMessage = _aiMessage.asStateFlow()

    private val _aiMessages = mutableListOf<String>()
    private var currentMessageIndex = 0
    private var isHomePageActive = false

    private val encouragingMessages = listOf(
        "Keep up the great work!",
        "You're making excellent progress!",
        "Stay focused, you're doing amazing!",
        "Remember to take breaks and stay hydrated!",
        "Your dedication is inspiring!",
        "Small steps lead to big achievements!",
        "You've got this! Keep pushing forward!",
        "Every task completed is a step toward success!"
    )

    init {
        generateMessages()
        isHomePageActive = true
    }

    // Generate messages once at startup
    private fun generateMessages() {
        viewModelScope.launch {
            try {
                // Generate 10 messages at once
                val context = createUserContext()
                for (i in 1..10) {
                    val response = geminiAPI.generateResponse(
                        "Generate a short, funny, and encouraging message for the user(max 100 chars). Make it personal and engaging.",
                        context
                    )

                    if (response.isNotBlank()) {
                        _aiMessages.add(response)
                        Log.d("HomeViewModel", "Generated message: $response")
                    } else {
                        _aiMessages.add(encouragingMessages.random())
                        Log.d("HomeViewModel", "!Using default message")
                    }
                }

                // If we couldn't generate enough messages, fill with defaults
                while (_aiMessages.size < 10) {
                    _aiMessages.add(encouragingMessages.random())
                }

                // Start displaying if home page is active
                if (isHomePageActive) {
                    startMessageRotation()
                }
            } catch (e: Exception) {
                // Fill with default messages on error
                for (i in 1..10) {
                    _aiMessages.add(encouragingMessages.random())
                }
            }
        }
    }

    // Start rotating through messages
    fun startMessageRotation() {
        isHomePageActive = true
        viewModelScope.launch {
            while (isHomePageActive) {
                if (_aiMessages.isNotEmpty()) {
                    _aiMessage.value = _aiMessages[currentMessageIndex]
                    currentMessageIndex = (currentMessageIndex + 1) % _aiMessages.size
                }
                delay(10000) // 10 seconds
            }
        }
    }

    // Stop rotation when leaving page
    fun stopMessageRotation() {
        isHomePageActive = false
    }

    private suspend fun createUserContext(): UserContext {
        val user = userRepository.getCurrentUser()
        val tasks = taskRepository.getTasksByUser(user.email).first()

        return UserContext(
            currentPage = "Home",
            userName = user.first_name.toString(),
            userTasks = tasks.map { task -> "${task.title} (Status: ${task.status})" }
        )
    }


}
