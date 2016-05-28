package com.fleecast.stamina.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class AppLauncherGridViewAdapter extends BaseAdapter implements Filterable {
   // private final DisplayImageOptions options;
    private MyApplication myApplication;
    private Context mContext;
    private List<GridViewAppItemStruct> mItems;
    private Filter gridViewItemFilter;

    private List<GridViewAppItemStruct> originalGridViewItem;

    public AppLauncherGridViewAdapter(Context context, List<GridViewAppItemStruct> items, MyApplication myApplication) {
        //super(context, R.layout.gridview_item, items);

        this.myApplication =  myApplication;
        mContext = context;
        mItems = items;
        originalGridViewItem = items;
        //Log.e("MAMA", myApplication.getAppJustLaunchedByUser() + "");
        /*options = new DisplayImageOptions.Builder()
					*//*.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error)*//*
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();*/
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
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        GridViewAppItemStruct item = mItems.get(position);
        //ImageLoader.getInstance().displayImage("drawable://" + R.drawable.lightbulb, viewHolder.ivIcon, options);
        viewHolder.ivIcon.setImageDrawable(item.getIcon());
        viewHolder.tvTitle.setText(item.getTitle());
        //mItems.set(position,null);
        return convertView;
    }

    public void applyGroupFilterToAdapter(){

         mItems =  filterByGroupCode("");
         notifyDataSetChanged();

    }


    private List<GridViewAppItemStruct> filterByGroupCode(CharSequence stringFilter){

        List<GridViewAppItemStruct> nItemList = new ArrayList<GridViewAppItemStruct>();
final CharSequence nn = stringFilter;
        int chosenFilter  = myApplication.getCurrentGroupFilter();

        if(chosenFilter>0) {
            for (GridViewAppItemStruct mitem : originalGridViewItem) {

                // Implementing group filter
                if (mitem.getAppGroup() == chosenFilter) {

                    if (stringFilter == null || stringFilter.length() == 0) {
                        nItemList.add(mitem);
                    }
                    else{
                            if (findTheLetterInSentence(mitem.getTitle(),stringFilter))
                                nItemList.add(mitem);
                    }
                }

            }
            return nItemList;
        }
        else{

            if (stringFilter == null || stringFilter.length() == 0) {

                return originalGridViewItem;

             }

            else{

                for (GridViewAppItemStruct mitem : mItems) {
                    if (findTheLetterInSentence(mitem.getTitle(),stringFilter))
                        nItemList.add(mitem);
                }

                return nItemList;


            }


        }

    }

    private boolean findTheLetterInSentence(String sentence, CharSequence filterLetter){
        String[] splitedStrArr = sentence.split("\\s+");

        for(int i=0; i < splitedStrArr.length; i++){

            if(splitedStrArr[i].toUpperCase().startsWith(filterLetter.toString().trim().toUpperCase()))
                return true;
        }

        return false;
    }

    public void resetData() {
        mItems = originalGridViewItem;
    }


    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
    }

    /*
	 * We create our filter
	 */

    @Override
    public Filter getFilter() {

        if (gridViewItemFilter == null)
            gridViewItemFilter = new GridViewAdapterFilter();

        return gridViewItemFilter;
    }



    private class GridViewAdapterFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                List<GridViewAppItemStruct> filteredItems = filterByGroupCode("");
                results.values = filteredItems;
                results.count = filteredItems.size();
            }
            else {
                /*// We perform filtering operation
                List<GridViewAppItemStruct> nItemList = new ArrayList<GridViewAppItemStruct>();

                for (GridViewAppItemStruct mitem : mItems) {
                    if (mitem.getTitle().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nItemList.add(mitem);
                }*/
                List<GridViewAppItemStruct> filteredItems = filterByGroupCode(constraint);
                results.values = filteredItems;
                results.count = filteredItems.size();

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

                mItems = (List<GridViewAppItemStruct>) results.values;
                notifyDataSetChanged();
        }

    }
}