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
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.fleecast.stamina.models.PlayListHelper;
import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.notetaking.AddNoteToAudio;
import com.fleecast.stamina.notetaking.PlayerService;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Main activity: shows media player buttons. This activity shows the media player buttons and
 * lets the user click them. No media handling is done here -- everything is done by passing
 * Intents to our {@link MusicService}.
 */
public class ActivityLegacyPlayer extends Activity implements OnClickListener {

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
    private ListView listView;
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
                        highlightSelectedPlayItem(myApplication.getIndexSomethingIsPlaying());
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
        setContentView(R.layout.activity_player_legacy);

        this.mContext = ActivityLegacyPlayer.this;

        myApplication = (MyApplication) getApplicationContext();

        seekBar = (SeekBar) findViewById(R.id.seekBarLegacy);


        mPlayButton = (ImageButton) findViewById(R.id.btnPlayLegacy);
        mPauseButton = (ImageButton) findViewById(R.id.btnPauseLegacy);
        mSkipButton = (ImageButton) findViewById(R.id.btnNextTrackLegacy);
        mRewindButton = (ImageButton) findViewById(R.id.btnRewindTrackLegacy);
        mStopButton = (ImageButton) findViewById(R.id.btnStopLegacy);
        mRewindNote = (ImageButton) findViewById(R.id.btnRewindNoteLegacy);
        mNextNote = (ImageButton) findViewById(R.id.btnNextNoteLegacy);
        txtTotalTime = (TextView) findViewById(R.id.txtTotalTimeLegacy);
        listView = (ListView) findViewById(R.id.listViewLegacy);
        imgNoNotePlaceHolder = (ImageView) findViewById(R.id.imgNoNotePlaceHolderLegacy);
        txtTitlePlayer = (TextView) findViewById(R.id.txtTitlePlayerLegacy);
        txtDetailsPlayer = (TextView) findViewById(R.id.txtDetailsPlayerLegacy);
        imgHideDetails = (ImageButton) findViewById(R.id.imgHideDetailsLegacy);
        detailsOfAudioNote = (RelativeLayout) findViewById(R.id.detailsOfAudioNoteLegacy);

        updateUI();


        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mSkipButton.setOnClickListener(this);
        mRewindButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mRewindNote.setOnClickListener(this);
        mNextNote.setOnClickListener(this);
        imgHideDetails.setOnClickListener(this);
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
        if (intent != null) {
            if (intent.hasExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID)) {

                parentDbId = intent.getIntExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID, Constants.CONST_NULL_ZERO);

                playListHelper = new PlayListHelper(this, String.valueOf(parentDbId));

                ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(mContext,
                        R.layout.listview_player, playListHelper.loadAudioListForListViewAdapter());

                adapter.notifyDataSetChanged();

