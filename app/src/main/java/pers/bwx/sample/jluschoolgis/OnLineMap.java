package pers.bwx.sample.jluschoolgis;


import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bwx on 2017/7/5.
 */

public class OnLineMap extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, BDLocationListener, DrawerLayout.DrawerListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private MapView onMapView = null;
    private BaiduMap mBaiduMap = null;

    private View onView;

    //功能按钮
    private FloatingActionButton btnOnFunc;
    //定位按钮
    private FloatingActionButton btnLocaton;

    //路径按钮
    private FloatingActionButton btnRoute;

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

    //地图类型图片数组
    private int mapTypeImage[];
    //地图类型名数组
    private String mapTypeName[];
    //选择地图类型格网
    private GridView mapTypeGV;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //声明locationClient类
        mLocationClicent = new LocationClient(getActivity());

        //地图类型图片数组
        mapTypeImage = new int[]{R.drawable.mapimage2d,R.drawable.mapimage3d,R.drawable.mapweixing};
        //地图类型名称数组
        mapTypeName = new String[]{"2D地图","3D地图","卫星图"};


        //setUserVisibleHint(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        onView = inflater.inflate(R.layout.onlinemap_fragment, container, false);

        onMapView = (MapView) onView.findViewById(R.id.bmapView);


        mBaiduMap = onMapView.getMap();
        //onMapView.showZoomControls(false);

        //添加地图加载完成回调监听
        mBaiduMap.setOnMapLoadedCallback(new MyMapLoadCallback());


        //功能按钮
        btnOnFunc = (FloatingActionButton) onView.findViewById(R.id.btnOnFunction);
        btnOnFunc.setOnClickListener(this);

        //定位按钮
        btnLocaton = (FloatingActionButton) onView.findViewById(R.id.btnLocation);
        btnLocaton.setOnClickListener(this);

        dyOnFunc = (DrawerLayout) onView.findViewById(R.id.dyOnFunc);
        dyOnFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //监听侧边栏
        dyOnFunc.addDrawerListener(this);
        nvOnFunc = (NavigationView) onView.findViewById(R.id.nvOnFunc);

        //查找到gridView
        mapTypeGV = (GridView) nvOnFunc.getHeaderView(0).findViewById(R.id.mapTypeGView);

        nvOnFunc.setNavigationItemSelectedListener(this);

        //路径按钮

        btnRoute = (FloatingActionButton) onView.findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(this);


        //地图类型图片文字list
        ArrayList<HashMap<String,Object>> lstImageName = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("mapTypeImage", mapTypeImage[i]);
            map.put("mapTypeName", mapTypeName[i]);
            lstImageName.add(map);
        }

        //地图类型选择adapter
        SimpleAdapter samapType = new SimpleAdapter(getContext(),lstImageName,
                R.layout.header_item,new String[]{"mapTypeImage","mapTypeName"},
                new int[]{R.id.itemImage,R.id.itemText});
        //设置地图类型gridView的adapter
        mapTypeGV.setAdapter(samapType);
        //设置地图类型选择监听器
        mapTypeGV.setOnItemSelectedListener(this);
        //点击地图类型监听器
        mapTypeGV.setOnItemClickListener(this);



        //开启定位图层
        //mBaiduMap.setMyLocationEnabled(true);


        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);


        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, false, mCurrentMarker);


        mBaiduMap.setMyLocationConfiguration(config);

        //设置定位监听器
        mLocationClicent.registerLocationListener(this);
        //初始化定位设置
        initLocation();
        //开始定位
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


    //功能：定位，路线
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOnFunction:
                //打开抽屉功能
                DrawerLayout drawer = (DrawerLayout) onView.findViewById(R.id.dyOnFunc);
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.btnLocation:
                //发起定位请求
                mBaiduMap.setMyLocationEnabled(true);
                mCurrentMarker = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_gcoding);
                MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, false, mCurrentMarker);
                mBaiduMap.setMyLocationConfiguration(config);
                mLocationClicent.registerLocationListener(this);
                initLocation();
                mLocationClicent.start();
                break;
            case R.id.btnRoute:
                Intent toRouteIntent = new Intent(getActivity(),Route.class);
                getActivity().startActivity(toRouteIntent);
                break;
        }


    }


    //在线地图功能
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.nvOnCompute:

                break;
            case R.id.nvOnLoad:
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
//            // if this view is visible to user, start to identufy user location
//            startRequestLocation();
//        } else if (isVisibleToUser == false) {
//            // if this view is not visible to user, stop to identufy user
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




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //选择地图类型
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mapTypeImage[position]){
            case R.drawable.mapimage2d:
                //正常地图类型
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);;

                //地图状态俯视角0
                MapStatus twoDStatus = new MapStatus.Builder()
                        .overlook(0)
                        .build();
                //地图状态变化对象
                MapStatusUpdate twoDStatusUpdate = MapStatusUpdateFactory.newMapStatus(twoDStatus);
                //改变地图状态
                mBaiduMap.setMapStatus(twoDStatusUpdate);

                break;
            case R.drawable.mapimage3d:
                //地图状态俯视角30
                MapStatus threeDStatus = new MapStatus.Builder()
                        .overlook(-30)
                        .build();
                //地图状态变化对象
                MapStatusUpdate threeDStatusUpdate = MapStatusUpdateFactory.newMapStatus(threeDStatus);
                //改变地图状态
                mBaiduMap.setMapStatus(threeDStatusUpdate);
                break;
            case R.drawable.mapweixing:
                mBaiduMap.setMyLocationEnabled(false);
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
        }
    }

    //地图加载完成时回调
    class MyMapLoadCallback implements BaiduMap.OnMapLoadedCallback{

        @Override
        public void onMapLoaded() {
            //缩放控件位置
            onMapView.setZoomControlsPosition(new Point(950,1200));
        }
    }


}




