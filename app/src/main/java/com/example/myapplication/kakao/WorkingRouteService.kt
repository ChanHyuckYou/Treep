package com.example.myapplication.kakao

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WalkingRouteService {
    @GET("/v2/local/path")
    fun getWalkingRoute(
        @Query("startX") startX: Double,
        @Query("startY") startY: Double,
        @Query("endX") endX: Double,
        @Query("endY") endY: Double
    ): Call<WalkingRoute>
}