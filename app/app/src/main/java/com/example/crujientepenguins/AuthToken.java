package com.example.crujientepenguins;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class AuthToken {
    @SerializedName("auth_token")
    private String authToken;

    public AuthToken(String token){
        this.authToken = token;
    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getToken() {
        return authToken;
    }

    public void setToken(String token) {
        this.authToken = token;
    }
}
