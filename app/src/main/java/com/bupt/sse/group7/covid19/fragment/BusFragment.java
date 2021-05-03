package com.bupt.sse.group7.covid19.fragment;

import android.util.Log;
import android.view.View;

import com.bupt.sse.group7.covid19.EditTrackActivity;

public class BusFragment extends BusBaseFragment {
    @Override
    void bindEvents() {
        BusFragment _this = this;
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("lyj", "clicked");
                String keyword = searchField.getText().toString();
                ((EditTrackActivity) getActivity()).setBusFragment(_this);
                ((EditTrackActivity) getActivity()).busService(keyword);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("lyj", "公交 " + busLineAdapter.getUid() + " " + startAdapter.getChosenName() + " " + endAdapter.getChosenName());
                // TODO 完成 公交 选择，添加到地图
                ((EditTrackActivity) getActivity()).updateBusView(startAdapter.getChosenName(),endAdapter.getChosenName());
                ((EditTrackActivity) getActivity()).closeBusDialog();
                searchField.setText("");

            }
        });
    }


}
