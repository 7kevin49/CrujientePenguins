package com.example.crujientepenguins;

import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;

public class LoginProfile {

    @SerializedName("user")
    private String user_id;

    @SerializedName("password")
    private String password;

    public Gson gson;
//
//    @SerializedName("points")
//        private int points;

//    @SerializedName("token")
//        private String token;

    public LoginProfile(String user, String password){
        this.user_id = user;
        this.password = password;
        this.gson = new Gson();
//        this.points = points;
//        this.token = token;
    }

    public String getJson() {
        return gson.toJson(this);
    }
}