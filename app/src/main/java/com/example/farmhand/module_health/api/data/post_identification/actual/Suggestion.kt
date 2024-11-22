package com.example.farmhand.module_health.api.data.post_identification.actual

data class Suggestion(
    val details: Details,
    val id: String,
    val name: String,
    val probability: Double,
    val scientific_name: String,
    val similar_images: List<SimilarImage>
)