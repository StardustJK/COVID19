package com.bupt.sse.group7.covid19.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.AuthenticateActivity;
import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.adapter.TripResultAdapter;
import com.bupt.sse.group7.covid19.interfaces.IUserTripViewCallBack;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.PatientTrip;
import com.bupt.sse.group7.covid19.model.UserTrip;
import com.bupt.sse.group7.covid19.presenter.UserTripPresenter;
import com.bupt.sse.group7.covid19.utils.Constants;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientTripRiskFragment extends Fragment implements IUserTripViewCallBack {

    private static final String TAG = "PatientTripRiskFragment";
    RecyclerView riskRv;
    TextView safe;
    UserTripPresenter  userTripPresenter;
    CardView login_btn;
    LinearLayout not_login;
    ImageView toggle;
    CurrentUser currentUser=CurrentUser.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_trip_risk,container,false);


        initView(view);

        return view;
    }

    private void initView(View view) {
        not_login=view.findViewById(R.id.not_login);
        login_btn =view.findViewById(R.id.login);
        safe=view.findViewById(R.id.safe);
        riskRv=view.findViewById(R.id.risk_rv);
        riskRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        toggle=view.findViewById(R.id.toggle);

        if(currentUser.isShowTripRisk()){
            toggle.setImageResource(R.drawable.switch_open);
            toggle.setTag("open");
        }else {
            toggle.setImageResource(R.drawable.switch_close);
            toggle.setTag("close");
        }

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle.getTag().equals("open")){
                    Map<String,String> param=new HashMap<>();
                    param.put("user_id",currentUser.getUserId()+"");
                    param.put("show","false");
                    Call<String> get = DBConnector.dao.Get("user/updateShowTripRisk", param);
                    get.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            JsonObject jsonObject= (JsonObject) JsonParser.parseString(response.body());
                            if(jsonObject.get("success").getAsBoolean()){
                                toggle.setImageResource(R.drawable.switch_close);
                                toggle.setTag("close");
                                currentUser.setShowTripRisk(false);
                            }
                            else {
                                Log.i(TAG,"关闭失败");
                                Toast.makeText(getActivity(),"关闭失败,请稍后重试",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.i(TAG,"关闭失败");
                            Toast.makeText(getActivity(),"关闭失败,请稍后重试",Toast.LENGTH_LONG).show();
                        }
                    });

                }
                else{
                    Map<String,String> param=new HashMap<>();
                    param.put("user_id",currentUser.getUserId()+"");
                    param.put("show","true");
                    Call<String> get = DBConnector.dao.Get("user/updateShowTripRisk", param);
                    get.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            JsonObject jsonObject= (JsonObject) JsonParser.parseString(response.body());
                            if(jsonObject.get("success").getAsBoolean()){
                                toggle.setImageResource(R.drawable.switch_open);
                                toggle.setTag("open");
                                currentUser.setShowTripRisk(true);
                            }
                            else {
                                Log.i(TAG,"打开失败");
                                Toast.makeText(getActivity(),"打开失败,请稍后重试",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.i(TAG,"打开失败");
                            Toast.makeText(getActivity(),"打开失败,请稍后重试",Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });

        userTripPresenter=UserTripPresenter.getInstance();
        userTripPresenter.registerCallBack(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent auth = new Intent(getActivity(), AuthenticateActivity.class);
                startActivity(auth);
                getActivity().finish();
            }
        });
        CurrentUser currentUser=CurrentUser.getCurrentUser();
        if (currentUser!= null) {
            int userId =currentUser.getUserId();
            userTripPresenter.getUserTripById(userId);
        }
        else {
            safe.setText("登录查看出行风险");
            safe.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.VISIBLE);
            not_login.setVisibility(View.VISIBLE);
            riskRv.setVisibility(View.GONE);



        }

    }


    @Override
    public void onUserTripInfoReturned(List<UserTrip> tripList) {
        riskRv.setVisibility(View.VISIBLE);
        not_login.setVisibility(View.GONE);
        //查询每个usertrip有没有风险
        String listString=new Gson().toJson(tripList);


        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(listString));
        Call<String> post = DBConnector.dao.Post("trip/risk", body);
        post.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JsonObject jsonObject=(JsonObject) JsonParser.parseString(response.body());
                if(jsonObject.get("success").getAsBoolean()){
                    List<PatientTrip> patientTripList = new ArrayList<>();
                    JsonArray trips = jsonObject.get("data").getAsJsonArray();
                    for (int i = 0; i < trips.size(); i++) {
                        PatientTrip patientTrip = new PatientTrip();
                        JsonObject trip = trips.get(i).getAsJsonObject();
                        String date=trip.get("t_date").getAsString().split(" ")[0];
                        patientTrip.setDate(date);
                        patientTrip.setTypeNo(Constants.types[trip.get("t_type").getAsInt()]
                                + trip.get("t_no").getAsString());
                        patientTrip.setNoSub(trip.get("t_no_sub").getAsString());
                        patientTrip.setStartPos(trip.get("t_pos_start").getAsString());
                        patientTrip.setEndPos(trip.get("t_pos_end").getAsString());
                        patientTrip.setWho(trip.get("who").getAsString());
                        patientTrip.setMemo(trip.get("t_memo").getAsString());
                        patientTripList.add(patientTrip);
                    }
                    TripResultAdapter resultAdapter = new TripResultAdapter(patientTripList);
                    riskRv.setAdapter(resultAdapter);
                }
                else {
                    noRisk();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                noRisk();
            }
        });


    }

    public void noRisk(){
        safe.setText("无出行风险");
        safe.setVisibility(View.VISIBLE);
        not_login.setVisibility(View.VISIBLE);
        riskRv.setVisibility(View.GONE);
        login_btn.setVisibility(View.GONE);

    }
    @Override
    public void onGetZeroData(String msg) {
        noRisk();

    }

    @Override
    public void onRisk() {

    }
}
