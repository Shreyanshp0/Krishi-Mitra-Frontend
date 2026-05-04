package com.example.krishimitra.data.network.api

import com.example.krishimitra.model.AuthResponse
import com.example.krishimitra.model.LoginRequest
import com.example.krishimitra.model.SignupRequest
import com.example.krishimitra.model.VerifySignupOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifySignupOtpRequest): Response<AuthResponse>

    @POST("api/auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getMe(): Response<UserResponse>
}

data class ResendOtpRequest(
    val email: String
)

data class UserResponse(
    val success: Boolean,
    val message: String,
    val user: UserData? = null
)

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val state: String,
    val district: String
)
