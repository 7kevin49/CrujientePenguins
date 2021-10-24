package com.example.crujientepenguins.pojos;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;


public class UserProfile {

    @SerializedName("username")
        private String username;

    @SerializedName("password")
        private String password;
//
//    @SerializedName("points")
//        private int points;

//    @SerializedName("token")
//        private String token;


    public UserProfile() {
    }

    public UserProfile(String username, String password){
        this.username = username;
        this.password = password;
//        this.points = points;
//        this.token = token;
    }

    public String getJson(){
        Gson gson = new Gson();
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
}
