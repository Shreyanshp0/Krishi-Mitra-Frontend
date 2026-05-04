package com.example.krishimitra.data.mapper

import com.example.krishimitra.data.dto.CropRecommendationDto
import com.example.krishimitra.data.dto.LocationDto
import com.example.krishimitra.data.dto.RecommendRequestDto
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.CropRecommendation

fun CropInput.toDto(): RecommendRequestDto {
    val soilData = if (mode == "shc") {
        val map = mutableMapOf<String, Any?>()
        nitrogen?.let { map["nitrogen"] = it }
        phosphorus?.let { map["phosphorus"] = it }
        potassium?.let { map["potassium"] = it }
        ph?.let { map["ph"] = it }
        if (soilType.isNotBlank()) map["soilType"] = soilType
        map.putAll(additionalInputs)
        if (map.isNotEmpty()) map else null
    } else null

    val inputs = if (mode == "manual") {
        val map = mutableMapOf<String, Any?>()
        map["season"] = season
        map["soilType"] = soilType
        
        // Send both versions of keys to ensure backend compatibility
        val fertility = (additionalInputs["fertility"] as? String) ?: ""
        map["soilFertility"] = fertility
        map["fertility"] = fertility
        
        map["waterAvailability"] = (additionalInputs["waterAvailability"] as? String) ?: ""
        
        val irrigation = (additionalInputs["irrigationSource"] as? String) ?: ""
        map["irrigationSource"] = irrigation
        map["irrigation"] = irrigation
        
        val priority = (additionalInputs["farmerPriority"] as? String) ?: ""
        map["farmerPriority"] = priority
        map["priority"] = priority
        
        map["farmSize"] = additionalInputs["farmSize"]
        map["previousCrop"] = additionalInputs["previousCrop"]
        
        map
    } else null

    return RecommendRequestDto(
        mode = mode,
        location = LocationDto(state = state, district = district),
        soilData = soilData,
        inputs = inputs
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
