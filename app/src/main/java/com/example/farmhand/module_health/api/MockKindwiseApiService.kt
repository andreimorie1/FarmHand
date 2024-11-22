package com.example.farmhand.module_health.api

import com.example.farmhand.module_health.api.data.post_identification.actual.ActualIdentificationResponse
import com.example.farmhand.module_health.api.data.post_identification.actual.CreateidentificationRequest
import com.example.farmhand.module_health.api.data.post_identification.actual.Crop
import com.example.farmhand.module_health.api.data.post_identification.actual.Details
import com.example.farmhand.module_health.api.data.post_identification.actual.Disease
import com.example.farmhand.module_health.api.data.post_identification.actual.Input
import com.example.farmhand.module_health.api.data.post_identification.actual.IsPlant
import com.example.farmhand.module_health.api.data.post_identification.actual.Result
import com.example.farmhand.module_health.api.data.post_identification.actual.SimilarImage
import com.example.farmhand.module_health.api.data.post_identification.actual.SimilarImageX
import com.example.farmhand.module_health.api.data.post_identification.actual.Suggestion
import com.example.farmhand.module_health.api.data.post_identification.actual.SuggestionX

class MockKindwiseApiService : KindwiseApiService {

    override suspend fun createIdentification(
        apiKey: String,
        request: CreateidentificationRequest
    ): ActualIdentificationResponse {
        // Return a mock response with rice crop details
        return ActualIdentificationResponse(
            access_token = "rhprtaSkMQP1Zll",
            completed = 1709893633.628719,
            created = 1709893632.887534,
            custom_id = null,
            input = Input(
                latitude = 49.207,
                longitude = 16.608,
                similar_images = true,
                images = listOf("https://crop.kindwise.com/media/images/c10151ec4c2446e89cb40309667265ce.jpg"),
                datetime = "2024-03-08T10:27:12.887534+00:00"
            ),
            model_version = "crop_health:1.1.1",
            result = Result(
                is_plant = IsPlant(binary = true, probability = 99.999000, threshold = 0.5),
                disease = Disease(
                    suggestions = listOf(
                        SuggestionX(
                            id = "b9ec757fefb92520",
                            name = "late blight",
                            probability = 0.980698,
                            scientific_name = "Phytophthora infestans",
                            similar_images = listOf(
                                SimilarImageX(
                                    id = "100ac65780f4761e7bfd42b8db30fd1ec36fc5e8",
                                    url = "https://crop-health.ams3.cdn.digitaloceanspaces.com/similar_images/1/100/ac65780f4761e7bfd42b8db30fd1ec36fc5e8.jpg",
                                    url_small = "https://crop-health.ams3.cdn.digitaloceanspaces.com/similar_images/1/100/ac65780f4761e7bfd42b8db30fd1ec36fc5e8.small.jpg",
                                    similarity = 0.789,
                                    license_name = "CC BY 3.0",
                                    license_url = "https://creativecommons.org/licenses/by/3.0/",
                                    citation = "Howard F. Schwartz, Colorado State University"
                                ),
                                SimilarImageX(
                                    id = "111fe91bae90b085eff701f6d57cfba7cebc8412",
                                    url = "https://crop-health.ams3.cdn.digitaloceanspaces.com/similar_images/1/111/fe91bae90b085eff701f6d57cfba7cebc8412.jpg",
                                    url_small = "https://crop-health.ams3.cdn.digitaloceanspaces.com/similar_images/1/111/fe91bae90b085eff701f6d57cfba7cebc8412.small.jpg",
                                    similarity = 0.756,
                                    license_name = "ac65780f4761e7bfd42b8db30fd1ec36fc5e8.jpg",
                                    license_url = "https://creativecommons.org/licenses/by/3.0/",
                                    citation = "Howard F. Schwartz, Colorado State University"
                                )

                            ),
                            details = Details(language = "en", entity_id = "b9ec757fefb92520")
                        )
                    )
                ),
                crop = Crop(
                    suggestions = listOf(
                        Suggestion(
                            id = "2746768c8d99bfbb",
                            name = "potato",
                            probability = 0.8785,
                            scientific_name = "Solanum tuberosum",
                            similar_images = listOf(
                                SimilarImage(
                                    id = "9b273d5f693bb69b892c0c3b6a30ac2e6758815d",
                                    url = "https://crop-health.ams3.cdn.digitaloceanspaces.com/similar_images/1/9b2/73d5f693bb69b892c0c3b6a30ac2e6758815d.jpeg",
                                    url_small = "https://crop-health.ams3.cdn.digitaloceanspaces.com/similar_images/1/9b2/73d5f693bb69b892c0c3b6a30ac2e6758815d.small.jpeg",
                                    similarity = 0.693
                                )
                            ),
                            details = Details(language = "en", entity_id = "2746768c8d99bfbb")
                        )
                    )
                )
            ),
            sla_compliant_client = true,
            sla_compliant_system = true,
            status = "COMPLETED"
        )
    }


    override suspend fun getIdentification(accessToken: String): ActualIdentificationResponse {
        // Return a realistic rice-related identification result
        return ActualIdentificationResponse(
            access_token = accessToken,
            completed = 1.0,
            created = System.currentTimeMillis() / 1000.0,
            custom_id = null,
            input = Input(
                datetime = "2024-11-05T12:00:00Z",
                images = listOf("http://example.com/mock-rice-image.jpg"),
                latitude = 13.4,
                longitude = 121.5,
                similar_images = true
            ),
            model_version = "1.0",
            result = Result(
                crop = Crop(
                    suggestions = listOf(
                        Suggestion(
                            details = Details("rice_entity_id", "en"),
                            id = "rice_suggestion_id",
                            name = "Rice",
                            probability = 0.95,
                            scientific_name = "Oryza sativa",
                            similar_images = listOf(
                                SimilarImage(
                                    id = "img_123",
                                    similarity = 0.88,
                                    url = "http://example.com/mock-rice-image.jpg",
                                    url_small = "http://example.com/mock-rice-image-small.jpg"
                                )
                            )
                        )
                    )
                ),
                disease = Disease(
                    suggestions = listOf(
                        SuggestionX(
                            details = Details("sheath_blight_entity_id", "en"),
                            id = "sheath_blight_suggestion_id",
                            name = "Sheath Blight",
                            probability = 0.82,
                            scientific_name = "Rhizoctonia solani",
                            similar_images = listOf(
                                SimilarImageX(
                                    citation = "Sample Citation for Sheath Blight",
                                    id = "img_987",
                                    license_name = "Creative Commons",
                                    license_url = "https://creativecommons.org/licenses/by/4.0/",
                                    similarity = 0.88,
                                    url = "http://example.com/mock-sheath-blight.jpg",
                                    url_small = "http://example.com/mock-sheath-blight-small.jpg"
                                )
                            )
                        )
                    )
                ),
                is_plant = IsPlant(binary = true, probability = 94.00, threshold = 0.9)
            ),
            sla_compliant_client = true,
            sla_compliant_system = true,
            status = "completed"
        )
    }
}

