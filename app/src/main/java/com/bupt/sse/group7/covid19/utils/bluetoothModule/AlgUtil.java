package com.bupt.sse.group7.covid19.utils.bluetoothModule;

import android.util.Log;

import com.bupt.sse.group7.covid19.bluetoothDataEntity.BluetoothInfo;
import com.bupt.sse.group7.covid19.bluetoothDataEntity.OtherSecretKeyInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

public class AlgUtil {
    private static final String TAG = "AlgUtil";

    /**
     * 通过Unix时间戳获得今天的天数
     * @return
     */
    public static int getDayNumber() {
        int dateStr = Integer.parseInt(Long.toString(System.currentTimeMillis() / 1000L)) / (60 * 60 * 24);
        return dateStr;
    }

    //获得Unix时间戳
    public static int getUnixTime() {
        int dateStr = Integer.parseInt(Long.toString(System.currentTimeMillis() / 1000L));
        return dateStr;
    }

    /**
     * 根据每日跟踪秘钥获得匿名标识符表
     * @param dtki
     * @return
     * @throws Exception
     */
    public static String[] getRpijByDtki(String dtki) throws Exception {
        String[] Rpijs = new String[144];
        String temp = "";
        for (int i = 0; i < 144; i++) {
            temp = dtki + i;
            Rpijs[i] = digest("SHA-256", temp.getBytes()).substring(0, 16);
        }
        return Rpijs;
    }

    /**
     * 获得每日跟踪秘钥
     * @param tk
     * @param Di
     * @return
     * @throws Exception
     */
    public static String getDtki(String tk, int Di) throws Exception {
        String dtki = tk + Di;
        return digest("SHA-256", dtki.getBytes()).substring(0, 32);
    }

    //返回64位16进制数（可选SHA-256）
    public static String digest(String algorithm, byte[] content) throws Exception {
        MessageDigest instance = MessageDigest.getInstance(algorithm);
        instance.update(content);
        //当所有数据已被更新,调用digest()方法完成哈希计算,返回字节数组
        byte[] digest = instance.digest();
        //System.out.println("算法=" + algorithm + ",摘要=" + DatatypeConverter.printHexBinary(digest));
        return DatatypeConverter.printHexBinary(digest);
    }


    //获得每日跟踪键
    public static String getTk(String jsonStr) throws JSONException {
        JSONObject j1 = new JSONObject(jsonStr);
        j1 = j1.getJSONObject("result");
        j1 = j1.getJSONObject("random");
        JSONArray a1 = j1.getJSONArray("data");
        String TK = "";
        for (int i = 0; i < 4; i++) {
            String item = (String) a1.get(i);
            TK += item;
        }
        Log.d(TAG, "getTk: 获得随机字符串成功re："+TK);

        return TK;
    }

    /**
     * 进行广播键集和本地蓝牙扫描信息的匹配
     * @return 匹配成功的信息条数
     * @throws Exception
     */
    public static int getCheckResult() throws Exception {
        Log.d(TAG, "getCheckResult: 开始本地匹配");

        String todayDateString = Utility.getDateString(0);

        //获取广播键列表，即确诊用户的密钥列表
        List<OtherSecretKeyInfo> SKInfoList = LitePal.findAll(OtherSecretKeyInfo.class);
        //获得本机的蓝牙扫描信息表
        List<BluetoothInfo> BTInfoList = LitePal.findAll(BluetoothInfo.class);

        //将广播键列表按与今天相隔的天数，分为14组
        ArrayList<OtherSecretKeyInfo>[] SKInfoArrayList = new ArrayList[14];
        for(int i = 0; i < 14; i++)
            SKInfoArrayList[i] = new ArrayList<OtherSecretKeyInfo>();
        for(OtherSecretKeyInfo SKInfo : SKInfoList){
            int n = Utility.getDaysBetween(SKInfo.getDate(), todayDateString);
            SKInfoArrayList[n].add(SKInfo);
        }

        //将蓝牙扫描信息表按与今天相隔的天数，分为14组
        ArrayList<BluetoothInfo>[] BTInfoArrayList = new ArrayList[14];
        for(int i = 0; i < 14; i++)
            BTInfoArrayList[i] = new  ArrayList<BluetoothInfo>();
        for(BluetoothInfo IdInfo : BTInfoList){
            int n = Utility.getDaysBetween(IdInfo.getDate(), todayDateString);
            BTInfoArrayList[n].add(IdInfo);
        }

        int result = 0;

        //将广播键和蓝牙扫描信息分为14天的，每天一轮匹配，即14轮匹配
        for(int i = 0; i < 14; i++ ){
            //判断在此轮匹配中，广播键和蓝牙扫描信息是否都存在，
            //若都存在则继续下一步
            if(SKInfoArrayList[i]!=null && SKInfoArrayList[i].size() > 0 &&
                    BTInfoArrayList[i]!=null && BTInfoArrayList[i].size() > 0){
                //在此轮匹配中，对于每一个广播键都生成一个匿名标识符列表
                //每一个匿名标识符列表大小是144
                for(int x = 0; x < SKInfoArrayList[i].size(); x++){
                    String[] rpijs = AlgUtil.getRpijByDtki(
                            SKInfoArrayList[i].get(x).getSecret_key());
                    //将生成的匿名标识符列表与蓝牙扫描信息里的目标标识符进行匹配
                    for(int j = 0; j < 144; j++){
                        for(int n = 0; n < BTInfoArrayList[i].size(); n++){
                            //若有匹配成功的记录，则result加1
                            if(BTInfoArrayList[i].get(n).getTarget_identifier().equals(rpijs[j]))
                                result++;
                        }
                    }
                }
            }
        }
        Log.d(TAG, "getCheckResult: 本地匹配结束，result："+result);

        return result;
    }

}


