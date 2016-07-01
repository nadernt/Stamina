package com.fleecast.stamina.notetaking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;


public class ActivityPlayerPortrait extends Activity {

    private MyApplication myApplication;
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private TextView txtTotalTime;
    private WindowManager windowManager;
    private Point szWindow = new Point();
    private ImageButton btnPlayPortrait, btnStopPortrait, btnRewindTrackPortrait, btnNextTrackPortrait;
    private TextView txtTitlePortraitPlayer, txtDescriptionPortraitPlayer;
    private int oldMediaSeekPosition = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_portrait);

        myApplication = (MyApplication) getApplicationContext();

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        int width = 0;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = (szWindow.x / 3) * 2;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = szWindow.x;
        }

        this.getWindow().setLayout(width,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);

        btnPlayPortrait = (ImageButton) findViewById(R.id.btnPlayPortrait); // Start
        btnStopPortrait = (ImageButton) findViewById(R.id.btnStopPortrait); // Stop
        btnNextTrackPortrait = (ImageButton) findViewById(R.id.btnNextTrackPortrait); // Next
        btnRewindTrackPortrait = (ImageButton) findViewById(R.id.btnRewindTrackPortrait); // Rewind

        txtTitlePortraitPlayer = (TextView) findViewById(R.id.txtTitlePortraitPlayer);

        txtDescriptionPortraitPlayer = (TextView) findViewById(R.id.txtDescriptionPortraitPlayer);


        btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_play);

        seekBar = (SeekBar) findViewById(R.id.seekBar1);


        // Handle Intents & action
        handleIntents(getIntent().getAction());


        seekBar.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (myApplication.isPlaying())
                    updateSeekChange();
                else
                    return true;
                return false;
            }
        });

        // Start
        btnPlayPortrait.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                if (!myApplication.isPlaying()) {
                    sendCommandToPlayerService(Constants.ACTION_PLAY, Constants.ACTION_NULL);
                    play();
                } else {
                    sendCommandToPlayerService(Constants.ACTION_PAUSE, Constants.ACTION_NULL);
                    pause();
                }

                myApplication.setIsPlaying(!myApplication.isPlaying());

            }
        });

        // Stop
        btnStopPortrait.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommandToPlayerService(Constants.ACTION_STOP, Constants.ACTION_NULL);
                stop();
            }
        });

        btnNextTrackPortrait.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nextTrack();
            }
        });

        btnRewindTrackPortrait.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rewindTrack();
            }
        });

    }

    private void handleIntents(String mAction) {
        if(mAction==null){
            myApplication.setIndexSomethingIsPlaying(Constants.CONST_NULL_ZERO);
            PlayListHelper playListHelper = new PlayListHelper(this);
        int dbId = getIntent().getIntExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, Constants.CONST_NULL_ZERO);
        String fileName = getIntent().getStringExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER);
        playListHelper.loadJustSingleFileForPlay(fileName, dbId);
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra(Constants.EXTRA_PLAY_NEW_SESSION, true);
        startService(intent);
        }else{

            if(mAction.equals(Constants.ACTION_SHOW_PLAYER_NO_NEW)){
                if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
                    btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_pause);
                    myApplication.setIsPlaying(true);
                }
                else{
                    btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_play);
                    myApplication.setIsPlaying(false);
                }

                //We set here to be sure there is not any mistake from any control before and thread start correctly.
                oldMediaSeekPosition=0;
                seekBar.setMax(myApplication.getMediaDuration());
                startPlayProgressUpdater();
            }


        }

 /*   if(mAction==null){

    *//*}else{

        if(mAction.equals(Constants.ACTION_SHOW_PLAYER_NO_NEW)){
            if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
                btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_pause);
                myApplication.setIsPlaying(true);
            }
            else{
                btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_play);
                myApplication.setIsPlaying(false);
            }

            //We set here to be sure there is not any mistake from any control before and thread start correctly.
            oldMediaSeekPosition=0;
            seekBar.setMax(myApplication.getMediaDuration());
            startPlayProgressUpdater();
        }
*//*

    }*/

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void play() {
        myApplication.setIsPlaying(true);
        btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_pause);
        seekBar.setMax(myApplication.getMediaDuration());
        startPlayProgressUpdater();
    }

    private void pause() {
        myApplication.setIsPlaying(false);
        btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_play);
        txtTotalTime.setText("Pause");
    }

    private void stop() {
        myApplication.setIsPlaying(false);
        handler.removeCallbacksAndMessages(null);
        txtTotalTime.setText("Stop");
        seekBar.setProgress(0);
        btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_play);
        btnPlayPortrait.setEnabled(true);
    }

    private void nextTrack() {
        // stop();
        sendCommandToPlayerService(Constants.ACTION_NEXT, Constants.ACTION_NULL);

    }

    private void rewindTrack() {
        sendCommandToPlayerService(Constants.ACTION_REWIND, Constants.ACTION_NULL);

    }

    private void sendCommandToPlayerService(String actionCommand, int seekTo) {

        Intent intent = new Intent(this, PlayerService.class);

        if (!actionCommand.equals(Constants.EXTRA_SEEK_TO) && !actionCommand.equals(Constants.EXTRA_UPDATE_SEEKBAR)) {
            intent.setAction(actionCommand);
        } else {
            if (actionCommand == Constants.EXTRA_SEEK_TO)
                intent.putExtra(Constants.EXTRA_SEEK_TO, seekTo);

            if (actionCommand == Constants.EXTRA_UPDATE_SEEKBAR)
                intent.putExtra(Constants.EXTRA_UPDATE_SEEKBAR, true);

        }
        startService(intent);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        int width = 0;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = (szWindow.x / 3) * 2;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = szWindow.x;
        }

        this.getWindow().setLayout(width,
                RelativeLayout.LayoutParams.WRAP_CONTENT);


    }

    private void updateSeekChange() {
        sendCommandToPlayerService(Constants.EXTRA_SEEK_TO, seekBar.getProgress());
        setProgressText();
    }

    public void startPlayProgressUpdater() {

        sendCommandToPlayerService(Constants.EXTRA_UPDATE_SEEKBAR, Constants.ACTION_NULL);

        if (oldMediaSeekPosition != myApplication.getCurrentMediaPosition()) {
            seekBar.setProgress(myApplication.getCurrentMediaPosition());
            setProgressText();
            oldMediaSeekPosition = myApplication.getCurrentMediaPosition();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    protected void setProgressText() {

        final int HOUR = 60 * 60 * 1000;
        final int MINUTE = 60 * 1000;
        final int SECOND = 1000;

        int durationInMillis = myApplication.getMediaDuration();
        int curVolume = myApplication.getCurrentMediaPosition();

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
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Constants.INTENTFILTER_PLAYER_SERVICE)
        );

        if (myApplication.getPlayerServiceCurrentState() == Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
            btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_pause);
            myApplication.setIsPlaying(true);
        } else {
            btnPlayPortrait.setImageResource(R.drawable.ic_action_playback_play);
            myApplication.setIsPlaying(false);
        }

        //We set here to be sure there is not any mistake from any control before and thread start correctly.
        oldMediaSeekPosition = 0;
        seekBar.setMax(myApplication.getMediaDuration());
        startPlayProgressUpdater();

    }


    @Override
    protected void onStop() {

        if (myApplication.isPlaying()) {
            sendCommandToPlayerService(Constants.ACTION_PAUSE, Constants.ACTION_NULL);
            pause();
            myApplication.setIsPlaying(false);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENTFILTER_PLAYER_SERVICE)) {
                int statusCode = intent.getIntExtra(Constants.EXTRA_PLAYER_SERVICE_PLAY_STATUS, -1);
                switch (statusCode) {

                    case Constants.PLAYER_SERVICE_STATUS_ERROR:
                        Toast.makeText(ActivityPlayerPortrait.this, "Error playing the media file!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_PLAYING:
                        play();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_TRACK_FINISHED:
                        //Log.e("DBG", "Command TRACK Finished or Stop Recieved");
                        stop();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_CLOSE_PLAYER:
                        //Log.e("DBG", "Command Close Player Recieved");
                        stop();
                        finish();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_PAUSE:
                        pause();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_SEEK_BAR_UPDATED:
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_TACK_CHANGED:

                        break;

                }

            }
        }

    };

}