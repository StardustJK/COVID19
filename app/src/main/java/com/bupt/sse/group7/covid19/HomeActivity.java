package com.bupt.sse.group7.covid19;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.baidu.mapapi.SDKInitializer;
import com.bupt.sse.group7.covid19.bluetoothActivity.BluetoothActivity;
import com.bupt.sse.group7.covid19.interfaces.IUserTripViewCallBack;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.UserTrip;
import com.bupt.sse.group7.covid19.presenter.HospitalPresenter;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;
import com.bupt.sse.group7.covid19.presenter.UserTripPresenter;
import com.bupt.sse.group7.covid19.utils.Constants;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 主页
 */
public class HomeActivity extends AppCompatActivity implements IUserTripViewCallBack {
    private static String TAG = "HomeActivity";
    private CardView hospitalCard;
    private CardView authCard;
    private CardView trackCard;
    private CardView pageCard;
    private CardView patientTripCard;
    private CardView wifiCard;
    private CardView bluetoothCard;

    private TextView mildTv, severeTv, curedTv, deadTv;

    private Context context = this;
    private JsonObject statistics;
    private UserTripPresenter userTripPresenter;
    private boolean notified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        userTripPresenter = UserTripPresenter.getInstance();
        userTripPresenter.registerCallBack(this);
        initView();
        checkPermission();
        initCurrentUser();
    }

    //初始化当前用户

    private void initCurrentUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("Current_User", Context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userId", null);
        //获取当前的用户对象
        if (currentUserId != null) {
            Map<String, String> param = new HashMap<>();
            param.put("userId", currentUserId);
            Call<ResponseBody> data = DBConnector.dao.executeGet("user/userInfo", param);
            data.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String dataString = JsonUtils.inputStream2String(response.body().byteStream());
                        JsonObject data = (JsonObject) JsonParser.parseString(dataString);
                        if (data.get("success").getAsBoolean()) {
                            JsonObject user = data.getAsJsonObject("data");
                            CurrentUser currentUser = new CurrentUser(
                                    user.get("id").getAsInt(),
                                    user.get("phone").getAsString(),
                                    user.get("name").getAsString(),
                                    user.get("status").getAsInt(),
                                    user.get("role").getAsInt(),
                                    user.get("auth").getAsBoolean(),
                                    user.get("showTripRisk").getAsBoolean()
                            );
                            CurrentUser.setCurrentUser(currentUser);

                            userTripPresenter.getRisk(currentUser.getUserId());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i(TAG, "getUserInfo failed");
                }
            });
        }

    }


    private void initView() {
        pageCard = findViewById(R.id.page_card);
        hospitalCard = findViewById(R.id.hospital_card);
        trackCard = findViewById(R.id.track_card);
        authCard = findViewById(R.id.auth_card);
        wifiCard = findViewById(R.id.wifi_card);
        bluetoothCard = findViewById(R.id.bluetooth_card);
        patientTripCard = findViewById(R.id.patient_trip);


        hospitalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StatisticActivity.class);
                startActivity(intent);
            }
        });

        authCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                if (CurrentUser.getCurrentUser() == null) {
                    intent = new Intent(HomeActivity.this, AuthenticateActivity.class);
                } else {
                    PatientPresenter.getInstance().setPatientId(CurrentUser.getCurrentUser().getUserId());
                    intent = new Intent(HomeActivity.this, LogOutActivity.class);

                }
                startActivity(intent);

            }
        });

        trackCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ShowMapActivity.class);
                startActivity(intent);
            }
        });

        pageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentUser.getCurrentUser() == null) {
                    Intent auth = new Intent(HomeActivity.this, AuthenticateActivity.class);
                    startActivity(auth);
                    Toast.makeText(HomeActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(HomeActivity.this, PatientMainPageActivity.class);
                    PatientPresenter.getInstance().setPatientId(CurrentUser.getCurrentUser().getUserId());
                    startActivity(intent);
                }

            }
        });

        patientTripCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PatientTripActivity.class);
                startActivity(intent);
            }
        });

        wifiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WIFIActivity.class);
                startActivity(intent);
            }
        });

        bluetoothCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * 动态权限申请
     */
    public void checkPermission() {
        int targetSdkVersion = 0;
        String[] PermissionString = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        try {
            final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;//获取应用的Target版本
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
//            Log.e("err", "检查权限_err0");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Build.VERSION.SDK_INT是获取当前手机版本 Build.VERSION_CODES.M为6.0系统
            //如果系统>=6.0
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                //第 1 步: 检查是否有相应的权限
                boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                if (isAllGranted) {
                    //Log.e("err","所有权限已经授权！");
                    return;
                }
                // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
                ActivityCompat.requestPermissions(this,
                        PermissionString, 1);
            }
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                //Log.e("err","权限"+permission+"没有授权");
                return false;
            }
        }
        return true;
    }

    //申请权限结果返回处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 所有的权限都授予了
                Log.e("err", "权限都授权了");
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                //容易判断错
                //MyDialog("提示", "某些权限未开启,请手动开启", 1) ;
            }
        }
    }


    public void notification(int id, String content, PendingIntent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "Covid";
        String name = "CovidChannel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.enableVibration(true);
            mChannel.enableLights(true);
            manager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Covid")
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(intent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.appicon);


        Notification notification = builder.build();
        manager.notify(id, notification);
    }

    @Override
    public void onUserTripInfoReturned(List<UserTrip> tripList) {

    }

    @Override
    public void onGetZeroData(String msg) {

    }

    @Override
    public void onRisk() {

        //用于跳转Activity
        Intent intent = new Intent(this, PatientTripActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification(1, "您有行程风险，点击查看", pendingIntent);


    }
}
