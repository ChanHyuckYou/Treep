package com.example.myapplication.kakao

import com.google.gson.annotations.SerializedName

data class WalkingRoute(
    @SerializedName("section")
    val section: List<WalkingRouteSection>
)

data class WalkingRouteSection(
    @SerializedName("points")
    val points: List<Double>
)
