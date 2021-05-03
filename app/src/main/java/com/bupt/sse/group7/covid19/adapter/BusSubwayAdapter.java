package com.bupt.sse.group7.covid19.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bupt.sse.group7.covid19.fragment.BusFragment;
import com.bupt.sse.group7.covid19.fragment.SubwayFragment;

public class BusSubwayAdapter extends FragmentStateAdapter {

    public BusSubwayAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new BusFragment();
        } else {
            fragment = new SubwayFragment();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
