package com.example.myapplication.Kakmap

import android.os.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class KakaoApi(private val apiType: ApiType, private val params: Map<String, String>) :
    AsyncTask<Void, Void, JSONObject>() {
    override fun doInBackground(vararg voids: Void?): JSONObject {
        val apiUrl = when (apiType) {
            ApiType.ADDRESS_SEARCH -> "https://dapi.kakao.com/v2/local/search/address.json"
            ApiType.COORD_TO_REGION -> "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json"
            ApiType.COORD_TO_ADDRESS -> "https://dapi.kakao.com/v2/local/geo/coord2address.json"
            ApiType.COORD_TRANSFORM -> "https://dapi.kakao.com/v2/local/geo/transcoord.json"
            ApiType.KEYWORD_SEARCH -> "https://dapi.kakao.com/v2/local/search/keyword.json"
            ApiType.CATEGORY_SEARCH -> "https://dapi.kakao.com/v2/local/search/category.json"
        }

        val apiKey = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4" // Kakao API 키를 여기에 넣어주세요.
        val url = URL("$apiUrl${createQueryParams(params)}")

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "KakaoAK $apiKey")

        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            return JSONObject(response.toString())
        } finally {
            connection.disconnect()
        }
    }

    override fun onPostExecute(result: JSONObject?) {
        super.onPostExecute(result)
        // 여기서 result를 사용하여 필요한 작업을 수행하세요.
    }

    private fun createQueryParams(params: Map<String, String>): String {
        return params.entries.joinToString("&") { (key, value) -> "$key=$value" }
    }
}

enum class ApiType {
    ADDRESS_SEARCH,
    COORD_TO_REGION,
    COORD_TO_ADDRESS,
    COORD_TRANSFORM,
    KEYWORD_SEARCH,
    CATEGORY_SEARCH
}
