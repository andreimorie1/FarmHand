package com.example.farmhand.weather.utils


    // Get current season based on the timestamp
    fun getSeason(dt: Long): String {
        val month = java.util.Calendar.getInstance().apply { timeInMillis = dt * 1000 }.get(java.util.Calendar.MONTH) + 1
        return when (month) {
            in 5..11 -> "Rainy Season"  // May to November
            else -> "Dry/Sunny Season"    // December to April
        }
    }


