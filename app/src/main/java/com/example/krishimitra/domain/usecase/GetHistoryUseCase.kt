package com.example.krishimitra.domain.usecase

import com.example.krishimitra.domain.repository.CropRepository

class GetHistoryUseCase(
    private val repository: CropRepository
) {
    operator fun invoke() = repository.getHistory()
}

