package com.example.krishimitra.data.repository

import com.example.krishimitra.data.network.api.WeatherApi
import com.example.krishimitra.domain.model.WeatherData
import com.example.krishimitra.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi
) : WeatherRepository {
    override suspend fun getWeatherData(lat: Double, lon: Double): Result<WeatherData> {
        return try {
            val response = weatherApi.getWeather(lat, lon)
            if (response.success) {
                Result.success(
                    WeatherData(
                        temperature = response.temperature,
                        condition = response.condition,
                        humidity = response.humidity,
                        rainfallMm = response.rainfall_mm
                    )
                )
            } else {
                Result.failure(Exception("Failed to fetch weather data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
