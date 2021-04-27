package com.bupt.sse.group7.covid19.model;

import java.util.Date;

public class BroadcastKey {
    public long ID = -1;
    public Date datetime;//扫描开始时间时间
    public int duration =0;//连接持续时间
    public String MAC_address;//扫描到的WIFIMAC地址
    public String name;//WIFI名字
    public int low_level;//最低强度
    public int high_level;//最高强度

    public BroadcastKey()
    {}

    public BroadcastKey(Date datetime,int duartion,String address,String name,int low_level,int high_level)
    {
        this.datetime = datetime;
        this.duration = duartion;
        this.MAC_address = address;
        this.name = name;
        this.low_level = low_level;
        this.high_level = high_level;
    }


    @Override
    public String toString(){
        String result = "";
        result += "id为" + this.ID +"，";
        result += "时间为" + WIFIConnection.DateToString(this.datetime) + "，";
        result += "MAC地址为" + this.MAC_address;
        result += "持续时间为"+ this.duration +"毫秒.";
        result += "最低信号强度为" + this.low_level;
        result += "最高信号强度为" + this.high_level;
        return result;
    }

}
