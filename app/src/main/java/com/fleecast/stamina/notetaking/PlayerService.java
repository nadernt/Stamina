package com.fleecast.stamina.notetaking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Prefs;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener, MusicFocusable {
    private MyApplication myApplication;

    private Context mContext;
    private MediaPlayer mPlayer;
    private LocalBroadcastManager broadcaster;
    boolean reinitForNewItemOrResume = true;
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat mController;
    private NotificationManager mNotifyManager;
    private int idNotification =1234;
    private boolean areEventsInitiated = false;
    private NotificationCompat.Builder mBuilder;
    AudioFocusHelper mAudioFocusHelper = null;
    private int lastStateBeforeLoseAudioFocuse =-1;
    private boolean playRequestIsFromPortraitePlayer=false;

    // do we have audio focus?
    enum AudioFocus {
        NoFocusNoDuck,    // we don't have audio focus, and can't duck
        NoFocusCanDuck,   // we don't have focus, but can play at a low volume ("ducking")
        Focused           // we have full audio focus
    }
    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = (MyApplication)getApplicationContext();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

  /*  public static PlayerService getInstance() {
        if (sInstance == null) {
            sInstance = new PlayerService();
        }
        return sInstance;
    }*/

    private NotificationCompat.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( this, PlayerService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), idNotification, intent, 0);
        return new NotificationCompat.Action.Builder( icon, title, pendingIntent ).build();
    }

    private PendingIntent createPendingIntent() {
        Intent intent;

        if(playRequestIsFromPortraitePlayer)
            intent = new Intent(this, ActivityPlayerPortrait.class);
        else
            intent = new Intent(this, ActivityRecordsPlayList.class);

        intent.setAction(Constants.ACTION_SHOW_PLAYER_NO_NEW);

        return PendingIntent.getActivity(this, 0, intent, 0);
    }
    private void buildNotification( NotificationCompat.Action action ) {
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setMediaSession( mMediaSession.getSessionToken() );
        //new NotificationCompat.BigTextStyle().bigText(aVeryLongString)
        //style.setShowCancelButton(true);

        mBuilder = new NotificationCompat.Builder( this );

        mBuilder.setStyle(style);

        if(Prefs.getBoolean(Constants.PREF_SHOW_PLAYER_FULL_NOTIFICATION,false))
            mBuilder.setPriority(Notification.PRIORITY_MAX);

        String mTitle =  myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getTitle();

        if(mTitle==null)
            mTitle = "No note";

        mBuilder.setContentTitle(mTitle);


        String mDescription = myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getDescription();

        if(mDescription==null)
            mDescription = "";

        mBuilder.setContentText(mDescription);

        //mBuilder.
        mBuilder.setSmallIcon(R.drawable.audio_wave);
        mBuilder.setShowWhen(false);


        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setOngoing(true);
        mBuilder.setContentIntent(createPendingIntent());
        //mBuilder.setDeleteIntent(pendingIntent);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.audio_wave);
        mBuilder.setLargeIcon(drawableToBitmap(drawable));


        mBuilder.addAction( generateAction( R.drawable.ic_action_playback_prev, "Previous", Constants.ACTION_REWIND ) );
        mBuilder.addAction( action );
        mBuilder.addAction( generateAction( R.drawable.ic_action_playback_next, "Next", Constants.ACTION_NEXT ) );
        mBuilder.addAction( generateAction( R.drawable.ic_action_cancel, "Close", Constants.ACTION_STOP ) );
        style.setShowActionsInCompactView(0,1,2,3);


        mNotifyManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        mNotifyManager.notify( idNotification, mBuilder.build());
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


        String action = intent.getAction();
        if(action!=null) {
            if (action.equals(Constants.ACTION_PLAY)) playRequest();
            else if (action.equals(Constants.ACTION_PAUSE)) pauseRequest();
            else if (action.equals(Constants.ACTION_NEXT)) nextRequest();
            else if (action.equals(Constants.ACTION_STOP)) stopRequest(true);
            else if (action.equals(Constants.ACTION_REWIND)) rewindRequest();

        }


        if(intent!=null) {
            if (intent.hasExtra(Constants.EXTRA_SEEK_TO)) requestSeekTo(intent);
            else if (intent.hasExtra(Constants.EXTRA_UPDATE_SEEKBAR)) updateSeekBarRequest();
            else if (intent.hasExtra(Constants.EXTRA_PLAY_NEW_SESSION)) {
                if(intent.hasExtra(Constants.EXTRA_PLAY_REQUEST_ISـFROM_PORTRATE_PLAYER))
                    playRequestIsFromPortraitePlayer = true;
                else
                    playRequestIsFromPortraitePlayer = false;
                preparePlaylist();
            }

        }


        return START_NOT_STICKY; // Means we started the service, but don't want it to
    }

    private void killServiceRequest() {

        if(mPlayer !=null)
        {
            mPlayer.stop();
            mPlayer = null;
        }

        stopForeground(true);


        myApplication.setCurrentMediaPosition(0);
        myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_NOT_ALIVE);

        myApplication.setIsPlaying(false);

        reinitForNewItemOrResume =true;

        sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_TRACK_FINISHED);

        mMediaSession.release();
        mMediaSessionManager=null;

        if(mNotifyManager!=null)
            mNotifyManager.cancel(idNotification);

        stopSelf();
    }

    private void preparePlaylist() {

        if(mPlayer !=null)
        {
            mPlayer.stop();
            mPlayer = null;
        }


        reinitForNewItemOrResume =true;

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
        sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_TRACK_FINISHED);
        myApplication.setIsPlaying(false);
        reinitForNewItemOrResume =true;

        if (myApplication.getIndexSomethingIsPlaying()== myApplication.stackPlaylist.size()-1) {
            Log.e("DDDDDDDd","SOmraya");
            stopRequest(true);
        }
        else
        {
            Log.e("DDDDDDDd","Momra");
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying()+1);
            playRequest();
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        giveUpAudioFocus();
        Toast.makeText(this,"Error in media format!",Toast.LENGTH_LONG).show();
        stopRequest(true);
        return false;
    }

    private void addForegroundService(){

        startForeground(idNotification, mBuilder.build());

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // 4. Play music
        mPlayer.start();
        myApplication.setIsPlaying(true);
        reinitForNewItemOrResume =false;
        myApplication.setMediaDuration(mPlayer.getDuration());
        myApplication.setCurrentMediaPosition(0);
        myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_PLAYING);
        sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PLAYING);
        addForegroundService();
    }

    private void playRequest() {
        if( mMediaSessionManager == null ) {
            initMediaSessions();
        }

        try {
            if (mPlayer == null || !areEventsInitiated) {
                if (mPlayer == null)
                    mPlayer = new MediaPlayer();
                mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mPlayer.setOnPreparedListener(this);
                mPlayer.setOnCompletionListener(this);
                mPlayer.setOnErrorListener(this);
                areEventsInitiated = true;
            }

            if (reinitForNewItemOrResume) {

                    mPlayer.reset();
/*
                    Log.e("HHHHHHHHHHHHH", "" + myApplication.getIndexSomethingIsPlaying());
*/
                    mPlayer.setDataSource(myApplication.stackPlaylist.get(myApplication.getIndexSomethingIsPlaying()).getFileName());
                    mPlayer.prepareAsync();
            } else {
                mPlayer.start();
                sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PLAYING);
                myApplication.setIsPlaying(true);
                myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_PLAYING);
            }
            mController.getTransportControls().play();

            tryToGetAudioFocus();
        }
        catch(Exception e){
            Toast.makeText(this,"Error in media format!\n" + e.getMessage(),Toast.LENGTH_LONG).show();
            stopRequest(true);
        }

    }

    private void pauseRequest() {
        mController.getTransportControls().pause();
        sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PAUSE);
        mPlayer.pause();
        myApplication.setIsPlaying(false);
        myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_PAUSED);
    }

    private void stopRequest(boolean stopAndKillTheService) {
        if(mPlayer !=null)
        {
            mPlayer.stop();
            mPlayer = null;
        }

        myApplication.setCurrentMediaPosition(0);

        myApplication.setIsPlaying(false);

        myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_STOPPED);

        reinitForNewItemOrResume =true;

        giveUpAudioFocus();

        // If yes close the remote control after finish the play list.
        if (Prefs.getBoolean(Constants.PREF_ON_FINISH_PLAYLIST_CLOSE_PLAYER_REMOTE,false) || stopAndKillTheService)
            killServiceRequest();
        else
            mController.getTransportControls().play();

    }

    private void nextRequest() {

        if (myApplication.getIndexSomethingIsPlaying() < myApplication.stackPlaylist.size()-1) {
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying() + 1);
        }

        myApplication.setIsPlaying(false);
        reinitForNewItemOrResume =true;

        playRequest();
    }

    private void rewindRequest() {

        if (myApplication.getIndexSomethingIsPlaying() > 0 )
        {
            myApplication.setIndexSomethingIsPlaying(myApplication.getIndexSomethingIsPlaying()-1);
        }

        myApplication.setIsPlaying(false);
        reinitForNewItemOrResume =true;

        playRequest();
    }

    public void sendBroadcastToActivity(int messageToActivity) {

        Intent intent = new Intent(Constants.INTENTFILTER_PLAYER_SERVICE);
        intent.putExtra(Constants.EXTRA_PLAYER_SERVICE_PLAY_STATUS, messageToActivity);
        broadcaster.sendBroadcast(intent);

    }

    private void initMediaSessions() {

        try {
            mPlayer = new MediaPlayer();
            mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(), this);

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
                                          buildNotification( generateAction( R.drawable.ic_action_playback_pause, "Pause", Constants.ACTION_PAUSE ) );
                                      }

                                      @Override
                                      public void onPause() {
                                          super.onPause();
                                          buildNotification(generateAction(R.drawable.ic_action_playback_play, "Play",  Constants.ACTION_PLAY));
                                      }

                                      @Override
                                      public void onSkipToNext() {
                                          super.onSkipToNext();
                                          buildNotification( generateAction( R.drawable.ic_action_playback_next, "Next",  Constants.ACTION_NEXT ) );
                                      }

                                      @Override
                                      public void onSkipToPrevious() {
                                          super.onSkipToPrevious();
                                          buildNotification( generateAction( R.drawable.ic_action_playback_prev, "Previous",  Constants.ACTION_REWIND ) );
                                      }

                                      @Override
                                      public void onFastForward() {
                                          super.onFastForward();
                                      }

                                      @Override
                                      public void onRewind() {
                                          super.onRewind();
                                      }

                                      @Override
                                      public void onStop() {
                                          super.onStop();
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

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onGainedAudioFocus() {
        /*if(myApplication.isPlaying())
            Toast.makeText(getApplicationContext(), "Gained audio focus.", Toast.LENGTH_SHORT).show();*/
        mAudioFocus = AudioFocus.Focused;

        //Check if before losing focus
        if(lastStateBeforeLoseAudioFocuse ==Constants.CONST_PLAY_SERVICE_STATE_PLAYING)
            configAndStartMediaPlayer();
    }

    @Override
    public void onLostAudioFocus(boolean canDuck) {
/*
        if(myApplication.isPlaying())
            Toast.makeText(getApplicationContext(), "lost audio focus." + (canDuck ? "can duck" :
                "no duck"), Toast.LENGTH_SHORT).show();
*/
        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;
        lastStateBeforeLoseAudioFocuse = myApplication.getPlayerServiceCurrentState();

        // start/restart/pause media player with new focus settings
        if (mPlayer != null && mPlayer.isPlaying())
            configAndStartMediaPlayer();
    }


    /**
     * Reconfigures MediaPlayer according to audio focus settings and starts/restarts it. This
     * method starts/restarts the MediaPlayer respecting the current audio focus state. So if
     * we have focus, it will play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is allowed by the
     * current focus settings. This method assumes mPlayer != null, so if you are calling it,
     * you have to do so from a context where you are sure this is the case.
     */
    void configAndStartMediaPlayer() {
        if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
            // If we don't have audio focus and can't duck, we have to pause, even if mState
            // is State.Playing. But we stay in the Playing state so that we know we have to resume
            // playback once we get the focus back.
            if (mPlayer.isPlaying()) pauseRequest();
            return;
        }
        else if (mAudioFocus == AudioFocus.NoFocusCanDuck) {
            mPlayer.setVolume(Constants.DUCK_VOLUME, Constants.DUCK_VOLUME);  // we'll be relatively quiet
            Log.e("DBG","DUCK");
        }
        else {
            mPlayer.setVolume(1.0f, 1.0f); // we can be loud
            Log.e("DBG","Aloud");
        }

        if (!mPlayer.isPlaying()) playRequest();
    }


    void giveUpAudioFocus() {
        if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.abandonFocus())
            mAudioFocus = AudioFocus.NoFocusNoDuck;
    }

    void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.requestFocus())
            mAudioFocus = AudioFocus.Focused;

    }
}
