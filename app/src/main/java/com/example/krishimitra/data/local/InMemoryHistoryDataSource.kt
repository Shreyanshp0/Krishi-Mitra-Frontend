package com.example.krishimitra.data.local

import com.example.krishimitra.domain.model.RecommendationHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryHistoryDataSource {
    private val historyState = MutableStateFlow<List<RecommendationHistoryItem>>(emptyList())

    fun getHistory(): StateFlow<List<RecommendationHistoryItem>> = historyState.asStateFlow()

    fun save(item: RecommendationHistoryItem) {
        historyState.value = listOf(item) + historyState.value
    }
}

