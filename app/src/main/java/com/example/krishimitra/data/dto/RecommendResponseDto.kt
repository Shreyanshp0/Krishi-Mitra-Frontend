package com.example.krishimitra.data.dto

import com.google.gson.annotations.SerializedName

data class RecommendResponseDto(
    @SerializedName("recommendations") val recommendations: List<CropRecommendationDto> = emptyList()
)

data class CropRecommendationDto(
    @SerializedName("crop") val cropName: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("confidence") val confidence: Double
)

