package com.bupt.sse.group7.covid19.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bupt.sse.group7.covid19.fragment.RegisterFragment;
import com.bupt.sse.group7.covid19.fragment.PatientAuthFragment;

/**
 * 认证页面面，根据tab栏选中的位置确认下方显示的内容是个人认证登录还是医院认证登录
 */
public class AuthPagerAdapter extends FragmentStateAdapter {

    public AuthPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new PatientAuthFragment();
        } else {
            fragment = new RegisterFragment();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
