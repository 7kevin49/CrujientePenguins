package com.example.crujientepenguins.pojos;

import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;

public class LoginProfile {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public Gson gson;
//
//    @SerializedName("points")
//        private int points;

//    @SerializedName("token")
//        private String token;


    public LoginProfile() {
    }

    public LoginProfile(String user, String password){
        this.username = user;
        this.password = password;
        this.gson = new Gson();
//        this.points = points;
//        this.token = token;
    }

    public String getJson() {
        return gson.toJson(this);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }
}