package pers.bwx.sample.jluschoolgis;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;


public class MainTabActivity extends AppCompatActivity {

    //填充不同选择的adapter
    private SectionsPagerAdapter mSectionsPagerAdapter;
    //承载内容的viewPager
    private ViewPager mViewPager;
    //判断是否退出程序标志
    boolean isExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化百度sdk
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main_tab);

        initUI();

    }


    /***
     * 返回键功能：
     * 关闭打开的抽屉
     * 连续按两次退出程序
     */
    @Override
    public void onBackPressed() {
        DrawerLayout offdrawer = (DrawerLayout) findViewById(R.id.dyOffFunc);
        DrawerLayout ondrawer = (DrawerLayout) findViewById(R.id.dyOnFunc);
        //返回键关闭抽屉
        if ((offdrawer.isDrawerOpen(GravityCompat.START)) && !(ondrawer.isDrawerOpen(GravityCompat.START))){
            offdrawer.closeDrawer(GravityCompat.START);
        } else if (!(offdrawer.isDrawerOpen(GravityCompat.START)) &&(ondrawer.isDrawerOpen(GravityCompat.START))) {
            ondrawer.closeDrawer(GravityCompat.START);
        } else {
            //两次返回键退出应用程序
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //2秒中后将判断标志重新设置为false
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                System.exit(0);
            }
        }

    }


    /***
     * 重设判退出程序的判断标志
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }

    };


    /***
     * 切换界面的adapter类
     * 根据选择返回离线与在线的fragment
     * 界面总数
     * 界面标题
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    return new OffLineMap();
                case 1:
                    return new OnLineMap();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "本地地图";
                case 1:
                    return "网络地图";
            }
            return null;
        }
    }

    /***
     * 初始化界面
     * 包括SectionPagerAdapter
     * ViewPage
     * Tablayout
     */

    private void initUI(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("JLUGIS");
        //初始化
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
