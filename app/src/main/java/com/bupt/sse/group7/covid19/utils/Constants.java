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

}
