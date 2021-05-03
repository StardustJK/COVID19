package com.bupt.sse.group7.covid19;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.bupt.sse.group7.covid19.SQLite.WIFIAdapter;
import com.bupt.sse.group7.covid19.model.WIFIConnection;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WIFIScanService extends Service {

    public static final String CHANNEL_ID = "com.example.wifiapplication.WIFIScanService";
    public static final String CHANNEL_NAME = "com.example.wifiapplication";
    private static final String TAG="MainActivity";
    private List<WIFIConnection> list;
    private List<WIFIConnection> lastlist;
    private List<String> adresslist;
    private List<String> lastadresslist;
    WIFIAdapter wifiadapter;
    private Thread workThread;

    IntentFilter intent;
    WifiManager wifiManager;



    @Override
    public void onCreate() {
        if(!initDevice())
        {   Toast.makeText(getApplicationContext(), "未开启wifi", Toast.LENGTH_LONG).show();
        stopSelf();return;}
        registerNotificationChannel();
        int notifyId = (int) System.currentTimeMillis();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder
                //必须要有
                .setSmallIcon(R.mipmap.ic_launcher)
        //可选
        //.setSound(null)
        //.setVibrate(null)
        //...
        ;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mBuilder.setContentTitle(getResources().getString(R.string.app_name));
        }
        startForeground(notifyId, mBuilder.build());

        wifiadapter = new WIFIAdapter(this);
        wifiadapter.open();//启动数据库

        list=new ArrayList<>();
        lastlist=new ArrayList<>();
        lastadresslist=new ArrayList<>();
        adresslist =new ArrayList<>();
        this.workThread = new Thread((ThreadGroup)null, this.backgroudWork, "WorkThread");
        this.workThread.start();
    }

    private void registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = mNotificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                //是否在桌面icon右上角展示小红点
                channel.enableLights(true);
                //小红点颜色
                channel.setLightColor(Color.RED);
                //通知显示
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                //是否在久按桌面图标时显示此渠道的通知
                //channel.setShowBadge(true);
                mNotificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
   Log.d(TAG, "onStartCommand()");
    // 在API11之后构建Notification的方式
   Notification.Builder builder = new Notification.Builder
    (this.getApplicationContext()); //获取一个Notification构造器
    Intent nfIntent = new Intent(this, WIFIActivity.class);

   builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
    .setContentTitle("WIFI扫描") // 设置下拉列表里的标题
        .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
        .setContentText("WIFI扫描中") // 设置上下文内容
        .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,                    NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ID);
        }

Notification notification = builder.build(); // 获取构建好的Notification
notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务

        return super.onStartCommand(intent, flags, startId);
    }






    private boolean initDevice() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        //获取WIFIManager
        if(!testWIFI()) return false;
        //addPairedDevice();
        return  true;
    }

    private Boolean testWIFI()
    {
        if (wifiManager == null) {
            Toast.makeText(getBaseContext(), "您的机器上没有发现WIFI适配器，本程序将不能运行!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
        {// 如果WIFI还没开启
            Toast.makeText(getBaseContext(), "请先开启WIFI", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private Runnable backgroudWork = new Runnable() {
        public void run() {
            while(true) {
                try {

                    if (!Thread.interrupted()) {
                        lastlist=list;
                        lastadresslist=adresslist;
                        list=new ArrayList<>();
                        adresslist=new ArrayList<>();
                        ScanRecord();
                        Thread.sleep(30*1000L);
                        continue;
                    }
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                return;
            }
        }
    };

    public List<ScanResult> getWifiList() {
        //获取WIFI链接列表
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                Log.e("运行", "搜索的wifi-ssid:" + scanResult.SSID);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }else {
            Log.e("运行", "没有搜索到wifi");
        }
        return wifiList;
    }

    public void ScanRecord() {
       List<ScanResult> scanlist=getWifiList();
       Date date = new Date();

       for(ScanResult item:scanlist)
       {

           if(lastadresslist.indexOf(item.BSSID)==-1)
           {
               WIFIConnection wt = new WIFIConnection(date,item.BSSID,item.SSID,item.level);
               wt.ID = wifiadapter.insertWIFIConnection(wt);
               list.add(wt);
               adresslist.add(wt.MAC_address);
               Log.v("测试",wt.toString());
           }
           else
               {
                   WIFIConnection wt = lastlist.get(lastadresslist.indexOf(item.BSSID));
                   wt.duration = (int) (date.getTime()-wt.datetime.getTime())/1000;
//                   wt.datetime = date;
                   wt.ID = wifiadapter.insertWIFIConnection(wt);
                   list.add(wt);
                   adresslist.add(wt.MAC_address);
                   Log.v("测试",wt.toString());
               }
       }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        if(workThread!=null) this.workThread.interrupt();
    }

    @Override
    public IBinder onBind(Intent intent) { return null;}
}
