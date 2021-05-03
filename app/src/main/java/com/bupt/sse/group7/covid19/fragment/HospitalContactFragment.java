package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bupt.sse.group7.covid19.R;

/**
 * 医院主页，联系方式卡片部分
 */
public class HospitalContactFragment extends Fragment {
    private View view;


    TextView hosp_tel, hosp_address, hosp_inCharge;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hospital_contact, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }

    private void initView() {
        hosp_tel = view.findViewById(R.id.hosp_tel);
        hosp_address = view.findViewById(R.id.hosp_address);
        hosp_inCharge = view.findViewById(R.id.hosp_inCharge);
    }

    public void updateView(String tel, String address, String people) {
        hosp_tel.setText(tel);
        hosp_address.setText(address);
        hosp_inCharge.setText(people);

    }

}
