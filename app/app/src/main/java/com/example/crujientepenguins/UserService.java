package com.example.crujientepenguins;

import com.example.crujientepenguins.pojos.LoginProfile;
import com.example.crujientepenguins.pojos.LoginToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/api/v1/login")
    Call<LoginToken> getUserToken(@Body LoginProfile json);
}


