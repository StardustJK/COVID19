package com.bupt.sse.group7.covid19.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.AuthenticateActivity;
import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.adapter.TripResultAdapter;
import com.bupt.sse.group7.covid19.adapter.UserTripHistoryAdapter;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.UserTrip;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientTripRecordFragment extends Fragment {
    private static String types[] = { " ", "火车", "地铁", "公交车", "出租车", "轮船"};

    ImageView addBtn;
    TextView title;
    RecyclerView history;

    List<UserTrip> userTripList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_trip_record,container,false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        title=view.findViewById(R.id.title);
        CurrentUser currentUser = CurrentUser.getCurrentUser();
        if(currentUser==null){
            title.setText("无历史出行记录");
        }
        addBtn=view.findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser==null){
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_LONG).show();
                    Intent auth=new Intent(getActivity(), AuthenticateActivity.class);
                    startActivity(auth);
                    getActivity().finish();
                }
                else {
                    Toast.makeText(getActivity(),"添加记录",Toast.LENGTH_LONG).show();

                }
            }
        });

        history=view.findViewById(R.id.history_rv);
        history.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        initHistoryView();


    }

    private void initHistoryView() {

        if(CurrentUser.getCurrentUser()!=null){
            int userId=CurrentUser.getCurrentUser().getUserId();

            Map<String,String> args=new HashMap();
            args.put("user_id",userId+"");
            Call<String> data = DBConnector.dao.Get("trip/getByUser", args);
            data.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    JsonObject result= (JsonObject) JsonParser.parseString(response.body());
                    if(result.get("success").getAsBoolean()){
                        userTripList=new ArrayList<>();
                        JsonArray jsonArray=result.get("data").getAsJsonArray();
                        for(int i=0;i<jsonArray.size();i++){
                            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                            UserTrip userTrip=new UserTrip();
                            userTrip.setDate(jsonObject.get("date").getAsString().split(" ")[0]);
                            userTrip.setTypeNo(types[jsonObject.get("type").getAsInt()]
                                    + jsonObject.get("no").getAsString());
                            userTrip.setNoSub(jsonObject.get("no_sub").getAsString());
                            userTrip.setStartPos(jsonObject.get("pos_start").getAsString());
                            userTrip.setEndPos(jsonObject.get("pos_end").getAsString());
                            userTrip.setMemo(jsonObject.get("memo").getAsString());
                            userTripList.add(userTrip);
                        }

                        UserTripHistoryAdapter userTripHistoryAdapter=new UserTripHistoryAdapter(userTripList);
                        history.setAdapter(userTripHistoryAdapter);
                    }
                    else{

                        title.setText("无历史出行记录");

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getActivity(),"网络错误请稍后重试",Toast.LENGTH_LONG).show();

                }
            });


        }






    }
}
