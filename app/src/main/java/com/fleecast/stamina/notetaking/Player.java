package com.fleecast.stamina.notetaking;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;

import android.util.Log;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;


public class Player extends Activity {

    private MediaPlayer mMediaplayer;
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private TextView txtTotalTime;
    private boolean errorInStream =false;
    private WindowManager windowManager;
    private Point szWindow = new Point();
    private boolean play_pause = false;
    private String mFileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);



        if (mMediaplayer != null) {
            mMediaplayer.release();
        }

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        int width=0;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = (szWindow.x / 3) * 2;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            width = szWindow.x;
        }

        this.getWindow().setLayout(width,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);

        final ImageButton btnPlay = (ImageButton) findViewById(R.id.btnPlay); // Start
        final ImageButton btnStop = (ImageButton) findViewById(R.id.btnStop); // Stop
        btnPlay.setImageResource(R.drawable.ic_action_playback_play);
        btnStop.setImageResource(R.drawable.ic_action_playback_stop);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);

        Intent intent = getIntent();
        mFileName = intent.getStringExtra("file_name");
        intent.putExtra("file_name", mFileName);

        mMediaplayer = new MediaPlayer();

        try {

            mMediaplayer.setDataSource(mFileName);
            mMediaplayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();

            errorInStream = true;

            seekBar.setVisibility(View.GONE);
            btnPlay.setVisibility(View.GONE);
            btnStop.setVisibility(View.GONE);
            txtTotalTime.setText("Error to play the file!\nClose application and try again.");
        }


        if(!errorInStream) {

            seekBar.setMax(mMediaplayer.getDuration());
            seekBar.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    updateSeekChange(v);
                    return false;
                }
            });

            mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    handler.removeCallbacksAndMessages(null);
                    play_pause = false;
                    btnPlay.setImageResource(R.drawable.ic_action_playback_play);
                    seekBar.setProgress(0);
                }
            });

            mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaplayer.seekTo(0);
                }
            });

            // Start
            btnPlay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!play_pause) {

                        mMediaplayer.start();
                        btnPlay.setImageResource(R.drawable.ic_action_playback_pause);
                        startPlayProgressUpdater();
                        btnStop.setEnabled(true);

                    }
                    else
                    {
                        btnPlay.setImageResource(R.drawable.ic_action_playback_play);
                        txtTotalTime.setText("Pause");
                        mMediaplayer.pause();
                    }

                    play_pause = !play_pause;

                }
            });

            // Stop
            btnStop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    handler.removeCallbacksAndMessages(null);
                    txtTotalTime.setText("Stop");
                    play_pause= false;
                    mMediaplayer.pause();
                    mMediaplayer.seekTo(0);
                    seekBar.setProgress(0);

                    btnPlay.setImageResource(R.drawable.ic_action_playback_play);
                    btnPlay.setEnabled(true);
                    btnStop.setEnabled(false);
                }
            });

            // Now everything is fine so start playing.
            btnPlay.performClick();
        }
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
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
           width = szWindow.x;
        }

        this.getWindow().setLayout(width,
                RelativeLayout.LayoutParams.WRAP_CONTENT);


    }

    private void updateSeekChange(View v) {
            SeekBar sb = (SeekBar) v;
            mMediaplayer.seekTo(sb.getProgress());
            setProgressText();
    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(mMediaplayer.getCurrentPosition());
        setProgressText();
        if (mMediaplayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, Constants.PLAYER_PROGRESS_UPDATE_TIME);
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
        Log.e("DBG", "Player Destroyed.");
        if (mMediaplayer != null) {
            mMediaplayer.release();
        }
        if(handler  != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    protected void setProgressText() {

        final int HOUR = 60 * 60 * 1000;
        final int MINUTE = 60 * 1000;
        final int SECOND = 1000;

        int durationInMillis = mMediaplayer.getDuration();
        int curVolume = mMediaplayer.getCurrentPosition();

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


}
