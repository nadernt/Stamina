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

import java.io.File;

/**
 * Created by nnt on 7/05/16.
 */
public class Recorder extends Service{

    private static final String LOG_TAG = "AudioRecordService";
    private int recorderSource;
    private MediaRecorder mRecorder = null;
    private MyApplication myApplication;
    private boolean recordStatus = false;
    private boolean playStatus = false;
    private String mFileDbUniqueToken ="";
    private String currentRecordingDir;
    private String recordFileName;
    private int recordQuality;
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication =  (MyApplication) getApplicationContext();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null) {

            if (intent.hasExtra(Constants.EXTRA_NEW_RECORD)) {
                Log.e(LOG_TAG, "EXTRA_NEW_RECORD");

                this.mFileDbUniqueToken = intent.getStringExtra(Constants.EXTRA_RECORD_FILENAME);

                //recorderSource = intent.getIntExtra(Constants.EXTRA_RECORD_SOURCE,MediaRecorder.AudioSource.MIC);
                recorderSource = Prefs.getInt(Constants.RECORDER_AUDIO_RECORDER_SOURCE_OPTION,MediaRecorder.AudioSource.MIC);

                recordQuality = Prefs.getInt(Constants.RECORDER_AUDIO_RECORDER_QUALITY_OPTION,Constants.RECORDER_AUDIO_RECORDER_QUALITY_MEDIUM);

                if(recordStatus) {
                        stopRecording();
                }

                try {
                    startRecording();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "prepare() failed");
                    myApplication.setIsRecordUnderGoing(false);
                    recordStatus = true;
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                    stopService(new Intent(getApplicationContext(), Recorder.class));
                }

            }
            else if(intent.hasExtra(Constants.EXTRA_STOP_RECORD)){
                Log.e(LOG_TAG, "STOP RECORDING");

                    stopRecording();
            }
            else if(intent.hasExtra(Constants.EXTRA_STOP_SERVICE)){

                stopService(new Intent(getApplicationContext(), Recorder.class));

            }

        }


        return super.onStartCommand(intent, flags, startId);

    }

    private void startRecording() throws Exception {

        mRecorder = new MediaRecorder();

        mRecorder.setAudioSource(recorderSource);



        // low quality record
        if(recordQuality == Constants.RECORDER_AUDIO_RECORDER_QUALITY_LOW)
        {
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
        else if(recordQuality == Constants.RECORDER_AUDIO_RECORDER_QUALITY_MEDIUM){ // default quality
            mRecorder.setAudioSamplingRate(22050);
            mRecorder.setAudioEncodingBitRate(12200);
            //MediaRecorder.getAudioSourceMax();
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }
        else if(recordQuality == Constants.RECORDER_AUDIO_RECORDER_QUALITY_HIGH) // high quality record
        {
           /* mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
            //mRecorder.setAudioEncoder(MediaRecorder.getMaxAmplitude());

            mRecorder.setAudioEncodingBitRate(16);
            mRecorder.setAudioSamplingRate(44100);
*/
            //mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            /*mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setAudioChannels(2);
            mRecorder.setAudioEncodingBitRate(128);
            mRecorder.setAudioSamplingRate(44100);*/
            /*mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(160 * 1024);
            mRecorder.setAudioChannels(2);*/
                mRecorder.setAudioSamplingRate(44100);
                mRecorder.setAudioEncodingBitRate(96000);
                MediaRecorder.getAudioSourceMax();
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        }

        if(ExternalStorageManager.checkWritable())
        {
           currentRecordingDir =  ExternalStorageManager.makeRecodingDirectory(mFileDbUniqueToken);

            // Creating unique id for postfix (it is unix timestamp so easy can be converted to time).
            String filePostfixToken = String.valueOf ((int) (System.currentTimeMillis() / 1000));

            recordFileName = currentRecordingDir + File.separator + mFileDbUniqueToken + Constants.CONST_SEPARATOR_OF_AUDIO_FILE + filePostfixToken ;

            mRecorder.setOutputFile(recordFileName);
            mRecorder.prepare();
            myApplication.setIsRecordUnderGoing(true);
            recordStatus = true;
            mRecorder.start();
        }
        else {
            stopService(new Intent(getApplicationContext(), Recorder.class));
        }

    }

    private void stopRecording()   {
        try {
            myApplication.setIsRecordUnderGoing(false);
            recordStatus = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            sendBroadcast(Constants.REPORT_RECORDED_FILE_TO_ACTIVITY);
        }
        catch (Exception e){
            sendBroadcast(Constants.REPORT_RECORD_ERROR_TO_ACTIVITY);
        }
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

   /* public void recordMedia(boolean start_stop,int mediaRecorderSource) {
        this.recorderSource = mediaRecorderSource;
        if (start_stop) {

          *//*  if(mPlayer!=null)
                playMedia(false);*//*

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
    }*/

/*
    public void playMedia(boolean start_stop) {
        if (start_stop) {

            if(mRecorder!=null)
                recordMedia(false, recorderSource);

            playStatus= true;

        } else {
            playStatus= false;
            //stopPlaying();
        }
    }
*/

  /*  private void startPlaying() {

        Intent intent = new Intent(this,Player.class);
        intent.putExtra("file_name", mFileDbUniqueToken);
        startActivity(intent);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRecorder!=null) {
            mRecorder.release();
            mRecorder = null;
        }

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
            myApplication.setIsRecordUnderGoing(true);
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            myApplication.setIsRecordUnderGoing(false);
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

    public void sendBroadcast(int messageToActivity) {

        Intent intent = new Intent(Constants.INTENTFILTER_RECORD_SERVICE);

        switch (messageToActivity){

            case Constants.REPORT_RECORD_ERROR_TO_ACTIVITY:

                File file = new File(recordFileName);

                if (file.exists()) {
                    file.delete();
                }

                intent.putExtra(Constants.EXTRA_RECORD_SERVICE_MESSAGES, messageToActivity);

                break;

            case Constants.REPORT_RECORDED_FILE_TO_ACTIVITY:
                intent.putExtra(Constants.EXTRA_RECORD_SERVICE_MESSAGES, messageToActivity);
                intent.putExtra(Constants.REPORT_RECORDED_FILE_TO_ACTIVITY_FILENAME, recordFileName);
                break;
        }

        broadcaster.sendBroadcast(intent);
    }
}
