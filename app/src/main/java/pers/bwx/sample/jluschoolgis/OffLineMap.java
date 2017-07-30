package pers.bwx.sample.jluschoolgis;

import android.os.Bundle;
import android.os.Environment;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bwx on 2017/7/5.
 */

public class OffLineMap extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, AdapterView.OnItemSelectedListener,AdapterView.OnItemClickListener {

    private MapView offMapView;
    private ArcGISMap mArcGISap;
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

    String mmpkPath = Environment.getExternalStorageDirectory()+"/ArcGIS/shuchu.mmpk";
    private MobileMapPackage mobileMapPackage;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        schoolimage1 = new int[]{R.drawable.zhongxin,R.drawable.chaoyang,R.drawable.xinmin};
        schoolimage2 = new int[]{R.drawable.nanling,R.drawable.nanhu,R.drawable.heping};
        schoolname1 = new String[]{"中心校区","朝阳校区","新民校区"};
        schoolname2 = new String[]{"南岭校区","南湖校区","和平校区"};

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //移动地图包
        try {

            offView = inflater.inflate(R.layout.offlinemap_fragment, container, false);
            offMapView = (MapView) offView.findViewById(R.id.arcmapView);
            mobileMapPackage = new MobileMapPackage(mmpkPath);
            mobileMapPackage.loadAsync();
            mobileMapPackage.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED) {
                        mArcGISap = mobileMapPackage.getMaps().get(0);
                        offMapView.setMap(mArcGISap);
                    } else {

                    }
                }
            });
//            mArcGISap  = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16);
//            offMapView.setMap(mArcGISap);

        }catch (Exception e){
            Log.e("fuck",e.toString());
        }


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
            case R.id.nvOffCompute:

                break;
            case R.id.nvOffLoad:
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
                            mArcGISap = mobileMapPackage.getMaps().get(0);
                            offMapView.setMap(mArcGISap);
                        }
                    }
                });
                mobileMapPackage.loadAsync();
                break;
            case R.drawable.xinmin:
                break;
        }
    }
}
