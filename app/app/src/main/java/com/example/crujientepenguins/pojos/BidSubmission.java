package com.example.crujientepenguins.pojos;

import com.google.gson.annotations.SerializedName;

public class BidSubmission {
    @SerializedName("coupon_id")
    private String couponId;
    @SerializedName("bid_amount")
    private Integer bidAmount;

    /**
     * No args constructor for use in serialization
     *
     */
    public BidSubmission() {
    }

    /**
     *
     * @param bidAmount
     * @param couponId
     */
    public BidSubmission(String couponId, Integer bidAmount) {
        super();
        this.couponId = couponId;
        this.bidAmount = bidAmount;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public Integer getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Integer bidAmount) {
        this.bidAmount = bidAmount;
    }
}
