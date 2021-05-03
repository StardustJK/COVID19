package com.bupt.sse.group7.covid19.interfaces;

import com.bupt.sse.group7.covid19.model.HospitalBrief;

import java.util.List;

public interface IHospitalListCallBack {
    void onHospitalListChanged(List<HospitalBrief> hospitalList);
    void onGetDataFailed();
}
