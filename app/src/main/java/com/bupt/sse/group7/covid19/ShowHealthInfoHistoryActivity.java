package com.bupt.sse.group7.covid19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bupt.sse.group7.covid19.adapter.HealthInfoHistoryAdapter;
import com.bupt.sse.group7.covid19.bluetoothActivity.BluetoothActivity;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.HealthInfo;
import com.bupt.sse.group7.covid19.model.Status;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShowHealthInfoHistoryActivity extends AppCompatActivity {
    private static final String TAG = "ShowHealthInfoHistory";
    private static final String API_URL = "http://192.168.43.129:3030/";
//    private static final String API_URL = "http://39.97.212.229:3030/";
    private static final int UPDATE_RE_VIEW = 1;


    private List<HealthInfo> healthInfoList = new ArrayList<>();
    private HealthInfoHistoryAdapter adapter;
    private RecyclerView recyclerView;
    private SharedPreferences pref;


    // handler用于线程通信
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_RE_VIEW:
                    String healthInfoListString = pref.getString("healthInfoList", null);
                    Gson gson = new Gson();
                    healthInfoList.clear();
                    healthInfoList.addAll(gson.fromJson(healthInfoListString,
                            new TypeToken<List<HealthInfo>>() {}.getType()));
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "handleMessage: 已更新健康信息记录");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:活动创建");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_health_info_history);

        adapter = new HealthInfoHistoryAdapter(this.healthInfoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.healthInfo_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        getHealthInfoHistory();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart:活动开始");
        super.onStart();

    }

    /**
     * 向服务器请求获得用户的健康信息历史记录
     */
    public void getHealthInfoHistory() {
        String userid = CurrentUser.getCurrentUser().getUserId();
        String url = API_URL + "api/healthInfo/findHealthInfoListByUserid?userid=" + userid;
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure: 获得健康信息记录失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, "onResponse: 获得健康信息记录成功：" + responseText);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ShowHealthInfoHistoryActivity.this).edit();
                editor.putString("healthInfoList", responseText);
                editor.apply();
                handler.sendEmptyMessage(UPDATE_RE_VIEW);
            }
        });
    }




}