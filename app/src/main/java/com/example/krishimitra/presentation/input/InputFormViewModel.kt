package com.example.krishimitra.presentation.input

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class InputFormState(
    val season: String = "",
    val soilType: String = "",
    val soilFertility: String = "",
    val waterAvailability: String = "",
    val irrigationSource: String = "",
    val priority: String = "",
    val previousCrop: String = "",
    val farmSize: String = "",
    val isLoading: Boolean = false
) {
    val isFormValid: Boolean
        get() = season.isNotEmpty() &&
                soilType.isNotEmpty() &&
                soilFertility.isNotEmpty() &&
                waterAvailability.isNotEmpty() &&
                irrigationSource.isNotEmpty() &&
                priority.isNotEmpty()
}

class InputFormViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InputFormState())
    val uiState: StateFlow<InputFormState> = _uiState.asStateFlow()

    fun updateSeason(value: String) = _uiState.update { it.copy(season = value) }
    fun updateSoilType(value: String) = _uiState.update { it.copy(soilType = value) }
    fun updateSoilFertility(value: String) = _uiState.update { it.copy(soilFertility = value) }
    fun updateWaterAvailability(value: String) = _uiState.update { it.copy(waterAvailability = value) }
    fun updateIrrigationSource(value: String) = _uiState.update { it.copy(irrigationSource = value) }
    fun updatePriority(value: String) = _uiState.update { it.copy(priority = value) }
    fun updatePreviousCrop(value: String) = _uiState.update { it.copy(previousCrop = value) }
    fun updateFarmSize(value: String) = _uiState.update { it.copy(farmSize = value) }

    fun submitForm(onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        // Trigger API logic here
        onSuccess()
    }
}
