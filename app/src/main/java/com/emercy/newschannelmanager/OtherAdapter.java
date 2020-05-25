package com.emercy.newschannelmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class OtherAdapter extends BaseAdapter {

    private List<String> mList;
    private Context mContext;
    private ViewHolder mViewHolder;

    OtherAdapter(Context context, List<String> otherList) {
        mContext = context.getApplicationContext();
        mList = otherList;
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

        viewHolder.tv.setText(mList.get(position));
        return convertView;
    }

    private class ViewHolder {
        TextView tv;
    }
}
