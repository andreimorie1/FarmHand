package com.example.farmhand.module_Reco.API

import com.example.farmhand.module_Reco.API.data.Choice
import com.example.farmhand.module_Reco.API.data.OpenAiRequest
import com.example.farmhand.module_Reco.API.data.OpenAiResponse

class MockOpenAiService {
    class MockOpenAiApiService : OpenAiApiService {
        override suspend fun createCompletion(
            authorization: String,
            request: OpenAiRequest
        ): OpenAiResponse {
            // Mocking a response for testing purposes
            return OpenAiResponse(
                id = "mock-id-123",
                objectType = "text_completion",
                created = System.currentTimeMillis() / 1000,
                model = request.model,
                choices = listOf(
                    Choice(
                        text = "This is a mocked response based on your prompt: ${request.prompt}",
                        index = 0,
                        logprobs = null,
                        finish_reason = "length"
                    )
                )
            )
        }
    }
}