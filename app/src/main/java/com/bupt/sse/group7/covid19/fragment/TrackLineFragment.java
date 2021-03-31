package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.adapter.TrackLineAdapter;
import com.bupt.sse.group7.covid19.model.Patient;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;
import com.google.gson.JsonArray;

/**
 * 病人主页 -> 轨迹卡片 -> 文字轴
 */
public class TrackLineFragment extends Fragment {

    private RecyclerView trackView;
    private TrackLineAdapter adapter;
    private LinearLayoutManager layoutManager;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track_line, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        PatientPresenter patientPresenter=PatientPresenter.getInstance();
        Patient patient=patientPresenter.getPatient();
        trackView = view.findViewById(R.id.track_line_view);
        layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new TrackLineAdapter(patient.getTrackPoints(), this.getContext());

        trackView.setLayoutManager(layoutManager);
        trackView.setAdapter(adapter);
    }


}
