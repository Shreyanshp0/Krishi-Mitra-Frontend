package com.example.krishimitra.data.network.api

import com.example.krishimitra.data.dto.RecommendRequestDto
import com.example.krishimitra.data.dto.RecommendResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendApi {
    @POST("api/recommend")
    suspend fun getRecommendations(@Body request: RecommendRequestDto): RecommendResponseDto
}
