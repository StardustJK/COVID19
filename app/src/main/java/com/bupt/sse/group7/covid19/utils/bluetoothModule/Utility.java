package com.bupt.sse.group7.covid19.utils.bluetoothModule;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utility {

    private static final String TAG = "Utility";

    /**
     * 根据 RSSI 值，计算大概的距离
     * @param rssi
     * @return
     */
    public static float getDistanceByRSSI(int rssi){
        int RSSI = Math.abs(rssi);
        return (float) Math.pow(10, (RSSI - 59) / (10 * 2.0));
    }


    /**
     * 获得 n天前的日期的字符串
     * @return
     */
    public static String getDateString(int n){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(new Date(date.getTime() - (long) n * 24 * 60 * 60 * 1000 ));
        return dateString;
    }

    /**
     * 计算并获得date1和date2之间相隔的天数，
     * date1在前，date2在后
     * @param dateString1
     * @param dateString2
     * @return
     * @throws ParseException
     */
    public static int getDaysBetween(String dateString1, String dateString2) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = dateFormat.parse(dateString1);
        Date date2 = dateFormat.parse(dateString2);
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        return (int)((time2 - time1) / (1000*60*60*24));
    }

}
