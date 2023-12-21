package com.example.myapplication.network

import com.example.myapplication.ResultSearchKeyword
import com.example.myapplication.kakao.WalkingRoute
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoRest {
    @GET("v2/local/search/keyword.json")    // Keyword.json의 정보를 받아옴
    fun getSearchKeyword(
        @Header("Authorization") key: String,     // 카카오 API 인증키 [필수]
        @Query("query") query: String,
        @Query("page") page: Int, // 검색을 원하는 질의어 [필수]
        // 매개변수 추가 가능
//        @Query("category_group_code") category: String


    ): Call<ResultSearchKeyword>    // 받아온 정보가 ResultSearchKeyword 클래스의 구조로 담김

    // Walking Route를 얻어오기 위한 API 호출
    @GET("v2/local/geo/transcoord.json")
    fun getWalkingRoute(
        @Header("Authorization") key: String?,
        @Query("x") startX: Double,
        @Query("y") startY: Double,
        @Query("destination_x") endX: Double,
        @Query("destination_y") endY: Double,
        @Query("output_coord") outputCoord: String?,
        @Query("searchCoord") searchCoord: String?
    ): Call<WalkingRoute?>?

}
