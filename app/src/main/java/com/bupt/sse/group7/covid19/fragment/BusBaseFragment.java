package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.adapter.BusLineRvAdapter;
import com.bupt.sse.group7.covid19.adapter.BusStationsRvAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BusBaseFragment extends Fragment {
    private static final String TAG = "BusBaseFragment";
    View view;
    CardView submitBtn;
    BusStationsRvAdapter startAdapter, endAdapter;
    BusLineRvAdapter busLineAdapter;
    RecyclerView.LayoutManager startManager, endManager, busLineManager;
    RecyclerView startView, endView, busLineView;
    EditText searchField;
    CardView searchBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initView();
        bindEvents();
    }

    private void initView() {
        submitBtn = view.findViewById(R.id.bus_confirm);
        searchField = view.findViewById(R.id.bus_keyword);
        searchBtn = view.findViewById(R.id.searchBtn);
        startView = view.findViewById(R.id.bus_start);
        endView = view.findViewById(R.id.bus_end);
        busLineView = view.findViewById(R.id.bus_result);
        startManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        endManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        busLineManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
    }

    // searchBtn, submitBtn事件监听绑定
    abstract void bindEvents();

    void updateEndView(List<String> stationList, BusBaseFragment context) {
        endAdapter = new BusStationsRvAdapter(stationList, context);
        endView.setAdapter(endAdapter);
        endView.setLayoutManager(endManager);
    }

    void updateStartView(List<String> stationList, BusBaseFragment context) {
        startAdapter = new BusStationsRvAdapter(stationList, context);
        startView.setAdapter(startAdapter);
        startView.setLayoutManager(startManager);
    }

    public void updateBusLineView(Map<String, String> busLineList, BusBaseFragment context) {
        Log.i(TAG, busLineList.keySet().toString());

        Map<String, String> busLines = new HashMap<>();
        for(String name : busLineList.keySet()) {
            String simpleName = name.split("\\(")[0];
            if (!busLines.containsKey(name)) {
                busLines.put(simpleName, busLineList.get(name));
            }
        }

        busLineAdapter = new BusLineRvAdapter(busLines, context);
        busLineView.setAdapter(busLineAdapter);
        busLineView.setLayoutManager(busLineManager);
    }

    public void updateStations(List<String> stationList, BusBaseFragment context) {
        updateStartView(stationList, context);
        updateEndView(stationList, context);
    }
}
