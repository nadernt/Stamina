package com.fleecast.stamina.chathead;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.NoteInfoStruct;
import com.fleecast.stamina.models.NotesAdapter;
import com.fleecast.stamina.models.RealmContactHelper;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.notetaking.ActivityAddAudioNote;
import com.fleecast.stamina.notetaking.ActivityAddTextNote;
import com.fleecast.stamina.notetaking.ActivityEditPhoneRecordNote;
import com.fleecast.stamina.notetaking.ActivityIgnoreListManager;
import com.fleecast.stamina.notetaking.ActivityPlayerPortrait;
import com.fleecast.stamina.notetaking.ActivityRecordsPlayList;
import com.fleecast.stamina.notetaking.ActivityViewTextNote;
import com.fleecast.stamina.notetaking.NoteDeleteHelper;
import com.fleecast.stamina.notetaking.PhonecallReceiver;
import com.fleecast.stamina.notetaking.PlayerService;
import com.fleecast.stamina.settings.ActivitySettings;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , SearchView.OnQueryTextListener {

    private DevicePolicyManager mManager;
    private ComponentName mComponent;
    private PowerManager.WakeLock wakeLock;
    private FloatingActionButton fab;
    private boolean blIsAlreadyAChatheadRequested=false;
    private static final String TAG = "NoteTakingList";


    private RecyclerView recyclerView;
    private RealmNoteHelper realmNoteHelper;
    private ArrayList<NoteInfoStruct> noteInfoStructs;
    private String searchString = null;
    private int searchFilter = Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION;
    private boolean sortOption;
    private int listShowNoteTypes = 0;
    private MenuItem menuSearchOption;
    private MenuItem menuShowTextNote;
    private MenuItem menuShowAudioNote;
    private MenuItem menuShowPhoneRecord;
    private MenuItem menuSearchFilter;
    private MyApplication myApplication;
    private MainActivity context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesAdapter adapter;
    private String titleOfEmail = "";
    private int reportType = 0;
    private CheckBox dontShowAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable admin
        mManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponent = new ComponentName(this, DeviceAdminSampleReceiver.class);

        firstRunAppInitials();


        if (!mManager.isAdminActive(mComponent)){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponent);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
            //startActivity(intent);
            startActivityForResult(intent, Constants.START_ACTIVITY_FOR_POWER_POLICY);
        }
        else{
            populateUI();
        }

    }

    private void firstRunAppInitials(){


      //  if(1<2){
        if(!Prefs.getBoolean(Constants.PREF_FIRST_INITIAL_OF_APP, false)) {

            Prefs.putBoolean(Constants.PREF_FIRST_INITIAL_OF_APP, true);

            Prefs.putInt(Constants.PREF_NOTELIST_SEARCH_FILTER, Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION);

            Prefs.putBoolean(Constants.RECORDER_PHONE_IS_RECORD, false);

            Prefs.putBoolean(Constants.PREF_SORT_IS_ALPHABETIC_OR_DATE, true);

            Prefs.putBoolean(Constants.PREF_NOTELIST_SEARCH_SORT_OPTION, Constants.CONST_NOTELIST_DESCENDING);

            //Prefs.putBoolean(Constants.PREF_ON_FINISH_PLAYLIST_CLOSE_PLAYER_REMOTE,false);

            Prefs.putBoolean(Constants.PREF_GROUP_ICON_SIZE, true);


            Prefs.putBoolean(Constants.PREF_SHOW_PLAYER_FULL_NOTIFICATION,false);

            Prefs.putBoolean(Constants.PREF_AUTO_RUN_RECORDER_ON_AUDIO_NOTES,false);
            Prefs.putBoolean(Constants.PREF_AUTO_RUN_PLAYER_ON_START,true);


            Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE,true);
            Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE,true);
            Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD,true);

            Prefs.getInt(Constants.PHONE_RECORDER_CHATHEAD_X,0);

            Prefs.getInt(Constants.PHONE_RECORDER_CHATHEAD_Y,100);

            Prefs.putBoolean(Constants.PREF_FIRST_QUESTION_PHONERECORDING, false);

            //Disable the phone recording service in manifest for the first initiation of app to the user. We don't want user records from first use of app!
            ComponentName component = new ComponentName(this, PhonecallReceiver.class);

            this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            /**
             * Phone recorder settings.
             */
        //Voice Call
            Prefs.putInt(Constants.RECORDER_PHONE_RECORDER_SOURCE_OPTION,4);

            /**
             * Audio recorder settings.
             */
        //Mic
            Prefs.putInt(Constants.RECORDER_AUDIO_RECORDER_SOURCE_OPTION, MediaRecorder.AudioSource.MIC);
            Prefs.putInt(Constants.RECORDER_AUDIO_RECORDER_QUALITY_OPTION,Constants.RECORDER_AUDIO_RECORDER_QUALITY_MEDIUM);

            // Put to internal storage the location of working directory

            Prefs.putString(Constants.PREF_WORKING_DIRECTORY_PATH, Environment.getExternalStorageDirectory().getPath());

            ExternalStorageManager.prepareWorkingDirectory(this);

        }




    }

    private void myFuckUp(){


     //   startActivity(new Intent(this, ActivityAddTextNote.class));
       /* Intent intent = new Intent(this, ActivitySettings.class);
        startActivity(intent);*/
        //startService(new Intent(this, ChatHeadRecordService.class));

     /*   Context context = getApplicationContext();
        ComponentName component = new ComponentName(this, PhonecallReceiver.class);



        int status = context.getPackageManager().getComponentEnabledSetting(component);
        if(status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            Log.d("AAAAAAAAAAAAAAAAAAAAa", "receiver is enabled");
        } else if(status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            Log.d("AAAAAAAAAAAAAAAAAAAAa", "receiver is disabled");
        }

//Disable
        //context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
//Enable
        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
*/
    }

