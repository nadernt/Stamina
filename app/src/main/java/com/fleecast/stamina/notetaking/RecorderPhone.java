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

    private boolean recordStatus = false;
    private boolean playStatus = false;
    private String mFileName = "";

    public RecorderPhone(Context context, String workingDirectory, String mFileName) {
        this.mFileName = workingDirectory + File.separator + mFileName;
        this.context = context;

        myApplication = (MyApplication) context.getApplicationContext();

    }

    public void recordMedia(boolean start_stop, int mediaRecorderSource) {
        this.mediaRecorderSource = mediaRecorderSource;
        if (start_stop) {

            recordStatus = true;

            startRecording();
        } else {
            recordStatus = false;
            stopRecording();
        }
    }

    public void playMedia(boolean start_stop) {
        if (start_stop) {

            if (mRecorder != null)
                recordMedia(false, mediaRecorderSource);

            playStatus = true;

            startPlaying();
        } else {
            playStatus = false;
        }
    }

    private void startPlaying() {

        Intent intent = new Intent(context, ActivityPlayerPortrait.class);
        intent.putExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER, mFileName);
        context.startActivity(intent);

    }


    private void startRecording() {

        Log.e(LOG_TAG, "Rec Init");

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(mediaRecorderSource);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            myApplication.setIsRecordUnderGoing(true);
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            myApplication.setIsRecordUnderGoing(false);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void stopRecording() {
        myApplication.setIsRecordUnderGoing(false);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


    /****************************************
     * ******* Getter Setters section ********
     ****************************************/

    public boolean isRecording() {
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
    }

}
