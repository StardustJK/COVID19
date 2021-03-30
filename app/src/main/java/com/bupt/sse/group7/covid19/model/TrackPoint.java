package com.bupt.sse.group7.covid19.model;

import com.baidu.mapapi.model.LatLng;

public class TrackPoint {
    public String getLocation() {
        return location;
    }

    private String date_time;
    private String location;
    private String description;
    private LatLng latLng;
    private String userId;

    public TrackPoint(String date_time, String location, String description,LatLng latLng,String userId) {
        this.date_time = date_time;
        this.location = location;
        this.description = description;
        this.latLng=latLng;
        this.userId=userId;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
