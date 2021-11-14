package com.fanshuff.rma.yourdiary.page.list;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fanshuff.rma.yourdiary.R;
import com.fanshuff.rma.yourdiary.database.DBdao;
import com.fanshuff.rma.yourdiary.entity.DiaryInfo;

import java.util.ArrayList;
import java.util.List;

public class DiaryListActivity extends AppCompatActivity {

    private TextView mTextViewBack;
    private ListView mListViewDiaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        initView();

        initData();

        initListener();

    }

    private void initListener() {
        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        DBdao dBdao = new DBdao(DiaryListActivity.this);

        List<DiaryInfo> mDiaryInfoList = new ArrayList<>();  //创建studentData 对象集合
        mDiaryInfoList = dBdao.getAllPoints();

        //创建Adapter 实例化对象， 调用构造函数传参，将数据和adapter  绑定
        DiaryListAdapter mDiaryListAdapter = new DiaryListAdapter(mDiaryInfoList,this);
        mListViewDiaryList.setAdapter(mDiaryListAdapter);   //将定义的adapter 和 listView 绑定
    }

    private void initView() {
        mTextViewBack = findViewById(R.id.tv_list_back);
        mListViewDiaryList = findViewById(R.id.lv_list);
    }
}