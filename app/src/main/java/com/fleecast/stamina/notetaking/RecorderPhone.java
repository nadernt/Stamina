package com.fleecast.stamina.notetaking;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;

import java.io.File;

/**
 * Created by nnt on 7/05/16.
 */
public class RecorderPhone {

    private static final String LOG_TAG = "AudioRecordTest";
    private final Context context;
    private int mediaRecorderSource;

    //private Chronometer mChronometer;
    private MediaRecorder mRecorder = null;

    private MyApplication myApplication;

   // private boolean recordStatus = false;
   // private boolean playStatus = false;
    private String mFileName = "";

    public RecorderPhone(Context context, String workingDirectory, String mFileName) {
        this.mFileName = workingDirectory + File.separator + mFileName;
        this.context = context;

        myApplication = (MyApplication) context.getApplicationContext();

    }

    public void recordMedia(boolean start_stop, int mediaRecorderSource) {
        this.mediaRecorderSource = mediaRecorderSource;
        if (start_stop) {

           // recordStatus = true;
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE);

            startRecording();
        } else {
           // recordStatus = false;
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
            stopRecording();
        }
    }

   /* public void playMedia(boolean start_stop) {
        if (start_stop) {

            if (mRecorder != null)
                recordMedia(false, mediaRecorderSource);

            //playStatus = true;
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE);

            startPlaying();
        } else {
            //playStatus = false;
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);

        }
    }

    private void startPlaying() {

        Intent intent = new Intent(context, ActivityPlayerPortrait.class);
        intent.putExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER, mFileName);
        context.startActivity(intent);

    }*/


    private void startRecording() {

        Log.e(LOG_TAG, "Rec Init");
        myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(mediaRecorderSource);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE);
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void stopRecording() {
        myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
    }


    /****************************************
     * ******* Getter Setters section ********
     ****************************************/

    /*public boolean isRecording() {
        return recordStatus;
    }

    public void setRecordStatus(boolean recordStatus) {
        this.recordStatus = recordStatus;
    }

    public boolean isPlaying() {
        return playStatus;
    }

    public void setPlayStatus(boolean playStatus) {
        this.playStatus = playStatus;
    }*/

}
