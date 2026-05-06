package com.example.krishimitra.data.repository

import com.example.krishimitra.data.network.api.RecommendApi
import com.example.krishimitra.data.local.HistoryManager
import com.example.krishimitra.data.mapper.toDomain
import com.example.krishimitra.data.mapper.toDto
import com.example.krishimitra.data.network.NetworkMonitor
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.DomainResult
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.example.krishimitra.domain.model.RecommendationResult
import com.example.krishimitra.domain.repository.CropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CropRepositoryImpl(
    private val api: RecommendApi,
    private val historyManager: HistoryManager,
    private val networkMonitor: NetworkMonitor
) : CropRepository {

    private var lastInput: CropInput? = null
    private var cachedResult: RecommendationResult? = null
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private var lastLanguage: String = "en"

    override suspend fun getRecommendations(input: CropInput, language: String): DomainResult<RecommendationResult> {
        lastInput = input
        lastLanguage = language

        if (!networkMonitor.isOnline()) {
            return cachedResult?.let {
                DomainResult.Success(it.copy(isOffline = true))
            } ?: DomainResult.Error("No internet connection. Please connect and retry.", isOffline = true)
        }

        return try {
            val response = api.getRecommendations(input.toDto().copy(language = language))
            
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

    override fun getHistory(): Flow<List<RecommendationHistoryItem>> = historyManager.history

    override suspend fun retryLastRequest(): DomainResult<RecommendationResult> {
        val input = lastInput ?: return DomainResult.Error("No previous request to retry.")
        return getRecommendations(input, lastLanguage)
    }

    override suspend fun deleteHistoryItem(id: Long) {
        historyManager.deleteHistoryItem(id)
    }

    override suspend fun clearHistory() {
        historyManager.clearHistory()
    }

    private fun saveHistory(input: CropInput, recommendations: List<com.example.krishimitra.domain.model.CropRecommendation>) {
        repositoryScope.launch {
            historyManager.saveHistoryItem(
                RecommendationHistoryItem(
                    id = System.currentTimeMillis(),
                    input = input,
                    recommendations = recommendations,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
        }
    }
}
