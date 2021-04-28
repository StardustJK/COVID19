package com.bupt.sse.group7.covid19.model;

import java.util.Date;

public class BroadcastKey {
    public long ID = -1;
    public Date datetime;//扫描开始时间时间
    public int duration =0;//连接持续时间
    public String MAC_address;//扫描到的WIFIMAC地址
    public String name;//WIFI名字
    public double average_distance;

    public BroadcastKey()
    {}

    public BroadcastKey(Date datetime,int duartion,String address,String name,double average_distance)
    {
        this.datetime = datetime;
        this.duration = duartion;
        this.MAC_address = address;
        this.name = name;
        this.average_distance = average_distance;
    }


    @Override
    public String toString(){
        String result = "";
        result += "id为" + this.ID +"，";
        result += "时间为" + WIFIConnection.DateToString(this.datetime) + "，";
        result += "MAC地址为" + this.MAC_address;
        result += "持续时间为"+ this.duration +"毫秒.";
        result += "平均信号强度为" + this.average_distance;
        return result;
    }

}
