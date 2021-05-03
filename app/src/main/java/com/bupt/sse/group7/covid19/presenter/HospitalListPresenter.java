package com.bupt.sse.group7.covid19.presenter;

import android.util.Log;

import com.bupt.sse.group7.covid19.interfaces.IHospitalListCallBack;
import com.bupt.sse.group7.covid19.model.HospitalBrief;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalListPresenter {
    private static final String TAG = "HospitalListPresenter";
    private String areaId;
    private static HospitalListPresenter instance = new HospitalListPresenter();
    private List<IHospitalListCallBack> callBacks = new ArrayList<>();

    public static HospitalListPresenter getInstance() {
        return instance;
    }

    public void registerCallBack(IHospitalListCallBack callBack) {
        if (callBacks != null && !callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    public void unregisterCallBack(IHospitalListCallBack callBack) {
        if (callBacks != null) {
            callBacks.remove(callBack);
        }
    }

    public void updateData(List<HospitalBrief> hospitals) {
        for (IHospitalListCallBack callBack : callBacks) {
            callBack.onHospitalListChanged(hospitals);
        }
    }

    public void setAreaId(String id) {
        this.areaId = id;
    }

    public void initData() {
        Call<ResponseBody> data;
        Map<String, String> args = new HashMap<>();
        if (areaId.length() == 4) {
            args.put("city", areaId);
            Log.d(TAG, "initData: " + areaId);
            //data = DBConnector.dao.executeGet("getHospitalListByCity.php", args);
            data=DBConnector.dao.executeGet("hello");
        } else {
            args.put("district", areaId);
            Log.d(TAG, "initData: " + areaId);
            //data = DBConnector.dao.executeGet("getHospitalListByDistrict.php", args);
            data=DBConnector.dao.executeGet("hello");

        }
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    List<HospitalBrief> hospitals = processResult(JsonUtils.parseInfo(response.body().byteStream()));
                    updateData(hospitals);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "HospitalListActivityOnFailure");
                handleOnGetDataFailed();
            }
        });
    }

    private void handleOnGetDataFailed() {
        for (IHospitalListCallBack callBack : callBacks) {
            callBack.onGetDataFailed();
        }
    }

    private List<HospitalBrief> processResult(JsonArray hospJson) {
        List<HospitalBrief> hospitals = new ArrayList<>();
        for (JsonElement hospital : hospJson) {
            JsonObject hospitalJson = hospital.getAsJsonObject();
            HospitalBrief hospitalBrief = new HospitalBrief(hospitalJson.get("h_id").getAsString(),
                    hospitalJson.get("name").getAsString());
            if (!hospitalJson.get("address").isJsonNull()) {
                hospitalBrief.setAddress(hospitalJson.get("address").getAsString());
            }
            hospitals.add(hospitalBrief);
        }
        return hospitals;
    }
}
