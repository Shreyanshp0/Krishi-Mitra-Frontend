package com.example.krishimitra.data.dto

import com.google.gson.annotations.SerializedName

data class RecommendResponseDto(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: RecommendationDataDto? = null
)

data class RecommendationDataDto(
    @SerializedName("recommendations") val recommendations: List<CropRecommendationDto>? = null
)

data class CropRecommendationDto(
    @SerializedName("crop") val crop: String = "",
    @SerializedName("reason") val reason: String = "",
    @SerializedName("confidence") val confidence: String = "" // Backend sends confidence as string
)
