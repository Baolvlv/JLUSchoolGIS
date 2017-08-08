package pers.bwx.sample.jluschoolgis;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class SelectPInMap extends AppCompatActivity {

    Toolbar myToolBar;

    MapView mapView;
    BaiduMap map;
    LocationClient mLocClient;
    LocationClientOption locOption;
    //当前经纬度
    LatLng ll;
    //定位数据
    MyLocationData locData;
    //起终点intent判断
    String startOrEnd;
    Intent seIntent;

    //地图marker
    Marker marker;

    //地理编码对象
    GeoCoder geoCoder;
    //地理反编码选项
    ReverseGeoCodeOption rgcOption;
    //详细地址
    String myAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化百度sdk
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_select_pin_map);

        //获取intent
        seIntent = getIntent();

        //初始化界面
        initUI();

        //初始化地图
        initMap();



    }

    /***
     * 生命周期相关的地图操作
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocClient.stop();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocClient.stop();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /***
     * 初始化ui
     * ToolBar
     * 返回上级的actionBar
     */
    public void initUI(){

        myToolBar = (Toolbar) findViewById(R.id.spToolbar);
        myToolBar.setTitle("");
        setSupportActionBar(myToolBar);

        //返回上级actionbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /***
     * 初始化地图
     * 定位
     * 状态改变监听器
     */

    public void initMap(){

        //地图定位
        mLocClient = new LocationClient(getApplicationContext());
        mapView = (MapView) findViewById(R.id.selectPMapView);
        map = mapView.getMap();
        MyLocListener mll = new MyLocListener();
        mLocClient.registerLocationListener(mll);
        initLocation();
        mLocClient.start();
        map.setOnMapStatusChangeListener(new MyMapStatusChageListerner());


    }

    /***
     * 初始化定位参数
     */
    private void initLocation(){
        locOption = new LocationClientOption();
        locOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        locOption.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        locOption.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        locOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        locOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        locOption.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        locOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        locOption.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        //设置定位选项
        mLocClient.setLocOption(locOption);
    }


    /***
     * 定位监听者
     */
    class MyLocListener implements BDLocationListener{
        LatLng mLat;

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(0).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();

            ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());


            // 设置定位数据
            map.setMyLocationData(locData);
            float maxLevel = map.getMaxZoomLevel();
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,maxLevel-3);
            map.animateMapStatus(u);

            //判断起点与终点，并设置marker图标
            if(seIntent.getStringExtra("s|e").equals("start")){
                startOrEnd = "s";
                setMarker(R.mipmap.ic_st);
            }else if (seIntent.getStringExtra("s|e").equals("end")){

                startOrEnd = "e";
                setMarker(R.mipmap.ic_en);
            }

            Log.e("sp",bdLocation.getLocType()+"");

        }



        @Override
        public void onConnectHotSpotMessage(String s, int i) {}

    }


    /***
     * 地理编码监听器
     * 收到反地理编码后
     * 提示具体地址信息
     */

    class MyGeoCoderListener implements OnGetGeoCoderResultListener {

        //获取地理编码结果
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        //获取地理反编码结果
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            myAddress = reverseGeoCodeResult.getAddress();
            Snackbar addSanc =  Snackbar.make(mapView, myAddress, Snackbar.LENGTH_LONG);
            //设置停留时间
            addSanc.setDuration(5000);
            //按钮颜色
            addSanc.setActionTextColor(getResources().getColor(R.color.lightgreen));
            // 获取 snackbar 视图
            View snackbarView = addSanc.getView();

            // 改变 snackbar 文本颜色
            int snackbarTextId = android.support.design.R.id.snackbar_text;
            TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
            textView.setTextColor(getResources().getColor(R.color.black));

            // 改变 snackbar 背景
            snackbarView.setBackgroundColor(getResources().getColor(R.color.white));



            addSanc.setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent reIntent = new Intent(getApplicationContext(),
                                    Route.class);
                            if(startOrEnd.equals("s")){
                                reIntent.putExtra("StartAddress",myAddress);
                                startActivity(reIntent);
                            }else if(startOrEnd.equals("e")){
                                reIntent.putExtra("EndAddress",myAddress);
                                startActivity(reIntent);
                            }


                        }
                    }).show();

        }
    }


    /***
     * 地图状态改变监听器
     * 重设marker
     * 请求地理反编码
     */
   class MyMapStatusChageListerner implements BaiduMap.OnMapStatusChangeListener {

       @Override
       public void onMapStatusChangeStart(MapStatus mapStatus) {

       }

       @Override
       public void onMapStatusChange(MapStatus mapStatus) {

       }

       //地图状态改变后
       @Override
       public void onMapStatusChangeFinish(MapStatus mapStatus) {

           //清除上一次marker
           marker.remove();
           ll = mapStatus.target;
          //重设marker
           if(startOrEnd.equals("s")){
               setMarker(R.mipmap.ic_st);
           }else if(startOrEnd.equals("e")) {
               setMarker(R.mipmap.ic_en);
           }

           //实例化地理编码对象
           geoCoder = GeoCoder.newInstance();
           //设置反编码坐标
           rgcOption = new ReverseGeoCodeOption();
           rgcOption.location(ll);
           //发起反编码请求
           geoCoder.reverseGeoCode(rgcOption);
           //设置获取结果监听器
           geoCoder.setOnGetGeoCodeResultListener(new MyGeoCoderListener());
       }
   }

    /***
     * 设置marker图标
     */

    public void setMarker(int resource){
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(resource);
        OverlayOptions options = new MarkerOptions().position(ll).icon(bitmap);
        marker = (Marker) map.addOverlay(options);
    }

}
