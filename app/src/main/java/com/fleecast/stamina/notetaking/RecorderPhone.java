package com.fleecast.stamina.notetaking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.content.ContextCompat;
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
    private MediaRecorder mRecorder = null;
    private MyApplication myApplication;
    private String mFileName = "";
    boolean isRecordStarted=false;

    public RecorderPhone(Context context, String workingDirectory, String mFileName) {
        this.mFileName = workingDirectory + File.separator + mFileName;
      //  Log.e(LOG_TAG,"Output file name: " +mFileName);
        this.context = context;

        myApplication = (MyApplication) context.getApplicationContext();
        isRecordStarted=false;
    }

    public void recordMedia(boolean start_stop, int mediaRecorderSource) {
        this.mediaRecorderSource = mediaRecorderSource;
        if (start_stop) {

            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE);

            startRecording();
        } else {
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
            stopRecording();
        }
    }

    private void startRecording() {
// Assume thisActivity is the current activity
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        }
        //if(permissionCheck)
        Log.e(LOG_TAG, "Rec Init");
        //  myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(mediaRecorderSource);
        //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioSamplingRate(22050);
        mRecorder.setAudioEncodingBitRate(43000);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(mFileName);


        try {
            mRecorder.prepare();
               Thread.sleep(1000);
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE);
            mRecorder.start();
            isRecordStarted=true;
        } catch (Exception e) {
            isRecordStarted=false;
            //System.out.println(e.getMessage() + " ssssssssssss");
            Log.e(LOG_TAG, "prepare() failed");
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void stopRecording() {
        if(mRecorder!=null && isRecordStarted) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
    }

}