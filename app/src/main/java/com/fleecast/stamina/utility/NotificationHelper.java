package com.fleecast.stamina.utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Created by nnt on 11/05/16.
 */
public class NotificationHelper {
    private final Context mContext;

    public NotificationHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void removeNotification(int idNotification) {


        android.app.NotificationManager notificationManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(idNotification);
    }

    public void showNotification( Intent intent, String notifyTitleText,String notifyBodyText,String notifyTicker,int iconRecourse,int notificationPriority,int notificationId) {

        PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);

        //Resources resourcesNotify = getResources();
        Notification notification = new NotificationCompat.Builder(mContext)
                .setTicker(notifyTicker)
                .setSmallIcon(iconRecourse)
                .setContentTitle(notifyTitleText)
                .setContentText(notifyBodyText)
                .setVisibility(notificationPriority)
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }
}
