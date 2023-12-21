package com.example.myapplication

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityKakaoNaviBinding
import com.github.kittinunf.fuel.Fuel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView
import org.json.JSONObject
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt



class KakaoMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKakaoNaviBinding
    private val polyline = MapPolyline()
    private lateinit var searchResult: ResultSearchKeyword
    companion object {
        const val REST_API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4"
    }
    private lateinit var document: Place
    private var startplaceName: String = "" // Declare startplaceName at the class level
    private var endplaceName: String = "" // Declare endplaceName at the class level

//    private var getRoadInfoTask: GetRoadInfoTask? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapView1234.removeAllPOIItems()


//        val jejuIsland = MapPoint.mapPointWithGeoCoord(33.4996, 126.5312) // 제주도의 위도, 경도 설정
//        binding.mapView1234.setMapCenterPointAndZoomLevel(jejuIsland, 1, true)


        val startLatitude = intent.getDoubleExtra("startLatitude", 0.0)
        val startLongitude = intent.getDoubleExtra("startLongitude", 0.0)
        val endLatitude = intent.getDoubleExtra("endLatitude", 0.0)
        val endLongitude = intent.getDoubleExtra("endLongitude", 0.0)
        startplaceName = intent.getStringExtra("placeName") ?: "Unknown Place"
        endplaceName = intent.getStringExtra("placeName") ?: "Unknown Place"


//        val pointObj = PointObj(startPoint = LatLng(startLatitude, startLongitude), endPoint = LatLng(endLatitude, endLongitude))
//
//        getRoadInfoTask = GetRoadInfoTask(pointObj) { result ->
//            // 이 곳에서 결과(result)를 처리하면 됩니다.
//            if (result != null) {
//                drawPolyline(result)
//            }
//        }

//        getRoadInfoTask?.execute()

        polyline.lineColor = Color.BLUE
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(startLatitude, startLongitude))
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(endLatitude, endLongitude))
        val startPoint = MapPoint.mapPointWithGeoCoord(startLatitude, startLongitude)
        val endPoint = MapPoint.mapPointWithGeoCoord(endLatitude, endLongitude)

        binding.mapView1234.addPolyline(polyline)
        addStartAndEndMarkers(startPoint, endPoint)


        val midPoint = MapPoint.mapPointWithGeoCoord(
            (startLatitude + endLatitude) / 2,
            (startLongitude + endLongitude) / 2
        )
        binding.mapView1234.setMapCenterPointAndZoomLevel(midPoint, 3, true)

        binding.mapView1234.setMapViewEventListener(mapViewEventListener)
    }

