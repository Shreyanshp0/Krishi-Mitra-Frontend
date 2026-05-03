package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.presentation.viewmodel.InputFormState
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.SoilBrown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFormScreen(
    uiState: InputFormState,
    onSeasonChange: (String) -> Unit,
    onSoilTypeChange: (String) -> Unit,
    onSoilFertilityChange: (String) -> Unit,
    onWaterAvailabilityChange: (String) -> Unit,
    onIrrigationSourceChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onPreviousCropChange: (String) -> Unit,
    onFarmSizeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Input Form", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepGreen)
            )
        },
        containerColor = LightBeige
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Farming Basics
            item {
                FormSection(title = "📍 Section 1: Farming Basics") {
                    FarmerDropdown(
                        label = "Season (Required)",
                        options = listOf("Kharif", "Rabi", "Zaid"),
                        selectedOption = uiState.season,
                        onOptionSelected = onSeasonChange
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FarmerDropdown(
                        label = "Farm Size (Optional)",
                        options = listOf("Small", "Medium", "Large"),
                        selectedOption = uiState.farmSize,
                        onOptionSelected = onFarmSizeChange
                    )
                }
            }

            // Section 2: Soil Details
            item {
                FormSection(title = "🌱 Section 2: Soil Details") {
                    FarmerDropdown(
                        label = "Soil Type (Required)",
                        options = listOf("Sandy", "Clay", "Loamy", "Black", "Red"),
                        selectedOption = uiState.soilType,
                        onOptionSelected = onSoilTypeChange
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FarmerDropdown(
                        label = "Soil Fertility (Required)",
                        options = listOf("Low", "Medium", "High"),
                        selectedOption = uiState.soilFertility,
                        onOptionSelected = onSoilFertilityChange
                    )
                }
            }

            // Section 3: Water & Irrigation
            item {
                FormSection(title = "💧 Section 3: Water & Irrigation") {
                    Text(
                        "Water Availability (Required)",
                        style = MaterialTheme.typography.labelLarge,
                        color = SoilBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    WaterSelectionRow(
                        selectedLevel = uiState.waterAvailability,
                        onLevelSelected = onWaterAvailabilityChange
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FarmerDropdown(
                        label = "Irrigation Source (Required)",
                        options = listOf("Rain-fed", "Borewell", "Canal", "River"),
                        selectedOption = uiState.irrigationSource,
                        onOptionSelected = onIrrigationSourceChange
                    )
                }
            }

            // Section 4: Preferences
            item {
                FormSection(title = "🎯 Section 4: Preferences") {
                    Text(
                        "Farmer Priority (Required)",
                        style = MaterialTheme.typography.labelLarge,
                        color = SoilBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PrioritySelectionGrid(
                        selectedPriority = uiState.priority,
                        onPrioritySelected = onPriorityChange
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.previousCrop,
                        onValueChange = onPreviousCropChange,
                        label = { Text("Previous Crop (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = onSubmit,
                    enabled = uiState.isValid && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepGreen,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Get Recommendation 🌾", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = DeepGreen,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WaterSelectionRow(selectedLevel: String, onLevelSelected: (String) -> Unit) {
    val levels = listOf("Low 💧", "Medium 💧💧", "High 💧💧💧")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        levels.forEach { level ->
            val isSelected = selectedLevel == level
            FilterChip(
                selected = isSelected,
                onClick = { onLevelSelected(level) },
                label = { Text(level) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DeepGreen,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun PrioritySelectionGrid(selectedPriority: String, onPrioritySelected: (String) -> Unit) {
    val priorities = listOf("High Profit 💰", "Low Risk 🛡️", "Fast Growth ⚡")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        priorities.forEach { priority ->
            val isSelected = selectedPriority == priority
            OutlinedCard(
                onClick = { onPrioritySelected(priority) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) DeepGreen else Color.LightGray
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) DeepGreen.copy(alpha = 0.1f) else Color.White
                )
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.CenterStart) {
                    Text(priority, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputFormScreenPreview() {
    InputFormScreen(
        uiState = InputFormState(),
        onSeasonChange = {},
        onSoilTypeChange = {},
        onSoilFertilityChange = {},
        onWaterAvailabilityChange = {},
        onIrrigationSourceChange = {},
        onPriorityChange = {},
        onPreviousCropChange = {},
        onFarmSizeChange = {},
        onSubmit = {},
        onBack = {}
    )
}
