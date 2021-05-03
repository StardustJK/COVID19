package com.bupt.sse.group7.covid19.model;

public class Supplies {
    private String n95;
    private String surgeon;
    private String ventilator;
    private String clothe;
    private String glasses;
    private String alcohol;
    private String pants;

    public Supplies(String n95, String surgeon, String ventilator, String clothe, String glasses, String alcohol, String pants) {
        this.n95 = n95;
        this.surgeon = surgeon;
        this.ventilator = ventilator;
        this.clothe = clothe;
        this.glasses = glasses;
        this.alcohol = alcohol;
        this.pants = pants;
    }

    public String getN95() {
        return n95;
    }

    public void setN95(String n95) {
        this.n95 = n95;
    }

    public String getSurgeon() {
        return surgeon;
    }

    public void setSurgeon(String surgeon) {
        this.surgeon = surgeon;
    }

    public String getVentilator() {
        return ventilator;
    }

    public void setVentilator(String ventilator) {
        this.ventilator = ventilator;
    }

    public String getClothe() {
        return clothe;
    }

    public void setClothe(String clothe) {
        this.clothe = clothe;
    }

    public String getGlasses() {
        return glasses;
    }

    public void setGlasses(String glasses) {
        this.glasses = glasses;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public String getPants() {
        return pants;
    }

    public void setPants(String pants) {
        this.pants = pants;
    }
}
