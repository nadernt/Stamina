package com.fleecast.stamina.notetaking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;

import java.io.File;

/**
 * Created by nnt on 7/05/16.
 */
public class RecorderNoteService extends Service{

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
    private int dbId=0;
    private int idNotification= 125;
   /* private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;*/
   private NotificationManager mNotifyManager;

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
    private NotificationCompat.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), RecorderNoteService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), idNotification, intent, 0);
        return new NotificationCompat.Action.Builder( icon, title, pendingIntent ).build();
    }
    
    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, ActivityAddAudioNote.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_START_RECORD, true);

        intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD,dbId);

        /***********************************************
         * *********************************************
         * For some unspecified reason, extras will be
         * delivered only if we've set some action,
         * for example setAction("foo").
         ***********************************************
         ***********************************************/
        intent.setAction("FakeAction");
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    void CreateNotification() {
        PendingIntent pi = createPendingIntent();

        // Defining notification
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.mic_note_recorder_notifi)
                .setContentTitle("Recording")
                .setContentText("Tap to go to record")
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(pi)
                .setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .addAction(generateAction(R.drawable.ic_stop_record, "",Constants.ACTION_STOP_RECORD));

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyManager.notify(idNotification, nBuilder.build());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(action!=null) {
            stopRecordingByNotification();
        }
        if(intent!=null) {

            if (intent.hasExtra(Constants.EXTRA_NEW_RECORD)) {

                Log.e(LOG_TAG, "EXTRA_NEW_RECORD");

                dbId = myApplication.tmpCurrentAudioNoteInfoStruct.getId();

                myApplication.setCurrentRecordingAudioNoteId(dbId);

                this.mFileDbUniqueToken = intent.getStringExtra(Constants.EXTRA_RECORD_FILENAME);

                //recorderSource = intent.getIntExtra(Constants.EXTRA_RECORD_SOURCE,MediaRecorder.AudioSource.MIC);
                recorderSource = Prefs.getInt(Constants.RECORDER_AUDIO_RECORDER_SOURCE_OPTION,MediaRecorder.AudioSource.MIC);

                recordQuality = Prefs.getInt(Constants.RECORDER_AUDIO_RECORDER_QUALITY_OPTION,Constants.RECORDER_AUDIO_RECORDER_QUALITY_MEDIUM);

                if(recordStatus) {
                        stopRecording();
                }

                try {
                    CreateNotification();
                    startRecording();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "prepare() failed");
                    if(mNotifyManager!=null)
                        mNotifyManager.cancel(idNotification);
                    myApplication.setCurrentRecordingAudioNoteId(Constants.CONST_NULL_ZERO);
                    myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
                    recordStatus = true;
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                    stopSelf();
                }

            }
            else if(intent.hasExtra(Constants.EXTRA_STOP_RECORD)){
                Log.e(LOG_TAG, "STOP RECORDING");
                if(mNotifyManager!=null)
                    mNotifyManager.cancel(idNotification);
                    stopRecording();
            }
            else if(intent.hasExtra(Constants.EXTRA_STOP_RECORD_SERVICE)){
                if(mNotifyManager!=null)
                    mNotifyManager.cancel(idNotification);
                stopSelf();

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
          /*  mRecorder.setAudioSamplingRate(22050);
            mRecorder.setAudioEncodingBitRate(12200);
            //MediaRecorder.getAudioSourceMax();
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);*/
            mRecorder.setAudioSamplingRate(22050);
            mRecorder.setAudioEncodingBitRate(43000);
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
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_NOTE);
            recordStatus = true;
            mRecorder.start();
        }
        else {
            stopService(new Intent(getApplicationContext(), RecorderNoteService.class));
        }

    }

    private void stopRecording()   {
        try {
            if(mNotifyManager!=null)
                mNotifyManager.cancel(idNotification);
            myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
            recordStatus = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            myApplication.setCurrentRecordingAudioNoteId(Constants.CONST_NULL_ZERO);
            sendBroadcastToActivity(Constants.REPORT_RECORDED_FILE_TO_ACTIVITY);
        }
        catch (Exception e){
            if(mNotifyManager!=null)
                mNotifyManager.cancel(idNotification);
            myApplication.setCurrentRecordingAudioNoteId(Constants.CONST_NULL_ZERO);
            sendBroadcastToActivity(Constants.REPORT_RECORD_ERROR_TO_ACTIVITY);
        }
    }

    /**
     *It is similar method stopRecording but reports to
     * activity to make up gui for user after tapping
     * stop in the activity.
     */
    private void stopRecordingByNotification()   {
        if(mNotifyManager!=null)
            mNotifyManager.cancel(idNotification);
            stopRecording();
            //sendBroadcastToActivity(Constants.REPORT_RECORD_STOPPED_BY_NOTIFICATION_TO_ACTIVITY);
        /*Intent intent = new Intent(this, ActivityAddAudioNote.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_START_RECORD, true);*/

        Intent intent = new Intent(this, ActivityAddAudioNote.class);
        intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD,dbId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRecorder!=null) {
            mRecorder.release();
            mRecorder = null;
        }

    }

    /****************************************
     ******** Getter Setters section ********
     ****************************************/

    public boolean isRecording() {
        return recordStatus;
    }

    public void sendBroadcastToActivity(int messageToActivity) {

        Intent intent = new Intent(Constants.INTENTFILTER_RECORD_SERVICE);

        switch (messageToActivity){

            case Constants.REPORT_RECORD_ERROR_TO_ACTIVITY:

                try {

                    File file = new File(recordFileName);

                    if (file.exists()) {
                        file.delete();
                    }

                    intent.putExtra(Constants.EXTRA_RECORD_SERVICE_REPORTS, messageToActivity);
                }
                catch (Exception e) {
                    if(mNotifyManager!=null)
                        mNotifyManager.cancel(idNotification);
                    Log.e("Error:", e.getMessage());
                }
                break;

            case Constants.REPORT_RECORDED_FILE_TO_ACTIVITY:
                intent.putExtra(Constants.EXTRA_RECORD_SERVICE_REPORTS, messageToActivity);
                intent.putExtra(Constants.REPORT_RECORDED_FILE_TO_ACTIVITY_FILENAME, recordFileName);
                break;
            case Constants.REPORT_RECORD_STOPPED_BY_NOTIFICATION_TO_ACTIVITY:
           //     EXTRA_EDIT_NOTE_AND_RECORD
/*
                intent.putExtra(Constants.EXTRA_RECORD_SERVICE_REPORTS, messageToActivity);

                intent.putExtra(Constants.REPORT_RECORDED_FILE_TO_ACTIVITY_FILENAME, recordFileName);
*/
                break;
        }

        broadcaster.sendBroadcast(intent);

        stopSelf();
    }
}
