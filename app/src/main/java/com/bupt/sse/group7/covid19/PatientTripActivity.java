package com.bupt.sse.group7.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientTripActivity extends AppCompatActivity {

    TextView type,area,no;
    TextView dateStart,dateEnd;
    CardView query;
    int typeSelected=0;

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
        area=findViewById(R.id.area);
        no=findViewById(R.id.no);
        RelativeLayout type_layout=findViewById(R.id.type_layout);
        type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType();
            }
        });
        initTimePicker();
        query=findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> args=new HashMap<>();
                args.put("area",area.getText().toString());
                args.put("type",typeSelected+"");
                args.put("no",no.getText().toString());
                //使用 yyyy-MM-dd格式后台读入会报错，改用yyyy/mm/dd传输数据g
                String start=dateStart.getText().toString().replace("-","/");
                args.put("start",start);
                String end=dateEnd.getText().toString().replace("-","/");
                args.put("end",end);
                Call<String> data= DBConnector.dao.Get("/trip/search",args);
                data.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        JsonObject info= (JsonObject) JsonParser.parseString(response.body());

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });

    }
    private void initTimePicker(){
        dateStart=findViewById(R.id.date_start);
        dateEnd=findViewById(R.id.date_end);
        Calendar calendar=Calendar.getInstance();
        //今天的日期
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        dateEnd.setText(year+"-"+(month+1)+"-"+day);
        //7天前的日期
        calendar.add(Calendar.DATE,-7);
        int year7=calendar.get(Calendar.YEAR);
        int month7=calendar.get(Calendar.MONTH);
        int day7=calendar.get(Calendar.DAY_OF_MONTH);
        dateStart.setText(year7+"-"+(month7+1)+"-"+day7);

        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateStart.setText(year + "-" + (++month) + "-" + dayOfMonth);
                    }
                };
                DatePickerDialog datePickerDialog=new DatePickerDialog(PatientTripActivity.this, AlertDialog.THEME_HOLO_LIGHT,listener,year7,month7,day7);
                datePickerDialog.show();
            }
        });
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateEnd.setText(year + "-" + (++month) + "-" + dayOfMonth);
                    }
                };
                DatePickerDialog datePickerDialog=new DatePickerDialog(PatientTripActivity.this, AlertDialog.THEME_HOLO_LIGHT,listener,year,month,day);
                datePickerDialog.show();
            }
        });
    }

    private void selectType() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_trip, null);

        RecyclerView recyclerView = view.findViewById(R.id.trip_dialog);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        BottomSheetDialog typeDialog = new BottomSheetDialog(this);
        typeDialog.setContentView(view);
        typeDialog.show();
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String text) {
                type.setText(text);
                typeSelected=position;
                typeDialog.dismiss();
            }
        });


    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        //TODO 改类别
        private String types[] = {"全部", "飞机", "火车", "地铁", "公交车", "出租车", "轮船"};
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener li) {
            onItemClickListener = li;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_trip, parent, false);
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
}