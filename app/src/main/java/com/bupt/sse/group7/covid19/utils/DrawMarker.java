package com.bupt.sse.group7.covid19.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.TrackPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ContentView;

import static com.baidu.mapapi.map.PolylineDottedLineType.DOTTED_LINE_SQUARE;

public class DrawMarker {
    private List<OverlayOptions> optionsList;//存访标志的集合
    BaiduMap baiduMap;
    BitmapDescriptor geo_bitmap;

    LayoutInflater inflater;
    View view;
    TextView tv_time;
    TextView tv_desc;
    TextView tv_number;
    Context context;

    public DrawMarker(BaiduMap baiduMap, Context context) {
        geo_bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        this.baiduMap = baiduMap;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.marker_item, null);
        tv_time = view.findViewById(R.id.tv_time);
        tv_desc = view.findViewById(R.id.tv_desc);
        this.context = context;
    }

    //展示所有人详细的轨迹
    public void drawAllDetail(List<JsonArray> tracklist) {
        baiduMap.clear();
        if (tracklist == null || tracklist.size() == 0)
            return;
        optionsList = new ArrayList<>();
        for (int j = 0; j < tracklist.size(); j++) {
            JsonArray track = tracklist.get(j);

            Bundle bundle = null;
            List<LatLng> points = new ArrayList<>();

            for (int i = 0; i < track.size(); i++) {

                JsonObject object = track.get(i).getAsJsonObject();
                int id = object.get("p_id").getAsInt();
                double curLng = object.get("longitude").getAsDouble();
                double curLan = object.get("latitude").getAsDouble();
                String date = object.get("date_time").getAsString();
                String descrip = "";
                JsonElement descObj = object.get("description");
                if (!descObj.isJsonNull()) {
                    descrip = descObj.getAsString();
                }

                LatLng currLatLng = new LatLng(curLan, curLng);

                tv_time.setText(date.substring(0, date.length() - 3));
                tv_desc.setText(descrip);

                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromView(view));
                //添加Marker
                OverlayOptions option = new MarkerOptions().position(currLatLng).icon(bitmap);
                Marker marker = (Marker) baiduMap.addOverlay(option);
                marker.setToTop();
                //Marker上绑定id信息用于界面跳转
                bundle = new Bundle();
                bundle.putInt("p_id", id);
                marker.setExtraInfo(bundle);
                points.add(currLatLng);
            }
            // 添加line
            if (points.size() > 1) {
                //dottedLine(true).dottedLineType(DOTTED_LINE_SQUARE)
                OverlayOptions ooPolyline = new PolylineOptions().width(8).dottedLine(true).dottedLineType(DOTTED_LINE_SQUARE).color(0xffff941d).points(points);
                Polyline polyline = (Polyline) baiduMap.addOverlay(ooPolyline);
                polyline.setExtraInfo(bundle);
            }
            points = new ArrayList<>();
        }
        baiduMap.addOverlays(optionsList);
    }

    //展示所有人详细的轨迹,不包括描述
    public void drawAllDetailWithoutDes(List<JsonArray> tracklist) {
        baiduMap.clear();
        if (tracklist == null || tracklist.size() == 0)
            return;
        optionsList = new ArrayList<>();
        for (int j = 0; j < tracklist.size(); j++) {
            JsonArray track = tracklist.get(j);

            Bundle bundle = null;
            List<LatLng> points = new ArrayList<>();

            for (int i = 0; i < track.size(); i++) {

                JsonObject object = track.get(i).getAsJsonObject();
                int id = object.get("p_id").getAsInt();
                double curLng = object.get("longitude").getAsDouble();
                double curLan = object.get("latitude").getAsDouble();


                LatLng currLatLng = new LatLng(curLan, curLng);

                //添加Marker
                OverlayOptions option = new MarkerOptions().position(currLatLng).icon(geo_bitmap);
                Marker marker = (Marker) baiduMap.addOverlay(option);
                marker.setToTop();
                //Marker上绑定id信息用于界面跳转
                bundle = new Bundle();
                bundle.putInt("p_id", id);
                marker.setExtraInfo(bundle);
                points.add(currLatLng);
            }
            //添加line
            if (points.size() > 1) {
                OverlayOptions ooPolyline = new PolylineOptions().width(8).dottedLine(true).dottedLineType(DOTTED_LINE_SQUARE).color(0xffff941d).points(points);
                Polyline polyline = (Polyline) baiduMap.addOverlay(ooPolyline);
                polyline.setExtraInfo(bundle);
            }
            points = new ArrayList<>();
        }
        baiduMap.addOverlays(optionsList);
    }

    //展示所有人的粗略轨迹,不包括描述,多条线
    public void drawAllRoughWithoutDes(List<List<TrackPoint>> tracklist) {
        baiduMap.clear();

        if (tracklist == null || tracklist.size() == 0)
            return;
        optionsList = new ArrayList<>();
        List<Marker> markerList = new ArrayList<>();
        for (int j = 0; j < tracklist.size(); j++) {
            //每个循环是同一个userId
            List<TrackPoint> track = tracklist.get(j);
            if (track.size() == 0)
                continue;
            Bundle bundle = null;
            TrackPoint trackPoint = track.get(0);//获取起点
            String userId = trackPoint.getUserId();

            //添加Marker
            OverlayOptions option = new MarkerOptions().position(trackPoint.getLatLng()).icon(geo_bitmap);
            Marker marker = (Marker) baiduMap.addOverlay(option);
            marker.setToTop();
            //Marker上绑定id信息用于界面跳转
            bundle = new Bundle();
            bundle.putString("userId", userId);
            marker.setExtraInfo(bundle);
        }
        baiduMap.addOverlays(optionsList);
    }

    //展示所有人的粗略轨迹
    public void drawAllRough(List<JsonArray> tracklist) {
        baiduMap.clear();

        if (tracklist == null || tracklist.size() == 0)
            return;
        optionsList = new ArrayList<>();
        List<Marker> markerList = new ArrayList<>();
        for (int j = 0; j < tracklist.size(); j++) {
            JsonArray track = tracklist.get(j);
            if (track.size() == 0)
                continue;
            Bundle bundle = null;
            JsonObject object = track.get(0).getAsJsonObject();//获取起点
            int id = object.get("p_id").getAsInt();
            double curLng = object.get("longitude").getAsDouble();
            double curLan = object.get("latitude").getAsDouble();
            String date = object.get("date_time").getAsString();
            String descrip = "";
            JsonElement descObj = object.get("description");
            if (!descObj.isJsonNull()) {
                descrip = descObj.getAsString();
            }
            LatLng currLatLng = new LatLng(curLan, curLng);
            tv_time.setText(date.substring(0, date.length() - 3));
            tv_desc.setText(descrip);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromView(view));

            //添加Marker
            OverlayOptions option = new MarkerOptions().position(currLatLng).icon(bitmap);
            Marker marker = (Marker) baiduMap.addOverlay(option);
            marker.setToTop();
            //Marker上绑定id信息用于界面跳转
            bundle = new Bundle();
            bundle.putInt("p_id", id);
            marker.setExtraInfo(bundle);
        }
        baiduMap.addOverlays(optionsList);
    }

    //一条线
    public void drawAllWithNumber(List<TrackPoint> trackPoints) {
        baiduMap.clear();
        if (trackPoints == null || trackPoints.size() == 0)
            return;
        optionsList = new ArrayList<>();
        List<LatLng> points = new ArrayList<>();
        Bundle bundle = null;

        for (int i = 0; i < trackPoints.size(); i++) {
            TrackPoint track = trackPoints.get(i);

            String id = track.getUserId();

            //添加Marker
            View numberView = inflater.inflate(R.layout.number_marker, null);
            tv_number = numberView.findViewById(R.id.marker_number);
            tv_number.setText((i + 1) + "");
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromView(numberView));
            OverlayOptions option = new MarkerOptions().position(track.getLatLng()).icon(bitmap);
            Marker marker = (Marker) baiduMap.addOverlay(option);
            marker.setToTop();
            //Marker上绑定id信息用于界面跳转
            bundle = new Bundle();
            bundle.putString("p_id", id);
            marker.setExtraInfo(bundle);
            points.add(track.getLatLng());
        }
        //添加line
        if(points.size()>1)

        {
            OverlayOptions ooPolyline = new PolylineOptions().width(8).dottedLine(true).dottedLineType(DOTTED_LINE_SQUARE).color(0xffff941d).points(points);
            Polyline polyline = (Polyline) baiduMap.addOverlay(ooPolyline);
            polyline.setExtraInfo(bundle);
        }

        points =new ArrayList<>();
        baiduMap.addOverlays(optionsList);
    }



    private Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }
}
