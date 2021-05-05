package com.bupt.sse.group7.covid19.model;

import com.baidu.mapapi.model.LatLng;

public class TrackPoint {
    public String getLocation() {
        return location;
    }

    private String date_time;
    private String location;//街道地址
    private String description;//地点描述
    private String city;//市
    private String district;//区
    private LatLng latLng;
    private int userId;


    public TrackPoint(String date_time, String location, String description,LatLng latLng,int userId,String city,String district) {
        this.date_time = date_time;
        this.location = location;
        this.description = description;
        this.latLng=latLng;
        this.userId=userId;
        this.city=city;
        this.district=district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
