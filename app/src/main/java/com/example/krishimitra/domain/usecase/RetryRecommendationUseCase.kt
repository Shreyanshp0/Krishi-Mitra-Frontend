package com.example.krishimitra.domain.usecase

import com.example.krishimitra.domain.repository.CropRepository

class RetryRecommendationUseCase(
    private val repository: CropRepository
) {
    suspend operator fun invoke() = repository.retryLastRequest()
}

