package com.fleecast.stamina.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;

import java.util.List;

/**
 * Created by nnt on 17/04/16.
 */
public class MostUsedRecentAdapter extends BaseAdapter {
    private Context mContext;
    private List<GridViewAppItemStruct> mItems;

    private List<GridViewAppItemStruct> originalGridViewItem;

    public MostUsedRecentAdapter(Context context, List<GridViewAppItemStruct> items, MyApplication myApplication) {
        //super(context, R.layout.gridview_item, items);
        mContext = context;
        mItems = items;
        originalGridViewItem = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitleTodoParent);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // update the item view
        GridViewAppItemStruct item = mItems.get(position);
        viewHolder.ivIcon.setImageDrawable(item.getIcon());
        viewHolder.tvTitle.setText(item.getTitle());

        return convertView;
    }


    public void resetData() {
        mItems = originalGridViewItem;
    }


    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
    }


}
