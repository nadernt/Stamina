package com.fleecast.stamina.notetaking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.NotificationHelper;
import com.fleecast.stamina.utility.Prefs;
import com.fleecast.stamina.utility.Utility;

import java.io.File;


public class AddActivity extends AppCompatActivity {


    private RealmNoteHelper realmNoteHelper;
    private EditText inputDescription;
    private EditText inputTitle;
    private Toolbar mToolbar;                              // Declaring the Toolbar Object
    private ImageView btnDeleteRecord;
    private MenuItem mnuItemDeleteNote;
    private MenuItem mnuItemSaveNote;
    private MenuItem mnuItemPlayRecord;
    private View textviewTimeLapse;
    private boolean weHaveRecordedSomething= false, weTypedSomethingNew = false;
    private int dbId;
    private MyApplication myApplication;
    private final static String TEMP_FILE = "temp";
    private NotificationHelper nfh;
    private String fileExtension="";
    private String TAG = "Add Activity";

    private WindowManager windowManager;
    private Point szWindow = new Point();
    private RelativeLayout recordButtonsView;
    private ImageView chatHead;
    private WindowManager.LayoutParams params;
    private ImageView btnNoStopRecord,btnTapRecord,btnStopRecord;
    private RelativeLayout recorderControlsLayout;
    private boolean toggleNoStopRecord=false;
    private Chronometer txtTimeLaps;
    private String latestRecordFileName;
    private ImageView btnRecordsListPlayer;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        intentHandler(intent);

        Log.e(TAG, "Magic " + dbId);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy killRecordService");
        killRecordService();
        //stopService(new Intent(this,RecorderService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_add_activity);



        startService(new Intent(this,RecorderService.class));

        // Creating unique id for db as primary key
        dbId = (int) (System.currentTimeMillis() / 1000);

        myApplication =  (MyApplication)getApplicationContext();

        realmNoteHelper = new RealmNoteHelper(AddActivity.this);

        nfh = new NotificationHelper(this);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object

        setSupportActionBar(mToolbar);


       // pathToWorkingDirectory =  ExternalStorageManager.prepareWorkingDirectory(this) + Constants.CONST_WORKING_DIRECTORY_NAME;

       /* if(pathToWorkingDirectory.length()==0)
            storageAvail = false;
        else
            storageAvail=true;*/

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);



        inputTitle = (EditText) findViewById(R.id.inputTitle);
        inputDescription = (EditText) findViewById(R.id.inputDescription);

        inputDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                weTypedSomethingNew = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                weTypedSomethingNew = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        populateGUI();

    }

    /*private PendingIntent createPendingIntent() {
        Log.d(Utility.LogTag, "Fookyou");

        Intent intent = new Intent(getBaseContext(), AddActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private Notification createNotificationCompat(PendingIntent intent) {

        return  new NotificationCompat.Builder(this)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.notificationText))
                .setSmallIcon(R.drawable.mic)
                .setContentIntent(intent)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .build();

    }
*/

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

                setBackGroundOfView(btnTapRecord,0,false);

                if(!toggleNoStopRecord)
                {
                    setBackGroundOfView(btnNoStopRecord,R.drawable.buttons_recorder_bg,true);
                        startRecord();
                }
                else
                {
                    setBackGroundOfView(btnNoStopRecord,0,false);
                        stopRecord();
                }

                toggleNoStopRecord = !toggleNoStopRecord;
            }
        });

        btnTapRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
          //      Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:


  //                      vib.vibrate(30);
                        setBackGroundOfView(btnNoStopRecord, 0, false);
                        setBackGroundOfView(btnTapRecord, R.drawable.buttons_recorder_bg, true);
                        startRecord();

                        return true;
                    case MotionEvent.ACTION_UP:
                        setBackGroundOfView(btnTapRecord, 0, false);
                        stopRecord();
