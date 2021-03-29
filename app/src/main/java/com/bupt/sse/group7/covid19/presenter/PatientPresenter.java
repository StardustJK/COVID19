package com.bupt.sse.group7.covid19.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bupt.sse.group7.covid19.interfaces.IDataBackCallBack;
import com.bupt.sse.group7.covid19.interfaces.IPatientViewCallBack;
import com.bupt.sse.group7.covid19.model.Patient;
import com.bupt.sse.group7.covid19.model.Status;
import com.bupt.sse.group7.covid19.model.TrackPoint;
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

public class PatientPresenter implements IDataBackCallBack {
    private static final String TAG = "PatientPresenter";

    private static PatientPresenter instance = new PatientPresenter();
    private List<IPatientViewCallBack> patientViewCallBacks = new ArrayList<>();

    private Patient patient;
    private JsonObject patientResult;
    private JsonArray pStatusResult;
    private JsonArray tracksResult;

    private final int dataCount = 3;
    private int dataSize = 0;


    PatientPresenter() {
        patient = new Patient();
    }

    public void getPatientInfo() {
        dataSize = 0;
        getPatientResult();
        getTrackResult();
        getStatusResult();

    }

    private void getPatientResult() {
        Map<String, String> args = new HashMap<>();
        args.put("p_id", String.valueOf(patient.getId()));
        Call<ResponseBody> data = DBConnector.dao.executeGet("getPatientById.php", args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    patientResult = JsonUtils.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
                    processPatientResult();
                    Log.i("hcccc", "processPatientResultDOwn");

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
        for (IPatientViewCallBack callBack : patientViewCallBacks) {
            callBack.onGetDataFailed();
        }
    }

    private void getStatusResult() {
        Map<String, String> args = new HashMap<>();
        args.put("p_id", String.valueOf(patient.getId()));
        Call<ResponseBody> data = DBConnector.dao.executeGet("getPStatusById.php", args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    pStatusResult = JsonUtils.parseInfo(response.body().byteStream());
                    processStatusResult();
                    Log.i("hcccc", "processStatusResultDown");

                    onAllDataBack();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleGetDataFailed();

            }
        });

    }

    private void getTrackResult() {
        Map<String, String> args = new HashMap<>();
        args.put("p_id", String.valueOf(patient.getId()));
        Call<ResponseBody> data = DBConnector.dao.executeGet("getPatientTrackById.php", args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    tracksResult = JsonUtils.parseInfo(response.body().byteStream());
                    processTrackResults();
                    Log.i("hcccc", "processTrackResultsDown");

                    onAllDataBack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleGetDataFailed();
            }
        });

    }

    private void handlePatientInfoResult() {
        Log.i("hcccc", "handlePatientInfoResult");
        for (IPatientViewCallBack callBack : patientViewCallBacks) {
            callBack.onPatientInfoReturned(this.patient);
        }
    }

    public void registerCallBack(IPatientViewCallBack callBack) {
        if (patientViewCallBacks != null && !patientViewCallBacks.contains(callBack)) {
            patientViewCallBacks.add(callBack);
        }
    }

    public void unregisterCallBack(IPatientViewCallBack callBack) {
        if (patientViewCallBacks != null) {
            patientViewCallBacks.remove(callBack);
        }
    }


    private void processPatientResult() {
        this.patient.setH_name(patientResult.get("h_name").getAsString());
        this.patient.setUsername(patientResult.get("username").getAsString());
        this.patient.setStatus(patientResult.get("status").getAsString());

    }

    private void processStatusResult() {
        this.patient.setStatuses(new ArrayList<>());
        for (JsonElement je : pStatusResult) {
            this.patient.getStatuses().add(
                    new Status(je.getAsJsonObject().get("day").getAsString(),
                            je.getAsJsonObject().get("status").getAsString()));
        }

    }

    private void processTrackResults() {
        // parse data and assign
        this.patient.setTrackPoints(new ArrayList<>());
        for (JsonElement je : tracksResult) {
            this.patient.getTrackPoints().add(
                    new TrackPoint(je.getAsJsonObject().get("date_time").getAsString(),
                            je.getAsJsonObject().get("location").getAsString(),
                            je.getAsJsonObject().get("description").getAsString()));
        }

    }

    public static PatientPresenter getInstance() {
        return instance;
    }

    public void setPatientId(String id) {
        this.patient.setId(id);
    }

    @Override
    public void onAllDataBack() {
        dataSize++;
        if (dataSize == dataCount) {
            handlePatientInfoResult();

        }
    }
}