                listView.setAdapter(adapter);


            }

        }

        detailsOfAudioNote.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Spannable itemValue = (Spannable) listView.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(),
                        itemValue, Toast.LENGTH_LONG)
                        .show();

                myApplication.setIndexSomethingIsPlaying(position);

                Intent serviceIntent = new Intent(mContext, MusicService.class);
                serviceIntent.setAction(Constants.ACTION_PLAY);
                startService(serviceIntent);


            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog myDialog;

                //If we are in portrait mode and details panel is showing.
                if(detailsOfAudioNote.getVisibility()== View.VISIBLE)
                    return false;

                final int chosenItemIndex = position;
                String[] items = {"Add/Edit note","Delete note","Delete note and audio","Share"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Options");
                builder.setIcon(R.drawable.audio_wave);

                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            tmpCurrentPlayingFile = myApplication.getIndexSomethingIsPlaying();
                            runAddAudioNoteActivity(parentDbId,myApplication.stackPlaylist.get(chosenItemIndex).getId());
                        }
                        else if(which==1){

                            RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);

                            realmAudioNoteHelper.deleteSingleAudioNote(myApplication.stackPlaylist.get(chosenItemIndex).getId());


                            ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(mContext,
                                    R.layout.listview_player, playListHelper.loadAudioListForListViewAdapter());

                            adapter.notifyDataSetChanged();

                            listView.setAdapter(adapter);

                        }
                        else if (which==2){
                            deleteFileAndNote(true,chosenItemIndex,String.valueOf(parentDbId));
                        }
                        else if(which==3){

                            File f=new File(myApplication.stackPlaylist.get(chosenItemIndex).getFileName().toString());

                            String tempFile = String.valueOf((int) (System.currentTimeMillis() / 1000));

                            File tmp = new File(ExternalStorageManager.getTempWorkingDirectory() + File.separator + tempFile + Constants.RECORDER_AUDIO_FORMAT_AAC);
                            try {
                                ExternalStorageManager.copy(f,tmp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Uri uri = Uri.fromFile(tmp);

                            Intent share = new Intent(Intent.ACTION_SEND);

                            String txtTitl  = myApplication.stackPlaylist.get(chosenItemIndex).getTitle();

                            if(txtTitl==null)
                                txtTitl = Constants.CONST_STRING_NO_TITLE;

                            String txtDescr  = myApplication.stackPlaylist.get(chosenItemIndex).getDescription();

                            if(txtDescr==null) {
                                txtDescr = Utility.unixTimeToReadable((long) getFilePostFixId(myApplication.stackPlaylist.get(chosenItemIndex).getFileName())) +
                                        "\n" + Constants.CONST_STRING_NO_DESCRIPTION;
                            }
                            else
                            {
                                txtDescr = Utility.unixTimeToReadable((long) getFilePostFixId(myApplication.stackPlaylist.get(chosenItemIndex).getFileName())) +
                                        "\n" + txtDescr ;
                            }


                            share.putExtra(Intent.EXTRA_SUBJECT,txtTitl);
                            share.putExtra(Intent.EXTRA_TITLE, txtTitl);
                            share.putExtra(Intent.EXTRA_TEXT, txtDescr);
                            share.putExtra(Intent.EXTRA_STREAM, uri);

                            share.setType("audio/*");
                            share.putExtra(Constants.EXTRA_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION);
                            share.putExtra(Constants.EXTRA_APP_ID, Constants.YOUR_APP_ID);
                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );

                            startActivityForResult(Intent.createChooser(share, "Share Audio File"),Constants.SHARE_TO_MESSENGER_REQUEST_CODE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Constants.RESULT_CODE_REQUEST_DIALOG)
        {
            if(null!=data)
            {

                playListHelper = new PlayListHelper(mContext, String.valueOf(parentDbId));

                ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(mContext,
                        R.layout.listview_player, playListHelper.loadAudioListForListViewAdapter());

                adapter.notifyDataSetChanged();

                listView.setAdapter(adapter);

                myApplication.setIndexSomethingIsPlaying(tmpCurrentPlayingFile);

            }
        }

    }

    public void onClick(View target) {
        // Send the correct intent to the MusicService, according to the button that was clicked
        Intent serviceIntent = new Intent(this, MusicService.class);
        if (target == mPlayButton) {
            serviceIntent.setAction(Constants.ACTION_PLAY);
            startService(serviceIntent);
        } else if (target == mPauseButton) {
            serviceIntent.setAction(Constants.ACTION_PAUSE);
            startService(serviceIntent);
        } else if (target == mSkipButton) {
            serviceIntent.setAction(Constants.ACTION_SKIP);
            startService(serviceIntent);
        } else if (target == mRewindButton) {
            serviceIntent.setAction(Constants.ACTION_REWIND);
            startService(serviceIntent);
        } else if (target == mStopButton) {
            serviceIntent.setAction(Constants.ACTION_STOP);
            startService(serviceIntent);
        } else if (target == mRewindNote) {
            if (notePointer > 0) {
                notePointer--;
                highlightSelectedNoteItem(notePointer);
                fadeWidgets();

            } else {
                notePointer = 0;
                highlightSelectedNoteItem(0);
                detailsOfAudioNote.setVisibility(View.VISIBLE);
            }
            fillTextNotesForUser();
        } else if (target == mNextNote) {
            if (notePointer < myApplication.stackPlaylist.size() - 1) {
                notePointer++;
                highlightSelectedNoteItem(notePointer);
                fadeWidgets();

            } else {
                notePointer = myApplication.stackPlaylist.size() - 1;
                highlightSelectedNoteItem(myApplication.stackPlaylist.size() - 1);
                detailsOfAudioNote.setVisibility(View.VISIBLE);
            }
            fillTextNotesForUser();
        } else if (target == imgHideDetails) {

            imgHideDetails.setVisibility(View.GONE);

                detailsOfAudioNote.setVisibility(View.GONE);
        }
    }
    private void deleteFileAndNote(boolean askQuestion, final int itemOrderInStack,final String mFileDbUniqueToken){

        if(myApplication.isPlaying()) {
            //First we try to kill the current working player service.
            Intent intent = new Intent(mContext, MusicService.class);
            intent.setAction(Constants.ACTION_STOP);
            startService(intent);

        }

        final int itmOrderInStack = itemOrderInStack;
        final String tmpFileDbUniqueToken = mFileDbUniqueToken;

        if(askQuestion) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

            LinearLayout layout = new LinearLayout(mContext);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(parms);

            layout.setGravity(Gravity.CLIP_VERTICAL);
            layout.setPadding(2, 2, 2, 2);

            TextView tv = new TextView(mContext);
            tv.setText("Delete item and note");
            tv.setPadding(40, 40, 40, 40);
            tv.setGravity(Gravity.LEFT);
            tv.setTextSize(20);

            final EditText et = new EditText(mContext);
            String etStr = et.getText().toString();

            TextView tv1 = new TextView(mContext);
            tv1.setPadding(20, 10, 20, 10);

            tv1.setText(Html.fromHtml("Type <font color='BLUE'>ASD</font> (case insensitive)"));

            LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv1Params.bottomMargin = 5;
            layout.addView(tv1, tv1Params);
            layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            alertDialogBuilder.setView(layout);
            alertDialogBuilder.setTitle("Note");
            alertDialogBuilder.setCustomTitle(tv);

            alertDialogBuilder.setIcon(R.drawable.audio_wave);
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(!et.getText().toString().trim().toLowerCase().contains("asd")){

                        Toast.makeText(mContext,"Wrong text!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);

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

                            return pathname.isFile() && !pathname.isHidden();
                        }
                    });

                    if(listOfFiles.length==0) {

                        File ff = new File(ExternalStorageManager.getWorkingDirectory()+ File.separator + mFileDbUniqueToken+ File.separator);
                        ff.delete();
                        Toast.makeText(mContext,"File removed to trash folder.",Toast.LENGTH_LONG).show();
                        ActivityLegacyPlayer.this.finish();
                    }
                    else{

                        playListHelper = new PlayListHelper(mContext, String.valueOf(parentDbId));
                        ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(mContext,
                                R.layout.listview_player, playListHelper.loadAudioListForListViewAdapter());

                        adapter.notifyDataSetChanged();

                        listView.setAdapter(adapter);


                        myApplication.setIndexSomethingIsPlaying(Constants.CONST_NULL_ZERO);
                    }
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();

            try {
                alertDialog.show();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        else{

            RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);

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

                    return pathname.isFile() && !pathname.isHidden();
                }
            });

            if(listOfFiles.length==0) {

                File ff = new File(ExternalStorageManager.getWorkingDirectory()+ File.separator + mFileDbUniqueToken+ File.separator);
                ff.delete();
                Toast.makeText(mContext,"File removed to trash folder.",Toast.LENGTH_LONG).show();
                ActivityLegacyPlayer.this.finish();
            }
            else {

                playListHelper = new PlayListHelper(mContext, String.valueOf(parentDbId));
                ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(mContext,
                        R.layout.listview_player, playListHelper.loadAudioListForListViewAdapter());

                adapter.notifyDataSetChanged();

                listView.setAdapter(adapter);


                myApplication.setIndexSomethingIsPlaying(Constants.CONST_NULL_ZERO);
            }
        }



    }



    private String getPathToAudioFiles(String mFileDbUniqueToken) {

        String pathToRecordingDirectory = ExternalStorageManager.getWorkingDirectory() + File.separator + mFileDbUniqueToken;

        return pathToRecordingDirectory;
    }

    public void runAddAudioNoteActivity(int idParentDb,int dbIdFile)
    {

        Intent intent=new Intent(this,AddNoteToAudio.class);

        intent.putExtra(Constants.EXTRA_AUDIO_NOTE_PARENT_DB_ID,idParentDb);

        intent.putExtra(Constants.EXTRA_AUDIO_NOTE_FILE_DB_ID, dbIdFile);

        startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_DIALOG);
    }

    private void fillTextNotesForUser(){

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
                    Utility.unixTimeToReadable((long) getFilePostFixId(myApplication.stackPlaylist.get(notePointer).getFileName())) +
                    "</font></small><br>" + "<font color='" + getHexStringFromInt(R.color.black_cat) + "'>" + Constants.CONST_STRING_NO_DESCRIPTION + "</font>";

        }
        else
        {
            txtDescr = "<small><font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" +
                    Utility.unixTimeToReadable((long) getFilePostFixId(myApplication.stackPlaylist.get(notePointer).getFileName())) +
                    "</font></small><br>" + "<font color='" + getHexStringFromInt(R.color.black_cat) + "'>" + txtDescr + "</font>";
        }

        txtDetailsPlayer.setText(Html.fromHtml(txtDescr));

        txtDetailsPlayer.setMovementMethod(new ScrollingMovementMethod());

    }
    private static int getFilePostFixId(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(file_name.lastIndexOf("_") + 1));
    }
    private String getHexStringFromInt(int resourceColorId){
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                startService(new Intent(Constants.ACTION_TOGGLE_PLAYBACK));
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startPlayProgressUpdater() {

        currentPosition = MusicService.getCurrentPosition();

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
         MusicService.processSeekToRequest(seekBar.getProgress());
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

    private void highlightSelectedPlayItem(int indexAudioItemToHighlight){

        listView.setItemChecked(indexAudioItemToHighlight, true);


        for(int i=0 ; i< listView.getChildCount();i++) {
            if (listView.getCheckedItemPosition()== i) {
                listView.getChildAt(i).setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
            }
            else if (notePointer == i)
                listView.getChildAt(i).setBackgroundColor(ContextCompat.getColor(this, R.color.deep_sky_blue));
            else
                listView.getChildAt(i).setBackgroundColor(listView.getSolidColor());

        }
    }

    private void highlightSelectedNoteItem(int indexNoteItemToHighlight) {

        for (int i = 0; i < listView.getChildCount(); i++) {
            if (listView.getCheckedItemPosition() == i)
                listView.getChildAt(i).setBackgroundColor(ContextCompat.getColor(this, R.color.amber));
            else if (indexNoteItemToHighlight == i) {
                listView.getChildAt(i).setBackgroundColor(ContextCompat.getColor(this, R.color.deep_sky_blue));
            }
            else
                listView.getChildAt(i).setBackgroundColor(listView.getSolidColor());

        }
    }

    @Override
    public void onBackPressed() {
        //If details panel is showing we bypass the finish player.
        if(detailsOfAudioNote.getVisibility()==View.VISIBLE) {

            imgHideDetails.setVisibility(View.GONE);

             detailsOfAudioNote.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
            finish();
        }
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
