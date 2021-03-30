package com.bupt.sse.group7.covid19.model;

import java.util.ArrayList;
import java.util.List;

public class Patient {
    private String id;
    private String username;
    private int status;
    private boolean auth;
    private List<Status> statuses = new ArrayList<>();
    private List<TrackPoint> trackPoints = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public void setTrackPoints(List<TrackPoint> trackPoints) {
        this.trackPoints = trackPoints;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public List<TrackPoint> getTrackPoints() {
        return trackPoints;
    }
}

