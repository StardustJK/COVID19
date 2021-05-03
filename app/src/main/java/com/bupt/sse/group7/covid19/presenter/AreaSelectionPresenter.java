package com.bupt.sse.group7.covid19.presenter;

import android.content.res.XmlResourceParser;

import com.bupt.sse.group7.covid19.interfaces.IAreaSelectionCallBack;
import com.bupt.sse.group7.covid19.model.Area;
import com.bupt.sse.group7.covid19.model.City;
import com.bupt.sse.group7.covid19.model.District;
import com.bupt.sse.group7.covid19.model.Province;

import java.util.ArrayList;
import java.util.List;

public class AreaSelectionPresenter {
    private Area area;
    private static AreaSelectionPresenter instance = new AreaSelectionPresenter();
    private List<IAreaSelectionCallBack> callBacks = new ArrayList<>();

    private List<Province> pList = null;

    public static AreaSelectionPresenter getInstance() {
        return instance;
    }

    public void setArea(Area area) {
        this.area = area;
        handleAreaChange();
    }

    private void handleAreaChange() {
        for (IAreaSelectionCallBack callBack : callBacks) {
            callBack.onAreaSelected(area);
        }
    }

    public void registerCallBack(IAreaSelectionCallBack callBack) {
        if (callBacks != null && !callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
    }

    public void unregisterCallBack(IAreaSelectionCallBack callBack) {
        if (callBacks != null) {
            callBacks.remove(callBack);
        }
    }

    public List<Province> getPList(XmlResourceParser parser) {
        if (pList == null) {
            provinceParser(parser);
        }
        return this.pList;
    }

    public void provinceParser(XmlResourceParser parser) {
        List<City> cList = null;
        List<District> dList = null;

        Province province = null;
        City city = null;
        District district;

        try {
            int type = parser.getEventType();
            while (type != 1) {
                String tag = parser.getName();
                switch (type) {
                    case XmlResourceParser.START_DOCUMENT:
                        pList = new ArrayList<>();
                        break;
                    case XmlResourceParser.START_TAG:
                        if ("p".equals(tag)) {
                            province = new Province();
                            cList = new ArrayList<>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("p_id".equals(name)) {
                                    province.setId(value);
                                }
                            }
                        }
                        if ("pn".equals(tag)) {
                            province.setName(parser.nextText());
                        }
                        if ("c".equals(tag)) {
                            city = new City();
                            dList = new ArrayList<District>();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("c_id".equals(name)) {
                                    if (value.length() == 3) {
                                        value = "0" + value;
                                    }
                                    city.setId(value);
                                }
                            }
                        }
                        if ("cn".equals(tag)) {
                            city.setName(parser.nextText());
                        }
                        if ("d".equals(tag)) {
                            district = new District();
                            int n = parser.getAttributeCount();
                            for (int i = 0; i < n; i++) {
                                String name = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                if ("d_id".equals(name)) {
                                    district.setId(value);
                                }
                            }
                            district.setName(parser.nextText());
                            dList.add(district);
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        if ("c".equals(tag)) {
                            dList.get(0).setName("全部");
                            city.setDistricts(dList);
                            cList.add(city);
                        }
                        if ("p".equals(tag)) {
                            province.setCities(cList);
                            pList.add(province);
                        }
                        break;
                    default:
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
