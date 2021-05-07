package com.bupt.sse.group7.covid19.presenter;

import android.util.Log;
import android.widget.Toast;

import com.bupt.sse.group7.covid19.interfaces.IUserTripViewCallBack;
import com.bupt.sse.group7.covid19.model.UserTrip;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTripPresenter {
    private static final String TAG = "UserTripPresenter";
    private static String typeArray[] = {" ", "火车", "地铁", "公交车", "出租车", "轮船"};

    private static UserTripPresenter instance = new UserTripPresenter();

    private List<IUserTripViewCallBack> callBacks = new ArrayList<>();

    List<UserTrip> userTripList;


    public static UserTripPresenter getInstance() {
        return instance;
    }

    public void registerCallBack(IUserTripViewCallBack callBack) {
        if (callBacks != null && !callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    public void unregisterCallBack(IUserTripViewCallBack callBack) {
        if (callBacks != null) {
            callBacks.remove(callBack);
        }
    }

    public void getRisk(int userId) {

        //TODO 可能设置risk字段
        Map<String, String> args = new HashMap();
        args.put("user_id", userId + "");
        Call<String> data = DBConnector.dao.Get("trip/riskNotify", args);
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JsonObject result = (JsonObject) JsonParser.parseString(response.body());
                if (result.get("success").getAsBoolean()) {
                    handleRisk();
                } else {
                    Log.i(TAG, "getRisk " + result.get("message").getAsString());

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "getRisk onFailure");

            }
        });

    }

    public void getUserTripById(int userId) {

        Map<String, String> args = new HashMap();
        args.put("user_id", userId + "");
        Call<String> data = DBConnector.dao.Get("trip/getByUser", args);
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JsonObject result = (JsonObject) JsonParser.parseString(response.body());
                if (result.get("success").getAsBoolean()) {
                    userTripList = new ArrayList<>();
                    JsonArray jsonArray = result.get("data").getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                        UserTrip userTrip = new UserTrip();
                        userTrip.setDate(jsonObject.get("date").getAsString().split(" ")[0]);
                        userTrip.setType(jsonObject.get("type").getAsInt());
                        userTrip.setNo(jsonObject.get("no").getAsString());
                        userTrip.setNoSub(jsonObject.get("no_sub").getAsString());
                        userTrip.setStartPos(jsonObject.get("pos_start").getAsString());
                        userTrip.setEndPos(jsonObject.get("pos_end").getAsString());
                        userTrip.setMemo(jsonObject.get("memo").getAsString());
                        userTrip.setId(jsonObject.get("id").getAsInt());
                        userTrip.setRisk(jsonObject.get("risk").getAsBoolean());
                        userTripList.add(userTrip);
                    }

                    handleUserTripInfoReturned();

                } else {
                    handleGetDataFailed("无历史出行记录");


                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }

    private void handleRisk() {
        for (IUserTripViewCallBack callBack : callBacks) {
            callBack.onRisk();
        }


    }

    private void handleUserTripInfoReturned() {
        for (IUserTripViewCallBack callBack : callBacks) {
            callBack.onUserTripInfoReturned(userTripList);
        }
    }

    private void handleGetDataFailed(String msg) {
        for (IUserTripViewCallBack callBack : callBacks) {
            callBack.onGetZeroData(msg);
        }
    }
}
