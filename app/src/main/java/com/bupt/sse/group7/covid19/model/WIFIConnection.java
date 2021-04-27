package com.bupt.sse.group7.covid19.model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WIFIConnection {
    public long ID = -1;
    public Date datetime;//扫描开始时间时间
    public int duration =0;//连接持续时间
    public String MAC_address;//扫描到的WIFIMAC地址
    public String name;
    public int isSent =0;//是否上传
    public int level;//信号强度

    public WIFIConnection()
    {}

    public WIFIConnection(Date datetime, String address,String name,int level)
    {
        this.datetime = datetime;
        this.name = name;
        this.MAC_address = address;
        this.level = level;
    }


    @Override
    public String toString(){
        String result = "";
        result += "id为" + this.ID +"，";
        result += "时间为" + DateToString(this.datetime) + "，";
        result += "WIFI名字为" + this.name+",";
        result += "MAC地址为" + this.MAC_address;
        result += "持续时间为"+ this.duration +"秒.";
        result += "信号强度为" + this.level;
        result +=" 是否发送" + this.isSent;
        return result;
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.v("Test",strDate);
        Date d=null;
        try {
            d = format.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String DateToString(Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=format.format(date);
        Log.v("Time",s);
        return s;
    }

    public static Date Lastdate(Date date)
    {
        Date lastdate;
        Calendar no = Calendar.getInstance();
        no.setTime(date);
        no.set(Calendar.DATE, no.get(Calendar.DATE) - 28);
        lastdate = no.getTime();
        return lastdate;
    }
}
