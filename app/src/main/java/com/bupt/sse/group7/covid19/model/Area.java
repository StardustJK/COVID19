package com.bupt.sse.group7.covid19.model;

public class Area {
    private Province province;
    private City city;
    private District district;

    public Area(Province province, City city, District district) {
        this.province = province;
        this.city = city;
        this.district = district;
    }

    public String getAreaId() {
        if (city.getDistricts().get(0) == district) {
            return city.getId();
        }
        return district.getId();
    }

}
