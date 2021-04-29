package com.bupt.sse.group7.covid19.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bupt.sse.group7.covid19.fragment.PatientTripQueryFragment;
import com.bupt.sse.group7.covid19.fragment.PatientTripRecordFragment;

public class PatientTripPagerAdapter extends FragmentStateAdapter {

    PatientTripQueryFragment patientTripQueryFragment;
    PatientTripRecordFragment patientTripRecordFragment;

    public PatientTripPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        patientTripRecordFragment = new PatientTripRecordFragment();
        patientTripQueryFragment = new PatientTripQueryFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = patientTripQueryFragment;
                break;
            case 1:
                fragment = patientTripRecordFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
