package com.bupt.sse.group7.covid19;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baidu.mapapi.model.LatLng;
import com.bupt.sse.group7.covid19.SQLite.WIFIAdapter;
import com.bupt.sse.group7.covid19.model.BroadcastKey;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.WIFIConnection;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WIFIActivity extends AppCompatActivity {

    List<ScanResult> list;  //存放周围wifi热点对象的列表
    TextView tv1;
    private static TextView tv_wifiscan_text;
    Button  bt3, bt4, bt5, bt6, bt7,bt_getrisk,bt_wifinode;
    ImageButton bt_wifiscan;
    WIFIAdapter wifiadapter;
    Intent serviceIntent = null;

    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiscan);
        tv1 = (TextView) findViewById(R.id.tv1);
        bt3 = (Button) findViewById(R.id.bt3);
        bt4 = (Button) findViewById(R.id.bt4);
        bt5 = (Button) findViewById(R.id.bt5);
        bt6 = (Button) findViewById(R.id.bt6);
        bt7 = (Button) findViewById(R.id.bt7);
        bt_wifiscan = (ImageButton) findViewById(R.id.bt_wifiscan);
        bt_getrisk = (Button) findViewById(R.id.bt_getrisk);
        bt_wifinode = (Button) findViewById(R.id.bt_wifinode);
        tv_wifiscan_text = (TextView) findViewById(R.id.tv_wifiscan_text);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serviceIntent = new Intent(this, WIFIScanService.class);
        wifiadapter = new WIFIAdapter(WIFIActivity.this);
        wifiadapter.open();//启动数据库

