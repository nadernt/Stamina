package com.fleecast.stamina.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nader on 2/7/2016.
 */
public class Utility {

    public static String LogTag = "MyLogTag";
    public static String EXTRA_MSG = "extra_msg";

    private static final String SCHEME = "package";

    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

    private static final String APP_PKG_NAME_22 = "pkg";

    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    public float getSystemIndependentPixel(Context context , int pixel){

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, context.getResources().getDisplayMetrics());

    }


    public static String wordEllipsizeMaker(String input,int maxCharacters,int maxCharacterWithEllipsis) {

        if (input.length() < maxCharacters) {
            return input;
        }

        else if (input.length() > maxCharacters) {
            String strDotsAfter = "";
            for (int i = 1; i < maxCharacterWithEllipsis - maxCharacters; i++) {
                strDotsAfter += ".";
            }
            return input.substring(0, maxCharacters) + strDotsAfter;
        }
        else
            return "";
    }

    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    public boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isSystemApp(ApplicationInfo info) {
        return (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    /**
     * Downsamples the icon to the custom quality.
     *
     * @param image
     * @param renderQuality
     * @param filterAlias
     * @return
     */

    public Drawable resizeIcon(Drawable image,int renderQuality,boolean filterAlias) {

        Bitmap mBitmap = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(mBitmap, renderQuality,renderQuality, filterAlias);
       /* if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap=null;
        }*/
        return new BitmapDrawable(Resources.getSystem(), bitmapResized);
    }

    public String getJustNumberOfPhone(String strNumber){

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(strNumber);
        String returnedNumber="";
        while (m.find()) {
            returnedNumber += m.group();
        }

        return returnedNumber;

    }
   /* public Date getCurrentTime()
    {
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        Date now = new Date();
        String strDate = dateformat.format(now);
        System.out.println(strDate);

        return now;
    }*/
}
