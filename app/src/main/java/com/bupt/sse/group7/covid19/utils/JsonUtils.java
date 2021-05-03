package com.bupt.sse.group7.covid19.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtils {
    public static String safeGet(JsonObject obj, String arg) {
        if (obj.get(arg) == null || obj.get(arg).isJsonNull()) {
            return "-";
        }
        else {
            return obj.get(arg).getAsString();
        }
    }
    public static JsonObject input2Json(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        JsonObject outline = (JsonObject) JsonParser.parseString(sb.toString());
        Log.d("DBData", outline.toString());
        return outline;
    }
    public static JsonArray parseInfo(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        JsonObject outline = (JsonObject) JsonParser.parseString(sb.toString());
        JsonArray rows = (JsonArray) outline.get("rows");
        Log.d("lyjDBData", rows.toString());
        return rows;
    }

    public static String inputStream2String(InputStream in) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        String outline = sb.toString();
        return outline;

    }
}
