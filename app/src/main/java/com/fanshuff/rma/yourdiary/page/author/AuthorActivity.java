package com.fanshuff.rma.yourdiary.page.author;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fanshuff.rma.yourdiary.R;

public class AuthorActivity extends AppCompatActivity {

    private TextView mTextViewBack, mTextViewSave;

    private EditText mEditTextAuthorName, mEditTextYear, mEditTextMonth, mEditTextDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

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

        mTextViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditTextAuthorName.getText().toString();
                int year = Integer.valueOf(mEditTextYear.getText().toString()),
                        month = Integer.valueOf(mEditTextMonth.getText().toString()),
                        day = Integer.valueOf(mEditTextDay.getText().toString());
                if (name.equals("")||name == null){
                    Toast.makeText(AuthorActivity.this, "Error.请输入作者名", Toast.LENGTH_SHORT).show();
                }  else{
                    SharedPreferences sharedPreferences = getSharedPreferences("AuthorInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //先清空
                    editor.clear();
                    editor.commit();
                    //在填入
                    editor.putString("AuthorName", name);
                    editor.putInt("Year", year);
                    editor.putInt("Month", month);
                    editor.putInt("Day", day);
                    editor.commit();
                    editor.clear();
                    Toast.makeText(AuthorActivity.this, "Save Success", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("AuthorInfo", MODE_PRIVATE);
        mEditTextAuthorName.setText(sharedPreferences.getString("AuthorName", null));
        mEditTextYear.setText(String.valueOf(sharedPreferences.getInt("Year", 1900)));
        mEditTextMonth.setText(String.valueOf(sharedPreferences.getInt("Month",1)));
        mEditTextDay.setText(String.valueOf(sharedPreferences.getInt("Day",1)));
    }

    private void initView() {

        mTextViewSave = findViewById(R.id.tv_author_save);
        mTextViewBack = findViewById(R.id.tv_author_back);

        mEditTextAuthorName = findViewById(R.id.et_author_name);
        mEditTextYear = findViewById(R.id.et_author_year);
        mEditTextMonth = findViewById(R.id.et_author_month);
        mEditTextDay = findViewById(R.id.et_author_day);
    }
}