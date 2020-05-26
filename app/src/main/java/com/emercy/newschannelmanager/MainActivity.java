package com.emercy.newschannelmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int ANIM_DURATION = 300;

    private GridView mOtherGv;
    private GridView mUserGv;
    private List<String> mUserList = new ArrayList<>();
    private List<String> mOtherList = new ArrayList<>();
    private ChannelAdapter mOtherAdapter;
    private ChannelAdapter mUserAdapter;

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
        mUserAdapter = new ChannelAdapter(this, mUserList);
        mOtherAdapter = new ChannelAdapter(this, mOtherList);
        mUserGv.setAdapter(mUserAdapter);
        mOtherGv.setAdapter(mOtherAdapter);
        mUserGv.setOnItemClickListener(this);
        mOtherGv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

        GridView currentView;
        final GridView anotherView;

        if (parent == mUserGv) {
            currentView = mUserGv;
            anotherView = mOtherGv;
        } else {
            currentView = mOtherGv;
            anotherView = mUserGv;
        }

        final int[] startPos = new int[2];
        final int[] endPos = new int[2];
        view.getLocationInWindow(startPos);

        ChannelAdapter currentAdapter = (ChannelAdapter) currentView.getAdapter();
        ChannelAdapter anotherAdapter = (ChannelAdapter) anotherView.getAdapter();

        anotherAdapter.setTranslating(true);
        anotherAdapter.add(currentAdapter.setRemove(position));

        final ImageView cloneView = getCloneView(view);
        ((ViewGroup) getWindow().getDecorView())
                .addView(cloneView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        currentView.post(new Runnable() {
            @Override
            public void run() {
                View lastView = anotherView.getChildAt(anotherView.getChildCount() - 1);
                lastView.getLocationInWindow(endPos);

                moveAnimation(cloneView, startPos, endPos, ANIM_DURATION);
            }
        });
    }

    private ImageView getCloneView(View view) {
        // 旧API
//        view.destroyDrawingCache();
//        view.setDrawingCacheEnabled(true);
//        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
//        view.setDrawingCacheEnabled(false);
//        ImageView imageView = new ImageView(this);
//        imageView.setImageBitmap(cache);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }


    /**
     * 移动动画，完成频道的添加删除动效
     *
     * @param moveView 待移动的View
     * @param startPos
     * @param endPos
     * @param duration
     */
    private void moveAnimation(final View moveView, int[] startPos, int[] endPos, int duration) {
        // 到这里要解决几个参数。。view、坐标、时长
        TranslateAnimation animation = new TranslateAnimation(startPos[0], endPos[0], startPos[1], endPos[1]);
        animation.setFillAfter(false);
        animation.setDuration(duration);
        moveView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((ViewGroup) moveView.getParent()).removeView(moveView);
                resetAdapterState();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void resetAdapterState() {
        mUserAdapter.setTranslating(false);
        mOtherAdapter.setTranslating(false);

        mUserAdapter.remove();
        mOtherAdapter.remove();
    }


}