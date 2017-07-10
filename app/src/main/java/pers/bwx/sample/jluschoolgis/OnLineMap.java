package pers.bwx.sample.jluschoolgis;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by bwx on 2017/7/5.
 */

public class OnLineMap extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, BDLocationListener, DrawerLayout.DrawerListener {

    private MapView onMapView = null;
    private BaiduMap mBaiduMap = null;

    private View onView;

    private FloatingActionButton btnOnFunc;
    private DrawerLayout dyOnFunc;
    private NavigationView nvOnFunc;

    //定位类
    public LocationClient mLocationClicent = null;
    //定位数据
    public MyLocationData locData;

    //当前定位图标
    private BitmapDescriptor mCurrentMarker;



    //定位设置
    LocationClientOption option = null;

    public LatLng ll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //声明locationClient类
        mLocationClicent = new LocationClient(getActivity());


        //setUserVisibleHint(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        onView = inflater.inflate(R.layout.onlinemap_fragment, container, false);

        onMapView = (MapView) onView.findViewById(R.id.bmapView);

        mBaiduMap = onMapView.getMap();

        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);



        btnOnFunc = (FloatingActionButton) onView.findViewById(R.id.btnOnFunction);
        btnOnFunc.setOnClickListener(this);

        dyOnFunc = (DrawerLayout) onView.findViewById(R.id.dyOnFunc);
        dyOnFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //监听侧边栏
        dyOnFunc.addDrawerListener(this);
        nvOnFunc = (NavigationView) onView.findViewById(R.id.nvOnFunc);

        nvOnFunc.setNavigationItemSelectedListener(this);



        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);


        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);


        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, false, mCurrentMarker);


        mBaiduMap.setMyLocationConfiguration(config);

        mLocationClicent.registerLocationListener(this);
        initLocation();
        mLocationClicent.start();




        return onView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mLocationClicent.stop();
        onMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        onMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationClicent.stop();
        onMapView.onPause();
    }

    //初始化定位参数
    private void initLocation(){
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

//        int span=1000;
//        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        //option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        //option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClicent.setLocOption(option);
    }


    //打开抽屉功能
    @Override
    public void onClick(View v) {

        DrawerLayout drawer = (DrawerLayout) onView.findViewById(R.id.dyOnFunc);
        drawer.openDrawer(GravityCompat.START);
    }


    //在线地图功能
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.nvOffCompute:

                break;
            case R.id.nvOffLoad:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) onView.findViewById(R.id.dyOnFunc);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {        //核心方法，避免因Fragment跳转导致地图崩溃
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser == true) {
//            // if this view is visible to user, start to request user location
//            startRequestLocation();
//        } else if (isVisibleToUser == false) {
//            // if this view is not visible to user, stop to request user
//            // location
//            stopRequestLocation();
//        }
//    }


    long startTime;
    long costTime;
    private void stopRequestLocation() {
        if (mLocationClicent != null) {
            mLocationClicent.unRegisterLocationListener(this);
            mLocationClicent.stop();
        }
    }
    private void startRequestLocation() {
        // this nullpoint check is necessary
        if (mLocationClicent == null) {
            mLocationClicent.registerLocationListener(this);
            mLocationClicent.start();
            mLocationClicent.requestLocation();
            startTime = System.currentTimeMillis();
        }
    }

    //接收定位信息
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(0).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();

         ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        float maxLevel = mBaiduMap.getMaxZoomLevel();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,maxLevel-3);
        mBaiduMap.animateMapStatus(u);

        Log.e("erro",bdLocation.getLocType()+"");
    }


    @Override
    public void onDrawerOpened(View drawerView) {
        //侧边栏打开时不锁定
        dyOnFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        //侧边栏关闭时锁定
        dyOnFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {}

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {}
    @Override
    public void onDrawerStateChanged(int newState) {}


}



