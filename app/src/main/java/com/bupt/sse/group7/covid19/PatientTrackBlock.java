package com.bupt.sse.group7.covid19;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.Map;

public class PatientTrackBlock extends Fragment {
    View view;
    PatientTrackFragment patientTrackFragment;
    TrackLineFragment trackLineFragment;
    JsonArray tracks;
    int id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patient_track_block, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        initData();
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initView() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        patientTrackFragment = new PatientTrackFragment();
        patientTrackFragment.setMp_id(this.id);
        FragmentTransaction trackTranStatus = fragmentManager.beginTransaction();
        trackTranStatus.add(R.id.track_block, patientTrackFragment);
        trackTranStatus.commit();

        trackLineFragment = new TrackLineFragment();
        trackLineFragment.setTracks(this.tracks);
        FragmentTransaction trackLineTran = fragmentManager.beginTransaction();
        trackLineTran.add(R.id.track_block, trackLineFragment);
        trackLineTran.commit();
    }

    public void setId(int id) {
        this.id = id;
    }

    private void initData() {
        Thread thread = getPatientTrack();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Thread getPatientTrack() {
        Map<String, String> args = new HashMap<>();
        args.put("p_id", String.valueOf(this.id));
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        tracks = DBConnector.getPatientTrackById(args);
                    }
                });
        thread.start();
        return thread;
    }


}
