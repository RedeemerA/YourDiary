package com.fanshuff.rma.yourdiary.page.list;

import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_AUTHOR;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_CONTENT;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_ID;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_PHOTO;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_TIME;
import static com.fanshuff.rma.yourdiary.normal.Value.DIARY_TITLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.fanshuff.rma.yourdiary.R;
import com.fanshuff.rma.yourdiary.database.DBdao;
import com.fanshuff.rma.yourdiary.entity.DiaryInfo;
import com.fanshuff.rma.yourdiary.page.main.MainActivity;

import java.util.List;

public class DiaryListAdapter extends BaseAdapter {

    private List<DiaryInfo> mDiaryInfoList;
    private LayoutInflater layoutInflater;

    public DiaryListAdapter (List<DiaryInfo> mDiaryInfoList, Context context){
        this.mDiaryInfoList = mDiaryInfoList;
        this.layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mDiaryInfoList == null?0:mDiaryInfoList.size(); //
    }

    @Override
    public Object getItem(int i) {
        return mDiaryInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //加载布局为一个视图
        View view = layoutInflater.inflate(R.layout.diary_list_item,null);
        DiaryInfo mDiaryInfo = (DiaryInfo) getItem(i);

        //在view 视图中查找 组件
        TextView mTextViewTitle = (TextView) view.findViewById(R.id.tv_list_item_title);
        TextView mTextViewAuthor = (TextView) view.findViewById(R.id.tv_list_item_author);
        TextView mTextViewTime = (TextView) view.findViewById(R.id.tv_list_item_time);
        TextView mTextViewDelete = (TextView) view.findViewById(R.id.tv_list_item_delete);
        ConstraintLayout mCLListItem = (ConstraintLayout) view.findViewById(R.id.cl_list_item);

        //为Item 里面的组件设置相应的数据
        mTextViewTitle.setText("标题："+ mDiaryInfo.getTitle());
        mTextViewAuthor.setText("作者: "+ mDiaryInfo.getAuthor());
        mTextViewTime.setText(mDiaryInfo.getTime());

        //delete function
        mTextViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBdao dBdao = new DBdao(view.getContext());
                dBdao.deleteInformation(mDiaryInfo.getId());
                notifyDataSetChanged();

            }
        });
        //open function
        mCLListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra(DIARY_TITLE, mDiaryInfo.getTitle());
                intent.putExtra(DIARY_AUTHOR, mDiaryInfo.getAuthor());
                intent.putExtra(DIARY_CONTENT, mDiaryInfo.getContent());
                intent.putExtra(DIARY_PHOTO, mDiaryInfo.getPhoto());
                intent.putExtra(DIARY_TIME, mDiaryInfo.getTime());
                intent.putExtra(DIARY_ID, mDiaryInfo.getId());
                view.getContext().startActivity(intent);
            }
        });

        //返回含有数据的view
        return view;
    }
}
