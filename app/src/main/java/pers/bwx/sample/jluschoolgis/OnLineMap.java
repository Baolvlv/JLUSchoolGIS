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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.AreaUtil;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.radius;
import static android.R.id.edit;
import static android.content.Context.INPUT_METHOD_SERVICE;

/**在线地图主fragment
 * 基于百度Android 地图 SDK v4.3.2
 * 包含基本地图显示，主要控件与定位功能
 * Created by bwx on 2017/7/5.
 */

public class OnLineMap extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, BDLocationListener, DrawerLayout.DrawerListener, AdapterView.OnItemClickListener {

    //百度地图与视图
    private MapView onMapView = null;
    private BaiduMap mBaiduMap = null;

    //界面根视图
    private View onView;

    //功能按钮
    private FloatingActionButton btnOnFunc;
    //定位按钮
    private FloatingActionButton btnLocaton;
    //路径按钮
    private FloatingActionButton btnRoute;

    //滑动侧边栏
    private DrawerLayout dyOnFunc;
    private NavigationView nvOnFunc;

    //定位类
    public LocationClient mLocationClicent = null;
    //定位数据
    public MyLocationData locData;

    //当前定位图标
    private BitmapDescriptor mCurrentMarker;

    //定位参数设置
    LocationClientOption option = null;

    //经纬度数据
    public LatLng ll;

    //地图类型图片数组
    private int mapTypeImage[];
    //地图类型名数组
    private String mapTypeName[];
    //选择地图类型格网
    private GridView mapTypeGV;

    //交通图与热力图开关
    Switch trafficSwitch;
    Switch heatSwitch;
    Switch panoramicSwitch;

    //用于测量的marker
    Marker markerA;
    Marker markerB;
    //marker a,b的坐标
    LatLng markerALL;
    LatLng markerBLL;

    //用于提示面积距离信息的泡泡
    TextView popupText;

    //POI检索实例
    PoiSearch mPoiSearch;

    //全景图标
    Marker panMarker;
    //全景图标经纬度
    LatLng pLatLng;

    //用于添加标注的泡泡
    EditText popupEdit;
    //标注泡泡的经纬度
    LatLng peLatLng;
    //用于标注的infowindow
    InfoWindow labelWindow;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //声明locationClient类
        mLocationClicent = new LocationClient(getActivity());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        onView = inflater.inflate(R.layout.onlinemap_fragment, container, false);

        //初始化用户界面
        initUI(onView);

        //设置地图类型选择的gridView
        setGV();

        //开始定位
        startLocate();

