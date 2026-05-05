package com.example.krishimitra.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.historyDataStore: DataStore<Preferences> by preferencesDataStore(name = "history_prefs")

@Singleton
class HistoryManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    companion object {
        private val HISTORY_KEY = stringPreferencesKey("recommendation_history")
    }

    val history: Flow<List<RecommendationHistoryItem>> = context.historyDataStore.data.map { preferences ->
        val json = preferences[HISTORY_KEY] ?: "[]"
        val type = object : TypeToken<List<RecommendationHistoryItem>>() {}.type
        gson.fromJson<List<RecommendationHistoryItem>>(json, type) ?: emptyList()
    }

    suspend fun saveHistoryItem(item: RecommendationHistoryItem) {
        context.historyDataStore.edit { preferences ->
            val json = preferences[HISTORY_KEY] ?: "[]"
            val type = object : TypeToken<MutableList<RecommendationHistoryItem>>() {}.type
            val currentList: MutableList<RecommendationHistoryItem> = gson.fromJson(json, type) ?: mutableListOf()
            
            currentList.add(0, item) // Latest on top
            
            preferences[HISTORY_KEY] = gson.toJson(currentList)
        }
    }

    suspend fun deleteHistoryItem(id: Long) {
        context.historyDataStore.edit { preferences ->
            val json = preferences[HISTORY_KEY] ?: "[]"
            val type = object : TypeToken<MutableList<RecommendationHistoryItem>>() {}.type
            val currentList: MutableList<RecommendationHistoryItem> = gson.fromJson(json, type) ?: mutableListOf()
            
            val updatedList = currentList.filterNot { it.id == id }
            
            preferences[HISTORY_KEY] = gson.toJson(updatedList)
        }
    }

    suspend fun clearHistory() {
        context.historyDataStore.edit { preferences ->
            preferences[HISTORY_KEY] = "[]"
        }
    }
}
