package com.example.farmhand.module_Reco.API

import com.example.farmhand.module_Reco.API.data.Choice
import com.example.farmhand.module_Reco.API.data.Message
import com.example.farmhand.module_Reco.API.data.OpenAiRequest
import com.example.farmhand.module_Reco.API.data.OpenAiResponse

class MockOpenAiService {
    class MockOpenAiApiService : OpenAiApiService {
        override suspend fun createCompletion(
            authorization: String,
            request: OpenAiRequest
        ): OpenAiResponse {
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
                            content = "Here are the recommended control measures for your crop and pest based on the weather conditions."
                        ),
                        index = 0,
                        finish_reason = "length"
                    )
                )
            )
        }
    }
}