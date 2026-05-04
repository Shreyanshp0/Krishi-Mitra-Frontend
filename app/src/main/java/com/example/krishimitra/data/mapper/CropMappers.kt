package com.example.krishimitra.data.mapper

import com.example.krishimitra.data.dto.CropRecommendationDto
import com.example.krishimitra.data.dto.LocationDto
import com.example.krishimitra.data.dto.RecommendRequestDto
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.CropRecommendation

fun CropInput.toDto(): RecommendRequestDto {
    val soilData = mutableMapOf<String, Any?>()
    nitrogen?.let { soilData["nitrogen"] = it }
    phosphorus?.let { soilData["phosphorus"] = it }
    potassium?.let { soilData["potassium"] = it }
    ph?.let { soilData["ph"] = it }
    soilType.takeIf { it.isNotBlank() }?.let { soilData["soilType"] = it }

    val inputs = mutableMapOf<String, Any?>()
    season.takeIf { it.isNotBlank() }?.let { inputs["season"] = it }
    temperature?.let { inputs["temperature"] = it }
    rainfall?.let { inputs["rainfall"] = it }
    inputs.putAll(additionalInputs)

    return RecommendRequestDto(
        mode = mode,
        location = LocationDto(state = state, district = district),
        soilData = if (soilData.isNotEmpty()) soilData else null,
        inputs = if (inputs.isNotEmpty()) inputs else null
    )
}

fun CropRecommendationDto.toDomain(): CropRecommendation = CropRecommendation(
    cropName = cropName,
    reason = reason,
    confidence = confidence
)
