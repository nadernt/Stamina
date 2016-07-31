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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.PlayListHelper;
import com.fleecast.stamina.utility.Constants;

/**
 * Main activity: shows media player buttons. This activity shows the media player buttons and
 * lets the user click them. No media handling is done here -- everything is done by passing
 * Intents to our {@link PlayerServiceLegacy}.
 */
public class ActivityLegacyPlayerPhone extends Activity implements OnClickListener {

    ImageButton mPlayButton;
    ImageButton mPauseButton;
    ImageButton mSkipButton;
    ImageButton mRewindButton;
    ImageButton mStopButton;
    ImageButton m;
    ImageButton mEjectButton;
    MyApplication myApplication;
    private SeekBar seekBar;
    private ImageButton mRewindNote;
    private ImageButton mNextNote;
    private TextView txtTotalTime;
    private Handler handler = new Handler();
    private int oldMediaSeekPosition = 0;
    private int parentDbId;
    private PlayListHelper playListHelper;
    private int currentPosition;
    private int notePointer = Constants.CONST_NULL_MINUS;
    private ImageView imgNoNotePlaceHolder;
    private TextView txtTitlePlayer;
    private TextView txtDetailsPlayer;
    private ImageButton imgHideDetails;
    private RelativeLayout detailsOfAudioNote;
    private boolean theOrintationIsLandscape =false;
    private int tmpCurrentPlayingFile = Constants.CONST_NULL_ZERO;
    private Context mContext;
    private int dbId;


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Constants.INTENTFILTER_PLAYER_SERVICE)
        );
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENTFILTER_PLAYER_SERVICE)) {
                int statusCode = intent.getIntExtra(Constants.EXTRA_PLAYER_SERVICE_PLAY_STATUS, -1);
                switch (statusCode) {

                    case Constants.PLAYER_SERVICE_STATUS_ERROR:
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_PLAYING:
                        Log.e("DBG", "PLAYER_SERVICE_STATUS_PLAYING: " + myApplication.getIndexSomethingIsPlaying());
                        startPlayProgressUpdater();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_TRACK_FINISHED:
                        Log.e("DBG", "Command TRACK Finished or Stop Recieved");
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_CLOSE_PLAYER:
                        Log.e("DBG", "Command Close Player Recieved");
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_PAUSE:
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_SEEK_BAR_UPDATED:
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_TACK_CHANGED:

                        break;

                }

            }
        }


    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_legacy_phone);

        this.mContext = ActivityLegacyPlayerPhone.this;

        myApplication = (MyApplication) getApplicationContext();

        seekBar = (SeekBar) findViewById(R.id.seekBar1LegacyPhone);


        mPlayButton = (ImageButton) findViewById(R.id.btnPlayLegacyPhone);
        mPauseButton = (ImageButton) findViewById(R.id.btnPauseLegacyPhone);
        mStopButton = (ImageButton) findViewById(R.id.btnStopLegacyPhone);

        txtTotalTime = (TextView) findViewById(R.id.txtTotalTimeLegacyPhone);
        txtTitlePlayer = (TextView) findViewById(R.id.txtTitleLegacyPhonePlayer);
        txtDetailsPlayer = (TextView) findViewById(R.id.txtDescriptionLegacyPhonePlayer);

        updateUI();


        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING || myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PAUSED)
                {
                    updateSeekChange();
                return false;
                }
                else {
                    return true;
                }


            }
        });
    }

    private void updateUI() {
        Intent intent = getIntent();
        handleIntents(getIntent().getAction());


    }

    private void handleIntents(String mAction) {
        if(mAction==null){

            myApplication.setIndexSomethingIsPlaying(Constants.CONST_NULL_ZERO);
            myApplication.setCurrentMediaPosition(0);

            PlayListHelper playListHelper = new PlayListHelper(this);
             dbId = getIntent().getIntExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, Constants.CONST_NULL_ZERO);

            if(getIntent().hasExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE)){
                String title = getIntent().getStringExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE);
                if(title==null)
                    title="No title";
                if(title.isEmpty())
                    title="No title";
                txtTitlePlayer.setText(title);
                txtTitlePlayer.setVisibility(View.VISIBLE);
            }

            if(getIntent().hasExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION)){
                String description = getIntent().getStringExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION);
                if(description==null)
                    description="No description";
                if(description.isEmpty())
                    description="No description";


                txtDetailsPlayer.setText(description);
                txtDetailsPlayer.setVisibility(View.VISIBLE);
            }

            String fileName = getIntent().getStringExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER);
            playListHelper.loadJustSingleFileForPlay(fileName, dbId);
            seekBar.setProgress(0);
            Intent intent = new Intent(this, PlayerServiceLegacy.class);
            intent.setAction(Constants.ACTION_PLAY_LEGACY);
            // We inform the notification in the service to create the returning intent for the ActivityPlayerPortrait
            intent.putExtra(Constants.EXTRA_PLAY_REQUEST_ISـFROM_PORTRATE_PLAYER, true);

            startService(intent);

        }else{

            if(mAction.equals(Constants.ACTION_SHOW_PLAYER_NO_NEW)){

                if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
                    myApplication.setIsPlaying(true);
                }
                else{
                    myApplication.setIsPlaying(false);
                }

                    String title = myApplication.stackPlaylist.get(Constants.CONST_NULL_ZERO).getTitle();
                    if(title==null)
                        title="No title";
                    if(title.isEmpty())
                        title="No title";
                    txtTitlePlayer.setText(title);
                    txtTitlePlayer.setVisibility(View.VISIBLE);


                    String description = myApplication.stackPlaylist.get(Constants.CONST_NULL_ZERO).getDescription();
                    if(description==null)
                        description="No description";
                    if(description.isEmpty())
                        description="No description";

                    txtDetailsPlayer.setText(description);
                    txtDetailsPlayer.setVisibility(View.VISIBLE);

                //We set here to be sure there is not any mistake from any control before and thread start correctly.
                oldMediaSeekPosition=0;
                seekBar.setMax(myApplication.getMediaDuration());
                startPlayProgressUpdater();
            }


        }

    }


    public void onClick(View target) {
        // Send the correct intent to the PlayerServiceLegacy, according to the button that was clicked
        Intent serviceIntent = new Intent(this, PlayerServiceLegacy.class);
        if (target == mPlayButton) {
            serviceIntent.setAction(Constants.ACTION_PLAY_LEGACY);
            serviceIntent.putExtra(Constants.EXTRA_PLAY_REQUEST_ISـFROM_PORTRATE_PLAYER, true);
            startService(serviceIntent);
        } else if (target == mPauseButton) {
            serviceIntent.setAction(Constants.ACTION_PAUSE_LEGACY);
            startService(serviceIntent);
        } else if (target == mStopButton) {
            seekBar.setProgress(0);
            currentPosition=0;
            myApplication.setCurrentMediaPosition(0);
            setProgressText();
            serviceIntent.setAction(Constants.ACTION_STOP_LEGACY);
            startService(serviceIntent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                startService(new Intent(Constants.ACTION_TOGGLE_PLAYBACK_LEGACY));
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startPlayProgressUpdater() {

        currentPosition = PlayerServiceLegacy.getCurrentPosition();

        if (oldMediaSeekPosition != currentPosition) {

            seekBar.setMax(myApplication.getMediaDuration());

            seekBar.setProgress(currentPosition);
            setProgressText();
            oldMediaSeekPosition = currentPosition;
        }
        else {
            handler.removeCallbacksAndMessages(null);
        }


        if (myApplication.getPlayerServiceCurrentState() == Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, Constants.CONST_PLAYER_PROGRESS_UPDATE_TIME);
        }
    }

    private void updateSeekChange() {
         PlayerServiceLegacy.processSeekToRequest(seekBar.getProgress());
        setProgressText();
    }

    protected void setProgressText() {

        final int HOUR = 60 * 60 * 1000;
        final int MINUTE = 60 * 1000;
        final int SECOND = 1000;

        int durationInMillis = myApplication.getMediaDuration();
        int curVolume = currentPosition;

        int durationHour = durationInMillis / HOUR;
        int durationMint = (durationInMillis % HOUR) / MINUTE;
        int durationSec = (durationInMillis % MINUTE) / SECOND;

        int currentHour = curVolume / HOUR;
        int currentMint = (curVolume % HOUR) / MINUTE;
        int currentSec = (curVolume % MINUTE) / SECOND;

        if (durationHour > 0) {
            txtTotalTime.setText(String.format("%02d:%02d:%02d/%02d:%02d:%02d",
                    currentHour, currentMint, currentSec, durationHour, durationMint, durationSec));
        } else {
            txtTotalTime.setText(String.format("%02d:%02d/%02d:%02d",
                    currentMint, currentSec, durationMint, durationSec));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
