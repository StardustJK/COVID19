package com.bupt.sse.group7.covid19.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bupt.sse.group7.covid19.EditTrackActivity;
import com.bupt.sse.group7.covid19.PatientMainPageActivity;
import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.BusTrack;
import com.bupt.sse.group7.covid19.model.Patient;
import com.bupt.sse.group7.covid19.model.TrackPoint;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;
import com.bupt.sse.group7.covid19.presenter.TrackAreaPresenter;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.DrawMarker;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.bupt.sse.group7.covid19.utils.overlayutil.BusLineOverlay;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 病人主页 -> 轨迹卡片部分 -> 地图显示
 */
public class PatientTrackFragment extends Fragment {
    private static final String TAG = "PatientTrackFragment";
    private View view;

    String mp_id;
    MapView mapView;
    BaiduMap baiduMap;
    BitmapDescriptor bitmap;
    private DrawMarker drawMarker;
    private List<JsonArray> tracklist = new ArrayList<>();
    //定位
    private GeoCoder mCoder;
    private final float mZoom = 15.0f;
    private ImageView locationIv;
    private LatLng initialLoc;

    //公交
    private List<String> allBusStations = new ArrayList<>();
    private BusLineSearch mBusLineSearch;
    private BusLineResult mBusLineResult;
    private String city;

    private Context context = getActivity();

