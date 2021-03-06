package com.bupt.sse.group7.covid19.model;

public class BusTrack {
    String busId;
    String userId;
    String name;
    String start;
    String end;
    String dateTime;

    public BusTrack(String id, String userId, String name, String start, String end, String dateTime) {
        this.busId = id;
        this.userId = userId;
        this.name = name;
        this.start = start;
        this.end = end;
        this.dateTime = dateTime;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
