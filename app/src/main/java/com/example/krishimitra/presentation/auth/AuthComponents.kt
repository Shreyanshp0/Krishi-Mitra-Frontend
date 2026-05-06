package com.example.krishimitra.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.ui.theme.DarkCharcoal
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.SoilBrown

@Composable
fun EmailInputField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
    isEnabled: Boolean = true,
    placeholder: String = "Enter your email",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Email", style = MaterialTheme.typography.bodyMedium) },
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        enabled = isEnabled,
        isError = error != null,
        supportingText = {
            error?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        },
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
    placeholder: String = "000000",
    label: String = "Enter OTP",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = error != null,
        supportingText = {
            error?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        },
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading,
    containerColor: Color = DeepGreen,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelector(
    states: List<String>? = emptyList(),
    districtOptions: List<String>? = emptyList(),
    selectedState: String,
    selectedDistrict: String,
    stateError: String? = null,
    districtError: String? = null,
    onStateChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onAutoDetectClick: () -> Unit,
    isDetectingLocation: Boolean = false,
    modifier: Modifier = Modifier
) {
    val statesList = states ?: emptyList()
    val districtsList = districtOptions ?: emptyList()
    var stateExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = onAutoDetectClick,
                enabled = !isDetectingLocation
            ) {
                if (isDetectingLocation) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Auto-detect location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_SMALL),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📍 Tap location icon to auto-detect your location",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(Dimensions.SMALL),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        ExposedDropdownMenuBox(
            expanded = stateExpanded,
            onExpandedChange = { stateExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedState,
                onValueChange = {},
                readOnly = true,
                label = { Text("State", style = MaterialTheme.typography.bodyMedium) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                isError = stateError != null,
                supportingText = {
                    stateError?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
                },
                shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
            ExposedDropdownMenu(
                expanded = stateExpanded,
                onDismissRequest = { stateExpanded = false }
            ) {
                statesList.forEach { state ->
                    DropdownMenuItem(
                        text = { Text(state) },
                        onClick = {
                            onStateChange(state)
                            stateExpanded = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = districtExpanded,
            onExpandedChange = { districtExpanded = it && districtsList.isNotEmpty() }
        ) {
            OutlinedTextField(
                value = selectedDistrict,
                onValueChange = {},
                readOnly = true,
                label = { Text("District", style = MaterialTheme.typography.bodyMedium) },
                placeholder = { Text("Select state first", style = MaterialTheme.typography.bodyMedium) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                isError = districtError != null,
                supportingText = {
                    districtError?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
                },
                shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
            ExposedDropdownMenu(
                expanded = districtExpanded,
                onDismissRequest = { districtExpanded = false }
            ) {
                districtsList.forEach { district ->
                    DropdownMenuItem(
                        text = { Text(district) },
                        onClick = {
                            onDistrictChange(district)
                            districtExpanded = false
                        }
                    )
                }
            }
        }
    }
}
