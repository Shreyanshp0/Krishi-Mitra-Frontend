package com.example.krishimitra.domain.usecase

import com.example.krishimitra.domain.repository.CropRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val repository: CropRepository
) {
    suspend operator fun invoke() = repository.clearHistory()
}
