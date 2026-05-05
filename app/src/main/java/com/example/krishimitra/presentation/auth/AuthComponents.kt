package com.example.krishimitra.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.krishimitra.ui.theme.DarkCharcoal
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.SoilBrown

/**
 * Email input field with validation error display
 */
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
        label = { Text("Email") },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                tint = SoilBrown
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        enabled = isEnabled,
        isError = error != null,
        supportingText = {
            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * OTP input field (6-digit numeric only)
 */
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
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = error != null,
        supportingText = {
            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Large action button with optional loading state
 */
@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = !isLoading,
    backgroundColor: Color = DeepGreen,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Text(text, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

/**
 * Location selector with auto-detect option
 */
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Location header with auto-detect button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Location",
                color = SoilBrown,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = onAutoDetectClick,
                enabled = !isDetectingLocation
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Auto-detect location",
                    tint = DeepGreen,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Text(
            "📍 Tap location icon to auto-detect your state & district",
            style = MaterialTheme.typography.bodySmall,
            color = SoilBrown.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 2.dp)
        )

        // State dropdown
        ExposedDropdownMenuBox(
            expanded = stateExpanded,
            onExpandedChange = { stateExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedState,
                onValueChange = {},
                readOnly = true,
                label = { Text("State") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                isError = stateError != null,
                supportingText = {
                    stateError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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

        // District dropdown
        ExposedDropdownMenuBox(
            expanded = districtExpanded,
            onExpandedChange = { districtExpanded = it && districtsList.isNotEmpty() }
        ) {
            OutlinedTextField(
                value = selectedDistrict,
                onValueChange = {},
                readOnly = true,
                label = { Text("District") },
                placeholder = { Text("Select state first") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                isError = districtError != null,
                supportingText = {
                    districtError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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
