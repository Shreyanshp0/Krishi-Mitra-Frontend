package com.example.krishimitra.data.network.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("api/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponseDto
}

data class WeatherResponseDto(
    val success: Boolean,
    val temperature: Double,
    val condition: String,
    val humidity: Int? = null,
    val rainfall_mm: Double? = null
)
