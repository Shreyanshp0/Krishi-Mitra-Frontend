package com.example.krishimitra.data.auth

import com.example.krishimitra.model.AuthResponse
import com.example.krishimitra.model.LoginRequest
import com.example.krishimitra.model.SignupRequest
import com.example.krishimitra.model.VerifySignupOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun requestSignupOtp(@Body request: SignupRequest): Response<AuthResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifySignupOtp(@Body request: VerifySignupOtpRequest): Response<AuthResponse>
}


