package com.fleecast.stamina.launcher;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fleecast.stamina.R;

public class CustomListLauncherFragment extends android.support.v4.app.Fragment {


    private View fragmentView;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("DBG", "onCreate ");

        //myApplication = (MyApplication) LauncherDialogActivity.myActivityInstance.getApplication();

    }
 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("DBG", "onCreateView");

        // inflate the root view of the fragment
        fragmentView = inflater.inflate(R.layout.fragment_custom_launcher, container, false);


/*
        if(!myApplication.getIsAppListLoaded()) {


        }

       loadGrid();

*/


        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadGrid() {

        Log.e("DBG", "Load Grid Called!");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     //   setRetainInstance(true);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        Log.e("DBG", "onDetach() called!");

            super.onDetach();
    }

}