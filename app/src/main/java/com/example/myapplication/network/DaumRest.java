package com.example.myapplication.network;

import com.example.myapplication.ResultSearchKeyword;
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
}