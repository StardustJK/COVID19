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
import com.bupt.sse.group7.covid19.model.Supplies;

/**
 * 医院主页，物资情况卡片部分
 */
public class HospitalSuppliesFragment extends Fragment {
    private View view;

    private TextView n95, surgeon, ventilator, clothe, glasses, alcohol, pants;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hospital_supplies, container, false);
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
        n95 = view.findViewById(R.id.n95_view);
        surgeon = view.findViewById(R.id.surgeon_view);
        ventilator = view.findViewById(R.id.ventilator_view);
        clothe = view.findViewById(R.id.clothe_view);
        glasses = view.findViewById(R.id.glasses_view);
        alcohol = view.findViewById(R.id.alcohol_view);
        pants = view.findViewById(R.id.pants_view);
    }


    public void updateView(Supplies supplies) {
        n95.setText(supplies.getN95());
        surgeon.setText(supplies.getSurgeon());
        ventilator.setText(supplies.getVentilator());
        clothe.setText(supplies.getClothe());
        glasses.setText(supplies.getGlasses());
        alcohol.setText(supplies.getAlcohol());
        pants.setText(supplies.getPants());

    }


}
