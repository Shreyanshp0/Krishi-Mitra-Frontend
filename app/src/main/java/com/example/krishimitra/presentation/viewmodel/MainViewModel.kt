package com.example.krishimitra.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.data.local.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val languageCode: StateFlow<String> = settingsManager.languageCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun updateLanguage(langCode: String) {
        viewModelScope.launch {
            settingsManager.saveLanguage(langCode)
        }
    }
}
