package com.bupt.sse.group7.covid19.interfaces;

import com.bupt.sse.group7.covid19.model.Hospital;

public interface IHospitalViewCallBack {
    void onHospitalInfoReturned(Hospital hospital);
    void onGetDataFailed();
}
