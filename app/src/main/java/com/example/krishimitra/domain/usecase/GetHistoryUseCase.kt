package com.example.krishimitra.domain.usecase

import com.example.krishimitra.domain.repository.CropRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: CropRepository
) {
    operator fun invoke() = repository.getHistory()
}

