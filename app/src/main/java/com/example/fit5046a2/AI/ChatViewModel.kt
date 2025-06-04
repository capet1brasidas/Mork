package com.example.fit5046a2.AI


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.fit5046a2.data.repository.UserRepository
import com.example.fit5046a2.data.repository.TaskRepository
import com.example.fit5046a2.AI.ChatMessage
import com.example.fit5046a2.AI.ChatState
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlin.collections.mapIndexed


class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val geminiAPI = GeminiAPI()
    private val userRepository = UserRepository(application)
    private val taskRepository = TaskRepository(application)
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private var currentContext = UserContext()

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                // Update state to show loading
                _chatState.value = _chatState.value.copy(
                    isLoading = true,
                    currentInput = message
                )

                // Create user message
                val userMessage = ChatMessage(
                    sender = "User",
                    to = "Morki",
                    text = message,
                    sentByUser = true
                )

                // Add user message to chat
                val updatedMessages = _chatState.value.messages + userMessage
                _chatState.value = _chatState.value.copy(
                    messages = updatedMessages,
                    currentInput = ""
                )

                // Update context with new message
                currentContext = currentContext.copy(
                    chatHistory = updatedMessages
                )

                // Get AI response with current context
                val response = geminiAPI.generateResponse(message, currentContext)

                // Create AI message
                val aiMessage = ChatMessage(
                    sender = "Morki",
                    to = "User",
                    text = response,
                    sentByUser = false
                )

                // Update chat with AI response
                _chatState.value = _chatState.value.copy(
                    messages = updatedMessages + aiMessage,
                    isLoading = false,
                    error = null
                )

            } catch (e: Exception) {
                _chatState.value = _chatState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun loadChatHistory() {
        viewModelScope.launch {
            try {
                _chatState.value = ChatState()

                // Load user data
                val user = userRepository.getCurrentUser()
                val tasks = taskRepository.getTasksByUser(user.email).first()

                // Update context with user data
                currentContext = currentContext.copy(
                    userName = user.first_name.toString(),
                    userTasks = tasks.map { task -> "${task.title} (Status: ${task.status})" }
//                    userPerformance = mapOf(
//                        "task_completion_rate" to user.taskStats?.toFloatOrNull() ?: 0f
//                    )
                )

            } catch (e: Exception) {
                _chatState.value = _chatState.value.copy(
                    error = "Error loading chat history: ${e.message}"
                )
            }
        }
    }

    fun updateContext(page: String) {
        // Store the previous page before updating
        val previousPage = currentContext.currentPage
        
        // Update the current page and previous page
        currentContext = currentContext.copy(
            currentPage = page,
            previousPage = if (page != previousPage) previousPage else currentContext.previousPage
        )
        
        // Clear graph data when not in chat or analysis pages
        if (page != "AI Chat" && page != "analysis") {
            // Clear graph context when leaving chat or analysis
            currentContext = currentContext.copy(
                userCurrentGraph = emptyList(),
                graphDescription = "",
                graphRecommendation = ""
            )
        }
        
        println("DEBUG: Updated context - Current: $page, Previous: ${currentContext.previousPage}")
    }

    fun updateAnalysisContext(barEntries: List<BarEntry>, xAxisLabels: List<String>, analysisType: String) {
        viewModelScope.launch {
            try {
                // Convert bar entries to string format
                val graphData = barEntries.mapIndexed { index, entry ->
                    "${xAxisLabels.getOrNull(index) ?: "Label$index"}: ${entry.y}"
                }
                
                // Update context with graph data
                currentContext = currentContext.copy(
                    userCurrentGraph = graphData
                )
                
                // Add system message about analysis context
                val systemMessage = ChatMessage(
                    sender = "System",
                    to = "User",
                    text = "Analysis data updated. You can ask questions about the $analysisType chart.",
                    sentByUser = false
                )
                
                _chatState.value = _chatState.value.copy(
                    messages = _chatState.value.messages + systemMessage
                )
                
                println("DEBUG: Updated analysis context with ${graphData.size} data points")
            } catch (e: Exception) {
                _chatState.value = _chatState.value.copy(
                    error = "Error updating analysis context: ${e.message}"
                )
            }
        }
    }
    
    fun updateAnalysisContent(description: String, recommendation: String) {
        // Update context with analysis content
        currentContext = currentContext.copy(
            graphDescription = description,
            graphRecommendation = recommendation
        )
        
        println("DEBUG: Updated analysis content - Description: ${description.take(20)}..., Recommendation: ${recommendation.take(20)}...")
    }
    
    // Get analysis content from context
    fun getAnalysisContent(): Pair<String, String> {
        return Pair(currentContext.graphDescription, currentContext.graphRecommendation)
    }
    
    // Check if coming from analysis page
    fun isComingFromAnalysis(): Boolean {
        return currentContext.previousPage == "analysis"
    }
}
