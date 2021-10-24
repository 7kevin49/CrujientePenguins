package com.example.crujientepenguins;

import org.json.JSONArray;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @POST("/api/v1/login")
    Call<LoginToken> getUserToken(@Body LoginProfile json);
}


