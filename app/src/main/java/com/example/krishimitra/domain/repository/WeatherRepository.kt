package com.example.krishimitra.domain.repository

import com.example.krishimitra.domain.model.WeatherData

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, lon: Double): Result<WeatherData>
}
