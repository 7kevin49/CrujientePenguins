package com.example.crujientepenguins.apiservices;

import com.example.crujientepenguins.pojos.LoginProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationService {
    @POST("/register")
    Call<Void> register(@Body LoginProfile json);
}
