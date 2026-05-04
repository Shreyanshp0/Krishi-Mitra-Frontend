package com.example.krishimitra.domain.model

data class CropInput(
    val mode: String = "manual",
    val state: String = "",
    val district: String = "",
    val soilType: String = "",
    val season: String = "",
    val nitrogen: Int? = null,
    val phosphorus: Int? = null,
    val potassium: Int? = null,
    val ph: Double? = null,
    val temperature: Int? = null,
    val rainfall: Int? = null,
    val additionalInputs: Map<String, Any?> = emptyMap()
)
