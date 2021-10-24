package com.example.crujientepenguins;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class LoginToken {
    @SerializedName("token")
    private String token;

    public LoginToken(String token){
        this.token = token;
    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
