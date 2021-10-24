package com.example.crujientepenguins.apiservices;

import com.example.crujientepenguins.pojos.Message;
import com.example.crujientepenguins.pojos.PointsAvailable;
import com.example.crujientepenguins.pojos.PointsUpdate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PointsService {
    @GET("/api/v1/points")
    Call<PointsAvailable> getUserPoints(@Header("auth_token") String token);

    @PATCH("/api/v1/points")
    Call<Void> updatePoints(@Header("auth_token") String token, @Body PointsUpdate update);

    @POST("/api/v1/points/{id}")
    Call<Message> collectOrder(@Header("auth_token") String token, @Path("id") String orderId);
}
