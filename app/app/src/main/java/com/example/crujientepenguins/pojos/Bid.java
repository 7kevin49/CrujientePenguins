package com.example.crujientepenguins.pojos;

import com.google.gson.annotations.SerializedName;

public class Bid {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("coupon_id")
    private String couponId;
    @SerializedName("points_spent")
    private Integer pointsSpent;
    @SerializedName("timestamp")
    private String timestamp;

    /**
     * No args constructor for use in serialization
     *
     */
    public Bid() {
    }

    /**
     *
     * @param pointsSpent
     * @param couponId
     * @param userId
     * @param timestamp
     */
    public Bid(String userId, String couponId, Integer pointsSpent, String timestamp) {
        super();
        this.userId = userId;
        this.couponId = couponId;
        this.pointsSpent = pointsSpent;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public Integer getPointsSpent() {
        return pointsSpent;
    }

    public void setPointsSpent(Integer pointsSpent) {
        this.pointsSpent = pointsSpent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
