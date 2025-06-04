package com.example.fit5046a2.AI

data class ChatMessage(
    val sender: String,
    val to: String,
    val text: String,
    val sentByUser: Boolean
)
