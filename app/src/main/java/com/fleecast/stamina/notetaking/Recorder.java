package com.fleecast.stamina.notetaking;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;

/**
 * Created by nnt on 7/05/16.
 */
public class Recorder extends Service{

    private static final String LOG_TAG = "AudioRecordTest";
    private int recorderSource;
    private MediaRecorder mRecorder = null;
    private MyApplication myApplication;
    private boolean recordStatus = false;
    private boolean playStatus = false;
    private String mFileDbUniqueToken ="";
    private String currentRecordingDir;
    private String recordFileName;
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        myApplication =  (MyApplication) getApplicationContext();

        if(intent!=null) {

            if (intent.hasExtra(Constants.EXTRA_NEW_RECORD)) {

                this.mFileDbUniqueToken = intent.getStringExtra(Constants.EXTRA_RECORD_FILENAME);

                recorderSource = intent.getIntExtra(Constants.EXTRA_RECORD_SOURCE,MediaRecorder.AudioSource.MIC);

                if(recordStatus)
                    stopRecording();

                try {
                    startRecording();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "prepare() failed");
                    myApplication.setIsRecordIsUnderGoing(false);
                    recordStatus = true;
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                    stopService(new Intent(getApplicationContext(), Recorder.class));
                }

            }
            else if(intent.hasExtra(Constants.EXTRA_STOP_RECORD)){

                stopRecording();


            }

        }

        return null;
    }

    private void startRecording() throws Exception {

        mRecorder = new MediaRecorder();

        mRecorder.setAudioSource(recorderSource);

        int recordQuality = Prefs.getInt(Constants.RECORDER_AUDIO_RECORDER_QUALITY_OPTION,Constants.RECORDER_AUDIO_RECORDER_QUALITY_MEDIUM);

        // low quality record
        if(recordQuality == Constants.RECORDER_AUDIO_RECORDER_QUALITY_LOW)
        {
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
            mRecorder.setAudioEncodingBitRate(16);
            mRecorder.setAudioSamplingRate(44100);
        }// high quality recor
        else if(recordQuality == Constants.RECORDER_AUDIO_RECORDER_QUALITY_HIGH){
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
            mRecorder.setAudioEncodingBitRate(16);
            mRecorder.setAudioSamplingRate(44100);
        }
        else // default quality
        {
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }


        if(ExternalStorageManager.checkWritable())
        {
           currentRecordingDir =  ExternalStorageManager.makeRecodingDirectory(mFileDbUniqueToken);

            // Creating unique id for postfix
            String filePostfixToken = String.valueOf ((int) (System.currentTimeMillis() / 1000));

            recordFileName = filePostfixToken + Constants.CONST_SEPARATOR_OF_AUDIO_FILE + currentRecordingDir;

            mRecorder.setOutputFile(recordFileName);
            mRecorder.prepare();
            myApplication.setIsRecordIsUnderGoing(true);
            recordStatus = true;
            mRecorder.start();
        }
        else {
            stopService(new Intent(getApplicationContext(), Recorder.class));
        }

    }

    private void stopRecording() {
        myApplication.setIsRecordIsUnderGoing(false);
        recordStatus = false;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    static final public String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";

    static final public String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if(message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    /*public Recorder(Context context, View viewTimer,String workingDirectory, String mFileDbUniqueToken){

        this.viewTimer = viewTimer;
        this.mFileDbUniqueToken = workingDirectory + File.separator + mFileDbUniqueToken;
        //mChronometer = (Chronometer)viewTimer;
        this.context = context;

        myApplication =  (MyApplication)context.getApplicationContext();

    }
*/

   /* public Recorder(Context context,String workingDirectory, String mFileDbUniqueToken){
        this.mFileDbUniqueToken = workingDirectory + File.separator + mFileDbUniqueToken;
        this.context = context;

        myApplication =  (MyApplication)context.getApplicationContext();

    }*/

    public void recordMedia(boolean start_stop,int mediaRecorderSource) {
        this.recorderSource = mediaRecorderSource;
        if (start_stop) {

          /*  if(mPlayer!=null)
                playMedia(false);*/

            recordStatus = true;

            try {
                startRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            recordStatus = false;
            stopRecording();
        }
    }

    public void playMedia(boolean start_stop) {
        if (start_stop) {

            if(mRecorder!=null)
                recordMedia(false, recorderSource);

            playStatus= true;

            startPlaying();
        } else {
            playStatus= false;
            //stopPlaying();
        }
    }

    private void startPlaying() {

        Intent intent = new Intent(this,Player.class);
        intent.putExtra("file_name", mFileDbUniqueToken);
        startActivity(intent);
    }


    private void stopPlaying() {
       // mPlayer.release();
       // mPlayer = null;

    }

  /*  private void startRecording() {

        Log.e(LOG_TAG, "Rec Init");
*//*        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
        recorder.setAudioEncodingBitRate(16);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(path);
        recorder.prepare();
        recorder.start();*//*
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(recorderSource);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileDbUniqueToken);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            myApplication.setIsRecordIsUnderGoing(true);
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            myApplication.setIsRecordIsUnderGoing(false);
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }*/



   /* public void startTimer()
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
*/

    /****************************************
     ******** Getter Setters section ********
     ****************************************/

    public boolean isRecording() {
        return recordStatus;
    }
}
