package com.emercy.newschannelmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.net.IDN;
import java.util.List;

public class ChannelAdapter extends BaseAdapter {

    private List<String> mList;
    private Context mContext;
    private int mReadyToRemove = -1;
    private AnimState mAnimState = AnimState.IDLE;

    enum AnimState {
        IDLE,
        TRANSLATING
    }

    ChannelAdapter(Context context, List<String> userList) {
        mContext = context;
        mList = userList;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_mygridview_item, null);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.tv = convertView.findViewById(R.id.text_item);
            convertView.setTag(viewHolder);
        }

        TextView tv = viewHolder.tv;
        if (mReadyToRemove == position
                || mAnimState == AnimState.TRANSLATING && position == getCount() - 1) {
            tv.setText("");
            tv.setSelected(true);
        } else {
            tv.setText(mList.get(position));
            tv.setSelected(false);
        }

        return convertView;
    }

    void add(String channelName) {
        mList.add(channelName);
        notifyDataSetChanged();
    }


    String setRemove(int position) {
        mReadyToRemove = position;
        notifyDataSetChanged();
        return mList.get(position);
    }

    void remove() {
        remove(mReadyToRemove);
        mReadyToRemove = -1;
    }

    private void remove(int index) {
        if (index > 0 && index < mList.size()) {
            mList.remove(index);
        }
        notifyDataSetChanged();
    }

    public void setTranslating(boolean translating) {
        mAnimState = translating ? AnimState.TRANSLATING : AnimState.IDLE;
    }

    private class ViewHolder {
        TextView tv;
    }
}
