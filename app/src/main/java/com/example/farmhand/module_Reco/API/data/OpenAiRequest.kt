package com.example.farmhand.module_Reco.API.data

data class OpenAiRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<Message>,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.3
)

data class Message(
    val role: String,
    val content: String
)
