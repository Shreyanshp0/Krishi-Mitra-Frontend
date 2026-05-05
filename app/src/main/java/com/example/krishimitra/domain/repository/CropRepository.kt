package com.example.krishimitra.domain.repository

import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.DomainResult
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.example.krishimitra.domain.model.RecommendationResult
import kotlinx.coroutines.flow.Flow

interface CropRepository {
    suspend fun getRecommendations(input: CropInput): DomainResult<RecommendationResult>
    fun getHistory(): Flow<List<RecommendationHistoryItem>>
    suspend fun retryLastRequest(): DomainResult<RecommendationResult>
    suspend fun deleteHistoryItem(id: Long)
    suspend fun clearHistory()
}

