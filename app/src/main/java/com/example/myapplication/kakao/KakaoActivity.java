package com.example.myapplication.kakao;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapType;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.MapViewInfo;
import com.kakao.vectormap.camera.CameraPosition;

public class KakaoActivity extends AppCompatActivity {
    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakaomap);
        MapView mapView = findViewById(R.id.map_view);
        mapView.start(new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap kakaoMap) {
                kakaoMap.getCameraPosition(new KakaoMap.OnCameraPositionListener() {
                    @Override
                    public void onCameraPosition(@NonNull CameraPosition cameraPosition) {
                        // CameraPosition 파라미터 값
                    }
                });
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
            }


            @NonNull
            @Override
            public LatLng getPosition() {

                // 지도 시작 시 위치 좌표를 설정
                return LatLng.from(37.406960, 127.115587);
            }

            @Override
            public int getZoomLevel() {
                // 지도 시작 시 확대/축소 줌 레벨 설정
                return 15;
            }

            @Override
            public String getViewName() {
                // KakaoMap 의 고유한 이름을 설정
                return "MyFirstMap";
            }

            @Override
            public boolean isVisible() {
                // 지도 시작 시 visible 여부를 설정
                return true;
            }

            @Override
            public String getTag() {
                // KakaoMap 의 tag 을 설정
                return "FirstMapTag";
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {
                // 인증 후 API 가 정상적으로 실행될 때 호출됨
            }
        });





    }
}

