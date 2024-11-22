package com.example.farmhand.module_Reco.API.data

data class OpenAiResponse(
    val id: String,
    val objectType: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>
)
