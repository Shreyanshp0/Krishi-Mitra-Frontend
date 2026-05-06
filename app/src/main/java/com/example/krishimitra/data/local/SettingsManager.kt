package com.example.krishimitra.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language_code")
    }

    val languageCode: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en"
    }

    suspend fun saveLanguage(langCode: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = langCode
        }
    }
}
