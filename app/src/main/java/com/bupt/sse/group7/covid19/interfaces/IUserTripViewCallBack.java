package com.bupt.sse.group7.covid19.interfaces;

import com.bupt.sse.group7.covid19.model.UserTrip;

import java.util.List;

public interface IUserTripViewCallBack {
    void onUserTripInfoReturned(List<UserTrip> tripList);
    void onGetZeroData(String msg);
    void onRisk();

}
