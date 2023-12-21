//package com.example.myapplication.network;
//
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class LocalApiRetrofitClient {
//    public static KakaoNaviApi create() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://apis-navi.kakaomobility.com/v1/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        return retrofit.create(KakaoNaviApi.class);
//    }
//}
//
