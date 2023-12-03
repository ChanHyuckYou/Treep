package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivitySearchBinding
import com.example.myapplication.kakao.ListAdapter
import com.example.myapplication.kakao.ListLayout
import com.example.myapplication.network.KakaoRest
import net.daum.android.map.MapView
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val jejuIsland = MapPoint.mapPointWithGeoCoord(33.4996, 126.5312) // 제주도의 위도, 경도 설정
        binding.mapView12.setMapCenterPointAndZoomLevel(jejuIsland, 1, true)




        // 리사이클러 뷰
        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                Log.d("SearchActivity","dasdasdasdasdas3")
                var mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                circle = MapCircle(
                    mapPoint, // 선택한 위치를 중심으로
                    100000, // 반경
                    Color.argb(128, 255, 0, 0), // 테두리 색상
                    Color.argb(128, 0, 255, 0) // 채우기 색상
                )
                binding.mapView12.addCircle(circle)
                Log.d("SearchActivity","dasdasdasdasdas1")

                // 선택한 위치의 주변 관광지에 대한 마커 추가
                addNearbyAttractionsMarkers(mapPoint)
                Log.d("SearchActivity","dasdasdasdasdas2")


                binding.mapView12.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
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

    //Circle생성
//    private fun MapCircle(mMapCircle: MapCircle) {
//        MapCircle(
//            MapPoint.mapPointWithGeoCoord(37.537094, 127.005470), // center
//            500, // radius
//            Color.argb(128, 255, 0, 0), // strokeColor
//            Color.argb(128, 0, 255, 0) // fillColor
//        );
//    }




    // 키워드 검색 함수
    private fun addNearbyAttractionsMarkers(selectedLocation: MapPoint) {
        Log.d("SearchActivity", "addNearbyAttractionsMarkers called")
        circle?.let { circle ->
            searchResult?.let { result ->
                val touristAttractions = result.documents
                    .filter { isTouristAttraction(it.category_name) }
                    .filter { isLocationInsideCircle(MapPoint.mapPointWithGeoCoord(it.y.toDouble(), it.x.toDouble()), circle) }

                for (document in touristAttractions) {
                    val attractionLocation = MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())

                    val poiItem = MapPOIItem()
                    poiItem.apply {
                        itemName = document.place_name
                        mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                            document.x.toDouble())
                        markerType = MapPOIItem.MarkerType.YellowPin
                        selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    }

                    // UI 업데이트를 메인 스레드에서 수행
                    runOnUiThread {
                        Log.d("SearchActivity", "Adding marker for ${document.place_name}")
                        binding.mapView12.addPOIItem(poiItem)
                    }
                }
            }
        }
    }


    // 관광지 여부를 확인하는 함수
    private fun isLocationInsideCircle(location: MapPoint, circle: MapCircle): Boolean {
        val distance = calculateDistance(location.mapPointGeoCoord, circle.center.mapPointGeoCoord)
        return distance <= circle.radius
    }

    // 관광지 여부를 확인하는 함수
    private fun isTouristAttraction(categoryName: String): Boolean {
        // 여기에서는 간단하게 "음식점"이라는 키워드가 포함되어 있는지 여부로 판단
        return categoryName.contains("여행")
    }

    // 두 지점 간의 거리를 계산하는 함수
    private fun calculateDistance(coord1: MapPoint.GeoCoordinate, coord2: MapPoint.GeoCoordinate): Double {
        val x = coord1.longitude - coord2.longitude
        val y = coord1.latitude - coord2.latitude
        return Math.sqrt(x * x + y * y)
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
                val item = ListLayout(document.category_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
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

    fun onCurrentLocationDeviceHeadingUpdate(mapView: MapView, headingAngle: Float) {
        // 여기에 메소드의 구현 내용을 추가하세요
        binding.mapView12.currentLocationTrackingMode
    }



}