package com.bupt.sse.group7.covid19;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bupt.sse.group7.covid19.fragment.BusBaseFragment;
import com.bupt.sse.group7.covid19.fragment.BusFragment;
import com.bupt.sse.group7.covid19.fragment.SubwayFragment;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;
import com.bupt.sse.group7.covid19.presenter.TrackAreaPresenter;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.overlayutil.BusLineOverlay;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.baidu.mapapi.map.PolylineDottedLineType.DOTTED_LINE_SQUARE;

/**
 * 病人打点页面
 * TODO 重复添加信息bug
 */
public class EditTrackActivity extends AppCompatActivity implements OnGetGeoCoderResultListener, OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener {
    //marker图标
    BitmapDescriptor bitmap;
    private Context mContext = this;
    private static final String TAG = "EditTrackActivity";
    String p_id = CurrentUser.getCurrentUser().getUserId();
    //int p_id=5;
    private MapView mapView;
    private BaiduMap baiduMap;
    LatLng currLatLng;
    Marker currMarker;


    private Button btn_cancel;
    //所有记录了的Marker
    private List<MyMarker> allMarkers = new ArrayList<>();
    private MyMarker curMyMarker = null;
    //时间选择
    private DatePicker datePickerStart;
    private TimePicker timePickerStart;
    private AlertDialog date_time_picker;
    private AlertDialog bus_picker;
    private CardView btn_confirmTime, btn_edit, btn_bus;
    List<String> datelist = new ArrayList<>();
    private BusBaseFragment fragment;

    //将坐标转换为地址
    private GeoCoder geoCoder;


    //输入描述
    private String description;
    private EditText et_des;
    private AlertDialog desDialog;
    //线条
    private Overlay LineOption;

    //定位
    private ImageView locationIv;
    private BDLocation mCurrentLoc;
    private boolean isFirstLoc = true;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    private final float mZoom = 15.0f;

