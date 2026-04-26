package com.example.krishimitra.data.dto

import com.google.gson.annotations.SerializedName

data class RecommendRequestDto(
    @SerializedName("soilType") val soilType: String,
    @SerializedName("season") val season: String,
    @SerializedName("temperature") val temperature: Int,
    @SerializedName("rainfall") val rainfall: Int,
    @SerializedName("nitrogen") val nitrogen: Int,
    @SerializedName("phosphorus") val phosphorus: Int,
    @SerializedName("potassium") val potassium: Int
)

