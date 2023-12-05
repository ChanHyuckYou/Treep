package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;
import android.Manifest;

import com.example.myapplication.kakao.ListAdapter;
import com.example.myapplication.kakao.ListLayout;
import com.example.myapplication.network.DaumRest;
import com.example.myapplication.network.KaKaoLocalRest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NearbyRestaurantsActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {
    private static final String BASE_URL = "https://dapi.kakao.com/";
    private static final String LOG_TAG = "NearbyRestaurantsActivity";
    private static final String API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    private int currentPage = 1; // 초기 페이지

    private final DaumRest daumRestService;
    private ArrayList<ListLayout> itemList;
    private ListAdapter adapter;
    RecyclerView recyclerView;
    private MapView mMapView;

    private final String query = "음식점";
    private final int radius = 500;











    public NearbyRestaurantsActivity() {
        KaKaoLocalRest restClient = new KaKaoLocalRest();
        this.daumRestService = restClient.create();
        this.itemList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum);



        mMapView = (MapView) findViewById(R.id.map_view);
        //mMapView.setDaumMapApiKey("0e22b0c0a249fcdfc0683fb1afdcbe52");
        mMapView.setCurrentLocationEventListener((MapView.CurrentLocationEventListener) this);

        recyclerView = findViewById(R.id.rv_list1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // itemList, adapter 초기화
        itemList = new ArrayList<>();  // itemList을 적절한 데이터로 초기화
        adapter = new ListAdapter(itemList);
        recyclerView.setAdapter(adapter);
        KaKaoLocalRest.create();

        onCurrentLocationUpdate(mMapView, mMapView.getMapCenterPoint(), 0f);





        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();

        }

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));

          // 필요에 따라 쿼리를 사용자 정의할 수 있습니다.

            Call<ResultSearchKeyword> call = daumRestService.getSearchKeyword(API_KEY, query, currentPage, radius, mapPointGeo.latitude, mapPointGeo.longitude);

        call.enqueue(new Callback<ResultSearchKeyword>() {
            @Override
            public void onResponse(@NonNull Call<ResultSearchKeyword> call, @NonNull Response<ResultSearchKeyword> response) {
                if (response.isSuccessful()) {
                    // API 응답 처리 및 DaumMap에 음식점 정보 업데이트
                    ResultSearchKeyword result = response.body();
                    if (result != null && result.getDocuments() != null && result.getDocuments().size() > 0) {
                        // 음식점 정보를 추출하고 DaumMap을 업데이트합니다.
                        List<Place> restaurants = result.getDocuments();
                        for (Place restaurant : restaurants) {
                            // 정보를 추출하고 DaumMap에 마커 또는 사용자 정의 로직을 추가합니다.
                            double latitude = Double.parseDouble(restaurant.getY());
                            double longitude = Double.parseDouble(restaurant.getX());
                            String restaurantName = restaurant.getPlace_name();

                            ListLayout item = new ListLayout(
                                    restaurant.getCategory_name(),
                                    restaurant.getRoad_address_name(),
                                    restaurant.getAddress_name(),
                                    latitude,
                                    longitude
                            );
                            itemList.add(item);

                            // DaumMap을 업데이트하는 로직을 추가하세요.
                            // (맵에 마커 또는 사용자 정의 오버레이를 추가해야 할 수 있음)
                            MapPOIItem point = new MapPOIItem();
                            point.setItemName(restaurantName);
                            point.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
                            point.setMarkerType(MapPOIItem.MarkerType.BluePin);
                            point.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

                            mMapView.addPOIItem(point);

                        }
                        Log.d(LOG_TAG, "itemList size: " + itemList.size());
                        // itemList에 데이터가 추가되었으므로 RecyclerView 갱신
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // API 오류 처리
                    Log.e(LOG_TAG, "KakaoRest API 요청 실패");
                }
            }

            @Override
            public void onFailure(Call<ResultSearchKeyword> call, Throwable t) {
                // API 요청 실패 처리
                Log.e(LOG_TAG, "KakaoRest API 요청 실패", t);
            }
        });
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }


    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }


    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }


    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }


    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(NearbyRestaurantsActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(NearbyRestaurantsActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(NearbyRestaurantsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(NearbyRestaurantsActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(NearbyRestaurantsActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(NearbyRestaurantsActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(NearbyRestaurantsActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(NearbyRestaurantsActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
