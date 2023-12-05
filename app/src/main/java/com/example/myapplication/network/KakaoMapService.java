//package com.example.myapplication.network;
//
//import com.example.myapplication.ResultSearchKeyword;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class KakaoMapService {
//
//    private static final String BASE_URL = "https://dapi.kakao.com";
//    private static final String API_KEY = "{YOUR_API_KEY}"; // 여기에 실제 API 키를 넣어주세요.
//
//    private KakaoApiService kakaoApiService;
//
//    public KakaoMapService() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        kakaoApiService = retrofit.create(KakaoApiService.class);
//    }
//
//    public void searchPlaces(String query, int page, int size) {
//        Call<ResultSearchKeyword> call = kakaoApiService.getSearchKeyword(query, page, size);
//
//        call.enqueue(new Callback<ResultSearchKeyword>() {
//            @Override
//            public void onResponse(Call<ResultSearchKeyword> call, Response<ResultSearchKeyword> response) {
//                if (response.isSuccessful()) {
//                    ResultSearchKeyword result = response.body();
//                    // 여기서 결과를 처리하세요
//                } else {
//                    // API 호출은 성공했지만 응답이 실패인 경우
//                    // response.errorBody() 등을 활용하여 에러를 처리할 수 있습니다.
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResultSearchKeyword> call, Throwable t) {
//                // API 호출 자체가 실패한 경우
//                t.printStackTrace();
//            }
//        });
//    }
//}
