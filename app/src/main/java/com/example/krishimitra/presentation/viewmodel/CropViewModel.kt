package com.example.krishimitra.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.DomainResult
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.example.krishimitra.domain.model.RecommendationResult
import com.example.krishimitra.domain.usecase.GetCropRecommendationUseCase
import com.example.krishimitra.domain.usecase.GetHistoryUseCase
import com.example.krishimitra.domain.usecase.RetryRecommendationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(
    private val getCropRecommendationUseCase: GetCropRecommendationUseCase,
    getHistoryUseCase: GetHistoryUseCase,
    private val retryRecommendationUseCase: RetryRecommendationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CropUiState>(CropUiState.Idle)
    val uiState: StateFlow<CropUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(InputFormState())
    val formState: StateFlow<InputFormState> = _formState.asStateFlow()

    val history: StateFlow<List<RecommendationHistoryItem>> = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateSeason(value: String) {
        _formState.value = _formState.value.copy(season = value)
    }

    fun updateFarmSize(value: String) {
        _formState.value = _formState.value.copy(farmSize = value)
    }

    fun updateSoilType(value: String) {
        _formState.value = _formState.value.copy(soilType = value)
    }

    fun updateSoilFertility(value: String) {
        _formState.value = _formState.value.copy(soilFertility = value)
    }

    fun updateWaterAvailability(value: String) {
        _formState.value = _formState.value.copy(waterAvailability = value)
    }

    fun updateIrrigationSource(value: String) {
        _formState.value = _formState.value.copy(irrigationSource = value)
    }

    fun updatePriority(value: String) {
        _formState.value = _formState.value.copy(priority = value)
    }

    fun updatePreviousCrop(value: String) {
        _formState.value = _formState.value.copy(previousCrop = value)
    }

    fun submitRecommendation(): Boolean {
        val form = _formState.value
        if (!form.isValid) return false

        val input = CropInput(
            soilType = form.soilType,
            season = form.season,
            temperature = 25, 
            rainfall = 100,   
            nitrogen = 50,    
            phosphorus = 50,
            potassium = 50
        )

        fetchRecommendation(input)
        return true
    }

    fun retry() {
        _uiState.value = CropUiState.Loading
        viewModelScope.launch {
            when (val result = retryRecommendationUseCase()) {
                is DomainResult.Success -> {
                    _uiState.value = CropUiState.Success(result.data)
                }
                is DomainResult.Error -> {
                    _uiState.value = CropUiState.Error(result.message, result.isOffline)
                }
            }
        }
    }

    fun clearResult() {
        _uiState.value = CropUiState.Idle
    }

    private fun fetchRecommendation(input: CropInput) {
        _uiState.value = CropUiState.Loading
        viewModelScope.launch {
            when (val result = getCropRecommendationUseCase(input)) {
                is DomainResult.Success -> {
                    _uiState.value = CropUiState.Success(result.data)
                }
                is DomainResult.Error -> {
                    _uiState.value = CropUiState.Error(result.message, result.isOffline)
                }
            }
        }
    }
}

data class InputFormState(
    val season: String = "",
    val farmSize: String = "",
    val soilType: String = "",
    val soilFertility: String = "",
    val waterAvailability: String = "",
    val irrigationSource: String = "",
    val priority: String = "",
    val previousCrop: String = "",
    val isLoading: Boolean = false
) {
    val isValid: Boolean
        get() = season.isNotBlank() &&
                soilType.isNotBlank() &&
                soilFertility.isNotBlank() &&
                waterAvailability.isNotBlank() &&
                irrigationSource.isNotBlank() &&
                priority.isNotBlank()
}

sealed interface CropUiState {
    data object Idle : CropUiState
    data object Loading : CropUiState
    data class Success(val data: RecommendationResult) : CropUiState
    data class Error(val message: String, val isOffline: Boolean) : CropUiState
}
