package com.example.farmhand.module_health.api.data.post_identification.actual

data class SimilarImage(
    val id: String,
    val similarity: Double,
    val url: String,
    val url_small: String
)