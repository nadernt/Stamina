package com.fleecast.stamina.notetaking;

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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
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
   // private boolean weTypedSomethingNew = false;
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
    private boolean theNoteIsAudioType=false;
    private boolean dontAcceptAnyNewSession =false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        intentHandler(intent);

        Log.e(TAG, "Magic " + dbId);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      /*  Log.e(TAG, "onDestroy killRecordService");
        killRecordService();*/
        //stopService(new Intent(this,RecorderNoteService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_add_activity);

        startService(new Intent(this,RecorderNoteService.class));


        myApplication =  (MyApplication)getApplicationContext();

        realmNoteHelper = new RealmNoteHelper(ActivityAddAudioNote.this);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object

        setSupportActionBar(mToolbar);


       // pathToWorkingDirectory =  ExternalStorageManager.prepareWorkingDirectory(this) + Constants.CONST_WORKING_DIRECTORY_NAME;

       /* if(pathToWorkingDirectory.length()==0)
            storageAvail = false;
        else
            storageAvail=true;*/

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
              //  weTypedSomethingNew = true;
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
                //weTypedSomethingNew = true;
                if(txtTitle.getText()!=null)
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

        mnuItemDeleteNote = menu.findItem(R.id.action_delete);
        mnuItemSaveNote = menu.findItem(R.id.action_save);
        mnuItemPlayRecord = menu.findItem(R.id.action_play_record);

        textviewTimeLapse = getLayoutInflater().inflate(R.layout.time_laps, null);
        mToolbar.addView(textviewTimeLapse);



        txtTimeLaps  = (Chronometer) textviewTimeLapse.findViewById(R.id.txtTimeLaps);
        txtTimeLaps.setVisibility(View.INVISIBLE);

        //recorder = new RecorderNoteService(this,txtTimeLaps,pathToWorkingDirectory,TEMP_FILE);

        final Intent intent = getIntent();

        intentHandler(intent);

       /* // Check if we have a recording.
        if(myApplication.isRecordUnderGoing() > Constants.CONST_RECORDER_SERVICE_IS_FREE)
        {
            android.app.AlertDialog myDialog;

            String[] items;

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

            builder.setTitle("Note");

            if(myApplication.isRecordUnderGoing() == Constants.CONST_RECORDER_SERVICE_WORKS_FOR_NOTE) {
                builder.setMessage("You have an audio recording under progress:");
                items = new String [] {"Stop and add a new note","Go to current recording","Cancel"};
            }
            else {
                builder.setMessage("You have an phone recording under progress:");
                items = new String [] {"Stop and add a new note","","Cancel"};
            }

            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                   if (which==0){

                        Intent intent = new Intent(ActivityAddAudioNote.this, RecorderNoteService.class);
                        //false is just fake we don't need value.
                        intent.putExtra(Constants.EXTRA_STOP_RECORD, false);
                        startService(intent);

                        intentHandler(intent);
                   }
                    else if(which==1){
                        intentHandler(intent);
                    }

                }
            });

            builder.setCancelable(true);
            myDialog = builder.create();
            myDialog.show();

        }else{
            intentHandler(intent);
        }*/


    }


    private void intentHandler(Intent intent){

        //if(intent.getAction(Constants.EXTRA_CURRENT_DBID_RECORD_SERVICE)!=null)
        if(intent.hasExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_START_RECORD)){

            Log.e("DBG","XXXXXXXXXXXXXXXXXXx");

           /* if (myApplication.isRecordUnderGoing()==Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE) {
                AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddAudioNote.this);


                adb.setMessage("You have a phone call recording under progress. Stop it and try again!");


                adb.setTitle("Note");
                adb.setIcon(R.drawable.ic_action_phone);

                adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveNote();
                        finish();
                    }
                });

                adb.show();

                return;
            }else {
*/

/*
                if (myApplication.isRecordUnderGoing() == Constants.CONST_RECORDER_SERVICE_WORKS_FOR_NOTE) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddAudioNote.this);


                    adb.setMessage("You have an audio note recording under progress. Stop it and try again!");

                    adb.setTitle("Note");
                    adb.setIcon(R.drawable.ic_action_phone);

                    adb.setPositiveButton("Ok open it!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbId = myApplication.getLastOpenedNoteId();
                            myApplication.setCurrentOpenedNoteId(dbId);

                            noteInfoRealmStruct = realmNoteHelper.getNoteById(dbId);
                            txtTitle.setText(noteInfoRealmStruct.getTitle());
                            txtDescription.setText(noteInfoRealmStruct.getDescription());


                            currentNoteType = Constants.CONST_IS_EDIT_TEXT_AND_RECORD;

                            mnuItemPlayRecord.setVisible(true);
                            btnDeleteRecord.setVisibility(View.INVISIBLE);
                            btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                            mnuItemDeleteNote.setVisible(true);
                            mnuItemSaveNote.setVisible(true);
                            recorderControlsLayout.setVisibility(View.VISIBLE);


                        }
                    });

                    adb.setNegativeButton("Continue new", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Check we have an opened note then save it and open a new
                            if(myApplication.getCurrentOpenedNoteId() > Constants.CONST_NULL_ZERO)
                            {
                                saveNote();
                            }

                            initVariables();

                            currentNoteType = Constants.CONST_IS_TEXT_AND_RECORD;

                            mnuItemPlayRecord.setVisible(true);
                            btnDeleteRecord.setVisibility(View.INVISIBLE);
                            btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                            mnuItemDeleteNote.setVisible(true);
                            mnuItemSaveNote.setVisible(true);
                            recorderControlsLayout.setVisibility(View.VISIBLE);

                            setBackGroundOfView(btnNoStopRecord, R.drawable.buttons_recorder_bg, true);
                            startRecord();
                            toggleNoStopRecord = true;

                        }
                    });

                    adb.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            saveNote();
                            finish();
                        }
                    });

                    adb.show();

                    return;
                }
                else{*/


                //    initVariables();

//            if(intent.hasExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD)) {
            // If we have any popup message then this blocks the new session by user.

             //if  {

                 if ((myApplication.tmpCurrentAudioNoteInfoStruct != null) && (myApplication.isRecordUnderGoing()==Constants.CONST_RECORDER_SERVICE_WORKS_FOR_NOTE)) {
                     Log.e("DBG","AA");

                     if(myApplication.tmpCurrentAudioNoteInfoStruct.getId() > 0)

                     //if it is an open note and didn't saved
                     if (!realmNoteHelper.isExist(myApplication.tmpCurrentAudioNoteInfoStruct.getId())) {
                           saveNote(false);
                     }
                     dbId = myApplication.tmpCurrentAudioNoteInfoStruct.getId();

                     noteInfoRealmStruct = realmNoteHelper.getNoteById(dbId);

                     myApplication.tmpCurrentAudioNoteInfoStruct.setId(dbId);
                     myApplication.tmpCurrentAudioNoteInfoStruct.setDescription(noteInfoRealmStruct.getDescription());
                     myApplication.tmpCurrentAudioNoteInfoStruct.setTitle(noteInfoRealmStruct.getTitle());
                     myApplication.tmpCurrentAudioNoteInfoStruct.setHasAudio(true);
                     myApplication.tmpCurrentAudioNoteInfoStruct.setTag(noteInfoRealmStruct.getTag());

                     txtTitle.setText(noteInfoRealmStruct.getTitle());
                     txtDescription.setText(noteInfoRealmStruct.getDescription());

                         setBackGroundOfView(btnNoStopRecord, R.drawable.buttons_recorder_bg, true);
                         toggleNoStopRecord = true;
                 }
                 else
                 {
                     Log.e("DBG","BB");

                     if (myApplication.tmpCurrentAudioNoteInfoStruct != null) {
                         if (myApplication.tmpCurrentAudioNoteInfoStruct.getId() > 0) {
                             saveNote(false);
                         }
                     }

                     initVariables();

                     myApplication.tmpCurrentAudioNoteInfoStruct = new TempNoteInfoStruct();
                     myApplication.tmpCurrentAudioNoteInfoStruct.setId(dbId);

                     mnuItemPlayRecord.setVisible(true);
                     btnDeleteRecord.setVisibility(View.INVISIBLE);
                     btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                     mnuItemDeleteNote.setVisible(true);
                     mnuItemSaveNote.setVisible(true);
                     recorderControlsLayout.setVisibility(View.VISIBLE);
                     currentNoteType = Constants.CONST_IS_TEXT_AND_RECORD;

                     if(Prefs.getBoolean(Constants.PREF_AUTO_RUN_RECORDER_ON_AUDIO_NOTES,false)) {
                         setBackGroundOfView(btnNoStopRecord, R.drawable.buttons_recorder_bg, true);
                         startRecord();
                         toggleNoStopRecord = true;
                     }
                 }




/*
            if(!dontAcceptAnyNewSession) {
                if (weTypedSomethingNew || myApplication.tmpCurrentAudioNoteInfoStruct != null) {
                    if (myApplication.tmpCurrentAudioNoteInfoStruct.getId() > 0)
                        saveNote();
                }

                initVariables();

                myApplication.tmpCurrentAudioNoteInfoStruct = new TempNoteInfoStruct();
                myApplication.tmpCurrentAudioNoteInfoStruct.setId(dbId);

                //          }

                currentNoteType = Constants.CONST_IS_TEXT_AND_RECORD;

                mnuItemPlayRecord.setVisible(true);
                btnDeleteRecord.setVisibility(View.INVISIBLE);
                btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                mnuItemDeleteNote.setVisible(true);
                mnuItemSaveNote.setVisible(true);
                recorderControlsLayout.setVisibility(View.VISIBLE);

                setBackGroundOfView(btnNoStopRecord, R.drawable.buttons_recorder_bg, true);
                startRecord();
                toggleNoStopRecord = true;

            }*/
        }
        else if(intent.hasExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_NO_RECORD)){

            Log.e("DBG","EXTRA_TAKE_NEW_NOTE_AND_NO_RECORD");

            //Check we have an opened note then save it and open a new
            if(myApplication.getCurrentOpenedNoteId() > Constants.CONST_NULL_ZERO)
            {
                saveNote(false);
            }

            initVariables();

            Log.e("DBG","AAAAAAAAAAAAAAAAAAAAAA");

            currentNoteType = Constants.CONST_IS_ONLY_TEXT;
            mnuItemPlayRecord.setVisible(false);
            mnuItemDeleteNote.setVisible(false);
            mnuItemSaveNote.setVisible(true);
            recorderControlsLayout.setVisibility(View.GONE);

        }
        else if(intent.hasExtra(Constants.EXTRA_EDIT_NOTE_AND_NO_RECORD)){
            /*****************************************************************************************************************
             * ***************************************************************************************************************
             * now you should get the db key intent and fetch the record from the db and fill it in the text edits.
             * ***************************************************************************************************************
             *****************************************************************************************************************/
            dbId = intent.getIntExtra(Constants.EXTRA_NOTE_DB_ID_FOR_EDIT,Constants.CONST_NULL_ZERO);

            if(dbId>0){
                weAreInEditMode=true;

                noteInfoRealmStruct = realmNoteHelper.getNoteById(dbId);
                txtTitle.setText(noteInfoRealmStruct.getTitle());
                txtDescription.setText(noteInfoRealmStruct.getDescription());

                currentNoteType = Constants.CONST_IS_EDIT_ONLY_TEXT;
                mnuItemPlayRecord.setVisible(false);
                mnuItemDeleteNote.setVisible(false);
                mnuItemSaveNote.setVisible(true);
                recorderControlsLayout.setVisibility(View.GONE);
            }


        }
        else if(intent.hasExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD)){

            /*****************************************************************************************************************
             * ***************************************************************************************************************
             * now you should get the db key intent and fetch the record from the db and fill it in the text edits.
             * ***************************************************************************************************************
             *****************************************************************************************************************/
            initVariables();

            dbId = intent.getIntExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD,Constants.CONST_NULL_ZERO);

            if(dbId>0){
                weAreInEditMode=true;
                noteInfoRealmStruct = realmNoteHelper.getNoteById(dbId);
                txtTitle.setText(noteInfoRealmStruct.getTitle());
                txtDescription.setText(noteInfoRealmStruct.getDescription());

            }
            mnuItemPlayRecord.setVisible(true);
            btnDeleteRecord.setVisibility(View.INVISIBLE);
            btnRecordsListPlayer.setVisibility(View.INVISIBLE);
            mnuItemDeleteNote.setVisible(true);
            mnuItemSaveNote.setVisible(true);
            recorderControlsLayout.setVisibility(View.VISIBLE);

            setBackGroundOfView(btnNoStopRecord, R.drawable.buttons_recorder_bg, true);
            startRecord();
            toggleNoStopRecord = true;

            currentNoteType = Constants.CONST_IS_EDIT_TEXT_AND_RECORD;

        }


        //myApplication.setByePassRecordBroadcastReceiverForOnce(false);


    }


    private void saveNote(boolean showPrompt) {
        String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();

        if (!myApplication.isAudioNoteSaved() || title.length() == 0) {
            Log.e("DBG","Bangoood");



            // If user didn't add any title
            if (title.length() == 0) {
                title = "Untitled";
                txtTitle.setText(title);
                myApplication.tmpCurrentAudioNoteInfoStruct.setTitle(title);
            } else {
                title = myApplication.tmpCurrentAudioNoteInfoStruct.getTitle();
                description = myApplication.tmpCurrentAudioNoteInfoStruct.getDescription();
            }

            myApplication.tmpCurrentAudioNoteInfoStruct.setHasAudio(true);

            Date updateTime = new Date();

            Date createdTime = null;

            if (weAreInEditMode)
                createdTime = noteInfoRealmStruct.getCreateTimeStamp();

            if (currentNoteType == Constants.CONST_IS_ONLY_TEXT || currentNoteType == Constants.CONST_IS_EDIT_ONLY_TEXT)
                theNoteIsAudioType = false;
            else
                theNoteIsAudioType = true;

            if (!weAreInEditMode)
                realmNoteHelper.addNote(dbId, title, description, theNoteIsAudioType, updateTime, createdTime, null, null, Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL, null, 0, 0);
            else
                realmNoteHelper.updateNotes(dbId, title, description, updateTime, 0, 0);

          /*  noteInfoRealmStruct = realmNoteHelper.getNoteById(dbId);
            txtTitle.setText(noteInfoRealmStruct.getTitle());
            txtDescription.setText(noteInfoRealmStruct.getDescription());
*/
            myApplication.setAudioNoteSaved(true);

            if (showPrompt)
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }

    private void initVariables(){

        myApplication.tmpCurrentAudioNoteInfoStruct = new TempNoteInfoStruct();
        // Creating unique id for db as primary key
        dbId = (int) (System.currentTimeMillis() / 1000);
        myApplication.tmpCurrentAudioNoteInfoStruct.setId(dbId);
        //myApplication.setLastOpenedNoteId(dbId);
        //myApplication.setCurrentOpenedNoteId(Constants.CONST_NULL_ZERO);
        txtDescription.setText("");
        txtTitle.setText("");
        txtTimeLaps.setVisibility(View.INVISIBLE);
        currentNoteType= Constants.CONST_NULL_ZERO;
        theNoteIsAudioType=false;
        weAreInEditMode=false;
        currentNoteType=0;
        toggleNoStopRecord=false;
        //weTypedSomethingNew=false;

        // Flashing title
        ColorDrawable[] color = {new ColorDrawable(Color.RED), new ColorDrawable(ContextCompat.getColor(this, R.color.blue_eyes))};
        TransitionDrawable trans = new TransitionDrawable(color);
        mToolbar.setBackground(trans);
        trans.startTransition(5000);
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

         myApplication = (MyApplication)getApplicationContext();

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

                dontAcceptAnyNewSession =true;

                        AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddAudioNote.this);


                        adb.setMessage("Are you sure want to delete your latest record?");


                        adb.setTitle("Note");


                        //adb.setIcon(android.R.drawable.ic_dialog_alert);


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
                                    dontAcceptAnyNewSession=false;
                                    Log.e(TAG, "Record deleted!");
                                }

                            }
                        });


                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dontAcceptAnyNewSession=false;
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
                    Log.e("DBGGGGGGG", dbId + "");
                    if(myApplication.isPlaying())
                    {
                        Intent intent = new Intent(ActivityAddAudioNote.this,PlayerService.class);
                        intent.setAction(Constants.ACTION_STOP);
                        startService(intent);
                    }

                    Intent intent = new Intent(ActivityAddAudioNote.this, ActivityRecordsPlayList.class);
                    intent.putExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID, String.valueOf(dbId));
                    startActivity(intent);

                }
            }
        });

    }

    private boolean isThereAnyRecordInPath(String recordedFilePath) {

        File f = new File(recordedFilePath);

        //f = new File(f.getAbsolutePath());

        String dir = f.getParent();

        File dirAsFile = f.getParentFile();

        File[] listOfFiles = dirAsFile.listFiles();

        if (listOfFiles.length > 1)
            return true;
        else
            return false;
    }

    private boolean startRecord() {



        if (myApplication.isRecordUnderGoing()!=Constants.CONST_RECORDER_SERVICE_IS_FREE || myApplication.isPlaying()) {

            AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddAudioNote.this);

            if(myApplication.isPlaying())
                adb.setMessage("The player is playing please stop it!");
            else
                adb.setMessage("You have a phone call or another recording recording under progress. Stop it and try again!");

            adb.setTitle("Note");

            adb.setIcon(R.drawable.ic_action_phone);

            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            adb.show();

            return false;
       }
        Log.e("DBGGGGGGG", "Hongla");

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
        Log.e("DBG","Adelante");
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
    protected void onPause() {
        super.onPause();
      /*  String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();*/
        Log.e("DBG","Add activity onPause()");
            saveNote(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

/*@Override
    public void onBackPressed() {

        String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();

        if(title.length() > 0 || description.length()>0 || weTypedSomethingNew) {

            AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddAudioNote.this);


            adb.setMessage("You have a note do you wish to save it?");


            adb.setTitle("Note");

            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    saveNote();
                    finish();
                }
            });

            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            adb.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            adb.show();


        }
        else {
            finish();
        }



    }*/

    /*private static final File EXTERNAL_STORAGE_DIRECTORY
                = getDirectory("EXTERNAL_STORAGE", "/sdcard");
        static File getDirectory(String variableName, String defaultPath) {
            String path = System.getenv(variableName);
            return path == null ? new File(defaultPath) : new File(path);
        }*/


    private boolean doWeHaveRecords(){

        String folderOfRecords = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(dbId));

        boolean foundAnyFile= false;
        File file = new File(folderOfRecords);
//        Log.e("MAMAM", file.exists() + " " + file.isDirectory() );

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

 private void animateTimeLaps(View view,boolean startStopAnimate){

    if(startStopAnimate) {
        //recorder.resetTimer();
       // recorder.startTimer();
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        view.startAnimation(animation);
    }
    else {
      //  recorder.stopTimer();
        view.clearAnimation();
    }
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_add, menu);

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
            saveNote(true);
            return true;
        }else if (id == R.id.action_play_record) {

            if ((myApplication.isRecordUnderGoing()==Constants.CONST_RECORDER_SERVICE_IS_FREE) && (latestRecordFileName != null) && !latestRecordFileName.isEmpty()) {

                File file = new File(latestRecordFileName);

                if (!file.exists()) {
                    Log.e(TAG, "No record to play");
                    return false;
                }
                playLatestRecord();

            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
    private void playLatestRecord() {
        if(myApplication.isPlaying())
        {
            Intent intent = new Intent(ActivityAddAudioNote.this,PlayerService.class);
            intent.setAction(Constants.ACTION_STOP);
            startService(intent);
        }
        Intent intent = new Intent(this,ActivityPlayerPortrait.class);
        intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, dbId);
        intent.putExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER, latestRecordFileName);
        startActivity(intent);
    }

    public void startTimer()
    {
        txtTimeLaps.setVisibility(View.VISIBLE);
       txtTimeLaps.start();
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
                        mnuItemDeleteNote.setVisible(true);

                        if(isThereAnyRecordInPath(latestRecordFileName))
                        {
                            btnRecordsListPlayer.setVisibility(View.VISIBLE);
                        }else
                        {
                            btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                        }

                    }
                    else if(msgRecordService == Constants.REPORT_RECORD_STOPPED_BY_NOTIFICATION_TO_ACTIVITY){
                        Log.e("DBG","Callam");
                        /*latestRecordFileName = intent.getStringExtra(Constants.REPORT_RECORDED_FILE_TO_ACTIVITY_FILENAME);
                        mnuItemPlayRecord.setVisible(true);
                        btnDeleteRecord.setVisibility(View.VISIBLE);
                        mnuItemDeleteNote.setVisible(true);

                        if(isThereAnyRecordInPath(latestRecordFileName))
                        {
                            btnRecordsListPlayer.setVisibility(View.VISIBLE);
                        }else
                        {
                            btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                        }*/

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