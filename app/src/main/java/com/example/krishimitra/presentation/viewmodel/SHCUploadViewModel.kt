package com.example.krishimitra.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Data model for extracted soil components.
 */
data class ExtractedSoilData(
    val nitrogen: String = "",
    val phosphorus: String = "",
    val potassium: String = "",
    val ph: String = ""
)

/**
 * UI States for the Soil Health Card Upload module.
 */
sealed interface SHCUploadUiState {
    data object Idle : SHCUploadUiState
    data class FileSelected(val fileName: String, val uri: Uri) : SHCUploadUiState
    data object Processing : SHCUploadUiState
    data class Success(val data: ExtractedSoilData) : SHCUploadUiState
    data class Error(val message: String) : SHCUploadUiState
}

class SHCUploadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SHCUploadUiState>(SHCUploadUiState.Idle)
    val uiState: StateFlow<SHCUploadUiState> = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri, fileName: String) {
        _uiState.value = SHCUploadUiState.FileSelected(fileName, uri)
    }

    fun removeFile() {
        _uiState.value = SHCUploadUiState.Idle
    }

    fun analyzeSoilData() {
        val currentState = _uiState.value
        if (currentState is SHCUploadUiState.FileSelected) {
            viewModelScope.launch {
                _uiState.value = SHCUploadUiState.Processing
                
                // Simulate OCR extraction delay
                delay(2500)
                
                // Simulate successful extraction with realistic data
                _uiState.value = SHCUploadUiState.Success(
                    ExtractedSoilData(
                        nitrogen = "Medium",
                        phosphorus = "Low",
                        potassium = "High",
                        ph = "6.8 (Neutral)"
                    )
                )
            }
        }
    }

    fun retry() {
        _uiState.value = SHCUploadUiState.Idle
    }

    fun saveSoilData() {
        viewModelScope.launch {
            // Logic to persist data would go here
            _uiState.value = SHCUploadUiState.Idle
        }
    }
}
