package com.example.crujientepenguins;

import com.google.gson.annotations.SerializedName;

public class LoginToken {
    @SerializedName("token")
    private String token;

    public LoginToken(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
