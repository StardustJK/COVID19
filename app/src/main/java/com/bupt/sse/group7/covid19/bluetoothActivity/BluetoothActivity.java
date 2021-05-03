package com.bupt.sse.group7.covid19.bluetoothActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.BluetoothInfo;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.MySecretKeyInfo;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.OtherSecretKeyInfo;
import com.bupt.sse.group7.covid19.bluetoothJsonEntity.JsonBluetoothInfo;
import com.bupt.sse.group7.covid19.bluetoothJsonEntity.JsonSecretKeyInfo;
import com.bupt.sse.group7.covid19.bluetoothService.BluetoothService;
import com.bupt.sse.group7.covid19.bluetoothService.FirstWorkService;
import com.bupt.sse.group7.covid19.bluetoothView.ScanningView;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.utils.Constants;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.AlgUtil;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.HttpUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BluetoothActivity";
    private static final String API_URL = "http://192.168.43.129:3030/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final int POST_SECRET_KEY_SUCCESSFUL = 0;
    public static final int POST_SECRET_KEY_UNSUCCESSFUL = 1;
    public static final int POST_BLUETOOTH_INFO_SUCCESSFUL = 2;
    public static final int POST_BLUETOOTH_INFO_UNSUCCESSFUL = 3;
    public static final int GET_OTHER_KEY_SUCCESSFUL = 4;
    public static final int GET_OTHER_KEY_UNSUCCESSFUL = 5;
    public static final int GET_RISK_LEVEL_SUCCESSFUL = 6;
    public static final int GET_RISK_LEVEL_UNSUCCESSFUL = 7;
    public static final int GET_CHECK_RESULT = 8;


    //自定义的假的用户id
    private int userid = 0;

    //蓝牙扫描的开关按钮
    private Button searchButton;

    //用户上传自己的每日跟踪秘钥的按钮
    private Button postSecretKeyInfoButton;

    //用户上传自己的蓝牙扫描信息的按钮
    private Button postBluetoothInfoButton;

    //用户获得广播键的按钮
    private Button getOtherSecretKeyButton;

    //用户获得本地匹配结果的按钮
    private Button getCheckResultButton;

    //用户获得感染风险评估等级的按钮
    private Button getRiskLevelButton;

    //蓝牙扫描动画view
    private ScanningView scanningView;

    //标记蓝牙扫描线程是否正在进行
    private boolean bluetoothThreadFlag;

    // handler用于线程通信
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case POST_SECRET_KEY_SUCCESSFUL:
                    Toast.makeText(BluetoothActivity.this,
                            "上传每日跟踪秘钥成功", Toast.LENGTH_SHORT).show();
                    break;
                case POST_SECRET_KEY_UNSUCCESSFUL:
                    Toast.makeText(BluetoothActivity.this,
                            "上传每日跟踪秘钥失败", Toast.LENGTH_SHORT).show();
                    break;
                case POST_BLUETOOTH_INFO_SUCCESSFUL:
                    Toast.makeText(BluetoothActivity.this,
                            "上传蓝牙扫描信息成功", Toast.LENGTH_SHORT).show();
                    break;
                case POST_BLUETOOTH_INFO_UNSUCCESSFUL:
                    Toast.makeText(BluetoothActivity.this,
                            "上传蓝牙扫描信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case GET_OTHER_KEY_SUCCESSFUL:
                    Toast.makeText(BluetoothActivity.this,
                            "成功下载最新广播键", Toast.LENGTH_SHORT).show();
                    break;
                case GET_OTHER_KEY_UNSUCCESSFUL:
                    Toast.makeText(BluetoothActivity.this,
                            "下载广播键失败", Toast.LENGTH_SHORT).show();
                    break;
                case GET_RISK_LEVEL_SUCCESSFUL:
                    Toast.makeText(BluetoothActivity.this,
                            "获得感染风险评估等级成功，请查看", Toast.LENGTH_SHORT).show();
                    break;
                case GET_RISK_LEVEL_UNSUCCESSFUL:
                    Toast.makeText(BluetoothActivity.this,
                            "获得感染风险评估等级失败", Toast.LENGTH_SHORT).show();
                    break;
                case GET_CHECK_RESULT:
                    Toast.makeText(BluetoothActivity.this,
                            "本地匹配完成，请查看", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    // 广播接收器，用于接收、处理service发来的消息
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean threadFlag = intent.getBooleanExtra("threadFlag", false);
            Log.d(TAG, "onReceive:广播收到threadFlag：" + threadFlag);
            if (threadFlag) {
                scanningView.setTitle("扫描中");
                scanningView.startScanAnimation();
                searchButton.setText("停止蓝牙扫描服务");
            } else {
                scanningView.setTitle(" ");
                scanningView.stopScanAnimation();
                searchButton.setText("开启蓝牙扫描服务");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(this);
        setContentView(R.layout.activity_bluetooth);
        Log.d(TAG, "onCreate:蓝牙活动创建");

        if (CurrentUser.getCurrentUser() != null && CurrentUser.getCurrentUser().getUserId() != null)
            userid = Integer.parseInt(CurrentUser.getCurrentUser().getUserId());
        Log.d(TAG, "onCreate:用户id是：" + userid);


        //获取控件
        searchButton = (Button) findViewById(R.id.search_button);
        postSecretKeyInfoButton = (Button) findViewById(R.id.post_secretKeyInfo_button);
        postBluetoothInfoButton = (Button) findViewById(R.id.post_bluetoothInfo_button);
        getOtherSecretKeyButton = (Button) findViewById(R.id.get_otherSecretKey_button);
        getCheckResultButton = (Button) findViewById(R.id.get_checkResult_button);
        getRiskLevelButton = (Button) findViewById(R.id.get_riskLevel_button);
        scanningView = (ScanningView) findViewById(R.id.scanning);

        //给按钮设置监听器
        searchButton.setOnClickListener(this::onClick);
        postSecretKeyInfoButton.setOnClickListener(this::onClick);
        postBluetoothInfoButton.setOnClickListener(this::onClick);
        getCheckResultButton.setOnClickListener(this::onClick);
        getRiskLevelButton.setOnClickListener(this::onClick);
        getOtherSecretKeyButton.setOnClickListener(this::onClick);
//        searchButtonFlag = 0;

        //注册广播监听器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.MY_ACTION_NAME);
        registerReceiver(receiver, intentFilter);

        //启动FirstWork服务
        startFirstWorkService();

//        //获得并存储14天内的广播键集
//        getAndSaveOtherSK();
//
//        //进行广播键集和本地蓝牙扫描信息的匹配
//        int result = 0;
//        try {
//            result = AlgUtil.getCheckResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //存储用户的userid
//        saveMyUserid(userid);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart:蓝牙活动开始");

        //根据蓝牙线程状态设置控件
        //获得蓝牙线程状态
        bluetoothThreadFlag = BluetoothService.threadFlag;
        Log.d(TAG, "onStart: 蓝牙线程bluetoothThreadFlag：" + bluetoothThreadFlag);

        //判断蓝牙线程是否在运行
        if (bluetoothThreadFlag) {
            scanningView.setTitle("扫描中");
            scanningView.startScanAnimation();
            searchButton.setText("停止蓝牙扫描服务");
        } else {
            scanningView.setTitle(" ");
            searchButton.setText("开启蓝牙扫描服务");
        }
        postSecretKeyInfoButton.setText("上传每日跟踪秘钥");
        postBluetoothInfoButton.setText("上传蓝牙扫描信息");
        getOtherSecretKeyButton.setText("下载广播键");
        getCheckResultButton.setText("获得本地匹配结果");
        getRiskLevelButton.setText("获得感染风险评估等级");

    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothThreadFlag = BluetoothService.threadFlag;
        Log.d(TAG, "onStop: 蓝牙线程bluetoothThreadFlag：" + bluetoothThreadFlag);
        if (bluetoothThreadFlag) {
            scanningView.stopScanAnimation();
        }
        Log.d(TAG, "onStop:蓝牙活动停止");

    }

    @Override
    protected void onDestroy() {
        // 注销蓝牙监听器
        unregisterReceiver(receiver);
        super.onDestroy();
        Log.d(TAG, "onDestroy:蓝牙活动销毁");
    }

    /**
     * 保存用户id
     *
     * @param userid
     */
//    private void saveMyUserid(int userid) {
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(BluetoothActivity.this).edit();
//        editor.putInt("userid", userid);
//        editor.apply();
//    }


    /**
     * 获得用户id
     *
     * @return
     */
//    private int getMyUserid() {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        int myUserid = prefs.getInt("userid", 0);
//        return userid;
//    }

    /**
     * 按钮监听器
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                bluetoothThreadFlag = BluetoothService.threadFlag;
                Log.d(TAG, "onClick: 蓝牙线程bluetoothThreadFlag：" + bluetoothThreadFlag);

                Intent BluetoothServerIntent = new Intent(this, BluetoothService.class);

                //判断蓝牙线程是否在运行
                if (bluetoothThreadFlag) {
                    Log.d(TAG, "onClick: 停止蓝牙扫描服务");
                    stopService(BluetoothServerIntent);
                    scanningView.setTitle(" ");
                    scanningView.stopScanAnimation();
                    searchButton.setText("开启蓝牙扫描服务");
                } else {
                    Log.d(TAG, "onClick: 开始蓝牙扫描服务");
                    scanningView.setTitle("扫描中");
                    startService(BluetoothServerIntent);
                    searchButton.setText("停止蓝牙扫描服务");
                }
                break;
            case R.id.post_secretKeyInfo_button:
                Log.d(TAG, "onClick: 上传每日跟踪秘钥");
                if (this.userid == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle("提示")//标题
                            .setMessage("匿名用户只有在确诊后才可以上传自己的每日跟踪秘钥，是否继续操作？")//内容
                            .setIcon(R.mipmap.ic_launcher)//图标
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    postMySecretKeyInfo();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    alertDialog.show();
                } else {
                    postMySecretKeyInfo();
                }
                break;
            case R.id.post_bluetoothInfo_button:
                Log.d(TAG, "onClick: 上传蓝牙扫描信息");
                if (this.userid == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle("有问题")//标题
                            .setMessage("用户在登录后才可以使用此功能。")//内容
                            .setIcon(R.mipmap.ic_launcher)//图标
                            .setPositiveButton("我知道了", null)
                            .create();
                    alertDialog.show();
                } else {
                    postBluetoothInfo();
                }
                break;
            case R.id.get_otherSecretKey_button:
                Log.d(TAG, "onClick: 下载广播键");
                getAndSaveOtherSK();
                break;
            case R.id.get_checkResult_button:
                Log.d(TAG, "onClick: 获得本地匹配结果");
                try {
                    getAndSaveCheckResult();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.get_riskLevel_button:
                Log.d(TAG, "onClick: 获得感染风险评估等级");
                AlertDialog alertDialog;//标题
                if (this.userid == 0) {
                    alertDialog = new AlertDialog.Builder(this)
                            .setTitle("有问题")//标题
                            .setMessage("用户在登录后才可以使用此功能。")//内容
                            .setIcon(R.mipmap.ic_launcher)//图标
                            .setPositiveButton("我知道了", null)
                            .create();
                } else {
                    alertDialog = new AlertDialog.Builder(this)
                            .setTitle("提示")//标题
                            .setMessage("用户使用此功能需要上传蓝牙扫描信息，是否继续操作？")//内容
                            .setIcon(R.mipmap.ic_launcher)//图标
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    postBluetoothInfo();
                                    findRiskLevelByUserid();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                }
                alertDialog.show();
                break;
            default:
                break;
        }
    }


    /**
     * 开启蓝牙模块先行服务
     */
    private void startFirstWorkService() {
        Intent FirstWorkServiceIntent = new Intent(this, FirstWorkService.class);
        startService(FirstWorkServiceIntent);
    }

    /**
     * 上传每日跟踪秘钥
     */
    private void postMySecretKeyInfo() {


        String url = API_URL + "api/Bluetooth/postSecretKeyInfoList";

        List<MySecretKeyInfo> mySecretKeyInfoList =
                LitePal.where("is_used = ?", "0").find(MySecretKeyInfo.class);
        List<JsonSecretKeyInfo> jsonSecretKeyInfoList = new ArrayList<>();
        for (MySecretKeyInfo mySecretKeyInfo : mySecretKeyInfoList) {
            JsonSecretKeyInfo secretKeyInfo = new JsonSecretKeyInfo();
            secretKeyInfo.setUserid(userid);
            secretKeyInfo.setDate(mySecretKeyInfo.getDate());
            secretKeyInfo.setSecretkey(mySecretKeyInfo.getSecret_key());
            jsonSecretKeyInfoList.add(secretKeyInfo);
        }
        int n = jsonSecretKeyInfoList.size();

        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonSecretKeyInfoList);
        Log.d(TAG, "postBluetoothInfo: 上传的json格式每日跟踪秘钥·：" + jsonString);

        RequestBody requestBody = RequestBody.create(JSON, jsonString);
        HttpUtil.sendOkHttpRequest(url, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(POST_SECRET_KEY_UNSUCCESSFUL);
                Log.d(TAG, "onFailure: 上传每日跟踪秘钥失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                for (MySecretKeyInfo mySecretKeyInfo : mySecretKeyInfoList) {
                    mySecretKeyInfo.setIs_used(true);
                    mySecretKeyInfo.save();
                }
                handler.sendEmptyMessage(POST_SECRET_KEY_SUCCESSFUL);
                Log.d(TAG, "onResponse: 上传每日跟踪秘钥成功，上传数量：" + n);
            }
        });
    }

    /**
     * 上传蓝牙扫描信息
     */
    private void postBluetoothInfo() {

        String url = API_URL + "api/Bluetooth/postBluetoothInfoList";

        List<BluetoothInfo> bluetoothInfoList =
                LitePal.where("is_used = ?", "0").find(BluetoothInfo.class);
        List<JsonBluetoothInfo> jsonBluetoothInfoList = new ArrayList<>();
        int n = bluetoothInfoList.size();
        for (BluetoothInfo bluetoothInfo : bluetoothInfoList) {
            JsonBluetoothInfo jsonBluetoothInfo = new JsonBluetoothInfo();
            jsonBluetoothInfo.setUserid(userid);
            jsonBluetoothInfo.setTime_stamp(bluetoothInfo.getTime_stamp());
            jsonBluetoothInfo.setMy_identifier(bluetoothInfo.getMy_identifier());
            jsonBluetoothInfo.setTarget_identifier(bluetoothInfo.getTarget_identifier());
            jsonBluetoothInfo.setDistance(bluetoothInfo.getDistance());
            jsonBluetoothInfo.setDuration(bluetoothInfo.getDuration());
            jsonBluetoothInfo.setDate(bluetoothInfo.getDate());
            jsonBluetoothInfoList.add(jsonBluetoothInfo);
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonBluetoothInfoList);

        Log.d(TAG, "postBluetoothInfo: 上传的json格式蓝牙扫描信息·：" + jsonString);

        RequestBody requestBody = RequestBody.create(JSON, jsonString);
        HttpUtil.sendOkHttpRequest(url, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(POST_BLUETOOTH_INFO_UNSUCCESSFUL);
                Log.d(TAG, "onFailure: 上传蓝牙扫描信息失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                for (BluetoothInfo bluetoothInfo : bluetoothInfoList) {
                    bluetoothInfo.setIs_used(true);
                    bluetoothInfo.save();
                }
                handler.sendEmptyMessage(POST_BLUETOOTH_INFO_SUCCESSFUL);
                Log.d(TAG, "onResponse: 上传蓝牙扫描信息成功，上传数量：" + n);
            }
        });
        return;
    }

    /**
     * 向服务器请求获得用户的感染风险
     */
    private void findRiskLevelByUserid() {
        int userid = this.userid;
        String url = API_URL + "user/getBluetoothRiskLevel?userId=" + userid;
//        RequestBody requestBody = new FormBody.Builder()
//                .add("userId", String.valueOf(userid))
//                .build();
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(GET_RISK_LEVEL_UNSUCCESSFUL);
                Log.d(TAG, "onFailure: 获得风险评估等级失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int riskLevel = 0;
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getBoolean("success"))
                        riskLevel = jsonObject.getInt("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                String responseText = response.body().string();
//                int riskLevel = Integer.parseFloat(responseText);
                handler.sendEmptyMessage(GET_RISK_LEVEL_SUCCESSFUL);
                Log.d(TAG, "onResponse: 获得风险评估等级成功，riskLevel：" + riskLevel);
                saveMyRiskLevel(riskLevel);
                Log.d(TAG, "onResponse: 已存储风险评估等级");
            }
        });
    }

    /**
     * 存储用户的感染风险评估等级
     *
     * @param riskLevel
     */
    private void saveMyRiskLevel(int riskLevel) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(BluetoothActivity.this).edit();
        editor.putInt("BluetoothRiskLevel", riskLevel);
        editor.apply();
    }


    /**
     * 下载并准备存储广播键
     */
    public void getAndSaveOtherSK() {
        String url = API_URL + "api/Bluetooth/getSecretKeyList";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(GET_OTHER_KEY_UNSUCCESSFUL);
                Log.d(TAG, "onFailure: 下载广播键失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                handler.sendEmptyMessage(GET_OTHER_KEY_SUCCESSFUL);
                Log.d(TAG, "onResponse: 获取广播键成功：" + responseText);
                try {
                    saveOtherSK(responseText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 存储广播键
     *
     * @param reponse
     * @return
     * @throws JSONException
     */
    public int saveOtherSK(String reponse) throws JSONException {
        JSONArray resultList = new JSONArray(reponse);
        for (int i = 0; i < resultList.length(); i++) {
            //获得一条广播键
            JSONObject jsonObject = (JSONObject) resultList.get(i);
            String secret_key = (String) jsonObject.get("secretKey");
            String dateString = (String) jsonObject.get("date");

            //先查找本地的广播键集
            List<OtherSecretKeyInfo> otherSecretKeyInfoList = LitePal
                    .where("secret_key = ? and date = ?", secret_key, dateString)
                    .find(OtherSecretKeyInfo.class);

            //如果没有这个广播键，则存储到本地
            if (otherSecretKeyInfoList.size() == 0) {
                OtherSecretKeyInfo otherSecretKeyInfo = new OtherSecretKeyInfo();
                otherSecretKeyInfo.setSecret_key(secret_key);
                otherSecretKeyInfo.setDate(dateString);
                otherSecretKeyInfo.setIs_used(false);
                otherSecretKeyInfo.save();
            }
        }
        Log.d(TAG, "saveOtherSK: 已存储广播键");
//        Toast.makeText(getApplication(), "已获得最新的广播键", Toast.LENGTH_SHORT).show();
        return 0;
    }


    /**
     * 获得并存储本地匹配结果
     *
     * @throws Exception
     */
    private void getAndSaveCheckResult() throws Exception {
        int checkResult = AlgUtil.getCheckResult();
        handler.sendEmptyMessage(GET_CHECK_RESULT);
        Log.d(TAG, "getAndSaveCheckResult: 本地匹配完成，匹配成功数量：" + checkResult);
        saveCheckResult(checkResult);
    }

    /**
     * 存储用户的本地匹配结果
     *
     * @param checkResult
     */
    private void saveCheckResult(int checkResult) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(BluetoothActivity.this).edit();
        editor.putFloat("checkResult", checkResult);
        editor.apply();
    }

}