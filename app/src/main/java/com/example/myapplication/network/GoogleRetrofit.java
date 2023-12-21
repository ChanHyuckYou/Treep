package com.example.myapplication.network;

import android.content.Context;

import com.example.myapplication.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class GoogleRetrofit {
    static GoogleApi apiServices(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.getResources().getString(R.string.base_url))
                .build();

        return retrofit.create(GoogleApi.class);
    }
}