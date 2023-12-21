//package com.example.myapplication
//
//import android.os.AsyncTask
//import net.daum.mf.map.api.MapPoint
//
//private class GetRoadInfoTask : AsyncTask<PointObj, Void, List<MapPoint>>() {
//
//    @Deprecated("Deprecated in Java")
//    override fun doInBackground(vararg params: PointObj?): List<MapPoint> {
//        if (isCancelled) {
//            return emptyList()
//        }
//
//        val pointObj = params[0] ?: return emptyList()
//
//        val url = "https://apis-navi.kakaomobility.com/v1/directions"
//        val origin = "${pointObj.startPoint.lng},${pointObj.startPoint.lat}"
//        val destination = "${pointObj.endPoint.lng},${pointObj.endPoint.lat}"
//
//        val queryParams = mutableMapOf(
//            "origin" to origin,
//            "destination" to destination
//        )
//
//        val requestUrl = "$url?${queryParams.map { "${it.key}=${it.value}" }.joinToString("&")}"
//
//        try {
//            val headers = mapOf(
//                "Authorization" to "KakaoAK $REST_API_KEY",
//                "Content-Type" to "application/json"
//            )
//
//            val (request, response, result) = Fuel.get(requestUrl)
//                .header(headers)
//                .response()
//
//            if (response.statusCode != 200) {
//                throw Exception("Error during API request: ${response.statusCode}")
//            }
//
//            return parseRoadInfo(String(result))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return emptyList()
//    }
//
//    override fun onPostExecute(result: List<MapPoint>?) {
//        if (!isCancelled) {
//            runOnUiThread {
//                try {
//                    if (result != null) {
//                        drawPolyline(result)
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//
//    private fun parseRoadInfo(responseData: String?): List<MapPoint> {
//        val linePath = mutableListOf<MapPoint>()
//
//        try {
//            val jsonObject = JSONObject(responseData)
//            val routes = jsonObject.getJSONArray("routes")
//
//            for (i in 0 until routes.length()) {
//                val sections = routes.getJSONObject(i).getJSONArray("sections")
//
//                for (j in 0 until sections.length()) {
//                    val roads = sections.getJSONObject(j).getJSONArray("roads")
//
//                    for (k in 0 until roads.length()) {
//                        val router = roads.getJSONObject(k)
//                        val vertexes = router.getJSONArray("vertexes")
//
//                        for (index in 0 until vertexes.length() step 2) {
//                            linePath.add(
//                                MapPoint.mapPointWithGeoCoord(
//                                    vertexes.getDouble(index + 1),
//                                    vertexes.getDouble(index)
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return linePath
//    }
//}