package com.example.crujientepenguins.apiservices;

import com.example.crujientepenguins.pojos.CouponAuction;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CouponService {
    @GET("/api/v1/coupons")
    Call<CouponAuction> getCoupons();
}
