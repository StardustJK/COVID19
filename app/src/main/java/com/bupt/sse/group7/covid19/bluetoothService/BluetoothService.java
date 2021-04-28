package com.bupt.sse.group7.covid19.bluetoothService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.bluetoothActivity.BluetoothActivity;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.BluetoothInfo;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.MyIdentifierInfo;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.Utility;

import org.litepal.LitePal;

import java.util.List;

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private static final int NOTIFICATION_ID = 1;
    public static final String MY_ACTION_NAME = "BLUETOOTH_SERVICE_ACTION";
    public static final int UPDATE_ANIMATION_TO_SCANNING = 0;
    public static final int UPDATE_ANIMATION_TO_WAITING = 1;

    // 获取到蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;

    //蓝牙线程
    private Thread workThread;

    //蓝牙线程标识，表示是否正在运行
    public static boolean threadFlag = false;

    // 广播过滤器
    IntentFilter filter = new IntentFilter();

    //用于向BluetoothActivity发送广播
    Intent myIntent;


    // 注册广播接收者
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取到广播的action
            String action = intent.getAction();
            // 判断广播是搜索到设备，还是搜索完成，还是扫描状态已经改变
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                // 找到设备后获取其设备、信号强度
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = 0;
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                }
                // 筛选出有名字的蓝牙
                if (device.getName() != null) {
                    Log.d(TAG, "onReceive: 扫描到bluetoothName:" + device.getName());
                    saveOneBluetoothInfo(mBluetoothAdapter, device, rssi);
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                // 扫描完成
                Log.d(TAG, "onReceive: 一次扫描完成");
            }
        }
    };


    // 蓝牙扫描线程
    private final Runnable bluetoothThread = new Runnable() {
        public void run() {
            Log.d(TAG, "bluetoothThread: 线程开始运行，线程id：" + workThread.getId());
            try {
                if (mBluetoothAdapter != null) {
                    if (mBluetoothAdapter.isEnabled()) {
                        threadFlag = true;
                        sendBroadcastThreadFlag();
                        do {
                            if (!Thread.interrupted() && threadFlag) {
                                Log.d(TAG, "bluetoothThread: 状态interrupted()：" + Thread.interrupted());
                                Log.d(TAG, "bluetoothThread: 标志threadFlag：" + threadFlag);

                                //设置蓝牙名称为匿名标识符
                                mBluetoothAdapter.setName(getCurrentIdentifier());
                                Log.d(TAG, "bluetoothThread: 已更改匿名标识符");

                                //开启蓝牙扫描
                                mBluetoothAdapter.startDiscovery();
                                Log.d(TAG, "bluetoothThread: 已开始扫描");

                                //睡眠
                                Log.d(TAG, "bluetoothThread: 睡眠");
                                Thread.sleep(30 * 1000L);

                                Log.d(TAG, "bluetoothThread: 唤醒");
                            } else {
                                Log.d(TAG, "bluetoothThread: 状态interrupted()：" + Thread.interrupted());
                                Log.d(TAG, "bluetoothThread: 标志threadFlag：" + threadFlag);
                                break;
                            }
                        } while (mBluetoothAdapter.isEnabled());
                    }
                    threadFlag = false;
                    mBluetoothAdapter.enable();
                    sendBroadcastThreadFlag();
                }
                stopSelf();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "bluetoothThread: 线程结束运行，线程id：" + workThread.getId());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: 服务创建");

        // 获取到蓝牙默认的适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        // 因为蓝牙搜索到设备和完成搜索都是通过广播来告诉其他应用的
        // 这里注册找到设备和完成搜索广播
        filter.addAction((BluetoothDevice.ACTION_FOUND));
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        // 获取服务通知
        Notification notification = createForegroundNotification();
        //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
        startForeground(NOTIFICATION_ID, notification);

        //注册蓝牙监听器
        registerReceiver(receiver, filter);

        // 开启蓝牙扫描线程
        this.workThread = new Thread(null, this.bluetoothThread, "WorkThread");
        this.workThread.start();

        Log.d(TAG, "onCreate: 启动前台服务");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: 服务开始");

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        threadFlag = false;
        // 注销蓝牙监听器
        unregisterReceiver(receiver);
        // 移除通知
        stopForeground(true);

        Log.d(TAG, "onDestroy: 停止前台服务");
        Log.d(TAG, "onDestroy: 标志threadFlag：" + threadFlag);
        Log.d(TAG, "onDestroy: 服务结束");
        super.onDestroy();

    }

    /**
     * 发送广播，内容是threadFlag
     */
    public void sendBroadcastThreadFlag() {
        myIntent = new Intent();
        myIntent.setAction(MY_ACTION_NAME);
        myIntent.putExtra("threadFlag", threadFlag);
        sendBroadcast(myIntent);
        Log.d(TAG, "broadcastThreadFlag: 发送广播，内容是threadFlag：" + threadFlag);

    }

    /**
     * 将一条蓝牙扫描记录保存到本地数据库
     *
     * @param mBluetoothAdapter
     * @param device
     * @param rssi
     * @return
     */
    public boolean saveOneBluetoothInfo(BluetoothAdapter mBluetoothAdapter,
                                        BluetoothDevice device, short rssi) {
        BluetoothInfo bluetoothInfo = new BluetoothInfo();

        int time_stamp;  //Unix时间戳，单位秒
        String target_identifier;
        String my_identifier;
        float distance;  //距离，单位米
        int duration;  //持续时长，单位秒
        String date;
        boolean is_used;

        time_stamp = (int) (System.currentTimeMillis() / 1000);
        target_identifier = device.getName();
        my_identifier = mBluetoothAdapter.getName();
        distance = Utility.getDistanceByRSSI(rssi);
        duration = 12;
        date = Utility.getDateString(0);
        is_used = false;

        bluetoothInfo.setTime_stamp(time_stamp);
        bluetoothInfo.setMy_identifier(my_identifier);
        bluetoothInfo.setTarget_identifier(target_identifier);
        bluetoothInfo.setDistance(distance);
        bluetoothInfo.setDuration(duration);
        bluetoothInfo.setDate(date);
        bluetoothInfo.setIs_used(is_used);
        bluetoothInfo.save();

        Log.d(TAG, "saveBluetoothInfo: 存储一条记录");
        return true;
    }

    /**
     * 获得此时间段的匿名标识符
     *
     * @return
     */
    public String getCurrentIdentifier() {
        int time_stamp = (int) (System.currentTimeMillis() / 1000);
        int seconds = time_stamp % (60 * 60 * 24);
        int number = seconds / (60 * 10);
        List<MyIdentifierInfo> myIdentifierInfoList = LitePal
                .where("identifier_id = ?", String.valueOf(number))
                .find(MyIdentifierInfo.class);
        return myIdentifierInfoList.get(0).getMy_identifier();
    }

    /**
     * 创建服务通知
     */
    private Notification createForegroundNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 唯一的通知通道的id.
        String notificationChannelId = "notification_channel_id_01";

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "前台服务消息";
            //通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("消息通道描述");
            //LED灯
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            //震动
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
        //通知小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //通知标题
        builder.setContentTitle("蓝牙扫描服务");
        //通知内容
        builder.setContentText("正在进行蓝牙扫描");
        //设定通知显示的时间
        builder.setWhen(System.currentTimeMillis());
        //设定启动的内容
        Intent activityIntent = new Intent(this, BluetoothActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        //创建通知并返回
        return builder.build();
    }

    /**
     * 判断蓝牙是否开启，如果关闭则请求打开蓝牙
     */
    private int checkBluetooth() {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                //调用enable()方法直接打开蓝牙
                if (!mBluetoothAdapter.enable()) {
                    Log.d(TAG, "checkBluetooth：蓝牙打开失败");
                    return 2; //蓝牙打开失败
                } else {
                    Log.d(TAG, "checkBluetooth：蓝牙未开启");
                    return 1; //蓝牙打开成功
                }
                //该方法也可以打开蓝牙，但是会有一个很丑的弹窗
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(enableBtIntent);
//                return 1;
            }
            Log.d(TAG, "checkBluetooth：蓝牙已打开");
            return 0; //蓝牙已打开
        }
        Log.d(TAG, "checkBluetooth：没有获得蓝牙适配器");
        return 3; //没有获得蓝牙适配器
    }


}