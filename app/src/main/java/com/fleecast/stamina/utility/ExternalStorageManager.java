package com.fleecast.stamina.utility;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.fleecast.stamina.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nnt on 7/05/16.
 */
public class ExternalStorageManager {

    public static boolean isExternalStorageWritable(){

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Minimum available storage calculation.
     *
     * @return
     */
    public static boolean isThereEnoughSpaceOnStorage() {

        String pathToWorkingDirectory = Prefs.getString(Constants.PREF_WORKING_DIRECTORY_PATH, "");

        // Check in the working directory is there enough empty space
        StatFs stat = new StatFs(pathToWorkingDirectory);

        long bytesAvailable;

        if(Build.VERSION.SDK_INT >= 18 ){
            bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
        }
        else{
             bytesAvailable = stat.getBlockSize() * stat.getBlockCount();

        }

        long megAvailable = bytesAvailable / 1048576;
        System.out.println("Megs :" + megAvailable);
        return megAvailable > Constants.MINIMUM_AVAILABLE_STORAGE_SPACE;

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
        return Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);

    }

    /**
     * @return True if the external storage is writable.
     * False otherwise.
     */
    public static boolean checkWritable() {

        // Retrieving the external storage state
        String state = Environment.getExternalStorageState();

        // Check if writable
        return Environment.MEDIA_MOUNTED.equals(state);

    }

    public static String prepareWorkingDirectory(Context context){

        if(ExternalStorageManager.checkAvailable()) {

            String pathToWorkingDirectory = Prefs.getString(Constants.PREF_WORKING_DIRECTORY_PATH, "");
            File f = new File(pathToWorkingDirectory + Constants.CONST_WORKING_DIRECTORY_NAME);
            // if user didn't choose any custom directory or it is first time.
            if (!f.exists()) {
                f.mkdirs();
            }
                pathToWorkingDirectory = pathToWorkingDirectory + Constants.CONST_WORKING_DIRECTORY_NAME;


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

    public static void ifWorkingDirIsNotExitMakeIt(Context context){

        if(ExternalStorageManager.checkAvailable()) {

            String pathToWorkingDirectory = Prefs.getString(Constants.PREF_WORKING_DIRECTORY_PATH, "");
            File f = new File(pathToWorkingDirectory + Constants.CONST_WORKING_DIRECTORY_NAME);
            // if user didn't choose any custom directory or it is first time.
            if (!f.exists()) {
                f.mkdirs();
            }

        }
        else
        {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Note:")
                    .setIcon(R.drawable.ic_action_info)
                    .setMessage("The storage device is not accessible now!").show();


        }
    }

    public static String getWorkingDirectory() {

        String pathToWorkingDirectory = Prefs.getString(Constants.PREF_WORKING_DIRECTORY_PATH, "");

        // if user didn't choose any custom directory or it is first time.
        if (pathToWorkingDirectory.length() == 0) {
            pathToWorkingDirectory = Environment.getExternalStorageDirectory().getPath() + Constants.CONST_WORKING_DIRECTORY_NAME;
            File directory = new File(pathToWorkingDirectory);
            directory.mkdirs();
        } else{
            pathToWorkingDirectory = pathToWorkingDirectory + Constants.CONST_WORKING_DIRECTORY_NAME;

        }

        //Log.e("KKKKKKKKKKKK",pathToWorkingDirectory);

        return pathToWorkingDirectory;

    }


    public static String getTempWorkingDirectory() {

        String pathToRecordingDirectory = getWorkingDirectory() + File.separator +  Constants.TEMP_FOLDER_NAME;

        File directory = new File(pathToRecordingDirectory);

        if(!directory.exists())
            directory.mkdirs();

        return pathToRecordingDirectory;
    }

    public static String getTrashDirectory() {

        String pathToTrashDirectory = getWorkingDirectory() + File.separator +  Constants.CONST_RECYCLEBIN_DIRECTORY_NAME;

        File directory = new File(pathToTrashDirectory);

        if(!directory.exists())
            directory.mkdirs();

        return pathToTrashDirectory;

    }

    public static String makeRecodingDirectory(String mFileDbUniqueToken){

        String pathToRecordingDirectory = getWorkingDirectory() + File.separator +  mFileDbUniqueToken;

            File directory = new File(pathToRecordingDirectory);

        if(!directory.exists())
             directory.mkdirs();

        return pathToRecordingDirectory;

    }

    public static String getPathToAudioFilesFolderById(String dbId) {

        String pathToRecordingDirectory = getWorkingDirectory() + File.separator +  dbId;

        return pathToRecordingDirectory;

    }

    // If targetLocation does not exist, it will be created.
    private static void copyDirectory(File sourceLocation , File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
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

}