private void populateUI(){

   // setContentView(R.layout.activity_main);

    //myFuckUp();

    //myApplication =  (MyApplication)getApplicationContext();


   /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
    setSupportActionBar(toolbar);*/
/*

    fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //myFuckUp();
            //myApplication.reInitEverything();
            startTheChatHead();
        }
    });
*/

    setContentView(R.layout.activity_main);
    myApplication = (MyApplication) getApplicationContext();
    myApplication.setLauncherDialogNotVisible(false);

    context = MainActivity.this;

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNoteTakingList);
    toolbar.setTitle("Notes");
    setSupportActionBar(toolbar);

    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
    params.setScrollFlags(0);

    Log.e(TAG, "ActivityTakenNotesList");
    noteInfoStructs = new ArrayList<>();
    realmNoteHelper = new RealmNoteHelper(context);

        /*recyclerView.dele*/
    recyclerView = (RecyclerView) findViewById(R.id.rvNotes);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT ) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Remove swiped item from list and notify the RecyclerView
            deleteItem(adapter.getItemAtPosition(viewHolder.getAdapterPosition()),true);
        }
    };

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

    itemTouchHelper.attachToRecyclerView(recyclerView);

    mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    //mSwipeRefreshLayout.playSoundEffect(R.raw.gestures);
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // Refresh items
            setRecyclerView();
        }
    });

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    if(Prefs.getBoolean(Constants.RECORDER_PHONE_IS_RECORD,false))
        navigationView.getMenu().findItem(R.id.nav_phone_record).setTitle("Record Calls  ✔");
    else
        navigationView.getMenu().findItem(R.id.nav_phone_record).setTitle("Record Calls   ");

}

    private String getContactNumberByName(String queryContactName) {

        if (queryContactName == null)
            return "";

        if (queryContactName.trim().isEmpty())
            return "";

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = this.getContentResolver().query(uri, projection, null, null, null);

        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        people.moveToFirst();

        if (people.getCount() == 0) {
            Log.e("DBG", "No record in contacts");
            return "";
        } else {
            boolean isHitAnyResult = false;
            String str;
            do {

                str = people.getString(indexName);
                //     Log.e("BAG" , str);

                if (str.toLowerCase().contains(queryContactName.toLowerCase())) {
                    Log.e("DBG", "Hit a contact");
                    str = people.getString(indexNumber);

                    if (realmNoteHelper.lookupPhoneNumber(str)) {
                        isHitAnyResult = true;
                        break;
                    }
                }

            } while (people.moveToNext());

            if (isHitAnyResult)
                return str;
            else
                return "";

        }
    }


    //private final String serviceName = "com.fleecast.stamina.chathead.ChatHeadRecordService";

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(service.service.getClassName().equals(getApplication().getPackageName() + ".chathead.ChatHeadService" )) {
               return true;
            }
        }

        return false;
    }

    private void startTheChatHead(){

        if(!blIsAlreadyAChatheadRequested) {

            try {

                if (isServiceRunning()) {
                    stopService(new Intent(MainActivity.this, ChatHeadService.class));
                }

            } catch (Exception e) {

            }

            Intent myIntent = new Intent(MainActivity.this, ChatHeadService.class);
/*

            myIntent.putExtra(Constants.CHATHEAD_X, fab.getX());
            myIntent.putExtra(Constants.CHATHEAD_Y, fab.getY());
*/


            startService(myIntent);
            finish();
        }


        if(!blIsAlreadyAChatheadRequested) {
            blIsAlreadyAChatheadRequested = true;

            Handler myHandler = new Handler();
            Runnable myRunnable = new Runnable() {

                @Override
                public void run() {
                    blIsAlreadyAChatheadRequested = false;
                }
            };

            myHandler.postDelayed(myRunnable, 4000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==12345) {
                if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i("Heyy", "Administration enable FAILED!");
                    setContentView(R.layout.device_admin_permission_alert);
                    TextView txtInfoText = (TextView) findViewById(R.id.txtInfoText);
                    txtInfoText.setText("In order to use the software you should active the admin permission. Please close the application and try again!");
                }
                if (resultCode == Activity.RESULT_OK) {
                    populateUI();
                }
        }
        else if (requestCode == Constants.RESULT_CODE_REQUEST_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                setRecyclerView();
            }
        }

    }

    Button.OnClickListener lst_StartService = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //startService(new Intent(MainActivity.this, ChatHeadRecordService.class));
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

             wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK ,
                    "MyWakelockTag");
            wakeLock.acquire();
        }

    };


    Button.OnClickListener lst_ShowMsg = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
