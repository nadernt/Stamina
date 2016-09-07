package com.fleecast.stamina.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppsUpdateReceiver extends BroadcastReceiver {
    OnUserAppsUpdatesEventListener onUserAppsUpdatesEventListener;
        @Override
        public void onReceive (Context context, Intent intent){

                if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                        intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) ) {
                    onUserAppsUpdatesEventListener.onAppsLauncherListChangedEvent();
            }

        }


    public AppsUpdateReceiver(OnUserAppsUpdatesEventListener onUserAppsUpdatesEventListener) {
        this.onUserAppsUpdatesEventListener = onUserAppsUpdatesEventListener;
    }
}

