package pers.bwx.sample.jluschoolgis;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.baidu.mapapi.BMapManager.getContext;

public class SelectPoint extends AppCompatActivity {

    //搜索条
    private SearchView pointSv;

    //起终点intent
    Intent pointIntent;

    //工具条
    Toolbar myToolBar;

    //选点类型图片
    private int[] selectPTypeImg;
    //选点类型名
    private String[] selectPTypeText;

    //选点类型gridView
    private GridView selectPGV;

    //提示文本
    String hintText;

    //历史位置列表
    private ListView locationList;
    //清除历史记录
    private TextView tvClean;
    //历史记录adapter
    SimpleCursorAdapter adapter;

    //搜索历史数据库
    HistoryDB hdb;
    SQLiteDatabase hdbRead;
    SQLiteDatabase hdbWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_point);

        //初始化UI
        initUI();

        //获取传递的intent
        pointIntent = getIntent();
        hintText = pointIntent.getStringExtra("point");

        //初始化选点类型
        initSelectPointType();

        //初始化数据库
        initDB();

        //设置历史列表
        setHistoryList();


    }

    /***
     * 初始化界面
     */

    public void initUI(){
        myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        //设置向上一级actionbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        selectPTypeImg = new int[]{R.mipmap.ic_sp_map,R.mipmap.ic_collection};
        selectPTypeText = new String[]{"地图选点","收藏选点"};
        selectPGV = (GridView) findViewById(R.id.selectPGirdView);
    }

    /***
     * 初始化数据库
     */
    public void initDB(){
        //初始化数据库
        hdb = new HistoryDB(getApplicationContext());
        hdbRead = hdb.getReadableDatabase();
        hdbWrite = hdb.getWritableDatabase();
    }

    /***
     * 设置历史列表
     */
    public void setHistoryList(){
        //历史位置列表
        locationList = (ListView) findViewById(R.id.list_location3);
        tvClean = (TextView) findViewById(R.id.tv_clean3);
        tvClean.setOnClickListener(new SCleanListener());

        Cursor c = hdbRead.query("record",
                new String[]{"_id as _id","location"}
                ,null,null,null,null,null);

        adapter = new SimpleCursorAdapter(getContext(),R.layout.single_text_item
                ,c,new String[]{"location"},new int[]{R.id.single_text});
        locationList.setAdapter(adapter);
    }

    /***
     * 解析searchView
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_point,menu);
        setSearchView(menu);
        return true;
    }


    /***
     * 设置searchView
     * @param menu
     */
    private void setSearchView(Menu menu) {
        MenuItem item = menu.getItem(0);
        pointSv = new SearchView(this);
        //设置展开后图标的样式,false时ICON在搜索框外,true为在搜索框内，无法修改
        pointSv.setIconifiedByDefault(false);
        pointSv.setQueryHint(hintText);
        //设置搜索文本变化监听器
        pointSv.setOnQueryTextListener(new MyQueryTextListern());
        item.setActionView(pointSv);
    }


    /****
     * 初始阿虎选点类型
     */
    private void initSelectPointType(){

        ArrayList<HashMap<String,Object>> spImageText = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("spImg", selectPTypeImg[i]);
            map.put("spText", selectPTypeText[i]);
            spImageText.add(map);
        }
        SimpleAdapter pointAdapter = new SimpleAdapter(getApplicationContext(),spImageText,
                R.layout.point_item,new String[]{"spImg","spText"},
                new int[]{R.id.pointImg,R.id.pointText});
        selectPGV.setAdapter(pointAdapter);
        selectPGV.setOnItemClickListener(new SelectTypeListener());

    }


    /***
     * 选点类型监听器
     */
    class SelectTypeListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (selectPTypeImg[position]){
                case R.mipmap.ic_sp_map:
                    Intent spInMapintent = new Intent(getApplicationContext(),
                            SelectPInMap.class);

                    if(hintText.equals("我的位置")){
                        spInMapintent.putExtra("s|e","start");
                        startActivity(spInMapintent);
                    }else if(hintText.equals("输入终点")) {
                        spInMapintent.putExtra("s|e","end");
                        startActivity(spInMapintent);
                    }
                    break;
                case R.mipmap.ic_collection:
                    break;
            }
        }
    }

    /***
     * 搜索文本监听器
     */

    class MyQueryTextListern implements SearchView.OnQueryTextListener{

        //提交搜索文本时
        @Override
        public boolean onQueryTextSubmit(String query) {
            Intent setSearchIntent = new Intent(getApplicationContext(),
                    Route.class);
            if(hintText.equals("我的位置")){
                setSearchIntent.putExtra("StartAddress",query);
                startActivity(setSearchIntent);
            }else if(hintText.equals("输入终点")) {
                setSearchIntent.putExtra("EndAddress",query);
                startActivity(setSearchIntent);
            }

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }


    /***
     * 刷新历史列表
     */
    public void refershListView(){
        //更改adapter的Cursor更新列表
        Cursor c = hdbRead.query("record",
                new String[]{"_id as _id","location"}
                ,null,null,null,null,null);
        adapter.changeCursor(c);


    }


    /***
     * 清除历史记录
     */

    class SCleanListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //弹出对话框
            new AlertDialog.Builder(getContext())
                    .setMessage("您确定要清空历史记录吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            hdbWrite.execSQL("delete from record");
                            refershListView();
                        }
                    }).setNegativeButton("取消",null).show();
        }
    }


}
