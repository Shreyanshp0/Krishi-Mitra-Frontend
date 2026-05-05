package com.example.krishimitra.data.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("api/chat")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>
}

data class ChatRequest(
    val message: String,
    val location: String = "", // e.g., "Maharashtra"
    val district: String = "" // e.g., "Pune"
)

data class ChatResponse(
    val success: Boolean,
    val message: String? = null,
    val reply: String? = null
)

