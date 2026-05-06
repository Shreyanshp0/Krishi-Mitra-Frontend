package com.example.krishimitra.data.repository

import com.example.krishimitra.data.network.api.ChatApi
import com.example.krishimitra.data.network.api.ChatRequest
import com.example.krishimitra.domain.model.DomainResult
import com.example.krishimitra.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ChatRepository {

    override suspend fun sendMessage(message: String, state: String, district: String, language: String): DomainResult<String> {
        return try {
            val response = chatApi.sendMessage(
                ChatRequest(
                    message = message,
                    location = state,
                    district = district,
                    language = language
                )
            )
            
            if (response.isSuccessful) {
                val reply = response.body()?.reply ?: "I'm sorry, I couldn't process that request."
                DomainResult.Success(reply)
            } else {
                DomainResult.Error("Server responded with an error: ${response.code()}")
            }
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "An unexpected error occurred")
        }
    }
}
