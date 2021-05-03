package com.bupt.sse.group7.covid19.presenter;

import android.util.Log;

import com.bupt.sse.group7.covid19.interfaces.IDataBackCallBack;
import com.bupt.sse.group7.covid19.interfaces.IDataUpdateCalBack;
import com.bupt.sse.group7.covid19.interfaces.IHospitalViewCallBack;
import com.bupt.sse.group7.covid19.model.Hospital;
import com.bupt.sse.group7.covid19.model.Statistics;
import com.bupt.sse.group7.covid19.model.Supplies;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bupt.sse.group7.covid19.utils.JsonUtils.safeGet;

public class HospitalPresenter implements IDataBackCallBack, IDataUpdateCalBack {

    private final String TAG = "HospitalPresenter";
    private Hospital mHospital;

    private JsonObject hospitalResult;
    private JsonObject statisticsResult;
    private JsonObject suppliesResult;
    //总获取data个数
    private final int getDataSize = 3;
    //当前获取的data个数
    private int getDataCount = 0;
    //总更新data个数
    private final int updateDataSize = 2;
    //当前更新的data个数
    private int updateDataCount = 0;

    private List<IHospitalViewCallBack> callBacks = new ArrayList<>();

    private static HospitalPresenter instance = new HospitalPresenter();

    HospitalPresenter() {
        mHospital = new Hospital();
    }

    public static HospitalPresenter getInstance() {
        return instance;
    }


    private void getHospitalResult() {
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(mHospital.getId()));
        Call<ResponseBody> data = DBConnector.dao.executeGet("getHospitalById.php", args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    hospitalResult = JsonUtils.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
                    onAllDataBack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "getHospitalResultOnFailure");
                handleGetDataFailed();
            }
        });

    }

    private void handleGetDataFailed() {
        for (IHospitalViewCallBack callBack : callBacks) {
            callBack.onGetDataFailed();
        }
    }

    private void getStatisticsResult() {
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(mHospital.getId()));
        Call<ResponseBody> data = DBConnector.dao.executeGet("getStatusNumberById.php", args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    statisticsResult = JsonUtils.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
                    onAllDataBack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "getStatisticsResultOnFailure");
                handleGetDataFailed();
            }
        });
    }

    private void getSuppliesResult() {
        Map<String, String> args = new HashMap<>();
        args.put("h_id", String.valueOf(mHospital.getId()));
        Call<ResponseBody> data = DBConnector.dao.executeGet("getSuppliesById.php", args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JsonArray temp = JsonUtils.parseInfo(response.body().byteStream());
                    if (temp != null && temp.size() > 0) {
                        //如果该医院有物资信息，如果没有的话supplies始终为空
                        suppliesResult = temp.get(0).getAsJsonObject();
                        //返回supplies的json对象
                    } else {
                        suppliesResult = new JsonObject();
                    }
                    onAllDataBack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "getSuppliesResultOnFailure");
                handleGetDataFailed();
            }
        });

    }

    public void getHospitalDetails() {
        getDataCount = 0;
        getHospitalResult();
        getStatisticsResult();
        getSuppliesResult();
    }

    private void handleHospitalInfoResults() {
        for (IHospitalViewCallBack callBack : callBacks) {
            callBack.onHospitalInfoReturned(mHospital);
        }
    }

    public void registerCallBack(IHospitalViewCallBack callBack) {
        callBacks.add(callBack);
    }

    public void unregisterCallBack(IHospitalViewCallBack callBack) {
        callBacks.remove(callBack);
    }

    private void processResults() {
        mHospital.setAddress(safeGet(hospitalResult, "address"));
        mHospital.setTel(safeGet(hospitalResult, "tel"));
        mHospital.setMildLeft(safeGet(hospitalResult, "mild_left"));
        mHospital.setSevereLeft(safeGet(hospitalResult, "severe_left"));
        mHospital.setName(safeGet(hospitalResult, "name"));
        mHospital.setPeople(safeGet(hospitalResult, "contact"));

        mHospital.setStatistics(new Statistics(safeGet(statisticsResult, "mild"),
                safeGet(statisticsResult, "severe"),
                safeGet(statisticsResult, "cured"),
                safeGet(statisticsResult, "dead")));
        mHospital.setSupplies(new Supplies(safeGet(suppliesResult, "n95"),
                safeGet(suppliesResult, "surgeon"),
                safeGet(suppliesResult, "ventilator"),
                safeGet(suppliesResult, "clothe"),
                safeGet(suppliesResult, "glasses"),
                safeGet(suppliesResult, "alcohol"),
                safeGet(suppliesResult, "pants")));
    }

    public void setID(int id) {
        mHospital.setId(id);
    }


    private void updateSupplies(JsonObject supplies) {
        //更新Supplies
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(supplies));
        Call<String> call = DBConnector.dao.Post("editSuppliesById.php", body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "医院物资更新成功");
                onAllDataUpdated();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                handleGetDataFailed();
            }
        });
    }

    private void updateHospital(JsonObject hospital) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(hospital));
        Call<String> call = DBConnector.dao.Post("editHospitalById.php", body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "医院信息更新成功");
                onAllDataUpdated();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                handleGetDataFailed();
            }
        });
    }

    //第二个参数中包含supplies的所有信息
    public void updateData(Hospital hospital) {
        this.mHospital = hospital;
        updateDataCount = 0;
        JsonObject suppliesJO = new JsonObject();
        JsonObject contactJO = new JsonObject();
        Supplies supplies = hospital.getSupplies();
        suppliesJO.add("n95", new JsonPrimitive(supplies.getN95()));
        suppliesJO.add("surgeon", new JsonPrimitive(supplies.getSurgeon()));
        suppliesJO.add("ventilator", new JsonPrimitive(supplies.getVentilator()));
        suppliesJO.add("clothe", new JsonPrimitive(supplies.getClothe()));
        suppliesJO.add("glasses", new JsonPrimitive(supplies.getGlasses()));
        suppliesJO.add("alcohol", new JsonPrimitive(supplies.getAlcohol()));
        suppliesJO.add("pants", new JsonPrimitive(supplies.getPants()));

        JsonObject args1 = new JsonObject();
        args1.add("id", new JsonPrimitive(hospital.getId()));
        args1.add("row", suppliesJO);

        updateSupplies(args1);

        contactJO.add("address", new JsonPrimitive(hospital.getAddress()));
        contactJO.add("tel", new JsonPrimitive(hospital.getTel()));
        contactJO.add("contact", new JsonPrimitive(hospital.getPeople()));
        contactJO.add("mild_left", new JsonPrimitive(hospital.getMildLeft()));
        contactJO.add("severe_left", new JsonPrimitive(hospital.getSevereLeft()));

        JsonObject args2 = new JsonObject();
        args2.add("id", new JsonPrimitive(hospital.getId()));
        args2.add("row", contactJO);

        updateHospital(args2);


    }


    @Override
    public void onAllDataBack() {
        getDataCount++;
        if (getDataSize == getDataCount) {
            processResults();
            handleHospitalInfoResults();

        }
    }

    @Override
    public void onAllDataUpdated() {
        updateDataCount++;
        if (updateDataSize == updateDataCount) {
            handleHospitalInfoResults();
        }
    }
}
