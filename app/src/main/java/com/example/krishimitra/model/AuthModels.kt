package com.example.krishimitra.model

// Email-based OTP login
data class SendLoginOtpRequest(
    val email: String
)

data class VerifyLoginOtpRequest(
    val email: String,
    val otp: String
)

// Old models (kept for backward compatibility)
data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val phone: String,
    val password: String,
    val email: String?,
    val state: String,
    val district: String
)

data class VerifySignupOtpRequest(
    val phone: String,
    val otp: String,
    val name: String,
    val password: String,
    val email: String?,
    val state: String,
    val district: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)

