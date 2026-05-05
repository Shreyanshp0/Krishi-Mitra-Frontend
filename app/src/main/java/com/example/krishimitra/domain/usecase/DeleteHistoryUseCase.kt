package com.example.krishimitra.domain.usecase

import com.example.krishimitra.domain.repository.CropRepository
import javax.inject.Inject

class DeleteHistoryUseCase @Inject constructor(
    private val repository: CropRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteHistoryItem(id)
}
