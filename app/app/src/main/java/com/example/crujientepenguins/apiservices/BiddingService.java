package com.example.crujientepenguins.apiservices;

import com.example.crujientepenguins.pojos.BidSubmission;
import com.example.crujientepenguins.pojos.Bids;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;

public interface BiddingService {
    @GET("/api/v1/bidding")
    Call<Bids> getBidsMade(@Header("auth_token") String token);

    @PUT("/api/v1/bidding")
    Call<Bids> placeBid(@Header("auth_token") String token, @Body BidSubmission json);

    @PATCH("/api/v1/bidding")
    Call<Bids> updateBid(@Header("auth_token") String token, @Body BidSubmission json);
}
