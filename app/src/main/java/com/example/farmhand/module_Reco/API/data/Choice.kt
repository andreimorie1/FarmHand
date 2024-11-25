package com.example.farmhand.module_Reco.API.data

data class Choice(
    val message: Message,
    val index: Int,
    val logprobs: Any? = null,
    val finish_reason: String
)
