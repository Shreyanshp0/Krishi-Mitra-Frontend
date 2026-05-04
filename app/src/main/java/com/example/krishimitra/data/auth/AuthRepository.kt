package com.example.krishimitra.data.auth

import com.example.krishimitra.data.network.api.AuthApi
import com.example.krishimitra.data.network.api.ResendOtpRequest
import com.example.krishimitra.data.network.api.UserResponse
import com.example.krishimitra.model.AuthResponse
import com.example.krishimitra.model.LoginRequest
import com.example.krishimitra.model.SignupRequest
import com.example.krishimitra.model.VerifySignupOtpRequest
import retrofit2.Response

class AuthRepository(
    private val authApi: AuthApi
) {

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return safeApiCall { authApi.login(request) }
    }

    suspend fun signup(request: SignupRequest): Result<AuthResponse> {
        return safeApiCall { authApi.signup(request) }
    }

    suspend fun verifyOtp(request: VerifySignupOtpRequest): Result<AuthResponse> {
        return safeApiCall { authApi.verifyOtp(request) }
    }

    suspend fun resendOtp(email: String): Result<AuthResponse> {
        return safeApiCall { authApi.resendOtp(ResendOtpRequest(email)) }
    }

    suspend fun getMe(): Result<UserResponse> {
        return try {
            val response = authApi.getMe()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception(body?.message ?: "Failed to fetch user profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                // In a real app, you'd parse the error body here
                Result.failure(Exception("Request failed with code: ${response.code()}"))
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
