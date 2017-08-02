package pers.bwx.sample.jluschoolgis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 搜索历史数据库
 * Created by bwx on 2017/8/1.
 */

public class HistoryDB extends SQLiteOpenHelper{

   //数据库名称
    private static final String DATABASE_NAME = "history.db";

    //构造函数
    public HistoryDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表，包含location一列，文本类型，默认值为空  PRIMARY KEY AUTOINCREMENT主键自增
        db.execSQL("CREATE TABLE record(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "location TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
