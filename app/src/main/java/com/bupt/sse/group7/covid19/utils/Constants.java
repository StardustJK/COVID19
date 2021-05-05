package com.bupt.sse.group7.covid19.utils;

import com.bupt.sse.group7.covid19.R;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int HEALTHY = 0;
    public static final int CONFIRMED = 1;
    public static final int MILD = 2;
    public static final int SEVERE = 3;
    public static final int DEAD = 4;

    public static final Map<Integer, String> statuses;
    static {
        statuses = new HashMap<>();

//        statuses.put("1", getResources().getString(R.string.confirmed));
//        statuses.put("2", getResources().getString(R.string.mild));
//        statuses.put("3", getResources().getString(R.string.severe));
//        statuses.put("4", getResources().getString(R.string.dead));
//        statuses.put("0", getResources().getString(R.string.cured));

        statuses.put(1, "确诊");
        statuses.put(2, "轻症");
        statuses.put(3, "重症");
        statuses.put(4, "死亡");
        statuses.put(0, "健康");
    }

    public static String types[] = {"全部", "飞机", "火车", "地铁", "公交车", "出租车", "轮船"};
    public static Map<String,Integer> typeMap;
    static {
        typeMap =new HashMap<>();
        typeMap.put("飞机",1);
        typeMap.put("火车",2);
        typeMap.put("地铁",3);
        typeMap.put("大巴",4);
        typeMap.put("公交车",5);
        typeMap.put("出租车",6);
        typeMap.put("轮船",7);
        typeMap.put("其他",8);
    }

}
