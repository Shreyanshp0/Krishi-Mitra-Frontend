package com.example.krishimitra.data.repository

import com.example.krishimitra.data.network.api.RecommendApi
import com.example.krishimitra.data.local.InMemoryHistoryDataSource
import com.example.krishimitra.data.mapper.toDomain
import com.example.krishimitra.data.mapper.toDto
import com.example.krishimitra.data.network.NetworkMonitor
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.DomainResult
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.example.krishimitra.domain.model.RecommendationResult
import com.example.krishimitra.domain.repository.CropRepository
import kotlinx.coroutines.flow.Flow

class CropRepositoryImpl(
    private val api: RecommendApi,
    private val historyDataSource: InMemoryHistoryDataSource,
    private val networkMonitor: NetworkMonitor
) : CropRepository {

    private var lastInput: CropInput? = null
    private var cachedResult: RecommendationResult? = null

    override suspend fun getRecommendations(input: CropInput): DomainResult<RecommendationResult> {
        lastInput = input

        if (!networkMonitor.isOnline()) {
            return cachedResult?.let {
                DomainResult.Success(it.copy(isOffline = true))
            } ?: DomainResult.Error("No internet connection. Please connect and retry.", isOffline = true)
        }

        return try {
            val response = api.getRecommendations(input.toDto())
            
            // Extract recommendations from the nested data structure
            val recommendationsList = response.data?.recommendations

            val recommendations = recommendationsList?.take(3)?.map { it.toDomain() } ?: emptyList()
            
            if (recommendations.isEmpty()) {
                val errorMsg = response.message ?: "No recommendation received from server."
                DomainResult.Error(errorMsg)
            } else {
                val result = RecommendationResult(
                    recommendations = recommendations,
                    isOffline = false
                )
                cachedResult = result
                saveHistory(input, recommendations)
                DomainResult.Success(result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DomainResult.Error(e.message ?: "Unexpected error occurred.")
        }
    }

    override fun getHistory(): Flow<List<RecommendationHistoryItem>> = historyDataSource.getHistory()

    override suspend fun retryLastRequest(): DomainResult<RecommendationResult> {
        val input = lastInput ?: return DomainResult.Error("No previous request to retry.")
        return getRecommendations(input)
    }

    private fun saveHistory(input: CropInput, recommendations: List<com.example.krishimitra.domain.model.CropRecommendation>) {
        historyDataSource.save(
            RecommendationHistoryItem(
                id = System.currentTimeMillis(),
                input = input,
                recommendations = recommendations,
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }
}
