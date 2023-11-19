package com.example.myapplication.kakao

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySearchBinding
import com.kakao.sdk.common.KakaoSdk

class chogiActivity  : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 295a13ed8bce4051b8a5df8dad90dee4"  // REST API 키
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "0e22b0c0a249fcdfc0683fb1afdcbe52")
    }
}