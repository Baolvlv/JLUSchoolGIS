package pers.bwx.sample.jluschoolgis;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectPoint extends AppCompatActivity {

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

    String hintText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_point);

        myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);
        //获取传递的intent
        pointIntent = getIntent();
        hintText = pointIntent.getStringExtra("point");

        //设置向上一级actionbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        selectPTypeImg = new int[]{R.mipmap.ic_sp_map,R.mipmap.ic_collection};
        selectPTypeText = new String[]{"地图选点","收藏选点"};
        selectPGV = (GridView) findViewById(R.id.selectPGirdView);

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
        selectPGV.setOnItemClickListener(new selectTypeListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_point,menu);
        setSearchView(menu);
        return true;
    }

    private void setSearchView(Menu menu) {
        MenuItem item = menu.getItem(0);
        pointSv = new SearchView(this);
        //设置展开后图标的样式,false时ICON在搜索框外,true为在搜索框内，无法修改
        pointSv.setIconifiedByDefault(false);
        pointSv.setQueryHint(hintText);
        item.setActionView(pointSv);
    }

    class selectTypeListener implements AdapterView.OnItemClickListener{

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

}
