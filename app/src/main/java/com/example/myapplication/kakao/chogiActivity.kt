package com.example.myapplication.kakao

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.KakaoSdk

class chogiActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "0e22b0c0a249fcdfc0683fb1afdcbe52")
    }
}