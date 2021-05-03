package com.bupt.sse.group7.covid19.model;

public class HospitalBrief {
    private String h_id;
    private String name;
    private String address;

    public HospitalBrief(String h_id, String name) {
        this.h_id = h_id;
        this.name = name;
    }

    public String getH_id() {
        return h_id;
    }

    public void setH_id(String h_id) {
        this.h_id = h_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
