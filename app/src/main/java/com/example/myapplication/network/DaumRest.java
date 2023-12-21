package com.example.myapplication.network;

import com.example.myapplication.ResultSearchKeyword;
import com.example.myapplication.kakao.WalkingRoute;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface DaumRest {
    @GET("v2/local/search/keyword.json")
    Call<ResultSearchKeyword> getSearchKeyword(
            @Header("Authorization") String key,
            @Query("query") String query,
            @Query("page") int page,
            @Query("radius") int radius,
            @Query("y") double latitude,
            @Query("x") double longitude
    );

    // Walking Route를 얻어오기 위한 API 호출
    @GET("v2/local/geo/transcoord.json")
    Call<WalkingRoute> getWalkingRoute(
            @Header("Authorization") String key,
            @Query("x") double startX,
            @Query("y") double startY,
            @Query("destination_x") double endX,
            @Query("destination_y") double endY,
            @Query("output_coord") String outputCoord,
            @Query("searchCoord") String searchCoord
    );
}