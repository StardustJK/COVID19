package com.bupt.sse.group7.covid19.model;

import java.util.List;
import java.util.Map;

public class Province extends District {

    private List<City> cities;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

}
