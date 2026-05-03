package com.example.krishimitra.presentation.auth

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.ui.theme.DarkCharcoal
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.SoilBrown
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun SignupScreen(
    formState: SignupFormState,
    uiState: AuthUiState,
    states: List<String>,
    districtOptions: List<String>,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onLocationDetected: (Double, Double) -> Unit,
    onSendOtpClick: () -> Unit,
    onVerifyOtpClick: () -> Unit,
    onNavigateLogin: () -> Unit,
    onAuthSuccess: () -> Unit,
    onErrorShown: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    onLocationDetected(it.latitude, it.longitude)
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            AuthUiState.Success -> onAuthSuccess()
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar(uiState.message)
                onErrorShown()
            }
            AuthUiState.Idle,
            AuthUiState.Loading -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LightBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create Your Account",
                style = MaterialTheme.typography.headlineSmall,
                color = DarkCharcoal,
                fontWeight = FontWeight.Bold
            )

            // Basic Details Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Basic Details",
                        color = SoilBrown,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // First & Last name
                    androidx.compose.material3.OutlinedTextField(
                        value = formState.firstName,
                        onValueChange = onFirstNameChange,
                        label = { Text("First Name") },
                        singleLine = true,
                        isError = formState.firstNameError != null,
                        supportingText = {
                            formState.firstNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    androidx.compose.material3.OutlinedTextField(
                        value = formState.lastName,
                        onValueChange = onLastNameChange,
                        label = { Text("Last Name") },
                        singleLine = true,
                        isError = formState.lastNameError != null,
                        supportingText = {
                            formState.lastNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Email input
                    EmailInputField(
                        value = formState.email,
                        onValueChange = onEmailChange,
                        error = formState.emailError,
                        isEnabled = !formState.otpRequested,
                        placeholder = "your.email@example.com"
                    )

                    // Phone
                    androidx.compose.material3.OutlinedTextField(
                        value = formState.phone,
                        onValueChange = onPhoneChange,
                        label = { Text("Phone Number") },
                        singleLine = true,
                        isError = formState.phoneError != null,
                        supportingText = { formState.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password
                    androidx.compose.material3.OutlinedTextField(
                        value = formState.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        singleLine = true,
                        isError = formState.passwordError != null,
                        supportingText = { formState.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Location Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LocationSelector(
                        states = states,
                        districtOptions = districtOptions,
                        selectedState = formState.state,
                        selectedDistrict = formState.district,
                        stateError = formState.stateError,
                        districtError = formState.districtError,
                        onStateChange = onStateChange,
                        onDistrictChange = onDistrictChange,
                        onAutoDetectClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }
            }

            // OTP Card (shown after OTP is sent)
            if (formState.otpRequested) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "OTP Verification",
                            color = SoilBrown,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            "Enter the 6-digit OTP sent to ${formState.email}",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkCharcoal
                        )

                        OtpInputField(
                            value = formState.otp,
                            onValueChange = onOtpChange,
                            error = formState.otpError,
                            label = "6-Digit OTP"
                        )
                    }
                }
            }

            // Action Buttons
            AuthButton(
                text = if (formState.otpRequested) "Verify OTP & Create Account" else "Send OTP",
                onClick = if (formState.otpRequested) onVerifyOtpClick else onSendOtpClick,
                isLoading = uiState is AuthUiState.Loading,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Resend OTP (shown after OTP is sent)
            if (formState.otpRequested) {
                TextButton(
                    onClick = onSendOtpClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Resend OTP",
                        color = DeepGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Navigate to Login
            TextButton(
                onClick = onNavigateLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Already have an account? Login",
                    color = SoilBrown,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
