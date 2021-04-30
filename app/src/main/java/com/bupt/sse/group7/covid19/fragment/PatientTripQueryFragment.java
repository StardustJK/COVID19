package com.bupt.sse.group7.covid19.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.PatientTripActivity;
import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.adapter.TripResultAdapter;
import com.bupt.sse.group7.covid19.adapter.TypeChooseAdapter;
import com.bupt.sse.group7.covid19.model.PatientTrip;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientTripQueryFragment extends Fragment {
    private static String types[] = {"全部", "飞机", "火车", "地铁", "公交车", "出租车", "轮船"};

    TextView type, area, no;
    TextView dateStart, dateEnd;
    CardView query;
    int typeSelected = 0;
    RecyclerView resultRv;

    List<PatientTrip> patientTripList;
    String today, day7ago;
    TextView safe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search_trip,container,false);


        initView(view);

        return view;
    }

    private void initView(View view) {
        type = view.findViewById(R.id.type);
        area = view.findViewById(R.id.area);
        no = view.findViewById(R.id.no);
        RelativeLayout type_layout = view.findViewById(R.id.type_layout);
        type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType();
            }
        });
        initTimePicker(view);
        query = view.findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> args = new HashMap<>();
                args.put("area", area.getText().toString());
                args.put("type", typeSelected + "");
                args.put("no", no.getText().toString());
                //使用 yyyy-MM-dd格式后台读入会报错，改用yyyy/mm/dd传输数据g
                String start = dateStart.getText().toString().replace("-", "/");
                args.put("start", start);
                String end = dateEnd.getText().toString().replace("-", "/");
                args.put("end", end);
                Call<String> data = DBConnector.dao.Get("/trip/search", args);
                data.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        JsonObject info = (JsonObject) JsonParser.parseString(response.body());
                        parseData(info);


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        safe.setVisibility(View.VISIBLE);
                        resultRv.setVisibility(View.GONE);
                    }
                });
            }
        });
        safe = view.findViewById(R.id.safe);
        initResult(view);

    }

    private void initResult(View view) {
        resultRv = view.findViewById(R.id.result_rv);
        resultRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        //获取初始数据，7天内所有type
        Map<String, String> args = new HashMap<>();
        args.put("area", "");
        args.put("type", "0");
        args.put("no", "");
        args.put("start", day7ago);
        args.put("end", today);
        Call<String> data = DBConnector.dao.Get("/trip/search", args);
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JsonObject info = (JsonObject) JsonParser.parseString(response.body());
                parseData(info);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                safe.setVisibility(View.VISIBLE);
                resultRv.setVisibility(View.GONE);
            }
        });


    }

    private void parseData(JsonObject info) {
        if (!info.get("success").getAsBoolean()) {
            safe.setVisibility(View.VISIBLE);
            resultRv.setVisibility(View.GONE);
            return;
        }
        safe.setVisibility(View.GONE);
        resultRv.setVisibility(View.VISIBLE);
        patientTripList = new ArrayList<>();
        JsonArray trips = info.get("data").getAsJsonArray();
        for (int i = 0; i < trips.size(); i++) {
            PatientTrip patientTrip = new PatientTrip();
            JsonObject trip = trips.get(i).getAsJsonObject();
            String date=trip.get("t_date").getAsString().split(" ")[0];
            patientTrip.setDate(date);
            patientTrip.setTypeNo(types[trip.get("t_type").getAsInt()]
                    + trip.get("t_no").getAsString());
            patientTrip.setNoSub(trip.get("t_no_sub").getAsString());
            patientTrip.setStartPos(trip.get("t_pos_start").getAsString());
            patientTrip.setEndPos(trip.get("t_pos_end").getAsString());
            patientTrip.setWho(trip.get("who").getAsString());
            patientTrip.setMemo(trip.get("t_memo").getAsString());
            patientTripList.add(patientTrip);
        }
        TripResultAdapter resultAdapter = new TripResultAdapter(patientTripList);
        resultRv.setAdapter(resultAdapter);

    }

    private void initTimePicker(View view) {
        dateStart = view.findViewById(R.id.date_start);
        dateEnd = view.findViewById(R.id.date_end);
        Calendar calendar = Calendar.getInstance();
        //今天的日期
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateEnd.setText(year + "-" + (month + 1) + "-" + day);
        today = year + "/" + (month + 1) + "/" + day;
        //7天前的日期
        calendar.add(Calendar.DATE, -7);
        int year7 = calendar.get(Calendar.YEAR);
        int month7 = calendar.get(Calendar.MONTH);
        int day7 = calendar.get(Calendar.DAY_OF_MONTH);
        dateStart.setText(year7 + "-" + (month7 + 1) + "-" + day7);
        day7ago = year7 + "/" + (month7 + 1) + "/" + day7;

        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateStart.setText(year + "-" + (++month) + "-" + dayOfMonth);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, listener, year7, month7, day7);
                datePickerDialog.show();
            }
        });
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateEnd.setText(year + "-" + (++month) + "-" + dayOfMonth);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, listener, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void selectType() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_trip, null);

        RecyclerView recyclerView = view.findViewById(R.id.trip_dialog);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        TypeChooseAdapter adapter = new TypeChooseAdapter(types);
        recyclerView.setAdapter(adapter);

        BottomSheetDialog typeDialog = new BottomSheetDialog(getActivity());
        typeDialog.setContentView(view);
        typeDialog.show();
        adapter.setOnItemClickListener(new TypeChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String text) {
                type.setText(text);
                typeSelected = position;
                typeDialog.dismiss();
            }
        });


    }

}
