package com.fanshuff.rma.yourdiary.database;


import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_DATABASE;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * auther: RMA
 * Date: 2021/11/4
 * Time:18:42
 * Des:
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static  DatabaseHelper dbHelper;
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "Diary";

    public static final String CREATE_DIARY = "create table Diary(" +
            //primary key 将id列设为主键    autoincrement表示id列是自增长的
            "id integer primary key autoincrement," +
            "diarytitle," +
            "diaryauthor," +
            "diarycontent," +
            "diaryphoto," +
            "diarytime)";

    private Context mContext;

    public static DatabaseHelper getInstance(Context context) {

        if (dbHelper == null) { //单例模式
            dbHelper = new DatabaseHelper(context);
        }
        return dbHelper;
    }

    //构造方法：第一个参数Context，第二个参数数据库名，第三个参数cursor允许我们在查询数据的时候返回一个自定义的光标位置，一般传入的都是null，第四个参数表示目前库的版本号（用于对库进行升级）
    public  DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory , int version){
        super(context,name ,factory,version);
        mContext = context;
    }

    public DatabaseHelper(Context context){
        super(context, DIARY_DATABASE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //调用SQLiteDatabase中的execSQL（）执行建表语句。
        db.execSQL(CREATE_DIARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

