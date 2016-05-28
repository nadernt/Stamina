package com.fleecast.stamina.listviewdragginganimation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fleecast.stamina.R;

import java.util.HashMap;
import java.util.List;

public class StableArrayAdapter extends ArrayAdapter<CheesesSortingStruct> {

    final int INVALID_ID = -1;
    private final Context context;

    HashMap<CheesesSortingStruct, Integer> mIdMap = new HashMap<CheesesSortingStruct, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId, List<CheesesSortingStruct> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        return mIdMap.get(getItem(position));
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.groups_ordering_text_view, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.groups_ordering_text);
        textView.setText(getItem(position).getGroupName());

        return rowView;
    }

    @Override
    public boolean hasStableIds()
    {
        return android.os.Build.VERSION.SDK_INT < 20;
    }
}
