package com.example.crujientepenguins;

import com.example.crujientepenguins.pojos.PointsAvailable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserServicePoints {
    @GET("/api/v1/points")
    Call<PointsAvailable> getUserPoints(@Header("auth_token") String json);
//    Call<PointsAvailable> getUserPoints();
}
