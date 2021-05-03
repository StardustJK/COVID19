package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.adapter.BusSubwayAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class BusCollectionFragment extends Fragment {
    private BusSubwayAdapter busSubwayAdapter;
    private ViewPager2 viewPager;
    private List<String> busTabLabels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        busSubwayAdapter = new BusSubwayAdapter(this);
        viewPager = view.findViewById(R.id.bus_subway_content);
        viewPager.setAdapter(busSubwayAdapter);

        busTabLabels = new ArrayList<>();
        busTabLabels.add("公交");
        busTabLabels.add("地铁");

        TabLayout tabs = view.findViewById(R.id.bus_subway_topbar);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(busTabLabels.get(position))).attach();
    }
}
