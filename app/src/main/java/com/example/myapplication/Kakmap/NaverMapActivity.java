//package com.example.myapplication;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.kakao.ListAdapter;
//import com.example.myapplication.kakao.ListLayout;
//import com.example.myapplication.network.DaumRest;
//import com.example.myapplication.network.RestApiRetrofitClient;
//import com.naver.maps.map.CameraUpdate;
//import com.naver.maps.map.LocationTrackingMode;
//import com.naver.maps.map.NaverMap;
//import com.naver.maps.map.OnMapReadyCallback;
//import com.naver.maps.map.overlay.Marker;
//import com.naver.maps.map.util.FusedLocationSource;
//import com.naver.maps.map.MapFragment;
//
//import java.util.ArrayList;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//
//public class NaverMapActivity extends AppCompatActivity implements OnMapReadyCallback {
//
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 5000;
//    private FusedLocationSource locationSource;
//    private NaverMap naverMap;
//
//    private static final String NAVER_ID = "YOUR_NAVER_ID"; // 네이버 개발자 센터에서 발급받은 클라이언트 ID
//    private static final String KAKAO_API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4";
//
//    private RecyclerView recyclerView;
//    private ListAdapter listAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_naver);
//
//        // 네이버 지도 초기화
//        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
//        mapFragment.getMapAsync(this);
//
//        // 위치 권한 요청
//        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
//
//        // RecyclerView 초기화
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        listAdapter = new ListAdapter(new ArrayList<>());
//        recyclerView.setAdapter(listAdapter);
//    }
//
//    @Override
//    public void onMapReady(@NonNull NaverMap naverMap) {
//        this.naverMap = naverMap;
//
//        // 위치 권한 설정
//        naverMap.setLocationSource(locationSource);
//        naverMap.getUiSettings().setLocationButtonEnabled(true);
//
//        // 서울을 중심으로 이동
//        naverMap.moveCamera(CameraUpdate.scrollTo(new com.naver.maps.geometry.LatLng(37.5666102, 126.9783881)));
//
//        // 검색 API를 통해 주변 관광지 검색
//        searchNearbyPlaces(new com.naver.maps.geometry.LatLng(37.5666102, 126.9783881));
//    }
//
//    private void searchNearbyPlaces(com.naver.maps.geometry.LatLng destination) {
//        // 네이버 검색 API를 사용하여 주변 관광지 검색
//        NaverSearchApi naverSearchApi = new NaverSearchApi.Builder(NAVER_ID)
//                .build();
//
//        // 주변 관광지 검색 예시 (원하는 검색 옵션 및 쿼리로 수정)
//        naverSearchApi.search("관광", destination.latitude, destination.longitude, 1000, 5, SearchSort.RANDOM, new NaverSearchApi.Callback<SearchResponse>() {
//            @Override
//            public void onResponse(SearchResponse response) {
//                // 검색 결과 처리
//                if (response != null && response.getItems() != null) {
//                    for (com.naver.search.api.v2.model.SearchResult item : response.getItems()) {
//                        // 각 검색 결과의 위치에 마커 표시
//                        Marker marker = new Marker();
//                        marker.setPosition(new com.naver.maps.geometry.LatLng(item.getPoint().getLatitude(), item.getPoint().getLongitude()));
//                        marker.setMap(naverMap);
//                        marker.setCaptionText(item.getTitle()); // 마커에 제목 표시
//
//                        // RecyclerView에 데이터 추가
//                        ListLayout listLayout = new ListLayout(item.getTitle(), item.getRoadAddress(), item.getAddress(), item.getPoint().getLongitude(), item.getPoint().getLatitude());
//                        listAdapter.addItem(listLayout);
//                    }
//
//                    // RecyclerView 갱신
//                    listAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable e) {
//                // 검색 실패 처리
//                e.printStackTrace();
//            }
//        });
//
//        // 카카오 검색 API를 사용하여 주변 관광지 검색
//        DaumRest daumRest = RestApiRetrofitClient.create();
//
//        // 검색 쿼리 및 위치 설정 (원하는 검색 옵션 및 쿼리로 수정)
//        Call<ResultSearchKeyword> call = daumRest.getSearchKeyword(KAKAO_API_KEY, "관광", 1, 1000, destination.getLatitude(), destination.getLongitude());
//        call.enqueue(new Callback<ResultSearchKeyword>() {
//            @Override
//            public void onResponse(Call<ResultSearchKeyword> call, Response<ResultSearchKeyword> response) {
//                if (response.isSuccessful()) {
//                    ResultSearchKeyword result = response.body();
//                    if (result != null && result.getDocuments() != null) {
//                        for (Place item : result.getDocuments()) {
//                            // 각 검색 결과의 위치에 마커 표시
//                            Marker marker = new Marker();
//                            marker.setPosition(new com.naver.maps.geometry.LatLng(Double.parseDouble(item.getY()), Double.parseDouble(item.getX())));
//                            marker.setMap(naverMap);
//                            marker.setCaptionText(item.getPlaceName()); // 마커에 제목 표시
//
//                            // RecyclerView에 데이터 추가
//                            ListLayout listLayout = new ListLayout(item.getPlaceName(), item.getRoadAddressName(), item.getAddressName(), Double.parseDouble(item.getX()), Double.parseDouble(item.getY()));
//                            listAdapter.addItem(listLayout);
//                        }
//
//                        // RecyclerView 갱신
//                        listAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResultSearchKeyword> call, Throwable t) {
//                // 검색 실패 처리
//                t.printStackTrace();
//            }
//        });
//    }
//}