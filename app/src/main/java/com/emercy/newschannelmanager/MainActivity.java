package com.emercy.newschannelmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /**
     * 动画持续时长
     */
    private static final int ANIM_DURATION = 300;
    private static final String CHANNEL_DATA_FILE = "channel_data.json";

    /**
     * 双GridView频道列表
     */
    private GridView mOtherGv;
    private GridView mUserGv;
    /**
     * 频道数据
     */
    private List<String> mUserList = new ArrayList<>();
    private List<String> mOtherList = new ArrayList<>();
    /**
     * 频道适配器
     */
    private ChannelAdapter mOtherAdapter;
    private ChannelAdapter mUserAdapter;

    private TextView mMoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化View相关
     */
    public void initView() {
        // 1、获取“更多频道”TextView、用户频道GridView以及其他频道GridView
        mMoreTextView = findViewById(R.id.tv_more);
        mUserGv = findViewById(R.id.userGridView);
        mOtherGv = findViewById(R.id.otherGridView);

        // 2、初始化数据
        try {
            InputStream is = getAssets().open(CHANNEL_DATA_FILE);
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf-8");
            JSONObject jsonObject = new JSONObject(result);
            JSONArray userChannel = jsonObject.optJSONArray("user");
            for (int i = 0; i < userChannel.length(); i++) {
                mUserList.add(userChannel.optString(i));
            }
            JSONArray otherChannel = jsonObject.optJSONArray("other");
            for (int i = 0; i < otherChannel.length(); i++) {
                mOtherList.add(otherChannel.optString(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 3、初始化适配器，并为双GridView设置适配器
        mUserAdapter = new ChannelAdapter(this, mUserList, true);
        mOtherAdapter = new ChannelAdapter(this, mOtherList, false);
        mUserGv.setAdapter(mUserAdapter);
        mOtherGv.setAdapter(mOtherAdapter);
        // 4、设置GridView列表点击监听
        mUserGv.setOnItemClickListener(this);
        mOtherGv.setOnItemClickListener(this);

        // 5、设置“编辑”Button的点击事件监听
        findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击“编辑”Button 进入/退出编辑态
                toggleEditState();
                ((ImageView) v).setImageResource(ChannelAdapter.isEdit() ? R.mipmap.ok : R.mipmap.edit);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

        // 判断是否为编辑态：
        // 如果是编辑态则点击的时候处理删除 / 增加操作
        // 如果不是编辑态则弹出Toast提示，实际使用中可以换成频道详情页的跳转
        if (ChannelAdapter.isEdit()) {

            // currentView表示当前被点击的GridView，anotherView表示另外一个GridView；
            // 这里统一定义current和anOther，后续无论点击的是哪一个列表均可共用同一套逻辑
            GridView currentView;
            final GridView anotherView;

            if (parent == mUserGv) {
                currentView = mUserGv;
                anotherView = mOtherGv;
            } else {
                currentView = mOtherGv;
                anotherView = mUserGv;
            }

            // 计算点击View的坐标，用作后面动画的起点
            final int[] startPos = new int[2];
            final int[] endPos = new int[2];
            view.getLocationInWindow(startPos);

            // 和GridView的处理一样，这里统一定义
            ChannelAdapter currentAdapter = (ChannelAdapter) currentView.getAdapter();
            ChannelAdapter anotherAdapter = (ChannelAdapter) anotherView.getAdapter();

            //
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
        } else {
            Toast.makeText(this, "进入" + mUserList.get(position) + "频道", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleEditState() {
        boolean isEdit = ChannelAdapter.isEdit();
        ChannelAdapter.setEdit(!isEdit);

        mMoreTextView.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
        mOtherGv.setVisibility(isEdit ? View.INVISIBLE : View.VISIBLE);
        mUserAdapter.notifyDataSetChanged();
        mOtherAdapter.notifyDataSetChanged();
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