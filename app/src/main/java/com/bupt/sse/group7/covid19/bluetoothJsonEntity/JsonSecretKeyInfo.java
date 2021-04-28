package com.bupt.sse.group7.covid19.bluetoothJsonEntity;

public class JsonSecretKeyInfo {
    private int userid;
    private String secretkey;
    private String date;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
