package com.example.myapplication.network;

import com.example.myapplication.model.DirectionResponses;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface GoogleApi {
    @GET("maps/api/directions/json")
    Call<DirectionResponses> getDirection(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode, // 추가: 이동 수단 (e.g., driving, walking)
            @Query("key") String apiKey
    );
}
