package com.example.krishimitra.domain.repository

import com.example.krishimitra.data.network.api.ChatResponse
import com.example.krishimitra.domain.model.DomainResult

interface ChatRepository {
    suspend fun sendMessage(message: String, state: String, district: String, language: String): DomainResult<String>
}
