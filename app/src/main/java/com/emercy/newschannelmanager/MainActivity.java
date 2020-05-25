package com.emercy.newschannelmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView mOtherGv;
    private GridView mUserGv;
    private List<String> mUserList = new ArrayList<>();
    private List<String> mOtherList = new ArrayList<>();
    private OtherAdapter mOtherAdapter;
    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    public void initView() {
        mUserGv = findViewById(R.id.userGridView);
        mOtherGv = findViewById(R.id.otherGridView);
        mUserList.add("推荐");
        mUserList.add("热点");
        mUserList.add("上海");
        mUserList.add("时尚");
        mUserList.add("科技");
        mUserList.add("体育");
        mUserList.add("军事");
        mUserList.add("财经");
        mUserList.add("网络");
        mOtherList.add("汽车");
        mOtherList.add("房产");
        mOtherList.add("社会");
        mOtherList.add("情感");
        mOtherList.add("女人");
        mOtherList.add("旅游");
        mOtherList.add("健康");
        mOtherList.add("美女");
        mOtherList.add("游戏");
        mOtherList.add("数码");
        mOtherList.add("娱乐");
        mOtherList.add("探索");
        mUserAdapter = new UserAdapter(this, mUserList);
        mOtherAdapter = new OtherAdapter(this, mOtherList);
        mUserGv.setAdapter(mUserAdapter);
        mOtherGv.setAdapter(mOtherAdapter);
        mUserGv.setOnItemClickListener(this);
        mOtherGv.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mUserGv) {
            mOtherList.add(mUserList.remove(position));

        } else {
            mUserList.add(mOtherList.remove(position));
        }
        mUserAdapter.notifyDataSetChanged();
        mOtherAdapter.notifyDataSetChanged();
    }
}