package com.example.krishimitra.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.DomainResult
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.example.krishimitra.domain.model.RecommendationResult
import com.example.krishimitra.domain.usecase.GetCropRecommendationUseCase
import com.example.krishimitra.domain.usecase.GetHistoryUseCase
import com.example.krishimitra.domain.usecase.RetryRecommendationUseCase
import com.example.krishimitra.domain.usecase.DeleteHistoryUseCase
import com.example.krishimitra.domain.usecase.ClearHistoryUseCase
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
    private val retryRecommendationUseCase: RetryRecommendationUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CropUiState>(CropUiState.Idle)
    val uiState: StateFlow<CropUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(InputFormState())
    val formState: StateFlow<InputFormState> = _formState.asStateFlow()

    val history: StateFlow<List<RecommendationHistoryItem>> = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Location should be set before requesting recommendation
    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    fun setLocation(state: String, district: String) {
        _locationState.value = LocationState(state, district)
    }

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

        val location = _locationState.value
        if (location.state.isBlank() || location.district.isBlank()) {
            _uiState.value = CropUiState.Error("Please ensure location (state/district) is selected in profile or during signup", false)
            return false
        }

        val input = CropInput(
            mode = "manual",
            state = location.state,
            district = location.district,
            soilType = form.soilType,
            season = form.season,
            additionalInputs = mapOf(
                "fertility" to form.soilFertility,
                "waterAvailability" to form.waterAvailability,
                "irrigationSource" to form.irrigationSource,
                "farmSize" to form.farmSize,
                "farmerPriority" to form.priority,
                "previousCrop" to form.previousCrop
            )
        )

        fetchRecommendation(input)
        return true
    }

    fun submitShcRecommendation(soilData: Map<String, Any?>): Boolean {
        val location = _locationState.value
        if (location.state.isBlank() || location.district.isBlank()) {
            _uiState.value = CropUiState.Error("Location required for recommendation", false)
            return false
        }

        val input = CropInput(
            mode = "shc",
            state = location.state,
            district = location.district,
            additionalInputs = soilData
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

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            deleteHistoryUseCase(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }

    private fun fetchRecommendation(input: CropInput) {
        _uiState.value = CropUiState.Loading
        viewModelScope.launch {
            Log.d("API_REQUEST", "Sending recommendation request for mode: ${input.mode}")
            when (val result = getCropRecommendationUseCase(input)) {
                is DomainResult.Success -> {
                    Log.d("API_RESPONSE", "Recommendation received successfully")
                    _uiState.value = CropUiState.Success(result.data)
                }
                is DomainResult.Error -> {
                    Log.e("API_ERROR", "Failed to fetch recommendation: ${result.message}")
                    _uiState.value = CropUiState.Error(result.message, result.isOffline)
                }
            }
        }
    }
}

data class LocationState(
    val state: String = "",
    val district: String = ""
)

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
                priority.isNotBlank() &&
                farmSize.isNotBlank()
}

sealed interface CropUiState {
    data object Idle : CropUiState
    data object Loading : CropUiState
    data class Success(val data: RecommendationResult) : CropUiState
    data class Error(val message: String, val isOffline: Boolean) : CropUiState
}
