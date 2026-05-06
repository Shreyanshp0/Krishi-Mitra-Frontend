package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
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

    val seasons = listOf(
        "Kharif" to R.string.opt_kharif,
        "Rabi" to R.string.opt_rabi,
        "Zaid" to R.string.opt_zaid,
        "Annual" to R.string.opt_annual
    )
    val farmSizes = listOf(
        "Small" to R.string.opt_small,
        "Medium" to R.string.opt_medium,
        "Large" to R.string.opt_large
    )
    val soilTypes = listOf(
        "Alluvial" to R.string.opt_alluvial,
        "Black" to R.string.opt_black,
        "Red" to R.string.opt_red,
        "Laterite" to R.string.opt_laterite,
        "Arid" to R.string.opt_arid,
        "Forest" to R.string.opt_forest,
        "Other" to R.string.opt_other
    )
    val fertilityLevels = listOf(
        "Low" to R.string.opt_low,
        "Medium" to R.string.opt_medium,
        "High" to R.string.opt_high
    )
    val irrigationSources = listOf(
        "Tube Well" to R.string.opt_tube_well,
        "Rainfed" to R.string.opt_rainfed,
        "Canal" to R.string.opt_canal,
        "Tank" to R.string.opt_tank,
        "Other" to R.string.opt_other
    )
    val priorities = listOf(
        "Profit" to R.string.opt_profit,
        "Yield" to R.string.opt_yield,
        "Low Maintenance" to R.string.opt_low_maint,
        "Drought Resistant" to R.string.opt_drought_res
    )

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
                title = { Text(stringResource(R.string.form_title), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_content_desc), tint = Color.White)
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
                FormSection(title = stringResource(R.string.section_basics)) {
                    FarmerDropdown(
                        label = stringResource(R.string.season_label),
                        options = seasons,
                        selectedKey = formState.season,
                        onOptionSelected = onSeasonChange
                    )
                    Spacer(modifier = Modifier.height(Dimensions.ITEM_SPACING))
                    FarmerDropdown(
                        label = stringResource(R.string.farm_size_label),
                        options = farmSizes,
                        selectedKey = formState.farmSize,
                        onOptionSelected = onFarmSizeChange
                    )
                }
            }

            // Section 2: Soil Details
            item {
                FormSection(title = stringResource(R.string.section_soil)) {
                    FarmerDropdown(
                        label = stringResource(R.string.soil_type_label),
                        options = soilTypes,
                        selectedKey = formState.soilType,
                        onOptionSelected = onSoilTypeChange
                    )
                    Spacer(modifier = Modifier.height(Dimensions.ITEM_SPACING))
                    FarmerDropdown(
                        label = stringResource(R.string.soil_fertility_label),
                        options = fertilityLevels,
                        selectedKey = formState.soilFertility,
                        onOptionSelected = onSoilFertilityChange
                    )
                }
            }

            // Section 3: Water & Irrigation
            item {
                FormSection(title = stringResource(R.string.section_water)) {
                    Text(
                        stringResource(R.string.water_avail_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = SoilBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Dimensions.SMALL))
                    WaterSelectionRow(
                        selectedLevel = formState.waterAvailability,
                        levels = fertilityLevels, // Use same low/med/high strings
                        onLevelSelected = onWaterAvailabilityChange
                    )
                    Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                    FarmerDropdown(
                        label = stringResource(R.string.irrigation_src_label),
                        options = irrigationSources,
                        selectedKey = formState.irrigationSource,
                        onOptionSelected = onIrrigationSourceChange
                    )
                }
            }

            // Section 4: Preferences
            item {
                FormSection(title = stringResource(R.string.section_prefs)) {
                    Text(
                        stringResource(R.string.farmer_priority_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = SoilBrown,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Dimensions.SMALL))
                    PrioritySelectionGrid(
                        selectedPriority = formState.priority,
                        priorities = priorities,
                        onPrioritySelected = onPriorityChange
                    )
                    Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                    OutlinedTextField(
                        value = formState.previousCrop,
                        onValueChange = onPreviousCropChange,
                        label = { Text(stringResource(R.string.prev_crop_label)) },
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
                        Text(stringResource(R.string.get_recommendation_btn), fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
    options: List<Pair<String, Int>>,
    selectedKey: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = options.find { it.first.equals(selectedKey, ignoreCase = true) }?.second?.let { stringResource(it) } ?: selectedKey

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = displayValue,
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
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { (key, resId) ->
                val localizedText = stringResource(resId)
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = localizedText,
                            style = MaterialTheme.typography.bodyLarge
                        ) 
                    },
                    onClick = {
                        onOptionSelected(key)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun WaterSelectionRow(
    selectedLevel: String,
    levels: List<Pair<String, Int>>,
    onLevelSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
    ) {
        levels.forEach { (key, resId) ->
            val isSelected = selectedLevel.equals(key, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = { onLevelSelected(key) },
                label = { Text(stringResource(resId)) },
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
fun PrioritySelectionGrid(
    selectedPriority: String,
    priorities: List<Pair<String, Int>>,
    onPrioritySelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)) {
        priorities.forEach { (key, resId) ->
            val isSelected = selectedPriority.equals(key, ignoreCase = true)
            val displayValue = stringResource(resId)
            OutlinedCard(
                onClick = { onPrioritySelected(key) },
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
                    Text(displayValue, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
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
