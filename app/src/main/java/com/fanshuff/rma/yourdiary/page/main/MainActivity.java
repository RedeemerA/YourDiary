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
            //?????????
            editor.clear();
            editor.commit();
            //?????????
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
                        //??????/??????????????????????????????
                        SharedPreferences sharedPreferences = getSharedPreferences("AuthorInfo", MODE_PRIVATE);
                        mAuthorInfo.setAuthorName(sharedPreferences.getString("AuthorName", "DEFAULT"));
                        mDiaryInfo.setAuthor(mAuthorInfo.getAuthorName());
                        //??????????????????&??????
                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                        Date date = new Date(System.currentTimeMillis());
                        mDiaryInfo.setTime(formatter.format(date));
                        if (mDiaryInfo.getPhoto() == null){
                            dBdao.addInformation(mDiaryInfo.getTitle(), mDiaryInfo.getAuthor(), mDiaryInfo.getContent(), null, mDiaryInfo.getTime());

                        }else{
                            dBdao.addInformation(mDiaryInfo.getTitle(), mDiaryInfo.getAuthor(), mDiaryInfo.getContent(), bitmabToBytes(mImageViewPhoto), mDiaryInfo.getTime());
                        }
                        Toast.makeText(MainActivity.this, "SAVE SUCCESS", Toast.LENGTH_SHORT).show();
                        //??????????????????????????????????????????????????????

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
                            .setTitle("??????")
                            .setMessage("??????????????????????????????????????????????????????????????????????????????")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {//??????"Yes"??????
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mEditTextMain.setText("");
                                    mEditTextTitle.setText("Nameless Title");
                                    photo = null;
                                    mImageViewPhoto.setImageBitmap(null);
                                    mImageViewPhoto.setBackground(null);
                                    mImageViewPhoto.setImageDrawable(null);
                                    Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {//????????????
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
                Toast.makeText(MainActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                mDiaryInfo = new DiaryInfo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST){

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_LONG).show();
                return;
            }
            photo = data.getParcelableExtra("data");
            mImageViewPhoto.setImageBitmap(photo);
            mDiaryInfo.setPhoto(bitmabToBytes(mImageViewPhoto));

        }

    }


    //???????????????????????????
    public byte[] bitmabToBytes(ImageView imageView){
        //????????????????????????
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //?????????????????????????????????,???????????????size
        ByteArrayOutputStream baos= new ByteArrayOutputStream(size);
        try {
            //???????????????????????????????????????100%????????????????????????????????????
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //?????????????????????????????????????????????byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }catch (Exception e){
        }finally {
            try {
                //??????????????????recycle???????????????????????????????????????????????????????????????????????????????????????????????????
//                if (bitmap != null && !bitmap.isRecycled()){
//                    imageView.setImageBitmap(null);// ??????scaleBitmap?????????imageView
//                    bitmap.recycle();//scaleBitmap ??????
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
            //??????????????????????????????
            Bitmap imagebitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            //????????????????????????
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
            //???Blob???????????????????????????
            int i = cur.getColumnIndex("diaryphoto");
            imgData=cur.getBlob(i);
        }
        return imgData;
    }
}