package com.example.krishimitra.domain.model

data class RecommendationHistoryItem(
    val id: Long,
    val input: CropInput,
    val recommendations: List<CropRecommendation>,
    val createdAtMillis: Long
)

