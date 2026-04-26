package com.example.krishimitra.domain.model

data class CropInput(
    val soilType: String,
    val season: String,
    val temperature: Int,
    val rainfall: Int,
    val nitrogen: Int,
    val phosphorus: Int,
    val potassium: Int
)

