package com.example.farmhand.module_health.api.data.post_identification.actual

data class SimilarImageX(
    val citation: String,
    val id: String,
    val license_name: String,
    val license_url: String,
    val similarity: Double,
    val url: String,
    val url_small: String
)