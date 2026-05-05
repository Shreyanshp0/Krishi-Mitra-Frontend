package com.example.krishimitra.presentation.auth

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.ui.theme.DarkCharcoal
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.SoilBrown
import com.google.android.gms.location.LocationServices

@Composable
fun SignupScreen(
    formState: SignupFormState,
    uiState: AuthUiState,
    states: List<String>? = emptyList(),
    districtOptions: List<String>? = emptyList(),
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
    val locationErrorMessage = remember { mutableStateOf<String?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Store LocationCallback reference for cleanup
    val locationCallbackRef = remember { mutableStateOf<com.google.android.gms.location.LocationCallback?>(null) }

    // Helper function to request location updates with proper error handling
    fun requestLocationUpdatesHelper(
        fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
        onLocationDetected: (Double, Double) -> Unit,
        errorMessage: androidx.compose.runtime.MutableState<String?>,
        callbackRef: androidx.compose.runtime.MutableState<com.google.android.gms.location.LocationCallback?>
    ) {
        @SuppressLint("MissingPermission")
        fun getLocation() {
            Log.d("Location", "Starting getLocation...")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d("Location", "Last location found: ${location.latitude}, ${location.longitude}")
                        onLocationDetected(location.latitude, location.longitude)
                    } else {
                        Log.d("Location", "Last location is null, requesting fresh location")
                        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                            5000 // 5 seconds
                        ).setMaxUpdates(1)
                            .build()

                        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                                val loc = result.lastLocation
                                if (loc != null) {
                                    Log.d("Location", "Fresh location obtained: ${loc.latitude}, ${loc.longitude}")
                                    onLocationDetected(loc.latitude, loc.longitude)
                                    fusedLocationClient.removeLocationUpdates(this)
                                    callbackRef.value = null
                                } else {
                                    Log.e("Location", "Location result was null")
                                    errorMessage.value = "Could not retrieve location. Please check GPS settings."
                                }
                            }
                            
                            override fun onLocationAvailability(availability: com.google.android.gms.location.LocationAvailability) {
                                Log.d("Location", "Location Availability: ${availability.isLocationAvailable}")
                                if (!availability.isLocationAvailable) {
                                    errorMessage.value = "Location services are unavailable. Is GPS on?"
                                }
                            }
                        }

                        callbackRef.value = locationCallback
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            android.os.Looper.getMainLooper()
                        ).addOnFailureListener { e ->
                            Log.e("Location", "Failed to request updates", e)
                            errorMessage.value = "Failed to start location search: ${e.message}"
                            callbackRef.value = null
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "lastLocation task failed", e)
                    errorMessage.value = "Error accessing location: ${e.message}"
                }
        }
        getLocation()
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("Location", "Permission results: $permissions")
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d("Location", "Permission granted, calling requestLocationUpdates")
            requestLocationUpdatesHelper(fusedLocationClient, onLocationDetected, locationErrorMessage, locationCallbackRef)
        } else {
            Log.e("Location", "Permission denied")
            locationErrorMessage.value = "Location permission denied. Please enable it in settings."
        }
    }

    // Cleanup LocationCallback when screen is disposed
    DisposableEffect(fusedLocationClient) {
        onDispose {
            locationCallbackRef.value?.let {
                try {
                    fusedLocationClient.removeLocationUpdates(it)
                    Log.d("LocationCleanup", "LocationCallback removed")
                } catch (e: Exception) {
                    Log.e("LocationCleanup", "Error removing location updates", e)
                }
            }
        }
    }

    // Show location error in snackbar
    LaunchedEffect(locationErrorMessage.value) {
        locationErrorMessage.value?.let { message ->
            snackbarHostState.showSnackbar(message)
            locationErrorMessage.value = null
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
                        states = states ?: emptyList(),
                        districtOptions = districtOptions ?: emptyList(),
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
