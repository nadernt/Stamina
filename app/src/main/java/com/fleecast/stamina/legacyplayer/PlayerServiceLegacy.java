/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fleecast.stamina.legacyplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;

import java.io.IOException;

public class PlayerServiceLegacy extends Service implements OnCompletionListener, OnPreparedListener,
        OnErrorListener, MusicFocusableLegacy,
        PrepareMusicRetrieverTask.MusicRetrieverPreparedListener {

    // The tag we put on debug messages
    final static String TAG = "LegacyMusicPlayer";


    // The volume we set the media player to when we lose audio focus, but are allowed to reduce
    // the volume instead of stopping playback.
    public static final float DUCK_VOLUME = 0.1f;

    // our media player
    static MediaPlayer mPlayer = null;

    // our AudioFocusHelperLegacy object, if it's available (it's available on SDK level >= 8)
    // If not available, this will be null. Always check for null before using!
    AudioFocusHelperLegacy mAudioFocusHelperLegacy = null;
    private MusicRetriever mRetriever;
    private MyApplication myApplication;
    private Handler handler;
    private boolean playRequestIsFromPhonePlayer =false;

    // indicates the state our service:
    enum State {
        Retrieving, // the MediaRetriever is retrieving music
        Stopped,    // media player is stopped and not prepared to play
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!). (but the media player may actually be
                    // paused in this state if we don't have audio focus. But we stay in this state
                    // so that we know we have to resume playback once we get focus back)
        Paused      // playback paused (media player ready!)
    };

    State mState = State.Retrieving;

    // if in Retrieving mode, this flag indicates whether we should start playing immediately
    // when we are ready or not.
    boolean mStartPlayingAfterRetrieve = false;

    // if mStartPlayingAfterRetrieve is true, this variable indicates the URL that we should
    // start playing when we are ready. If null, we should play a random song from the device
    Uri mWhatToPlayAfterRetrieve = null;

    enum PauseReason {
        UserRequest,  // paused by user request
        FocusLoss,    // paused because of audio focus loss
    };

    // why did we pause? (only relevant if mState == State.Paused)
    PauseReason mPauseReason = PauseReason.UserRequest;

    // do we have audio focus?
    enum AudioFocus {
        NoFocusNoDuck,    // we don't have audio focus, and can't duck
        NoFocusCanDuck,   // we don't have focus, but can play at a low volume ("ducking")
        Focused           // we have full audio focus
    }
    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;

    // title of the song we are currently playing
    String mSongTitle = "";

    // whether the song we are playing is streaming from the network
    boolean mIsStreaming = false;

    // Wifi lock that we hold when streaming files from the internet, in order to prevent the
    // device from shutting off the Wifi radio
    WifiLock mWifiLock;

    // The ID we use for the notification (the onscreen alert that appears at the notification
    // area at the top of the screen as an icon -- and as text as well if the user expands the
    // notification area).
    final int NOTIFICATION_ID = 1;


    // our RemoteControlClient object, which will use remote control APIs available in
    // SDK level >= 14, if they're available.
    RemoteControlClientCompat mRemoteControlClientCompat;

    // Dummy album art we will pass to the remote control (if the APIs are available).
    Bitmap mDummyAlbumArt;

    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;

    AudioManager mAudioManager;
    NotificationManager mNotificationManager;

    Notification.Builder mNotificationBuilder = null;
    private LocalBroadcastManager broadcaster;

    /**
     * Makes sure the media player exists and has been reset. This will create the media player
     * if needed, or reset the existing media player if one already exists.
     */
    void createMediaPlayerIfNeeded() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();

            // Make sure the media player will acquire a wake-lock while playing. If we don't do
            // that, the CPU might go to sleep while the song is playing, causing playback to stop.
            //
            // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
            // permission in AndroidManifest.xml.
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing, and when it's done
            // playing:
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        }
        else
            mPlayer.reset();
    }
    public void sendBroadcastToActivity(int messageToActivity) {

        Intent intent = new Intent(Constants.INTENTFILTER_PLAYER_SERVICE);
        intent.putExtra(Constants.EXTRA_PLAYER_SERVICE_PLAY_STATUS, messageToActivity);
        broadcaster.sendBroadcast(intent);

    }
    @Override
    public void onCreate() {
        Log.i(TAG, "debug: Creating service");
        myApplication = (MyApplication)getApplicationContext();
        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        broadcaster = LocalBroadcastManager.getInstance(this);

        // Create the retriever and start an asynchronous task that will prepare it.
        mRetriever = new MusicRetriever(this);
        (new PrepareMusicRetrieverTask(mRetriever,this)).execute();


        // create the Audio Focus Helper, if the Audio Focus feature is available (SDK 8 or above)
        if (android.os.Build.VERSION.SDK_INT >= 8)
            mAudioFocusHelperLegacy = new AudioFocusHelperLegacy(getApplicationContext(), this);
        else
            mAudioFocus = AudioFocus.Focused; // no focus feature, so we always "have" audio focus

        mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_music_1);

        mMediaButtonReceiverComponent = new ComponentName(this, MusicIntentReceiver.class);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(Constants.ACTION_TOGGLE_PLAYBACK_LEGACY)) processTogglePlaybackRequest();
        else if (action.equals(Constants.ACTION_PLAY_LEGACY)){

            if(intent.hasExtra(Constants.EXTRA_PLAY_REQUEST_ISـFROM_PORTRATE_PLAYER))
                playRequestIsFromPhonePlayer = true;
            else
                playRequestIsFromPhonePlayer = false;

            //mState = State.Stopped;

            processPlayRequest();
        }
        else if (action.equals(Constants.ACTION_PAUSE_LEGACY)) processPauseRequest();
        else if (action.equals(Constants.ACTION_SKIP_LEGACY)) processSkipRequest();
        else if (action.equals(Constants.ACTION_STOP_LEGACY)) processStopRequest();
        else if (action.equals(Constants.ACTION_REWIND_LEGACY)) processRewindRequest();
        //else if (action.equals(Constants.ACTION_S)) processSeekToRequest();
       // else if (action.equals(ACTION_URL)) processAddRequest(intent);

        return START_NOT_STICKY; // Means we started the service, but don't want it to
                                 // restart in case it's killed.
    }

    public static void processSeekToRequest(int seekTo) {
        //if (mState == State.Playing || mState == State.Paused)
            mPlayer.seekTo(seekTo);
    }

    void processTogglePlaybackRequest() {
        if (mState == State.Paused || mState == State.Stopped) {
            processPlayRequest();
        } else {
            processPauseRequest();
        }
    }

    void processPlayRequest() {

        if (mState == State.Retrieving) {
            // If we are still retrieving media, just set the flag to start playing when we're
            // ready
            mWhatToPlayAfterRetrieve = null; // play a random song
            mStartPlayingAfterRetrieve = true;
            return;
        }

        tryToGetAudioFocus();

        // actually play the song

        if (mState == State.Stopped) {
            // If we're stopped, just go ahead to the next song and start playing
            playSong();
        }
        else if (mState == State.Paused) {

            // If we're paused, just continue playback and restore the 'foreground service' state.
            mState = State.Playing;
            setUpAsForeground(mSongTitle + " (playing)");
            configAndStartMediaPlayer();
        }

        // Tell any remote controls that our playback state is 'playing'.
        if (mRemoteControlClientCompat != null) {
            mRemoteControlClientCompat
                    .setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
        }
    }

    void processPauseRequest() {

        if (mState == State.Retrieving) {
            // If we are still retrieving media, clear the flag that indicates we should start
            // playing when we're ready
            mStartPlayingAfterRetrieve = false;
            return;
        }

        if (mState == State.Playing) {
// Pause media player and cancel the 'foreground service' state.
            mState = State.Paused;

            mPlayer.pause();
            myApplication.setIsPlaying(false);
            myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_PAUSED);
            relaxResources(false); // while paused, we always retain the MediaPlayer

            // do not give up audio focus
        }

        // Tell any remote controls that our playback state is 'paused'.
        if (mRemoteControlClientCompat != null) {
            mRemoteControlClientCompat
                    .setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
        }
    }

    void processRewindRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            tryToGetAudioFocus();
            mRetriever.getPrevItem();
            playSong();
        }
    }

    void processSkipRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            tryToGetAudioFocus();
            mRetriever.getNextItem();
            playSong();
        }
    }

    void processStopRequest() {
        processStopRequest(false);

    }


    void processStopRequest(boolean force) {
        if (mState == State.Playing || mState == State.Paused || force) {

            sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_CLOSE_PLAYER);
            mState = State.Stopped;

            // let go of all resources...
            relaxResources(true);
            giveUpAudioFocus();

            // Tell any remote controls that our playback state is 'paused'.
            if (mRemoteControlClientCompat != null) {
                mRemoteControlClientCompat
                        .setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
            }

            myApplication.setCurrentMediaPosition(0);
            myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_NOT_ALIVE);

            myApplication.setIsPlaying(false);

            // service is no longer necessary. Will be started again if needed.
            stopSelf();
        }
    }

    void relaxResources(boolean releaseMediaPlayer) {


        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mPlayer != null) {
            // stop being a foreground service
            stopForeground(true);
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }

        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) mWifiLock.release();
    }

    void giveUpAudioFocus() {
        if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelperLegacy != null
                                && mAudioFocusHelperLegacy.abandonFocus())
            mAudioFocus = AudioFocus.NoFocusNoDuck;
    }


    void configAndStartMediaPlayer() {
        if (mAudioFocus == AudioFocus.NoFocusNoDuck) {

            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                myApplication.setIsPlaying(false);
                myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_PAUSED);
                sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PAUSE);
            }
            return;
        }
        else if (mAudioFocus == AudioFocus.NoFocusCanDuck)
            mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME);  // we'll be relatively quiet
        else
            mPlayer.setVolume(1.0f, 1.0f); // we can be loud

        if (!mPlayer.isPlaying()) {
            mPlayer.start();
            myApplication.setIsPlaying(true);
            myApplication.setMediaDuration(mPlayer.getDuration());
            myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_PLAYING);
            sendBroadcastToActivity(Constants.PLAYER_SERVICE_STATUS_PLAYING);

        }
    }

    void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelperLegacy != null
                        && mAudioFocusHelperLegacy.requestFocus())
            mAudioFocus = AudioFocus.Focused;
    }

    void playSong() {
        mState = State.Stopped;
        relaxResources(false); // release everything except MediaPlayer

        try {
            MusicRetriever.Item playingItem = null;

                mIsStreaming = false; // playing a locally available song

                playingItem = mRetriever.getItem();

                // set the source of the media player a a content URI
                createMediaPlayerIfNeeded();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(getApplicationContext(), playingItem.getURI());


            mSongTitle = playingItem.getTitle();
            if(mSongTitle==null)
                mSongTitle= "No title";

            mState = State.Preparing;
            setUpAsForeground(mSongTitle);

            // Use the media button APIs (if available) to register ourselves for media button
            // events

            MediaButtonHelper.registerMediaButtonEventReceiverCompat(
                    mAudioManager, mMediaButtonReceiverComponent);

            // Use the remote control APIs (if available) to set the playback state

            if (mRemoteControlClientCompat == null) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                intent.setComponent(mMediaButtonReceiverComponent);
                mRemoteControlClientCompat = new RemoteControlClientCompat(
                        PendingIntent.getBroadcast(this /*context*/,
                                0 /*requestCode, ignored*/, intent /*intent*/, 0 /*flags*/));
                RemoteControlHelper.registerRemoteControlClient(mAudioManager,
                        mRemoteControlClientCompat);
            }

            mRemoteControlClientCompat.setPlaybackState(
                    RemoteControlClient.PLAYSTATE_PLAYING);

            mRemoteControlClientCompat.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                    RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                    RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
                    RemoteControlClient.FLAG_KEY_MEDIA_STOP);

            // Update the remote controls
            mRemoteControlClientCompat.editMetadata(true)
                    .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, playingItem.getArtist())
                    .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, playingItem.getAlbum())
                    .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, playingItem.getTitle())
                    .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION,
                            playingItem.getDuration())
                    // TODO: fetch real item artwork
                    .putBitmap(
                            RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK,
                            mDummyAlbumArt)
                    .apply();

            mPlayer.prepareAsync();

            if (mIsStreaming) mWifiLock.acquire();
            else if (mWifiLock.isHeld()) mWifiLock.release();
        }
        catch (IOException ex) {
            Log.e("PlayerServiceLegacy", "IOException playing next song: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static int getCurrentPosition(){
        if(mPlayer != null)
        return mPlayer.getCurrentPosition();
        else
        return 0;
    }

    /** Called when media player is done playing current song. */
    public void onCompletion(MediaPlayer player) {
        // The media player finished playing the current song, so we go ahead and start the next.
        if (myApplication.getIndexSomethingIsPlaying() == myApplication.stackPlaylist.size()-1) {
            processStopRequest(true);
        }
        else {
            processSkipRequest();
            //playSong();
        }
    }

    /** Called when media player is done preparing. */
    public void onPrepared(MediaPlayer player) {
        // The media player is done preparing. That means we can start playing!
        mState = State.Playing;
        updateNotification(mSongTitle);
        configAndStartMediaPlayer();
    }

    /** Updates the notification. */
    void updateNotification(String text) {
        Intent intent;

        if(!playRequestIsFromPhonePlayer)
            intent = new Intent(getApplicationContext(), ActivityLegacyPlayer.class);
        else
            intent = new Intent(getApplicationContext(), ActivityLegacyPlayerPhone.class);

        intent.setAction(Constants.ACTION_SHOW_PLAYER_NO_NEW);

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentText(text)
                .setContentIntent(pi);
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    void setUpAsForeground(String text) {
        Intent intent;

        if(!playRequestIsFromPhonePlayer)
            intent = new Intent(getApplicationContext(), ActivityLegacyPlayer.class);
        else
            intent = new Intent(getApplicationContext(), ActivityLegacyPlayerPhone.class);

        intent.setAction(Constants.ACTION_SHOW_PLAYER_NO_NEW);

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification object.
        mNotificationBuilder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.audio_wave)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Stamina Player")
                .setContentText(text)
                .setContentIntent(pi)
                .setOngoing(true);

        startForeground(NOTIFICATION_ID, mNotificationBuilder.build());
    }


    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Media player error! Resetting.",
            Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra));

        mState = State.Stopped;
        myApplication.setIsPlaying(false);
        myApplication.setPlayerServiceCurrentState(Constants.CONST_PLAY_SERVICE_STATE_STOPPED);
        relaxResources(true);
        giveUpAudioFocus();
        return true; // true indicates we handled the error
    }

    public void onGainedAudioFocus() {
        mAudioFocus = AudioFocus.Focused;

        // restart media player with new focus settings
        if (mState == State.Playing)
            configAndStartMediaPlayer();
    }

    public void onMusicRetrieverPrepared() {
        // Done retrieving!
        mState = State.Stopped;

        // If the flag indicates we should start playing after retrieving, let's do that now.
        if (mStartPlayingAfterRetrieve) {
            tryToGetAudioFocus();
            playSong();
        }
    }

    public void onLostAudioFocus(boolean canDuck) {

        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;

        // start/restart/pause media player with new focus settings
        if (mPlayer != null && mPlayer.isPlaying())
            configAndStartMediaPlayer();
    }

    @Override
    public void onDestroy() {
        // Service is being killed, so make sure we release our resources
        mState = State.Stopped;
        relaxResources(true);
        giveUpAudioFocus();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
