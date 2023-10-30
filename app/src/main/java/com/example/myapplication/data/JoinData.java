package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class JoinData {
    @SerializedName("userName")
    private String userName;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userPwd")
    private String userPwd;

    @SerializedName("userBirth")
    private  String userBirth;

    @SerializedName("userNick")
    private  String userNick;

    public JoinData(String userName, String userEmail, String userPwd, String userBirth, String userNick) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPwd = userPwd;
        this.userBirth = userBirth;
        this.userNick = userNick;
    }
}