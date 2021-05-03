package com.bupt.sse.group7.covid19.utils.overlayutil;

import android.graphics.Color;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.bupt.sse.group7.covid19.R;

import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.map.PolylineDottedLineType.DOTTED_LINE_SQUARE;

/**
 * 用于显示一条公交详情结果的Overlay
 */
public class BusLineOverlay extends OverlayManager {
    private final String TAG = "BusLineOverlay";

    private BusLineResult mBusLineResult = null;

    /**
     * 构造函数
     *
     * @param baiduMap 该BusLineOverlay所引用的 BaiduMap 对象
     */
    public BusLineOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    /**
     * 设置公交线数据
     *
     * @param result 公交线路结果数据
     */
    public void setData(BusLineResult result) {
        this.mBusLineResult = result;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {

        if (mBusLineResult == null || mBusLineResult.getStations() == null) {
            return null;
        }
        List<OverlayOptions> overlayOptionses = new ArrayList<OverlayOptions>();
        BusLineResult.BusStation busStationStart = mBusLineResult.getStations().get(0);
        BusLineResult.BusStation busStationEnd = mBusLineResult.getStations().get(mBusLineResult.getStations().size() - 1);


        //TODO 换图标
        overlayOptionses.add(new MarkerOptions()
                .position(busStationStart.getLocation())
                .zIndex(10)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.station)).scaleX(0.3f).scaleY(0.3f));
        //TODO 换图标
        overlayOptionses.add(new MarkerOptions()
                .position(busStationEnd.getLocation())
                .zIndex(10)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.station)).scaleX(0.3f).scaleY(0.3f));

        Log.i(TAG, "station.size" + mBusLineResult.getStations().size());

        List<LatLng> points = new ArrayList<LatLng>();
        Log.i(TAG, "step.size" + mBusLineResult.getSteps().size());

        for (BusLineResult.BusStep step : mBusLineResult.getSteps()) {
            Log.i(TAG, "step.getName()" + step.getName());

            if (step.getWayPoints() != null) {
                points.addAll(step.getWayPoints());
            }
        }
        if (points.size() > 0) {
            overlayOptionses
                    .add(new PolylineOptions()
                            .color(0xff92729e).zIndex(0).width(8).dottedLine(true).dottedLineType(DOTTED_LINE_SQUARE)
                            .points(points));
        }
        return overlayOptionses;
    }

    /**
     * 覆写此方法以改变默认点击行为
     *
     * @param index 被点击的站点在
     *              {@link BusLineResult#getStations()}
     *              中的索引
     * @return 是否处理了该点击事件
     */
    public boolean onBusStationClick(int index) {
        if (mBusLineResult.getStations() != null
                && mBusLineResult.getStations().get(index) != null) {
            Log.i("baidumapsdk", "BusLineOverlay onBusStationClick");
        }
        return false;
    }

    public final boolean onMarkerClick(Marker marker) {
        if (mOverlayList != null && mOverlayList.contains(marker)) {
            return onBusStationClick(mOverlayList.indexOf(marker));
        } else {
            return false;
        }

    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }
}
