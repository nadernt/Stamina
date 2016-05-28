package com.fleecast.stamina.chathead;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by nnt on 21/02/16.
 */
public class UserPowerPolicies {

    /**
     * currentChosenPolicy = 0 then {Normal system mode}
     *                     = 1 then {Always on}
     *                     = 2 then {User chose the always onn with dim}
     *                     = 3 then {User chose the timeout}
     */
    private int currentChosenPolicy = 0;
    private boolean currentScreenTweak =false;

    private long timeOutToTurnOfScreen=0;

    public int NORMAL_SYSTEM_MODE =0;
    public int ALWAYSـON_NO_DIM =1;
    public int ALWAYSـON_WITH_DIM = 2;
    public int USER_CHOSE_THE_TIMEOUT = 3;

    private WindowManager windowManager;

    public void setScreenAlwaysON(View view, boolean screenAlwaysOn){

       /* WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) view.getLayoutParams();
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;*/
            currentScreenTweak=screenAlwaysOn;
            view.setKeepScreenOn(currentScreenTweak);

    }

    public void turnOFFScreen(Context context){


        DevicePolicyManager mManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mComponent = new ComponentName(context, DeviceAdminSampleReceiver.class);

        if (mManager.isAdminActive(mComponent)){
            mManager.lockNow();
        }

    }

    public void setChatHeadTransparent(float percentageTransparent,View view,boolean setTransparent){

        WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) view.getLayoutParams();
        if(!setTransparent) {
            mParams.alpha = 1.0f;
        }
        else
        {
            mParams.alpha =  percentageTransparent/100.0f;
        }
        windowManager.updateViewLayout(view, mParams);

    }

    public UserPowerPolicies(WindowManager windowManager) {
        this.windowManager= windowManager;
    }

    public int getCurrentChosenPolicy() {
        return currentChosenPolicy;
    }

    public void setCurrentChosenPolicy(int currentChosenPolicy) {
        this.currentChosenPolicy = currentChosenPolicy;
    }

    public boolean getCurrentScreenTweak() {
        return currentScreenTweak;
    }

    public void setCurrentScreenTweak(boolean currentScreenTweak) {

        if(currentScreenTweak)
            currentChosenPolicy=1;
        else
            currentChosenPolicy=0;
        this.currentScreenTweak = currentScreenTweak;
    }

    public long getTimeOutToTurnOfScreen() {
        return timeOutToTurnOfScreen;
    }

    public void setTimeOutToTurnOfScreen(long timeOutToTurnOfScreen) {
        this.timeOutToTurnOfScreen = timeOutToTurnOfScreen;
    }
}
