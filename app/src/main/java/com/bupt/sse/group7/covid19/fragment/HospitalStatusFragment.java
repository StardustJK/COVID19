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
import com.bupt.sse.group7.covid19.model.Statistics;

/**
 * 医院主页，确诊情况卡片部分
 */
public class HospitalStatusFragment extends Fragment {
    private View view;


    TextView mild_view, severe_view, cured_view, dead_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hospital_status, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initView();
    }

    private void initView() {
        mild_view = view.findViewById(R.id.mild_view);
        severe_view = view.findViewById(R.id.severe_view);
        cured_view = view.findViewById(R.id.cured_view);
        dead_view = view.findViewById(R.id.dead_view);
    }

    public void updateView(Statistics statistics) {
        mild_view.setText(statistics.getMild());
        severe_view.setText(statistics.getSevere());
        cured_view.setText(statistics.getCured());
        dead_view.setText(statistics.getDead());

    }

}