//                        vib.vibrate(30);
                        return true;
                }

                return false;
            }
        });

        btnDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        AlertDialog.Builder adb = new AlertDialog.Builder(AddActivity.this);


                        adb.setMessage("Are you sure want to delete this record?");


                        adb.setTitle("Note");


                        adb.setIcon(android.R.drawable.ic_dialog_alert);


                        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                File file = new File(latestRecordFileName);

                                if (file.exists()) {

                                    file.delete();

                                    mnuItemPlayRecord.setVisible(false);
                                    btnDeleteRecord.setVisibility(View.INVISIBLE);

                                    if(isThereAnyRecordInPath(latestRecordFileName))
                                    {
                                        weHaveRecordedSomething = true;
                                        btnRecordsListPlayer.setVisibility(View.VISIBLE);
                                    }else
                                    {
                                        btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                                        weHaveRecordedSomething = false;
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

                startActivity(new Intent(AddActivity.this,ActivityRecordsPlayList.class));

            }
        });
        /*        btnStopRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                        vib.vibrate(30);
                    }
                });*/
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

    private void startRecord() {

        boolean hasEnoughSpace = ExternalStorageManager.isThereEnoughSpaceOnStorage();
        if (hasEnoughSpace) {
            Intent intent = new Intent(this, RecorderService.class);
            //false is just fake we don't need value.
            intent.putExtra(Constants.EXTRA_NEW_RECORD, false);
            intent.putExtra(Constants.EXTRA_RECORD_FILENAME, String.valueOf(dbId));
            startService(intent);
            resetTimer();
            startTimer();
        } else {
            Utility.showMessage("You don't have enough empty space.", "No Empty Space", AddActivity.this);
        }

    }

    private void stopRecord() {

            Intent intent = new Intent(this, RecorderService.class);
            //false is just fake we don't need value.
            intent.putExtra(Constants.EXTRA_STOP_RECORD, false);
            startService(intent);
            stopTimer();
    }

    private void killRecordService() {
        Intent intent = new Intent(this,RecorderService.class);
        //false is just fake we don't need value.
        intent.putExtra(Constants.EXTRA_STOP_SERVICE,false);
        startService(intent);
    }

    private int calcPixelIndependent(int pixelToConvert) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelToConvert, getResources().getDisplayMetrics());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();

        if(title.length() > 0 || description.length()>0 || weTypedSomethingNew) {
            saveNote();
        }
        else { // Cleanup the temporary file

            //File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

            //file.delete();
        }

    }

    /*private static final File EXTERNAL_STORAGE_DIRECTORY
                = getDirectory("EXTERNAL_STORAGE", "/sdcard");
        static File getDirectory(String variableName, String defaultPath) {
            String path = System.getenv(variableName);
            return path == null ? new File(defaultPath) : new File(path);
        }*/
