package com.example.crujientepenguins.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Bids {
    @SerializedName("bids_made")
    private List<Bid> bidsMade = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Bids() {
    }

    /**
     *
     * @param bidsMade
     */
    public Bids(List<Bid> bidsMade) {
        super();
        this.bidsMade = bidsMade;
    }

    public List<Bid> getBidsMade() {
        return bidsMade;
    }

    public void setBidsMade(List<Bid> bidsMade) {
        this.bidsMade = bidsMade;
    }
}
