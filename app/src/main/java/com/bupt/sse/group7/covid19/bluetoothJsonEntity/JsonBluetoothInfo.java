package com.bupt.sse.group7.covid19.bluetoothJsonEntity;

public class JsonBluetoothInfo {

    private int userid;
    private int time_stamp;  //Unix时间戳，单位秒
    private String my_identifier;
    private String target_identifier;
    private float distance;  //距离，单位米
    private int duration;  //持续时长，单位毫秒
    private String date;  //日期

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(int time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getMy_identifier() {
        return my_identifier;
    }

    public void setMy_identifier(String my_identifier) {
        this.my_identifier = my_identifier;
    }

    public String getTarget_identifier() {
        return target_identifier;
    }

    public void setTarget_identifier(String target_identifier) {
        this.target_identifier = target_identifier;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

