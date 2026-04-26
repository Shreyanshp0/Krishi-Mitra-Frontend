package com.example.krishimitra.domain.usecase

import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.repository.CropRepository

class GetCropRecommendationUseCase(
    private val repository: CropRepository
) {
    suspend operator fun invoke(input: CropInput) = repository.getRecommendations(input)
}

