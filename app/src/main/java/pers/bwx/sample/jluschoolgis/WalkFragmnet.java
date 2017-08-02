package pers.bwx.sample.jluschoolgis;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by bwx on 2017/7/22.
 */

public class WalkFragmnet extends Fragment {

    //根布局
    private View walkView;
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

        //初始化数据库
        hdb = new HistoryDB(getContext());
        hdbRead = hdb.getReadableDatabase();
        hdbWrite = hdb.getWritableDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        walkView  =inflater.inflate(R.layout.walk_fragmnet,container,false);

        //历史位置列表
        locationList = (ListView) walkView.findViewById(R.id.list_location2);
        tvClean = (TextView) walkView.findViewById(R.id.tv_clean2);
        tvClean.setOnClickListener(new WCleanListener());

        Cursor c = hdbRead.query("record",
                new String[]{"_id as _id","location"}
                ,null,null,null,null,null);

        adapter = new SimpleCursorAdapter(getContext(),R.layout.single_text_item
                ,c,new String[]{"location"},new int[]{R.id.single_text});
        locationList.setAdapter(adapter);

        return walkView;
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

    class WCleanListener implements View.OnClickListener{

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
