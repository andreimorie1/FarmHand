package com.example.farmhand.module_health.api.data.post_identification.actual

data class Result(
    val crop: Crop,
    val disease: Disease,
    val is_plant: IsPlant
)