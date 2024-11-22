package com.example.farmhand.module_health.api.data.post_identification.actual

data class IsPlant(
    val binary: Boolean,
    val probability: Double,
    val threshold: Double
)