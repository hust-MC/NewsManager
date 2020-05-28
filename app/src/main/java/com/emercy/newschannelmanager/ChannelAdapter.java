package com.emercy.newschannelmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * 频道列表适配器，负责列表的数据处理和UI刷新，List形式的数组和网格列表UI样式适配到一起
 *
 * @author 马超 Emercy
 */
class ChannelAdapter extends BaseAdapter {

    /** 数据列表 */
    private List<String> mList;
    private Context mContext;
    /** 标记准备删除的数据 */
    private int mReadyToRemove = -1;
    /** 动画状态 */
    private AnimState mAnimState = AnimState.IDLE;
    /** 标记是否是用户频道列表 */
    private boolean mIsUserChannel;
    /** 标记当前是否是编辑状态 */
    private static boolean mInEditState;

    /**
     * 动画状态枚举类，用于对不同的动画状态进行处理。当前只支持空闲和移动态也可以用boolean变量，
     * 用enum主要是方便后续可以根据业务继续扩展
     */
    enum AnimState {
        IDLE,
        TRANSLATING
    }

    /**
     * 构造器，要求使用方传入上下文以及数据列表、并标识自己是否是用户频道
     *
     * @param context       上下文对象，用于获取layoutInflate对象
     * @param userList      用户数据列表
     * @param isUserChannel 是否是用户频道
     */
    ChannelAdapter(Context context, List<String> userList, boolean isUserChannel) {
        mContext = context;
        mList = userList;
        mIsUserChannel = isUserChannel;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1、复用convertView，只创建一次，节省性能
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_mygridview_item, null);
        }
        // 2、绑定ViewHolder，避免频繁的findViewById大量耗时
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.tv = convertView.findViewById(R.id.text_item);
            viewHolder.iv = convertView.findViewById(R.id.iv_edit);
            convertView.setTag(viewHolder);
        }

        // 3、获取item上的控件对象
        TextView tv = viewHolder.tv;
        ImageView iv = viewHolder.iv;

        // 4、业务逻辑

        // 根据当前是否为编辑态决定是否显示编辑icon（加或者减)
        if (mInEditState) {
            iv.setImageResource(mIsUserChannel ? R.mipmap.substract : R.mipmap.add);
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.INVISIBLE);
        }

        if (mReadyToRemove == position
                || mAnimState == AnimState.TRANSLATING && position == getCount() - 1) {
            // 如果当前位置是准备删除的item，或者是正在播放加入动画，那么设置TextView为占位状态。
            // 即设置成选择态，并清空文本内容，隐藏icon
            tv.setText("");
            tv.setSelected(true);
            iv.setVisibility(View.INVISIBLE);
        } else {
            // 为除if条件以外的item设置文本，取消选择态
            tv.setText(mList.get(position));
            tv.setSelected(false);
        }

        return convertView;
    }

    /**
     * 增加列表数据并触发列表刷新
     *
     * @param channelName 增加的列表文本
     */
    void add(String channelName) {
        mList.add(channelName);
        notifyDataSetChanged();
    }

    /**
     * 标记待删除的列表序号，并触发列表刷新，更新被标记的item样式
     *
     * @param position 列表序号
     * @return 返回被删除的那一条频道的名称
     */
    String setRemove(int position) {
        mReadyToRemove = position;
        notifyDataSetChanged();
        return mList.get(position);
    }

    /**
     * 删除当前标记的频道，并充值标记，刷新列表
     */
    void remove() {
        remove(mReadyToRemove);
        mReadyToRemove = -1;
    }

    /**
     * 直接删除指定的列表序号，并刷新列表
     *
     * @param index
     */
    void remove(int index) {
        if (index > 0 && index < mList.size()) {
            mList.remove(index);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置当前的动画状态
     *
     * @param translating 是否正在移动
     */
    void setTranslating(boolean translating) {
        mAnimState = translating ? AnimState.TRANSLATING : AnimState.IDLE;
    }

    /**
     * 设置当前是否是编辑态
     *
     * @param isEdit true：是编辑态；false：非编辑态：
     */
    static void setEdit(boolean isEdit) {
        mInEditState = isEdit;
    }

    /**
     * 判断当期是否是编辑态
     *
     * @return true：是编辑态；false：非编辑态
     */
    static boolean isEdit() {
        return mInEditState;
    }

    /**
     * 控件缓存，用来保存列表上的控件对象，与item绑定在一起，
     * 目的是避免频繁的findViewById从而节省性能
     */
    private class ViewHolder {
        TextView tv;
        ImageView iv;
    }
}
