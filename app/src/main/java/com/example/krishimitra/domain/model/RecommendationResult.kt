package com.example.krishimitra.domain.model

data class RecommendationResult(
    val recommendations: List<CropRecommendation>,
    val isOffline: Boolean
)

