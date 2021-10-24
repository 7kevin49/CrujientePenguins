package com.example.crujientepenguins.pojos;

import com.google.gson.annotations.SerializedName;

public class PointsAvailable {
    @SerializedName("points_available")
    private String pointsAvailable;
    private int points;

    public PointsAvailable() {
    }

    public PointsAvailable(String pointsAvailable){
        this.pointsAvailable = pointsAvailable;
    }

    public String getPointsAvailable() {
        return pointsAvailable;
    }

    public void setPointsAvailable(String pointsAvailable) {
        this.pointsAvailable = pointsAvailable;
    }

    public void updatePoints() {
        if(pointsAvailable != null) {
            points = Integer.parseInt(pointsAvailable);
        }
    }
}
