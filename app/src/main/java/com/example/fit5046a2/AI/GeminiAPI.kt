package com.example.fit5046a2.AI

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlin.collections.isNotEmpty
import kotlin.collections.takeLast

class GeminiAPI() {
    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-2.0-flash-001",
        apiKey = "AIzaSyBQ6I0NlLtN4ynqC5eFX2-CsDVg23h5-wY"
    )
    
    suspend fun generateResponse(
        prompt: String,
        userContext: UserContext
    ): String {
        Log.d("GeminiAPI", "Generating response for prompt: $prompt")
        Log.d("GeminiAPI", "Context: $userContext")
        
        return try {
            val fullPrompt = buildPrompt(prompt, userContext)
            Log.d("GeminiAPI", "Full prompt built: $fullPrompt")
            
            try {
                val response = generativeModel.generateContent(fullPrompt)
                
                when {
                    response == null -> {
                        Log.e("GeminiAPI", "Received null response")
                        "I apologize, but I couldn't generate a response at this time. Please try again."
                    }
                    response.text == null -> {
                        Log.e("GeminiAPI", "Response text is null")
                        "I apologize, but I received an invalid response. Please try again."
                    }
                    response.text.toString().isEmpty() -> {
                        Log.e("GeminiAPI", "Response text is empty")
                        "I apologize, but I received an empty response. Please try again."
                    }
                    else -> {
                        val responseText = response.text.toString()
                        Log.d("GeminiAPI", "Successfully received response: $responseText")
                        responseText
                    }
                }
            } catch (e: com.google.ai.client.generativeai.type.ServerException) {
                Log.e("GeminiAPI", "Server error occurred: ${e.message}", e)
                if (e.message?.contains("Unexpected Response") == true) {
                    "I apologize, but the AI service returned an unexpected response. This might be due to temporary issues. Please try again in a few moments."
                } else {
                    "Server error: The AI service is temporarily unavailable. Please try again in a few moments."
                }
            } catch (e: Exception) {
                Log.e("GeminiAPI", "Error during API call: ${e.message}", e)
                "An error occurred while communicating with the AI service: ${e.message}"
            }
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Unexpected error: ${e.message}", e)
            "An unexpected error occurred. Please try again later."
        }
    }
    
    private fun buildPrompt(
        userInput: String, 
        context: UserContext
    ): String {
        val promptBuilder = StringBuilder()
        
        // Add system context
        promptBuilder.append("You are Morki, an AI assistant in a task management app. ")
        promptBuilder.append("You should be friendly, encouraging, and helpful. ")
        promptBuilder.append("Current page: ${context.currentPage}\n\n")
        
        // Add graph data if available
        if (context.userCurrentGraph.isNotEmpty()) {
            promptBuilder.append("Current graph data:\n")
            context.userCurrentGraph.forEach { dataPoint ->
                promptBuilder.append("- $dataPoint\n")
            }
            promptBuilder.append("\n")
        }
        
        // Add user context
        if (context.userTasks.isNotEmpty()) {
            promptBuilder.append("User's current tasks:\n")
            context.userTasks.forEach { task ->
                promptBuilder.append("- $task\n")
            }
            promptBuilder.append("\n")
        }
        
        // Add performance context
        if (context.userPerformance.isNotEmpty()) {
            promptBuilder.append("User's performance metrics:\n")
            context.userPerformance.forEach { (metric, value) ->
                promptBuilder.append("- $metric: $value\n")
            }
            promptBuilder.append("\n")
        }
        
        // Add recent chat history for context
        if (context.chatHistory.isNotEmpty()) {
            promptBuilder.append("Recent conversation:\n")
            context.chatHistory.takeLast(5).forEach { message ->
                val role = if (message.sentByUser) "User" else "Assistant"
                promptBuilder.append("$role: ${message.text}\n")
            }
            promptBuilder.append("\n")
        }
        
        // Add current user input
        promptBuilder.append("User: $userInput\n")
        promptBuilder.append("Assistant: ")
        
        return promptBuilder.toString()
    }
}
