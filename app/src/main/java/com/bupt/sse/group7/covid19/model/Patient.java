package com.bupt.sse.group7.covid19.model;

import java.util.ArrayList;
import java.util.List;

public class Patient {
    private String id;
    private String h_name;
    private String username;
    private String status;
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

    public String getH_name() {
        return h_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setH_name(String h_name) {
        this.h_name = h_name;
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

