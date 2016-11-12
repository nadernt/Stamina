package com.fleecast.stamina.launcher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.GridViewAppItemStruct;
import com.fleecast.stamina.models.MostUsedRecentAdapter;
import com.fleecast.stamina.models.MostUsedAndRecentAppsStruct;
import com.fleecast.stamina.models.RealmAppHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

import java.util.ArrayList;
import java.util.List;

public class RecentMostUsedFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener{

    private MyApplication myApplication;
    private View fragmentRecentMostUsed;
    private ArrayList<GridViewAppItemStruct> mostUsed;
    private ArrayList<GridViewAppItemStruct> recentUsed;
    private MostUsedRecentAdapter mostUsedAdapter;
    private MostUsedRecentAdapter recentUsedAdapter;
    private GridView gridViewMostUsed;
    private GridView gridViewRecentUsed;
    private RealmAppHelper realmAppHelper;

    /* If app whule is loading goes to background then we cannot have the subchild of the grid correctly
    * so when fragment resumes we check the update happened before if not we update again grids.*/
    private boolean ifAlreadyOneGridUpdateIsInProgress =false;

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            updateGrids();
        }
    }

    private void updateGrids(){


        RecentUsedApps recentUsedApps = new RecentUsedApps();
        recentUsedApps.execute(new PackageManagerStruct(LauncherDialogActivity.myActivityInstance.getPackageManager(), Constants.LIST_FOR_RECENT_USED));

        MostUsedApps mostUsedApps = new MostUsedApps();
        mostUsedApps.execute(new PackageManagerStruct(LauncherDialogActivity.myActivityInstance.getPackageManager(), Constants.LIST_FOR_MOST_USE));


    }

    private class RecentUsedApps extends AsyncAppInfoLoader {
        private List<GridViewAppItemStruct> tmpItems = new ArrayList<GridViewAppItemStruct>();

        @Override
        protected void onProgressUpdate(GridViewAppItemStruct... values) {
            //Check for shutdown of program by user.
            if(myApplication.getIsUserTerminateApp()) {
                this.cancel(true);
            }

                // check that the fragment is still attached to activity
                if (LauncherDialogActivity.myActivityInstance != null) {
                    // add the new item to the data set
                    tmpItems.add(values[0]);

                }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ifAlreadyOneGridUpdateIsInProgress =true;
            List<MostUsedAndRecentAppsStruct> tmpList = realmAppHelper.getAppListByRecentUsed();

            //Check if the size is zero kill the thread.
            if (tmpList.size() > 0) {
                myApplication.setRecentUsedAppsStruct(tmpList);
            } else
                this.cancel(true);

        }


        @Override
        protected void onPostExecute(Integer result) {
            if (LauncherDialogActivity.myActivityInstance != null) {

                if (recentUsed != null)
                    recentUsed.clear();

                recentUsed = new ArrayList<GridViewAppItemStruct>(tmpItems);
                // initialize the adapter
                recentUsedAdapter = new MostUsedRecentAdapter(getContext(), recentUsed, myApplication);

                gridViewRecentUsed.setAdapter(recentUsedAdapter);
            }
        }
    }

    private int calcPixelIndependent(int pixelToConvert){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelToConvert, getResources().getDisplayMetrics());
    }


    private class MostUsedApps extends AsyncAppInfoLoader {
        private List<GridViewAppItemStruct> tmpItems  = new ArrayList<GridViewAppItemStruct>();

        @Override
        protected void onProgressUpdate(GridViewAppItemStruct... values) {

            //Check for shutdown of program by user.
            if(myApplication.getIsUserTerminateApp()) {
                this.cancel(true);
            }
                // check that the fragment is still attached to activity
                if (LauncherDialogActivity.myActivityInstance != null) {
                    // add the new item to the data set
                    tmpItems.add(values[0]);

                }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            List<MostUsedAndRecentAppsStruct> tmpList = realmAppHelper.getAppListByMostUsed();

            //Check if the size is zero kill the thread.
            if(tmpList.size()>0) {
                myApplication.setMostUsedAppsStruct(tmpList);
            }
            else {
                this.cancel(true);
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            if(LauncherDialogActivity.myActivityInstance != null) {

                if(mostUsed!=null)
                    mostUsed.clear();

                mostUsed = new ArrayList<GridViewAppItemStruct>(tmpItems);
                // initialize the adapter
                mostUsedAdapter = new MostUsedRecentAdapter(getContext(), mostUsed, myApplication);

                gridViewMostUsed.setAdapter(mostUsedAdapter);
                ifAlreadyOneGridUpdateIsInProgress =false;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.e("DBG", " RecentMostUsedFragment onAttach ");

    }

    @Override
    public void onResume() {
        super.onResume();

        if(!ifAlreadyOneGridUpdateIsInProgress)
            updateGrids();

        Log.e("DBG", "RecentMostUsedFragment onResume ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("DBG", "RecentMostUsedFragment onCreate ");

        realmAppHelper = new RealmAppHelper(getContext());

        myApplication = (MyApplication) getContext().getApplicationContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("DBG", "RecentMostUsedFragment onCreateView");

              // inflate the root view of the fragment
        fragmentRecentMostUsed = inflater.inflate(R.layout.fragment_recent_most, container, false);

        // initialize the GridView
        gridViewRecentUsed = (GridView) fragmentRecentMostUsed.findViewById(R.id.gridViewRecent);
        gridViewRecentUsed .setAdapter(recentUsedAdapter);
        gridViewRecentUsed .setOnItemClickListener(this);
        // initialize the GridView
        gridViewMostUsed = (GridView) fragmentRecentMostUsed.findViewById(R.id.gridViewMostUsed);
        gridViewMostUsed.setAdapter(mostUsedAdapter);
        gridViewMostUsed.setOnItemClickListener(this);

        return fragmentRecentMostUsed;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("DBG", "RecentMostUsedFragment onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("DBG", "RecentMostUsedFragment onStop");

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setRetainInstance(true);
        Log.e("DBG", "RecentMostUsedFragment onActivityCreated");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("DBG", "RecentMostUsedFragment onDestroy");

    }

    @Override
    public void onPause() {
        super.onPause();
        //Check for shutdown of program by user.
      /*  if(myApplication.getIsUserTerminateApp()) {
            Log.e("DBG", "RecentMostUsedFragment finito!!!!!!!!");
            getActivity().finish();
        }*/
        Log.e("DBG", "RecentMostUsedFragment onPause");

    }

    @Override
    public void onDetach() {
        Log.e("DBG", "RecentMostUsedFragment onDetach");

        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {

            GridViewAppItemStruct item = (GridViewAppItemStruct) parent.getItemAtPosition(position);

            Utility utility = new Utility();

            if(!utility.isPackageInstalled(item.getActivityInfo().getPackageName(),getContext()))
            {
                Log.e("DBG", "App uninstalled!");
                realmAppHelper.delete(item.getActivityInfo().getPackageName());
                parent.removeViewAt(position);

            }
            else {


                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName(item.getActivityInfo().getPackageName(), item.getActivityInfo().getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                startActivity(intent);

                // Update app launch in database.
                realmAppHelper.updateLastUsageApp(item.getTitle(), item.getActivityInfo().getPackageName());

                updateGrids();


                Log.e("DBG", "Package exist.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}