package com.fleecast.stamina.notetaking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class ActivityRecordsPlayList extends Activity {

    private static PlayListHelper playListHelper;
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private TextView txtTotalTime;
    private ImageButton btnPlay, btnStop,btnRewindTrack,btnNextTrack;
    private TextView txtTitlePlayer;
    private static TextView txtDetailsPlayer;
    private int oldMediaSeekPosition = 0;
    private MyApplication myApplication;
    private static boolean playlistHasLoaded =false;
    private static ImageButton btnRewindNote;
    private ImageButton btnNextNote;
    private static int notePointer =-1;
    private ImageView imgNoNotePlaceHolder;
    private static RelativeLayout detailsOfAudioNote;
    private static int parentDbId;
    private ImageButton imgHideDetails;
    private static boolean detailsAreVisible = false;
    private boolean theOrintationIsLandscape = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listplayer_layout);
        myApplication =  (MyApplication)getApplicationContext();


        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime1);

        btnPlay = (ImageButton) findViewById(R.id.btnPlay); // Start
        btnStop = (ImageButton) findViewById(R.id.btnStop); // Stop
        btnNextTrack = (ImageButton) findViewById(R.id.btnNextTrack); // Next
        btnRewindTrack = (ImageButton) findViewById(R.id.btnRewindTrack); // Rewind
        btnRewindNote = (ImageButton) findViewById(R.id.btnRewindNote); // Rewind
        btnNextNote = (ImageButton) findViewById(R.id.btnNextNote); // Rewind
        imgNoNotePlaceHolder = (ImageView) findViewById(R.id.imgNoNotePlaceHolder);
        txtTitlePlayer = (TextView) findViewById(R.id.txtTitlePlayer);
        txtDetailsPlayer = (TextView) findViewById(R.id.txtDetailsPlayer);
        imgHideDetails = (ImageButton) findViewById(R.id.imgHideDetails);
        detailsOfAudioNote = (RelativeLayout) findViewById(R.id.detailsOfAudioNote);
        btnPlay.setImageResource(R.drawable.ic_action_playback_play);

        seekBar = (SeekBar) findViewById(R.id.seekBar2);

        Intent intent = getIntent();

        if(!playlistHasLoaded || intent.hasExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID)) {

            String mFolderDbUniqueToken  = intent.getStringExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID);

            loadPlayListForListViw(mFolderDbUniqueToken, false);

            myApplication.setIndexSomethingIsPlaying(Constants.CONST_NULL_ZERO);

            parentDbId = Integer.valueOf(mFolderDbUniqueToken);

           // Log.e("DBG","1465131201");

        }else {
            TitlesFragment.highlightSelectedNoteItem(notePointer);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            theOrintationIsLandscape=true;
            imgHideDetails.setVisibility(View.GONE);
            detailsOfAudioNote.setVisibility(View.VISIBLE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            theOrintationIsLandscape=false;
            if(detailsAreVisible)
                imgHideDetails.setVisibility(View.VISIBLE);
        }



        seekBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(myApplication.isPlaying())
                    updateSeekChange();
                else
                    return true;
                return false;
            }
        });

        // Start
        btnPlay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                if(!myApplication.isPlaying()) {
                    sendCommandToPlayerService(Constants.ACTION_PLAY,Constants.ACTION_NULL);
                    play();
                }
                else
                {
                    sendCommandToPlayerService(Constants.ACTION_PAUSE,Constants.ACTION_NULL);
                    pause();
                }

                myApplication.setIsPlaying(!myApplication.isPlaying());

            }
        });

        // Stop
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommandToPlayerService(Constants.ACTION_STOP,Constants.ACTION_NULL);
                stop();
            }
        });

        btnNextTrack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nextTrack();
            }
        });

        btnRewindTrack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rewindTrack();
            }
        });

        btnRewindNote.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(notePointer > 0){
                    notePointer--;
                    TitlesFragment.highlightSelectedNoteItem(notePointer);
                    fadeWidgets();

                }
                else {
                    notePointer=0;
                    TitlesFragment.highlightSelectedNoteItem(0);
                    detailsOfAudioNote.setVisibility(View.VISIBLE);
                }
                fillTextNotesForUser();
            }
        });

        btnNextNote.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(notePointer <  myApplication.stackPlaylist.size()-1){
                    notePointer++;
                    TitlesFragment.highlightSelectedNoteItem(notePointer);
                    fadeWidgets();

                }
                else
                {
                    notePointer=myApplication.stackPlaylist.size()-1;
                    TitlesFragment.highlightSelectedNoteItem(myApplication.stackPlaylist.size()-1);
                    detailsOfAudioNote.setVisibility(View.VISIBLE);
                }
                fillTextNotesForUser();
            }
        });

        imgHideDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!theOrintationIsLandscape) {

                    detailsAreVisible=false;

                    imgHideDetails.setVisibility(View.GONE);

                    if(!theOrintationIsLandscape)
                        detailsOfAudioNote.setVisibility(View.GONE);
                    else
                        detailsOfAudioNote.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void fillTextNotesForUser(){

        detailsAreVisible=true;

        if(!theOrintationIsLandscape)
            imgHideDetails.setVisibility(View.VISIBLE);

        String txtTitl  = myApplication.stackPlaylist.get(notePointer).getTitle();

        if(txtTitl==null) {
            imgNoNotePlaceHolder.setVisibility(View.VISIBLE);
            txtTitl = "<font color='" + getHexStringFromInt(R.color.black_cat) + "'>" + Constants.CONST_STRING_NO_TITLE + "</font>";
        }
        else {
            txtTitl = "<font color='" + getHexStringFromInt(R.color.black_cat) + "'>" + txtTitl + "</font>";
            imgNoNotePlaceHolder.setVisibility(View.GONE);
        }

        txtTitlePlayer.setText(Html.fromHtml(txtTitl));

        String txtDescr  = myApplication.stackPlaylist.get(notePointer).getDescription();

        if(txtDescr==null) {
            txtDescr = "<small><font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" +
                    unixTimeToReadable((long) getFilePostFixId(myApplication.stackPlaylist.get(notePointer).getFileName())) +
                    "</font></small><br>" + "<font color='" + getHexStringFromInt(R.color.black_cat) + "'>" + Constants.CONST_STRING_NO_DESCRIPTION + "</font>";

        }
        else
        {
            txtDescr = "<small><font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" +
                    unixTimeToReadable((long) getFilePostFixId(myApplication.stackPlaylist.get(notePointer).getFileName())) +
                    "</font></small><br>" + "<font color='" + getHexStringFromInt(R.color.black_cat) + "'>" + txtDescr + "</font>";
        }

        txtDetailsPlayer.setText(Html.fromHtml(txtDescr));

        txtDetailsPlayer.setMovementMethod(new ScrollingMovementMethod());

    }

    private String getHexStringFromInt(int resourceColorId){
        //ContextCompat.getColor(mContext, R.color.color_name)
        int intColor = ContextCompat.getColor(this, resourceColorId);
        return "#" + String.valueOf(Integer.toHexString(intColor)).substring(2);
    }
    private void fadeWidgets(){

        detailsOfAudioNote.setVisibility(View.VISIBLE);
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(500);
        txtDetailsPlayer.startAnimation(animation1);
        txtTitlePlayer.startAnimation(animation1);
    }
    final Timer timer = new Timer();

    public void textEffect(){
        txtTitlePlayer.setTypeface(null, Typeface.BOLD);
        txtDetailsPlayer.setTypeface(null, Typeface.BOLD);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtTitlePlayer.setTypeface(null, Typeface.NORMAL);
                            txtDetailsPlayer.setTypeface(null, Typeface.NORMAL);
                        }
                    });
                }
            };
            timer.schedule(task,  500);
    }

    private String unixTimeToReadable(long unixSeconds){

        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        // E, dd MMM yyyy HH:mm:ss z
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss a"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        //System.out.println(formattedDate);
        return formattedDate;

    }

    private int getFilePostFixId(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(file_name.lastIndexOf("_") + 1));
    }

    private void loadPlayListForListViw(String mFileDbUniqueId,boolean startAutoPlayeOnStart) {

        playListHelper = new PlayListHelper(this, mFileDbUniqueId);

        myApplication.setIndexSomethingIsPlaying(0);

        playlistHasLoaded =true;

        if(startAutoPlayeOnStart) {
            handleIntents(null);
        }

    }

    private void handleIntents(String mAction){

        if(mAction==null){

            Intent intent = new Intent(this, PlayerService.class);
            intent.putExtra(Constants.EXTRA_PLAY_NEW_SESSION, true);
            startService(intent);

        }else{

            if(mAction.equals(Constants.ACTION_SHOW_PLAYER_NO_NEW)){
                if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
                    btnPlay.setImageResource(R.drawable.ic_action_playback_pause);
                    myApplication.setIsPlaying(true);
                }
                else{
                    btnPlay.setImageResource(R.drawable.ic_action_playback_play);
                    myApplication.setIsPlaying(false);
                }

                //We set here to be sure there is not any mistake from any control before and thread start correctly.
                oldMediaSeekPosition=0;
                seekBar.setMax(myApplication.getMediaDuration());
                startPlayProgressUpdater();
            }


        }

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void play()
    {
        myApplication.setIsPlaying(true);
        btnPlay.setImageResource(R.drawable.ic_action_playback_pause);
        seekBar.setMax(myApplication.getMediaDuration());
        startPlayProgressUpdater();
    }

    private void pause()
    {
        myApplication.setIsPlaying(false);
        btnPlay.setImageResource(R.drawable.ic_action_playback_play);
        txtTotalTime.setText("Pause");
    }

    private void stop()
    {
        myApplication.setIsPlaying(false);
        handler.removeCallbacksAndMessages(null);
        txtTotalTime.setText("Stop");
        seekBar.setProgress(0);
        btnPlay.setImageResource(R.drawable.ic_action_playback_play);
        btnPlay.setEnabled(true);
    }

    private void nextTrack()
    {
        sendCommandToPlayerService(Constants.ACTION_NEXT,Constants.ACTION_NULL);
    }

    private void rewindTrack()
    {
        sendCommandToPlayerService(Constants.ACTION_REWIND,Constants.ACTION_NULL);
    }

    private void sendCommandToPlayerService(String actionCommand, int seekTo) {

        Intent intent = new Intent(this,PlayerService.class);

        if(!actionCommand.equals(Constants.EXTRA_SEEK_TO) && !actionCommand.equals(Constants.EXTRA_UPDATE_SEEKBAR)) {
            intent.setAction(actionCommand);
        }else
        {
            if(actionCommand == Constants.EXTRA_SEEK_TO )
                intent.putExtra(Constants.EXTRA_SEEK_TO,seekTo);

            if(actionCommand == Constants.EXTRA_UPDATE_SEEKBAR)
                intent.putExtra(Constants.EXTRA_UPDATE_SEEKBAR,true);

        }
        startService(intent);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            theOrintationIsLandscape=true;
            imgHideDetails.setVisibility(View.GONE);
            detailsOfAudioNote.setVisibility(View.VISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            theOrintationIsLandscape=false;
            if(detailsAreVisible)
                imgHideDetails.setVisibility(View.VISIBLE);
        }




    }

    private void updateSeekChange() {
        sendCommandToPlayerService(Constants.EXTRA_SEEK_TO,seekBar.getProgress());
        setProgressText();
    }

    public void startPlayProgressUpdater() {

        sendCommandToPlayerService(Constants.EXTRA_UPDATE_SEEKBAR,Constants.ACTION_NULL);

        if(oldMediaSeekPosition != myApplication.getCurrentMediaPosition()) {
            seekBar.setProgress(myApplication.getCurrentMediaPosition());
            setProgressText();
            oldMediaSeekPosition = myApplication.getCurrentMediaPosition();
        }
        if (myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
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

        //If details panel is showing we bypass the finish player.
        if(!theOrintationIsLandscape && detailsOfAudioNote.getVisibility()==View.VISIBLE) {

            detailsAreVisible=false;

            imgHideDetails.setVisibility(View.GONE);

            if(!theOrintationIsLandscape)
                detailsOfAudioNote.setVisibility(View.GONE);
            else
                detailsOfAudioNote.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if(handler  != null) {
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

        if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
            btnPlay.setImageResource(R.drawable.ic_action_playback_pause);
            myApplication.setIsPlaying(true);
        }
        else{
            btnPlay.setImageResource(R.drawable.ic_action_playback_play);
            myApplication.setIsPlaying(false);
        }

        //We set here to be sure there is not any mistake from any control before and thread start correctly.
        oldMediaSeekPosition=0;
        seekBar.setMax(myApplication.getMediaDuration());
        startPlayProgressUpdater();

    }


    @Override
    protected void onStop() {
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
                        Toast.makeText(ActivityRecordsPlayList.this, "Error playing the media file!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_PLAYING:
                        TitlesFragment.highlightSelectedPlayItem(myApplication.getIndexSomethingIsPlaying());
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

    public static class TitlesFragment extends ListFragment {
        boolean mDualPane;
        int mCurCheckPosition = 0;
        private MyApplication myApplication;
        private static ListView playlistListviewInstance;
        private static Context mContextTitlesFragment;
        private ArrayAdapter<Spanned> la;
        private AlertDialog myDialog;
        private int tmpCurrentPlayingFile = Constants.CONST_NULL_ZERO;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            myApplication = (MyApplication) getActivity().getApplication();

            la = new ArrayAdapter<Spanned>(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    playListHelper.loadAudioListForListViewAdapter());
            la.notifyDataSetChanged();
            setListAdapter(la);

            mContextTitlesFragment = getActivity();

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }

            playlistListviewInstance =getListView();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mDualPane=true;
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                detailsOfAudioNote.setVisibility(View.VISIBLE);
            }
            else{
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                getListView().setItemChecked(mCurCheckPosition, true);
                detailsOfAudioNote.setVisibility(View.GONE);
                mDualPane=false;
            }


            getListView().setLongClickable(true);

            getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                    //If we are in portrait mode and details panel is showing.
                    if((detailsOfAudioNote.getVisibility()== View.VISIBLE) && !mDualPane)
                        return false;

                    final int chosenItemIndex = pos;
                    String[] items = {"Add/Edit note","Delete note","Delete note and audio","Share"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Options");
                    builder.setIcon(R.drawable.audio_wave);

                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which==0){
                                tmpCurrentPlayingFile = myApplication.getIndexSomethingIsPlaying();
                                Intent intent = new Intent(getActivity(),AddNoteToAudio.class);
                                //myApplication.stackPlaylist.get(chosenItemIndex).
                                //Log.e("DBG",)
                                runAddAudioNoteActivity(parentDbId,myApplication.stackPlaylist.get(chosenItemIndex).getId());
                            }
                            else if(which==1){

                                RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(getActivity());

                                realmAudioNoteHelper.deleteSingleAudioNote(myApplication.stackPlaylist.get(chosenItemIndex).getId());

                                // playListHelper = new PlayListHelper(getActivity(), String.valueOf(parentDbId));

                                la = new ArrayAdapter<Spanned>(getActivity(),
                                        android.R.layout.simple_list_item_activated_1,
                                        playListHelper.loadAudioListForListViewAdapter());
                                la.notifyDataSetChanged();

                                setListAdapter(la);

/*
                                myApplication.setIndexSomethingIsPlaying(tmpCurrentPlayingFile);
*/
                            }
                            else if (which==2){
                                deleteFileAndNote(true,chosenItemIndex,String.valueOf(parentDbId));
                            }
                        }
                    });


                    builder.setCancelable(true);
                    myDialog = builder.create();
                    myDialog.show();
                    return true;
                }
            });

        }

        private void deleteFileAndNote(boolean askQuestion, final int itemOrderInStack,final String mFileDbUniqueToken){

            if(myApplication.isPlaying()) {
                //First we try to kill the current working player service.
                Intent intent = new Intent(mContextTitlesFragment, PlayerService.class);
                intent.setAction(Constants.ACTION_STOP);
                mContextTitlesFragment.startService(intent);
            }

            final int itmOrderInStack = itemOrderInStack;
            final String tmpFileDbUniqueToken = mFileDbUniqueToken;

            if(askQuestion) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                LinearLayout layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(parms);

                layout.setGravity(Gravity.CLIP_VERTICAL);
                layout.setPadding(2, 2, 2, 2);

                TextView tv = new TextView(getActivity());
                tv.setText("Delete item and note");
                tv.setPadding(40, 40, 40, 40);
                tv.setGravity(Gravity.LEFT);
                tv.setTextSize(20);

                final EditText et = new EditText(getActivity());
                String etStr = et.getText().toString();
                TextView tv1 = new TextView(getActivity());
                tv1.setText("Type asd (case insensitive)");

                LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tv1Params.bottomMargin = 5;
                layout.addView(tv1, tv1Params);
                layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                alertDialogBuilder.setView(layout);
                alertDialogBuilder.setTitle("Some title");
                // alertDialogBuilder.setMessage("Input Student ID");
                alertDialogBuilder.setCustomTitle(tv);

                alertDialogBuilder.setIcon(R.drawable.audio_wave);
                // alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setCancelable(false);

                // Setting Negative "Cancel" Button
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                // Setting Positive "OK" Button
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(!et.getText().toString().trim().toLowerCase().contains("asd")){

                            Toast.makeText(getActivity(),"Wrong text!",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(getActivity());
                        Log.e("Hongralll",myApplication.stackPlaylist.get(itmOrderInStack).getFileName());
                        File file = new File(myApplication.stackPlaylist.get(itmOrderInStack).getFileName());

                        File createTrashFolder = new File(ExternalStorageManager.getWorkingDirectory()+ Constants.CONST_RECYCLEBIN_DIRECTORY_NAME);
                        createTrashFolder.mkdir();
                        if (file.exists()) {
                            File moveTobin = new File(ExternalStorageManager.getWorkingDirectory()+ Constants.CONST_RECYCLEBIN_DIRECTORY_NAME + File.separator +file.getName());
                            file.renameTo(moveTobin);
                        }

                        realmAudioNoteHelper.deleteSingleAudioNote(myApplication.stackPlaylist.get(itmOrderInStack).getId());


                        File folder = new File(getPathToAudioFiles(tmpFileDbUniqueToken));

                        File[] listOfFiles = folder.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                //String name = pathname.getName().toLowerCase();
                                //return name.endsWith(".xml") && pathname.isFile();
                                return pathname.isFile() && !pathname.isHidden();
                            }
                        });

                        if(listOfFiles.length==0) {

                            File ff = new File(ExternalStorageManager.getWorkingDirectory()+ File.separator + mFileDbUniqueToken+ File.separator);
                            ff.delete();
                            Toast.makeText(mContextTitlesFragment,"File removed to trash folder.",Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                        else{
                           // playListHelper = new PlayListHelper(getActivity(), String.valueOf(parentDbId));

                            la = new ArrayAdapter<Spanned>(getActivity(),
                                    android.R.layout.simple_list_item_activated_1,
                                    playListHelper.loadAudioListForListViewAdapter());
                            la.notifyDataSetChanged();

                            setListAdapter(la);

                            myApplication.setIndexSomethingIsPlaying(Constants.CONST_NULL_ZERO);
                        }
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                try {
                    alertDialog.show();
                } catch (Exception e) {
                    // WindowManager$BadTokenException will be caught and the app would
                    // not display the 'Force Close' message
                    e.printStackTrace();
                }
            }
            else{
                RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(getActivity());
                Log.e("Hongralll",myApplication.stackPlaylist.get(itmOrderInStack).getFileName());
                File file = new File(myApplication.stackPlaylist.get(itmOrderInStack).getFileName());

                File createTrashFolder = new File(ExternalStorageManager.getWorkingDirectory()+ Constants.CONST_RECYCLEBIN_DIRECTORY_NAME);
                createTrashFolder.mkdir();
                if (file.exists()) {
                    File moveTobin = new File(ExternalStorageManager.getWorkingDirectory()+ Constants.CONST_RECYCLEBIN_DIRECTORY_NAME + File.separator +file.getName());
                    file.renameTo(moveTobin);
                }

                realmAudioNoteHelper.deleteSingleAudioNote(myApplication.stackPlaylist.get(itmOrderInStack).getId());


                File folder = new File(getPathToAudioFiles(tmpFileDbUniqueToken));

                File[] listOfFiles = folder.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        //String name = pathname.getName().toLowerCase();
                        //return name.endsWith(".xml") && pathname.isFile();
                        return pathname.isFile() && !pathname.isHidden();
                    }
                });

                if(listOfFiles.length==0) {

                    File ff = new File(ExternalStorageManager.getWorkingDirectory()+ File.separator + mFileDbUniqueToken+ File.separator);
                    ff.delete();
                    Toast.makeText(mContextTitlesFragment,"File removed to trash folder.",Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                else {
                    // playListHelper = new PlayListHelper(getActivity(), String.valueOf(parentDbId));

                    la = new ArrayAdapter<Spanned>(getActivity(),
                            android.R.layout.simple_list_item_activated_1,
                            playListHelper.loadAudioListForListViewAdapter());
                    la.notifyDataSetChanged();

                    setListAdapter(la);

                    myApplication.setIndexSomethingIsPlaying(Constants.CONST_NULL_ZERO);
                }
            }



        }



        private String getPathToAudioFiles(String mFileDbUniqueToken) {

            String pathToRecordingDirectory = ExternalStorageManager.getWorkingDirectory() + File.separator +  mFileDbUniqueToken;

            return pathToRecordingDirectory;

        }
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            //If we are in portrait mode and details panel is showing.
            if((detailsOfAudioNote.getVisibility()== View.VISIBLE) && !mDualPane)
                return;
            else
                runPlayerForItem(position);
        }


        // Method to handle the Click Event on GetMessage Button
        public void runAddAudioNoteActivity(int idParentDb,int dbIdFile)
        {
            // Create The  Intent and Start The Activity to get The message
            Intent intent=new Intent(getActivity(),AddNoteToAudio.class);

            intent.putExtra(Constants.EXTRA_AUDIO_NOTE_PARENT_DB_ID,idParentDb);

            intent.putExtra(Constants.EXTRA_AUDIO_NOTE_FILE_DB_ID, dbIdFile);

            startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_DIALOG);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode==Constants.RESULT_CODE_REQUEST_DIALOG)
            {
                if(null!=data)
                {
                    // fetch the message String
                    //String message=data.getStringExtra("MESSAGE");
                   int dbIdFromAddEditDlg = data.getIntExtra(Constants.EXTRA_AUDIO_NOTE_FILE_DB_ID,Constants.CONST_NULL_MINUS);
                    playListHelper = new PlayListHelper(getActivity(), String.valueOf(parentDbId));

                    la = new ArrayAdapter<Spanned>(getActivity(),
                            android.R.layout.simple_list_item_activated_1,
                            playListHelper.loadAudioListForListViewAdapter());
                    la.notifyDataSetChanged();

                    setListAdapter(la);

                    myApplication.setIndexSomethingIsPlaying(tmpCurrentPlayingFile);

                }
            }

        }

        private static void highlightSelectedPlayItem(int indexAudioItemToHighlight){

            playlistListviewInstance.setItemChecked(indexAudioItemToHighlight, true);

            for(int i=0 ; i< playlistListviewInstance.getChildCount();i++) {
                if (playlistListviewInstance.getCheckedItemPosition()== i) {
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.radical_red));
                }
                else if (notePointer == i)
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.deep_sky_blue));
                else
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(playlistListviewInstance.getSolidColor());

            }
        }

        private static void highlightSelectedNoteItem(int indexNoteItemToHighlight) {

            for (int i = 0; i < playlistListviewInstance.getChildCount(); i++) {
                if (playlistListviewInstance.getCheckedItemPosition() == i)
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.radical_red));
                 else if (indexNoteItemToHighlight == i) {
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.deep_sky_blue));
                }
                else
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(playlistListviewInstance.getSolidColor());

            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            outState.putInt("curChoice", mCurCheckPosition);
        }


        void runPlayerForItem(int index) {

            mCurCheckPosition = index;

            getListView().setItemChecked(index, true);

            myApplication.setIndexSomethingIsPlaying(index);
            Intent intent = new Intent(getActivity(), PlayerService.class);
            intent.putExtra(Constants.EXTRA_PLAY_NEW_SESSION, true);
            getActivity().startService(intent);
        }

    }

}