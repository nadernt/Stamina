package com.fleecast.stamina.utility;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.fleecast.stamina.R;

import java.io.File;

/**
 * Created by nnt on 7/05/16.
 */
public class ExternalStorageManager {

    public static boolean isExternalStorageWritable(){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Minimum available storage calculation.
     *
     * @return
     */
    public static boolean isThereEnoughSpaceOnStorage() {

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

        long bytesAvailable;

        if(Build.VERSION.SDK_INT >= 18 ){
            bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
        }
        else{
             bytesAvailable = stat.getBlockSize() * stat.getBlockCount();

        }

        long megAvailable = bytesAvailable / 1048576;
        System.out.println("Megs :" + megAvailable);
        if(megAvailable > Constants.MINIMUM_AVAILABLE_STORAGE_SPACE)
            return true;
        else
        return false;

    }

    // ---------------------------------------------------------------
    // + <static> FUNCTIONS
    // ---------------------------------------------------------------

    /**
     * @return True if the external storage is available.
     * False otherwise.
     */
    public static boolean checkAvailable() {

        // Retrieving the external storage state
        String state = Environment.getExternalStorageState();

        // Check if available
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }

        return false;
    }

    /**
     * @return True if the external storage is writable.
     * False otherwise.
     */
    public static boolean checkWritable() {

        // Retrieving the external storage state
        String state = Environment.getExternalStorageState();

        // Check if writable
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;

    }

    public static String prepareWorkingDirectory(Context context){

        if(ExternalStorageManager.checkAvailable()) {

            String pathToWorkingDirectory = Prefs.getString(Constants.WORKING_DIRECTORY_PATH, "");

            // if user didn't choose any custom directory or it is first time.
            if (pathToWorkingDirectory.length() == 0) {
                pathToWorkingDirectory = Environment.getExternalStorageDirectory().getPath() + Constants.WORKING_DIRECTORY_NAME;
                File directory = new File(pathToWorkingDirectory);
                directory.mkdirs();
            } else{
                pathToWorkingDirectory = pathToWorkingDirectory + Constants.WORKING_DIRECTORY_NAME;
        }

            Log.e("DDD",pathToWorkingDirectory);
                return pathToWorkingDirectory;
        }
        else
        {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Note:")
                    .setIcon(R.drawable.ic_action_info)
                    .setMessage("The storage device is not accessible now!").show();


        }
        return "";
    }

    public static String getWorkingDirectory() {

        String pathToWorkingDirectory = Prefs.getString(Constants.WORKING_DIRECTORY_PATH, "");

        // if user didn't choose any custom directory or it is first time.
        if (pathToWorkingDirectory.length() == 0) {
            pathToWorkingDirectory = Environment.getExternalStorageDirectory().getPath() + Constants.WORKING_DIRECTORY_NAME;
            File directory = new File(pathToWorkingDirectory);
            directory.mkdirs();
        } else{
            pathToWorkingDirectory = pathToWorkingDirectory + Constants.WORKING_DIRECTORY_NAME;

        }

        Log.e("KKKKKKKKKKKK",pathToWorkingDirectory);
        return pathToWorkingDirectory;

    }

    public static String makeRecodingDirectory(String mFileDbUniqueToken){

        String pathToRecordingDirectory = getWorkingDirectory() + File.separator +  mFileDbUniqueToken;

            File directory = new File(pathToRecordingDirectory);
            directory.mkdirs();

        return pathToRecordingDirectory;

    }
}
