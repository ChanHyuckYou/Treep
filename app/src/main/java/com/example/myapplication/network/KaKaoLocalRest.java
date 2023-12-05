package com.example.myapplication.network;



import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KaKaoLocalRest {

    private static final String BASE_URL = "https://apis-navi.kakaomobility.com/v1/destinations/directions\n";
    private static final String API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4";

    public static DaumRest create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

//        return retrofit.create(.class);
        return null;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
