package com.fanshuff.rma.yourdiary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fanshuff.rma.yourdiary.entity.DiaryInfo;

import java.util.ArrayList;
import java.util.List;

public class DBdao {

    private DatabaseHelper databaseHelper;
    private  Context context;
    private SQLiteDatabase db ;

    public DBdao(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    //添加数据
    public void addInformation(String title,String author,String content, byte[] photo,String time){
        db = databaseHelper.getWritableDatabase();
        db.beginTransaction();

// insert into Orders(Id, CustomName, OrderPrice, Country) values (7, "Jne", 700, "China");
        ContentValues contentValues = new ContentValues();

        contentValues.put("diarytitle", title);
        contentValues.put("diaryauthor", author);
        contentValues.put("diarycontent", content);
        contentValues.put("diaryphoto", photo);
        contentValues.put("diarytime", time);
        db.insertOrThrow(databaseHelper.TABLE_NAME, null, contentValues);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //删除数据
    public void deleteInformation(int id){
        db = databaseHelper.getWritableDatabase();
        db.beginTransaction();

// delete from Orders where Id = ?
        db.delete(databaseHelper.TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //修改数据
    public void modifyInformation(int id, String title,String author,String content, byte[] photo,String time){
        db = databaseHelper.getWritableDatabase();
        db.beginTransaction();

// update Orders set OrderPrice = 800 where Id = 6
        ContentValues cv = new ContentValues();

        cv.put("diarytitle", title);
        cv.put("diaryauthor", author);
        cv.put("diarycontent", content);
        cv.put("diaryphoto", photo);
        cv.put("diarytime", time);

        db.update(databaseHelper.TABLE_NAME,
                cv,
                "id = ?",
                new String[]{String.valueOf(id)});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //遍历输出数据库的数列表
    public List<DiaryInfo> getAllPoints() {
        String sql = "select * from Diary";
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        List<DiaryInfo> diaryList = new ArrayList<>();
        DiaryInfo diaryInfo = null;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            diaryInfo = new DiaryInfo();
            int id = cursor.getColumnIndex("id"),
                    title = cursor
                    .getColumnIndex("diarytitle"),
                    author = cursor
                            .getColumnIndex("diaryauthor"),
                    content = cursor
                            .getColumnIndex("diarycontent"),
                    photo = cursor
                            .getColumnIndex("diaryphoto"),
                    time = cursor
                            .getColumnIndex("diarytime");

            diaryInfo.setId(cursor.getInt(id));
            diaryInfo.setTitle(cursor.getString(title));
            diaryInfo.setAuthor(cursor.getString(author));
            diaryInfo.setContent(cursor.getString(content));
            diaryInfo.setPhoto(cursor.getBlob(photo));
            diaryInfo.setTime(cursor.getString(time));
            diaryList.add(diaryInfo);
        }
        return diaryList;
    }

}
