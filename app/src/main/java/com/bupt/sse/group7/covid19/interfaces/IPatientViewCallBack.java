package com.bupt.sse.group7.covid19.interfaces;

import com.bupt.sse.group7.covid19.model.Patient;

public interface IPatientViewCallBack {
    void onPatientInfoReturned(Patient patient);
    void onGetDataFailed();
}
