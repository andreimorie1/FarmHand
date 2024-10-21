package com.example.farmhand.weather.utils


data class WeatherRecommendations(
    val farming: String,
    val farmerHealth: String,
    val pestManagement: String
)

fun getWeatherRecommendation(
    weatherMain: String?,
    temperature: Double?,
    humidity: Double?,
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
                    farming = """
                    - Irrigate regularly due to dry conditions.
                    - Fertilizer application should be done early morning or late afternoon to avoid burning plants.
                    - Consider drought-resistant rice varieties.
                    """
                    farmerHealth = """
                    - Stay hydrated and take breaks often.
                    - Wear lightweight, light-colored clothing to reflect heat.
                    - Use a hat and sunscreen to protect against sunburn.
                    """
                    pestManagement = """
                    - Check for signs of drought-tolerant pests like leafhoppers.
                    - Use pesticides sparingly; avoid applying during peak sunlight to prevent evaporation.
                    """
                }
                temperature != null && temperature > 32 && humidity != null && humidity >= 60 -> {
                    farming = """
                    - High heat and humidity can stress rice plants, monitor for wilting.
                    - Ensure proper irrigation and drainage to avoid waterlogging.
                    - Fertilizer application should be postponed until cooler conditions.
                    """
                    farmerHealth = """
                    - Stay indoors during peak heat, and drink water regularly.
                    - Wear breathable, moisture-wicking clothing to stay comfortable.
                    """
                    pestManagement = """
                    - Check for fungal infections that thrive in hot and humid conditions.
                    - Monitor for pests that thrive in warm, moist environments such as snails and rice borers.
                    """
                }
                else -> {
                    farming = """
                    - Continue regular irrigation.
                    - Monitor rice growth and apply fertilizer as required.
                    """
                    farmerHealth = """
                    - Maintain hydration and wear protective clothing for sun exposure.
                    """
                    pestManagement = """
                    - No significant pest risks under normal conditions.
                    """
                }
            }
        }
        "Clouds" -> {
            when {
                temperature != null && temperature > 30 -> {
                    farming = """
                    - Cloudy conditions with high temperatures can still stress crops; monitor soil moisture closely.
                    - Consider adjusting fertilizer timing to cooler hours.
                    """
                    farmerHealth = """
                    - Even in cloudy weather, stay hydrated and wear sun protection.
                    """
                    pestManagement = """
                    - Check for pest infestations, as cloud cover can sometimes lead to increased humidity.
                    """
                }
                temperature != null && temperature < 20 -> {
                    farming = """
                    - Cooler temperatures are favorable for rice crops; maintain regular irrigation.
                    """
                    farmerHealth = """
                    - Wear additional layers if working in cooler conditions.
                    """
                    pestManagement = """
                    - Pest risks are lower in cooler temperatures.
                    """
                }
                else -> {
                    farming = """
                    - General care for crops during cloudy conditions.
                    """
                    farmerHealth = """
                    - Stay comfortable and wear weather-appropriate clothing.
                    """
                    pestManagement = """
                    - No significant pest risks under cloudy conditions.
                    """
                }
            }
        }
        "Rain" -> {
            when {
                rainfall != null && rainfall > 5 -> {
                    farming = """
                    - Prepare for heavy rainfall, ensure drainage systems are clear.
                    - Avoid applying fertilizer immediately before or after heavy rain.
                    """
                    farmerHealth = """
                    - Wear rain gear to avoid getting soaked.
                    - Stay cautious of slippery surfaces to avoid injuries.
                    """
                    pestManagement = """
                    - Monitor for pests that thrive in wet conditions, such as snails and rice bugs.
                    - Check for fungal diseases caused by excess moisture.
                    """
                }
                rainfall != null && rainfall <= 5 -> {
                    farming = """
                    - Light rain benefits rice crops, but avoid waterlogging.
                    """
                    farmerHealth = """
                    - Dress appropriately for rain but ensure ventilation to avoid overheating.
                    """
                    pestManagement = """
                    - Monitor fields for pests and fungi during mild rainfall.
                    """
                }
                else -> {
                    farming = """
                    - Manage water levels appropriately during rainy days.
                    """
                    farmerHealth = """
                    - Stay protected from rain but ensure proper ventilation to avoid discomfort.
                    """
                    pestManagement = """
                    - Check for water-related pests such as snails and slugs.
                    """
                }
            }
        }
        "Thunderstorm" -> {
            when {
                windSpeed != null && windSpeed > 10 -> {
                    farming = """
                    - Secure farming equipment to prevent wind damage.
                    - Delay outdoor activities until the storm passes.
                    """
                    farmerHealth = """
                    - Stay indoors, avoid working in thunderstorms.
                    """
                    pestManagement = """
                    - Check for crop damage from heavy rains or winds.
                    - Monitor for pests after storm-induced flooding.
                    """
                }
                else -> {
                    farming = """
                    - Stormy conditions can lead to flooding; monitor drainage systems.
                    """
                    farmerHealth = """
                    - Stay indoors and avoid lightning risk.
                    """
                    pestManagement = """
                    - Increased chance of pest infestations after storms.
                    """
                }
            }
        }
        "Drizzle" -> {
            farming = """
            - Light rain helps in rice growth, but monitor water levels.
            """
            farmerHealth = """
            - Wear a light jacket or rain gear to stay dry.
            """
            pestManagement = """
            - Mild increase in pest activity, but generally low threat.
            """
        }
        "Mist", "Haze", "Fog" -> {
            farming = """
            - No significant impact on farming, but monitor visibility for safe operation of machinery.
            """
            farmerHealth = """
            - Wear a mask or protective gear to avoid inhaling mist or haze.
            """
            pestManagement = """
            - Pests generally inactive during these conditions.
            """
        }
        // Add more conditions for other weather types (Thunderstorm, Mist, Haze, etc.)
        else -> {
            farming = """
            - General farming advice applicable for unknown conditions.
            """
            farmerHealth = """
            - General health advice for unknown weather conditions.
            """
            pestManagement = """
            - General pest management advice applicable for unknown conditions.
            """
        }
    }

    // Return the recommendations as a WeatherRecommendations object
    return WeatherRecommendations(farming, farmerHealth, pestManagement)
}
