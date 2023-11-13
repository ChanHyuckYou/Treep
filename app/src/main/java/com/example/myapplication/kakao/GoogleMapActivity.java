package com.example.myapplication.kakao;

import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng destination; // 목적지 좌표

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemapview);

        // 목적지 좌표를 설정합니다.
        destination = new LatLng(37.7749, 126.4194); // 샌프란시스코의 예시 좌표

        // 지도 프래그먼트를 가져옵니다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 현재 위치를 얻어옵니다. (실제 앱에서는 권한 체크 및 위치 서비스 사용 코드가 필요합니다.)
        LatLng currentLocation = new LatLng(37.7749, -122.4194); // 현재 위치 예시 좌표

        // 현재 위치로 지도를 이동합니다.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        // Circle을 추가하여 목적지를 표시합니다.
        CircleOptions circleOptions = new CircleOptions()
                .center(destination)
                .radius(100) // Circle의 반지름 (미터 단위)
                .strokeWidth(2)
                .strokeColor(0xff0000ff) // 테두리 색상 (파란색)
                .fillColor(0x220000ff); // 내부 채우기 색상 (파란색, 투명도 34%)
        mMap.addCircle(circleOptions);

        // 거리를 계산하고 시간을 비례하게 설정하는 로직을 추가하세요.
        double distance = calculateDistance(currentLocation, destination);
        // 시간을 계산하여 사용자에게 표시 또는 처리하는 로직을 추가하세요.
    }

    // 두 좌표 사이의 거리를 계산하는 메서드
    private double calculateDistance(LatLng from, LatLng to) {
        Location fromLocation = new Location("FromLocation");
        fromLocation.setLatitude(from.latitude);
        fromLocation.setLongitude(from.longitude);

        Location toLocation = new Location("ToLocation");
        toLocation.setLatitude(to.latitude);
        toLocation.setLongitude(to.longitude);

        // 미터 단위로 거리를 반환합니다.
        return fromLocation.distanceTo(toLocation);
    }
}
