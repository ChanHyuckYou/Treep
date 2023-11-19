package com.example.myapplication.kakao;

import android.app.Application;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.common.KakaoSdk;

public class kakoActivity extends Application {
    public void onCreate()
    {
        super.onCreate();
        KakaoSdk.init(this, "0e22b0c0a249fcdfc0683fb1afdcbe52");
    }
}
