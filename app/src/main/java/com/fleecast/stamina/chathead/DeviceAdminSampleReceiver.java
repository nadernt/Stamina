package com.fleecast.stamina.chathead;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by cxphong on 7/12/15.
 */
public class DeviceAdminSampleReceiver extends android.app.admin.DeviceAdminReceiver {
    void showToast(Context context, String msg) {
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return null;
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
    }
}