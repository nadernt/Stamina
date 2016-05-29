package com.fleecast.stamina.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fleecast.stamina.R;

import java.io.File;
import java.util.List;

public class FolderChooseAdapter extends BaseAdapter {
    private List <File> files;
    private Context context;

    public FolderChooseAdapter(Context context, List files) {
        this.files = files;
        this.context = context;
    }


    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        ViewHolder holder;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.folder_choose_list, parent, false);
            holder = new ViewHolder();
            holder.tvItemName = (TextView) convertView.findViewById(R.id.list_item_textview);
            holder.ivItemIcon = (ImageView) convertView.findViewById(R.id.list_item_icon_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File f = files.get(position);
        holder.tvItemName.setText(f.getName());
        holder.ivItemIcon.setImageResource(f.isDirectory() ? R.drawable.ic_dir : R.drawable.ic_file);

        return convertView;
    }

    private class ViewHolder {
        private ImageView ivItemIcon;
        private TextView tvItemName;
    }
}