package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.presentation.viewmodel.InputFormState
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.SoilBrown

private val soilOptions = listOf("Sandy", "Clay", "Loamy", "Black", "Red")
private val seasonOptions = listOf("Kharif", "Rabi", "Zaid")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFormScreen(
    formState: InputFormState,
    onSoilTypeChange: (String) -> Unit,
    onSeasonChange: (String) -> Unit,
    onTemperatureChange: (String) -> Unit,
    onRainfallChange: (String) -> Unit,
    onNitrogenChange: (String) -> Unit,
    onPhosphorusChange: (String) -> Unit,
    onPotassiumChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Field Details", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepGreen)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Soil & Climate Information",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepGreen,
                        fontWeight = FontWeight.Bold
                    )

                    DropdownField(
                        label = "Soil Type",
                        options = soilOptions,
                        selected = formState.soilType,
                        onSelected = onSoilTypeChange,
                        error = formState.soilTypeError
                    )

                    DropdownField(
                        label = "Season",
                        options = seasonOptions,
                        selected = formState.season,
                        onSelected = onSeasonChange,
                        error = formState.seasonError
                    )

                    NumberField("Temperature (°C)", formState.temperature, onTemperatureChange, formState.temperatureError)
                    NumberField("Rainfall (mm)", formState.rainfall, onRainfallChange, formState.rainfallError)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Nutrient Levels",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepGreen,
                        fontWeight = FontWeight.Bold
                    )

                    NumberField("Nitrogen (N)", formState.nitrogen, onNitrogenChange, formState.nitrogenError)
                    NumberField("Phosphorus (P)", formState.phosphorus, onPhosphorusChange, formState.phosphorusError)
                    NumberField("Potassium (K)", formState.potassium, onPotassiumChange, formState.potassiumError)
                }
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
            ) {
                Text("Get Recommendation", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, color = SoilBrown, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select $label") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = error != null,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepGreen,
                    unfocusedBorderColor = SoilBrown.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, color = SoilBrown, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Enter $label") },
            isError = error != null,
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DeepGreen,
                unfocusedBorderColor = SoilBrown.copy(alpha = 0.5f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