    //公交
    private Map<String, String> busLines;
    private Map<String, String> subwayLines;
    private BusLineSearch mBusLineSearch;
    //获取到的所有的公交站
    private List<String> allBusStations = new ArrayList<>();
    private String curCity;
    private boolean isFirstCityLoc = true;
    private BusLineResult mBusLineResult;
    private String busLineSelected;
    private String busKeyword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_edit_track);
        //返回

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        initView();
        initLocation();
        initMap();


        // 公交
        AlertDialog.Builder busBuilder = new AlertDialog.Builder(this);
        View busView = View.inflate(this, R.layout.dialog_bus, null);
        busBuilder.setView(busView);
        bus_picker = busBuilder.create();

        //时间选择
        AlertDialog.Builder timeBuilder = new AlertDialog.Builder(this);
        View timeView = View.inflate(this, R.layout.dialog_date_time, null);
        datePickerStart = timeView.findViewById(R.id.date_picker);
        timePickerStart = timeView.findViewById(R.id.time_picker);
        timeBuilder.setView(timeView);
        timePickerStart.setIs24HourView(true);
        hideYear(datePickerStart);
        date_time_picker = timeBuilder.setCancelable(false).create();


        //description输入框
        AlertDialog.Builder desbuilder = new AlertDialog.Builder(this);
        View desview = View.inflate(this, R.layout.dialog_description, null);
        et_des = desview.findViewById(R.id.et_des);
        desbuilder.setView(desview);
        desDialog = desbuilder.setTitle("请输入相关描述")
                .setCancelable(false)
                .setPositiveButton("完成",
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                MyMarker myMarker = new MyMarker();

                                description = et_des.getText().toString();
                                Log.i("hcccc", "输入的description" + description);
                                myMarker.setDescription(description);
                                et_des.setText("");

                                //date
                                String date = datePickerStart.getYear() + "-";
                                if (datePickerStart.getMonth() + 1 < 10) date += "0";
                                date += (datePickerStart.getMonth() + 1) + "-";
                                if (datePickerStart.getDayOfMonth() < 10) date += "0";
                                date += datePickerStart.getDayOfMonth();

                                //time
                                date += " ";
                                if (timePickerStart.getHour() < 10) date += "0";
                                date += timePickerStart.getHour() + ":";
                                if (timePickerStart.getMinute() < 10) date += "0";
                                date += timePickerStart.getMinute() + ":00";
                                myMarker.setDate(date);

                                //is_confirmed=true;
                                myMarker.setRecord(true);
                                myMarker.setMarker(currMarker);
                                myMarker.setLocation(currLatLng);
                                btn_edit.setCardBackgroundColor(getResources().getColor(R.color.darkGrey));

                                OverlayOptions textOptions = new TextOptions()
                                        //                    .bgColor(0xAAFFFF00)
                                        .fontSize(36)
                                        .fontColor(Color.BLACK)
                                        .text(myMarker.getDate() + " " + myMarker.getDescription())
                                        .position(myMarker.getLocation());

                                myMarker.setTextOverlay(baiduMap.addOverlay(textOptions));

                                curMyMarker = myMarker;
                                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(currLatLng));

                                allMarkers.add(myMarker);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    drawLines();
                                }
                            }
                        }).create();

        btn_confirmTime = timeView.findViewById(R.id.btn_confirmTime);
        btn_confirmTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.i("hcccccc", "选择的时间是：" + (datePickerStart.getMonth() + 1) + "-" + datePickerStart.getDayOfMonth()
                        + " " + timePickerStart.getHour() + ":" + timePickerStart.getMinute());
                date_time_picker.dismiss();
                desDialog.show();
            }
        });

        //TODO 点不上了？！
        //点击marker进行删除和编辑
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker thismarker) {
                boolean flag = false;
                for (MyMarker myMarker : allMarkers) {
                    if (myMarker.marker == thismarker) {
                        flag = true;
                    }
                }
                if (!flag) {
                    return true;
                }
                Log.i(TAG, "onMarkerClick");
                View view = View.inflate(mContext, R.layout.window_marker_click, null);
                Button edit = view.findViewById(R.id.btn_edit);
                Button dele = view.findViewById(R.id.btn_dele);
                final InfoWindow mInfoWindow = new InfoWindow(view, thismarker.getPosition(), 100);
                baiduMap.showInfoWindow(mInfoWindow);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG, "edit");
                        editMarker();

                    }
                });
                dele.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        baiduMap.hideInfoWindow();
                        Log.i(TAG, "dele");
                        int index = allMarkers.size();
                        for (int i = 0; i < allMarkers.size(); i++) {
                            if (allMarkers.get(i).marker == thismarker) {
                                index = i;
                                break;
                            }
                        }
                        if (index < allMarkers.size()) {

                            MyMarker deleMarker = allMarkers.remove(index);
                            deleMarker.marker.remove();
                            deleMarker.textOverlay.remove();

                        }

                        drawLines();


                    }
                });
                return true;
            }
        });

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            //点击地图标点
            @Override
            public void onMapClick(LatLng latLng) {
                baiduMap.hideInfoWindow();
                currLatLng = latLng;
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                Log.i("hccc", latitude + "," + longitude);
                if (curMyMarker != null && !curMyMarker.isRecord()) {
                    curMyMarker.getMarker().remove();
                }

                curMyMarker = new MyMarker();
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(bitmap);
                currMarker = (Marker) baiduMap.addOverlay(markerOptions);
                currMarker.setToTop();
                curMyMarker.setMarker(currMarker);
                curMyMarker.setRecord(false);
                btn_edit.setCardBackgroundColor(getResources().getColor(R.color.cardLightBlue));
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });

        //确认一个点
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curMyMarker==null||currMarker==null||curMyMarker.isRecord()){
                    return;
                }
                editMarker();
            }
        });


        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
