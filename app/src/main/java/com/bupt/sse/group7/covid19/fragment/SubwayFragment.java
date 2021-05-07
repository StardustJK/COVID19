package com.bupt.sse.group7.covid19.fragment;

import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.bupt.sse.group7.covid19.EditTrackActivity;

public class SubwayFragment extends BusBaseFragment {
    @Override
    void bindEvents() {
        SubwayFragment _this = this;
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchField.getText().toString();
                ((EditTrackActivity) getActivity()).setBusFragment(_this);
                ((EditTrackActivity) getActivity()).busService(keyword);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d("lyj", "地铁 " + busLineAdapter.getUid() + " " + startAdapter.getChosenName() + " " + endAdapter.getChosenName());
                // TODO 完成公交选择，添加到地图
                ((EditTrackActivity) getActivity()).updateBusView(startAdapter.getChosenName(),endAdapter.getChosenName());
                ((EditTrackActivity) getActivity()).closeBusDialog();
                searchField.setText("");

            }
        });
    }
}
