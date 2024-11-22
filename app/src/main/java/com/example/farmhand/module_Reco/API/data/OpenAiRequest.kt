package com.example.farmhand.module_Reco.API.data

data class OpenAiRequest(
    val model: String = "gpt-4o-mini",
    val prompt: String,
    val max_tokens: Int = 180,
    val temperature: Double = 0.3
)
