package com.example.krishimitra.presentation.auth

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.data.auth.AuthRepository
import com.example.krishimitra.data.local.TokenManager
import com.example.krishimitra.data.local.UserProfileManager
import com.example.krishimitra.data.network.api.UserData
import com.example.krishimitra.domain.model.WeatherData
import com.example.krishimitra.domain.repository.LocationRepository
import com.example.krishimitra.model.LoginRequest
import com.example.krishimitra.model.SignupRequest
import com.example.krishimitra.model.VerifySignupOtpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val locationRepository: LocationRepository,
    private val tokenManager: TokenManager,
    private val userProfileManager: UserProfileManager,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _loginForm = MutableStateFlow(LoginFormState())
    val loginForm = _loginForm.asStateFlow()

    private val _signupForm = MutableStateFlow(SignupFormState())
    val signupForm = _signupForm.asStateFlow()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _states = MutableStateFlow<List<String>>(emptyList())
    val allStates = _states.asStateFlow()

    private val _districts = MutableStateFlow<List<String>>(emptyList())
    val districts = _districts.asStateFlow()

    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherData?>(null)
    val weatherState = _weatherState.asStateFlow()

    init {
        loadStates()
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            if (tokenManager.token.first() != null) {
                fetchUserProfile()
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            val result = authRepository.getMe()
            result.onSuccess { response ->
                response.user?.let { profile ->
                    _userProfile.value = profile
                    userProfileManager.saveUserProfile(profile)
                }
            }
        }
    }

    private fun loadStates() {
        viewModelScope.launch {
            locationRepository.getStates().onSuccess {
                _states.value = it
            }
        }
    }

    private fun loadDistricts(state: String) {
        viewModelScope.launch {
            locationRepository.getDistricts(state).onSuccess {
                _districts.value = it
            }
        }
    }

    val token = tokenManager.token
    val rememberMe = tokenManager.rememberMe

    fun updateLoginEmail(email: String) {
        _loginForm.value = _loginForm.value.copy(email = email, emailError = null)
    }

    fun updateLoginPassword(password: String) {
        _loginForm.value = _loginForm.value.copy(password = password, passwordError = null)
    }

    fun updateRememberMe(remember: Boolean) {
        _loginForm.value = _loginForm.value.copy(rememberMe = remember)
    }

    fun updateFirstName(name: String) {
        _signupForm.value = _signupForm.value.copy(firstName = name, firstNameError = null)
    }

    fun updateLastName(name: String) {
        _signupForm.value = _signupForm.value.copy(lastName = name, lastNameError = null)
    }

    fun updateEmail(email: String) {
        _signupForm.value = _signupForm.value.copy(email = email, emailError = null)
    }

    fun updateSignupPhone(phone: String) = updatePhone(phone)
    fun updateSignupPassword(password: String) = updatePassword(password)

    fun updatePhone(phone: String) {
        _signupForm.value = _signupForm.value.copy(phone = phone, phoneError = null)
    }

    fun updatePassword(password: String) {
        _signupForm.value = _signupForm.value.copy(password = password, passwordError = null)
    }

    fun updateOtp(otp: String) {
        _signupForm.value = _signupForm.value.copy(otp = otp, otpError = null)
    }

    fun updateState(state: String) {
        _signupForm.value = _signupForm.value.copy(
            state = state,
            stateError = null,
            district = "" // Reset district when state changes
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
                    val geocoder = Geocoder(context, Locale.ENGLISH)
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

                        // Use Geocoder values directly without manual matching
                        val stateValue = adminArea
                        val districtValue = subAdminArea?.replace("Division", "", ignoreCase = true)?.trim() ?: ""

                        Log.d("Location", "Using direct values - State: $stateValue, District: $districtValue")
                        
                        updateState(stateValue)
                        if (districtValue.isNotEmpty()) {
                            updateDistrict(districtValue)
                        }
                    } else {
                        Log.w("Location", "Geocoded address has no adminArea (state)")
                        _uiState.value = AuthUiState.Error("Unable to detect state. Please select manually.")
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

    fun fetchWeather(latitude: Double, longitude: Double) {
        // Implementation for weather fetching if needed
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
                tokenManager.saveSession(
                    token = token,
                    remember = validated.rememberMe,
                    name = ""
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
                val result = authRepository.signup(request)
                result.onSuccess {
                    _signupForm.value = _signupForm.value.copy(otpRequested = true)
                    _uiState.value = AuthUiState.Idle
                }.onFailure {
                    _uiState.value = AuthUiState.Error(it.message ?: "Failed to request OTP")
                }
            } catch (e: Exception) {
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
                val result = authRepository.verifyOtp(request)
                result.onSuccess { response ->
                    val token = response.token ?: ""
                    tokenManager.saveSession(
                        token = token,
                        remember = true,
                        name = "${form.firstName} ${form.lastName}"
                    )
                    fetchUserProfile()
                    _uiState.value = AuthUiState.Success
                }.onFailure {
                    _uiState.value = AuthUiState.Error(it.message ?: "Verification failed")
                }
            } catch (e: Exception) {
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
