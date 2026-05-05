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
import com.example.krishimitra.presentation.viewmodel.CropUiState
import com.example.krishimitra.presentation.viewmodel.InputFormState
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.SoilBrown
import com.example.krishimitra.ui.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFormScreen(
    formState: InputFormState,
    uiState: CropUiState,
    onSeasonChange: (String) -> Unit,
    onSoilTypeChange: (String) -> Unit,
    onSoilFertilityChange: (String) -> Unit,
    onWaterAvailabilityChange: (String) -> Unit,
    onIrrigationSourceChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onPreviousCropChange: (String) -> Unit,
    onFarmSizeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onConsumeError: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is CropUiState.Error) {
            snackbarHostState.showSnackbar(uiState.message)
            onConsumeError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                .padding(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.CARD_SPACING)
        ) {
            // Section 1: Farming Basics
            item {
                FormSection(title = "📍 Section 1: Farming Basics") {
                    FarmerDropdown(
                        label = "Season (Required)",
                        options = listOf("Kharif", "Rabi", "Zaid", "Annual"),
                        selectedOption = formState.season,
                        onOptionSelected = onSeasonChange
                    )
                    Spacer(modifier = Modifier.height(Dimensions.ITEM_SPACING))
                    FarmerDropdown(
                        label = "Farm Size (Required)",
                        options = listOf("Small", "Medium", "Large"),
                        selectedOption = formState.farmSize,
                        onOptionSelected = onFarmSizeChange
                    )
                }
            }

            // Section 2: Soil Details
            item {
                FormSection(title = "🌱 Section 2: Soil Details") {
                    FarmerDropdown(
                        label = "Soil Type (Required)",
                        options = listOf("Alluvial", "Black", "Red", "Laterite", "Arid", "Forest", "Other"),
                        selectedOption = formState.soilType,
                        onOptionSelected = onSoilTypeChange
                    )
                    Spacer(modifier = Modifier.height(Dimensions.ITEM_SPACING))
                    FarmerDropdown(
                        label = "Soil Fertility (Required)",
                        options = listOf("Low", "Medium", "High"),
                        selectedOption = formState.soilFertility,
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
                    Spacer(modifier = Modifier.height(Dimensions.SMALL))
                    WaterSelectionRow(
                        selectedLevel = formState.waterAvailability,
                        onLevelSelected = onWaterAvailabilityChange
                    )
                    Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                    FarmerDropdown(
                        label = "Irrigation Source (Required)",
                        options = listOf("Tube Well", "Rainfed", "Canal", "Tank", "Other"),
                        selectedOption = formState.irrigationSource,
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
                    Spacer(modifier = Modifier.height(Dimensions.SMALL))
                    PrioritySelectionGrid(
                        selectedPriority = formState.priority,
                        onPrioritySelected = onPriorityChange
                    )
                    Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                    OutlinedTextField(
                        value = formState.previousCrop,
                        onValueChange = onPreviousCropChange,
                        label = { Text("Previous Crop (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM)
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = onSubmit,
                    enabled = formState.isValid && uiState !is CropUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.BUTTON_HEIGHT),
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepGreen,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    if (uiState is CropUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(Dimensions.ICON_SIZE_MEDIUM))
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
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.MEDIUM)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = DeepGreen,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Dimensions.ITEM_SPACING))
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
                .menuAnchor(),
            shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM)
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
    val levels = listOf("Low", "Medium", "High")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
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
    val priorities = listOf("Profit", "Yield", "Low Maintenance", "Drought Resistant")
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)) {
        priorities.forEach { priority ->
            val isSelected = selectedPriority == priority
            OutlinedCard(
                onClick = { onPrioritySelected(priority) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                border = BorderStroke(
                    width = if (isSelected) Dimensions.BORDER_WIDTH_MEDIUM else Dimensions.BORDER_WIDTH_THIN,
                    color = if (isSelected) DeepGreen else Color.LightGray
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) DeepGreen.copy(alpha = 0.1f) else Color.White
                )
            ) {
                Box(modifier = Modifier.padding(Dimensions.MEDIUM), contentAlignment = Alignment.CenterStart) {
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
        formState = InputFormState(),
        uiState = CropUiState.Idle,
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
