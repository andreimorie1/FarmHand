package com.example.farmhand.module_weather.utils


data class WeatherRecommendations(
    val farming: String,
    val farmerHealth: String,
    val pestManagement: String
)

fun multilineString(vararg lines: String): String {
    return lines.joinToString("\n")
}

fun getWeatherRecommendation(
    weatherMain: String?,
    temperature: Double?,
    humidity: Int?,
    windSpeed: Double?,
    rainfall: Double?,
    dt: Long?
): WeatherRecommendations {
    val season = dt?.let { getSeason(it) } ?: "Unknown Season"

    val farming: String
    val farmerHealth: String
    val pestManagement: String

    when (weatherMain) {
        "Clear" -> {
            when {
                temperature != null && temperature > 32 && humidity != null && humidity < 60 -> {
                    farming = multilineString(
                        "Irrigate regularly due to dry conditions.",
                        "Fertilizer application should be done early morning or late afternoon to avoid burning plants.",
                        "Consider drought-resistant rice varieties."
                    )
                    farmerHealth = multilineString(
                        "Stay hydrated and take breaks often.",
                        "Wear lightweight, light-colored clothing to reflect heat.",
                        "Use a hat and sunscreen to protect against sunburn."

                    )
                    pestManagement = multilineString(
                        "Check for signs of drought-tolerant pests like leafhoppers.",
                        "Use pesticides sparingly; avoid applying during peak sunlight to prevent evaporation."
                    )

                }

                temperature != null && temperature > 32 && humidity != null && humidity >= 60 -> {
                    farming = multilineString(
                        "• High heat and humidity can stress rice plants, monitor for wilting.",
                        "• Ensure proper irrigation and drainage to avoid water logging.",
                        "• Fertilizer application should be postponed until cooler conditions."
                    )
                    farmerHealth = multilineString(
                        "• Stay indoors during peak heat, and drink water regularly.",
                        "• Wear breathable, moisture-wicking clothing to stay comfortable.",
                    )
                    pestManagement = multilineString(
                        "• Check for fungal infections that thrive in hot and humid conditions.",
                        "• Monitor for pests that thrive in warm, moist environments such as snails and rice borers."
                    )
                }

                else -> {
                    farming = multilineString(
                        "• Continue regular irrigation.",
                        "• Monitor rice growth and apply fertilizer as required."
                    )
                    farmerHealth = multilineString(
                        "• Maintain hydration and wear protective clothing for sun exposure."
                    )
                    pestManagement = multilineString(
                        "• No significant pest risks under normal conditions."
                    )
                }
            }
        }

        "Clouds" -> {
            when {
                temperature != null && temperature > 30 -> {
                    farming = multilineString(
                        "• Cloudy conditions with high temperatures can still stress crops; monitor soil moisture closely.",
                        "• Consider adjusting fertilizer timing to cooler hours."
                    )
                    farmerHealth = multilineString(
                        "• Even in cloudy module_weather, stay hydrated and wear sun protection."
                    )
                    pestManagement = multilineString(
                        "• Check for pest infestations, as cloud cover can sometimes lead to increased humidity."
                    )
                }

                temperature != null && temperature < 20 -> {
                    farming = multilineString(
                        "• Cooler temperatures are favorable for rice crops; maintain regular irrigation."
                    )

                    farmerHealth = multilineString(
                        "• Wear additional layers if working in cooler conditions."

                    )
                    pestManagement = multilineString(
                        "• Pest risks are lower in cooler temperatures."
                    )
                }

                else -> {
                    farming = multilineString(
                        "• General care for crops during cloudy conditions."
                    )

                    farmerHealth = multilineString(
                        "• Stay comfortable and wear module_weather-appropriate clothing."
                    )
                    pestManagement = multilineString(
                        "• No significant pest risks under cloudy conditions."
                    )
                }
            }
        }

        "Rain" -> {
            when {
                rainfall != null && rainfall > 5 -> {
                    farming = multilineString(
                        "• Prepare for heavy rainfall, ensure drainage systems are clear.",
                        "• Avoid applying fertilizer immediately before or after heavy rain."
                    )
                    farmerHealth = multilineString(
                        "• Wear rain gear to avoid getting soaked.",
                        "• Stay cautious of slippery surfaces to avoid injuries."
                    )
                    pestManagement = multilineString(
                        "• Monitor for pests that thrive in wet conditions, such as snails and rice bugs.",
                        "• Check for fungal diseases caused by excess moisture."
                    )

                }

                rainfall != null && rainfall <= 5 -> {
                    farming = multilineString(
                        "• Light rain benefits rice crops, but avoid water logging."
                    )
                    farmerHealth = multilineString(
                        "• Dress appropriately for rain but ensure ventilation to avoid overheating."
                    )
                    pestManagement = multilineString(
                        "• Monitor fields for pests and fungi during mild rainfall."
                    )
                }

                else -> {
                    farming = multilineString(
                        "• Manage water levels appropriately during rainy days."
                    )
                    farmerHealth = multilineString(
                        "• Stay protected from rain but ensure proper ventilation to avoid discomfort."
                    )
                    pestManagement = multilineString(
                        "• Check for water-related pests such as snails and slugs."
                    )
                }
            }
        }

        "Thunderstorm" -> {
            when {
                windSpeed != null && windSpeed > 10 -> {
                    farming = multilineString(
                        "• Secure farming equipment to prevent wind damage.",
                        "• Delay outdoor activities until the storm passes."
                    )
                    farmerHealth = multilineString(
                        "• Stay indoors, avoid working in thunderstorms."
                    )
                    pestManagement = multilineString(
                        "• Check for crop damage from heavy rains or winds.",
                        "• Monitor for pests after storm-induced flooding."
                    )
                }

                else -> {
                    farming = multilineString(
                        "• Stormy conditions can lead to flooding; monitor drainage systems."
                    )

                    farmerHealth = multilineString(
                        "• Stay indoors and avoid lightning risk."
                    )
                    pestManagement = multilineString(
                        "• Increased chance of pest infestations after storms."
                    )
                }
            }
        }

        "Drizzle" -> {
            farming = multilineString(
                "• Light rain helps in rice growth, but monitor water levels."
            )
            farmerHealth = multilineString(
                "• Wear a light jacket or rain gear to stay dry."
            )
            pestManagement = multilineString(
                "• Mild increase in pest activity, but generally low threat."
            )
        }

        "Mist", "Haze", "Fog" -> {
            farming = multilineString(
                "• No significant impact on farming, but monitor visibility for safe operation of machinery."
            )
            farmerHealth = multilineString(
                "• Wear a mask or protective gear to avoid inhaling mist or haze."
            )
            pestManagement = multilineString(
                "• Pests generally inactive during these conditions."
            )
        }
        // Add more conditions for other module_weather types (Thunderstorm, Mist, Haze, etc.)
        else -> {
            farming = multilineString(
                "• General farming advice applicable for unknown conditions."
            )
            farmerHealth = multilineString(
                "• General health advice for unknown module_weather conditions."
            )
            pestManagement = multilineString(
                "• General pest management advice applicable for unknown conditions."
            )
        }
    }

    // Return the recommendations as a WeatherRecommendations object
    return WeatherRecommendations(farming, farmerHealth, pestManagement)
}
