import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.libsDirectory

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 33


    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    //kakao
    implementation ("com.kakao.maps.open:android:2.6.3") //kakao map
    implementation ("com.kakao.sdk:v2-all:2.17.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation ("com.kakao.sdk:v2-user:2.17.0") // 카카오 로그인
    implementation ("com.github.bumptech.glide:glide:4.13.2") // profiimage
    implementation (files("libs/libDaumMapAndroid.jar"))
    implementation ("io.reactivex.rxjava2:rxjava:2.2.21")
//    implementation ("net.daum.android:daum-map-sdk:5.3.0")




//retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.maps:google-maps-services:0.15.0")
    implementation ("com.google.maps.android:android-maps-utils:0.6.2")


    //firebase Auth import
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation ("com.google.code.gson:gson:2.8.2")

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat-resources:1.6.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation(files("libs/com.skt.Tmap_1.75.jar"))

    //kakao SDK
    testImplementation("junit:junit:4.13.2")
    implementation ("com.kakao.sdk:v2-navi:2.18.0")
    implementation ("com.kakaomobility.knsdk:knsdk_ui:1.9.3")






    //naverLogin Connect
    implementation("com.navercorp.nid:oauth-jdk8:5.8.0") // jdk 8
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("com.naver.maps:map-sdk:3.17.0")

    implementation ("com.github.kittinunf.fuel:fuel:2.3.1")





//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}


