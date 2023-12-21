package com.example.myapplication.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiRetrofitClient {

    private static final String BASE_URL = "https://dapi.kakao.com/";
    private static final String API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4";

    public static DaumRest create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(DaumRest.class);
    }


    public static String getBaseUrl() {
        return BASE_URL;
    }

    // Walking Route를 호출하는 메서드
    public static DaumRest createWalkingRoute() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(DaumRest.class);
    }
}
