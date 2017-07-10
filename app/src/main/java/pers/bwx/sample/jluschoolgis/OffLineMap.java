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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.MapView;

/**
 * Created by bwx on 2017/7/5.
 */

public class OffLineMap extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    private MapView offMapView;
    private ArcGISMap mArcGISap;
    private View offView;
    private FloatingActionButton btnOffFunc;
    private DrawerLayout dyOffFunc;
    private NavigationView nvOffFunc;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        offView = inflater.inflate(R.layout.offlinemap_fragment, container, false);
        offMapView = (MapView) offView.findViewById(R.id.arcmapView);
        String mmpkPath = Environment.getExternalStorageDirectory()+"/ArcGIS/shuchu.mmpk";

        //移动地图包
        final MobileMapPackage mobileMapPackage = new MobileMapPackage(mmpkPath);

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

        btnOffFunc = (FloatingActionButton) offView.findViewById(R.id.btnOffFunction);
        btnOffFunc.setOnClickListener(this);

        dyOffFunc = (DrawerLayout) offView.findViewById(R.id.dyOffFunc);
        dyOffFunc.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        dyOffFunc.addDrawerListener(this);
        nvOffFunc = (NavigationView) offView.findViewById(R.id.nvOffFunc);
        nvOffFunc.setNavigationItemSelectedListener(this);





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
}
