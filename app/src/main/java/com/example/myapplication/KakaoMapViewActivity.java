package com.example.myapplication;



import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.kakao.ListAdapter;
import com.example.myapplication.kakao.ListLayout;
import com.example.myapplication.network.KakaoRest;
import com.kakao.vectormap.GestureType;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraAnimation;
import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.CompetitionType;
import com.kakao.vectormap.label.CompetitionUnit;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelLayerOptions;
import com.kakao.vectormap.label.LabelManager;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import com.kakao.vectormap.label.LabelTransition;
import com.kakao.vectormap.label.OrderingType;
import com.kakao.vectormap.label.PolylineLabel;
import com.kakao.vectormap.label.Transition;


import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KakaoMapViewActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://dapi.kakao.com/";
    public static final String API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4";  // REST API 키

    private ArrayList<ListLayout> listItems = new ArrayList<>(); // 리사이클러 뷰 아이템
    private ListAdapter listAdapter = new ListAdapter(listItems); // 리사이클러 뷰 어댑터
    private int pageNumber = 1; // 검색 페이지 번호
    private String keyword = ""; // 검색 키워드
    private Button mSearchbtn;
    private Button mPrevPagebtn;
    private Button mPagebtn;
    private TextView mtvpageNumber;
    private EditText mSearchFalid;
    private KakaoMap kakaoMap;
    private LabelLayer labelLayer;
    private Label moveLabel;
    private PolylineLabel polylineLabel;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakaomapview);


        MapView mapView = findViewById(R.id.mapView);
        RecyclerView rvList = findViewById(R.id.rv_list); // Replace with the actual ID of your RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);
        ListAdapter listAdapter = new ListAdapter(listItems); // Assuming ListAdapter has a default constructor
        rvList.setAdapter(listAdapter);
        mSearchbtn = (Button) findViewById(R.id.btn_search);
        mPrevPagebtn = (Button) findViewById(R.id.btn_prevPage);
        mPagebtn = (Button) findViewById(R.id.btn_nextPage);
        mtvpageNumber = (TextView) findViewById(R.id.tv_pageNumber);
        mSearchFalid = (EditText) findViewById(R.id.et_search_field);

        listAdapter.setItemClickListener((v, position) -> {
            double x = listItems.get(position).getX();
            double y = listItems.get(position).getY();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(y, x));
            kakaoMap.moveCamera(cameraUpdate);
        });




        mapView.start(new KakaoMapReadyCallback() {

            public void onMapError(Exception error) {
                Toast.makeText(KakaoMapViewActivity.this, "카카오맵 에러", Toast.LENGTH_SHORT).show();
            }
            public void onMapReady(KakaoMap map) {
                kakaoMap = map;
                labelLayer = kakaoMap.getLabelManager().getLayer();
                kakaoMap.setOnCameraMoveEndListener(new KakaoMap.OnCameraMoveEndListener() {
                    @Override
                    public void onCameraMoveEnd(@NonNull KakaoMap kakaoMap,
                                                @NonNull CameraPosition cameraPosition,
                                                @NonNull GestureType gestureType) {

                    }
                });
            }

            public String getViewName() {
                // KakaoMap 의 고유한 이름을 설정
                return "Treep";
            }
            public int getZoomLevel() {
                // 지도 시작 시 확대/축소 줌 레벨 설정
                return 15;
            }
        });

        mSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = mSearchFalid.getText().toString();
                pageNumber = 1;
                searchKeyword(keyword, pageNumber);
            }
        });

// 이전 페이지 버튼
        mPrevPagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNumber--;
                mtvpageNumber.setText(String.valueOf(pageNumber));
                searchKeyword(keyword, pageNumber);
            }
        });

// 다음 페이지 버튼
        mPagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNumber++;
                mtvpageNumber.setText(String.valueOf(pageNumber));
                searchKeyword(keyword, pageNumber);
            }
        });
    }


    private void searchKeyword(String keyword, int page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KakaoRest api = retrofit.create(KakaoRest.class);
        Call<ResultSearchKeyword> call = api.getSearchKeyword(API_KEY, keyword, page);

        // API 서버에 요청
        call.enqueue(new Callback<ResultSearchKeyword>() {
            @Override
            public void onResponse(@NonNull Call<ResultSearchKeyword> call, @NonNull Response<ResultSearchKeyword> response) {
                // 통신 성공
                addItemsAndMarkers(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResultSearchKeyword> call, @NonNull Throwable t) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: " + t.getMessage());
            }
        });
    }

    private void addItemsAndMarkers(ResultSearchKeyword searchResult) {
        if (searchResult != null && !searchResult.getDocuments().isEmpty()) {
            // 검색 결과 있음
            listItems.clear();                   // 리스트 초기화
            //Objects.requireNonNull(kakaoMap.getLabelManager()).removeAllLabelLayer(); // 지도의 마커 모두 제거
            for (Place document : searchResult.getDocuments()) {
                // 결과를 리사이클러 뷰에 추가
                ListLayout item = new ListLayout(document.getPlace_name(),
                        document.getRoad_address_name(),
                        document.getAddress_name(),
                        Double.parseDouble(document.getX()),
                        Double.parseDouble(document.getY()));
                listItems.add(item);



                LabelLayer layer = Objects.requireNonNull(kakaoMap.getLabelManager()).addLayer(LabelLayerOptions.from(document.getPlace_name())
                        .setOrderingType(OrderingType.Rank)
                        .setCompetitionUnit(CompetitionUnit.IconAndText)
                        .setCompetitionType(CompetitionType.All));

                LatLng pos = LatLng.from(Double.parseDouble(document.getY()),
                        Double.parseDouble(document.getX()));
                LabelStyles styles = kakaoMap.getLabelManager()
                        .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.blue_marker)
                                .setIconTransition(LabelTransition.from(Transition.None, Transition.None))));


                // 라벨 생성

                labelLayer.addLabel(LabelOptions.from(pos).setStyles(styles));

            }
            listAdapter.notifyDataSetChanged();

            mPagebtn.setEnabled(!searchResult.getMeta().is_end()); // 페이지가 더 있을 경우 다음 버튼 활성화
            mPrevPagebtn.setEnabled(pageNumber != 1);             // 1페이지가 아닐 경우 이전 버튼 활성화
        } else{
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
        }
    }
}
