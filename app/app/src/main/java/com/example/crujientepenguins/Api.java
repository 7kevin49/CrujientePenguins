package com.example.crujientepenguins;

import android.widget.Toast;

import com.example.crujientepenguins.apiservices.BiddingService;
import com.example.crujientepenguins.apiservices.CouponService;
import com.example.crujientepenguins.apiservices.LoginService;
import com.example.crujientepenguins.apiservices.PointsService;
import com.example.crujientepenguins.apiservices.RegistrationService;
import com.example.crujientepenguins.pojos.Bid;
import com.example.crujientepenguins.pojos.BidSubmission;
import com.example.crujientepenguins.pojos.Bids;
import com.example.crujientepenguins.pojos.CouponAuction;
import com.example.crujientepenguins.pojos.LoginProfile;
import com.example.crujientepenguins.pojos.LoginToken;
import com.example.crujientepenguins.pojos.Message;
import com.example.crujientepenguins.pojos.PointsAvailable;
import com.example.crujientepenguins.pojos.PointsUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Api instance;

    private Retrofit retrofit;

    private Api(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public static Api getInstance() {
        if (instance == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
                    .create();

            Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:5000/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            instance = new Api(retrofit);
        }
        return instance;
    }

    public void register(LoginProfile profile, Callback<Void> callback) {
        RegistrationService service = retrofit.create(RegistrationService.class);
        Call<Void> call = service.register(profile);
        call.enqueue(callback);
    }

    public void login(LoginProfile profile, Callback<LoginToken> callback) {
        LoginService service = retrofit.create(LoginService.class);
        Call<LoginToken> call = service.getUserToken(profile);
        call.enqueue(callback);
    }

    public void getBids(String token, Callback<Bids> callback) {
        BiddingService service = retrofit.create(BiddingService.class);
        Call<Bids> call = service.getBidsMade(token);
        call.enqueue(callback);
    }

    public void placeBid(String token, BidSubmission submission, Callback<Bids> callback) {
        BiddingService service = retrofit.create(BiddingService.class);
        Call<Bids> call = service.placeBid(token, submission);
        call.enqueue(callback);
    }

    public void updateBid(String token, BidSubmission submission, Callback<Bids> callback) {
        BiddingService service = retrofit.create(BiddingService.class);
        Call<Bids> call = service.updateBid(token, submission);
        call.enqueue(callback);
    }

    public void getCouponAuction(Callback<CouponAuction> callback) {
        CouponService service = retrofit.create(CouponService.class);
        Call<CouponAuction> call = service.getCoupons();
        call.enqueue(callback);
    }

    public void getPoints(String token, Callback<PointsAvailable> callback) {
        PointsService service = retrofit.create(PointsService.class);
        Call<PointsAvailable> call = service.getUserPoints(token);
        call.enqueue(callback);
    }

    public void updatePoints(String token, PointsUpdate update, Callback<Void> callback) {
        PointsService service = retrofit.create(PointsService.class);
        Call<Void> call = service.updatePoints(token, update);
        call.enqueue(callback);
    }

    public void collectOrder(String token, String orderId, Callback<Message> callback) {
        PointsService service = retrofit.create(PointsService.class);
        Call<Message> call = service.collectOrder(token, orderId);
        call.enqueue(callback);
    }

}
