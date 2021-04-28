package com.bupt.sse.group7.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

public class PatientTripActivity extends AppCompatActivity {

    TextView type;

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
        RelativeLayout type_layout=findViewById(R.id.type_layout);
        type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType();
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
                typeDialog.dismiss();
            }
        });


    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

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