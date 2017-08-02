package pers.bwx.sample.jluschoolgis;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bwx on 2017/7/22.
 */

public class BusFragmnet extends Fragment {

    private View busView;

    //公交功能图片数组
    private int busImage[];
    //公交功能名数组
    private String busName[];
    //公交选择格网
    private GridView busGV;
    //历史位置列表
    private ListView locationList;
    //清除历史jilu
    private TextView tvClean;
    //历史记录adapter
    SimpleCursorAdapter adapter;

    //搜索历史数据库
    HistoryDB hdb;
    SQLiteDatabase hdbRead;
    SQLiteDatabase hdbWrite;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        busImage = new int[]{R.mipmap.ic_bus,R.mipmap.ic_subway};
        busName =new String[]{"查公交","地铁图"};

        //初始化数据库
        hdb = new HistoryDB(getContext());
        hdbRead = hdb.getReadableDatabase();
        hdbWrite = hdb.getWritableDatabase();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        busView = inflater.inflate(R.layout.bus_fragment,container,false);

        busGV = (GridView) busView.findViewById(R.id.busGirdView);

        //地图类型图片文字list
        ArrayList<HashMap<String,Object>> lstImageName = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("busImage", busImage[i]);
            map.put("busName", busName[i]);
            lstImageName.add(map);
        }

        //地图类型选择adapter
        SimpleAdapter busAdapter = new SimpleAdapter(getContext(),lstImageName,
                R.layout.route_item,new String[]{"busImage","busName"},
                new int[]{R.id.itemIcon,R.id.itemText});
        //设置地图类型gridView的adapter
        busGV.setAdapter(busAdapter);


        //历史位置列表
        locationList = (ListView) busView.findViewById(R.id.list_location);
        tvClean = (TextView) busView.findViewById(R.id.tv_clean);
        tvClean.setOnClickListener(new CleanListener());

        //注意：第一个参数一定要为"_id as _id"！！！！！
        Cursor c = hdbRead.query("record",
                new String[]{"_id as _id","location"}
                ,null,null,null,null,null);
        if(c.moveToNext()){
            Log.e("dddddddddd","false");
        }


         adapter = new SimpleCursorAdapter(getContext(),R.layout.single_text_item
                ,c,new String[]{"location"},new int[]{R.id.single_text});
        locationList.setAdapter(adapter);


        return busView;
    }


    //刷新历史记录列表
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

    class CleanListener implements View.OnClickListener{

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
