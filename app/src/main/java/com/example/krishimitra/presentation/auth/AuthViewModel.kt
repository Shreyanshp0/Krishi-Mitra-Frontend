package com.example.krishimitra.presentation.auth

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.data.auth.AuthRepository
import com.example.krishimitra.data.local.TokenManager
import com.example.krishimitra.data.network.api.UserData
import com.example.krishimitra.domain.repository.LocationRepository
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val locationRepository: LocationRepository,
    private val tokenManager: TokenManager,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val prefs = appContext.getSharedPreferences("krishi_auth", Context.MODE_PRIVATE)

    private val _loginForm = MutableStateFlow(LoginFormState(
        email = prefs.getString("email", "") ?: "",
        password = prefs.getString("password", "") ?: "",
        rememberMe = prefs.getBoolean("remember", false)
    ))
    val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()

    private val _signupForm = MutableStateFlow(SignupFormState())
    val signupForm: StateFlow<SignupFormState> = _signupForm.asStateFlow()

    private val _states = MutableStateFlow<List<String>>(emptyList())
    val states: StateFlow<List<String>> = _states.asStateFlow()

    private val _districts = MutableStateFlow<List<String>>(emptyList())
    val districts: StateFlow<List<String>> = _districts.asStateFlow()

    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> = _userProfile.asStateFlow()

    init {
        loadStates()
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            authRepository.getMe().onSuccess { response ->
                _userProfile.value = response.user
            }
        }
    }

    private fun loadStates() {
        viewModelScope.launch {
            locationRepository.getStates().onSuccess {
                _states.value = it
            }.onFailure {
                _uiState.value = AuthUiState.Error("Failed to load states: ${it.message}")
            }
        }
    }

    fun loadDistricts(state: String) {
        viewModelScope.launch {
            locationRepository.getDistricts(state).onSuccess {
                _districts.value = it
            }.onFailure {
                _uiState.value = AuthUiState.Error("Failed to load districts: ${it.message}")
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
                val address = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
                }

                address?.let {
                    val adminArea = it.adminArea // State
                    val subAdminArea = it.subAdminArea // District

                    if (adminArea != null) {
                        val matchedState = _states.value.find { state -> 
                            state.equals(adminArea, ignoreCase = true) || 
                            adminArea.contains(state, ignoreCase = true) 
                        }
                        
                        matchedState?.let { s ->
                            updateState(s)
                            if (subAdminArea != null) {
                                // We need to wait for districts to load or use the ones we just fetched if we had them
                                // For now, we'll try to find it in the current list which might be updated by updateState(s)
                                // but updateState is async (launches a coroutine).
                                // Better: fetch districts specifically here if needed.
                                locationRepository.getDistricts(s).onSuccess { dists ->
                                    _districts.value = dists
                                    val matchedDistrict = dists.find { district ->
                                        district.equals(subAdminArea, ignoreCase = true) ||
                                        subAdminArea.contains(district, ignoreCase = true)
                                    }
                                    matchedDistrict?.let { d -> updateDistrict(d) }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                response.token?.let { tokenManager.saveToken(it) }
                if (validated.rememberMe) saveCredentials(form.email, form.password)
                fetchUserProfile()
                _uiState.value = AuthUiState.Success
            }.onFailure {
                _uiState.value = AuthUiState.Error(it.message ?: "Login failed")
            }
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
            val result = authRepository.signup(SignupRequest(
                name = "${form.firstName} ${form.lastName}",
                phone = form.phone,
                password = form.password,
                email = form.email,
                state = form.state,
                district = form.district
            ))
            result.onSuccess {
                _signupForm.value = _signupForm.value.copy(otpRequested = true)
                _uiState.value = AuthUiState.Idle
            }.onFailure {
                _uiState.value = AuthUiState.Error(it.message ?: "Failed to request OTP")
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
            val result = authRepository.verifyOtp(VerifySignupOtpRequest(
                phone = form.phone,
                otp = form.otp,
                name = "${form.firstName} ${form.lastName}",
                password = form.password,
                email = form.email,
                state = form.state,
                district = form.district
            ))
            result.onSuccess { response ->
                response.token?.let { tokenManager.saveToken(it) }
                fetchUserProfile()
                _uiState.value = AuthUiState.Success
            }.onFailure {
                _uiState.value = AuthUiState.Error(it.message ?: "Verification failed")
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
