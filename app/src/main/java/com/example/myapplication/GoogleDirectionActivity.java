package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.DirectionResponses;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

@SuppressLint("Registered")
public class GoogleDirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private LatLng fkip;
    private LatLng monas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        fkip = new LatLng(33.4996, 126.5312); // 예시: 제주도의 좌표
        monas = new LatLng(33.2385, 126.5500); // 예시: 제주도의 다른 지역 좌표

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        MarkerOptions markerFkip = new MarkerOptions()
                .position(fkip)
                .title("FKIP");
        MarkerOptions markerMonas = new MarkerOptions()
                .position(monas)
                .title("Monas");

        map.addMarker(markerFkip);
        map.addMarker(markerMonas);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(monas, 11.6f));


        String fromFKIP = String.valueOf(fkip.latitude) + "," + String.valueOf(fkip.longitude);
        String toMonas = String.valueOf(monas.latitude) + "," + String.valueOf(monas.longitude);

        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("origin", fromFKIP);
        queryParameters.put("destination", toMonas);
        queryParameters.put("key", getString(R.string.google_maps_key));
        queryParameters.put("mode", "transit");


        ApiServices apiServices = RetrofitClient.apiServices(this);
        apiServices.getDirection(queryParameters)
                .enqueue(new Callback<DirectionResponses>() {
                    @Override
                    public void onResponse(@NonNull Call<DirectionResponses> call, @NonNull Response<DirectionResponses> response) {
                        drawPolyline(response);
                        Log.d("bisa dong oke", response.message());

                        // Access travel mode and use it as needed
                        String travelMode = response.body().getTravelMode();
                        Log.d("Travel Mode", "Travel Mode: " + travelMode);
                    }

                    @Override
                    public void onFailure(@NonNull Call<DirectionResponses> call, @NonNull Throwable t) {
                        Log.e("anjir error", t.getLocalizedMessage());
                    }
                });
    }

    private void drawPolyline(@NonNull Response<DirectionResponses> response) {
        if (response.body() != null) {
            if (!response.body().getRoutes().isEmpty()) {
                String shape = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                PolylineOptions polyline = new PolylineOptions()
                        .addAll(PolyUtil.decode(shape))
                        .width(8f)
                        .color(Color.RED);
                map.addPolyline(polyline);
            } else {
                Log.e("drawPolyline", "No routes in the response");
            }
        } else {
            Log.e("drawPolyline", "Response body is null");
        }
    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        Call<DirectionResponses> getDirection(
                @QueryMap Map<String, String> queryParams
        );
    }

    private static class RetrofitClient {
        static ApiServices apiServices(Context context) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(context.getResources().getString(R.string.base_url))
                    .build();

            return retrofit.create(ApiServices.class);
        }
    }
}