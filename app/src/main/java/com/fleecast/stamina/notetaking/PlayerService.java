package com.fleecast.stamina.notetaking;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;

import java.io.IOException;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener {
    private static final int START_OF_PLAYLIST = 0;
    private MyApplication myApplication;
    private static final String TAG = "Player Service";
    private static PlayerService sInstance;

    private static final String CMD_NAME = "command";
    private static final String CMD_PAUSE = "pause";
    private static final String CMD_STOP = "pause";
    private static final String CMD_PLAY = "play";

    // Jellybean
    private static String SERVICE_CMD = "com.sec.android.app.music.musicservicecommand";
    private static String PAUSE_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.pause";
    private static String PLAY_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.play";
    //private boolean resumingFromPauseToPlay=false;

    // Honeycomb
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            SERVICE_CMD = "com.android.music.musicservicecommand";
            PAUSE_SERVICE_CMD = "com.android.music.musicservicecommand.pause";
            PLAY_SERVICE_CMD = "com.android.music.musicservicecommand.play";
        }
    };

    private Context mContext;
    private boolean mAudioFocusGranted = false;
    private MediaPlayer mPlayer;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private BroadcastReceiver mIntentReceiver;
    private boolean mReceiverRegistered = false;
    private LocalBroadcastManager broadcaster;
    boolean reinitForNewItem = true;

    @Override
    public void onCreate() {
        super.onCreate();

        myApplication = (MyApplication)getApplicationContext();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public static PlayerService getInstance() {
        if (sInstance == null) {
            sInstance = new PlayerService();
        }
        return sInstance;
    }


    /**
     * Called when we receive an Intent. When we receive an intent sent to us via startService(),
     * this is the method that gets called. So here we react appropriately depending on the
     * Intent's action, which specifies what is being requested of us.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            String action = intent.getAction();
            if(action!=null) {
                if (action.equals(Constants.ACTION_PLAY)) playRequest();
                else if (action.equals(Constants.ACTION_PAUSE)) pauseRequest();
                else if (action.equals(Constants.ACTION_NEXT)) nextRequest();
                else if (action.equals(Constants.ACTION_STOP)) stopRequest();
                else if (action.equals(Constants.ACTION_REWIND)) rewindRequest();
            }
                if(intent!=null) {



          if (intent.hasExtra(Constants.EXTRA_SEEK_TO)) requestSeekTo(intent);
            else if (intent.hasExtra(Constants.EXTRA_UPDATE_SEEKBAR)) requestUpdateSeekBar();
            else if (intent.hasExtra(Constants.EXTRA_PLAY_NEW_SESSION)) preparePlaylist();
        }

        return START_NOT_STICKY; // Means we started the service, but don't want it to
        // restart in case it's killed.
    }

    private void preparePlaylist() {

        if(mPlayer !=null)
        {
            mPlayer.stop();
            mPlayer = null;
        }


            reinitForNewItem=true;
            abandonAudioFocus();
            myApplication.setCurrentMediaPosition(0);
            myApplication.setIsPlaying(false);

        playRequest();

        /*private void stopRequest() {
            // 1. Stop play back
            if (mAudioFocusGranted) {

                //sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_FINISHED);
                mPlayer.stop();
                mPlayer = null;
                myApplication.setCurrentMediaPosition(0);
                //mAudioIsPlaying = false;
                myApplication.setIsPlaying(false);
                reinitForNewItem=true;
                // 2. Give up audio focus
                abandonAudioFocus();
            }
        }*/

    }

    private void requestUpdateSeekBar() {
        myApplication.setCurrentMediaPosition(mPlayer.getCurrentPosition());
        sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_SEEK_BAR_UPDATED);

    }

    private void requestSeekTo(Intent intent) {
        int mediaPosition = intent.getIntExtra(Constants.EXTRA_SEEK_TO,0);
        myApplication.setCurrentMediaPosition(mediaPosition);
        mPlayer.seekTo(mediaPosition);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("DBG", "C");
        sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_FINISHED);
        myApplication.setIsPlaying(false);
        reinitForNewItem=true;

        if (myApplication.getIndexSomethingIsPlaying()== myApplication.stackPlaylist.size()-1)
        {
            stopRequest();
        }
        else {
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying()+1);
            //mAudioIsPlaying=false;
            playRequest();
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // 4. Play music
        mPlayer.start();
        myApplication.setIsPlaying(false);
        reinitForNewItem=false;
        myApplication.setMediaDuration(mPlayer.getDuration());
        myApplication.setCurrentMediaPosition(0);
        sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PLAYING);
        //mAudioIsPlaying = true;
    }

    private void playRequest() {

            if (mPlayer == null) {
                Log.e("DBG", "mPlayer == null");

                mPlayer = new MediaPlayer();
                mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mPlayer.setOnPreparedListener(this);
                mPlayer.setOnCompletionListener(this);
                mPlayer.setOnErrorListener(this);
                //myApplication.setIndexSomethingIsPlaying(START_OF_PLAYLIST);
            }

            if(reinitForNewItem) {

                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getFileName());
                    mPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else
            {
                mPlayer.start();
                sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PLAYING);
            }

          //      mAudioIsPlaying=true;

            // 1. Acquire audio focus
            if (!mAudioFocusGranted && requestAudioFocus()) {
                // 2. Kill off any other play back sources
                forceMusicStop();
                // 3. Register broadcast receiver for player intents
                setupBroadcastReceiver();
            }


      //  }
    }

    private void pauseRequest() {
        // 1. Suspend play back
        if (mAudioFocusGranted) {
            Log.e("DBG", "pauseRequest");
            sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PAUSE);
            mPlayer.pause();
            myApplication.setIsPlaying(true);
            //mAudioIsPlaying = false;
        }
    }

    private void stopRequest() {
        // 1. Stop play back
        if (mAudioFocusGranted) {

            //sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_FINISHED);
            mPlayer.stop();
            mPlayer = null;
            myApplication.setCurrentMediaPosition(0);
            //mAudioIsPlaying = false;
            myApplication.setIsPlaying(false);
            reinitForNewItem=true;
            // 2. Give up audio focus
            abandonAudioFocus();
        }
    }

    private void nextRequest() {

        if (myApplication.getIndexSomethingIsPlaying() < myApplication.stackPlaylist.size()-1) {
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying() + 1);
        }
        //stopRequest();
        //sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_FINISHED);

        myApplication.setIsPlaying(false);
        reinitForNewItem=true;
        //mAudioIsPlaying=false;

        playRequest();
    }

    private void rewindRequest() {

        if (myApplication.getIndexSomethingIsPlaying() > 0 )
        {
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying()-1);
        }

        //sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_FINISHED);

        myApplication.setIsPlaying(false);
        reinitForNewItem=true;

