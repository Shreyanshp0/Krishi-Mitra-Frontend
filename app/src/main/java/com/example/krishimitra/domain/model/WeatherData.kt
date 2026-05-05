package com.example.krishimitra.domain.model

data class WeatherData(
    val temperature: Double,
    val condition: String,
    val humidity: Int? = null,
    val rainfallMm: Double? = null
)
