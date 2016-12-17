package com.fleecast.stamina.notetaking;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.legacyplayer.ActivityLegacyPlayer;
import com.fleecast.stamina.legacyplayer.ActivityLegacyPlayerPhone;
import com.fleecast.stamina.legacyplayer.PlayerServiceLegacy;
import com.fleecast.stamina.models.NoteInfoRealmStruct;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.models.TempNoteInfoStruct;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.util.Date;


public class ActivityAddAudioNote extends AppCompatActivity {


    private RealmNoteHelper realmNoteHelper;
    private NoteInfoRealmStruct noteInfoRealmStruct;
    private EditText txtDescription;
    private EditText txtTitle;
    private Toolbar mToolbar;                              // Declaring the Toolbar Object
    private ImageView btnDeleteRecord;
    private MenuItem mnuItemDeleteNote;
    private MenuItem mnuItemSaveNote;
    private MenuItem mnuItemPlayRecord;
    private View textviewTimeLapse;
    private int dbId;
    private MyApplication myApplication;
    private String TAG = "Add Activity";

    private WindowManager windowManager;
    private Point szWindow = new Point();

    private ImageView btnNoStopRecord,btnTapRecord,btnStopRecord;
    private RelativeLayout recorderControlsLayout;
    private boolean toggleNoStopRecord=false;
    private Chronometer txtTimeLaps;
    private String latestRecordFileName;
    private ImageView btnRecordsListPlayer;
    private int currentNoteType=0;
    private boolean weAreInEditMode=false;
    private boolean itIsTotallyNewNote =true;
    private boolean skipSaveOnPause =false;
    private MenuItem mnuItemRedo;
    private MenuItem mnuItemUndo;
    private UndoRedoHelper undoRedoHelper;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        intentHandler(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.audio_note_add_activity);


        myApplication =  (MyApplication)getApplicationContext();

