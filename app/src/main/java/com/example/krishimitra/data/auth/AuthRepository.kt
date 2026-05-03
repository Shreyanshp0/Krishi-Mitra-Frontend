package com.example.krishimitra.data.auth

import com.example.krishimitra.model.AuthResponse
import com.example.krishimitra.model.LoginRequest
import com.example.krishimitra.model.SignupRequest
import com.example.krishimitra.model.VerifySignupOtpRequest

class AuthRepository(
    private val authApi: AuthApi
) {

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return safeApiCall { authApi.login(request) }
    }

    suspend fun requestSignupOtp(request: SignupRequest): Result<AuthResponse> {
        return safeApiCall { authApi.requestSignupOtp(request) }
    }

    suspend fun verifySignupOtp(request: VerifySignupOtpRequest): Result<AuthResponse> {
        return safeApiCall { authApi.verifySignupOtp(request) }
    }

    private suspend fun safeApiCall(call: suspend () -> retrofit2.Response<AuthResponse>): Result<AuthResponse> {
        return try {
            val response = call()
            val body = response.body()
            when {
                response.isSuccessful && body != null && body.success -> Result.success(body)
                response.isSuccessful && body != null -> Result.failure(Exception(body.message))
                else -> Result.failure(Exception("Request failed (${response.code()}). Please try again."))
            }
        } catch (exception: Exception) {
            Result.failure(Exception(exception.message ?: "Unable to connect to server."))
        }
    }
}

