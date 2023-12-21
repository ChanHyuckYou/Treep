package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivitySearchBinding
import com.example.myapplication.kakao.ListAdapter
import com.example.myapplication.kakao.ListLayout
import com.example.myapplication.network.KakaoRest
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4"  // REST API 키
    }

    private lateinit var binding: ActivitySearchBinding
    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드
    private lateinit var searchResult: ResultSearchKeyword


    private var circle: MapCircle? = null
    private var radius = 500
    private val walkingRoutePolylines = mutableListOf<MapPolyline>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val jejuIsland = MapPoint.mapPointWithGeoCoord(33.4996, 126.5312) // 제주도의 위도, 경도 설정
        binding.mapView12.setMapCenterPointAndZoomLevel(jejuIsland, 1, true)


        // 리사이클러 뷰
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                binding.mapView12.removeAllPolylines()
                Log.d("SearchActivity", "dasdasdasdasdas3")
                val selectedLocation =
                    MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                val currentLocation = binding.mapView12.mapCenterPoint.mapPointGeoCoord

                // 거리 계산
                val distance = calculateDistance(currentLocation, selectedLocation.mapPointGeoCoord)
                Toast.makeText(this@SearchActivity, "목적지 까지의 거리: ${"%.1f".format(distance)} km", Toast.LENGTH_SHORT).show()

                // Polyline 추가
                val polyline = MapPolyline()
                polyline.tag = 123411
                Log.d("SearchActivity", "Map Center: ${binding.mapView12.mapCenterPoint.mapPointGeoCoord.latitude}, ${binding.mapView12.mapCenterPoint.mapPointGeoCoord.longitude}")
                Log.d("SearchActivity", "Current Location: ${currentLocation.latitude}, ${currentLocation.longitude}")

                val numOfPoints = 300 // 추가할 점의 수 (조절 가능)
                for (i in 0..numOfPoints) {
                    val t = i.toDouble() / numOfPoints
                    val intermediateLat =
                        currentLocation.latitude + t * (selectedLocation.mapPointGeoCoord.latitude - currentLocation.latitude)
                    val intermediateLon =
                        currentLocation.longitude + t * (selectedLocation.mapPointGeoCoord.longitude - currentLocation.longitude)
                    polyline.addPoint(
                        MapPoint.mapPointWithGeoCoord(
                            intermediateLat,
                            intermediateLon
                        )
                    )
                }

                polyline.lineColor = Color.argb(128, 255, 0, 0)
                binding.mapView12.addPolyline(polyline)


                var mapPoint =
                    MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                circle = MapCircle(
                    mapPoint, // 선택한 위치를 중심으로
                    1000, // 반경
                    Color.argb(128, 255, 0, 0), // 테두리 색상
                    Color.argb(128, 0, 255, 0) // 채우기 색상
                )
                binding.mapView12.addCircle(circle)
                Log.d("SearchActivity", "dasdasdasdasdas1")
                binding.mapView12.setMapCenterPointAndZoomLevel(mapPoint, 1, true)

                // 선택한 위치의 주변 관광지에 대한 마커 추가
                addNearbyAttractionsMarkers(mapPoint)
                Log.d("SearchActivity", "dasdasdasdasdas2")
                val midPoint = findMidPoint(currentLocation, selectedLocation.mapPointGeoCoord)
                binding.mapView12.setMapCenterPointAndZoomLevel(
                    MapPoint.mapPointWithGeoCoord(midPoint.latitude, midPoint.longitude),
                    3,
                    true
                )


