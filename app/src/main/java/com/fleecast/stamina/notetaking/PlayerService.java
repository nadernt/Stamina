package com.fleecast.stamina.notetaking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.fleecast.stamina.R;
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
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private int idNotification =1;
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
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat mController;

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

    private NotificationCompat.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), PlayerService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), idNotification, intent, 0);
        return new NotificationCompat.Action.Builder( icon, title, pendingIntent ).build();
    }

    private void buildNotification( NotificationCompat.Action action ) {
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setMediaSession( mMediaSession.getSessionToken() );
        style.setShowCancelButton(true);

        mMediaSession.setActive(true);

        Intent intent = new Intent( getApplicationContext(), PlayerService.class );
        intent.setAction( Constants.ACTION_STOP );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), idNotification, intent, 0);
        mBuilder = new NotificationCompat.Builder( this );

        String mTitle =  myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getTitle();

        if(mTitle==null)
            mTitle = "No note";

        mBuilder.setContentTitle(mTitle);

/*        String sb = " Notifications can include an animated progress indicator that shows users the status of an ongoing operation. If you can estimate how long the operation takes and how much of it is complete at any time, use the \"determinate\" form of the indicator (a progress bar). If you can't estimate the length of the operation, use the \"indeterminate\" form of the indicator (an activity indicator)." +
                "Progress indicators are displayed with the platform's implementation of the ProgressBar class." +
                "To use a progress indicator, call setProgress(). The determinate and indeterminate forms are described in the following sections.";*/
        String mDescription = myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getDescription();

        if(mDescription==null)
            mDescription = "";

        mBuilder.setContentText(mDescription);

        //mBuilder.
        mBuilder.setSmallIcon(R.drawable.audio_wave);
        mBuilder.setShowWhen(false);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.audio_wave ,options);
        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.audio_wave);
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.audio_wave);
        mBuilder.setLargeIcon(drawableToBitmap(drawable));
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setOngoing(true);
        mBuilder.setDeleteIntent(pendingIntent);
        mBuilder.setStyle(style);



        mBuilder.addAction( generateAction( R.drawable.ic_action_playback_prev, "Previous", Constants.ACTION_REWIND ) );
        mBuilder.addAction( action );
        mBuilder.addAction( generateAction( R.drawable.ic_action_playback_next, "Next", Constants.ACTION_NEXT ) );
        mBuilder.addAction( generateAction( R.drawable.ic_action_cancel, "Close", Constants.ACTION_CLOSE ) );
        style.setShowActionsInCompactView(0,1,2,3);

        mNotifyManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        mNotifyManager.notify( idNotification, mBuilder.build() );
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Called when we receive an Intent. When we receive an intent sent to us via startService(),
     * this is the method that gets called. So here we react appropriately depending on the
     * Intent's action, which specifies what is being requested of us.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if( mMediaSessionManager == null ) {
            initMediaSessions();
        }

        String action = intent.getAction();
            if(action!=null) {
                if (action.equals(Constants.ACTION_PLAY)) playRequest();
                else if (action.equals(Constants.ACTION_PAUSE)) pauseRequest();
                else if (action.equals(Constants.ACTION_NEXT)) nextRequest();
                else if (action.equals(Constants.ACTION_STOP)) stopRequest();
                else if (action.equals(Constants.ACTION_REWIND)) rewindRequest();
                else if (action.equals(Constants.ACTION_CLOSE)) killServiceRequest();
                else if (action.equals(Constants.ACTION_SHOW_PLAYER)) showPlayerRequest();

            }
                if(intent!=null) {



          if (intent.hasExtra(Constants.EXTRA_SEEK_TO)) requestSeekTo(intent);
            else if (intent.hasExtra(Constants.EXTRA_UPDATE_SEEKBAR)) updateSeekBarRequest();
            else if (intent.hasExtra(Constants.EXTRA_PLAY_NEW_SESSION)) preparePlaylist();

        }


        return START_NOT_STICKY; // Means we started the service, but don't want it to
        // restart in case it's killed.
    }

    private void showPlayerRequest() {

        Intent intent = new Intent(this,ActivityPlayerPortrait.class);
        startActivity(intent);

    }

    private void killServiceRequest() {

        sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_FINISHED);
        stopRequest();
        mContext.unregisterReceiver(mIntentReceiver);
        mNotifyManager.cancelAll();
        mMediaSession.release();
        mMediaSessionManager=null;
        stopSelf();
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

    }

    private void updateSeekBarRequest() {
      if(mPlayer!=null) {
          // mBuilder.setProgress(mPlayer.getDuration(), mPlayer.getCurrentPosition(), false);
          // Displays the progress bar for the first time.
          //mNotifyManager.notify(idNotification, mBuilder.build());

          myApplication.setCurrentMediaPosition(mPlayer.getCurrentPosition());
          sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_SEEK_BAR_UPDATED);
      }
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
                    mController.getTransportControls().play();

            // 1. Acquire audio focus
            if (!mAudioFocusGranted && requestAudioFocus()) {
                // 2. Kill off any other play back sources
                forceMusicStop();
                // 3. Register broadcast receiver for player intents
                setupBroadcastReceiver();
            }

    }

    private void pauseRequest() {
        // 1. Suspend play back
        if (mAudioFocusGranted) {
            mController.getTransportControls().pause();
            Log.e("DBG", "pauseRequest");
            sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PAUSE);
            mPlayer.pause();
            myApplication.setIsPlaying(true);
        }
    }

    private void stopRequest() {

        // 1. Stop play back
        if (mAudioFocusGranted) {

            if(mPlayer !=null)
            {
                mPlayer.stop();
                mPlayer = null;
            }

            myApplication.setCurrentMediaPosition(0);

            myApplication.setIsPlaying(false);

            reinitForNewItem=true;

            Log.e("DBG", "Chatanog");

            mController.getTransportControls().pause();

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

    public void sendBroadcastToActivity(int messageToActivity) {

        Intent intent = new Intent(Constants.INTENTFILTER_PLAYER_SERVICE);
        intent.putExtra(Constants.EXTRA_PLAYER_SERVICE_PLAY_STATUS, messageToActivity);
        broadcaster.sendBroadcast(intent);

    }

    private void initMediaSessions() {
       /* m_objMediaPlayer = new MediaPlayer();
        m_objMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        m_objMediaSession = new MediaSession(getApplicationContext(), "sample session");
        m_objMediaController = new MediaController(getApplicationContext(), m_objMediaSession.getSessionToken());*/

        try {
            mPlayer = new MediaPlayer();
            mMediaSession = new MediaSessionCompat(getApplicationContext(), "stamina player session");
            mMediaSessionManager = (MediaSessionManager) mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
            mController =new MediaControllerCompat(getApplicationContext(), mMediaSession.getSessionToken());
            mMediaSession.setActive(true);
            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mMediaSession.setCallback(new MediaSessionCompat.Callback(){
                                 @Override
                                 public void onPlay() {
                                     super.onPlay();
                                     Log.e( "MediaPlayerService", "onPlay");
                                     buildNotification( generateAction( R.drawable.ic_action_playback_pause, "Pause", Constants.ACTION_PAUSE ) );
                                 }

                                 @Override
                                 public void onPause() {
                                     super.onPause();
                                     Log.e( "MediaPlayerService", "onPause");
                                     buildNotification(generateAction(R.drawable.ic_action_playback_play, "Play",  Constants.ACTION_PLAY));
                                 }

                                 @Override
                                 public void onSkipToNext() {
                                     super.onSkipToNext();
                                     Log.e( "MediaPlayerService", "onSkipToNext");
                                     //Change media here
                                     buildNotification( generateAction( R.drawable.ic_action_playback_next, "Next",  Constants.ACTION_NEXT ) );
                                 }

                                 @Override
                                 public void onSkipToPrevious() {
                                     super.onSkipToPrevious();
                                     Log.e( "MediaPlayerService", "onSkipToPrevious");
                                     //Change media here
                                     buildNotification( generateAction( R.drawable.ic_action_playback_prev, "Previous",  Constants.ACTION_REWIND ) );
                                 }

                                 @Override
                                 public void onFastForward() {
                                     super.onFastForward();
                                     Log.e( "MediaPlayerService", "onFastForward");
                                     //Manipulate current media here
                                 }

                                 @Override
                                 public void onRewind() {
                                     super.onRewind();
                                     Log.e( "MediaPlayerService", "onRewind");
                                     //Manipulate current media here
                                 }

                                 @Override
                                 public void onStop() {
                                     super.onStop();
                                     Log.e( "MediaPlayerService", "onStop");
                                     //Stop media player here
                                     sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_FINISHED);
                                     stopRequest();
                                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                     notificationManager.cancel( 1 );
                                     Intent intent = new Intent( getApplicationContext(), PlayerService.class );
                                     stopService( intent );
                                 }

                                 @Override
                                 public void onSeekTo(long pos) {
                                     super.onSeekTo(pos);
                                 }


                             }
        );
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

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaSession.release();
        return super.onUnbind(intent);
    }

   /* @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
            Log.i("Bangiula" , "onBufferingUpdate() " + mp.getCurrentPosition());
            int progress = mp.getCurrentPosition();

    }*/
}
