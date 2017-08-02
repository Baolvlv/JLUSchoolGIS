package pers.bwx.sample.jluschoolgis;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bwx on 2017/7/5.
 */

public class OffLineMap extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, AdapterView.OnItemSelectedListener,AdapterView.OnItemClickListener {

    private MapView offMapView;
    private ArcGISMap mArcGISMap;
    private View offView;
    private FloatingActionButton btnOffFunc;
    private DrawerLayout dyOffFunc;
    private NavigationView nvOffFunc;

    //校区选择图片数组
    private int schoolimage1[];
    private int schoolimage2[];
    //校区名数组
    private String schoolname1[];
    private String schoolname2[];
    //校区选择网格
    private GridView schoolGv1;
    private GridView schoolGv2;

    //上滑列表布局
    SlidingUpPanelLayout supLayout;


    String mmpkPath = Environment.getExternalStorageDirectory()+"/ArcGIS/shuchu.mmpk";
    private MobileMapPackage mobileMapPackage;

    //草图编辑器
    SketchEditor mainSketchEditor;
    //草图样式
    SketchStyle mainSketchStyle;

    //属性列表
    ListView featureListView;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTextAndImage();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        offView = inflater.inflate(R.layout.new_offlinemap, container, false);
        supLayout = (SlidingUpPanelLayout) offView.findViewById(R.id.sliding_layout);
        offMapView = (MapView) offView.findViewById(R.id.arcmapView);
        featureListView = (ListView) offView.findViewById(R.id.feature_list);

        //初始化layout和button
        initViewAndButton();
        //初始化GridView
        initGridView();
        //加载地图
        loadMap();




        return offView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //打开功能抽屉
    @Override
    public void onClick(View v) {
        DrawerLayout drawer = (DrawerLayout) offView.findViewById(R.id.dyOffFunc);
        drawer.openDrawer(GravityCompat.START);

    }




    //离线地图功能
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.nvOffIdentify:
                //识别功能
                IdentifyFeatureLayerTouchListener ml =
                        new IdentifyFeatureLayerTouchListener(getContext(),offMapView);
                offMapView.setOnTouchListener(ml);
                Log.e("oooooooooooooooooooooo",ml.point.toString());


