package com.example.farmhand.module_Reco.API

import com.example.farmhand.module_Reco.API.data.Choice
import com.example.farmhand.module_Reco.API.data.Message
import com.example.farmhand.module_Reco.API.data.OpenAiRequest
import com.example.farmhand.module_Reco.API.data.OpenAiResponse
import kotlinx.coroutines.delay


class MockOpenAiApiService : OpenAiApiService {
    override suspend fun createCompletion(
        authorization: String,
        request: OpenAiRequest
    ): OpenAiResponse {

        delay(5000)
        // Mocking a response for Chat endpoint
        return OpenAiResponse(
            id = "mock-id-123",
            objectType = "chat.completion",
            created = System.currentTimeMillis() / 1000,
            model = request.model,
            choices = listOf(
                Choice(
                    message = Message(
                        role = "assistant",
                        content = """
### Summary
Brown spot of rice, caused by the fungus *Bipolaris oryzae*, is a significant disease affecting rice crops, particularly in tropical climates like the Philippines. The disease manifests as dark brown lesions on leaves, which can lead to reduced photosynthesis and lower yields. The current weather conditions—high temperature (31.61°C), high humidity (67%), and low wind speed—are conducive to the spread of this disease. Warm and humid conditions favor fungal growth, while still air can prevent the drying of leaves, allowing the pathogen to thrive.
                                                                                                    
### Control Measures
- **Cultural Practices:**
- **Crop Rotation:** Rotate rice with non-host crops to break the disease cycle.
- **Field Sanitation:** Remove and destroy infected plant debris to reduce inoculum levels.
- **Water Management:** Avoid prolonged flooding and maintain proper water levels to minimize leaf wetness.
- **Resistant Varieties:** Plant rice varieties that are resistant or tolerant to brown spot disease.
- **Chemical Control:**
- **Fungicides:** Apply fungicides such as propiconazole or azoxystrobin at the first sign of disease. Follow local guidelines for application rates and timing.
- **Seed Treatment:** Treat seeds with fungicides before planting to prevent early infection.
                                                                                                    
- **Integrated Pest Management (IPM):**
- **Monitoring:** Regularly inspect fields for early signs of brown spot and other diseases.
- **Threshold Levels:** Establish economic threshold levels for treatment to avoid unnecessary applications.
- **Biological Control:** Explore the use of beneficial microorganisms or biopesticides that can suppress *Bipolaris oryzae*.
                                                                                                    
### Weather Considerations
- **Temperature:** The current temperature of 31.61°C is within the optimal range for fungal growth, which can lead to increased disease incidence.
- **Humidity:** At 67% humidity, the environment is favorable for the development and spread of brown spot, as high moisture levels on leaf surfaces can facilitate fungal infection.
- **Wind Speed:** The low wind speed (2.57 m/s) may hinder the drying of leaves, prolonging leaf wetness and creating a conducive environment for the disease.
                                                                                                    
### Additional Notes
- **Local Practices:** Farmers in the Philippines often utilize a combination of traditional and modern practices. Incorporating local knowledge about resistant varieties and cultural practices is crucial for effective management.
- **Education and Training:** Continuous education on disease management and the importance of monitoring weather conditions can help farmers make informed decisions.
- **Community Collaboration:** Encourage farmers to work together in monitoring and managing disease outbreaks, sharing resources, and knowledge about effective control measures.
                                                                                                    
By implementing these control measures and considering the current weather conditions, farmers can effectively manage brown spot disease in rice crops, thereby protecting their yields and ensuring sustainable agricultural practices.
                            """.trimMargin()
                    ),
                    index = 0,
                    finish_reason = "length"
                )
            )
        )
    }
}
