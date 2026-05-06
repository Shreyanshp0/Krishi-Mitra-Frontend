package com.example.krishimitra.domain.model

data class CropInput(
    val mode: String = "manual",
    val language: String = "en",
    val state: String = "",
    val district: String = "",
    val season: String? = null,
    val soilType: String? = null,
    val soilFertility: String? = null,
    val waterAvailability: String? = null,
    val irrigationSource: String? = null,
    val priority: String? = null,
    val nitrogen: String? = null,
    val phosphorus: String? = null,
    val potassium: String? = null,
    val ph: String? = null
)
