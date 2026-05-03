package com.example.krishimitra.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InputFormData(
    val season: String = "",
    val soilType: String = "",
    val soilFertility: String = "",
    val waterAvailability: String = "",
    val irrigationSource: String = "",
    val priority: String = "",
    val previousCrop: String = "",
    val farmSize: String = ""
)

data class InputFormUiState(
    val inputData: InputFormData = InputFormData(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean = with(inputData) {
        season.isNotEmpty() &&
        soilType.isNotEmpty() &&
        soilFertility.isNotEmpty() &&
        waterAvailability.isNotEmpty() &&
        irrigationSource.isNotEmpty() &&
        priority.isNotEmpty()
    }
}

class InputFormViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InputFormUiState())
    val uiState: StateFlow<InputFormUiState> = _uiState.asStateFlow()

    fun onSeasonSelected(value: String) = updateData { it.copy(season = value) }
    fun onSoilTypeSelected(value: String) = updateData { it.copy(soilType = value) }
    fun onSoilFertilitySelected(value: String) = updateData { it.copy(soilFertility = value) }
    fun onWaterAvailabilitySelected(value: String) = updateData { it.copy(waterAvailability = value) }
    fun onIrrigationSourceSelected(value: String) = updateData { it.copy(irrigationSource = value) }
    fun onPrioritySelected(value: String) = updateData { it.copy(priority = value) }
    fun onPreviousCropChanged(value: String) = updateData { it.copy(previousCrop = value) }
    fun onFarmSizeSelected(value: String) = updateData { it.copy(farmSize = value) }

    private fun updateData(transform: (InputFormData) -> InputFormData) {
        _uiState.update { it.copy(inputData = transform(it.inputData)) }
    }

    fun submit(onSuccess: (InputFormData) -> Unit) {
        if (!_uiState.value.isValid) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate API logic
            delay(2000)
            _uiState.update { it.copy(isLoading = false) }
            onSuccess(_uiState.value.inputData)
        }
    }
}
