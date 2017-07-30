package pers.bwx.sample.jluschoolgis;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bwx on 2017/7/26.
 * 用于显示路线可选择的公交列表
 */

public class BusListFragment extends Fragment implements OnGetRoutePlanResultListener {

    View rootView;

    //OverlayManager routeOverlay = null;
    //路线搜索实例
    RoutePlanSearch mSearch = null;
    //开始结束节点字符串
    String startNodeStr;
    String endNodeStr;

    //公交结果列表
    ListView busList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startNodeStr = getArguments().getString("ST").substring(6);
        endNodeStr = getArguments().getString("ED").substring(6);

        Log.e("1111111",startNodeStr);
        Log.e("222222",endNodeStr);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bus_list_fragmnet,container,false);

        busList = (ListView) rootView.findViewById(R.id.busResultList);

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        //注册路线结果监听者
        mSearch.setOnGetRoutePlanResultListener(this);

        //设置起终点
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("长春", startNodeStr);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("长春", endNodeStr);

        //发起公交检索
        //mSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stNode).to(enNode));
        mSearch.transitSearch((new TransitRoutePlanOption())
                .from(stNode)
                .city("长春")
                .to(enNode));

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSearch.destroy();
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {}

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.e("5555555","无结果");
        }
        if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            Log.e("5555555", transitRouteResult.getSuggestAddrInfo().describeContents() + "");
        }
        busList.setAdapter(getBusListAdapter(transitRouteResult));
    }

    //综合公交线路规划结果
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
       //busList.setAdapter(getBusListAdapter(massTransitRouteResult));

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

    public SimpleAdapter getBusListAdapter(TransitRouteResult result){

        ArrayList<HashMap<String,Object>> busListText = new ArrayList<>();

        for(int i = 0; i < result.getRouteLines().size(); i++){
            TransitRouteLine allLine = result.getRouteLines().get(i);
            HashMap<String,Object> map = new HashMap<>();
            map.put("bus_name", allLine.getAllStep().get(1).getVehicleInfo().getTitle());
            map.put("bus_info", allLine.getDuration()/60 + "分钟 · 步行"
                    +(allLine.getAllStep().get(0).getDistance()+
                    allLine.getAllStep().get(allLine.getAllStep().size()-1).getDistance())+"米");
            busListText.add(map);
        }

        SimpleAdapter busAdapter = new SimpleAdapter(getContext(),busListText,
                R.layout.bus_scheme_item,new String[]{"bus_name","bus_info"},
                new int[]{R.id.busName,R.id.busInfo});
        return busAdapter;

    }
}
