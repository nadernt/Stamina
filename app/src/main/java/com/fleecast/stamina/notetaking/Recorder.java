package com.fleecast.stamina.notetaking;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import com.fleecast.stamina.chathead.MyApplication;

import java.io.File;

/**
 * Created by nnt on 7/05/16.
 */
public class Recorder {

    private static final String LOG_TAG = "AudioRecordTest";
    private final Context context;
    private View viewTimer;
    private int mediaRecorderSource;

    private Chronometer mChronometer;
    private MediaRecorder mRecorder = null;

    private MyApplication myApplication;

    private boolean recordStatus = false;
    private boolean playStatus = false;
    private String mFileName="";

    public Recorder(Context context, View viewTimer,String workingDirectory, String mFileName){

        this.viewTimer = viewTimer;
        this.mFileName = workingDirectory + File.separator + mFileName;
        mChronometer = (Chronometer)viewTimer;
        this.context = context;

        myApplication =  (MyApplication)context.getApplicationContext();

    }

    public Recorder(Context context,String workingDirectory, String mFileName){
        this.mFileName = workingDirectory + File.separator + mFileName;
        this.context = context;

        myApplication =  (MyApplication)context.getApplicationContext();

    }

    public void recordMedia(boolean start_stop,int mediaRecorderSource) {
        this.mediaRecorderSource = mediaRecorderSource;
        if (start_stop) {

          /*  if(mPlayer!=null)
                playMedia(false);*/

            recordStatus = true;

            startRecording();
        } else {
            recordStatus = false;
            stopRecording();
        }
    }

    public void playMedia(boolean start_stop) {
        if (start_stop) {

            if(mRecorder!=null)
                recordMedia(false,mediaRecorderSource);

            playStatus= true;

            startPlaying();
        } else {
            playStatus= false;
            //stopPlaying();
        }
    }

    private void startPlaying() {

        Intent intent = new Intent(context,Player.class);
        intent.putExtra("file_name", mFileName);
        context.startActivity(intent);

/*

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
*/
    }


    private void stopPlaying() {
       // mPlayer.release();
       // mPlayer = null;

    }

    private void startRecording() {

        Log.e(LOG_TAG, "Rec Init");
/*        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
        recorder.setAudioEncodingBitRate(16);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(path);
        recorder.prepare();
        recorder.start();*/
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(mediaRecorderSource);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            myApplication.setIsRecordIsUnderGoing(true);
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            myApplication.setIsRecordIsUnderGoing(false);
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    private void stopRecording() {
        myApplication.setIsRecordIsUnderGoing(false);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


    public void startTimer()
    {
        mChronometer.setVisibility(View.VISIBLE);
       mChronometer.start();
    }

    public void stopTimer()
    {
        mChronometer.stop();
    }

    public void resetTimer()
    {
        mChronometer.setBase(SystemClock.elapsedRealtime());
    }

    public void formatTimer()
    {
        mChronometer.setFormat("Formatted time (%s)");
    }

    public void clearTimer()
    {
        mChronometer.setFormat(null);
    }


    /****************************************
     ******** Getter Setters section ********
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

  /*  public int getPlayerProgress(){

        return mPlayer.getCurrentPosition();
    }

    public int getPlayFileDuration(){

        return mPlayer.getDuration();
    }*/

}
