package pers.bwx.sample.jluschoolgis;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;
import java.util.HashMap;

public class Route extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;

    //起终点图片数组
    private int pointImg[];
    //起终点文本数组
    String[] pointText;
    //起终点listView
    private ListView pointLv;

    //起点的sharedPreference
    SharedPreferences shpreferences;
    //声明sharedPreference的editor
    SharedPreferences.Editor shpreEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化百度sdk
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_route);

        pointImg = new int[]{R.mipmap.ic_st,R.mipmap.ic_en};
        pointText = new String[]{"我的位置","输入终点"};

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.route_mode);
        tabLayout.setupWithViewPager(mViewPager);

        pointLv = (ListView) findViewById(R.id.lv_setPoint);


        setPointText();
        //设置起终点文本框
        initPointLv();

        pointLv.setOnItemClickListener(new MyPointItemClickListerner());



    }


    //返回上一个应用
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainTabActivity.class));
    }



    //设置起终点文本
    public void setPointText(){
        Intent setIntent = getIntent();

        if (setIntent.getStringExtra("StartAddress")!= null){
            shpreferences = getPreferences(Activity.MODE_PRIVATE);
            //创建preference的editor对象
            shpreEditor = shpreferences.edit();
            shpreEditor.putString("sa",setIntent.getStringExtra("StartAddress"));
            shpreEditor.commit();
            pointText[0] = setIntent.getStringExtra("StartAddress");

        }
        if(setIntent.getStringExtra("EndAddress")!= null){
            shpreferences = getPreferences(Activity.MODE_PRIVATE);
            pointText[0] = shpreferences.getString("sa","dudulu");
            pointText[1] = setIntent.getStringExtra("EndAddress");
        }
        initPointLv();
    }


    public void initPointLv(){
        //起终点图片文字list
        ArrayList<HashMap<String,Object>> lstImageText = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("pointImg", pointImg[i]);
            map.put("pointText", pointText[i]);
            lstImageText.add(map);
        }

        //地图类型选择adapter
        SimpleAdapter pointAdapter = new SimpleAdapter(getApplicationContext(),lstImageText,
                R.layout.point_item,new String[]{"pointImg","pointText"},
                new int[]{R.id.pointImg,R.id.pointText});
        pointLv.setAdapter(pointAdapter);
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //选择不同的出行方式，返回不同的界面
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if(!(pointText[0].equals("我的位置")) && !(pointText[1].equals("输入终点"))){
                        Fragment blFragment = new BusListFragment();
                        //传递起终点文本
                        Bundle sebundle = new Bundle();
                        sebundle.putString("ST",pointText[0]);
                        sebundle.putString("ED",pointText[1]);
                        blFragment.setArguments(sebundle);
                        return blFragment;
                    }else {
                        return new BusFragmnet();
                    }

                case 1:
                    if(!(pointText[0].equals("我的位置")) && !(pointText[1].equals("输入终点"))){
                        Fragment wrFragment = new WalkResult();
                        //传递起终点文本
                        Bundle sebundle = new Bundle();
                        sebundle.putString("ST",pointText[0]);
                        sebundle.putString("ED",pointText[1]);
                        wrFragment.setArguments(sebundle);
                        return wrFragment;

                    }else {
                        return new WalkFragmnet();
                    }

            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "公交";
                case 1:
                    return "步行";
            }
            return null;
        }
    }


    //选择起点和终点时跳转
    class MyPointItemClickListerner implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent pointIntent = new Intent(getApplicationContext(),SelectPoint.class);
            switch (pointImg[position]){
                case R.mipmap.ic_st:
                    pointIntent.putExtra("point", "我的位置");
                    startActivity(pointIntent);
                    break;
                case R.mipmap.ic_en:
                    pointIntent.putExtra("point","输入终点");
                    startActivity(pointIntent);
                    break;
            }

        }
    }
}