//                val intent = Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("kakaomap://route?sp=$currentLocation&ep=$selectedLocation&by=FOOT")
//                )
//
//                if (intent.resolveActivity(packageManager) != null) {
//                    startActivity(intent)
//                } else {
//                    // Kakao 지도 앱이 설치되어 있지 않은 경우, Play 스토어로 이동
//                    val playStoreIntent = Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("market://details?id=net.daum.android.map")
//                    )
//                    startActivity(playStoreIntent)
//                }
            }


        })


        // 검색 버튼
        binding.btnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }

        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

    }



    private fun addNearbyAttractionsMarkers(selectedLocation: MapPoint) {
        Log.d("SearchActivity", "addNearbyAttractionsMarkers called")
        searchResult?.let { result ->
            for (document in result.documents) {
                val attractionLocation =
                    MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())

                val poiItem = MapPOIItem()
                poiItem.apply {
                    itemName = document.place_name
                    mapPoint =
                        MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())
                    markerType =
                        if (isLocationInsideCircle(attractionLocation, selectedLocation, radius)) {
                            MapPOIItem.MarkerType.YellowPin
                        } else {
                            MapPOIItem.MarkerType.BluePin
                        }
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }

                // UI 업데이트를 메인 스레드에서 수행
                runOnUiThread {
                    Log.d("SearchActivity", "Adding marker for ${document.place_name}")
                    if (isLocationInsideCircle(attractionLocation, selectedLocation, radius)) {
                        binding.mapView12.addPOIItem(poiItem)
                    } else {
                        binding.mapView12.removePOIItem(poiItem)
                    }
                }
            }
        }
    }

    // 관광지 여부를 확인하는 함수
    private fun isLocationInsideCircle(
        location: MapPoint,
        circleCenter: MapPoint,
        circleRadius: Int
    ): Boolean {
        val distance = calculateDistance(location.mapPointGeoCoord, circleCenter.mapPointGeoCoord)
        return distance <= circleRadius
    }


    // 두 지점 간의 거리를 계산하는 함수
    private fun calculateDistance(
        coord1: MapPoint.GeoCoordinate,
        coord2: MapPoint.GeoCoordinate
    ): Double {
        val radius = 6371.0 // 지구 반지름 (단위: km)
        val dLat = Math.toRadians(coord2.latitude - coord1.latitude)
        val dLon = Math.toRadians(coord2.longitude - coord1.longitude)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(coord1.latitude)) * Math.cos(Math.toRadians(coord2.latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return radius * c
    }

    // 키워드 검색 함수
    fun searchKeyword(keyword: String, page: Int) {
        val retrofit = Retrofit.Builder()          // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoRest::class.java)            // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, page)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공
                searchResult = response.body()!!
                runOnUiThread {
                    addItemsAndMarkers(searchResult)
                }
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }


    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            binding.mapView12.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(
                    document.category_name,
                    document.place_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(
                        document.y.toDouble(),
                        document.x.toDouble()
                    )
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView12.addPOIItem(point)


            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
    private fun findMidPoint(currentLocation: MapPoint.GeoCoordinate, selectedLocation: MapPoint.GeoCoordinate): MapPoint.GeoCoordinate {
        val midLatitude = (currentLocation.latitude + selectedLocation.latitude) / 2
        val midLongitude = (currentLocation.longitude + selectedLocation.longitude) / 2
        return MapPoint.GeoCoordinate(midLatitude, midLongitude)
    }
//    private val currentLocationMarker: MapPOIItem by lazy {
//        val marker = MapPOIItem()
//        marker.itemName = "현재 위치"
//        marker.markerType = MapPOIItem.MarkerType.CustomImage
//        marker.customImageResourceId = android.R.drawable.ic_menu_mylocation
//        marker.isCustomImageAutoscale = false
//        marker.setCustomImageAnchor(0.5f, 1.0f)
//        marker.isShowCalloutBalloonOnTouch = false
//        marker
//    }
//    private fun checkLocationPermission(): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        } else {
//            true
//        }
//    }

    // 위치 권한 요청
//    private fun requestLocationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }

    // ... (이전 코드 생략)



//    private fun getWalkingRoute(startX: Double, startY: Double, endX: Double, endY: Double) {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val api = retrofit.create(KakaoRest::class.java)
//        val call = api.getWalkingRoute(
//            API_KEY,
//            startX,
//            startY,
//            endX,
//            endY,
//
//        )
//
//        call.enqueue(object : Callback<WalkingRoute> {
//            override fun onResponse(call: Call<WalkingRoute>, response: Response<WalkingRoute>) {
//                if (response.isSuccessful) {
//                    val walkingRoute = response.body()
//                    handleWalkingRoute(walkingRoute)
//                } else {
//                    Log.e("WalkingRoute", "Failed to get walking route: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<WalkingRoute>, t: Throwable) {
//                Log.e("WalkingRoute", "Error getting walking route: ${t.message}")
//            }
//        })
//    }
//
//    // 추가된 함수: Walking Route를 처리하는 함수
//    private fun handleWalkingRoute(walkingRoute: WalkingRoute?) {
//        walkingRoute?.let {
//            // 여기에서 Walking Route에 관련된 처리를 수행합니다.
//            // walkingRoute.sections 등을 사용하여 정보를 활용할 수 있습니다.
//        }
//    }



}



