package com.example.krishimitra.data.dto

import com.google.gson.annotations.SerializedName

data class RecommendRequestDto(
    @SerializedName("mode") val mode: String,
    @SerializedName("language") val language: String,
    @SerializedName("state") val state: String,
    @SerializedName("district") val district: String,
    @SerializedName("season") val season: String? = null,
    @SerializedName("soilType") val soilType: String? = null,
    @SerializedName("soilFertility") val soilFertility: String? = null,
    @SerializedName("waterAvailability") val waterAvailability: String? = null,
    @SerializedName("irrigationSource") val irrigationSource: String? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("nitrogen") val nitrogen: String? = null,
    @SerializedName("phosphorus") val phosphorus: String? = null,
    @SerializedName("potassium") val potassium: String? = null,
    @SerializedName("ph") val ph: String? = null
)
