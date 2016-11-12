package com.fleecast.stamina.chathead;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

public class ScreenReceiver extends BroadcastReceiver {
    private OnScreenChangesEventListener eventListener;
    private boolean screenOff;

        @Override
        public void onReceive (Context context, Intent intent){
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screenOff = true;
                eventListener.onScreenOnOffEvent(screenOff);
            }

            if (intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
                Configuration newConfig = context.getResources().getConfiguration();

            }



        }

        public ScreenReceiver(OnScreenChangesEventListener eventListener) {
            this.eventListener = eventListener;
        }

}
