package com.example.crujientepenguins.pojos;

import com.google.gson.annotations.SerializedName;

public class PointsUpdate {
    @SerializedName("delta_points")
    private int deltaPoints;

    public PointsUpdate() {
    }

    public PointsUpdate(int deltaPoints) {
        this.deltaPoints = deltaPoints;
    }

    public int getDeltaPoints() {
        return deltaPoints;
    }

    public void setDeltaPoints(int deltaPoints) {
        this.deltaPoints = deltaPoints;
    }
}
