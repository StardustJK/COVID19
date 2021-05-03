package com.bupt.sse.group7.covid19.model;

import java.util.List;
import java.util.Map;

public class City extends District {

    private List<District> districts;

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
}
