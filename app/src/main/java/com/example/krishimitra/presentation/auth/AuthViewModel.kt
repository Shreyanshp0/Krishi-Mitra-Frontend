package com.example.krishimitra.presentation.auth

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.data.auth.AuthRepository
import android.util.Log
import com.example.krishimitra.data.local.TokenManager
import com.example.krishimitra.data.local.UserProfileManager
import com.example.krishimitra.data.network.api.UserData
import com.example.krishimitra.domain.model.WeatherData
import com.example.krishimitra.domain.repository.LocationRepository
import com.example.krishimitra.domain.repository.WeatherRepository
import com.example.krishimitra.model.AuthResponse
import com.example.krishimitra.model.LoginRequest
import com.example.krishimitra.model.SignupRequest
import com.example.krishimitra.model.VerifySignupOtpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val tokenManager: TokenManager,
    private val userProfileManager: UserProfileManager,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherData?>(null)
    val weatherState: StateFlow<WeatherData?> = _weatherState.asStateFlow()

    private val prefs = appContext.getSharedPreferences("krishi_auth", Context.MODE_PRIVATE)

    private val _loginForm = MutableStateFlow(LoginFormState(
        email = prefs.getString("email", "") ?: "",
        password = prefs.getString("password", "") ?: "",
        rememberMe = prefs.getBoolean("remember", false)
    ))
    val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()

    private val _signupForm = MutableStateFlow(SignupFormState())
    val signupForm: StateFlow<SignupFormState> = _signupForm.asStateFlow()

    private val _states = MutableStateFlow(listOf<String>())
    val allStates: StateFlow<List<String>> = _states.asStateFlow()

    private val _districts = MutableStateFlow(listOf<String>())
    val districts: StateFlow<List<String>> = _districts.asStateFlow()

    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> = _userProfile.asStateFlow()

    val token = tokenManager.token
    val rememberMe = tokenManager.rememberMe

    init {
        loadStates()
        // Load user profile from DataStore on app start
        viewModelScope.launch {
            userProfileManager.userProfile.collect { savedProfile ->
                if (savedProfile != null && _userProfile.value == null) {
                    _userProfile.value = savedProfile
                    Log.d("AuthViewModel", "Loaded user profile from DataStore: ${savedProfile.name}")
                }
            }
        }
        // If we have a token, fetch profile to ensure we have the latest data
        viewModelScope.launch {
            tokenManager.token.collect { tk ->
                if (tk != null) {
                    fetchUserProfile()
                }
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            authRepository.getMe().onSuccess { response ->
                val user = response.user
                if (user != null) {
                    _userProfile.value = user
                    // Save user profile to DataStore for offline access and persistence
                    userProfileManager.saveUserProfile(user)
                    Log.d("AuthViewModel", "Saved user profile to DataStore: ${user.name}")
                    // Update user name in token manager if needed
                    tokenManager.saveSession(
                        token = tokenManager.token.first() ?: "",
                        remember = tokenManager.rememberMe.first(),
                        name = user.name
                    )
                }
            }.onFailure {
                // If offline, we might want to keep the current profile if it exists
                // or handle unauthorized error
                Log.e("AuthViewModel", "Failed to fetch profile: ${it.message}")
            }
        }
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherRepository.getWeatherData(lat, lon).onSuccess {
                _weatherState.value = it
            }
        }
    }

    private val hardcodedLocations = mapOf(
        "Maharashtra" to listOf("Mumbai", "Pune", "Nagpur", "Nashik", "Aurangabad"),
        "Uttar Pradesh" to listOf("Lucknow", "Kanpur", "Agra", "Varanasi", "Meerut"),
        "Karnataka" to listOf("Bengaluru", "Mysuru", "Hubballi", "Mangaluru", "Belagavi"),
        "Tamil Nadu" to listOf("Chennai", "Coimbatore", "Madurai", "Salem", "Tiruchirappalli"),
        "Gujarat" to listOf("Ahmedabad", "Surat", "Vadodara", "Rajkot", "Bhavnagar")
    )

    private fun loadStates() {
        viewModelScope.launch {
            locationRepository.getStates().onSuccess {
                _states.value = it.ifEmpty { hardcodedLocations.keys.toList() }
            }.onFailure {
                _states.value = hardcodedLocations.keys.toList()
                _uiState.value = AuthUiState.Error("Failed to load states: ${it.message}. Using offline data.")
            }
        }
    }

    fun loadDistricts(state: String) {
        viewModelScope.launch {
            locationRepository.getDistricts(state).onSuccess {
                _districts.value = it.ifEmpty { hardcodedLocations[state] ?: emptyList() }
            }.onFailure {
                _districts.value = hardcodedLocations[state] ?: emptyList()
                _uiState.value = AuthUiState.Error("Failed to load districts: ${it.message}. Using offline data.")
            }
        }
    }

    fun districtsFor(state: String): List<String> = _districts.value

    fun updateLoginEmail(email: String) {
        _loginForm.value = _loginForm.value.copy(email = email.trim(), emailError = null)
    }

    fun updateLoginPassword(password: String) {
        _loginForm.value = _loginForm.value.copy(password = password, passwordError = null)
    }

    fun updateRememberMe(remember: Boolean) {
        _loginForm.value = _loginForm.value.copy(rememberMe = remember)
    }

    fun updateFirstName(first: String) {
        _signupForm.value = _signupForm.value.copy(firstName = first, firstNameError = null)
    }

    fun updateLastName(last: String) {
        _signupForm.value = _signupForm.value.copy(lastName = last, lastNameError = null)
    }

    fun updateEmail(email: String) {
        _signupForm.value = _signupForm.value.copy(email = email.trim(), emailError = null)
    }

    fun updateSignupPhone(phone: String) {
        _signupForm.value = _signupForm.value.copy(phone = phone.filter { it.isDigit() }.take(10), phoneError = null)
    }

    fun updateSignupPassword(password: String) {
        _signupForm.value = _signupForm.value.copy(password = password, passwordError = null)
    }

    fun updateOtp(otp: String) {
        _signupForm.value = _signupForm.value.copy(otp = otp.filter { it.isDigit() }.take(6), otpError = null)
    }

    fun updateState(state: String) {
        _signupForm.value = _signupForm.value.copy(
            state = state,
            district = "",
            stateError = null,
            districtError = null
        )
        loadDistricts(state)
    }

    fun updateDistrict(district: String) {
        _signupForm.value = _signupForm.value.copy(district = district, districtError = null)
    }

    fun updateLocation(context: Context, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val addresses = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    try {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(latitude, longitude, 1)
                    } catch (e: Exception) {
                        Log.e("Geocoder", "Geocoding failed for coordinates: $latitude, $longitude", e)
                        null
                    }
                }

                addresses?.firstOrNull()?.let { address ->
                    val adminArea = address.adminArea // State
                    val subAdminArea = address.subAdminArea // District

                    if (adminArea != null) {
                        Log.d("Location", "Geocoded address - State: $adminArea, District: $subAdminArea, Locality: ${address.locality}")

                        // Exact state match first - most reliable
                        var matchedState = _states.value.find { state ->
                            state.equals(adminArea, ignoreCase = true)
                        }

                        // Fallback to partial match with word boundary consideration
                        if (matchedState == null) {
                            Log.d("Location", "No exact state match for '$adminArea', trying partial match")
                            matchedState = _states.value.find { state ->
                                val stateWords = state.split(Regex("\\s+"))
                                val adminWords = adminArea.split(Regex("\\s+"))
                                // Match if there's a common word (more reliable than simple contains)
                                stateWords.any { stateWord ->
                                    adminWords.any { adminWord ->
                                        stateWord.equals(adminWord, ignoreCase = true)
                                    }
                                }
                            }
                        }

                        // Last fallback: simple contains check
                        if (matchedState == null) {
                            Log.d("Location", "No word match for state, trying simple contains")
                            matchedState = _states.value.find { state ->
                                adminArea.contains(state, ignoreCase = true) ||
                                state.contains(adminArea, ignoreCase = true)
                            }
                        }

                        // Try address lines as last resort
                        if (matchedState == null && address.maxAddressLineIndex >= 0) {
                            Log.d("Location", "Searching in address lines for state match")
                            for (i in 0..address.maxAddressLineIndex) {
                                val line = address.getAddressLine(i)
                                matchedState = _states.value.find { state ->
                                    line.contains(state, ignoreCase = true)
                                }
                                if (matchedState != null) {
                                    Log.d("Location", "Found state '$matchedState' in address line: $line")
                                    break
                                }
                            }
                        }
                        
                        matchedState?.let { s ->
                            Log.d("Location", "Matched state: $s")
                            updateState(s)
                            fetchWeather(latitude, longitude) // Fetch weather when location is detected

                            if (subAdminArea != null) {
                                // Wait for districts to load after updateState triggers the async loadDistricts
                                // Using a delay to allow the district fetch to complete
                                delay(500)

                                Log.d("Location", "Current districts in state: ${_districts.value}")

                                // Exact district match first
                                var matchedDistrict = _districts.value.find { district ->
                                    district.equals(subAdminArea, ignoreCase = true)
                                }

                                // Partial match with word boundary
                                if (matchedDistrict == null) {
                                    Log.d("Location", "No exact district match for '$subAdminArea', trying partial match")
                                    matchedDistrict = _districts.value.find { district ->
                                        val districtWords = district.split(Regex("\\s+"))
                                        val subadminWords = subAdminArea.split(Regex("\\s+"))
                                        districtWords.any { distWord ->
                                            subadminWords.any { subWord ->
                                                distWord.equals(subWord, ignoreCase = true)
                                            }
                                        }
                                    }
                                }

                                // Simple contains match
                                if (matchedDistrict == null) {
                                    Log.d("Location", "No word match for district, trying contains")
                                    matchedDistrict = _districts.value.find { district ->
                                        district.contains(subAdminArea, ignoreCase = true) ||
                                        subAdminArea.contains(district, ignoreCase = true) ||
                                        address.locality?.contains(district, ignoreCase = true) == true
                                    }
                                }

                                matchedDistrict?.let { d ->
                                    Log.d("Location", "Matched district: $d")
                                    updateDistrict(d)
                                } ?: run {
                                    Log.w("Location", "Could not match district: $subAdminArea. Showing districts for selection.")
                                }
                            }
                        } ?: run {
                            Log.w("Location", "Could not match any state for adminArea: $adminArea")
                        }
                    } else {
                        Log.w("Location", "Geocoded address has no adminArea (state)")
                    }
                } ?: run {
                    Log.w("Location", "Could not geocode location coordinates: $latitude, $longitude")
                    _uiState.value = AuthUiState.Error("Location found but could not be identified. Please select manually.")
                }
            } catch (e: Exception) {
                Log.e("Location", "Unexpected error during updateLocation", e)
                _uiState.value = AuthUiState.Error("Error processing location: ${e.message}")
            }
        }
    }

    fun login() {
        val form = _loginForm.value

        val validated = form.copy(
            emailError = validateEmail(form.email),
            passwordError = if (form.password.length >= 6) null else "Password must be at least 6 characters"
        )

        _loginForm.value = validated
        if (validated.hasErrors) return

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = authRepository.login(LoginRequest(email = form.email, password = form.password))
            
            result.onSuccess { response ->
                val token = response.token ?: ""
                // Save session with user name (temporarily use email if name not in login response)
                tokenManager.saveSession(
                    token = token,
                    remember = validated.rememberMe,
                    name = "" // Will be updated by fetchUserProfile
                )
                
                if (validated.rememberMe) {
                    saveCredentials(form.email, form.password)
                } else {
                    clearSavedCredentials()
                }
                
                fetchUserProfile()
                _uiState.value = AuthUiState.Success
            }.onFailure {
                _uiState.value = AuthUiState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearSession()
            userProfileManager.clearUserProfile()
            _userProfile.value = null
            _uiState.value = AuthUiState.Idle
            Log.d("AuthViewModel", "User logged out and profile cleared")
        }
    }

    private fun saveCredentials(email: String, password: String) {
        prefs.edit()
            .putString("email", email)
            .putString("password", password)
            .putBoolean("remember", true)
            .apply()
    }

    fun clearSavedCredentials() {
        prefs.edit().clear().apply()
        _loginForm.value = _loginForm.value.copy(email = "", password = "", rememberMe = false)
    }

    fun requestSignupOtp() {
        val form = _signupForm.value
        val validated = form.copy(
            firstNameError = if (form.firstName.trim().isEmpty()) "First name is required" else null,
            lastNameError = if (form.lastName.trim().isEmpty()) "Last name is required" else null,
            emailError = validateEmail(form.email),
            phoneError = if (form.phone.length == 10) null else "Phone must be 10 digits",
            passwordError = if (form.password.length >= 6) null else "Password must be at least 6 characters",
            stateError = if (form.state.isBlank()) "Please select state" else null,
            districtError = if (form.district.isBlank()) "Please select district" else null,
            otpError = null
        )

        _signupForm.value = validated
        if (validated.hasBaseErrors) return

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val request = SignupRequest(
                    name = "${form.firstName} ${form.lastName}",
                    phone = form.phone,
                    password = form.password,
                    email = form.email,
                    state = form.state,
                    district = form.district
                )
                Log.d("Signup", "Requesting OTP for: ${request.email}")
                val result = authRepository.signup(request)
                result.onSuccess {
                    Log.d("Signup", "OTP requested successfully")
                    _signupForm.value = _signupForm.value.copy(otpRequested = true)
                    _uiState.value = AuthUiState.Idle
                }.onFailure {
                    Log.e("Signup", "OTP request failed: ${it.message}")
                    _uiState.value = AuthUiState.Error(it.message ?: "Failed to request OTP")
                }
            } catch (e: Exception) {
                Log.e("Signup", "Unexpected error: ${e.message}")
                _uiState.value = AuthUiState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun verifySignupOtp() {
        val form = _signupForm.value
        val otpError = validateOtp(form.otp)
        if (otpError != null) {
            _signupForm.value = form.copy(otpError = otpError)
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val request = VerifySignupOtpRequest(
                    phone = form.phone,
                    otp = form.otp,
                    name = "${form.firstName} ${form.lastName}",
                    password = form.password,
                    email = form.email,
                    state = form.state,
                    district = form.district
                )
                Log.d("Signup", "Verifying OTP for: ${request.email}")
                val result = authRepository.verifyOtp(request)
                result.onSuccess { response ->
                    Log.d("Signup", "OTP verified successfully")
                val token = response.token ?: ""
                tokenManager.saveSession(
                    token = token,
                    remember = true, // Default to true for signup
                    name = "${form.firstName} ${form.lastName}"
                )
                fetchUserProfile()
                _uiState.value = AuthUiState.Success
            }.onFailure {
                    Log.e("Signup", "Verification failed: ${it.message}")
                    _uiState.value = AuthUiState.Error(it.message ?: "Verification failed")
                }
            } catch (e: Exception) {
                Log.e("Signup", "Unexpected verification error: ${e.message}")
                _uiState.value = AuthUiState.Error("Verification failed: ${e.message}")
            }
        }
    }

    fun consumeError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Email is required"
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return if (regex.matches(email)) null else "Enter a valid email"
    }

    private fun validateOtp(otp: String): String? {
        return if (otp.length == 6) null else "Enter 6-digit OTP"
    }
}

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val hasErrors: Boolean
        get() = listOf(emailError, passwordError).any { it != null }
}

data class SignupFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val state: String = "",
    val district: String = "",
    val otp: String = "",
    val otpRequested: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val stateError: String? = null,
    val districtError: String? = null,
    val otpError: String? = null
) {
    val hasBaseErrors: Boolean
        get() = listOf(firstNameError, lastNameError, emailError, phoneError, passwordError, stateError, districtError).any { it != null }
}
