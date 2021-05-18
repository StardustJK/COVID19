package com.bupt.sse.group7.covid19;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.adapter.HealthInfoHistoryAdapter;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.HealthInfo;
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
    //    private static final String API_URL = "http://192.168.43.129:3030/";
    private static final String API_URL = "http://39.97.212.229:3030/";
    private static final int UPDATE_RE_VIEW = 1;
    private static final int SHOW_DETAIL_DIALOG = 2;


    private List<HealthInfo> healthInfoList = new ArrayList<>();
    private HealthInfoHistoryAdapter mAdapter;
    private RecyclerView recyclerView;
    private SharedPreferences pref;
    private int myPosition = 0;

    TextView typeDetailText;
    TextView submitTimeDetailText;
    TextView auditStatusDetailText;
    TextView contentDetailText;
    TextView auditOpinionDetailText;
    TextView auditTimeDetailText;


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
                            new TypeToken<List<HealthInfo>>() {
                            }.getType()));
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, "handleMessage: 已更新健康信息记录");
                    break;
                case SHOW_DETAIL_DIALOG:
//                    showHealthInfoDetailDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:活动创建");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_health_info_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new HealthInfoHistoryAdapter(this.healthInfoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.healthInfo_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        getHealthInfoHistory();

        mAdapter.setOnItemClickListener(new HealthInfoHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                myPosition = position;
                showHealthInfoDetailDialog();
            }
        });

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
        int userid = CurrentUser.getCurrentUser().getUserId();
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

    /**
     * 展示用户健康信息详情的dialog
     */
    private void showHealthInfoDetailDialog() {
        Log.d(TAG, "showHealthInfoDetailDialog: 显示细节dialog");
        HealthInfo healthInfo = healthInfoList.get(myPosition);

        AlertDialog.Builder detailDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.healthinfo_history_detail, null);
        detailDialog.setView(dialogView);
        detailDialog.setPositiveButton("确定", null);
//        detailDialog.setNeutralButton("确定", null);

        //通过控件所在的view调用findViewById方法，才可以获取到正确的控件，否则会出现为空的情况
        typeDetailText = dialogView.findViewById(R.id.typeDetailText);
        submitTimeDetailText = dialogView.findViewById(R.id.submitTimeDetailText);
        auditStatusDetailText = dialogView.findViewById(R.id.auditStatusDetailText);
        contentDetailText = dialogView.findViewById(R.id.contentDetailText);
        auditOpinionDetailText = dialogView.findViewById(R.id.auditOpinionDetailText);
        auditTimeDetailText = dialogView.findViewById(R.id.auditTimeDetailText);

        typeDetailText.setText(healthInfo.getType());
        submitTimeDetailText.setText(healthInfo.getSubmitTime());
        contentDetailText.setText(healthInfo.getContent());
        auditStatusDetailText.setText(healthInfo.getAuditStatus());
        auditTimeDetailText.setText(healthInfo.getAuditTime());
        auditOpinionDetailText.setText(healthInfo.getAuditOpinion());

        contentDetailText.setMovementMethod(ScrollingMovementMethod.getInstance());
        auditOpinionDetailText.setMovementMethod(ScrollingMovementMethod.getInstance());

        detailDialog.show();
    }


}