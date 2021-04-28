package com.bupt.sse.group7.covid19.bluetoothService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.bupt.sse.group7.covid19.bluetoothDataEntity.BluetoothInfo;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.MyIdentifierInfo;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.MySecretKeyInfo;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.OtherSecretKeyInfo;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.AlgUtil;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.HttpUtil;
import com.bupt.sse.group7.covid19.utils.bluetoothModule.Utility;

import org.json.JSONException;
import org.litepal.LitePal;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirstWorkService extends Service {

    private static final String TAG = "FirstWorkService";
    private static final String API_URL = "http://192.168.43.129:8080/api/Bluetooth";
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 服务创建");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onCreate: 服务开始");

        String dateStringToday = Utility.getDateString(0);
        String dateString14dAgo = Utility.getDateString(13);

        // 清除本地过期的蓝牙扫描信息、每日跟踪秘钥、广播键、匿名标识符
        deleteBluetoothInfo(dateString14dAgo);
        deleteMySecretKeyInfo(dateString14dAgo);
        deleteOtherSecretKeyInfo(dateStringToday);
        deleteMyIdentifierInfo(dateStringToday);

        //检查并获得、存储每日跟踪秘钥和匿名标识符
        MySecretKeyInfo mySecretKeyInfo = LitePal.findLast(MySecretKeyInfo.class);
        if(mySecretKeyInfo == null || !mySecretKeyInfo.getDate().equals(dateStringToday)){
            try {
                Log.d(TAG, "onStartCommand: 没有检查到今日跟踪秘钥");
                getAndSaveMySK(dateStringToday);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onCreate: 服务结束");

    }

    /**
     *删除14天外的每日跟踪秘钥
     * @param dateString14dAgo
     * @return
     */
    private int deleteMySecretKeyInfo(String dateString14dAgo){
        LitePal.deleteAll(MySecretKeyInfo.class, "date < ?", dateString14dAgo);
        Log.d(TAG, "deleteMySecretKeyInfo: 已删除过期的每日跟踪秘钥信息");

        return 0;
    }

    /**
     *删除14天外的蓝牙扫描信息
     * @param dateString14dAgo
     * @return
     */
    private int deleteBluetoothInfo(String dateString14dAgo){
        LitePal.deleteAll(BluetoothInfo.class, "date < ?", dateString14dAgo);
        Log.d(TAG, "deleteBluetoothInfo: 已删除过期的蓝牙扫描信息");

        return 0;
    }

    /**
     *删除1天外的广播键信息
     * @param dateStringToday
     * @return
     */
    private int deleteOtherSecretKeyInfo(String dateStringToday){
        LitePal.deleteAll(OtherSecretKeyInfo.class, "date < ?", dateStringToday);
        Log.d(TAG, "deleteOtherSecretKeyInfo: 已删除过期的广播键信息");

        return 0;
    }

    /**
     *删除1天外的匿名标识符信息
     * @param dateStringToday
     * @return
     */
    private int deleteMyIdentifierInfo(String dateStringToday){
        LitePal.deleteAll(MyIdentifierInfo.class, "date < ?", dateStringToday);
        Log.d(TAG, "deleteMyIdentifierInfo: 已删除过期的匿名标识符信息");

        return 0;
    }

    /**
     * 获得并存储生成的每日跟踪键和匿名标识符
     * @return
     * @throws Exception
     */
    private int getAndSaveMySK(String dateStringToday) throws Exception {
        Log.d(TAG, "getAndSaveMySK: 开始获取今日跟踪秘钥和匿名标识符");

        String json = "{\"jsonrpc\":\"2.0\",\"method\":\"generateStrings\",\"params\":{\"apiKey\":\"3612e9a5-8486-4331-b83f-d72311af338d\",\"n\":4,\"length\":16,\"characters\":\"0123456789abcdefh\",\"replacement\":true,\"pregeneratedRandomization\":null},\"id\":15728}";
        RequestBody requestBody = RequestBody.create( MediaType.parse("application/json"), json);
        HttpUtil.sendOkHttpRequest("https://api.random.org/json-rpc/4/invoke", requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onResponse: 获得随机字符串失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String jsonStr = response.body().string();
                Log.d(TAG, "onResponse: 获得随机字符串成功jsonStr: "+jsonStr);

                //获得每日跟踪键
                String TK = null;
                try {
                    TK = AlgUtil.getTk(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "getAndSaveMySK: 获得每日跟踪键成功："+TK);

                //获得每日跟踪秘钥
                int DI = AlgUtil.getDayNumber();
                String Dtki = null;
                try {
                    Dtki = AlgUtil.getDtki(TK, DI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "getAndSaveMySK: 获得每日跟踪秘钥成功："+Dtki);

                //获得匿名标识符集合
                String[] Rpi = new String[0];
                try {
                    Rpi = AlgUtil.getRpijByDtki(Dtki);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "getAndSaveMySK: 获得匿名标识符成功："+Rpi);


                //存储生成的每日跟踪秘钥
                saveMySecretKeyInfo(Dtki, dateStringToday);
                //存储生成的匿名标识符
                for(int i = 0; i < Rpi.length; i++){
                    String my_identifier = Rpi[i];
                    saveMyIdentifierInfo(i, my_identifier, dateStringToday);
                }
                Log.d(TAG, "getAndSaveMySK: 已存储今日跟踪秘钥和匿名标识符");

            }
        });

        return 0;
    }

    /**
     * 存储生成的每日跟踪秘钥
     * @param Dtki
     * @param date
     * @return
     */
    private int saveMySecretKeyInfo(String Dtki, String date){
        MySecretKeyInfo mySecretKeyInfo = new MySecretKeyInfo();
        mySecretKeyInfo.setSecret_key(Dtki);
        mySecretKeyInfo.setDate(date);
        mySecretKeyInfo.setIs_used(false);
        mySecretKeyInfo.save();
        return 0;
    }

    /**
     * 存储生成的匿名标识符
     * @param my_identifier
     * @param date
     * @return
     */
    private int saveMyIdentifierInfo(int identifier_id, String my_identifier, String date){
        MyIdentifierInfo myIdentifierInfo = new MyIdentifierInfo();
        myIdentifierInfo.setIdentifier_id(identifier_id);
        myIdentifierInfo.setMy_identifier(my_identifier);
        myIdentifierInfo.setDate(date);
        myIdentifierInfo.save();
        return 0;
    }

}