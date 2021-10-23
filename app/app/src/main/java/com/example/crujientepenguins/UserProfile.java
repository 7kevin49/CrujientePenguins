package com.example.crujientepenguins;

import com.google.gson.annotations.SerializedName;


public class UserProfile {

    @SerializedName("user_id")
        private String user_id;

    @SerializedName("user")
        private String user;
//
//    @SerializedName("points")
//        private int points;

//    @SerializedName("token")
//        private String token;

    public UserProfile(String user_id, String user, int points, String token){
        this.user_id = user_id;
        this.user = user;
//        this.points = points;
//        this.token = token;
    }
}
