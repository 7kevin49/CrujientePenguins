package com.example.crujientepenguins.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coupon {
    @SerializedName("id")
    private String id;
    @SerializedName("max_bid")
    private Integer maxBid;
    @SerializedName("winner_id")
    private String winnerId;
    @SerializedName("closeout_time")
    private String closeoutTime;
    @SerializedName("description")
    private String description;

    /**
     * No args constructor for use in serialization
     *
     */
    public Coupon() {
    }

    /**
     *
     * @param closeoutTime
     * @param winnerId
     * @param maxBid
     * @param description
     * @param id
     */
    public Coupon(String id, Integer maxBid, String winnerId, String closeoutTime, String description) {
        super();
        this.id = id;
        this.maxBid = maxBid;
        this.winnerId = winnerId;
        this.closeoutTime = closeoutTime;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(Integer maxBid) {
        this.maxBid = maxBid;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getCloseoutTime() {
        return closeoutTime;
    }

    public void setCloseoutTime(String closeoutTime) {
        this.closeoutTime = closeoutTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
