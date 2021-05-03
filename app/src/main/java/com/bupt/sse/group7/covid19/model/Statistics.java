package com.bupt.sse.group7.covid19.model;

public class Statistics {
    private String mild;
    private String severe;
    private String cured;
    private String dead;

    public Statistics(String mild, String severe, String cured, String dead) {
        this.mild = mild;
        this.severe = severe;
        this.cured = cured;
        this.dead = dead;
    }

    public String getMild() {
        return mild;
    }

    public void setMild(String mild) {
        this.mild = mild;
    }

    public String getSevere() {
        return severe;
    }

    public void setSevere(String severe) {
        this.severe = severe;
    }

    public String getCured() {
        return cured;
    }

    public void setCured(String cured) {
        this.cured = cured;
    }

    public String getDead() {
        return dead;
    }

    public void setDead(String dead) {
        this.dead = dead;
    }
}
