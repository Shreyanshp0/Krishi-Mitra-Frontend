package com.example.krishimitra.data.dto

import com.google.gson.annotations.SerializedName

data class StatesResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("states") val states: List<String>
)

data class DistrictsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("districts") val districts: List<String>
)