//        mHandler = new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                //处理消息
//                Bundle bundle = msg.getData();
//                districtList.put(currMarker.hashCode(),bundle.getString("district"));
//                addressList.put(currMarker.hashCode(),bundle.getString("address"));
//                return true;
//            }
//        });


    }

    //画线和描述
    private void drawLines() {
        if (LineOption != null) {
            LineOption.remove();

        }

        List<LatLng> points = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getSortedLocation(allMarkers);
        }

        for (int i = 0; i < allMarkers.size(); i++) {
            points.add(allMarkers.get(i).getLocation());
        }

        if (points.size() > 1) {
            OverlayOptions ooPolyline = new PolylineOptions().width(8).dottedLine(true).dottedLineType(DOTTED_LINE_SQUARE).color(0xffff941d).points(points);
            LineOption = baiduMap.addOverlay(ooPolyline);
        }
    }

    private void initMap() {
        mapView = findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);

    }

    private void initView() {
        locationIv = findViewById(R.id.locationIv);
        //确认单个marker
        btn_edit = findViewById(R.id.btn_confirm);
        btn_edit.setCardBackgroundColor(getResources().getColor(R.color.darkGrey));
        btn_bus = findViewById(R.id.bus_button);
        btn_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Window dialogWindow = bus_picker.getWindow();
                dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
                bus_picker.show();
                bus_picker.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                bus_picker.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });
    }

    private void initLocation() {
        //定位参数
        locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        myLocationListener = new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
        //初始化定位
        locationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "locationTvOnClicked");
                LatLng latLng = new LatLng(mCurrentLoc.getLatitude(), mCurrentLoc.getLongitude());
                Log.i(TAG, "longitude:" + mCurrentLoc.getLongitude() + "   lantitude:" + mCurrentLoc.getLatitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(mZoom);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                //完成打点并提交到数据库
                submit();
                Intent intent=new Intent(EditTrackActivity.this,PatientMainPageActivity.class);
                PatientPresenter.getInstance().setPatientId(CurrentUser.getCurrentUser().getUserId());
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //提交到数据库
    private void submit() {
        JsonArray jsonArray = new JsonArray();

        if (allMarkers.size() == 0)
            return;
        for (int i = 0; i < allMarkers.size(); i++) {
            JsonObject info = new JsonObject();
            MyMarker myMarker = allMarkers.get(i);
            info.add("dateTime", new JsonPrimitive(myMarker.getDate()));
            info.add("longitude", new JsonPrimitive(myMarker.getLocation().longitude));
            info.add("latitude", new JsonPrimitive(myMarker.getLocation().latitude));
            info.add("location", new JsonPrimitive(myMarker.getAddress()));
            info.add("district", new JsonPrimitive(myMarker.getDistrict()));
            info.add("userId", new JsonPrimitive(p_id));
            info.add("description", new JsonPrimitive(myMarker.getDescription()));
            info.add("city", new JsonPrimitive(myMarker.getCity()));
            jsonArray.add(info);
        }
        addData(jsonArray);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<LatLng> getSortedLocation(List<MyMarker> markers) {
        markers.sort(Comparator.comparing(MyMarker::getDate));
        List<LatLng> result = new ArrayList<>();
        for (MyMarker marker : markers) {
            result.add(marker.getLocation());
        }
        return result;
    }

    public void closeBusDialog() {
        bus_picker.dismiss();
    }

    private void addData(JsonArray args) {
        Thread thread=new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(args));
                        try {
                            DBConnector.dao.Post("/track/addTrack",body).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void hideYear(DatePicker datePicker) {
        //安卓5.0以上的处理
        int daySpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
        if (daySpinnerId != 0) {
            View daySpinner = datePicker.findViewById(daySpinnerId);
            if (daySpinner != null) {
                daySpinner.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    //通过坐标转换成地理名字
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null) {
            Log.i("hcccc", "reverseGeoCodeResult==null");
            return;

        }
        if (reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.i("hcccc", String.valueOf(reverseGeoCodeResult.error));
            return;
        }
        ReverseGeoCodeResult.AddressComponent component = reverseGeoCodeResult.getAddressDetail();
        if (isFirstCityLoc) {
            curCity = component.city;
            Log.i(TAG, "curCity" + curCity);
            isFirstCityLoc = false;
            return;
        }
        String city = component.city;
        city = city.substring(0, city.length() - 1);
        String address = component.street + component.streetNumber;
        String district = component.district;
        district = district.substring(0, district.length() - 1);
        TrackAreaPresenter areaPresenter = TrackAreaPresenter.getInstance();
        if (areaPresenter.getPList(getResources().getXml(R.xml.cities)) != null) {
            Log.i(TAG,"district"+district);
            district = areaPresenter.dMap.get(district).getId();
            city = areaPresenter.cMap.get(city).getId();
        }
        Log.i("hcccc", "address:" + address);


        curMyMarker.setCity(city);
        curMyMarker.setAddress(address);
        curMyMarker.setDistrict(district);


    }

    //搜索keyword的所有结果
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.i(TAG, "onGetPoiResult error");
            return;
        }
        busLines = new HashMap<>();
        subwayLines = new HashMap<>();
        //遍历所有POI，找到类型为公交线路的POI
        for (PoiInfo poi : poiResult.getAllPoi()) {
            Log.i(TAG, "poi:" + poi.toString());
            if (poi.getPoiDetailInfo() == null) {
                return;
            }
            if (poi.getPoiDetailInfo().getTag().equals("公交线路")) {
                //获取该条公交路线POI的UID
                busLines.put(poi.name, poi.uid);
                //searchBusOrSubway(poi.uid);

            } else if (poi.getPoiDetailInfo().getTag().equals("地铁线路")) {
                subwayLines.put(poi.name, poi.uid);
            }

        }
        Log.i(TAG, "buslines" + busLines.keySet().toString());

        if (fragment instanceof BusFragment) {
            Log.i(TAG, "busfrag");
            fragment.updateBusLineView(busLines, fragment);
        } else if (fragment instanceof SubwayFragment) {
            Log.i(TAG, "subwayF");
            fragment.updateBusLineView(subwayLines, fragment);
        } else {
            Log.i(TAG, "else");
        }

    }


    //根据uid搜索线路的具体信息
    public void searchBusOrSubway(String busLineId) {
        busLineSelected = busLineId;
        Log.i(TAG, "city" + curCity);
        mBusLineSearch.searchBusLine(new BusLineSearchOption()
                .city(curCity)
                .uid(busLineId));
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * 搜索具体的busline返回的结果
     *
     * @param busLineResult
     */
    @Override
    public void onGetBusLineResult(BusLineResult busLineResult) {
        busKeyword=busLineResult.getBusLineName();
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


        fragment.updateStations(allBusStations, fragment);
    }


    public void updateBusView(String startStation, String endStation) {
        BusLineOverlay overlay = new BusLineOverlay(baiduMap);
        overlay.setData(getChosenStations(startStation, endStation, mBusLineResult));
        overlay.addToMap();
        overlay.zoomToSpan();
        saveBusLine(startStation, endStation);
    }

    public void saveBusLine(String startStation, String endStation) {
        JsonObject busLineJO = new JsonObject();
        busLineJO.add("uid", new JsonPrimitive(busLineSelected));
        busLineJO.add("name", new JsonPrimitive(busKeyword));
        busLineJO.add("p_id", new JsonPrimitive(p_id));
        busLineJO.add("start", new JsonPrimitive(startStation));
        busLineJO.add("end", new JsonPrimitive(endStation));
        //TODO 改时间
        busLineJO.add("date_time", new JsonPrimitive("2020-06-09 13:13:13"));

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(busLineJO);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("rows", jsonArray);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
        Log.i(TAG, "update bus data :" + String.valueOf(jsonObject));
        Call<String> call = DBConnector.dao.Post("addBusTrack.php", body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "公交更新成功");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "公交更新失败");
                Toast.makeText(mContext, "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void setBusFragment(BusBaseFragment fragment) {
        this.fragment = fragment;
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            if (isFirstLoc) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.i(TAG, "longitude:" + location.getLongitude() + "   lantitude:" + location.getLatitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(mZoom);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isFirstLoc = false;
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }
            mCurrentLoc = location;
            locationClient.stop();
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                Log.i(TAG, "GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                Log.i(TAG, "网络位置");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                Log.i(TAG, "离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                Log.i(TAG, "服务端网络定位失败,错误代码：" + location.getLocType());
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Log.i(TAG, "网络不通导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Log.i(TAG, "无法获取有效定位依据导致定位失败");
            } else {
                Log.i(TAG, "未知原因，请向百度地图SDK论坛求助，location.getLocType()错误代码：" + location.getLocType());
            }

        }
    }

    public void busService(String keyword) {
        mBusLineSearch = BusLineSearch.newInstance();
        mBusLineSearch.setOnGetBusLineSearchResultListener(this);
        PoiSearch mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .city(curCity)
                .keyword(keyword)
                .scope(2));


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


    public void editMarker() {
        Window dialogWindow = date_time_picker.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        date_time_picker.show();

    }


}

class MyMarker {
    int mid;
    Marker marker;
    Overlay textOverlay;
    String description;
    String date;
    boolean isRecord;
    LatLng location;
    String address;
    String district;
    String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public boolean isRecord() {
        return isRecord;
    }

    public void setRecord(boolean record) {
        isRecord = record;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Overlay getTextOverlay() {
        return textOverlay;
    }

    public void setTextOverlay(Overlay textOverlay) {
        this.textOverlay = textOverlay;
    }


}
