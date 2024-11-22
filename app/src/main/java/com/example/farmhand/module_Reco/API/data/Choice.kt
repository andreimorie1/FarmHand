package com.example.farmhand.module_Reco.API.data

data class Choice(
    val text: String,
    val index: Int,
    val logprobs: Any?,
    val finish_reason: String
)
