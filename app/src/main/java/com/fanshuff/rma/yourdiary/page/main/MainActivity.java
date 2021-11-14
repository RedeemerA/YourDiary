package com.fanshuff.rma.yourdiary.page.main;

import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_AUTHOR;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_CONTENT;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_DATABASE;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_DATABASE_TABLE;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_ID;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_PHOTO;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_TIME;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_TITLE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanshuff.rma.yourdiary.R;
import com.fanshuff.rma.yourdiary.database.DBdao;
import com.fanshuff.rma.yourdiary.database.DatabaseHelper;
import com.fanshuff.rma.yourdiary.entity.AuthorInfo;
import com.fanshuff.rma.yourdiary.entity.DiaryInfo;
import com.fanshuff.rma.yourdiary.page.author.AuthorActivity;
import com.fanshuff.rma.yourdiary.page.list.DiaryListActivity;
import com.fanshuff.rma.yourdiary.util.FanshuffEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * auther: RMA
 * Date: 2021/11/4
 * Time:18:42
 * Des:
 */

public class MainActivity extends AppCompatActivity {

    private Bitmap photo;

    public static final int TAKE_PHOTO_REQUEST = 1;

    private TextView mTextViewList, mTextViewAuthor, mTextViewSave, mTextViewNew, mTextViewAddPhoto;

    private FanshuffEditText mEditTextMain;

    private ImageView mImageViewPhoto;

    private EditText mEditTextTitle;

    private DatabaseHelper dbHelper;

    private DiaryInfo mDiaryInfo = new DiaryInfo();

    private AuthorInfo mAuthorInfo = new AuthorInfo();

