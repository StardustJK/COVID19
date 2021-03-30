package com.bupt.sse.group7.covid19.model;

public class Status {
    private String day;
    private int status;

    public Status(String day, int status) {
        this.day = day;
        this.status = status;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}