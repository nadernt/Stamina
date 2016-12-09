package com.fleecast.stamina.backup;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fleecast.stamina.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArrayAdapterJournalBackups extends ArrayAdapter<File> {
	private final Context context;
	private final List<File> values;

	public ArrayAdapterJournalBackups(Context context, ArrayList<File> values) {
		super(context, R.layout.activity_jurnal_files, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.activity_jurnal_files, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.labelFile);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.fileEncryptType);

		textView.setText(values.get(position).getName().substring(0,values.get(position).getName().indexOf(".journal")));

		String s = values.get(position).getName();

		if (s.contains("encrypt")) {
			imageView.setImageResource(R.drawable.ic_action_key);
		} else {
			imageView.setImageResource(R.drawable.ic_action_lock_open);
		}

		return rowView;
	}
	public void myRemove(int position){
		values.remove(position);
		ArrayAdapterJournalBackups.super.notifyDataSetChanged();
	}
}