//        WIFIConnection wt1 = new WIFIConnection(WIFIConnection.strToDate("2021-04-24 08:00:00"),"test1","test1",5);
//        WIFIConnection wt2 = new WIFIConnection(WIFIConnection.strToDate("2021-04-24 13:00:00"),"test2","test3",5);
//        WIFIConnection wt3 = new WIFIConnection(WIFIConnection.strToDate("2021-04-24 18:00:00"),"test3","test3",5);
//
//        wifiadapter.insertWIFIConnection(wt1);
//        wifiadapter.insertWIFIConnection(wt2);
//        wifiadapter.insertWIFIConnection(wt3);


        if (!isIgnoringBatteryOptimizations())
            requestIgnoreBatteryOptimizations();

        bt_wifiscan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(!bt_wifiscan.isSelected()){
                    Toast.makeText(WIFIActivity.this, "开始扫描", Toast.LENGTH_SHORT).show();
                    startScanService();//开始扫描
                    bt_wifiscan.setSelected(true);
                    tv_wifiscan_text.setText("扫描已开启");
                    tv_wifiscan_text.setTextColor(getResources().getColor(R.color.white));
                }else{
                    Toast.makeText(WIFIActivity.this, "停止扫描", Toast.LENGTH_SHORT).show();
                    WIFIActivity.this.stopService(serviceIntent);
                    bt_wifiscan.setSelected(false);
                    tv_wifiscan_text.setText("扫描未开启");
                    tv_wifiscan_text.setTextColor(getResources().getColor(R.color.text_grey));
                }

            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(WIFIActivity.this, "上传WiFi记录", Toast.LENGTH_SHORT).show();
                UploadWifiInfo();
            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(WIFIActivity.this, "下载确诊者wifi记录", Toast.LENGTH_SHORT).show();
                DownloadBroadcastKey();
            }
        });

        bt5.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(WIFIActivity.this, "展示wifi记录", Toast.LENGTH_SHORT).show();
                showWIFIConnection();
            }
        });

        bt6.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(WIFIActivity.this, "展示下载记录", Toast.LENGTH_SHORT).show();
                showBroadcastKey();
            }
        });

        bt7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(WIFIActivity.this, "生成安全提醒", Toast.LENGTH_SHORT).show();
                CompareBroadcastKey();
            }
        });

        bt_getrisk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(CurrentUser.getCurrentUser() == null)
                {
                    Toast.makeText(WIFIActivity.this, "尚未登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(WIFIActivity.this, "风险预测", Toast.LENGTH_SHORT).show();
                UploadWifiInfo();
                getUserRisk(String.valueOf(CurrentUser.getCurrentUser().getUserId()));

            }
        });

        bt_wifinode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(CurrentUser.getCurrentUser() == null)
                {
                    Toast.makeText(WIFIActivity.this, "尚未登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(WIFIActivity.this, "查看关注已WiFi节点", Toast.LENGTH_SHORT).show();
                DownloadBroadcastKey();
                getWifinode(String.valueOf(CurrentUser.getCurrentUser().getUserId()));

            }
        });




    }

    public void getUserRisk(String userId)
    {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userId",userId)
                .build();
        Request request = new Request.Builder()
                .url("http://81.70.253.77:8080/api/CalculateRisk")
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("LoginTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.d("预测风险", "onResponse: "+s);
                Looper.prepare();
                builder = new AlertDialog.Builder(WIFIActivity.this).setIcon(R.mipmap.ic_launcher).setTitle("提示")
                        .setMessage("您的感染风险是"+s).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
                Looper.loop();


            }
        });
    }

    public void getWifinode(String userId)
    {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("userid",userId)
                .build();
        Request request = new Request.Builder()
                .url("http://81.70.253.77:8080/api/Wifinode/getRegisterWifi")
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("LoginTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.d("查看WiFi节点", "onResponse: "+s);
                Looper.prepare();
                final String[] items = processMac(s);
                builder = new AlertDialog.Builder(WIFIActivity.this).setIcon(R.mipmap.ic_launcher)
                        .setTitle("已关注的WIFI节点")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(WIFIActivity.this, "你点击的内容为： " + items[i], Toast.LENGTH_LONG).show();

                            }
                        });
                builder.create().show();
                Looper.loop();


            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startScanService() {
        registerPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            this.startForegroundService(serviceIntent);
        } else {
            this.startService(serviceIntent);
        }
    }

    public void showWIFIConnection() {
        String s = "";
        WIFIConnection[] wt = wifiadapter.queryAllWIFIConnection();
        if (wt != null)
            for (WIFIConnection item : wt) {
                s += item.toString();
                s += "\n\n";
            }
        tv1.setText(s);

    }

    public void showBroadcastKey() {
        String s = "";
        BroadcastKey[] wt = wifiadapter.queryAllBroadcastKey();
        if (wt != null)
            for (BroadcastKey item : wt) {
                s += item.toString();
                s += "\n\n";
            }
        tv1.setText(s);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void registerPermission() {
        //动态获取定位权限
        if (WIFIActivity.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    Log.v("权限获取", "get");
                } else {
                    // 没有获取到权限，做特殊处理
                    Log.v("权限获取", "notget");
                }
                break;
            default:
                break;
        }
    }



    public void UploadWifiInfo() {
        WIFIConnection[] bt = wifiadapter.queryUnsentWIFIConnection();

        OkHttpClient client = new OkHttpClient();

        SharedPreferences sharedPreferences = getSharedPreferences("Current_User", Context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userId", "0");

        if(bt!=null)
            for(final WIFIConnection newbt:bt) {
                FormBody body = new FormBody.Builder()
                        .add("userId", currentUserId)
                        .add("startTime",WIFIConnection.DateToString(newbt.datetime))
                        .add("duartion",String.valueOf(newbt.duration))
                        .add("distanceLevel", String.valueOf(newbt.level))
                        .add("name",newbt.name)
                        .add("wifiMac",newbt.MAC_address)
                        .build();
                Request request = new Request.Builder()
                        .url("http://81.70.253.77:8080/api/WifiInfo/insertOne")
                        .post(body)
                        .build();

                Log.d("send",  newbt.ID+"\n"+
                        WIFIConnection.DateToString(newbt.datetime)+"\n"+String.valueOf(newbt.duration)
                        +"\n"+newbt.MAC_address);

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                        Log.d("LoginTest", "onFailure: 访问服务器失败");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String s = response.body().string();
                        if(Integer.parseInt(s)==1)
                        {newbt.isSent=1;
                        wifiadapter.updateWIFIConnection(newbt.ID,newbt);
                            Log.d("UploadWifiConnection", "上传成功");}
                        else{Log.d("UploadWifiConnection", "上传失败,用户不存在");}
                        //Toast.makeText(getActivity().getBaseContext(),"上传蓝牙连接ID:"+newbt.ID, Toast.LENGTH_SHORT).show();
                    }
                });

            }

        if(bt==null)
        {Toast.makeText(WIFIActivity.this,"无WIFI连接需要上传", Toast.LENGTH_SHORT).show();}
        else Toast.makeText(WIFIActivity.this,"上传结束", Toast.LENGTH_SHORT).show();
    }

    public void DownloadBroadcastKey()
    {
        SharedPreferences preferences = this.getSharedPreferences("DownloadDate", Context.MODE_PRIVATE);
        String lastdate = preferences.getString("lastDownloadDate", "2020-01-01 00:00:00");
        //http请求数据库
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("lastDownloadDate",lastdate)
                .build();
        Request request = new Request.Builder()
                .url("http://81.70.253.77:8080/api/Broadcastkey/download")
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d("LoginTest", "onFailure: 访问服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.d("LoginTest", "onResponse: "+s);

                Date date = new Date();
                SharedPreferences.Editor editor = getSharedPreferences("DownloadDate",MODE_PRIVATE).edit();
                editor.putString("DownloadDate", WIFIConnection.DateToString(date));
                editor.apply();
                processBroadcastKey(s);
            }
        });
    }

    public void processBroadcastKey(String s)
    {
        try{
            JSONArray jsonArray = new JSONArray(s);
            for(int i=0;i <jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String wifiMac = jsonObject.getString("wifiMac");
                String wifiName = jsonObject.getString("wifiName");
                String startTime = jsonObject.getString("startTime");
                int duartion = jsonObject.getInt("duartion");
                int averageDistance =jsonObject.getInt("averageDistance");
                BroadcastKey broadcastKey = new BroadcastKey(WIFIConnection.strToDate(startTime),duartion,
                        wifiMac,wifiName,averageDistance);
                wifiadapter.insertBroadcastKey(broadcastKey);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] processMac(String s)
    {
        String[] macname = new String[1];
        try{
            JSONArray jsonArray = new JSONArray(s);
            macname = new String[jsonArray.length()];
            for(int i=0;i <jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                macname[i] = jsonObject.getString("mac");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return macname;
    }

    public void CompareBroadcastKey()
    {
        String s ="";
        Date date = new Date();
        Date lastDate = WIFIConnection.Lastdate(date);
        BroadcastKey[] bklist= wifiadapter.queryBroadcastKeyByDate(lastDate,date);
        WIFIConnection[] wtlist = wifiadapter.queryWIFIConnectionByDate(lastDate,date);

        if(bklist==null) {tv1.setText("无下载记录");return;}
        if(wtlist==null) {tv1.setText("无扫描记录");return;}

        int count=0;
        for(BroadcastKey bk:bklist)
        {
            for(WIFIConnection wt:wtlist)
            {
                if(wt.MAC_address.equals(bk.MAC_address))
                {
                    Log.e("MAC",bk.ID+":"+wt.ID);
                    if(wt.datetime.getTime()+wt.duration*1000+10*60*1000< bk.datetime.getTime()-10*60*1000 ||
                            bk.datetime.getTime()+wt.duration*1000+10*60*1000< wt.datetime.getTime()-10*60*1000)
                        continue;
                    else {
                         count++;
                        s+="风险提醒第"+count+"条：\n"+"扫描记录："+wt.toString()+"\n"+
                                "与下载记录："+bk.toString()+"匹配成功\n";
                    }

                }
                else continue;
            }
        }

        if(count==0) s+="无条目匹配成功，无风险";
        else s+="\n您曾于确诊患者同处同一wifi下，请进行风险评估，获取感染可能";
        tv1.setText(s);
    }



}