//    override fun onDestroy() {
//        super.onDestroy()
////        getRoadInfoTask?.cancel(true)
//    }


    private val mapViewEventListener = object : MapView.MapViewEventListener {
        override fun onMapViewInitialized(p0: MapView?) {}
        override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}
        override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}
        override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
            handlePolylineTap(p1)
        }

        override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}
        override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}
        override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}
        override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}
        override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}
    }

    private fun handlePolylineTap(tapPoint: MapPoint?) {
        if (tapPoint != null) {
            val closestPoint = findClosestPointOnPolyline(tapPoint, polyline)
            val distance = calculateDistance(tapPoint, closestPoint)

            if (distance < 100.0) {
                // Show distance between start and end points
                val distanceText = "Distance between start and end: ${String.format("%.2f", distance)} km"
                Toast.makeText(this, distanceText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findClosestPointOnPolyline(tapPoint: MapPoint, polyline: MapPolyline): MapPoint {
        return polyline.getPoint(0)
    }

    private fun calculateDistance(point1: MapPoint, point2: MapPoint): Double {
        val radius = 6371.0
        val dLat =
            Math.toRadians(point2.mapPointGeoCoord.latitude - point1.mapPointGeoCoord.latitude)
        val dLon =
            Math.toRadians(point2.mapPointGeoCoord.longitude - point1.mapPointGeoCoord.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(point1.mapPointGeoCoord.latitude)) * cos(Math.toRadians(point2.mapPointGeoCoord.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radius * c
    }

    private fun addStartAndEndMarkers(startPoint: MapPoint, endPoint: MapPoint) {
        val startMarker = MapPOIItem()
        startMarker.itemName = startplaceName
        startMarker.mapPoint = startPoint
        startMarker.markerType = MapPOIItem.MarkerType.CustomImage
        startMarker.customImageResourceId = R.drawable.custom_poi_marker_start
        startMarker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        val endMarker = MapPOIItem()
        endMarker.itemName = endplaceName
        endMarker.mapPoint = endPoint
        endMarker.markerType = MapPOIItem.MarkerType.CustomImage

        endMarker.customImageResourceId = R.drawable.custom_poi_marker_end
        endMarker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        binding.mapView1234.addPOIItem(startMarker)
        binding.mapView1234.addPOIItem(endMarker)
    }


//    private fun drawPolyline(routeWaypoints: List<MapPoint>) {
//        polyline.lineColor = Color.BLUE
//        binding.mapView1234.removeAllPolylines()
//
//        for (waypoint in routeWaypoints) {
//            polyline.addPoint(waypoint)
//        }
//
//        binding.mapView1234.addPolyline(polyline)
//        binding.mapView1234.setMapCenterPoint(routeWaypoints[0], true)
//    }
////
//    class GetRoadInfoTask(
//        private val pointObj: PointObj,
//        private val callback: (List<MapPoint>?) -> Unit
//    ) : AsyncTask<Void, Void, List<MapPoint>>() {
//        private var exception: Exception? = null
//
//        override fun doInBackground(vararg params: Void?): List<MapPoint> {
//            if (isCancelled) {
//                return emptyList()
//            }
//
//            val url = "https://apis-navi.kakaomobility.com/v1/directions"
//            val origin = "${pointObj.startPoint.longitude},${pointObj.startPoint.latitude}"
//            val destination = "${pointObj.endPoint.longitude},${pointObj.endPoint.latitude}"
//
//            val queryParams = mutableMapOf(
//                "origin" to origin,
//                "destination" to destination
//            )
//
//            val requestUrl = "$url?${queryParams.map { "${it.key}=${it.value}" }.joinToString("&")}"
//
//            try {
//                val headers = mapOf(
//                    "Authorization" to "KakaoAK $REST_API_KEY",
//                    "Content-Type" to "application/json"
//                )
//
//                val (request, response, result) = Fuel.get(requestUrl)
//                    .header(headers)
//                    .response()
//
//                if (response.statusCode != 200) {
//                    throw Exception("Error during API request: ${response.statusCode}")
//                }
//
//                return parseRoadInfo(String(response.data))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            return emptyList()
//        }
//
//        override fun onPostExecute(result: List<MapPoint>?) {
//            if (!isCancelled) {
//                if (exception != null) {
//                    // Handle the exception, e.g., log it or show an error message
//                    exception?.printStackTrace()
//                } else {
//                    // Execute the callback on the UI thread
//                    callback(result)
//                }
//            }
//        }
//
//        private fun parseRoadInfo(responseData: String?): List<MapPoint> {
//            val linePath = mutableListOf<MapPoint>()
//
//            try {
//                val jsonObject = JSONObject(responseData)
//                val routes = jsonObject.getJSONArray("routes")
//
//                for (i in 0 until routes.length()) {
//                    val sections = routes.getJSONObject(i).getJSONArray("sections")
//
//                    for (j in 0 until sections.length()) {
//                        val roads = sections.getJSONObject(j).getJSONArray("roads")
//
//                        for (k in 0 until roads.length()) {
//                            val router = roads.getJSONObject(k)
//                            val vertexes = router.getJSONArray("vertexes")
//
//                            for (index in 0 until vertexes.length() step 2) {
//                                linePath.add(
//                                    MapPoint.mapPointWithGeoCoord(
//                                        vertexes.getDouble(index + 1),
//                                        vertexes.getDouble(index)
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            return linePath
//        }
//    }
//}
//data class PointObj(
//    val startPoint: com.example.myapplication.LatLng,
//    val endPoint: com.example.myapplication.LatLng
//)
//
//data class LatLng(val latitude: Double, val longitude: Double) {
//    // Add a factory function to create instances of LatLng
//    companion object {
//        fun create(latitude: Double, longitude: Double): com.example.myapplication.LatLng {
//            return LatLng(latitude, longitude)
//        }
//    }
}