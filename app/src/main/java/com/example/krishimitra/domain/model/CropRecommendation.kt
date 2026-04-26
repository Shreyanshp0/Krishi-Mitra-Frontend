package com.example.krishimitra.domain.model

data class CropRecommendation(
    val cropName: String,
    val reason: String,
    val confidence: Double
)

