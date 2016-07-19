package com.fleecast.stamina.launcher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.ActivityInfoStruct;
import com.fleecast.stamina.models.GridViewAppItemStruct;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Prefs;

import java.util.Collections;
import java.util.List;

public class AsyncAppInfoLoader extends AsyncTask<PackageManagerStruct, GridViewAppItemStruct, Integer> {

    private int totalDetectedAppNumbers=0;
    private int command;

    @Override
    protected Integer doInBackground(PackageManagerStruct... params) {
          MyApplication mApplication =  (MyApplication) LauncherDialogActivity.myActivityInstance.getApplicationContext();

       // RealmAppHelper realmAppHelper = new RealmAppHelper(LauncherDialogActivity.myActivityInstance);

        // this method executes the task in a background thread
        PackageManager packageManager = params[0].getPackageManager(); // the PackageManager for loading the data
        command = params[0].getCommand();

        int index = 0;

        Log.e("DBGA", "AsyncAppInfoLoader" + " Command is = " + command);



        if(command== Constants.LIST_FOR_GRID) {

            Log.e("NumbeRRRR:", "Badoo1");

            Intent intent = new Intent(Intent.ACTION_MAIN, null);

            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            totalDetectedAppNumbers = resolveInfoList.size();

            if (Prefs.getBoolean("SortIsAlphabeticOrDate", true)) {
                Collections.sort(resolveInfoList, new ResolveInfo.DisplayNameComparator(packageManager));
            }

            if(resolveInfoList != null){


                for (ResolveInfo resolveInfo : resolveInfoList)   {
                    Drawable icon = resolveInfo.loadIcon(packageManager);
                    CharSequence label = resolveInfo.loadLabel(packageManager);

                    if(icon != null && label != null) {

                       /* BitmapDrawable bd=(BitmapDrawable) icon;
                        int height=bd.getBitmap().getHeight();
                        int width=bd.getBitmap().getWidth();
                        Log.e("Dad",height + "  " + width);*/

                        // update the UI thread
                        publishProgress(new GridViewAppItemStruct(icon, label.toString(), new ActivityInfoStruct(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name), Constants.APP_IS_IN_DEFAULT_GROUP));

                        index++;

                    }

                    icon = null;

                    /*index++;

                    if(index>5)
                    break;
*/

                }

                resolveInfoList.clear();
                resolveInfoList = null;
            }

        }

        else if (command== Constants.LIST_FOR_MOST_USE || command== Constants.LIST_FOR_RECENT_USED) {


            int querySize = 0;
            int defaultGroup = 0;

            if(command== Constants.LIST_FOR_RECENT_USED)
                querySize = mApplication.getRecentUsedAppsStruct().size();
            else
                querySize = mApplication.getMostUsedAppsStruct().size();

            if(querySize>20)
                querySize=20;

            for(int i=0; i<querySize;i++){

                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setAction(Intent.ACTION_MAIN);

                if(command== Constants.LIST_FOR_RECENT_USED)
                    intent.setPackage(mApplication.getRecentUsedAppsStruct().get(i).getAppPackageName());
                else
                    intent.setPackage(mApplication.getMostUsedAppsStruct().get(i).getAppPackageName());


                ResolveInfo rInfo = packageManager.resolveActivity(intent, 0);

                ActivityInfo aInfo = rInfo.activityInfo;

                try {

                    Drawable icon = rInfo.loadIcon(packageManager);
                    CharSequence label = rInfo.loadLabel(packageManager);

                    if(command== Constants.LIST_FOR_RECENT_USED)
                        defaultGroup =  mApplication.getRecentUsedAppsStruct().get(i).getAppGroup();
                    else
                        defaultGroup =  mApplication.getMostUsedAppsStruct().get(i).getAppGroup();

                    if(icon != null && label != null) {
                        // update the UI thread
                        publishProgress(new GridViewAppItemStruct(icon,label.toString(), new ActivityInfoStruct(aInfo.packageName,aInfo.name), defaultGroup));
                        index++;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        packageManager = null;
        Log.e("DBG", "Indez!" + index);
        return index;
    }

    public int getTotalDetectedAppNumbers(){return totalDetectedAppNumbers;}


}

