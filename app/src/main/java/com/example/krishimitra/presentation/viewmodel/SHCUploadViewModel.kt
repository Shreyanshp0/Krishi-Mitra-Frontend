package com.example.krishimitra.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.domain.repository.ShcRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

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

@HiltViewModel
class SHCUploadViewModel @Inject constructor(
    private val shcRepository: ShcRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

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
                
                try {
                    val file = uriToFile(context, currentState.uri, currentState.fileName)
                    val result = shcRepository.uploadShc(file)
                    
                    result.onSuccess { data ->
                        _uiState.value = SHCUploadUiState.Success(
                            ExtractedSoilData(
                                nitrogen = data["nitrogen"]?.toString() ?: "",
                                phosphorus = data["phosphorus"]?.toString() ?: "",
                                potassium = data["potassium"]?.toString() ?: "",
                                ph = data["ph"]?.toString() ?: ""
                            )
                        )
                    }.onFailure {
                        _uiState.value = SHCUploadUiState.Error(it.message ?: "Upload failed")
                    }
                } catch (e: Exception) {
                    _uiState.value = SHCUploadUiState.Error("Failed to process file: ${e.message}")
                }
            }
        }
    }

    fun retry() {
        _uiState.value = SHCUploadUiState.Idle
    }

    fun saveSoilData() {
        // Logic to persist data is now handled by upload
        _uiState.value = SHCUploadUiState.Idle
    }

    private fun uriToFile(context: Context, uri: Uri, fileName: String): File {
        val tempFile = File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}
