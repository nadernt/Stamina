package com.fleecast.stamina.utility;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;

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

            if (ExternalStorageManager.isThereEnoughSpaceOnStorage()) {

                String pathToWorkingDirectory =  Prefs.getString("StoragePath", "");

                // if user didn't choose any custom directory.
                if(pathToWorkingDirectory.length()==0) {
                    pathToWorkingDirectory = Environment.getExternalStorageDirectory().getPath() + Constants.WORKING_DIRECTORY;
                    File directory = new File(pathToWorkingDirectory);
                    directory.mkdirs();
                 //   Log.e("DBG", "Sex ");

                }
                return pathToWorkingDirectory;
               // Log.e("DBG", "External " + pathToWorkingDirectory);


            } else {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Note:")
                        .setIcon(R.drawable.ic_action_info)
                        .setMessage("The storage enough space on device!\nYou can just take the note.").show();

            }
        }
        else
        {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Note:")
                    .setIcon(R.drawable.ic_action_info)
                    .setMessage("The storage device is not available now!\nYou can just take the note.").show();


        }
        return "";
    }
}
