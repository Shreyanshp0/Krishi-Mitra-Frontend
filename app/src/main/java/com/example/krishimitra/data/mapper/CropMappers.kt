package com.example.krishimitra.data.mapper

import com.example.krishimitra.data.dto.CropRecommendationDto
import com.example.krishimitra.data.dto.RecommendRequestDto
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.CropRecommendation

fun CropInput.toDto(): RecommendRequestDto {
    return RecommendRequestDto(
        mode = mode,
        language = language,
        state = state,
        district = district,
        season = season,
        soilType = soilType,
        soilFertility = soilFertility,
        waterAvailability = waterAvailability,
        irrigationSource = irrigationSource,
        priority = priority,
        nitrogen = nitrogen,
        phosphorus = phosphorus,
        potassium = potassium,
        ph = ph
    )
}

fun CropRecommendationDto.toDomain(): CropRecommendation {
    // Handle confidence which can be "High" (String) or numeric
    val confidenceValue = when {
        confidence.isEmpty() -> 0.5
        confidence.equals("high", ignoreCase = true) -> 0.9
        confidence.equals("medium", ignoreCase = true) -> 0.7
        confidence.equals("low", ignoreCase = true) -> 0.4
        else -> {
            try {
                confidence.toDoubleOrNull() ?: 0.5
            } catch (e: Exception) {
                0.5
            }
        }
    }

    return CropRecommendation(
        cropName = crop,
        reason = reason,
        confidence = confidenceValue
    )
}
