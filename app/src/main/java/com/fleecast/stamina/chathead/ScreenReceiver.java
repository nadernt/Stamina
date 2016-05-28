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
                //Get the orientation from the current configuration object
               // eventListener.onMyScreenOrintationEvent(newConfig);
                //Log.e("EEEEEEEEEEEEEEEEEE", "BBBBBBBBBBBBBB");

                /*int orientation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
                //Configuration newConfig;
                //Get the orientation from the current configuration object
                int configOrientation = context.getResources().getConfiguration().orientation;

                //Display the current orientation using a Toast notification
                switch (configOrientation)
                {
                    case Configuration.ORIENTATION_LANDSCAPE:
                    {
                        eventListener.onMyScreenOrintationEvent(Configuration);
                        break;
                    }
                    case Configuration.ORIENTATION_PORTRAIT:
                    {
                        eventListener.onMyScreenOrintationEvent(Configuration.ORIENTATION_PORTRAIT);
                        break;
                    }
                    case Configuration.ORIENTATION_SQUARE:
                    {
                        Toast.makeText(context, "Orientation is SQUARE", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case Configuration.ORIENTATION_UNDEFINED:
                    default:
                    {
                        Toast.makeText(context, "Orientation is UNDEFINED", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }*/
                        //eventListener.onMyScreenOrintationEvent(context.getResources().getConfiguration());
            }



        }

        public ScreenReceiver(OnScreenChangesEventListener eventListener) {
            this.eventListener = eventListener;
        }

}
