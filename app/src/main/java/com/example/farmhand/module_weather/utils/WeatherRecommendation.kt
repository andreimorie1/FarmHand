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
    mainData: String,
    windSpeedData: Double,
    humidityData: Int,
    visibilityData: Int,
    rainData: Double,
    tempData: Double,
    airQualityData: String
): WeatherRecommendations {
    val main = mainData //currentWeatherResponse.weather[0].main
    val windSpeed = windSpeedData //currentWeatherResponse.wind.speed
    val humidity = humidityData //currentWeatherResponse.main.humidity
    val visibility = visibilityData //currentWeatherResponse.visibility
    val rain = rainData //currentWeatherResponse.rain.`1h`
    val temp = tempData //currentWeatherResponse.main.temp
    val airquality = airQualityData

    val farming: String
    val farmerHealth: String
    val pestManagement: String

    when (
        main
        //currentWeatherResponse.weather[0].main
    ) {
        "Clear" -> { //     Clear Weather
            when {
                // Clear and Windy
                //currentWeatherResponse.wind.speed
                windSpeed > 15 -> {
                    farming = multilineString(
                        "• Check for soil erosion and protect young plants from strong winds.",
                        "• Ensure irrigation systems are secure to prevent water loss due to evaporation."
                    )
                    farmerHealth = multilineString(
                        "• Wear protective gear to shield against wind exposure.",
                        "• Be cautious when working at height due to strong gusts."
                    )
                    pestManagement = multilineString(
                        "• Monitor for pest movement; windy conditions can carry pests from one field to another."
                    )
                }

                // Clear and Hot
                //currentWeatherResponse.main.temp
                temp > 35 -> {
                    farming = multilineString(
                        "• Increase irrigation frequency to prevent stress on crops.",
                        "• Apply mulch to retain soil moisture."
                    )
                    farmerHealth = multilineString(
                        "• Take frequent breaks to avoid heat exhaustion; consider working early or late in the day.",
                        "• Stay hydrated and wear hats or visors to shield from direct sunlight."
                    )
                    pestManagement = multilineString(
                        "• Check for pests that thrive in heat, like spider mites and whiteflies."
                    )
                }

                // Clear and Dry
                //currentWeatherResponse.main.humidity
                humidity < 30 -> {
                    farming = multilineString(
                        "• Irrigate regularly to combat dry conditions and support plant growth.",
                        "• Monitor for signs of drought stress in crops."
                    )
                    farmerHealth = multilineString(
                        "• Ensure proper hydration to avoid dehydration; consider electrolyte supplements.",
                        "• Wear light clothing to reflect sunlight."
                    )
                    pestManagement = multilineString(
                        "• Watch for pests that prefer dry conditions, such as certain beetles."
                    )
                }

                // Clear and Humid
                //currentWeatherResponse.main.humidity
                humidity > 70 -> {
                    farming = multilineString(
                        "• Ensure good air circulation in crops to prevent fungal diseases.",
                        "• Water in the early morning or late afternoon to reduce evaporation."
                    )
                    farmerHealth = multilineString(
                        "• Be mindful of heat stress; wear breathable clothing.",
                        "• Take breaks in shaded areas to cool down."
                    )
                    pestManagement = multilineString(
                        "• Monitor for fungal diseases that thrive in humid conditions, such as downy mildew."
                    )
                }

                // Default for Clear Weather
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

        "Clouds" -> {   //  Cloudy
            when {
                // Cloudy and Cool
                //currentWeatherResponse.main.temp
                temp < 20 -> {
                    farming = multilineString(
                        "• Monitor for frost risk; consider protective measures for sensitive crops.",
                        "• Delayed planting may be necessary if temperatures are too low."
                    )
                    farmerHealth = multilineString(
                        "• Dress warmly and be prepared for cold, damp conditions.",
                        "• Stay dry to avoid colds and respiratory issues."
                    )
                    pestManagement = multilineString(
                        "• Fewer pests may be active, but check for diseases that thrive in cool, damp environments."
                    )
                }

                // Cloudy and Mild
                // currentWeatherResponse.main.temp
                temp in 20.0..30.0 -> {
                    farming = multilineString(
                        "• Good growing conditions; monitor for adequate moisture levels.",
                        "• Consider applying nitrogen fertilizers to promote growth."
                    )
                    farmerHealth = multilineString(
                        "• Comfortable working conditions; dress in layers for variable temperatures.",
                        "• Maintain hydration, especially if humidity is high."
                    )
                    pestManagement = multilineString(
                        "• Watch for pests that prefer humid conditions, like aphids and caterpillars."
                    )
                }

                // Cloudy and Warm
                // currentWeatherResponse.main.temp
                temp > 30 -> {
                    farming = multilineString(
                        "• Ensure proper irrigation; cloudy weather can sometimes hide heat stress.",
                        "• Monitor for disease pressure due to high humidity levels."
                    )
                    farmerHealth = multilineString(
                        "• Be cautious of heat-related illnesses; take breaks in shaded areas.",
                        "• Wear breathable clothing to stay cool."
                    )
                    pestManagement = multilineString(
                        "• Check for pests that thrive in warm, humid conditions, such as snails and caterpillars."
                    )
                }

                // Cloudy with High Humidity
                // currentWeatherResponse.main.humidity
                humidity > 80 -> {
                    farming = multilineString(
                        "• Increase air circulation around crops to prevent fungal diseases.",
                        "• Delay watering if conditions are excessively humid to avoid waterlogging."
                    )
                    farmerHealth = multilineString(
                        "• Dress appropriately for humid conditions to avoid discomfort.",
                        "• Take breaks to cool down and stay hydrated."
                    )
                    pestManagement = multilineString(
                        "• Be vigilant for fungal infections that thrive in humid conditions."
                    )
                }

                // Cloudy with Rain/Drizzle
                // currentWeatherResponse.rain.`1h`
                rain > 0 -> {
                    farming = multilineString(
                        "• Assess drainage systems to avoid waterlogging.",
                        "• Delay any pesticide applications until after the rain subsides."
                    )
                    farmerHealth = multilineString(
                        "• Wear waterproof clothing and ensure proper footwear to stay dry.",
                        "• Be cautious of slippery conditions when walking in fields."
                    )
                    pestManagement = multilineString(
                        "• Monitor for pests that may emerge after rainfall; check for slugs and snails."
                    )
                }

                // Cloudy with Strong Winds
                // currentWeatherResponse.wind.speed
                windSpeed > 15 -> {
                    farming = multilineString(
                        "• Secure any loose structures or equipment to prevent damage from wind.",
                        "• Check for potential damage to crops from wind gusts."
                    )
                    farmerHealth = multilineString(
                        "• Be cautious when working outdoors in windy conditions; consider working in sheltered areas.",
                        "• Protect eyes and skin from wind exposure."
                    )
                    pestManagement = multilineString(
                        "• Wind can disrupt pest movement; monitor for changes in pest behavior."
                    )
                }

                // Default for Cloudy Weather
                else -> {
                    farming = multilineString(
                        //"• General care for crops during cloudy conditions."
                        "• Continue regular monitoring of crop health and moisture levels.",
                        "• Consider applying fertilizers if conditions are favorable."
                    )
                    farmerHealth = multilineString(
                        //"• Stay comfortable and wear module_weather-appropriate clothing."
                        "• Dress for variable temperatures and maintain hydration."
                    )
                    pestManagement = multilineString(
                        //"• No significant pest risks under cloudy conditions."
                        "• General monitoring for pests is advised; no specific risks identified."
                    )
                }
            }
        }

        "Rain" -> {
            when {
                // Light Rain with Mild Temperature
                //currentWeatherResponse.rain.`1h`      currentWeatherResponse.main.temp
                rain < 5 && temp in 20.0..30.0 -> {
                    farming = multilineString(
                        "• Light rain provides good moisture; monitor soil to avoid over-irrigation.",
                        "• Consider applying nitrogen fertilizers, as rain can enhance nutrient absorption."
                    )
                    farmerHealth = multilineString(
                        "• Wear a raincoat and waterproof boots for light rain protection.",
                        "• Avoid staying out too long to reduce exposure to damp conditions."
                    )
                    pestManagement = multilineString(
                        "• Monitor for pests like snails and slugs that thrive in wet conditions.",
                        "• Check for early signs of fungal diseases on leaves and stems."
                    )
                }

                // Moderate to Heavy Rain
                //currentWeatherResponse.rain.`1h`
                rain in 5.0..15.0 -> {
                    farming = multilineString(
                        "• Ensure proper drainage to prevent waterlogging in fields.",
                        "• Avoid applying fertilizers or pesticides until rain subsides to prevent runoff.",
                        "• Monitor for erosion in sloped areas and consider additional ground cover."
                    )
                    farmerHealth = multilineString(
                        "• Stay indoors when possible, as heavy rain can reduce visibility and increase accident risks.",
                        "• Wear protective, waterproof clothing if you must work outside."
                    )
                    pestManagement = multilineString(
                        "• Increased likelihood of fungal infections; inspect crops for signs regularly.",
                        "• Keep an eye out for pest outbreaks that often follow rainfall."
                    )
                }

                // Heavy Rain with Cool Temperatures
                //currentWeatherResponse.rain.`1h`      currentWeatherResponse.main.temp
                rain > 15 && temp < 20 -> {
                    farming = multilineString(
                        "• Heavy rain with low temperatures can cause root rot; inspect drainage frequently.",
                        "• Delay all chemical applications until the ground dries to prevent crop burn.",
                        "• Ensure young plants are protected from soil erosion."
                    )
                    farmerHealth = multilineString(
                        "• Avoid outdoor work during heavy rain; cold rain increases risk of hypothermia.",
                        "• Dress in waterproof, insulated clothing and take breaks indoors."
                    )
                    pestManagement = multilineString(
                        "• Watch for slug and snail infestations, especially near wet, shaded areas.",
                        "• Fungal diseases may thrive in these conditions; check plants thoroughly."
                    )
                }

                // Intense Rain with High Winds
                // currentWeatherResponse.wind.speed
                windSpeed > 15 -> {
                    farming = multilineString(
                        "• Secure young or lightweight crops that could be damaged by high winds.",
                        "• Delay fertilizer applications, as runoff is likely.",
                        "• Monitor for soil erosion and support plants that may lean due to wet soil."
                    )
                    farmerHealth = multilineString(
                        "• Strong winds with rain make outdoor work hazardous; avoid exposure if possible.",
                        "• Wear eye protection to shield against blowing debris."
                    )
                    pestManagement = multilineString(
                        "• Pests may be less active in these conditions, but monitor closely after rain ends.",
                        "• Check for soil-borne pests that thrive in wet, disturbed soil."
                    )
                }

                // Persistent Light Rain with High Humidity
                // currentWeatherResponse.rain.`1h`     currentWeatherResponse.main.humidity
                rain < 5 && humidity > 80 -> {
                    farming = multilineString(
                        "• Persistent light rain increases humidity, which may promote fungal diseases.",
                        "• Avoid watering plants as soil moisture is likely adequate.",
                        "• Monitor closely for mold and mildew on crops."
                    )
                    farmerHealth = multilineString(
                        "• High humidity may cause discomfort; dress in moisture-wicking clothes.",
                        "• Take breaks to avoid prolonged exposure to damp conditions."
                    )
                    pestManagement = multilineString(
                        "• Increased risk of fungal spores spreading; inspect plants regularly.",
                        "• Watch for aphids and other humidity-loving pests."
                    )
                }

                // Default Rainy Conditions
                else -> {
                    farming = multilineString(
                        "• Moderate rainfall is beneficial but keep an eye on soil moisture.",
                        "• Avoid applying chemicals during rain to prevent runoff and waste."
                    )
                    farmerHealth = multilineString(
                        "• Dress in waterproof clothing and carry a rain cover for outdoor tasks.",
                        "• Monitor for slippery surfaces in fields."
                    )
                    pestManagement = multilineString(
                        "• Regularly check plants for fungal diseases in moist conditions.",
                        "• Keep an eye out for snails and other rain-attracted pests."
                    )
                }
            }
        }

        "Thunderstorm" -> {
            when {
                // Thunderstorm with Heavy Rain
                //currentWeatherResponse.rain.`1h`
                rain > 15 -> {
                    farming = multilineString(
                        "• Heavy rain during a thunderstorm can cause severe waterlogging; check field drainage regularly.",
                        "• Secure seedlings and young plants as high winds may uproot them.",
                        "• Delay fertilizing or pesticide applications until after the storm to prevent runoff."
                    )
                    farmerHealth = multilineString(
                        "• Avoid working outside during thunderstorms due to lightning risk.",
                        "• Seek shelter immediately if you see lightning or hear thunder nearby.",
                        "• After the storm, assess any areas prone to flooding or slippery ground before resuming work."
                    )
                    pestManagement = multilineString(
                        "• Inspect fields for signs of pest activity after the storm, as waterlogged soil attracts pests like slugs and snails.",
                        "• Watch for early signs of fungal infections, which are likely to thrive after heavy rain."
                    )
                }

                // Thunderstorm with High Winds
                //currentWeatherResponse.wind.speed
                windSpeed > 15 -> {
                    farming = multilineString(
                        "• High winds can damage crops; use support stakes for plants that might bend or uproot.",
                        "• Postpone any irrigation to avoid oversaturation and soil erosion during high winds.",
                        "• Check for fallen branches or debris in fields that may obstruct growth or cause damage."
                    )
                    farmerHealth = multilineString(
                        "• High winds combined with lightning are extremely hazardous; remain indoors until the storm passes.",
                        "• If absolutely necessary to go outside, wear protective clothing and avoid open fields."
                    )
                    pestManagement = multilineString(
                        "• Pest control can be delayed until calmer weather returns.",
                        "• After the storm, inspect for pests that may have been displaced by wind and rain."
                    )
                }

                // Thunderstorm with Mild Rain and Humidity
                //currentWeatherResponse.rain.`1h`      currentWeatherResponse.main.humidity
                rain < 10 && humidity > 80 -> {
                    farming = multilineString(
                        "• Thunderstorms with light rain can increase humidity, which promotes fungal growth; check crops for mildew.",
                        "• Ensure proper drainage to prevent minor waterlogging in humid conditions.",
                        "• Delay pesticide application to avoid runoff."
                    )
                    farmerHealth = multilineString(
                        "• High humidity and thunderstorms can cause discomfort; seek shelter until the storm passes.",
                        "• If you must go outside, use light waterproof clothing and be aware of lightning risks."
                    )
                    pestManagement = multilineString(
                        "• Monitor plants for fungal diseases, especially those related to high humidity.",
                        "• After the storm, watch for an increase in pests that thrive in damp conditions."
                    )
                }

                // Severe Thunderstorm with Lightning and Moderate Rain
                //currentWeatherResponse.rain.`1h`
                rain in 10.0..15.0 -> {
                    farming = multilineString(
                        "• Securely stake crops vulnerable to wind and rain damage.",
                        "• Postpone any fertilizing or spraying activities until the storm subsides.",
                        "• Check drainage areas in fields to avoid minor flooding after moderate rain."
                    )
                    farmerHealth = multilineString(
                        "• Severe thunderstorms with lightning are highly dangerous; avoid all outdoor activity.",
                        "• Stay indoors until the storm completely passes to reduce lightning risk."
                    )
                    pestManagement = multilineString(
                        "• Delayed pest control may be needed; monitor for pests that thrive post-storm.",
                        "• Check for fungal infections that may have started due to the storm's humidity."
                    )
                }

                // Default Thunderstorm Condition
                else -> {
                    farming = multilineString(
                        "• Thunderstorms may affect crop health; inspect fields after the storm.",
                        "• Postpone all irrigation, fertilizing, or spraying activities until weather clears."
                    )
                    farmerHealth = multilineString(
                        "• Avoid going outside; thunderstorms present serious lightning hazards.",
                        "• Resume work only when the storm has safely passed."
                    )
                    pestManagement = multilineString(
                        "• Monitor crops for signs of fungal infection or pest activity post-storm.",
                        "• Inspect soil and plants for any damage caused by the storm."
                    )
                }
            }
        }

        "Drizzle" -> {
            when {
                // Light Drizzle with Cool Temperatures
                // currentWeatherResponse.main.temp     currentWeatherResponse.rain.`1h`
                temp < 25 && rain < 2 -> {
                    farming = multilineString(
                        "• Light drizzle with cool temperatures can benefit rice seedlings by providing gentle moisture.",
                        "• Continue monitoring fields to ensure adequate moisture without waterlogging.",
                        "• Consider using a light layer of mulch to retain moisture if drizzle is intermittent."
                    )
                    farmerHealth = multilineString(
                        "• Dress in layers to stay warm and dry during cool drizzle.",
                        "• Avoid staying in wet clothes for too long to prevent chills or colds."
                    )
                    pestManagement = multilineString(
                        "• Light drizzle can increase the presence of fungal spores; check crops for signs of mildew.",
                        "• Monitor for pests that favor damp conditions, like snails and slugs."
                    )
                }

                // Moderate Drizzle with Warm Temperatures
                // currentWeatherResponse.main.temp     currentWeatherResponse.rain.`1h`
                temp in 25.0..30.0 && rain in 2.0..5.0 -> {
                    farming = multilineString(
                        "• Moderate drizzle and warm temperatures may lead to waterlogged soil; ensure proper drainage.",
                        "• Delay fertilizer application to avoid runoff or nutrient leaching.",
                        "• Inspect fields regularly to prevent any buildup of standing water."
                    )
                    farmerHealth = multilineString(
                        "• Warm drizzle can lead to discomfort; wear breathable, waterproof clothing if working outside.",
                        "• Take breaks indoors if feeling overheated due to warm, humid conditions."
                    )
                    pestManagement = multilineString(
                        "• Humid conditions from drizzle can increase pest activity, particularly insects that thrive in moisture.",
                        "• Apply pest control measures only if necessary and avoid spraying in wet conditions."
                    )
                }

                // Prolonged Drizzle with High Humidity
                //currentWeatherResponse.rain.`1h`      currentWeatherResponse.main.humidity
                rain >= 5 && humidity > 85 -> {
                    farming = multilineString(
                        "• Prolonged drizzle with high humidity can weaken young plants; monitor for signs of root rot or fungal issues.",
                        "• Delay any sowing or planting until conditions dry out to avoid seed rot.",
                        "• Avoid heavy machinery to prevent soil compaction in wet conditions."
                    )
                    farmerHealth = multilineString(
                        "• High humidity and prolonged drizzle can increase discomfort; avoid extended outdoor work.",
                        "• Wear lightweight, waterproof clothing to manage humidity and prevent skin irritation."
                    )
                    pestManagement = multilineString(
                        "• High humidity encourages fungal infections; inspect plants daily for early signs of infection.",
                        "• After prolonged drizzle, monitor for snails, slugs, and other moisture-loving pests."
                    )
                }

                // Default Drizzle Condition
                else -> {
                    farming = multilineString(
                        "• Drizzle can add light moisture to fields; ensure proper drainage to avoid mild waterlogging.",
                        "• Consider light irrigation only if drizzle is insufficient for crop needs."
                    )
                    farmerHealth = multilineString(
                        "• Wear a rain jacket and avoid staying wet for prolonged periods.",
                        "• Work indoors if drizzle leads to uncomfortable humidity levels."
                    )
                    pestManagement = multilineString(
                        "• Check for early signs of mildew or fungal infections after drizzly conditions.",
                        "• Monitor for any increase in pests due to dampness."
                    )
                }
            }
        }


        "Mist" -> {
            when {
                // High Humidity Mist
                //currentWeatherResponse.main.humidity
                humidity > 90 -> {
                    farming = multilineString(
                        "• High humidity from mist may encourage fungal diseases; inspect crops regularly.",
                        "• Delay applying pesticides or fertilizers until the mist clears to prevent runoff.",
                        "• Avoid soil disturbance to prevent compaction in damp conditions."
                    )
                    farmerHealth = multilineString(
                        "• Misty conditions with high humidity can be uncomfortable; wear moisture-wicking clothing.",
                        "• Visibility is reduced; use caution when moving through fields or operating machinery."
                    )
                    pestManagement = multilineString(
                        "• High humidity increases pest activity; inspect crops for insects that thrive in damp conditions.",
                        "• Watch for signs of fungal diseases like mildew."
                    )
                }

                // Mild Mist with Moderate Humidity
                // currentWeatherResponse.main.humidity
                humidity in 80..90 -> {
                    farming = multilineString(
                        "• Light mist can delay evaporation; monitor soil moisture to avoid over-watering.",
                        "• Mist may reduce sunlight, so adjust irrigation if plants show signs of moisture stress."
                    )
                    farmerHealth = multilineString(
                        "• Moderate humidity in misty conditions may cause mild discomfort; wear moisture-wicking clothing.",
                        "• Take caution in low visibility, especially when moving through fields."
                    )
                    pestManagement = multilineString(
                        "• Inspect crops for early signs of fungal disease after prolonged mist.",
                        "• Check for any increase in pests that thrive in low light."
                    )
                }

                // Default Mist Condition
                else -> {
                    farming = multilineString(
                        "• Mist can limit sunlight; monitor plant health if low light persists.",
                        "• Avoid pesticide or fertilizer application to ensure even distribution in low visibility."
                    )
                    farmerHealth = multilineString(
                        "• Reduced visibility due to mist; use caution when working in the fields.",
                        "• Wear light, breathable layers to stay comfortable."
                    )
                    pestManagement = multilineString(
                        "• Mist can affect pest activity; inspect crops for insects or fungi after mist clears."
                    )
                }
            }
        }

        "Fog" -> {
            when {
                // Dense Fog with High Humidity
                // currentWeatherResponse.main.humidity
                humidity > 90 -> {
                    farming = multilineString(
                        "• Dense fog can increase the risk of fungal diseases; monitor plants closely.",
                        "• Avoid chemical applications until fog clears to reduce runoff risk.",
                        "• Check drainage as persistent fog keeps the ground damp."
                    )
                    farmerHealth = multilineString(
                        "• Fog with high humidity can lead to discomfort; wear breathable, moisture-wicking clothing.",
                        "• Reduced visibility is a safety concern; avoid heavy machinery use in the fog."
                    )
                    pestManagement = multilineString(
                        "• High humidity increases pest and fungal risk; inspect crops for mildew or pests post-fog.",
                        "• Plan for pest control when visibility improves."
                    )
                }

                // Mild Fog with Moderate Humidity
                // currentWeatherResponse.main.humidity
                humidity in 80..90 -> {
                    farming = multilineString(
                        "• Moderate fog can reduce sunlight; adjust irrigation if plants show signs of moisture stress.",
                        "• Avoid disturbing soil to prevent compaction in damp conditions."
                    )
                    farmerHealth = multilineString(
                        "• Moderate humidity may cause discomfort; wear layers to adjust as needed.",
                        "• Take precautions in low visibility, especially near machinery."
                    )
                    pestManagement = multilineString(
                        "• Moderate fog may increase fungal disease risks; inspect crops after fog clears.",
                        "• Monitor for pests that thrive in humid, low-light conditions."
                    )
                }

                // Default Fog Condition
                else -> {
                    farming = multilineString(
                        "• Fog can limit sunlight; monitor plant health if low light persists.",
                        "• Avoid chemical application until fog clears for consistent distribution."
                    )
                    farmerHealth = multilineString(
                        "• Reduced visibility due to fog; wear reflective clothing if working near roads or machinery.",
                        "• Stay hydrated and wear comfortable, moisture-wicking clothing."
                    )
                    pestManagement = multilineString(
                        "• Fog can encourage pests and fungi; inspect crops after fog dissipates."
                    )
                }
            }
        }

        "Haze" -> {
            when {
                // Haze with Poor Air Quality
                // currentWeatherResponse.main.humidity

                humidity < 80 && (airquality.contains("smoke") || airquality.contains("dust")) -> {
                    farming = multilineString(
                        "• Haze due to dust or smoke may limit sunlight, affecting photosynthesis; monitor crop health.",
                        "• Avoid spraying pesticides or fertilizers as they may not distribute evenly in low visibility."
                    )
                    farmerHealth = multilineString(
                        "• Poor air quality; consider wearing a mask if haze is due to dust or smoke.",
                        "• Low visibility may pose a safety risk; avoid operating machinery if possible."
                    )
                    pestManagement = multilineString(
                        "• Reduced light may increase pest activity; inspect plants for insects that thrive in low light.",
                        "• Airborne particles may harm plant leaves; rinse with clean water if dust accumulates."
                    )
                }

                // Haze with Moderate Visibility and Humidity
                //currentWeatherResponse.main.humidity
                humidity in 80..90 -> {
                    farming = multilineString(
                        "• Haze with moderate humidity can reduce sunlight; monitor plants for signs of light stress.",
                        "• Continue regular farming practices but be cautious with chemical application in low visibility."
                    )
                    farmerHealth = multilineString(
                        "• Hazy conditions with moderate humidity may cause discomfort; dress in breathable clothing.",
                        "• Be cautious in low visibility; consider wearing a mask if the haze is from pollutants."
                    )
                    pestManagement = multilineString(
                        "• Monitor for pests that thrive in low light or humid conditions.",
                        "• Haze can limit photosynthesis; check plants for signs of reduced growth."
                    )
                }

                // Default Haze Condition
                else -> {
                    farming = multilineString(
                        "• Haze can impact sunlight; monitor crops for signs of light stress.",
                        "• Avoid applying chemicals during haze as particles may limit even distribution."
                    )
                    farmerHealth = multilineString(
                        "• Reduced visibility due to haze; use caution, especially when traveling or operating machinery.",
                        "• Consider using a mask if haze impacts air quality."
                    )
                    pestManagement = multilineString(
                        "• Haze may encourage pests that prefer low light; inspect plants after the haze clears."
                    )
                }
            }
        }

        "Smoke" -> {
            when {
                // Dense Smoke
                // currentWeatherResponse.visibility
                visibility < 500 -> {
                    farming = multilineString(
                        "• Extremely low visibility due to smoke necessitates postponing all outdoor activities.",
                        "• Ensure that all equipment is secured to avoid accidents.",
                        "• Limit movement in fields to reduce the risk of accidents."
                    )
                    farmerHealth = multilineString(
                        "• Heavy smoke can cause severe respiratory issues; avoid outdoor work.",
                        "• Stay indoors to minimize health risks from smoke inhalation."
                    )
                    pestManagement = multilineString(
                        "• Monitor crops for damage due to particulate settling from smoke."
                    )
                }

                // Moderate Smoke
                // currentWeatherResponse.visibility
                visibility in 500..1000 -> {
                    farming = multilineString(
                        "• Reduced visibility can impact field navigation; proceed with caution.",
                        "• Consider postponing any non-essential tasks until visibility improves."
                    )
                    farmerHealth = multilineString(
                        "• Smoke may cause irritation; use masks if outdoor activity is unavoidable.",
                        "• Stay hydrated and monitor for any respiratory issues."
                    )
                    pestManagement = multilineString(
                        "• Inspect for any changes in pest activity; smoke may affect pest behavior."
                    )
                }

                // Default Smoke Condition
                else -> {
                    farming = multilineString(
                        "• Regular farming practices can continue if visibility is manageable.",
                        "• Stay alert for any changes in air quality."
                    )
                    farmerHealth = multilineString(
                        "• Maintain hydration and health precautions when working outdoors."
                    )
                    pestManagement = multilineString(
                        "• Regular pest monitoring is encouraged; check for any signs of activity."
                    )
                }
            }
        }


        "Dust" -> {
            when {
                // High Dust Levels with Low Visibility
                // currentWeatherResponse.visibility
                visibility < 1000 -> {
                    farming = multilineString(
                        "• Poor visibility due to dust requires caution when moving through fields; consider postponing work.",
                        "• Ensure that all equipment is secured to prevent accidents due to low visibility.",
                        "• If dust is heavy, limit outdoor activities until conditions improve."
                    )
                    farmerHealth = multilineString(
                        "• Dust can irritate the respiratory system; wear masks or respirators when outside.",
                        "• Keep hydrated to help reduce the effects of dust exposure on health."
                    )
                    pestManagement = multilineString(
                        "• Dust can hinder visibility for pest monitoring; be cautious when inspecting crops."
                    )
                }

                // Moderate Dust Levels
                // currentWeatherResponse.visibility
                visibility in 1000..2000 -> {
                    farming = multilineString(
                        "• Moderate dust may impact visibility; proceed with caution while working outdoors.",
                        "• Monitor dust levels and consider adjusting irrigation practices to mitigate soil disturbance."
                    )
                    farmerHealth = multilineString(
                        "• Use protective masks if necessary; minimize exposure to dust by staying indoors when possible.",
                        "• Stay hydrated to counteract any respiratory irritation from dust."
                    )
                    pestManagement = multilineString(
                        "• Dust may impact pest activity; check for any changes in pest behavior following dust events."
                    )
                }

                // Default Dust Condition
                else -> {
                    farming = multilineString(
                        "• Regular farming practices can continue; however, monitor dust levels.",
                        "• Keep equipment clean to avoid buildup of dust that could impair function."
                    )
                    farmerHealth = multilineString(
                        "• Maintain general health precautions and stay hydrated when working outdoors."
                    )
                    pestManagement = multilineString(
                        "• Regular pest monitoring is encouraged; ensure visibility is adequate for inspections."
                    )
                }
            }
        }


        "Ash" -> {
            when {
                // High Ash fall with Low Visibility
                // currentWeatherResponse.visibility
                visibility < 1000 -> {
                    farming = multilineString(
                        "• Volcanic ash can damage crops; cover sensitive plants if possible to reduce exposure.",
                        "• Avoid irrigation during ashfall to prevent ash from solidifying on plants.",
                        "• Use protective coverings for soil to reduce ash buildup, which can alter soil composition and pH levels."
                    )
                    farmerHealth = multilineString(
                        "• Ash can severely irritate the respiratory system; wear high-quality masks (N95 or similar) and goggles if outside.",
                        "• Limit time outdoors to minimize exposure; volcanic ash can cause eye and skin irritation.",
                        "• Stay hydrated to reduce respiratory irritation and avoid outdoor work if possible."
                    )
                    pestManagement = multilineString(
                        "• Ashfall may deter pests temporarily; however, inspect plants for any pests once the ash clears.",
                        "• Delay pest control treatments until ash has settled to ensure even application and effectiveness."
                    )
                }

                // Moderate Ashfall with Reduced Visibility
                //currentWeatherResponse.visibility
                visibility in 1000..2000 -> {
                    farming = multilineString(
                        "• Light ashfall can affect soil and crop health; gently clean ash from crops if feasible.",
                        "• Minimize activities that disturb the soil, as ash can compact and alter soil properties.",
                        "• Postpone pesticide and fertilizer applications, as ash can interfere with absorption and effectiveness."
                    )
                    farmerHealth = multilineString(
                        "• Wear protective masks and avoid touching your face to reduce ash exposure risks.",
                        "• Reduce outdoor activity to prevent respiratory issues; seek shelter if ashfall intensifies."
                    )
                    pestManagement = multilineString(
                        "• Ash may impact pest behavior; resume monitoring and control activities once ashfall stops.",
                        "• Check for any fungal growth on plants, as ash can retain moisture on leaf surfaces."
                    )
                }

                // Light Ashfall with Minor Impact
                else -> {
                    farming = multilineString(
                        "• Light ash may settle on crops; consider gently washing crops with clean water after ashfall.",
                        "• Monitor soil pH levels periodically, as even light ashfall can alter soil chemistry.",
                        "• Delay fertilizer application until after rain or irrigation clears ash from plants."
                    )
                    farmerHealth = multilineString(
                        "• Ash can still irritate the respiratory system; wearing a mask is advisable during light ashfall.",
                        "• Reduce unnecessary outdoor activity until the ash disperses."
                    )
                    pestManagement = multilineString(
                        "• Resume pest management activities once ashfall ends; inspect crops for any signs of stress due to ash.",
                        "• Ashfall may temporarily reduce pest activity; observe for any pest resurgence after rain or irrigation."
                    )
                }
            }
        }

        "Squall" -> {
            when {
                // High-Intensity Squall with Extremely High Winds
                // currentWeatherResponse.wind.speed
                windSpeed > 20 -> {
                    farming = multilineString(
                        "• Strong winds may damage or uproot crops; consider using additional supports for vulnerable plants.",
                        "• Avoid applying fertilizers or pesticides, as strong winds may cause drift and uneven application.",
                        "• Inspect fields for debris after the squall, and clear any obstructions to ensure crop growth isn’t hindered."
                    )
                    farmerHealth = multilineString(
                        "• Stay indoors during high winds to avoid flying debris and potential injuries.",
                        "• If absolutely necessary to go outside, wear protective clothing and avoid open areas.",
                        "• Wait for winds to calm before inspecting fields or using heavy machinery."
                    )
                    pestManagement = multilineString(
                        "• Squalls may displace pests; inspect crops post-squall for signs of new infestations.",
                        "• Delay pest control applications until winds subside to ensure safe and even distribution."
                    )
                }

                // Moderate Squall with High Winds
                // currentWeatherResponse.wind.speed
                windSpeed in 15.0..20.0 -> {
                    farming = multilineString(
                        "• High winds can stress or damage crops; check supports for plants that may be vulnerable.",
                        "• Postpone irrigation to prevent soil erosion and minimize moisture loss due to evaporation.",
                        "• After the squall, inspect for fallen branches or debris that might impact crop health."
                    )
                    farmerHealth = multilineString(
                        "• Remain indoors or seek shelter to stay safe from sudden gusts and airborne debris.",
                        "• Use caution if working outdoors, and wear sturdy clothing to protect from debris and wind exposure."
                    )
                    pestManagement = multilineString(
                        "• Check for pest displacement, as squalls may cause pests to move or shelter in crops.",
                        "• Delay pest control applications until the wind decreases for accurate coverage."
                    )
                }

                // Light Squall with Mild Winds
                else -> {
                    farming = multilineString(
                        "• Mild squalls may still stress plants; monitor crops for signs of wind damage.",
                        "• Check soil moisture post-squall, as wind can dry the topsoil quickly.",
                        "• Clear any minor debris in fields after winds settle to prevent interference with crops."
                    )
                    farmerHealth = multilineString(
                        "• Exercise caution during outdoor tasks, as sudden gusts may still pose a hazard.",
                        "• Wear wind-resistant clothing to stay comfortable while working outdoors."
                    )
                    pestManagement = multilineString(
                        "• Inspect for any pests that may have been affected by wind; adjust pest control measures as needed.",
                        "• Squalls may create damp conditions post-wind; watch for pests that thrive in humid microclimates."
                    )
                }
            }
        }


        "Tornado" -> {
            farming = multilineString(
                "• Tornadoes can cause severe crop damage; secure any loose farm equipment or tools if time allows.",
                "• Move livestock to sheltered areas if possible to protect them from flying debris.",
                "• Inspect fields for damage after the tornado passes, and clear debris to prevent further issues with crop growth."
            )
            farmerHealth = multilineString(
                "• Tornado safety is paramount; take shelter immediately in a storm cellar or an interior room without windows.",
                "• Avoid any outdoor activity; tornadoes are highly dangerous and unpredictable.",
                "• After the tornado, assess fields only when authorities deem it safe, as there may be downed power lines or other hazards."
            )
            pestManagement = multilineString(
                "• Tornadoes can displace pests significantly; inspect for pest movement once it’s safe to return to the field.",
                "• Expect possible increases in pest populations post-tornado due to disrupted habitats."
            )
        }


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

    return WeatherRecommendations(
        farming, farmerHealth, pestManagement
    )
}
