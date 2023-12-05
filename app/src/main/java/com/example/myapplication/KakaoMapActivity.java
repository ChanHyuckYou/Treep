//package com.example.myapplication;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.kakao.ListAdapter;
//import com.example.myapplication.kakao.ListAdapter2;
//import com.example.myapplication.kakao.ListLayout;
//import com.example.myapplication.network.DaumRest;
//import com.example.myapplication.network.KakaoApiService;
//import com.example.myapplication.network.KakaoMapService;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class KakaoMapActivity extends AppCompatActivity {
//
//    private EditText editTextSearch;
//    private RecyclerView recyclerView;
//    private ListAdapter listAdapter;
//
//    private KakaoMapService kakaoMapService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_kakaomap);
//
//        editTextSearch = findViewById(R.id.editTextSearch);
//        recyclerView = findViewById(R.id.recyclerView);
//
//        // RecyclerView 설정
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        listAdapter = new ListAdapter(new ArrayList<>());
//        recyclerView.setAdapter(listAdapter);
//
//        // KakaoMapService 초기화
//        kakaoMapService = new KakaoMapService();
//
//        // 검색 버튼 클릭 이벤트 처리
//        findViewById(R.id.buttonSearch).setOnClickListener(v -> onSearchButtonClick());
//    }
//
//    private void onSearchButtonClick() {
//        String query = editTextSearch.getText().toString().trim();
//        if (!query.isEmpty()) {
//            // 검색어가 비어 있지 않은 경우 Kakao REST API 호출
//            searchPlaces(query, 1, 10); // 이 부분을 바로 호출합니다.
//        } else {
//            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
//        }
//    }
//    private List<ListLayout> convertToLayouts(List<Place> places) {
//        List<ListLayout> listLayouts = new ArrayList<>();
//        for (Place place : places) {
//            double latitude = Double.parseDouble(place.getY());
//            double longitude = Double.parseDouble(place.getX());
//            ListLayout layout = new ListLayout(
//                    place.getPlace_name(),
//                    place.getRoad_address_name(),
//                    place.getAddress_name(),
//                    latitude,
//                    longitude
//            );
//            listLayouts.add(layout);
//        }
//        return listLayouts;
//    }
//    // KakaoMapService 클래스의 searchPlaces 메서드 일부
//    // KakaoMapService 클래스의 searchPlaces 메서드 일부
//    public void searchPlaces(String query, int page, int size) {
//        Call<ResultSearchKeyword> call = DaumRest.getSearchKeyword("KakaoAK " + "295a13ed8bce4051b8a5df8dad90dee4", query, page, size, 37.5665, 126.9780);
//
//        // null 체크를 추가하고 오류 처리
//        if (call != null) {
//            call.enqueue(new Callback<ResultSearchKeyword>() {
//                @Override
//                public void onResponse(@NonNull Call<ResultSearchKeyword> call, @NonNull Response<ResultSearchKeyword> response) {
//                    if (response.isSuccessful()) {
//                        ResultSearchKeyword result = response.body();
//                        if (result != null) {
//                            handleSearchResult(result);
//                        } else {
//                            Log.e("KakaoMapService", "Response body is null.");
//                        }
//                    } else {
//                        Log.e("KakaoMapService", "Response unsuccessful. Code: " + response.code());
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<ResultSearchKeyword> call, @NonNull Throwable t) {
//                    Log.e("KakaoMapService", "API call failed. Error: " + t.getMessage());
//                }
//            });
//        } else {
//            Log.e("KakaoMapService", "Call object is null.");
//        }
//    }
//
//
//    private void handleSearchResult(ResultSearchKeyword result) {
//        // 결과를 로그로 출력
//        Log.d("KakaoMapActivity", "Received Result: " + result);
//
//        // SearchResult를 ListLayout으로 변환하는 로직이 필요한 경우
//        List<ListLayout> listLayouts = convertToLayouts(result.getDocuments());
//
//        // ListAdapter2 사용
//        ListAdapter2 listAdapter = new ListAdapter2(new ArrayList<>());
//        listAdapter.setItemList((ArrayList<ListLayout>) listLayouts); // setItemList 변경
//
//        recyclerView.setAdapter(listAdapter);
//    }
//}
