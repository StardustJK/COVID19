package com.bupt.sse.group7.covid19;

import android.app.Application;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MyApplication extends Application {


    public String province="北京市";
    public LocationClient mLocationClient=null;
    private MyLocationListener myLocationListener=new MyLocationListener();

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myLocationListener);
        initLocation();
        mLocationClient.start();
    }



    public void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setNeedNewVersionRgc(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 获取当前城市
     * @return
     */
    public String getCurrentProvince(){
        return province;
    }

    public class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            province=bdLocation.getProvince();
            mLocationClient.stop();
        }
    }

}
