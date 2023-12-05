package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class FindIdResponse {
    @SerializedName("code")
    int code;

    @SerializedName("message")
    String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}