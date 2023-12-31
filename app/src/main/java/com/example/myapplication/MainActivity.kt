package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.kakao.sdk.user.UserApiClient


public class MainActivity : AppCompatActivity() {

    lateinit var  binding : ActivityMainBinding
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val button2 : Button = findViewById(R.id.kakaoMap)
        button2.setOnClickListener {
            val kakaointent = Intent(this, SearchActivity::class.java)
            startActivity(kakaointent)
        }

        val settingbutton : ImageButton = findViewById(R.id.setting_button)
        settingbutton.setOnClickListener {
            val settingintent = Intent(this, SettingActivity::class.java)
            startActivity(settingintent)
        }

        val LocalInfobtn : Button = findViewById(R.id.localinfobtn)
        LocalInfobtn.setOnClickListener {
            val Localinfointent = Intent(this, LocalInfoActivity::class.java)
            startActivity(Localinfointent)
        }
        val NearbyRestaurants : Button = findViewById(R.id.NearbyRestaurants)
        NearbyRestaurants.setOnClickListener {
            val NearbyRestaurantsintent = Intent(this, NearbyRestaurantsActivity::class.java)
            startActivity(NearbyRestaurantsintent)
        }
        val circleinlocal : Button = findViewById(R.id.circleinlocal)
        circleinlocal.setOnClickListener {
            val circleinlocalintent = Intent(this, KakaoMapView::class.java)
            startActivity(circleinlocalintent)
        }
        val goingcafebtn : Button = findViewById(R.id.goingcafe)
        goingcafebtn.setOnClickListener {
            val goingcafeintent = Intent(this, LocalCafeActivity::class.java)
            startActivity(goingcafeintent)
        }
    }
}