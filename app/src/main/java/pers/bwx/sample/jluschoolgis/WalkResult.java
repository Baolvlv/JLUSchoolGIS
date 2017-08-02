package pers.bwx.sample.jluschoolgis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

/**
 * Created by bwx on 2017/7/25.
 */

public class WalkResult extends Fragment implements OnGetRoutePlanResultListener {

    View rootView;
    MapView mapView;
    BaiduMap map;
    OverlayManager routeOverlay = null;
    //路线搜索实例
    RoutePlanSearch mSearch = null;
    //步行路线结果类
    WalkingRouteResult nowResultwalk = null;
    //路线结果
    RouteLine route = null;
    boolean useDefaultIcon = false;
    //开始结束节点字符串
    String startNodeStr;
    String endNodeStr;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            startNodeStr = getArguments().getString("ST").substring(6);
            endNodeStr = getArguments().getString("ED").substring(6);

        }catch (Exception ex){

        }



    }

    //
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.walk_result,container,false);
        mapView = (MapView) rootView.findViewById(R.id.walkRMapView);
        map = mapView.getMap();
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        //注册路线结果监听者
        mSearch.setOnGetRoutePlanResultListener(this);

        //设置起终点
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("长春", startNodeStr);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("长春", endNodeStr);

        mSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(stNode).to(enNode));

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSearch.destroy();
    }

    //获取步行路线结果
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
           //Toast.makeText(getContext(),"起终点不明确，尝试地图选点",Toast.LENGTH_SHORT).show();
        }
        if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            String st = walkingRouteResult.getSuggestAddrInfo().describeContents() + "";
            //Toast.makeText(getContext(),"起终点不明确，尝试地图选点"+st,Toast.LENGTH_SHORT).show();
        }

//        if (walkingRouteResult.getRouteLines().size() == 1) {
            // 直接显示
        try{
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(map);
            map.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(walkingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }catch (Exception ex){
            Toast.makeText(getContext(),"抱歉，无结果",Toast.LENGTH_SHORT).show();
        }

    }
    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_en);
            }
            return null;
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}





