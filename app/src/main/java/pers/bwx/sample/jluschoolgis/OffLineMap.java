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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bwx on 2017/7/5.
 */

public class OffLineMap extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener{

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
        loadMap(MapPath.chaoyangPath);

        //初始化草图样式与草图编辑器
        initSketch();

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


    /***
     * 离线地图功能
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.nvOffIdentify:
                //识别功能
                IdentifyFeatureLayerTouchListener ml =
                        new IdentifyFeatureLayerTouchListener(getContext(),offMapView);
                offMapView.setOnTouchListener(ml);
                break;
            case R.id.nvOffSketchEditor:
                //草图编辑功能：
                mainSketchEditor.start(SketchCreationMode.POLYGON);
                break;
            case R.id.nvOffIntroduction:
                //校园简介
                Intent toIntroduction = new Intent(getContext(),SchoolIntroduction.class);
                startActivity(toIntroduction);
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


    /***
     * 识别点击屏幕监听者
     */
    private class IdentifyFeatureLayerTouchListener extends DefaultMapViewOnTouchListener {
        Point point = new Point();

        //默认构造函数，确认mapView和需要识别的图层
        public IdentifyFeatureLayerTouchListener(Context context, MapView mapView) {
            super(context, mapView);

        }
        // 点击屏幕
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
           //点击屏幕获取屏幕点
            this.point = new Point((int) e.getX(), (int) e.getY());
            //调用识别方法
            identify(point);

            //设置向上滑动属性表的出现
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
        schoolGv2.setAdapter(saSchool2);
        schoolGv1.setOnItemClickListener(new SchoolSelectListener1());
        schoolGv2.setOnItemClickListener(new SchoolSelectListener2());
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

    public void loadMap(String mapPath){
        try{
            mobileMapPackage = new MobileMapPackage(mapPath);
            mobileMapPackage.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED) {
                        mArcGISMap = mobileMapPackage.getMaps().get(0);
                        offMapView.setMap(mArcGISMap);
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

    public void identify(Point p){

        //使用identifyLayersAsync,识别全图层，传入参数为点击的屏幕点，容错像素，返回值，最大结果数
        final ListenableFuture<List<IdentifyLayerResult>> identifyFuture = offMapView.identifyLayersAsync(
                p, 10, false, 25);

        //添加监听者，执行识别完成的操作
        identifyFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取识别结果
                    List<IdentifyLayerResult> identifyLayersResults = identifyFuture.get();
                    ArrayList<String> featureList = new ArrayList<>();
                    String tableName;

                    //迭代识别结果中的所有图层
                    for (IdentifyLayerResult identifyLayerResult : identifyLayersResults) {

                        //迭代所有结果，并判断是否为控件要素
                        for (GeoElement identifiedElement : identifyLayerResult.getElements()) {
                            if (identifiedElement instanceof Feature) {
                                Feature identifiedFeature = (Feature) identifiedElement;

                                //获取属性表名称，并添加进要素列表中
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
                    Log.e("fuuuuuuuuuu",ex.getMessage());
                }
            }
        });

    }

    /***
     * 初始化草图样式与草图编辑器
     */

    public void initSketch(){
        //初始化草图样式与草图编辑器
        mainSketchEditor = new SketchEditor();
        mainSketchStyle = new SketchStyle();
        mainSketchEditor.setSketchStyle(mainSketchStyle);
        offMapView.setSketchEditor(mainSketchEditor);
    }

    /***
     * 选择校区监听类
     */

    class SchoolSelectListener1 implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (schoolimage1[position]){
                case R.drawable.zhongxin:
                    loadMap(MapPath.zhongxinPath);
                    break;
                case R.drawable.chaoyang:
                    loadMap(MapPath.chaoyangPath);
                    break;
                case R.drawable.xinmin:
                    loadMap(MapPath.xinminPath);
                    break;
            }
        }
    }

    class SchoolSelectListener2 implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (schoolimage2[position]){
                case R.drawable.nanling:
                    loadMap(MapPath.nanlingPath);
                    break;
                case R.drawable.nanhu:
                    loadMap(MapPath.nanhuPath);
                    break;
                case R.drawable.heping:
                    loadMap(MapPath.xinminPath);
                    break;
            }
        }
    }






}