//        mAudioIsPlaying=false;

        playRequest();
    }

 /*   private void preparePlaylist() {
        Log.e("GGGGGGGGGGG","I am run");
*//*
        if (action.equals(Constants.ACTION_PLAY_FILE)){


        }
        else if (action.equals(Constants.ACTION_PLAY_FOLDER)) {


        }*//*

    }*/


    public void sendBroadcastToActivity(int messageToActivity) {

        Intent intent = new Intent(Constants.INTENTFILTER_PLAYER_SERVICE);
        intent.putExtra(Constants.EXTRA_PLAYER_SERVICE_PLAY_STATUS, messageToActivity);
        broadcaster.sendBroadcast(intent);

    }


    public PlayerService() {
        mContext = this;

        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        Log.i(TAG, "AUDIOFOCUS_GAIN");
                        playRequest();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT");
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        Log.e(TAG, "AUDIOFOCUS_LOSS");
                        pauseRequest();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                        pauseRequest();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                    case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                        Log.e(TAG, "AUDIOFOCUS_REQUEST_FAILED");
                        break;
                    default:
                        //
                }
            }
        };
    }


    private boolean requestAudioFocus() {
        if (!mAudioFocusGranted) {
            AudioManager am = (AudioManager) mContext
                    .getSystemService(Context.AUDIO_SERVICE);
            // Request audio focus for play back
            int result = am.requestAudioFocus(mOnAudioFocusChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusGranted = true;
            } else {
                // FAILED
                Log.e(TAG,
                        ">>>>>>>>>>>>> FAILED TO GET AUDIO FOCUS <<<<<<<<<<<<<<<<<<<<<<<<");
            }
        }
        return mAudioFocusGranted;
    }


    private void abandonAudioFocus() {
        AudioManager am = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        int result = am.abandonAudioFocus(mOnAudioFocusChangeListener);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocusGranted = false;
        } else {
            // FAILED
            Log.e(TAG,">>>>>>>>>>>>> FAILED TO ABANDON AUDIO FOCUS <<<<<<<<<<<<<<<<<<<<<<<<");
        }
        mOnAudioFocusChangeListener = null;
    }

    private void setupBroadcastReceiver() {
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String cmd = intent.getStringExtra(CMD_NAME);
                Log.i(TAG, "mIntentReceiver.onReceive " + action + " / " + cmd);

                if (PAUSE_SERVICE_CMD.equals(action)
                        || (SERVICE_CMD.equals(action) && CMD_PAUSE.equals(cmd))) {
                    pauseRequest();
                }

                if (PLAY_SERVICE_CMD.equals(action)
                        || (SERVICE_CMD.equals(action) && CMD_PLAY.equals(cmd))) {
                    pauseRequest();
                }
            }
        };

        // Do the right thing when something else tries to play
        if (!mReceiverRegistered) {
            IntentFilter commandFilter = new IntentFilter();
            commandFilter.addAction(SERVICE_CMD);
            commandFilter.addAction(PAUSE_SERVICE_CMD);
            commandFilter.addAction(PLAY_SERVICE_CMD);
            mContext.registerReceiver(mIntentReceiver, commandFilter);
            mReceiverRegistered = true;
        }
    }

    private void forceMusicStop() {
        AudioManager am = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        if (am.isMusicActive()) {
            Intent intentToStop = new Intent(SERVICE_CMD);
            intentToStop.putExtra(CMD_NAME, CMD_STOP);
            mContext.sendBroadcast(intentToStop);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


   /* @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
            Log.i("Bangiula" , "onBufferingUpdate() " + mp.getCurrentPosition());
            int progress = mp.getCurrentPosition();

    }*/
}
