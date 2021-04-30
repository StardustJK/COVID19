package com.bupt.sse.group7.covid19.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.bupt.sse.group7.covid19.adapter.UserTripHistoryAdapter;
import com.bupt.sse.group7.covid19.dialog.AddTripDialog;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.UserTrip;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientTripRecordFragment extends Fragment {
    private static String typeArray[] = {" ", "火车", "地铁", "公交车", "出租车", "轮船"};
    static Map<String,Integer> typeMap;
    static {
        typeMap =new HashMap<>();
        typeMap.put("飞机",1);
        typeMap.put("火车",2);
        typeMap.put("地铁",3);
        typeMap.put("大巴",4);
        typeMap.put("公交车",5);
        typeMap.put("出租车",6);
        typeMap.put("轮船",7);
        typeMap.put("其他",8);
    }
    ImageView addBtn;
    TextView title;
    RecyclerView history;
    AddTripDialog addTripDialog;

    List<UserTrip> userTripList;

    CurrentUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_record, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        title = view.findViewById(R.id.title);
        currentUser = CurrentUser.getCurrentUser();
        if (currentUser == null) {
            title.setText("无历史出行记录");
        }
        addBtn = view.findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_LONG).show();
                    Intent auth = new Intent(getActivity(), AuthenticateActivity.class);
                    startActivity(auth);
                    getActivity().finish();
                } else {
                    addTripDialog = new AddTripDialog(getActivity());
                    addTripDialog.setCancelClickListener(new AddTripDialog.CancelClickListener() {
                        @Override
                        public void onCancelClick() {
                            addTripDialog.dismiss();
                        }
                    });

                    addTripDialog.setYesClickListener(new AddTripDialog.YesClickListener() {
                        @Override
                        public void onYesClick(UserTrip userTrip) {
                            addTrip(userTrip);
                            addTripDialog.dismiss();
//                            /更新数据
                            initView(view);
                        }


                    });
                    addTripDialog.show();
                    DisplayMetrics dm=new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    addTripDialog.getWindow().setLayout(dm.widthPixels,addTripDialog.getWindow().getAttributes().height);
                }
            }
        });

        history = view.findViewById(R.id.history_rv);
        history.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        initHistoryView();


    }

    private void addTrip(UserTrip userTrip) {
        JsonObject param=new JsonObject();
        param.add("userId", new JsonPrimitive(currentUser.getUserId()));
        param.add("date",new JsonPrimitive(userTrip.getDate()));
        param.add("type",new JsonPrimitive(typeMap.get(userTrip.getType())));
        param.add("no",new JsonPrimitive(userTrip.getNo()));
        param.add("memo",new JsonPrimitive(userTrip.getMemo()));
        param.add("no_sub",new JsonPrimitive(userTrip.getNoSub()));
        param.add("pos_start",new JsonPrimitive(userTrip.getStartPos()));
        param.add("pos_end",new JsonPrimitive(userTrip.getEndPos()));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(param));

        Call<String> post = DBConnector.dao.Post("trip/add", body);
        post.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JsonObject jsonObject= (JsonObject) JsonParser.parseString(response.body());
                Toast.makeText(getActivity(),jsonObject.get("message").getAsString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(),"网络错误，请稍后重试",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initHistoryView() {

        if (currentUser!= null) {
            int userId =currentUser.getUserId();

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
                            userTrip.setType(typeArray[jsonObject.get("type").getAsInt()]);
                            userTrip.setNo(jsonObject.get("no").getAsString());
                            userTrip.setNoSub(jsonObject.get("no_sub").getAsString());
                            userTrip.setStartPos(jsonObject.get("pos_start").getAsString());
                            userTrip.setEndPos(jsonObject.get("pos_end").getAsString());
                            userTrip.setMemo(jsonObject.get("memo").getAsString());
                            userTripList.add(userTrip);
                        }

                        UserTripHistoryAdapter userTripHistoryAdapter = new UserTripHistoryAdapter(userTripList);
                        history.setAdapter(userTripHistoryAdapter);
                    } else {

                        title.setText("无历史出行记录");

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getActivity(), "网络错误请稍后重试", Toast.LENGTH_LONG).show();

                }
            });


        }


    }
}
