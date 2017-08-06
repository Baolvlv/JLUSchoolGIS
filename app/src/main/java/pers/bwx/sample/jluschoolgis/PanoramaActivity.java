package pers.bwx.sample.jluschoolgis;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.lbsapi.panoramaview.*;
import com.baidu.lbsapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;

public class PanoramaActivity extends AppCompatActivity {

    private PanoramaView mPanoView;
    private double lon;
    private double lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       MyMapApplication app = (MyMapApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);
            app.mBMapManager.init(new MyMapApplication.MyGeneralListener());
        }

        setContentView(R.layout.activity_panorama);

        Intent llIntent = getIntent();
        lon = llIntent.getDoubleExtra("longitude",0);
        lat = llIntent.getDoubleExtra("latitude",0);

        mPanoView = (PanoramaView) findViewById(R.id.my_panorama);
        mPanoView.setPanorama(lon, lat);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPanoView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPanoView.onResume();
    }

    @Override
    protected void onDestroy() {
        mPanoView.destroy();
        super.onDestroy();
    }
}