                break;
            case R.id.nvOffSketchEditor:
                //草图编辑功能：
                mainSketchEditor.start(SketchCreationMode.POLYGON);
                break;
            case R.id.nvOffSave:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) offView.findViewById(R.id.dyOffFunc);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {}

    @Override
    public void onDrawerOpened(View drawerView) {
        //侧边栏打开时不锁定
        dyOffFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        //侧边栏关闭时锁定
        dyOffFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onDrawerStateChanged(int newState) {}




    //选择不同校区时加载地图
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == schoolGv1){
            switch (position){
                case R.drawable.zhongxin:
                    break;
                case R.drawable.chaoyang:
                    break;
                case R.drawable.xinmin:
                    break;
            }
        }else if(parent == schoolGv2){
                switch (position){
                    case R.drawable.nanling:
                        break;
                    case R.drawable.nanhu:
                        break;
                    case R.drawable.heping:
                        break;
                }
            }
        }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    //点击不同校区时，加载图片

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (schoolimage1[position]){
            case R.drawable.zhongxin:
                break;
            case R.drawable.chaoyang:

                mobileMapPackage.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        if(mobileMapPackage.getLoadStatus() == LoadStatus.LOADED){
                            mArcGISMap = mobileMapPackage.getMaps().get(0);
                            offMapView.setMap(mArcGISMap);
                        }
                    }
                });
                mobileMapPackage.loadAsync();
                break;
            case R.drawable.xinmin:
                break;
        }
    }



    private class IdentifyFeatureLayerTouchListener extends DefaultMapViewOnTouchListener {
        Point point = new Point();

        //默认构造函数，确认mapView和需要识别的图层
        public IdentifyFeatureLayerTouchListener(Context context, MapView mapView) {
            super(context, mapView);

        }

        // 点击屏幕
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // get the screen point where user tapped
            this.point = new Point((int) e.getX(), (int) e.getY());
            Log.e("777777777777777",point.toString());
            // ...

            identufy(point);

            //设置向上滑动属性表出现
            supLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return true;
        }
    }


    /***
     * 初始化layout和button
     */
    public void initViewAndButton(){
        btnOffFunc = (FloatingActionButton) offView.findViewById(R.id.btnOffFunction);
        btnOffFunc.setOnClickListener(this);

        dyOffFunc = (DrawerLayout) offView.findViewById(R.id.dyOffFunc);
        dyOffFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        dyOffFunc.addDrawerListener(this);
        nvOffFunc = (NavigationView) offView.findViewById(R.id.nvOffFunc);
        //查找到gridView
        schoolGv1 = (GridView) nvOffFunc.getHeaderView(0).findViewById(R.id.schoolAreaGView1);
        schoolGv2 = (GridView) nvOffFunc.getHeaderView(0).findViewById(R.id.schoolAreaGView2);
        nvOffFunc.setNavigationItemSelectedListener(this);

    }

    /***
     * 初始化校区选择的gridView
     */

    public void initGridView(){

        //校区图片文字list
        ArrayList<HashMap<String,Object>> lstImageName1 = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("schoolImage1", schoolimage1[i]);
            map.put("schoolName1", schoolname1[i]);
            lstImageName1.add(map);
        }

        ArrayList<HashMap<String,Object>> lstImageName2 = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("schoolImage2", schoolimage2[i]);
            map.put("schoolName2", schoolname2[i]);
            lstImageName2.add(map);
        }

        //校区选择adapter
        SimpleAdapter saSchool1 = new SimpleAdapter(getContext(),lstImageName1,
                R.layout.header_item,new String[]{"schoolImage1","schoolName1"},
                new int[]{R.id.itemImage,R.id.itemText});
        SimpleAdapter saSchool2 = new SimpleAdapter(getContext(),lstImageName2,
                R.layout.header_item,new String[]{"schoolImage2","schoolName2"},
                new int[]{R.id.itemImage,R.id.itemText} );

        schoolGv1.setAdapter(saSchool1);
        schoolGv1.setOnItemSelectedListener(this);
        schoolGv2.setAdapter(saSchool2);
        schoolGv2.setOnItemSelectedListener(this);
    }

    /***
     * 初始化校区选择图片与文本
     */

    public void initTextAndImage(){
        schoolimage1 = new int[]{R.drawable.zhongxin,R.drawable.chaoyang,R.drawable.xinmin};
        schoolimage2 = new int[]{R.drawable.nanling,R.drawable.nanhu,R.drawable.heping};
        schoolname1 = new String[]{"中心校区","朝阳校区","新民校区"};
        schoolname2 = new String[]{"南岭校区","南湖校区","和平校区"};
    }

    /***
     * 加载地图
     */

    public void loadMap(){
        try{
            mobileMapPackage = new MobileMapPackage(mmpkPath);
            mobileMapPackage.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED) {
                        mArcGISMap = mobileMapPackage.getMaps().get(0);
                        offMapView.setMap(mArcGISMap);
                        //初始化草图样式与草图编辑器
                        mainSketchEditor = new SketchEditor();
                        mainSketchStyle = new SketchStyle();
                        mainSketchEditor.setSketchStyle(mainSketchStyle);
                        offMapView.setSketchEditor(mainSketchEditor);
                    }
                }
            });

            mobileMapPackage.loadAsync();

        }catch (Exception e){
            Log.e("fuck",e.toString());
        }
    }

    /***
     * 查询所有的要素图层
     */

    public void identufy(Point p){

        // call identifyLayersAsync, passing in the screen point, tolerance, return types, and maximum results, but no layer
        final ListenableFuture<List<IdentifyLayerResult>> identifyFuture = offMapView.identifyLayersAsync(
                p, 10, false, 25);

        // add a listener to the future
        identifyFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // get the identify results from the future - returns when the operation is complete
                    List<IdentifyLayerResult> identifyLayersResults = identifyFuture.get();
                    ArrayList<String> featureList = new ArrayList<>();
                    String tableName;

                    // iterate all the layers in the identify result
                    for (IdentifyLayerResult identifyLayerResult : identifyLayersResults) {

                        // iterate each result in each identified layer, and check for Feature results
                        for (GeoElement identifiedElement : identifyLayerResult.getElements()) {
                            if (identifiedElement instanceof Feature) {
                                Feature identifiedFeature = (Feature) identifiedElement;

                                // Use feature as required, for example access attributes or geometry, select, build a table, etc...
                                //processIdentifyFeatureResult(identifiedFeature, identifyLayerResult.getLayerContent());

                                Log.e("result:",identifiedFeature.getFeatureTable().getTableName());
                                tableName = identifiedFeature.getFeatureTable().getTableName();

                                featureList.add(tableName);
                            }
                        }
                    }
                    ArrayAdapter featureAdapter = new ArrayAdapter(getContext(),
                            R.layout.single_text_item,R.id.single_text,featureList);
                    featureListView.setAdapter(featureAdapter);
                } catch (InterruptedException | ExecutionException ex) {
                    // must deal with exceptions thrown from the async identify operation
                    Log.e("fuuuuuuuuuu",ex.getMessage());
                }
            }
        });

    }



}
