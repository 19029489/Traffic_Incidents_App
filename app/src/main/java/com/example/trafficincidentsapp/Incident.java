package com.example.trafficincidentsapp;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class Incident {

    private String type;
    private double latitude;
    private double longitude;
    private String message;
    private Date date;

    public Incident(String type, double latitude, double longitude, String message, Date date) {
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
