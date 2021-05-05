package com.bupt.sse.group7.covid19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.HealthInfo;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.HttpUtil;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.Utility;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostHealthInfoActivity extends AppCompatActivity {
    private static final String TAG = "PostHealthInfoActivity";
    private static final String API_URL = "http://192.168.43.129:3030/";
    //    private static final String API_URL = "http://39.97.212.229:3030/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    String[] healthInfoType = {};
    TextView typeText;
    Spinner typeItems;
    EditText contentText;
    CardView submitCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: PostHealthInfoActivity活动创建");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_health_info);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: PostHealthInfoActivity活动开始");

        super.onStart();
        typeText = findViewById(R.id.healthInfo_type_text);
        typeItems = findViewById(R.id.healthInfo_type_spinner);
        contentText = findViewById(R.id.healthInfo_content);
        submitCard = findViewById(R.id.healthInfo_submit);

        //获取array中定义的值
        healthInfoType = getResources().getStringArray(R.array.healthInfoType);

        //添加Spinner监听事件
        typeItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: 监听到元素被选中");

                typeText.setText(healthInfoType[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 点击提交按钮");
                postHealthInfo();
            }
        });

    }

    private void postHealthInfo() {
        String url = API_URL + "api/healthInfo/insertOneHealthInfo";

        Integer userid = Integer.valueOf(CurrentUser.getCurrentUser().getUserId());
        String type = typeText.getText().toString();
        String content = contentText.getText().toString();
        String submitTime = Utility.getDateTimeString();

        HealthInfo healthInfo = new HealthInfo();
        healthInfo.setUserid(userid);
        healthInfo.setType(type);
        healthInfo.setContent(content);
        healthInfo.setSubmitTime(submitTime);

        Gson gson = new Gson();
        String jsonString = gson.toJson(healthInfo);
        Log.d(TAG, "postHealthInfo: 上传的json格式健康信息：" + jsonString);

        RequestBody requestBody = RequestBody.create(JSON, jsonString);
        HttpUtil.sendOkHttpRequest(url, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure: 上传健康信息失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: 上传健康信息成功");
            }
        });

    }

}