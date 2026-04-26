package com.example.krishimitra.data.mapper

import com.example.krishimitra.data.dto.CropRecommendationDto
import com.example.krishimitra.data.dto.RecommendRequestDto
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.CropRecommendation

fun CropInput.toDto(): RecommendRequestDto = RecommendRequestDto(
    soilType = soilType,
    season = season,
    temperature = temperature,
    rainfall = rainfall,
    nitrogen = nitrogen,
    phosphorus = phosphorus,
    potassium = potassium
)

fun CropRecommendationDto.toDomain(): CropRecommendation = CropRecommendation(
    cropName = cropName,
    reason = reason,
    confidence = confidence
)

