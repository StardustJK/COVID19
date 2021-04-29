package com.bupt.sse.group7.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class PatientTripActivity extends AppCompatActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_trip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();


    }

    private void initView() {
        type = findViewById(R.id.type);
        area = findViewById(R.id.area);
        no = findViewById(R.id.no);
        RelativeLayout type_layout = findViewById(R.id.type_layout);
        type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType();
            }
        });
        initTimePicker();
        query = findViewById(R.id.query);
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
        safe = findViewById(R.id.safe);
        initResult();

    }

    private void initResult() {
        resultRv = findViewById(R.id.result_rv);
        resultRv.setLayoutManager(new LinearLayoutManager(this));
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
        ResultAdapter resultAdapter = new ResultAdapter(patientTripList);
        resultRv.setAdapter(resultAdapter);

    }

    private void initTimePicker() {
        dateStart = findViewById(R.id.date_start);
        dateEnd = findViewById(R.id.date_end);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(PatientTripActivity.this, AlertDialog.THEME_HOLO_LIGHT, listener, year7, month7, day7);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(PatientTripActivity.this, AlertDialog.THEME_HOLO_LIGHT, listener, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void selectType() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_trip, null);

        RecyclerView recyclerView = view.findViewById(R.id.trip_dialog);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        TypeAdapter adapter = new TypeAdapter();
        recyclerView.setAdapter(adapter);

        BottomSheetDialog typeDialog = new BottomSheetDialog(this);
        typeDialog.setContentView(view);
        typeDialog.show();
        adapter.setOnItemClickListener(new TypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String text) {
                type.setText(text);
                typeSelected = position;
                typeDialog.dismiss();
            }
        });


    }

    static class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.MyViewHolder> {

        //TODO 改类别
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener li) {
            onItemClickListener = li;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_type, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tv.setText(types[position]);
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(position,
                                types[position]);
                    }
                });
            }

        }


        @Override
        public int getItemCount() {
            return types.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.text);
            }
        }

        interface OnItemClickListener {
            void onItemClick(int position, String text);
        }
    }

    static class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {
        private List<PatientTrip> patientTripList;

        public ResultAdapter(List<PatientTrip> patientTrips) {
            patientTripList = patientTrips;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_result, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            PatientTrip patientTrip = patientTripList.get(position);
            holder.date.setText(patientTrip.getDate());
            holder.typeNo.setText(patientTrip.getTypeNo());
            holder.subNo.setText(patientTrip.getNoSub());
            holder.startPos.setText(patientTrip.getStartPos());
            holder.endPos.setText(patientTrip.getEndPos());
            holder.who.setText(patientTrip.getWho());
            if(patientTrip.getMemo().equals("")){
                holder.memo.setVisibility(View.GONE);
                holder.beizhu.setVisibility(View.GONE);
            }
            else {
                holder.memo.setText(patientTrip.getMemo());
            }
        }

        @Override
        public int getItemCount() {
            return patientTripList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView date, typeNo, subNo, startPos, endPos, who,memo,beizhu;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                typeNo = itemView.findViewById(R.id.type_no);
                subNo = itemView.findViewById(R.id.no_sub);
                startPos = itemView.findViewById(R.id.start_pos);
                endPos = itemView.findViewById(R.id.end_pos);
                who = itemView.findViewById(R.id.who);
                memo=itemView.findViewById(R.id.memo);
                beizhu=itemView.findViewById(R.id.beizhu);

            }
        }
    }
}

