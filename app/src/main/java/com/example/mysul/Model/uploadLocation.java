package com.example.mysul.Model;

public class uploadLocation {
    private double wayLatitude, wayLongtitude;
    private String userID;

    public uploadLocation() {
    }

    public uploadLocation(double wayLatitude, double wayLongtitude, String userID) {
        this.wayLatitude = wayLatitude;
        this.wayLongtitude = wayLongtitude;
        this.userID = userID;
    }

    public double getWayLatitude() {
        return wayLatitude;
    }

    public void setWayLatitude(double wayLatitude) {
        this.wayLatitude = wayLatitude;
    }

    public double getWayLongtitude() {
        return wayLongtitude;
    }

    public void setWayLongtitude(double wayLongtitude) {
        this.wayLongtitude = wayLongtitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
