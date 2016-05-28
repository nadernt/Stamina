package com.fleecast.stamina.models;

import android.graphics.drawable.Drawable;

import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

public class GridViewAppItemStruct {
    private final Drawable icon;       // the drawable for the ListView item ImageView
    private final String title;        // the text for the GridView item title
    private final ActivityInfoStruct activityInfo;
    private final int appGroup;

    public GridViewAppItemStruct(Drawable icon, String title, ActivityInfoStruct activityInfo,int appGroup) {
        this.icon =  new Utility().resizeIcon(icon,Constants.ICONS_RENDER_QUALITY_HIGH,Constants.ICONS_RENDER_QUALITY_ALIAS);
        this.title = title;
        this.activityInfo = activityInfo;
        this.appGroup = appGroup;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public int getAppGroup(){
        return appGroup;
    }


    public ActivityInfoStruct getActivityInfo() {
        return activityInfo;
    }


}