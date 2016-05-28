package com.fleecast.stamina.notetaking;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.settings.ActivitySettings;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.NotificationHelper;
import com.fleecast.stamina.utility.Prefs;

import java.io.File;


public class AddActivity extends AppCompatActivity{


    private RealmNoteHelper realmNoteHelper;
    private EditText inputDescription;
    private EditText inputTitle;
    private Toolbar mToolbar;                              // Declaring the Toolbar Object
    private MenuItem mnuItemDeleteRecord, mnuItemDeleteNote,mnuItemSaveNote, mnuItemRecord,mnuItemPlayRecord,mnuItemRecordCall;
    private View textviewTimeLapse;
    private TextView txtTimeLaps;
    private Recorder recorder;
    private String pathToWorkingDirectory;
    private boolean storageAvail = false;
    private boolean weHaveRecordedSomething= false, weTypedSomethingNew = false;
    private int dbId;
    private MyApplication myApplication;
    private final static String TEMP_FILE = "temp";
    private int chosenSourceOfRecord = 0;
    private NotificationHelper nfh;
    private String fileExtension="";
    //private CallReceiver callReceiver;
    //private PhonecallReceiver mReceiver;
    private String TAG = "Add Activity";

    private WindowManager windowManager;
    private Point szWindow = new Point();
    private RelativeLayout recordButtonsView;
    private ImageView chatHead;
    private WindowManager.LayoutParams params;
    private ImageView btnNoStopRecord,btnTapRecord,btnStopRecord;
    private RelativeLayout recorderControlsLayout;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        intentHandler(intent);

        Log.e(TAG, "Magic " + dbId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_add_activity);

        // Creating unique id for db as primary key
        dbId = (int) (System.currentTimeMillis() / 1000);

        myApplication =  (MyApplication)getApplicationContext();

        realmNoteHelper = new RealmNoteHelper(AddActivity.this);

        nfh = new NotificationHelper(this);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object

        setSupportActionBar(mToolbar);


        pathToWorkingDirectory =  ExternalStorageManager.prepareWorkingDirectory(this);

        if(pathToWorkingDirectory.length()==0)
            storageAvail = false;
        else
            storageAvail=true;

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

//Log.e(TAG , getApplication().getPackageName());
        /******************************************88
         * *****************************************
         ******************************************/

 /*   IntentFilter intentFilter = new IntentFilter();

    intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
    //intentFilter.addAction("android.intent.action.PHONE_STATE");
    intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);


    mReceiver = new PhonecallReceiver(this);

    registerReceiver(mReceiver, intentFilter);*/

