package com.bupt.sse.group7.covid19;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.adapter.PatientTripPagerAdapter;

import org.w3c.dom.Text;


public class PatientTripActivity extends AppCompatActivity{

TextView query,record;
ViewPager2 viewPager;

PatientTripPagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        query=findViewById(R.id.query_btn);
        record=findViewById(R.id.record_btn);
        mAdapter=new PatientTripPagerAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager=findViewById(R.id.viewpager);
        viewPager.setAdapter(mAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //TODO改变按钮状态

//                switch (position){
//                    case 0:
//                        query.setSelected(true);
//                        break;
//                    case 1:
//                        record.setSelected(true);
//                        break;
//                }
            }

        });

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

