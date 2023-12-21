package com.example.myapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityKakaomapBinding
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


class KakaoMapView : AppCompatActivity() {
    // 도움 함수로 사용할 상수 및 변수 정의
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4"
    }

    private lateinit var binding: ActivityKakaomapBinding
    private val listItems = arrayListOf<ListLayout>()
    private val listAdapter = ListAdapter(listItems)
    private var pageNumber = 1
    private var keyword = ""
    private lateinit var searchResult: ResultSearchKeyword
    private var isStartButtonClicked: Boolean = false
    private var isEndButtonClicked: Boolean = false









    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaomapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isStartButtonClicked = false
        isEndButtonClicked = false


        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter






        setupItemClickListener()



        // 검색 버튼 클릭 시 동작 설정
        binding.startbtnSearch.setOnClickListener {
            // 출발지와 목적지에 대한 처리 추가 예정


            Log.d("ButtonClick", "Start Button Clicked")
            isStartButtonClicked = true
            isEndButtonClicked = false
            keyword = binding.etStartLocation.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }

        binding.endbtnSearch.setOnClickListener {

            isStartButtonClicked = false
            isEndButtonClicked = true
            Log.d("ButtonClick", "End Button Clicked")
            keyword = binding.etEndLocation.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }

        // 이전 페이지 버튼 동작 설정
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 다음 페이지 버튼 동작 설정
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }
//        출발지와 목적지의 거리계산
        binding.buttonMath.setOnClickListener {
            val startLocationName = binding.etStartLocation.text.toString()
            val endLocationName = binding.etEndLocation.text.toString()

            // 출발지와 목적지 이름으로 해당 장소의 좌표(x, y)를 찾아오기
            val startLocation = listItems.find { it.name == startLocationName }
            val endLocation = listItems.find { it.name == endLocationName }

            if (startLocation != null && endLocation != null) {
                // 출발지와 목적지의 좌표를 이용하여 거리 계산
                val distance = calculateDistance(
                    MapPoint.GeoCoordinate(startLocation.y, startLocation.x),
                    MapPoint.GeoCoordinate(endLocation.y, endLocation.x)
                )

                // 사용자에게 UI에 출력
                Toast.makeText(
                    this@KakaoMapView,
                    "출발지와 목적지의 거리: ${"%.1f".format(distance)} km",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // 출발지 또는 목적지를 찾을 수 없는 경우
                Toast.makeText(
                    this@KakaoMapView,
                    "출발지 또는 목적지를 찾을 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.buttonNavigate.setOnClickListener {
            Log.d("ButtonNavigate", "Start Location Name: 1")
            val startLocationName = binding.etStartLocation.text.toString()
            val endLocationName = binding.etEndLocation.text.toString()
            Log.d("ButtonNavigate", "Start Location Name: $startLocationName")

            // Find start and end locations in the listItems
            val startLocation = listItems.find { it.name == startLocationName }
            Log.d("ButtonNavigate", "Start Location Name: $startLocationName")
            val endLocation = listItems.find { it.name == endLocationName }
            Log.d("ButtonNavigate", "End Location Name: $endLocationName")

            if (startLocation != null && endLocation != null) {
                // Start navigation
                startKakaoMapNavigation(startLocation, endLocation)
            } else {
                // Inform the user if start or end location is not found
                Toast.makeText(this, "출발지 또는 목적지를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }



        // 위치 서비스 확인 및 권한 요청

    }

    // 두 지점 간의 거리를 계산하는 함수
    private fun calculateDistance(coord1: MapPoint.GeoCoordinate, coord2: MapPoint.GeoCoordinate): Double {
        val radius = 6371.0
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
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        searchResult = responseBody
                        runOnUiThread {
                            addItemsAndMarkers(searchResult)
                        }
                    } else {
                        Log.e("KakaoMapView", "응답 바디가 null입니다.")
                    }
                } else {
                    Log.e("KakaoMapView", "서버 응답이 실패하였습니다. 코드: ${response.code()}")
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
            listItems.clear()
//            binding.mapView12.removeAllPOIItems()

            for (document in searchResult!!.documents) {
                val item = ListLayout(
                    document.place_name,
                    document.category_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)

//                val point = MapPOIItem()
//                point.apply {
//                    itemName = document.place_name
//                    mapPoint = MapPoint.mapPointWithGeoCoord(
//                        document.y.toDouble(),
//                        document.x.toDouble()
//                    )
//                    markerType = MapPOIItem.MarkerType.BluePin
//                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
//                }
//                binding.mapView12.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end
            binding.btnPrevPage.isEnabled = pageNumber != 1
        } else {
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 리스트 아이템 클릭 리스너 설정 함수
    private fun setupItemClickListener() {
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
//                binding.mapView12.removeAllPolylines()


                val clickedPlaceName = listItems[position].name
                Toast.makeText(applicationContext, "장소는 $clickedPlaceName", Toast.LENGTH_SHORT).show()



                // 클릭한 아이템의 place_name startbtnSearch 또는 endbtnSearch 필드에 설정
                if (isStartButtonClicked) {
                    // 출발지 버튼이 클릭된 경우
                    binding.etStartLocation.setText(clickedPlaceName)
                } else if (isEndButtonClicked) {
                    // 목적지 버튼이 클릭된 경우
                    binding.etEndLocation.setText(clickedPlaceName)
                }
//                when (selectedLocationType) {
//                    LocationType.START -> {
//                        // 출발지 버튼이 클릭된 경우
//                        binding.etStartLocation.setText(clickedPlaceName)
//                    }
//                    LocationType.END -> {
//                        // 목적지 버튼이 클릭된 경우
//                        binding.etEndLocation.setText(clickedPlaceName)
//                    }
//                    LocationType.NONE -> {
//                        // 다른 동작 수행
//                    }
//                }

//                var mapPoint =
//                    MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)

//                binding.mapView12.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
        })
    }
    private fun startKakaoMapNavigation(startLocation: ListLayout, endLocation: ListLayout) {
        val intent = Intent(this, KakaoMapActivity::class.java)
        intent.putExtra("startLatitude", startLocation.y)
        intent.putExtra("startLongitude", startLocation.x)
        intent.putExtra("endLatitude", endLocation.y)
        intent.putExtra("endLongitude", endLocation.x)
        intent.putExtra("startplaceName", startLocation.name) // Pass the placeName
        intent.putExtra("endplaceName", endLocation.name) // Pass the placeName

        startActivity(intent)
    }



}