        realmNoteHelper = new RealmNoteHelper(ActivityAddAudioNote.this);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar_audio_note); // Attaching the layout to the toolbar object

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);



        txtTitle = (EditText) findViewById(R.id.inputTitle);
        txtDescription = (EditText) findViewById(R.id.inputDescription);

        txtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtDescription.getText()!=null)
                    myApplication.tmpCurrentAudioNoteInfoStruct.setDescription(txtDescription.getText().toString());
                myApplication.setAudioNoteSaved(false);
                }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtTitle.getText().toString()!=null)
                myApplication.tmpCurrentAudioNoteInfoStruct.setTitle(txtTitle.getText().toString());
                myApplication.setAudioNoteSaved(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        populateGUI();

    }

    private void populateUserInterface(Menu menu, boolean populateForAddOrEdit){

        mnuItemSaveNote = menu.findItem(R.id.action_save);
        mnuItemPlayRecord = menu.findItem(R.id.action_play_record);
        mnuItemUndo = menu.findItem(R.id.action_undo);
        mnuItemRedo = menu.findItem(R.id.action_redo);

        undoRedoHelper = new UndoRedoHelper(txtDescription);

        textviewTimeLapse = getLayoutInflater().inflate(R.layout.time_laps, null);
        mToolbar.addView(textviewTimeLapse);



        txtTimeLaps  = (Chronometer) textviewTimeLapse.findViewById(R.id.txtTimeLaps);
        txtTimeLaps.setVisibility(View.INVISIBLE);



        intentHandler(getIntent());
    }


    private void intentHandler(Intent intent){

        if(intent.hasExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD)){

            /*****************************************************************************************************************
             * ***************************************************************************************************************
             * now you should get the db key intent and fetch the record from the db and fill it in the text edits.
             * ***************************************************************************************************************
             *****************************************************************************************************************/

            dbId = intent.getIntExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD,Constants.CONST_NULL_ZERO);

            if(dbId>0){

                weAreInEditMode=true;
                noteInfoRealmStruct = realmNoteHelper.getNoteById(dbId);


                myApplication.tmpCurrentAudioNoteInfoStruct = new TempNoteInfoStruct();
                myApplication.tmpCurrentAudioNoteInfoStruct.setId(dbId);
                myApplication.tmpCurrentAudioNoteInfoStruct.setDescription(noteInfoRealmStruct.getDescription());
                myApplication.tmpCurrentAudioNoteInfoStruct.setTitle(noteInfoRealmStruct.getTitle());
                myApplication.tmpCurrentAudioNoteInfoStruct.setTag(noteInfoRealmStruct.getColor());


                txtTitle.setText(noteInfoRealmStruct.getTitle());
                txtDescription.setText(noteInfoRealmStruct.getDescription());
                txtDescription.setSelection(txtDescription.getText().length());


                int i = populatePathForOldRecords(dbId);

                if (i > Constants.CONST_NULL_ZERO) {

                    if(i>1) {
                        btnRecordsListPlayer.setVisibility(View.VISIBLE);
                    }
                    else{
                        btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                    }

                    btnDeleteRecord.setVisibility(View.VISIBLE);

                    String folderOfRecords = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(dbId));

                    File file = new File(folderOfRecords);

                    long oldFile=0;

                    for (File tmpFile : file.listFiles()) {

                        if (tmpFile.isFile()) {

                            if(oldFile < tmpFile.lastModified()){

                                oldFile = tmpFile.lastModified();

                                latestRecordFileName = tmpFile.getPath();

                            }

                        }
                    }

                }
                else{
                    btnDeleteRecord.setVisibility(View.INVISIBLE);
                    btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                }

                mnuItemPlayRecord.setVisible(true);
                mnuItemSaveNote.setVisible(true);
                recorderControlsLayout.setVisibility(View.VISIBLE);
                currentNoteType = Constants.CONST_IS_TEXT_AND_RECORD;


                itIsTotallyNewNote=false;

                // We are running activty by tapping on notification description
                if((myApplication.isRecordUnderGoing()==Constants.CONST_RECORDER_SERVICE_WORKS_FOR_NOTE)) {
                    setBackGroundOfView(btnNoStopRecord, R.drawable.buttons_recorder_bg, true);
                    toggleNoStopRecord = true;
                    txtTimeLaps.setBase(myApplication.getRecordTimeTick());

                    startTimer();

                }
                else // We are running activty by tapping on notification stop button
                {
                    setBackGroundOfView(btnTapRecord,0,false);
                    setBackGroundOfView(btnNoStopRecord,0,false);
                    toggleNoStopRecord=false;
                    txtTimeLaps.setVisibility(View.INVISIBLE);
                }

                currentNoteType = Constants.CONST_IS_EDIT_TEXT_AND_RECORD;
            }



        } else if(intent.hasExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_START_RECORD)){

                 if ((myApplication.tmpCurrentAudioNoteInfoStruct != null) ) {

                     dbId = myApplication.tmpCurrentAudioNoteInfoStruct.getId();

                     noteInfoRealmStruct = realmNoteHelper.getNoteById(dbId);

                     myApplication.tmpCurrentAudioNoteInfoStruct.setId(dbId);
                     myApplication.tmpCurrentAudioNoteInfoStruct.setDescription(noteInfoRealmStruct.getDescription());
                     myApplication.tmpCurrentAudioNoteInfoStruct.setTitle(noteInfoRealmStruct.getTitle());
                     myApplication.tmpCurrentAudioNoteInfoStruct.setTag(noteInfoRealmStruct.getColor());

                     txtTitle.setText(noteInfoRealmStruct.getTitle());
                     txtDescription.setText(noteInfoRealmStruct.getDescription());
                     txtDescription.setSelection(txtDescription.getText().length());

                     if((myApplication.isRecordUnderGoing()==Constants.CONST_RECORDER_SERVICE_WORKS_FOR_NOTE)) {
                         setBackGroundOfView(btnNoStopRecord, R.drawable.buttons_recorder_bg, true);
                         toggleNoStopRecord = true;
                     }
                     else
                     {
                         setBackGroundOfView(btnTapRecord,0,false);
                         setBackGroundOfView(btnNoStopRecord,0,false);
                         toggleNoStopRecord=false;
                         txtTimeLaps.setVisibility(View.INVISIBLE);
                     }
                 }
                 else
                 {

                     initVariables();

                     mnuItemPlayRecord.setVisible(true);
                     btnDeleteRecord.setVisibility(View.INVISIBLE);
                     btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                     mnuItemSaveNote.setVisible(true);
                     recorderControlsLayout.setVisibility(View.VISIBLE);
                     currentNoteType = Constants.CONST_IS_TEXT_AND_RECORD;

                     if(Prefs.getBoolean(Constants.PREF_AUTO_RUN_RECORDER_ON_AUDIO_NOTES,false)) {
                         setBackGroundOfView(btnNoStopRecord, R.drawable.buttons_recorder_bg, true);
                         startRecord();
                         toggleNoStopRecord = true;
                     }
                 }


        }
        setIntent(new Intent());

    }

    private int populatePathForOldRecords(int dbId){

        String folderOfRecords = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(dbId));

        boolean foundAnyFile= false;
        File file = new File(folderOfRecords);

        int i=0;

        if(file.exists() && file.isDirectory()) {
            for (File tmpFile : file.listFiles()) {
                if (tmpFile.isFile()) {
                    i++;
                    foundAnyFile = true;
                }
            }

            //Folder is exist but it is empty empty.
            if(!foundAnyFile)
            {
                file.delete();
            }
        }
        else{
            return i;
        }

        return i;
    }

    private void saveNote(boolean showPrompt,boolean killAfterSave) {
        String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();

        if((itIsTotallyNewNote && !doWeHaveRecords() && title.isEmpty() && description.isEmpty()) || myApplication.isAudioNoteSaved()){
            myApplication.setAudioNoteSaved(true);
            //Release resources
            myApplication.tmpCurrentAudioNoteInfoStruct = null;
            skipSaveOnPause =true;
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }else {

        if (!myApplication.isAudioNoteSaved() || title.length() == 0) {

            // If user didn't add any title
            if (title.length() == 0) {
                title = "Untitled";
                txtTitle.setText(title);
                txtTitle.setSelection(txtTitle.getText().length());
                myApplication.tmpCurrentAudioNoteInfoStruct.setTitle(title);
            } else {
                title = myApplication.tmpCurrentAudioNoteInfoStruct.getTitle();
                description = myApplication.tmpCurrentAudioNoteInfoStruct.getDescription();
            }

            Date updateTime = new Date();

            Date createdTime = null;

            if (weAreInEditMode)
                createdTime = noteInfoRealmStruct.getCreateTimeStamp();

            if (!weAreInEditMode)
                realmNoteHelper.addNote(dbId, title, description, true, updateTime, createdTime, null, null, Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL, null, Constants.CONST_NOTETYPE_AUDIO);
            else
                realmNoteHelper.updateNotes(dbId, title, description, updateTime, Constants.CONST_NOTETYPE_AUDIO);
            myApplication.setAudioNoteSaved(true);

            if (showPrompt)
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();

            if(killAfterSave) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
        }
    }

    private void initVariables(){
        itIsTotallyNewNote=true;
        skipSaveOnPause =false;
        myApplication.tmpCurrentAudioNoteInfoStruct = new TempNoteInfoStruct();
        // Creating unique id for db as primary key
        dbId = (int) (System.currentTimeMillis() / 1000);
        myApplication.tmpCurrentAudioNoteInfoStruct.setId(dbId);
        txtDescription.setText("");
        txtTitle.setText("");
        txtTimeLaps.setVisibility(View.INVISIBLE);
        currentNoteType= Constants.CONST_NULL_ZERO;

        weAreInEditMode=false;
        currentNoteType=0;
        toggleNoStopRecord=false;

        // Flashing title
        ColorDrawable[] color = {new ColorDrawable(Color.RED), new ColorDrawable(ContextCompat.getColor(this, R.color.blue_eyes))};
        TransitionDrawable trans = new TransitionDrawable(color);
        mToolbar.setBackground(trans);
        trans.startTransition(Constants.TRANSIATION_TIME);
    }


    private void setBackGroundOfView(View view,int drawableId,boolean addRemove){

        if(addRemove)
            view.setBackgroundResource(drawableId);
        else
            view.setBackgroundResource(0);

    }

    private void populateGUI(){

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {

            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

         recorderControlsLayout = (RelativeLayout) findViewById(R.id.recorderControlsLayout);

         btnNoStopRecord = (ImageView) findViewById(R.id.btnNoStopRecord);
         btnTapRecord = (ImageView) findViewById(R.id.btnTapRecord);
         btnStopRecord = (ImageView) findViewById(R.id.btnStopRecord);
        btnDeleteRecord = (ImageView) findViewById(R.id.btnDeleteRecord);

        btnRecordsListPlayer= (ImageView) findViewById(R.id.btnRecordsListPlayer);

        btnNoStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                vib.vibrate(30);

                if(!toggleNoStopRecord)
                {
                    if(startRecord()) {
                        setBackGroundOfView(btnTapRecord,0,false);
                        toggleNoStopRecord=true;
                        setBackGroundOfView(btnNoStopRecord,R.drawable.buttons_recorder_bg,true);

                    }
                }
                else
                {
                    setBackGroundOfView(btnTapRecord,0,false);
                    toggleNoStopRecord=false;
                    setBackGroundOfView(btnNoStopRecord,0,false);
                    stopRecord();
                }
            }
        });

        btnTapRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        if (startRecord()) {
                            setBackGroundOfView(btnNoStopRecord, 0, false);
                            setBackGroundOfView(btnTapRecord, R.drawable.buttons_recorder_bg, true);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                            setBackGroundOfView(btnTapRecord, 0, false);
                            stopRecord();
                        return true;
                }

                return false;
            }
        });

        btnDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddAudioNote.this);


                        adb.setMessage("Are you sure want to delete your latest record?");


                        adb.setTitle("Note");

                        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                File file = new File(latestRecordFileName);

                                if (file.exists()) {

                                    file.delete();

                                    mnuItemPlayRecord.setVisible(false);
                                    btnDeleteRecord.setVisibility(View.INVISIBLE);

                                    if(isThereAnyRecordInPath(latestRecordFileName))
                                    {
                                       btnRecordsListPlayer.setVisibility(View.VISIBLE);
                                    }else
                                    {
                                        btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                                    }

                                    latestRecordFileName="";
                                    Log.e(TAG, "Record deleted!");
                                }

                            }
                        });


                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        adb.show();


                }
        });

        btnRecordsListPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myApplication.isRecordUnderGoing()==Constants.CONST_RECORDER_SERVICE_IS_FREE) {

                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            && ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))) {
                        // your code using RemoteControlClient API here - is between 14-20

                        if (myApplication.isPlaying() || myApplication.getPlayerServiceCurrentState() == Constants.CONST_PLAY_SERVICE_STATE_PAUSED) {
                            Intent intent = new Intent(ActivityAddAudioNote.this, PlayerServiceLegacy.class);
                            intent.setAction(Constants.ACTION_STOP_LEGACY);
                            startService(intent);
                        }

                        myApplication.setIndexSomethingIsPlaying(0);

                        Intent intent = new Intent(ActivityAddAudioNote.this, ActivityLegacyPlayer.class);
                        intent.putExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID, dbId);
                        startActivity(intent);

                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        if (myApplication.isPlaying()) {
                            Intent intent = new Intent(ActivityAddAudioNote.this, PlayerService.class);
                            intent.setAction(Constants.ACTION_STOP);
                            startService(intent);
                        }
                        myApplication.setPlaylistHasLoaded(false);

                        Intent intent = new Intent(ActivityAddAudioNote.this, ActivityRecordsPlayList.class);
                        intent.putExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID, String.valueOf(dbId));
                        startActivity(intent);

                    }

                }
            }
        });

    }

    private boolean isThereAnyRecordInPath(String recordedFilePath) {

        File f = new File(recordedFilePath);

        File dirAsFile = f.getParentFile();

        File[] listOfFiles = dirAsFile.listFiles();

        return listOfFiles.length > 1;
    }

    private boolean startRecord() {

        if (myApplication.isRecordUnderGoing()!=Constants.CONST_RECORDER_SERVICE_IS_FREE || myApplication.isPlaying()) {

            AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddAudioNote.this);

            if(myApplication.isPlaying())
                adb.setMessage("The player is playing please stop it!");
            else
                adb.setMessage("You have a phone call or another recording under progress. Stop it and try again!");

            adb.setTitle("Note");

            adb.setIcon(R.drawable.ic_action_phone);

            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            adb.show();

            return false;
       }

        boolean hasEnoughSpace = ExternalStorageManager.isThereEnoughSpaceOnStorage();
        if (hasEnoughSpace) {
            Intent intent = new Intent(this, RecorderNoteService.class);
            //false is just fake we don't need value.
            intent.putExtra(Constants.EXTRA_NEW_RECORD, false);
            intent.putExtra(Constants.EXTRA_RECORD_FILENAME, String.valueOf(dbId));
            startService(intent);
            resetTimer();
            startTimer();
            return true;
        } else {
            setBackGroundOfView(btnTapRecord,0,false);

            setBackGroundOfView(btnNoStopRecord,0,false);

            setBackGroundOfView(btnTapRecord, 0, false);

            toggleNoStopRecord=false;

            AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddAudioNote.this);


            adb.setMessage("You don't have enough empty space!");


            adb.setTitle("No Enough Storage");
            adb.setIcon(R.drawable.ic_action_phone);

            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            });

            adb.show();

            return false;
        }

    }

    private void stopRecord() {

        latestRecordFileName="";
            Intent intent = new Intent(this, RecorderNoteService.class);
            //false is just fake we don't need value.
        intent.putExtra(Constants.EXTRA_STOP_RECORD, false);
            startService(intent);
            stopTimer();
    }

    private void killRecordService() {
        Intent intent = new Intent(this,RecorderNoteService.class);
        //false is just fake we don't need value.
        intent.putExtra(Constants.EXTRA_STOP_RECORD_SERVICE,false);
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(latestRecordFileName==null)
            return;
        if(!itIsTotallyNewNote) {
            if (isThereAnyRecordInPath(latestRecordFileName)) {
                btnRecordsListPlayer.setVisibility(View.VISIBLE);
            } else {
                btnRecordsListPlayer.setVisibility(View.INVISIBLE);
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!skipSaveOnPause)
            saveNote(true,false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       saveNote(true,true);
    }


    private boolean doWeHaveRecords(){

        String folderOfRecords = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(dbId));

        boolean foundAnyFile= false;
        File file = new File(folderOfRecords);

        if(file.exists() && file.isDirectory()) {
            for (File tmpFile : file.listFiles()) {
                if (tmpFile.isFile()) {
                    foundAnyFile = true;
                    break;
                }
            }

            //Folder is exist but it is empty empty.
            if(!foundAnyFile)
            {
                file.delete();
            }
        }
        else{
            return foundAnyFile;
        }

    return foundAnyFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_note_add, menu);

        populateUserInterface(menu, true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveNote(true, true);
            return true;
        }
        else if (id == R.id.action_undo) {
            undoRedoHelper.undo();
            return true;

        }
        else if (id == R.id.action_redo) {
            undoRedoHelper.redo();
                return true;

        }else if (id == R.id.action_play_record) {

            if ((myApplication.isRecordUnderGoing()==Constants.CONST_RECORDER_SERVICE_IS_FREE) && (latestRecordFileName != null) && !latestRecordFileName.isEmpty()) {

                File file = new File(latestRecordFileName);

                if (!file.exists()) {
                    return false;
                }
                playLatestRecord();

            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
    private void playLatestRecord() {

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                && ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))) {
            // your code using RemoteControlClient API here - is between 14-20

            if (myApplication.isPlaying() || myApplication.getPlayerServiceCurrentState() == Constants.CONST_PLAY_SERVICE_STATE_PAUSED) {
                Intent intent = new Intent(ActivityAddAudioNote.this, PlayerServiceLegacy.class);
                intent.setAction(Constants.ACTION_STOP_LEGACY);
                startService(intent);
            }

            Intent intent = new Intent(ActivityAddAudioNote.this, ActivityLegacyPlayerPhone.class);
            intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, dbId);

            intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE, "");
            intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION, "");


            intent.putExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER, latestRecordFileName);
            startActivity(intent);

        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

           if(myApplication.isPlaying())
            {
                Intent intent = new Intent(ActivityAddAudioNote.this,PlayerService.class);
                intent.setAction(Constants.ACTION_STOP);
                startService(intent);
            }
            Intent intent = new Intent(this,ActivityPlayerPhone.class);
            intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, dbId);
            intent.putExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER, latestRecordFileName);
            startActivity(intent);

        }

    }

    public void startTimer()
    {
        txtTimeLaps.setVisibility(View.VISIBLE);

       txtTimeLaps.start();

        txtTimeLaps.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                myApplication.setRecordTimeTick(chronometer.getBase());
            }
        });
    }

    public void stopTimer()
    {
        txtTimeLaps.stop();
    }

    public void resetTimer()
    {
        txtTimeLaps.setBase(SystemClock.elapsedRealtime());
    }

    public void formatTimer()
    {
        txtTimeLaps.setFormat("Formatted time (%s)");
    }

    public void clearTimer()
    {
        txtTimeLaps.setFormat(null);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Constants.INTENTFILTER_RECORD_SERVICE)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if( myApplication.isByePassRecordBroadcastReceiverForOnce()) {
                myApplication.setByePassRecordBroadcastReceiverForOnce(false);
                return;
            }

            if (intent.getAction().equals(Constants.INTENTFILTER_RECORD_SERVICE)) {

                    int msgRecordService =  intent.getIntExtra(Constants.EXTRA_RECORD_SERVICE_REPORTS,Constants.CONST_NULL_MINUS);

                    if(msgRecordService == Constants.REPORT_RECORD_ERROR_TO_ACTIVITY) {
                        toggleNoStopRecord = false;
                        Utility.showMessage("Hey you are too much fast don't rush at least hold on one second! ", "Ohhh", ActivityAddAudioNote.this);
                    }
                    else if(msgRecordService == Constants.REPORT_RECORDED_FILE_TO_ACTIVITY){

                        latestRecordFileName = intent.getStringExtra(Constants.REPORT_RECORDED_FILE_TO_ACTIVITY_FILENAME);
                        mnuItemPlayRecord.setVisible(true);
                        btnDeleteRecord.setVisibility(View.VISIBLE);

                        if(isThereAnyRecordInPath(latestRecordFileName))
                        {
                            btnRecordsListPlayer.setVisibility(View.VISIBLE);
                        }else
                        {
                            btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                        }

                    }
                    else if(msgRecordService == Constants.REPORT_RECORD_STOPPED_BY_NOTIFICATION_TO_ACTIVITY){

                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                        vib.vibrate(30);
                        setBackGroundOfView(btnTapRecord,0,false);

                        setBackGroundOfView(btnNoStopRecord,0,false);
                         stopRecord();

                        toggleNoStopRecord = false;

                    }
            }
        }

    };
} 