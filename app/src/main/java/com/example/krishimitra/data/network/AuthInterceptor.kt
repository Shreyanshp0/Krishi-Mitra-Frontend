package com.example.krishimitra.data.network

import com.example.krishimitra.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.token.first()
        }
        
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        val response = chain.proceed(requestBuilder.build())
        
        if (response.code == 401) {
            // Token might be invalid or expired
            runBlocking {
                tokenManager.deleteToken()
            }
            // In a real app, you might want to trigger a logout event here
        }
        
        return response
    }
}
