package com.bupt.sse.group7.covid19.utils.bluetoothModule;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    private static final String TAG = "HttpUtil";


    /**
     * 发送 get 请求
     * @param address
     * @param callback
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        Log.d(TAG, "sendOkHttpRequest: url: "+address);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 发送 post 请求
     * @param address
     * @param requestBody
     * @param callback
     */
    public static void sendOkHttpRequest(String address, RequestBody requestBody, okhttp3.Callback callback){
        Log.d(TAG, "sendOkHttpRequest: url: "+address);
        Log.d(TAG, "sendOkHttpRequest: requestBody: "+requestBody.toString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

}
