package com.bupt.sse.group7.covid19.model;

public class Hospital {
    private int id;
    //医院名称
    private String name;
    //负责人
    private String people;
    private String address;
    private String tel;

    private Statistics statistics;
    private Supplies supplies;

    private String mildLeft;
    private String severeLeft;

    public Hospital(int id, String name, String people, String address, String tel, Statistics statistics, Supplies supplies, String mildLeft, String severeLeft) {
        this.id = id;
        this.name = name;
        this.people = people;
        this.address = address;
        this.tel = tel;
        this.statistics = statistics;
        this.supplies = supplies;
        this.mildLeft = mildLeft;
        this.severeLeft = severeLeft;
    }

    public Hospital() {
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public Supplies getSupplies() {
        return supplies;
    }

    public void setSupplies(Supplies supplies) {
        this.supplies = supplies;
    }

    public String getMildLeft() {
        return mildLeft;
    }

    public void setMildLeft(String mildLeft) {
        this.mildLeft = mildLeft;
    }

    public String getSevereLeft() {
        return severeLeft;
    }

    public void setSevereLeft(String severeLeft) {
        this.severeLeft = severeLeft;
    }
}