    private PatientPresenter patientPresenter;
    private Patient patient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());

        return inflater.inflate(R.layout.fragment_patient_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void initView() {
        mapView = view.findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        locationIv = view.findViewById(R.id.locationIv);
        locationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "locationTvOnClicked");
                LatLng latLng = new LatLng(initialLoc.latitude, initialLoc.longitude);
                Log.i(TAG, "longitude:" + initialLoc.latitude + "   lantitude:" + initialLoc.longitude);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(mZoom);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });

        //marker图标
        drawMarker = new DrawMarker(baiduMap, getActivity().getApplicationContext());
        initLocation();

        patientPresenter = PatientPresenter.getInstance();
        patient = patientPresenter.getPatient();


        initBusTrack();

        List<TrackPoint> trackPoints = patient.getTrackPoints();


        drawMarker.drawAllWithNumber(trackPoints);


        locate();


    }

    //取其中一个点作为定位
    private void locate() {
        List<TrackPoint> trackPoints = patient.getTrackPoints();

        if (trackPoints == null || trackPoints.size() == 0)
            return;

        TrackPoint trackPoint = trackPoints.get(0);
        city = trackPoint.getCity();
        String district = trackPoint.getDistrict();
        String address = "";
        TrackAreaPresenter areaPresenter = TrackAreaPresenter.getInstance();
        if (areaPresenter.getPList(getResources().getXml(R.xml.cities)) != null) {
            //TODO 这里是数字转换成城市名???
//            city = areaPresenter.cNameMap.get(city).getName();
//            district = areaPresenter.dNameMap.get(district).getName();
            address = city + district + trackPoint.getLocation();
        }

        Log.i(TAG, "city:" + city + "address" + address);

        mCoder.geocode(new GeoCodeOption()
                .city(city)
                .address(address));
    }


    //TODO

    private void initBusTrack() {
        Map<String, String> args = new HashMap<>();
        args.put("userId", patient.getId());
        Call<ResponseBody> data = DBConnector.dao.executeGet("track/busTrack", args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "成功获取busline:" + response.body());

                try {
                    String dataString = JsonUtils.inputStream2String(response.body().byteStream());
                    JsonObject rawData = (JsonObject) JsonParser.parseString(dataString);
                    if (rawData.get("success").getAsBoolean()) {
                        JsonArray busTracks = rawData.getAsJsonArray("data");
                        if (busTracks.size() == 0) {
                            return;
                        }
                        ((PatientMainPageActivity) getActivity()).busTrackLayout.setVisibility(View.VISIBLE);
                        TextView busTrackTv = ((PatientMainPageActivity) getActivity()).busTrackTv;
                        String busTrackText = "该患者于：\n";
                        for (int i = 0; i < busTracks.size(); i++) {
                            JsonObject jsonObject = busTracks.get(i).getAsJsonObject();
                            BusTrack busTrack = new BusTrack(
                                    jsonObject.get("id").getAsString(),
                                    jsonObject.get("userId").getAsString(),
                                    jsonObject.get("name").getAsString(),
                                    jsonObject.get("start").getAsString(),
                                    jsonObject.get("end").getAsString(),
                                    jsonObject.get("dateTime").getAsString()
                            );

                            //画公交线路
                            searchBusOrSubway(busTrack);
                            busTrackText += busTrack.getDateTime().substring(5, 10) + "在" + busTrack.getStart() + "乘坐" + busTrack.getName()
                                    + "至" + busTrack.getEnd() + "\n";
                        }
                        busTrackTv.setText(busTrackText);


                    } else return;
                } catch (IOException e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "获取busline失败");
                Toast.makeText(getActivity(), "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void drawBusTrack(BusTrack busTrack) {
        BusLineOverlay overlay = new BusLineOverlay(baiduMap);
        overlay.setData(getChosenStations(busTrack.getStart(), busTrack.getEnd(), mBusLineResult));
        overlay.addToMap();
        overlay.zoomToSpan();

    }

    public void searchBusOrSubway(BusTrack busTrack) {
        mBusLineSearch = BusLineSearch.newInstance();
        //获取的是具体的公交线
        mBusLineSearch.setOnGetBusLineSearchResultListener(new OnGetBusLineSearchResultListener() {
            @Override
            public void onGetBusLineResult(BusLineResult busLineResult) {
                mBusLineResult = busLineResult;
                if (busLineResult == null || busLineResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Log.i(TAG, "onGetBusLineResult : error");
                    return;
                }
                Log.i(TAG, "onGetBusLineResult");
                allBusStations.clear();

                for (BusLineResult.BusStation busStation : busLineResult.getStations()) {
                    allBusStations.add(busStation.getTitle());
                }
                drawBusTrack(busTrack);

            }
        });
        //TODO 需要获取正确的线路uid来测试
        mBusLineSearch.searchBusLine(new BusLineSearchOption()
                .city(city)
                .uid(busTrack.getId()));


    }

    public BusLineResult getChosenStations(String start, String end, BusLineResult busLineResult) {
        BusLineResult mBusLineResult = busLineResult;
        int indexStart = 0;
        int indexEnd = allBusStations.size();
        for (int i = 0; i < allBusStations.size(); i++) {
            if (start.equals(allBusStations.get(i))) {
                indexStart = i;
            }
            if (end.equals(allBusStations.get(i))) {
                indexEnd = i;
            }
        }
        if (indexStart > indexEnd) {
            int temp = indexStart;
            indexStart = indexEnd;
            indexEnd = temp;
        }

        List<BusLineResult.BusStation> busStations = busLineResult.getStations().subList(indexStart, indexEnd + 1);
        List<BusLineResult.BusStep> busSteps = getChosenSteps(busStations, busLineResult.getSteps().get(0));
        mBusLineResult.setStations(busStations);
        mBusLineResult.setSteps(busSteps);

        return mBusLineResult;
    }

    private List<BusLineResult.BusStep> getChosenSteps(List<BusLineResult.BusStation> busStations, BusLineResult.BusStep busStep) {
        if (busStations == null) {
            return null;
        }
        List<LatLng> wayPoints = busStep.getWayPoints();
        LatLng start = busStations.get(0).getLocation();
        LatLng end = busStations.get(busStations.size() - 1).getLocation();
        int indexS = 0, indexE = wayPoints.size() - 1;
        double width = 50;
        for (int i = 0; i < wayPoints.size(); i++) {
            double dis = DistanceUtil.getDistance(start, wayPoints.get(i));
            if (dis < width) {
                indexS = i;
                break;
            }
        }
        for (int i = wayPoints.size() - 1; i >= 0; i--) {
            double dis = DistanceUtil.getDistance(end, wayPoints.get(i));
            if (dis < width) {
                indexE = i;
                break;
            }
        }
        List<BusLineResult.BusStep> busSteps = new ArrayList<>();
        busStep.setWayPoints(wayPoints.subList(indexS, indexE + 1));
        busSteps.add(busStep);
        return busSteps;

    }


    //初始化定位
    private void initLocation() {
        mCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (null != geoCodeResult && null != geoCodeResult.getLocation()) {
                    if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        //没有检索到结果
                        Log.i(TAG, "onGetGeoCodeResult没有检索到结果");
                        return;
                    } else {
                        //定位到选择的区域
                        double latitude = geoCodeResult.getLocation().latitude;
                        double longitude = geoCodeResult.getLocation().longitude;
                        initialLoc = new LatLng(latitude, longitude);
                        Log.i(TAG, "onGetGeoCodeResult:latitude " + latitude + " longitude: " + longitude);
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(initialLoc).zoom(mZoom);
                        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                }

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }

        };
        mCoder.setOnGetGeoCodeResultListener(listener);
    }

    //get all track by p_id
    private Thread getTrackInfo(String p_id) {
        final Map<String, String> args = new HashMap<>();
        args.put("p_id", p_id);
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        tracklist.add(DBConnector.getPatientTrackById(args));
                    }
                }
        );
        thread.start();
        return thread;
    }


    public void setMp_id(String mp_id) {
        this.mp_id = mp_id;
    }
}
