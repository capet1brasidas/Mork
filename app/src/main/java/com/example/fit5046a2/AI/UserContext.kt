package com.example.fit5046a2.AI

data class UserContext(
    val currentPage: String = "",
    val previousPage: String = "", // Track previous page for navigation history
    val userName: String = "",
    val userTasks: List<String> = emptyList(),
    val userPerformance: Map<String, Float> = emptyMap(),
    val chatHistory: List<ChatMessage> = emptyList(),
    val userCurrentGraph: List<String> = emptyList(), // Store BarEntry data as strings
    val graphDescription: String = "", // Store graph description from analysis
    val graphRecommendation: String = "" // Store graph recommendation from analysis
)
