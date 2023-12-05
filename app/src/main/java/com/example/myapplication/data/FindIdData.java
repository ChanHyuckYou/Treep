package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class FindIdData {
    @SerializedName("userEmail")
    String userEmail;

    public FindIdData(String userEmail) {
        this.userEmail = userEmail;
    }
}