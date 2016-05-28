package com.fleecast.stamina.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppsUpdateReceiver extends BroadcastReceiver {
    OnUserAppsUpdatesEventListener onUserAppsUpdatesEventListener;
        @Override
        public void onReceive (Context context, Intent intent){

/*
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) || intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED) ||
                    intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {*/
                if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                        intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) ) {
                //Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();
                    Log.e("DBGGGFFFFFFFFFFFFFF" ,intent.getAction());
                    onUserAppsUpdatesEventListener.onAppsLauncherListChangedEvent();
            }

        }


    public AppsUpdateReceiver(OnUserAppsUpdatesEventListener onUserAppsUpdatesEventListener) {
        this.onUserAppsUpdatesEventListener = onUserAppsUpdatesEventListener;
    }
}