    private boolean textModify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initListener();
    }

    private void initView() {
        mTextViewList = findViewById(R.id.tv_main_list);
        mTextViewAuthor = findViewById(R.id.tv_main_author);
        mTextViewSave = findViewById(R.id.tv_main_save);
        mTextViewNew = findViewById(R.id.tv_main_new);
        mTextViewAddPhoto = findViewById(R.id.tv_add_photo);

        mEditTextTitle = findViewById(R.id.et_main_title);
        mEditTextMain = findViewById(R.id.et_main);

        mImageViewPhoto = findViewById(R.id.iv_diary_photo);

        dbHelper = new DatabaseHelper(MainActivity.this);
    }


    private void initData() {

        Intent intent = getIntent();
        mDiaryInfo.setTitle(intent.getStringExtra(DIARY_TITLE));
        mDiaryInfo.setAuthor(intent.getStringExtra(DIARY_AUTHOR));
        mDiaryInfo.setContent(intent.getStringExtra(DIARY_CONTENT));
        mDiaryInfo.setId(intent.getIntExtra(DIARY_ID, -1));
        mDiaryInfo.setPhoto(intent.getByteArrayExtra(DIARY_PHOTO));
        mDiaryInfo.setTime(intent.getStringExtra(DIARY_TIME));
        if (mDiaryInfo.getPhoto() != null){
            readImage();
        }
        if (mDiaryInfo.getId() != -1 && mDiaryInfo.getId() != 0){
            mEditTextTitle.setText(mDiaryInfo.getTitle());
            mEditTextMain.setText(mDiaryInfo.getContent());

            SharedPreferences sharedPreferences = getSharedPreferences("AuthorInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //先清空
            editor.clear();
            editor.commit();
            //在填入
            editor.putString("AuthorName", mDiaryInfo.getAuthor());
            editor.commit();
            editor.clear();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("AuthorInfo", MODE_PRIVATE);
        mAuthorInfo.setAuthorName(sharedPreferences.getString("AuthorName", "DEFAULT"));
    }

    private void initListener() {

        mTextViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DiaryListActivity.class);
                startActivity(intent);
            }
        });

        mTextViewAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AuthorActivity.class);
                startActivity(intent);
            }
        });


        mTextViewAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        });


        mTextViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDiaryInfo.setContent(mEditTextMain.getText().toString());
                mDiaryInfo.setTitle(mEditTextTitle.getText().toString());


                if (mDiaryInfo.getContent().equals("")||mDiaryInfo.getTitle().equals("")){
                    Toast.makeText(MainActivity.this, "SAVE ERROR", Toast.LENGTH_SHORT).show();
                }else{
                    DBdao dBdao = new DBdao(MainActivity.this);
                    if (mDiaryInfo.getId() == -1 || mDiaryInfo.getId() == 0){
                        //打开/创建数据库并写入数据
                        SharedPreferences sharedPreferences = getSharedPreferences("AuthorInfo", MODE_PRIVATE);
                        mAuthorInfo.setAuthorName(sharedPreferences.getString("AuthorName", "DEFAULT"));
                        mDiaryInfo.setAuthor(mAuthorInfo.getAuthorName());
                        //获取当前时间&日期
                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                        Date date = new Date(System.currentTimeMillis());
                        mDiaryInfo.setTime(formatter.format(date));
                        if (mDiaryInfo.getPhoto() == null){
                            dBdao.addInformation(mDiaryInfo.getTitle(), mDiaryInfo.getAuthor(), mDiaryInfo.getContent(), null, mDiaryInfo.getTime());

                        }else{
                            dBdao.addInformation(mDiaryInfo.getTitle(), mDiaryInfo.getAuthor(), mDiaryInfo.getContent(), bitmabToBytes(mImageViewPhoto), mDiaryInfo.getTime());
                        }
                        Toast.makeText(MainActivity.this, "SAVE SUCCESS", Toast.LENGTH_SHORT).show();
                        //已经保存，不必再在新建时提示用户保存

                    }else{
                        //id != -1 modify data
                        if (mDiaryInfo.getPhoto() == null){
                            dBdao.modifyInformation(mDiaryInfo.getId(),mDiaryInfo.getTitle(), mDiaryInfo.getAuthor(), mDiaryInfo.getContent(), null, mDiaryInfo.getTime());
                        }else{
                            dBdao.modifyInformation(mDiaryInfo.getId(),mDiaryInfo.getTitle(), mDiaryInfo.getAuthor(), mDiaryInfo.getContent(), bitmabToBytes(mImageViewPhoto), mDiaryInfo.getTime());
                        }
                        Toast.makeText(MainActivity.this, "Modify Success", Toast.LENGTH_SHORT).show();
                    }
                    textModify = false;
                }

            }
        });

        mEditTextMain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (textModify = false){
                    textModify = true;
                }
            }
        });

        mTextViewNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textModify){
                    AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("警告")
                            .setMessage("直接新建将不会存储您未保存的修改，请问是否确定新建？")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mEditTextMain.setText("");
                                    mEditTextTitle.setText("Nameless Title");
                                    photo = null;
                                    mImageViewPhoto.setImageBitmap(null);
                                    mImageViewPhoto.setBackground(null);
                                    mImageViewPhoto.setImageDrawable(null);
                                    Toast.makeText(MainActivity.this, "已新建日记", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
                }else{
                    mEditTextMain.setText("");
                    mEditTextTitle.setText("Nameless Title");
                    photo = null;
                    mImageViewPhoto.setImageBitmap(null);
                    mImageViewPhoto.setBackground(null);
                    mImageViewPhoto.setImageDrawable(null);
                }
                Toast.makeText(MainActivity.this, "新建日记成功", Toast.LENGTH_SHORT).show();
                mDiaryInfo = new DiaryInfo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST){

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "取消了拍照", Toast.LENGTH_LONG).show();
                return;
            }
            photo = data.getParcelableExtra("data");
            mImageViewPhoto.setImageBitmap(photo);
            mDiaryInfo.setPhoto(bitmabToBytes(mImageViewPhoto));

        }

    }


    //图片转为二进制数据
    public byte[] bitmabToBytes(ImageView imageView){
        //将图片转化为位图
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos= new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }catch (Exception e){
        }finally {
            try {
                //这里没有进行recycle不知道会不会出现内存方面的问题。因此最好不要照搬，下次使用记得修改
//                if (bitmap != null && !bitmap.isRecycled()){
//                    imageView.setImageBitmap(null);// 取消scaleBitmap渲染到imageView
//                    bitmap.recycle();//scaleBitmap 回收
//                    bitmap = null;
//                }
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    private void readImage(){

        byte[] imgData= readImageOperation();
        if (imgData!=null) {
            //将字节数组转化为位图
            Bitmap imagebitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            //将位图显示为图片
            mImageViewPhoto.setImageBitmap(imagebitmap);
        }else {
            mImageViewPhoto.setBackgroundResource(android.R.drawable.menuitem_background);
        }
    }


    public byte[] readImageOperation(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cur=db.query("Diary", new String[]{"id","diarytitle","diaryauthor",
                "diarycontent","diaryphoto","diarytime"}, "id="+mDiaryInfo.getId(), null, null, null, null);
        byte[] imgData=null;
        if(cur.moveToNext()){
            //将Blob数据转化为字节数组
            int i = cur.getColumnIndex("diaryphoto");
            imgData=cur.getBlob(i);
        }
        return imgData;
    }
}