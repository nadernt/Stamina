package com.fleecast.stamina.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private final static String NON_THIN = "[^iIl1\\.,']";

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

    private static int textWidth(String str) {
        return str.length() - str.replaceAll(NON_THIN, "").length() / 2;
    }

    public static String ellipsize(String text, int max) {

        if(text==null)
            return "";

        if (textWidth(text) <= max)
            return text;

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1)
            return text.substring(0, max-3) + "...";

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
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

    public static void showMessage(CharSequence messageToUser, String titleOfDialog, Context context){

        new AlertDialog.Builder(context)
                .setTitle(titleOfDialog)
                .setMessage(messageToUser)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
    public static void showMessage(CharSequence messageToUser, String titleOfDialog, @DrawableRes int iconId, Context context){

        new AlertDialog.Builder(context)
                .setTitle(titleOfDialog)
                .setMessage(messageToUser)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(iconId)
                .show();

    }
    public static void showMessage(CharSequence messageToUser, String titleOfDialog, Context context,boolean showIcon,String confirmText){

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titleOfDialog);
                if(titleOfDialog!=null)
                    dialog.setMessage(messageToUser);
            dialog.setPositiveButton(confirmText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            if(showIcon)
                dialog.setIcon(android.R.drawable.ic_dialog_alert);
                dialog.show();

    }
    public static void snackMaker(View view,String userMessage , String txtUserAction,int actionColor,int timeLast){

        Snackbar snackbar = Snackbar.make(view, userMessage, timeLast)
                .setAction(txtUserAction, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

        ColoredSnackBar.info(snackbar);
        snackbar.setActionTextColor(actionColor);
        snackbar.show();
    }

    public static String unixTimeToReadable(long unixSeconds){

        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        // E, dd MMM yyyy HH:mm:ss z
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy hh:mm:ss a"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        //System.out.println(formattedDate);
        return formattedDate;

    }


    public static String getContactName(Context context, String phoneNumber) {

        if(phoneNumber==null)
           return "";
        else
            Log.e("DBG", phoneNumber);

        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        if(contactName==null)
            return phoneNumber;
        else
            return contactName;
    }

    public static int getFilePostFixId(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(file_name.lastIndexOf("_") + 1));
    }

    /**
     *
     * @param fileOrDirectory
     * @param pathToSkip if null then deletes the parent directory
     */
    public static void deleteRecursive(File fileOrDirectory,File pathToSkip) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child,pathToSkip);
            }
        }
        if((pathToSkip==null) || (fileOrDirectory.compareTo(pathToSkip)!=0))
            fileOrDirectory.delete();
    }

    public static int getDbIdFromFileName(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(file_name.lastIndexOf("_") + 1));
    }

    public static void copyDirectory(File sourceLocation , File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static boolean copyAssetFolder(AssetManager assetManager,
                                           String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains("."))
                    res &= copyAsset(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                else
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyAsset(AssetManager assetManager,
                                     String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public  static String convertNewLineCharToBrHtml(String input){
        return input.replaceAll("(\r\n|\n)", "<br />");
    }
    public static String calculateCallDuration(Date startDate, Date endDate){

        long diff = endDate.getTime() - startDate.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);

        String toReturn="";


        if(diffHours==0)
            toReturn += "00";
        else if(diffHours<10)
            toReturn += "0"+ String.valueOf(diffHours);
        else
            toReturn += String.valueOf(diffHours);

        toReturn += ":";

        if(diffMinutes==0)
            toReturn += "00";
        else if(diffMinutes<10)
            toReturn += "0"+ String.valueOf(diffMinutes);
        else
            toReturn += String.valueOf(diffMinutes);

        toReturn += ":";

        if(diffSeconds==0)
            toReturn += "00";
        else if(diffSeconds<10)
            toReturn += "0"+ String.valueOf(diffSeconds);
        else
            toReturn += String.valueOf(diffSeconds);


        return toReturn;

    }

    public static Spanned fromHTMLVersionCompat(String stringToHtml, int HtmlVersionFlag){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(stringToHtml,HtmlVersionFlag);
        } else {
            return Html.fromHtml(stringToHtml);
        }
    }

}
