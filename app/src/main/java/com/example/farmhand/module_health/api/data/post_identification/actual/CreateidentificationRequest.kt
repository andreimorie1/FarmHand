package com.example.farmhand.module_health.api.data.post_identification.actual

data class CreateidentificationRequest(
    val images: List<String>,
    val latitude: Double,
    val longitude: Double,
    val similar_images: Boolean
)