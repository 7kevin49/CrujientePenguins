package com.example.crujientepenguins.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CouponAuction {
    @SerializedName("coupon_auction")
    private List<Coupon> couponAuction = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public CouponAuction() {
    }

    /**
     *
     * @param couponAuction
     */
    public CouponAuction(List<Coupon> couponAuction) {
        super();
        this.couponAuction = couponAuction;
    }

    public List<Coupon> getCouponAuction() {
        return couponAuction;
    }

    public void setCouponAuction(List<Coupon> couponAuction) {
        this.couponAuction = couponAuction;
    }

    @Override
    public String toString() {
        return "CouponAuction{" +
                "couponAuction=" + couponAuction +
                '}';
    }
}
