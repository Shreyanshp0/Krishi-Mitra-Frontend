package com.example.krishimitra.domain.usecase

import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.repository.CropRepository
import javax.inject.Inject

class GetCropRecommendationUseCase @Inject constructor(
    private val repository: CropRepository
) {
    suspend operator fun invoke(input: CropInput, language: String) = repository.getRecommendations(input, language)
}