        return onView;
    }


    /***
     * 在fragment相关生命周期中
     * 执行mapView与locationClient
     * 相关操作
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mLocationClicent.stop();
        onMapView.onDestroy();
        mPoiSearch.destroy();
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

    /***
     * 初始化定位参数
     */
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


    /***
     * 功能：
     * 打开抽屉，定位，路径
     * @param v
     */
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
                startLocate();
                break;
            case R.id.btnRoute:
                //跳转路径activity
                Intent toRouteIntent = new Intent(getActivity(),Route.class);
                getActivity().startActivity(toRouteIntent);
                break;
        }


    }


    /***
     * 侧边栏功能
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.nvOnMeasureDis:
                //测量距离
                mBaiduMap.setOnMapStatusChangeListener(null);
                mBaiduMap.clear();
                cleanMarker();
                initMeasureMarker();
                mBaiduMap.setOnMarkerDragListener(new DMarkerDragListener());
                break;
            case R.id.nvOnMeasureArea:
                //测量面积
                mBaiduMap.setOnMapStatusChangeListener(null);
                mBaiduMap.clear();
                cleanMarker();
                initMeasureMarker();
                mBaiduMap.setOnMarkerDragListener(new AMarkerDragListener());
                break;
            case R.id.nvOnSetLable:
                //添加标注
                mBaiduMap.setOnMapStatusChangeListener(null);
                initPopupEdit();

                mBaiduMap.showInfoWindow(new InfoWindow(popupEdit, ll, -50));
                mBaiduMap.setOnMapStatusChangeListener(new PopupEditListener());
                break;
            case R.id.nvOnFindFood:
                //周边美食
                mBaiduMap.setOnMapStatusChangeListener(null);
                mBaiduMap.clear();
                cleanMarker();
                searchRestaurant();
                break;
            case R.id.nvFindHotel:
                //周边旅店
                mBaiduMap.setOnMapStatusChangeListener(null);
                mBaiduMap.clear();
                searchHotel();
                break;
        }

        //执行相关功能后关闭侧边栏
        DrawerLayout drawer = (DrawerLayout) onView.findViewById(R.id.dyOnFunc);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    /***
     * 接收定位信息
     * @param bdLocation
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(0).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();

         ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        Log.e("fuuuuuuuul",ll.latitude+"    ,"+ll.longitude);

        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        float maxLevel = mBaiduMap.getMaxZoomLevel();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,maxLevel-3);
        mBaiduMap.animateMapStatus(u);

        Log.e("erro",bdLocation.getLocType()+"");
    }

    /***
     * 发起定位
     */

    public void startLocate(){
        //设置定位图标发起定位
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, false, mCurrentMarker);
        mBaiduMap.setMyLocationConfiguration(config);
        mLocationClicent.registerLocationListener(this);
        initLocation();
        mLocationClicent.start();

    }

    /***
     * 设置地图类型选择gridView
     */

    public void setGV(){
        //地图类型图片数组
        mapTypeImage = new int[]{R.drawable.mapimage2d,R.drawable.mapimage3d,R.drawable.mapweixing};
        //地图类型名称数组
        mapTypeName = new String[]{"2D地图","3D地图","卫星图"};
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
        //点击地图类型监听器
        mapTypeGV.setOnItemClickListener(this);
    }


    /***
     * 初始化界面
     */

    public void initUI(View view){
        onMapView = (MapView) view.findViewById(R.id.bmapView);


        mBaiduMap = onMapView.getMap();

        //添加地图加载完成回调监听
        mBaiduMap.setOnMapLoadedCallback(new MyMapLoadCallback());


        //功能按钮
        btnOnFunc = (FloatingActionButton) view.findViewById(R.id.btnOnFunction);
        btnOnFunc.setOnClickListener(this);

        //定位按钮
        btnLocaton = (FloatingActionButton) view.findViewById(R.id.btnLocation);
        btnLocaton.setOnClickListener(this);

        dyOnFunc = (DrawerLayout) view.findViewById(R.id.dyOnFunc);
        dyOnFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //监听侧边栏
        dyOnFunc.addDrawerListener(this);
        nvOnFunc = (NavigationView) view.findViewById(R.id.nvOnFunc);

        //查找到gridView
        mapTypeGV = (GridView) nvOnFunc.getHeaderView(0).findViewById(R.id.mapTypeGView);

        nvOnFunc.setNavigationItemSelectedListener(this);

        //路径按钮

        btnRoute = (FloatingActionButton) view.findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(this);


        //初始化交通图与热力图开关，设置监听器
        trafficSwitch = (Switch) nvOnFunc.getHeaderView(0).findViewById(R.id.traffic_switch);
        heatSwitch = (Switch) nvOnFunc.getHeaderView(0).findViewById(R.id.heat_switch);
        panoramicSwitch= (Switch) nvOnFunc.getHeaderView(0).findViewById(R.id.panoramic_switch);
        heatSwitch = (Switch) nvOnFunc.getHeaderView(0).findViewById(R.id.heat_switch);
        SwitchChangeListener scl = new SwitchChangeListener();
        trafficSwitch.setOnCheckedChangeListener(scl);
        heatSwitch.setOnCheckedChangeListener(scl);
        panoramicSwitch.setOnCheckedChangeListener(scl);

    }


    /***
     * 设置侧边栏的滑动的状态
     * @param drawerView
     */
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


    /***
     * 选择地图类型
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mapTypeImage[position]){
            case R.drawable.mapimage2d:
                //正常地图类型
                mBaiduMap.setOnMapStatusChangeListener(null);
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mBaiduMap.setMyLocationEnabled(false);
                cleanMarker();

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
                mBaiduMap.setOnMapStatusChangeListener(null);
                mBaiduMap.setMyLocationEnabled(false);
                cleanMarker();
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
                mBaiduMap.setOnMapStatusChangeListener(null);
                cleanMarker();
                mBaiduMap.setMyLocationEnabled(false);
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
        }
    }

    /***
     * 初始化用于测量的marker
     * 清除marker
     */

    //初始化测量marker
    public void initMeasureMarker(){
        BitmapDescriptor bitmapA = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_mark_a);
        BitmapDescriptor bitmapB = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_mark_b);

        markerALL = new LatLng(ll.latitude+0.0005,ll.longitude+0.0005);
        markerBLL = new LatLng(ll.latitude-0.0005,ll.longitude-0.0005);

        OverlayOptions optionA = new MarkerOptions()
                .position(markerALL)  //设置marker的位置
                .icon(bitmapA)  //设置marker图标
                .draggable(true);  //设置手势拖拽
        OverlayOptions optionB = new MarkerOptions()
                .position(markerBLL)  //设置marker的位置
                .icon(bitmapB)  //设置marker图标
                .draggable(true);  //设置手势拖拽

        //将marker添加到地图上
        markerA = (Marker) (mBaiduMap.addOverlay(optionA));
        markerB = (Marker) (mBaiduMap.addOverlay(optionB));
        initPopupText();
    }

    //清除marker
    public void cleanMarker(){
        if(markerA != null && markerB != null){
            markerA.remove();
            markerB.remove();
        }

        mBaiduMap.hideInfoWindow();

    }


    /***
     * 地图加载完成时的回调类，用以设置缩放控件位置
     */
    class MyMapLoadCallback implements BaiduMap.OnMapLoadedCallback{

        @Override
        public void onMapLoaded() {
            //缩放控件位置
            onMapView.setZoomControlsPosition(new Point(950,1200));
        }
    }

    /***
     * 开关转化监听器
     */
    class SwitchChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            switch (id){
                //实施交通图
                case R.id.traffic_switch:
                    mBaiduMap.setOnMapStatusChangeListener(null);
                    if(isChecked){
                        mBaiduMap.setTrafficEnabled(true);
                    }else {
                        mBaiduMap.setTrafficEnabled(false);
                    }
                    break;
                //城市热力图
                case R.id.heat_switch:
                    mBaiduMap.setOnMapStatusChangeListener(null);
                    if(isChecked){
                        mBaiduMap.setBaiduHeatMapEnabled(true);
                    }else {
                        mBaiduMap.setBaiduHeatMapEnabled(false);
                    }
                    break;
                //城市全景图
                case R.id.panoramic_switch:
                    mBaiduMap.setOnMapStatusChangeListener(null);
                    if(isChecked){
                        setPanoramaMarker(ll);
                        mBaiduMap.setOnMapStatusChangeListener(new PanoramaChangeListener());

                    }else {
                        mBaiduMap.clear();
                        //解绑监听者
                        mBaiduMap.setOnMapStatusChangeListener(null);
                    }
                    break;
            }
        }
    }


    /***
     * 测量距离的marker拖拽监听器
     */
    class DMarkerDragListener implements BaiduMap.OnMarkerDragListener{

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            if(marker.equals(markerA)){
                markerALL = marker.getPosition();
            }
            if (marker.equals(markerB)){
                markerBLL = marker.getPosition();
            }

            popupText.setText("A,B两点之间的距离为："
                    +DistanceUtil.getDistance(markerALL,markerBLL)+"米");

            mBaiduMap.showInfoWindow(new InfoWindow(popupText, ll, 0));
        }

        @Override
        public void onMarkerDragStart(Marker marker) {

        }
    }

    /***
     * 测量面积的marker拖拽监听器
     */
    class AMarkerDragListener implements BaiduMap.OnMarkerDragListener{

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            if(marker.equals(markerA)){
                markerALL = marker.getPosition();
            }
            if (marker.equals(markerB)){
                markerBLL = marker.getPosition();
            }

            popupText.setText("以A,B两点为东北西南角的矩形的面积为："
                    +AreaUtil.calculateArea(markerALL,markerBLL)+"平方米");

            mBaiduMap.showInfoWindow(new InfoWindow(popupText, ll, 0));
        }

        @Override
        public void onMarkerDragStart(Marker marker) {

        }
    }


    /***
     * poi检索监听者
     */
    class MyPoiSearchListener implements OnGetPoiSearchResultListener{


        //获取poi检索结果
        @Override
        public void onGetPoiResult(PoiResult poiResult) {


            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                PoiOverlay overlay = new PoiOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(poiResult);
                overlay.addToMap();
                overlay.zoomToSpan();
                showNearbyArea(ll, 200);
                return;
            }

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    }

    /***
     * 检索美食方法
     */
    public void searchRestaurant(){
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new MyPoiSearchListener());
        //采用周边搜索，设置周边搜索参数
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword("美食").sortType(PoiSortType.distance_from_near_to_far)
                .location(ll).radius(200);
        mPoiSearch.searchNearby(nearbySearchOption);

    }

    /***
     * 检索旅店方法
     */

    public void searchHotel(){
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new MyPoiSearchListener());
        //采用周边搜索，设置周边搜索参数
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword("旅店").sortType(PoiSortType.distance_from_near_to_far)
                .location(ll).radius(200);
        mPoiSearch.searchNearby(nearbySearchOption);
    }

    /***
     * 设置全景marker图标
     */
    public void setPanoramaMarker(LatLng latLng){
        Log.e("fuuuuck",latLng.toString());
        BitmapDescriptor pBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);

        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions pOption = new MarkerOptions()
                .position(latLng)
                .icon(pBitmap);
        //在地图上添加Marker，并显示
        panMarker = (Marker) mBaiduMap.addOverlay(pOption);
    }

    /**
     * 对周边检索的范围进行绘制
     * @param center
     * @param radius
     */
    public void showNearbyArea( LatLng center, int radius) {
        BitmapDescriptor centerBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        MarkerOptions ooMarker = new MarkerOptions().position(center).icon(centerBitmap);
        mBaiduMap.addOverlay(ooMarker);

        OverlayOptions ooCircle = new CircleOptions().fillColor(0x00000000)
                .center(center).stroke(new Stroke(10,0xff00ffff))
                .radius(radius);
        mBaiduMap.addOverlay(ooCircle);
    }

    /***
     * 初始化popupText
     */
    public void initPopupText(){
        popupText = new TextView(getContext());
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
    }

    /***
     * 初始化popupEdit
     */

    public void initPopupEdit(){
        popupEdit = new EditText(getContext());
        popupEdit.setBackgroundResource(R.drawable.popup);
        popupEdit.setHintTextColor(0xff00ffff);
        popupEdit.setHint("在此添加标注");
        popupEdit.setSingleLine(true);
        popupEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        popupEdit.setOnEditorActionListener(new PopEditOnKeyListener());
    }




    /***
     * 地图状态改变监听类
     * 用来移动全景marker,获得移动后的坐标
     */
    class PanoramaChangeListener implements BaiduMap.OnMapStatusChangeListener{

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

            panMarker.remove();
            pLatLng = mapStatus.target;
            setPanoramaMarker(pLatLng);

            initPopupText();
            popupText.setText("查看全景");
            //第三个参数为偏移量，用以显示在marker上方
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, pLatLng, -50));
            popupText.setOnClickListener(new ViewPanorama());

        }
    }

    /***
     * 地图状态改变监听类
     * 用来改变popupEdit获得移动后的坐标
     */
    class PopupEditListener implements BaiduMap.OnMapStatusChangeListener{

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

            peLatLng = mapStatus.target;

            initPopupEdit();

            labelWindow = new InfoWindow(popupEdit, peLatLng, -50);


            mBaiduMap.showInfoWindow(labelWindow);


        }
    }

    /***
     * 输入结束监听器
     */

    class PopEditOnKeyListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId==EditorInfo.IME_ACTION_SEND
                    ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                mBaiduMap.hideInfoWindow();
                addLabelOnMap(popupEdit.getText().toString());

            }

            return false;
        }
    }

    /***
     * 向地图上添加文字标注
     */

    public void addLabelOnMap(String text){

        OverlayOptions labelOption = new TextOptions()
                .bgColor(0xAAFFFF00)
                .fontSize(60)
                .fontColor(0xFFFF00FF)
                .text(text)
                .rotate(-30)
                .position(peLatLng);

        mBaiduMap.addOverlay(labelOption);

    }


    /***
     * 查看全景监听器
     */

    class ViewPanorama implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent toPanorama = new Intent(getContext(),PanoramaActivity.class);

            toPanorama.putExtra("latitude",pLatLng.latitude);
            toPanorama.putExtra("longitude",pLatLng.longitude);

            startActivity(toPanorama);

        }
    }









    @Override
    public void onConnectHotSpotMessage(String s, int i) {}

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {}
    @Override
    public void onDrawerStateChanged(int newState) {}


}