if(wakeLock.isHeld()) {
    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
    dlgAlert.setMessage("This is an alert with no consequence");
    dlgAlert.setTitle("App Title");
    dlgAlert.setPositiveButton("Ok",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //dismiss the dialog
                }
            });
    dlgAlert.show();
}
            wakeLock.release();

           /* // TODO Auto-generated method stub
            java.util.Date now = new java.util.Date();
            String str = "test by Nader  " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

            Intent it = new Intent(MainActivity.this, ChatHeadRecordService.class);
            it.putExtra(Utility.EXTRA_MSG, str);
            startService(it);*/
        }

    };

    @Override
    public void onBackPressed() {

        if (!mManager.isAdminActive(mComponent)){
            finish();
            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_list_manager, menu);


        final MenuItem item = menu.findItem(R.id.action_search_notes);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);


        menuSearchFilter = menu.findItem(R.id.action_add_note_search_filter);
        menuSearchOption = menu.findItem(R.id.action_sort_options);

        menuShowTextNote = menu.findItem(R.id.action_show_text_notes);
        menuShowAudioNote = menu.findItem(R.id.action_show_audio_notes);
        menuShowPhoneRecord = menu.findItem(R.id.action_show_phone_records);

        if (Prefs.getInt(Constants.PREF_NOTELIST_SEARCH_FILTER, Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION) == Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION) {
            menuSearchFilter.setIcon(R.drawable.ic_action_news);
            searchFilter = Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION;
        } else if (Prefs.getInt(Constants.PREF_NOTELIST_SEARCH_FILTER, Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION) == Constants.CONST_SEARCH_NOTE_CONTACTS) {
            menuSearchFilter.setIcon(R.drawable.ic_action_user);
            searchFilter = Constants.CONST_SEARCH_NOTE_CONTACTS;
        }

        menuShowTextNote.setChecked(Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true));

        menuShowAudioNote.setChecked(Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true));

        menuShowPhoneRecord.setChecked(Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true));

        if (Prefs.getBoolean(Constants.PREF_NOTELIST_SEARCH_SORT_OPTION, Constants.CONST_NOTELIST_ACCEDING)) {
            menuSearchOption.setTitle("Sort older first");
        } else {
            menuSearchOption.setTitle("Sort newer first");
        }


        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        Log.e("HHHHHHHhhh", "GGGGGGGGGGGGGGG");
                        // Do something when collapsed
                        //contactsListAdapter.setFilter(mContactStruct);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        Log.e("HHHHHHHhhh", "LLLLLLLLLLLLLLLLL");
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

        setRecyclerView();
        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // setRecyclerView();
        Log.e("HHHHHHHhhh", "JJJJJJJJJJJJJjj");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e("HHHHHHHhhh", "AAAAA");
        searchString = newText.trim().toLowerCase();
        setRecyclerView();
        return false;
    }

    public void setRecyclerView() {
        try {

            if (searchString == null) {
                noteInfoStructs = realmNoteHelper.findAllNotes(null, searchFilter);
            } else if (searchString.isEmpty()) {
                noteInfoStructs = realmNoteHelper.findAllNotes(null, searchFilter);
            } else if (searchFilter == Constants.CONST_SEARCH_NOTE_CONTACTS) {
                String strContactNumberByName = getContactNumberByName(searchString);
                Log.e("BBBBBBBBBBB", strContactNumberByName);

                if (!strContactNumberByName.isEmpty()) {
                    noteInfoStructs = realmNoteHelper.findAllNotes(strContactNumberByName, Constants.CONST_SEARCH_NOTE_CONTACTS);
                } else { // Again try to search probably user has entered number
                    noteInfoStructs = realmNoteHelper.findAllNotes(searchString, Constants.CONST_SEARCH_NOTE_CONTACTS);
                }
            } else if (searchFilter == Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION) {

                noteInfoStructs = realmNoteHelper.findAllNotes(searchString, Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION);
            }

            mSwipeRefreshLayout.setRefreshing(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter = new NotesAdapter(context, noteInfoStructs, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onClick(NoteInfoStruct item) {

                if (!item.getHasAudio()) {

                    Intent intent = new Intent(context, ActivityViewTextNote.class);
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, item.getId());

                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE, item.getTitle());
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION, item.getDescription());

                    startActivity(intent);

                } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {

                    if(doWeHaveRecordsInPath(item.getId())) {

                        if (myApplication.isRecordUnderGoing() == Constants.CONST_RECORDER_SERVICE_IS_FREE) {
                            if (myApplication.isPlaying()) {
                                Intent intent = new Intent(context, PlayerService.class);
                                intent.setAction(Constants.ACTION_STOP);
                                startService(intent);
                            }
                            myApplication.setPlaylistHasLoaded(false);

                            Intent intent = new Intent(context, ActivityRecordsPlayList.class);
                            intent.putExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID, String.valueOf(item.getId()));
                            startActivity(intent);
                        }
                    }
                } else if (item.getHasAudio() && (item.getCallType() > Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL)) {

                    if (myApplication.isPlaying()) {
                        Intent intent = new Intent(context, PlayerService.class);
                        intent.setAction(Constants.ACTION_STOP);
                        startService(intent);
                    }

                    Intent intent = new Intent(context, ActivityPlayerPortrait.class);
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, item.getId());
                    String filePath = ExternalStorageManager.getWorkingDirectory() +
                            Constants.CONST_PHONE_CALLS_DIRECTORY_NAME +
                            File.separator + String.valueOf(item.getId());
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE, item.getTitle());
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION, item.getDescription());

                    Log.e("EEEEEEE", filePath);
                    intent.putExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER, filePath + Constants.RECORDER_AUDIO_FORMAT_AMR);
                    startActivity(intent);

                    /*
                    Intent i = new Intent(context, ActivityEditPhoneRecordNote.class);
                    i.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, item.getId());
                    i.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE, item.getTitle());
                    i.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION, item.getDescription());
                    startActivity(i);*/
                }

              /*  Intent i = new Intent(getApplicationContext(), ActivityEditPhoneRecordNote.class);
                i.putExtra("id", item.getId());
                i.putExtra("title", item.getTitle());
                i.putExtra("description", item.getDescription());
                startActivity(i);
                finish();*/
            }
        }, new NotesAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(final NoteInfoStruct item) {

                String[] items;
                final RealmContactHelper realmContactHelper = new RealmContactHelper(context);
                if (!item.getHasAudio()) {
                    items = new String[]{"Edit", "Delete", "Share"};
                } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {

                    items = new String[]{"Edit", "Delete", "Share"};
                } else {
                    if (!realmContactHelper.checkIfExistsInIgnoreList(item.getPhoneNumber().trim()))
                        items = new String[]{"Edit", "Delete", "Share", "Call", "Add to ignore"};
                    else
                        items = new String[]{"Edit", "Delete", "Share", "Call", "Remove ignore"};
                }

                AlertDialog myDialog;


                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle(Utility.ellipsize(item.getTitle(), 50));
                builder.setIcon(R.drawable.ic_sun);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            if (!item.getHasAudio()) {

                                final Intent intent = new Intent(context, ActivityAddTextNote.class);

                                Log.e("EEEEEEE", "A");

                                if (myApplication.getCurrentOpenedTextNoteId() > 0) {

                                    android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(context);

                                    adb.setMessage("Another not saved note is open. Do you want close it and edit this one?");

                                    adb.setTitle("Note");

                                    //adb.setIcon(android.R.drawable.ic_dialog_alert);

                                    adb.setPositiveButton("Continue?", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_NO_RECORD, item.getId());

                                            startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_LIST);
                                        }
                                    });


                                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    });
                                    adb.show();

                                } else {
                                    intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_NO_RECORD, item.getId());

                                    startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_LIST);
                                }


                            } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {

                                final Intent intent = new Intent(context, ActivityAddAudioNote.class);

                                Log.e("EEEEEEE", "B");

                                if (myApplication.isRecordUnderGoing() != Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE) {

                                    if (myApplication.getCurrentRecordingAudioNoteId() > 0) {

                                        if (myApplication.getCurrentRecordingAudioNoteId() != item.getId())
                                            return;


                                        intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD, item.getId());

                                        startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_LIST);

                                    } else {
                                        intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD, item.getId());

                                        startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_LIST);
                                    }
                                    // If we do not have any on going record.

                                } else {
                                    Toast.makeText(context, "Note: A phone recording is in progress. You can not take audio note.", Toast.LENGTH_LONG).show();
                                }


                            } else if (item.getHasAudio() && (item.getCallType() > Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL)) {
                                Log.e("EEEEEEE", "B");

                                Intent intent = new Intent(context, ActivityEditPhoneRecordNote.class);

                                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                                intent.putExtra(Constants.EXTRA_EDIT_PHONE_RECORD_NOTE, item.getId());

                                startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_LIST);
                            }

                        } else if (which == 1) {

                            deleteItem(item,false);

                        } else if (which == 2) { //Share

                            if (!item.getHasAudio()) {

                                String shareBody = item.getDescription();

                                if(item.getDescription()==null)
                                    shareBody="No description";
                                if(item.getDescription().trim().isEmpty())
                                    shareBody="No description";

                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, item.getTitle());

                                sharingIntent.putExtra(Intent.EXTRA_TITLE, item.getTitle());
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                sharingIntent.putExtra(Constants.EXTRA_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION);
                                sharingIntent.putExtra(Constants.EXTRA_APP_ID, Constants.YOUR_APP_ID);
                                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );

                                startActivityForResult(Intent.createChooser(sharingIntent, "Share Text Note"),Constants.SHARE_TO_MESSENGER_REQUEST_CODE);

                            } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {
                                Log.e("DBG","A");


                                String audioNotesFilesPath = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(item.getId())) ;

                                Log.e("DBG",audioNotesFilesPath);

                                File f=new File(audioNotesFilesPath);


                                if(f.listFiles().length==Constants.CONST_NULL_ZERO) {
                                    Toast.makeText(context, "No file to share.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(!doWeHaveEnoughSpaceForShare())
                                    return;


                                File copyfolder = new File(ExternalStorageManager.getTempWorkingDirectory(), String.valueOf(item.getId()));

                                removeDirectory(copyfolder);

                                copyfolder.mkdirs();


                                try {
                                    copyFolderForShareToTempFolder(f,copyfolder);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                File file[] = copyfolder.listFiles();
                                ArrayList<Uri> imageUris = new ArrayList<Uri>();

                                //Log.d("Files", "Size: "+ file.length);
                                for (int i=0; i < file.length; i++)
                                {
                                    imageUris.add(i, Uri.fromFile(file[i]));
                                    //imageUris.add(i, Uri.parse(file[i].getName()));

                                  /*  imageUris.add(
                                            android.provider.MediaStore.Audio.Media.getContentUriForPath(file[i].getAbsolutePath()));*/

                                    /*try {
                                        imageUris.add(Uri.parse(
                                                android.provider.MediaStore.Images.Media.insertImage(
                                                        getContentResolver(),
                                                        file[i].getAbsolutePath(), null, null)));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }*/
                                  //  Log.d("Files", "FileName:" + file[i].getName());
                                }


                                Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);

                                String txtTitl  = item.getTitle();

                                if(txtTitl==null)
                                    txtTitl = Constants.CONST_STRING_NO_TITLE;

                                String txtDescr  =  item.getDescription();

                                if(txtDescr==null) {
                                    txtDescr = Utility.unixTimeToReadable(item.getId()) +
                                            "\n" + Constants.CONST_STRING_NO_DESCRIPTION;
                                }
                                else
                                {
                                    txtDescr = Utility.unixTimeToReadable(item.getId()) +
                                            "\n" + txtDescr ;
                                }


                                share.putExtra(Intent.EXTRA_SUBJECT,txtTitl);
                                share.putExtra(Intent.EXTRA_TITLE, txtTitl);
                                share.putExtra(Intent.EXTRA_TEXT, txtDescr);
                                share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);

                                share.setType("audio/*");
                                share.putExtra(Constants.EXTRA_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION);
                                share.putExtra(Constants.EXTRA_APP_ID, Constants.YOUR_APP_ID);
                                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );

                                startActivityForResult(Intent.createChooser(share, "Share Audio File"),Constants.SHARE_TO_MESSENGER_REQUEST_CODE);


                            } else if (item.getHasAudio() && (item.getCallType() > Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL)) {
                                Log.e("DBG","‌B");

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                LinearLayout layout = new LinearLayout(context);
                                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.setLayoutParams(parms);

                                layout.setGravity(Gravity.CLIP_VERTICAL);
                                layout.setPadding(2, 2, 2, 2);

                                TextView tv = new TextView(context);
                                tv.setText("You are sharing " + Utility.ellipsize(item.getTitle(),50)  + " phone call!");
                                tv.setPadding(40, 40, 40, 40);
                                tv.setGravity(Gravity.LEFT);
                                tv.setTextSize(20);

                                final EditText et = new EditText(context);
                                String etStr = et.getText().toString();
                                TextView tv1 = new TextView(context);
                                tv1.setPadding(20, 10, 20, 10);
                                tv1.setText(Html.fromHtml("Type <font color='RED'>ASD</font> (case insensitive)"));

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

                                            Toast.makeText(context,"Wrong text!",Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        if(!doWeHaveEnoughSpaceForShare())
                                            return;

                                        String phoneCallFilePath = ExternalStorageManager.getWorkingDirectory() +
                                                Constants.CONST_PHONE_CALLS_DIRECTORY_NAME +
                                                File.separator + String.valueOf(item.getId()) +Constants.RECORDER_AUDIO_FORMAT_AMR;

                                        Log.e("DBG",phoneCallFilePath);

                                        File f=new File(phoneCallFilePath);

                                        //Uri uri = Uri.parse("file://"+f.getAbsolutePath());

                                        String tempFile = String.valueOf((int) (System.currentTimeMillis() / 1000));

                                        File tmp = new File(ExternalStorageManager.getTempWorkingDirectory() + File.separator + tempFile + Constants.RECORDER_AUDIO_FORMAT_AMR);
                                        try {
                                            copy(f,tmp);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        Uri uri = Uri.fromFile(tmp);

                                        Intent share = new Intent(Intent.ACTION_SEND);

                                        String txtTitl  = item.getTitle();

                                        if(txtTitl==null)
                                            txtTitl = Constants.CONST_STRING_NO_TITLE;

                                        String txtDescr  =  item.getDescription();

                                        if(txtDescr==null) {
                                            txtDescr = Utility.unixTimeToReadable(item.getId()) +
                                                    "\n" + Constants.CONST_STRING_NO_DESCRIPTION;
                                        }
                                        else
                                        {
                                            txtDescr = Utility.unixTimeToReadable(item.getId()) +
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

                        } else if (which == 3) { // Call
                            try {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                                        == PackageManager.PERMISSION_GRANTED) {

                                    String uri = "tel:" + item.getPhoneNumber().trim();
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse(uri));
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                            }
                        } else if (which == 4) { //Add to ignore

                            String strContactName =  Utility.getContactName(context,item.getPhoneNumber().trim());

                            if (!realmContactHelper.checkIfExistsInIgnoreList(item.getPhoneNumber().trim())) {
                                realmContactHelper.addIgnoreList(
                                        item.getPhoneNumber().trim(),
                                        strContactName);

                                Toast.makeText(context, "Number added to ignore list.", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                realmContactHelper.deleteContactFromIgnoreList(
                                        item.getPhoneNumber().trim()
                                );
                                Toast.makeText(context, "Number removed from ignore list.", Toast.LENGTH_LONG).show();

                            }

                        }

                    }
                });


                builder.setCancelable(true);
                myDialog = builder.create();
                myDialog.show();

            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void deleteItem(final NoteInfoStruct item,final boolean isItFromSwipeFunction){
        if (myApplication.getCurrentOpenedTextNoteId() == item.getId() || myApplication.getCurrentRecordingAudioNoteId() == item.getId()) {
            Utility.showMessage("This note is already open. Close it and try again.","Note",context);
            return;
        }

        if(myApplication.stackPlaylist.size()>0) {

            if (myApplication.stackPlaylist.get(0).getParentDbId() == item.getId() && myApplication.isPlaying()) {
                // Check if player is in pause mode stop it
                if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PAUSED ||
                        myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {

                    Intent tmpIntent = new Intent(context, PlayerService.class);
                    tmpIntent.setAction(Constants.ACTION_STOP);
                    startService(tmpIntent);

                }

/*
                                Utility.showMessage("This is playing. Stop it and try again.", "Note", context);
                                return;
*/
            }

        }
        android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(context);

        adb.setMessage("Are you sure want to delete " + Html.fromHtml("<strong>" + Utility.ellipsize(item.getTitle(), 50) + "</strong>") + "?");

        adb.setTitle("Note");
        final NoteDeleteHelper noteDeleteHelper = new NoteDeleteHelper(context);

        //adb.setIcon(android.R.drawable.ic_dialog_alert);

        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!item.getHasAudio()) {

                    noteDeleteHelper.deleteTextNote(item.getId());
                    Toast.makeText(context, "Item deleted!", Toast.LENGTH_SHORT).show();

                } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {

                    noteDeleteHelper.deleteAudioNoteAndItsFiles(item.getId());
                    Toast.makeText(context, "Item deleted. Contents moved to trash folder.", Toast.LENGTH_SHORT).show();

                } else if (item.getHasAudio() && (item.getCallType() > Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL)) {

                    noteDeleteHelper.deletePhoneNoteAndItsFiles(item.getId());
                    Toast.makeText(context, "Item deleted. Contents moved to trash folder.", Toast.LENGTH_SHORT).show();

                }

                //setRecyclerView();
                for(int j=0; i< noteInfoStructs.size();j++){
                    if(noteInfoStructs.get(j).getId() == item.getId()) {
                        noteInfoStructs.remove(j);
                        adapter.removeItem(j);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }



            }
        });


        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(isItFromSwipeFunction)
                    adapter.notifyDataSetChanged();
                return;
            }
        });
        adb.show();

    }

    private boolean doWeHaveRecordsInPath(int dbId){

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

    public static void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile);
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }

    // If targetLocation does not exist, it will be created.
    private void copyFolderForShareToTempFolder(File sourceLocation , File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
           // int timeStampForShare = (int) (System.currentTimeMillis() / 1000);

            for (int i=0; i<children.length; i++) {
                copyFolderForShareToTempFolder(new File(sourceLocation, children[i]),
                        new File(targetLocation, Utility.getFilePostFixId(children[i]) +
                                Constants.RECORDER_AUDIO_FORMAT_AAC));
               // timeStampForShare++;
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private boolean doWeHaveEnoughSpaceForShare(){

        boolean hasEnoughSpace = ExternalStorageManager.isThereEnoughSpaceOnStorage();

        if (hasEnoughSpace) {
            return true;
        } else {

            android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(context);


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

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

       @Override
    public boolean onOptionsItemSelected(MenuItem item) {
           int id = item.getItemId();

           //noinspection SimplifiableIfStatement
           if (id == R.id.action_sort_options) {

                if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SEARCH_SORT_OPTION, Constants.CONST_NOTELIST_ACCEDING)) {
                Prefs.putBoolean(Constants.PREF_NOTELIST_SEARCH_SORT_OPTION, Constants.CONST_NOTELIST_DESCENDING);
                menuSearchOption.setTitle("Sort older first");
                } else {
                Prefs.putBoolean(Constants.PREF_NOTELIST_SEARCH_SORT_OPTION, Constants.CONST_NOTELIST_ACCEDING);
                    menuSearchOption.setTitle("Sort newer first");
            }
               setRecyclerView();
               return true;
           } else if (id == R.id.action_show_text_notes) {
               boolean bl = !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true);
               Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, bl);
               menuShowTextNote.setChecked(bl);
               setRecyclerView();
               return true;
           } else if (id == R.id.action_show_audio_notes) {
               boolean bl = !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true);
               Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, bl);
               menuShowAudioNote.setChecked(bl);
               setRecyclerView();
               return true;
           } else if (id == R.id.action_show_phone_records) {
               boolean bl = !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true);
               Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, bl);
               menuShowPhoneRecord.setChecked(bl);
               setRecyclerView();
               return true;
           } else if (id == R.id.action_add_note_search_filter) {
               AlertDialog myDialog;

               String[] items = {"Title & Descriptions", "Contacts"};

               if (searchFilter == Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION) {
                   items[0] = "✔ " + items[0];
                   items[1] = "     " + items[1];
               } else {
                   items[1] = "✔ " + items[1];
                   items[0] = "     " + items[0];
               }

               AlertDialog.Builder builder = new AlertDialog.Builder(context);

               builder.setTitle("Search Filters:");
               //builder.setIcon(R.drawable.audio_wave);

               builder.setItems(items, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if (which == 0) {
                           Prefs.putInt(Constants.PREF_NOTELIST_SEARCH_FILTER, Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION);
                           searchFilter = Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION;
                           menuSearchFilter.setIcon(R.drawable.ic_action_news);
                       } else if (which == 1) {
                           Prefs.putInt(Constants.PREF_NOTELIST_SEARCH_FILTER, Constants.CONST_SEARCH_NOTE_CONTACTS);
                           searchFilter = Constants.CONST_SEARCH_NOTE_CONTACTS;
                           menuSearchFilter.setIcon(R.drawable.ic_action_user);
                       }

                   }
               });


               builder.setCancelable(true);
               myDialog = builder.create();
               myDialog.show();
               return true;
           }

           return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
 /*       nav_take_text_note
                nav_take_audio_note
        nav_phone_record
                nav_phone_ignorelist
        nav_chathead
                nav_backup_report
        nav_emptytrash
                nav_settings
        nav_report_bug
                nav_rate
        nav_help
                nav_about*/

        if (id == R.id.nav_chathead) {
            startTheChatHead();
        } else if (id == R.id.nav_take_text_note) {

            Intent intent = new Intent(this, ActivityAddTextNote.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (myApplication.getCurrentOpenedTextNoteId() > 0)
                intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_NO_RECORD, myApplication.getCurrentOpenedTextNoteId());
            else
                intent.putExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_NO_RECORD, true);

            //   updateChatHeadSize(1);
            startActivity(intent);

        } else if (id == R.id.nav_take_audio_note) {
            // If we do not have any on going record.
            if (myApplication.isRecordUnderGoing() != Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE) {

                Intent intent = new Intent(this, ActivityAddAudioNote.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (myApplication.getCurrentRecordingAudioNoteId() > 0)
                    intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD, myApplication.getCurrentRecordingAudioNoteId());
                else
                    intent.putExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_START_RECORD, true);

                startActivity(intent);
            } else {
                Toast.makeText(this, "Note: A phone recording is in progress. You can not take audio note.", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_phone_record) {

            if(!Prefs.getBoolean(Constants.PREF_FIRST_QUESTION_PHONERECORDING,false)) {
            String msg = "In some countries, even if both parties know about the recording the call " +
                    "conversation, that would still be violating the law and privacy. Record the phone " +
                    "calls on your own responsibility. Please for more details about the call recording " +
                    "refer to your local or national regulations and law.";
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            LayoutInflater adbInflater = LayoutInflater.from(this);
            View eulaLayout = adbInflater.inflate(R.layout.alertdialog_phone_recording, null);

            dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.chkDontAskPhonRecordingMessage);
            adb.setView(eulaLayout);
            adb.setTitle("Important Notice");
            adb.setMessage(msg);

            adb.setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    if (dontShowAgain.isChecked()) {
                        Prefs.putBoolean(Constants.PREF_FIRST_QUESTION_PHONERECORDING, true);
                    } else {

                        Prefs.putBoolean(Constants.PREF_FIRST_QUESTION_PHONERECORDING, false);
                    }
                    phoneCallEnableDisable(item);
                    return;
                }
            });

            adb.setNegativeButton("I Don't Agree", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    return;
                }
            });
            adb.show();
        }
        else {

                phoneCallEnableDisable(item);

            }



        } else if (id == R.id.nav_backup_report) {


        }
     else if (id == R.id.nav_emptytrash) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            LinearLayout layout = new LinearLayout(context);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(parms);

            layout.setGravity(Gravity.CLIP_VERTICAL);
            layout.setPadding(2, 2, 2, 2);

            TextView tv = new TextView(context);
            tv.setText("You are deleting all of trash file this operation doeas not revert back!");
            tv.setPadding(40, 40, 40, 40);
            tv.setGravity(Gravity.LEFT);
            tv.setTextSize(20);

            final EditText et = new EditText(context);
            String etStr = et.getText().toString();
            
            TextView tv1 = new TextView(context);
            tv1.setPadding(20, 10, 20, 10);

            tv1.setText(Html.fromHtml("Type <font color='RED'>ASD</font> (case insensitive)"));

            LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv1Params.bottomMargin = 5;
            layout.addView(tv1, tv1Params);
            layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            alertDialogBuilder.setView(layout);
            alertDialogBuilder.setTitle("Note");
            alertDialogBuilder.setCustomTitle(tv);

            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(!et.getText().toString().trim().toLowerCase().contains("asd")){

                        Toast.makeText(context,"Wrong text!",Toast.LENGTH_SHORT).show();
                        return;
                    }


                    File trashDir = new File(ExternalStorageManager.getTrashDirectory());

                    removeDirectory(trashDir);
                    File tempDir = new File(ExternalStorageManager.getTempWorkingDirectory());

                    removeDirectory(tempDir);

                    Toast.makeText(context,"Trash is empty",Toast.LENGTH_SHORT).show();


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



        } else if (id == R.id.nav_report_bug) {

            android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(context);

            adb.setMessage("Your reports and suggestions help me to make this application better for you. Please explain simple and clear the problem or the feature you like.\n\nThank you!");

            adb.setTitle("Report and Suggestion");


            adb.setIcon(R.drawable.ic_sun);

            adb.setPositiveButton("Bug Report?", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Uri uri = Uri.parse("mailto:fleecast@gmail.com");
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.putExtra(Intent.EXTRA_SUBJECT,
                            "Bug Report");
                    intent.putExtra(Intent.EXTRA_TEXT   , "Section of app you got error or problem (e.g Recorder): " + "\n\n" +
                            "Error or problem detail: ");
                    startActivity(intent);

                }
            });

            adb.setNeutralButton("Suggestion?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Uri uri = Uri.parse("mailto:fleecast@gmail.com");
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.putExtra(Intent.EXTRA_SUBJECT,
                            "Suggestion");
                    intent.putExtra(Intent.EXTRA_TEXT,"Section of app: " + "\n\n" +
                            "Or whatever is your idea: ");
                    startActivity(intent);
                }
            });

            adb.show();


        } else if (id == R.id.nav_rate) {


        } else if (id == R.id.nav_help) {
            String url = "http://www.fleecast.com/stamina";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.nav_about) {

        Intent intent = new Intent(context,ActivityAbout.class);
        startActivity(intent);

        }else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);

        } else if (id == R.id.nav_phone_ignorelist) {
            Intent intent = new Intent(this, ActivityIgnoreListManager.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

private void phoneCallEnableDisable (MenuItem item) {
    ComponentName component = new ComponentName(this, PhonecallReceiver.class);

    //Disable call recording BroadcastReceiver (class PhonecallReceiver)
    if (Prefs.getBoolean(Constants.RECORDER_PHONE_IS_RECORD, false)) {
        //Disable
        this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        Prefs.putBoolean(Constants.RECORDER_PHONE_IS_RECORD, false);
        item.setTitle("Record Calls   ");
        Toast.makeText(this, "Phone recording disabled", Toast.LENGTH_LONG).show();
    } else { //Enable call recording BroadcastReceiver (class PhonecallReceiver)
        //Enable
        this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Prefs.putBoolean(Constants.RECORDER_PHONE_IS_RECORD, true);
        item.setTitle("Record Calls  ✔");
        Toast.makeText(this, "Phone recording enabled", Toast.LENGTH_LONG).show();
    }
}
}