        /******************************************88
         * *****************************************
         ******************************************/
       //mToolbar.getItem(R.id.action_save).setVisible(false);
        /*save = (Button) findViewById(R.id.save);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


     //   startService(new Intent(this, ChatHeadRecordService.class));

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

        btnNoStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                vib.vibrate(30);
            }
        });

        btnTapRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                vib.vibrate(30);
            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                vib.vibrate(30);
            }
        });
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

            File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

            file.delete();
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


    if (recorder.isRecording()) {
        recorder.recordMedia(false, chosenSourceOfRecord);
        weHaveRecordedSomething = true;
    }

    String title = inputTitle.getText().toString().trim();
    String description = inputDescription.getText().toString().trim();

    // If user didn't add any title
    if(title.length()==0)
        title= "Untitled";

    // We have recorded something we rename it to real file name with ID stamp.
    if(weHaveRecordedSomething)
    {
        File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

        file.renameTo(new File(pathToWorkingDirectory + File.separator + String.valueOf(dbId) + fileExtension));
    }

    realmNoteHelper.addNote(dbId, title, description, weHaveRecordedSomething,null,null,null,null,-1,null,0,0);

    /*Intent i = new Intent(AddActivity.this, NoteTakingRecyclerViewActivity.class);
    startActivity(i);*/
    finish();
}

    private void populateUserInterface(Menu menu, boolean populateForAddOrEdit){

        mnuItemDeleteRecord = menu.findItem(R.id.action_delete_record);
        mnuItemDeleteNote = menu.findItem(R.id.action_delete);
        mnuItemSaveNote = menu.findItem(R.id.action_save);
        mnuItemRecord = menu.findItem(R.id.action_record);
        mnuItemPlayRecord = menu.findItem(R.id.action_play_record);
        mnuItemRecordCall = menu.findItem(R.id.action_listen_for_phone_call);


        if(Prefs.getBoolean("IsRecordPhoneCall",false))
            mnuItemRecordCall.setChecked(true);
        else
            mnuItemRecordCall.setChecked(false);

        textviewTimeLapse = getLayoutInflater().inflate(R.layout.time_laps, null);
        mToolbar.addView(textviewTimeLapse);

        //User wants add.
        if(populateForAddOrEdit) {

         //   if(myApplication.isUserWantsRecordPhoneCalls())

            mnuItemDeleteRecord.setVisible(false);
            mnuItemPlayRecord.setVisible(false);
            mnuItemDeleteNote.setVisible(false);
            mnuItemSaveNote.setVisible(true);
            mnuItemRecord.setVisible(true);

        }
        else
        {
            mnuItemPlayRecord.setVisible(true);
            mnuItemDeleteRecord.setVisible(true);
            mnuItemDeleteNote.setVisible(true);
            mnuItemSaveNote.setVisible(true);
            mnuItemRecord.setVisible(true);

        }

        txtTimeLaps  = (Chronometer) textviewTimeLapse.findViewById(R.id.txtTimeLaps);
        txtTimeLaps.setVisibility(View.INVISIBLE);
        recorder = new Recorder(this,txtTimeLaps,pathToWorkingDirectory,TEMP_FILE);

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


        if(intent.getBooleanExtra("audio",false) && !myApplication.isRecordIsUnderGoing()){


            Log.e(TAG, "audio note");


            chosenSourceOfRecord = Prefs.getInt("AudioRecorderSource",MediaRecorder.AudioSource.MIC);

            recordAudio(chosenSourceOfRecord);

        }
        else if(intent.getBooleanExtra("phone_call",false)){


            Log.e(TAG, "Phone call");

            if(Prefs.getBoolean("IsRecordPhoneCall",false)) {





                if (myApplication.isRecordIsUnderGoing()){


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
                        if (!myApplication.isRecordIsUnderGoing()) {
                            Log.e(TAG, "Record phone call started");
                            chosenSourceOfRecord = MediaRecorder.AudioSource.VOICE_CALL;

                            recordAudio(chosenSourceOfRecord);
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
        recorder.resetTimer();
        recorder.startTimer();
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        view.startAnimation(animation);
    }
    else {
        recorder.stopTimer();
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
        } else if (id == R.id.action_record) {
            recordAudio(MediaRecorder.AudioSource.MIC);

        } else if (id == R.id.action_delete_record) {

            AlertDialog.Builder adb = new AlertDialog.Builder(this);


            adb.setMessage("Are you sure want to delete this record?");


            adb.setTitle("Note");


            adb.setIcon(android.R.drawable.ic_dialog_alert);


            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

                    if (file.exists()) {
                        file.delete();
                        mnuItemPlayRecord.setVisible(false);
                        mnuItemDeleteRecord.setVisible(false);
                        weHaveRecordedSomething = false;
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


            return true;

        } else if (id == R.id.action_play_record) {

            if (storageAvail && !recorder.isRecording()) {

                File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

                if (!file.exists()) {
                    Log.e(TAG, "No record to play");
                    return false;
                }
                recorder.playMedia(true);
            }
            return true;

        } else if(id == R.id.action_listen_for_phone_call){

            /*int status = this.getPackageManager().getComponentEnabledSetting(component);
            if(status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                Log.d("AAAAAAAAAAAAAAAAAAAAa", "receiver is enabled");
            } else if(status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                Log.d("AAAAAAAAAAAAAAAAAAAAa", "receiver is disabled");
            }*/

            ComponentName component = new ComponentName(this, PhonecallReceiver.class);

            //Disable call recording BroadcastReceiver (class PhonecallReceiver)
            if(mnuItemRecordCall.isChecked()) {
                //Disable
                this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
                Prefs.putBoolean("IsRecordPhoneCall", false);
                mnuItemRecordCall.setChecked(false);
            }
            else { //Enable call recording BroadcastReceiver (class PhonecallReceiver)
                //Enable
                this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
                Prefs.putBoolean("IsRecordPhoneCall", true);
                mnuItemRecordCall.setChecked(true);
            }


        }
         else if (id == R.id.action_settings) {

            // First stop probable record.
            //recorder.recordMedia(false,-1);

            Intent intent = new Intent(this,ActivitySettings.class);

            // We just pass this code and and run activity for result in order to have modal dialog!

            int fakeResultCode = 101;

            startActivityForResult(intent,101);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean recordAudio(int typeOfRecord) {
        chosenSourceOfRecord = typeOfRecord;
        if (storageAvail) {

            if (!recorder.isRecording()) {
                //nfh.removeNotification(Constants.AUDIO_RECORDING_NOTIFICATION_ID);
                Intent intent = new Intent(this, AddActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                NotificationHelper nfh = new NotificationHelper(this);

                nfh.showNotification(intent, "Record", "Click to go to record", "Recording!",
                        R.drawable.ic_stat_mic,
                        NotificationCompat.VISIBILITY_SECRET,
                        Constants.AUDIO_RECORDING_NOTIFICATION_ID);

                myApplication.setIsRecordIsUnderGoing(true);
                mnuItemRecord.setIcon(R.drawable.ic_action_mic_stop);
                mnuItemDeleteRecord.setVisible(false);
                mnuItemPlayRecord.setVisible(false);
                recorder.recordMedia(true,typeOfRecord);
                animateTimeLaps(textviewTimeLapse, true);

            } else {
                myApplication.setIsRecordIsUnderGoing(false);
                nfh.removeNotification(Constants.AUDIO_RECORDING_NOTIFICATION_ID);
                weHaveRecordedSomething = true;
                myApplication.setIsRecordIsUnderGoing(false);
                mnuItemRecord.setIcon(R.drawable.ic_action_mic);
                mnuItemDeleteRecord.setVisible(true);
                mnuItemPlayRecord.setVisible(true);
                recorder.recordMedia(false,typeOfRecord);
                animateTimeLaps(textviewTimeLapse, false);

            }

        }

        return true;
    }

   /* @Override
    public void onIncomingCallReceived(Context ctx, String number, Date start) {
        Log.e("Chatanuga", "onIncomingCallReceived " + number);
    }

    @Override
    public void onIncomingCallAnswered(Context ctx, String number, Date start) {
        Log.e("Chatanuga", "onIncomingCallAnswered " + number);

    }

    @Override
    public void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.e("Chatanuga", "onIncomingCallEnded " + number);

    }

    @Override
    public void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.e("Chatanuga", "onOutgoingCallStarted " + number);

    }

    @Override
    public void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.e("Chatanuga", "onOutgoingCallEnded " + number);

    }

    @Override
    public void onMissedCall(Context ctx, String number, Date start) {
        Log.e("Chatanuga", "onMissedCall " + number);

    }*/
} 