private void saveNote(){
    this.setVisible(false);


   /* if (recorder.isRecording()) {
        recorder.recordMedia(false, chosenSourceOfRecord);
        weHaveRecordedSomething = true;
    }*/

    String title = inputTitle.getText().toString().trim();
    String description = inputDescription.getText().toString().trim();

    // If user didn't add any title
    if(title.length()==0)
        title= "Untitled";

    // We have recorded something we rename it to real file name with ID stamp.
    if(weHaveRecordedSomething)
    {
    //    File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

      //  file.renameTo(new File(pathToWorkingDirectory + File.separator + String.valueOf(dbId) + fileExtension));
    }

    realmNoteHelper.addNote(dbId, title, description, weHaveRecordedSomething,null,null,null,null,-1,null,0,0);

    /*Intent i = new Intent(AddActivity.this, NoteTakingRecyclerViewActivity.class);
    startActivity(i);*/
    finish();
}

    private void populateUserInterface(Menu menu, boolean populateForAddOrEdit){

        mnuItemDeleteNote = menu.findItem(R.id.action_delete);
        mnuItemSaveNote = menu.findItem(R.id.action_save);
        mnuItemPlayRecord = menu.findItem(R.id.action_play_record);

        textviewTimeLapse = getLayoutInflater().inflate(R.layout.time_laps, null);
        mToolbar.addView(textviewTimeLapse);

        //User wants add.
        if(populateForAddOrEdit) {

         //   if(myApplication.isUserWantsRecordPhoneCalls())

            btnDeleteRecord.setVisibility(View.INVISIBLE);
            btnRecordsListPlayer.setVisibility(View.INVISIBLE);
            mnuItemPlayRecord.setVisible(false);
            mnuItemDeleteNote.setVisible(false);
            mnuItemSaveNote.setVisible(true);

        }
        else
        {
            mnuItemPlayRecord.setVisible(true);
            btnDeleteRecord.setVisibility(View.VISIBLE);
            btnRecordsListPlayer.setVisibility(View.VISIBLE);
            mnuItemDeleteNote.setVisible(true);
            mnuItemSaveNote.setVisible(true);
        }

        txtTimeLaps  = (Chronometer) textviewTimeLapse.findViewById(R.id.txtTimeLaps);
        txtTimeLaps.setVisibility(View.INVISIBLE);
        //recorder = new RecorderService(this,txtTimeLaps,pathToWorkingDirectory,TEMP_FILE);

        Intent intent = getIntent();

        intentHandler(intent);

    }

    private void intentHandler(Intent intent){

        /*switch (phoneEvent){
            case 0: i.putExtra("PHONE_OUT_GOING_CALL_STARTED", 0);
                break;
            case 1: i.putExtra("PHONE_INCOMING_CALL_RECEIVED", 1);
                break;
            case 2: i.putExtra("PHONE_INCOMING_CALL_ANSWERED", 2);
                break;
            case 3: i.putExtra("PHONE_MISSING_CALL", 3);
                break;
            case 4:  i.putExtra("PHONE_INCOMING_CALL_ENDED", 4);
                break;
            case 5:  i.putExtra("PHONE_OUT_GOING_CALL_ENDED", 5);
                break;
        }*/

        Log.e(TAG, "Audio intent?" + intent.getBooleanExtra("audio", false));
        Log.e(TAG, "Phone call intent?" + intent.getBooleanExtra("phone_call", false));


        if(intent.getBooleanExtra("audio",false) && !myApplication.isRecordUnderGoing()){


            Log.e(TAG, "audio note");


          //  chosenSourceOfRecord = Prefs.getInt("AudioRecorderSource",MediaRecorder.AudioSource.MIC);

//            recordAudio(chosenSourceOfRecord);

        }
        else if(intent.getBooleanExtra("phone_call",false)){


            Log.e(TAG, "Phone call");

            if(Prefs.getBoolean("IsRecordPhoneCall",false)) {





                if (myApplication.isRecordUnderGoing()){


                    Log.e(TAG, "We have one record under going so app rejects the phone record.So bye bye!");
                 //   finish();

                }else {

/*
                    public static int PHONE_OUT_GOING_CALL_STARTED = 0;
                    public static final int PHONE_INCOMING_CALL_RECEIVED = 1;
                    public static final int PHONE_INCOMING_CALL_ANSWERED = 2;
                    public static int PHONE_MISSING_CALL = 3;
                    public static final int PHONE_INCOMING_CALL_ENDED = 4;
                    public static int PHONE_OUT_GOING_CALL_ENDED = 5;
*/

                    if (intent.getIntExtra("phone_state", -1) == Constants.PHONE_OUT_GOING_CALL_STARTED || (intent.getIntExtra("phone_state", -1) == Constants.PHONE_INCOMING_CALL_RECEIVED)) {

                        // This record check is for the time we are getting a new call while we are recording another one so we bypass the new one.
                        if (!myApplication.isRecordUnderGoing()) {
                            Log.e(TAG, "Record phone call started");
                         //   chosenSourceOfRecord = MediaRecorder.AudioSource.VOICE_CALL;

                            //recordAudio(chosenSourceOfRecord);
                        }

                    } else if ((intent.getIntExtra("phone_state", -1) == Constants.PHONE_INCOMING_CALL_RECEIVED))
                    {



                    }



                }

            }
            else
            {
                Log.e(TAG, "We have not chosen app record menu option. So bye bye!");
                //finish();
            }



        }
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
            saveNote();
            return true;
        }else if (id == R.id.action_play_record) {

            if (!myApplication.isRecordUnderGoing()) {

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

        Intent intent = new Intent(this,ActivityPlayerPortrait.class);
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
            if (intent.getAction().equals(Constants.INTENTFILTER_RECORD_SERVICE)) {
                if(intent.hasExtra(Constants.EXTRA_RECORD_SERVICE_ERROR))
                {
                    int msgRecordService =  intent.getIntExtra(Constants.EXTRA_RECORD_SERVICE_ERROR,-1);
                    if(msgRecordService == Constants.REPORT_RECORD_ERROR_TO_ACTIVITY) {
                        Utility.showMessage("Hey you are too much fast don't rush at least hold on one second! ", "Ohhh", AddActivity.this);
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
                }
            }
        }

    };
} 