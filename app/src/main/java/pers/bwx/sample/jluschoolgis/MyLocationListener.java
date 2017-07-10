package pers.bwx.sample.jluschoolgis;



import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MyLocationData;

/**
 * Created by bwx on 2017/7/5.
 */

public class MyLocationListener implements BDLocationListener {

    public Double mLatitude;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

//        OnLineMap.locData = new MyLocationData.Builder()
//                .accuracy(bdLocation.getRadius())
//                // 此处设置开发者获取到的方向信息，顺时针0-360
//                .direction(100).latitude(bdLocation.getLatitude())
//                .longitude(bdLocation.getLongitude()).build();
        bdLocation.getLatitude();


    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
}
