package com.example.krishimitra.data.dto

import com.google.gson.annotations.SerializedName

data class RecommendRequestDto(
    @SerializedName("mode") val mode: String,
    @SerializedName("location") val location: LocationDto,
    @SerializedName("soilData") val soilData: Map<String, Any?>? = null,
    @SerializedName("inputs") val inputs: Map<String, Any?>? = null
)

data class LocationDto(
    @SerializedName("state") val state: String,
    @SerializedName("district") val district: String
)
