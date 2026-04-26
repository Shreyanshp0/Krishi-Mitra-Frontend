package com.example.krishimitra.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.domain.model.CropInput
import com.example.krishimitra.domain.model.DomainResult
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.example.krishimitra.domain.model.RecommendationResult
import com.example.krishimitra.domain.usecase.GetCropRecommendationUseCase
import com.example.krishimitra.domain.usecase.GetHistoryUseCase
import com.example.krishimitra.domain.usecase.RetryRecommendationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class CropViewModel(
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

    fun updateSoilType(value: String) {
        _formState.value = _formState.value.copy(soilType = value, soilTypeError = null)
    }

    fun updateSeason(value: String) {
        _formState.value = _formState.value.copy(season = value, seasonError = null)
    }

    fun updateTemperature(value: String) {
        _formState.value = _formState.value.copy(temperature = value, temperatureError = null)
    }

    fun updateRainfall(value: String) {
        _formState.value = _formState.value.copy(rainfall = value, rainfallError = null)
    }

    fun updateNitrogen(value: String) {
        _formState.value = _formState.value.copy(nitrogen = value, nitrogenError = null)
    }

    fun updatePhosphorus(value: String) {
        _formState.value = _formState.value.copy(phosphorus = value, phosphorusError = null)
    }

    fun updatePotassium(value: String) {
        _formState.value = _formState.value.copy(potassium = value, potassiumError = null)
    }

    fun submitRecommendation(): Boolean {
        val form = _formState.value
        val validated = validateForm(form)
        if (validated.hasErrors) {
            _formState.value = validated
            return false
        }

        val input = CropInput(
            soilType = validated.soilType,
            season = validated.season,
            temperature = validated.temperature.toInt(),
            rainfall = validated.rainfall.toInt(),
            nitrogen = validated.nitrogen.toInt(),
            phosphorus = validated.phosphorus.toInt(),
            potassium = validated.potassium.toInt()
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

    private fun validateForm(state: InputFormState): InputFormState {
        fun validateNumber(value: String, label: String): String? {
            val number = value.toIntOrNull() ?: return "$label is required"
            return if (number < 0) "$label cannot be negative" else null
        }

        return state.copy(
            soilTypeError = if (state.soilType.isBlank()) "Select soil type" else null,
            seasonError = if (state.season.isBlank()) "Select season" else null,
            temperatureError = validateNumber(state.temperature, "Temperature"),
            rainfallError = validateNumber(state.rainfall, "Rainfall"),
            nitrogenError = validateNumber(state.nitrogen, "Nitrogen"),
            phosphorusError = validateNumber(state.phosphorus, "Phosphorus"),
            potassiumError = validateNumber(state.potassium, "Potassium")
        )
    }
}

class CropViewModelFactory(
    private val getCropRecommendationUseCase: GetCropRecommendationUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val retryRecommendationUseCase: RetryRecommendationUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CropViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CropViewModel(
                getCropRecommendationUseCase = getCropRecommendationUseCase,
                getHistoryUseCase = getHistoryUseCase,
                retryRecommendationUseCase = retryRecommendationUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

data class InputFormState(
    val soilType: String = "",
    val season: String = "",
    val temperature: String = "",
    val rainfall: String = "",
    val nitrogen: String = "",
    val phosphorus: String = "",
    val potassium: String = "",
    val soilTypeError: String? = null,
    val seasonError: String? = null,
    val temperatureError: String? = null,
    val rainfallError: String? = null,
    val nitrogenError: String? = null,
    val phosphorusError: String? = null,
    val potassiumError: String? = null
) {
    val hasErrors: Boolean
        get() = listOf(
            soilTypeError,
            seasonError,
            temperatureError,
            rainfallError,
            nitrogenError,
            phosphorusError,
            potassiumError
        ).any { it != null }
}

sealed interface CropUiState {
    data object Idle : CropUiState
    data object Loading : CropUiState
    data class Success(val data: RecommendationResult) : CropUiState
    data class Error(val message: String, val isOffline: Boolean) : CropUiState
}


