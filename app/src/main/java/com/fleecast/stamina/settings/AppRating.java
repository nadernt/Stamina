package com.fleecast.stamina.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Utility;

import java.util.Date;

/**
 * Created by nnt on 4/12/16.
 */

public class AppRating {

        private final static String APP_TITLE = "Stamina";// App Name
        private static String APP_PNAME = "com.example.name";// Package Name

        private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
        private final static int LAUNCHES_UNTIL_PROMPT = 3;//Min number of launches

        public static void app_launched(Context mContext) {
            SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
            if (prefs.getBoolean("dontshowagain", false)) { return ; }

            SharedPreferences.Editor editor = prefs.edit();
            APP_PNAME = mContext.getPackageName();
            // Increment launch counter
            long launch_count = prefs.getLong("launch_count", 0) + 1;
            editor.putLong("launch_count", launch_count);

            // Get date of first launch
            Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
            if (date_firstLaunch == 0) {
                date_firstLaunch = System.currentTimeMillis();
                editor.putLong("date_firstlaunch", date_firstLaunch);
            }

            // Wait at least n days before opening
            if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
                if (System.currentTimeMillis() >= date_firstLaunch +
                        (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                    showRateDialog(mContext, editor);
                }
            }

            editor.commit();
        }

        public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {

            final AlertDialog.Builder ratingDialogBuilder= new AlertDialog.Builder(mContext);

            LinearLayout layout = new LinearLayout(mContext);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(parms);

            layout.setGravity(Gravity.CENTER);
           // layout.setPadding(5, 5, 5, 5);

            TextView tvMessage = new TextView(mContext);

            tvMessage.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            tvMessage.setPaddingRelative(30,40,15,15);

            tvMessage.setText("\u2764 If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");

            layout.addView(tvMessage, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            ratingDialogBuilder.setView(layout);

            ratingDialogBuilder.setTitle("Rate " + APP_TITLE);
            ratingDialogBuilder.setIcon(R.drawable.sun);
            ratingDialogBuilder.setCancelable(false);


                ratingDialogBuilder.setPositiveButton("Rate " + APP_TITLE,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing here because we override this button later to change the close behaviour.
                                //However, we still need this because on older versions of Android unless we
                                //pass a handler the button doesn't get instantiated
                            }
                        });

                ratingDialogBuilder.setNegativeButton("Later",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing here because we override this button later to change the close behaviour.
                                //However, we still need this because on older versions of Android unless we
                                //pass a handler the button doesn't get instantiated
                            }
                        });
                ratingDialogBuilder.setNeutralButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing here because we override this button later to change the close behaviour.
                                //However, we still need this because on older versions of Android unless we
                                //pass a handler the button doesn't get instantiated
                            }
                        });



            final AlertDialog dialog = ratingDialogBuilder.create();

            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                    dialog.dismiss();
                }
            });
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();


                }
            });



            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (editor != null) {
                        editor.putBoolean("dontshowagain", true);
                        editor.commit();
                    }
                    dialog.dismiss();
                }
            });

        }

}
