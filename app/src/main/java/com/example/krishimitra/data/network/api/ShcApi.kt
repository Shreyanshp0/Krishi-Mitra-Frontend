package com.example.krishimitra.data.network.api

import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ShcApi {
    @Multipart
    @POST("shc/upload")
    suspend fun uploadShc(@Part file: MultipartBody.Part): ShcResponseDto

    @GET("shc/latest")
    suspend fun getLatestShc(): ShcResponseDto
}

data class ShcResponseDto(
    val success: Boolean,
    val message: String,
    val data: Map<String, Any?>? = null
)
