package com.fleecast.stamina.chathead;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.TelephonyManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.backup.ActivityBackupHome;
import com.fleecast.stamina.customgui.GroupsListDialog;
import com.fleecast.stamina.models.FilterColorsStruct;
import com.fleecast.stamina.colorpicker.ColorPickerDialog;
import com.fleecast.stamina.colorpicker.ColorPickerSwatch;
import com.fleecast.stamina.legacyplayer.ActivityLegacyPlayer;
import com.fleecast.stamina.legacyplayer.ActivityLegacyPlayerPhone;
import com.fleecast.stamina.legacyplayer.PlayerServiceLegacy;
import com.fleecast.stamina.models.NoteInfoRealmStruct;
import com.fleecast.stamina.models.NoteInfoStruct;
import com.fleecast.stamina.models.NotesAdapter;
import com.fleecast.stamina.models.NotesGroupsDictionary;
import com.fleecast.stamina.models.RealmContactHelper;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.notetaking.ActivityAddAudioNote;
import com.fleecast.stamina.notetaking.ActivityAddTextNote;
import com.fleecast.stamina.notetaking.ActivityEditPhoneRecordNote;
import com.fleecast.stamina.notetaking.ActivityIgnoreListManager;
import com.fleecast.stamina.notetaking.ActivityPlayerPhone;
import com.fleecast.stamina.notetaking.ActivityRecordsPlayList;
import com.fleecast.stamina.notetaking.ActivityViewNotes;
import com.fleecast.stamina.notetaking.NoteDeleteHelper;
import com.fleecast.stamina.notetaking.PhonecallReceiver;
import com.fleecast.stamina.notetaking.PlayerService;
import com.fleecast.stamina.settings.ActivitySettings;
import com.fleecast.stamina.settings.AppRating;
import com.fleecast.stamina.todo.ActivityTodoParentRecyclerView;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private DevicePolicyManager mManager;
    private ComponentName mComponent;
    private PowerManager.WakeLock wakeLock;
    //private FloatingActionButton fab;
    private boolean blIsAlreadyAChatheadRequested = false;
    private static final String TAG = "NoteTakingList";


    private RecyclerView recyclerView;
    private RealmNoteHelper realmNoteHelper;
    private ArrayList<NoteInfoStruct> noteInfoStructs;
    private NotesGroupsDictionary notesGroupsDictionary;
    private String searchString = null;
    private int searchFilter = Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION;
    //private boolean sortOption;
    //private int listShowNoteTypes = 0;
    private MenuItem menuSearchOption;
    private MenuItem menuShowTextNote;
    private MenuItem menuShowAudioNote;
    private MenuItem menuShowPhoneRecord;
    private MenuItem menuSearchFilter;
    private MyApplication myApplication;
    private MainActivity mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesAdapter adapter;
    //private String titleOfEmail = "";
    //private int reportType = 0;
    private CheckBox dontShowAgain;
    private final int OVERLAY_PERMISSION_REQ_CODE = 1324;
    private boolean isDeviceSmartPhone;
    private ListView mDrawerFiltersList;
    private FilterColorsStruct filterColorsStruct;
    private int currentColorFilter = Constants.CONST_NULL_ZERO;
    private String currentGroupFilter = null;
    private DrawerLayout mDrawerLayout;
    private ImageButton buttons[] =new ImageButton[10];
    private ImageButton imgBtnCleanGroupFilter;
    private Switch swFilterAutoClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable admin
        mManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponent = new ComponentName(this, DeviceAdminSampleReceiver.class);

        firstRunAppInitials();


        if (!mManager.isAdminActive(mComponent)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponent);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
            //startActivity(intent);
            startActivityForResult(intent, Constants.START_ACTIVITY_FOR_POWER_POLICY);
        } else {
            populateUI();
        }

    }

    private void firstRunAppInitials() {

        //  if(1<2){
        if (!Prefs.getBoolean(Constants.PREF_FIRST_INITIAL_OF_APP, false)) {

            Prefs.putBoolean(Constants.PREF_FIRST_INITIAL_OF_APP, true);

            Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false);

            Prefs.putInt(Constants.PREF_NOTELIST_SEARCH_FILTER, Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION);

            Prefs.putBoolean(Constants.RECORDER_PHONE_IS_RECORD, false);

            Prefs.putBoolean(Constants.PREF_SORT_IS_ALPHABETIC_OR_DATE, true);

            Prefs.putBoolean(Constants.PREF_NOTELIST_SEARCH_SORT_OPTION, Constants.CONST_NOTELIST_DESCENDING);

            //Prefs.putBoolean(Constants.PREF_ON_FINISH_PLAYLIST_CLOSE_PLAYER_REMOTE,false);

            Prefs.putBoolean(Constants.PREF_GROUP_ICON_SIZE, true);

            Prefs.putBoolean(Constants.PREF_AUTO_CLOSE_RIGHT_DRAWER, true);

            Prefs.putBoolean(Constants.PREF_SHOW_PLAYER_FULL_NOTIFICATION, false);

            Prefs.putBoolean(Constants.PREF_AUTO_RUN_RECORDER_ON_AUDIO_NOTES, false);
            Prefs.putBoolean(Constants.PREF_AUTO_RUN_PLAYER_ON_START, true);


            Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true);
            Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true);
            Prefs.putBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true);

            Prefs.getInt(Constants.PHONE_RECORDER_CHATHEAD_X, 0);

            Prefs.getInt(Constants.PHONE_RECORDER_CHATHEAD_Y, 100);

            Prefs.putBoolean(Constants.PREF_FIRST_QUESTION_PHONERECORDING, false);

            //Disable the phone recording service in manifest for the first initiation of app to the user. We don't want user records from first use of app!
            ComponentName component = new ComponentName(this, PhonecallReceiver.class);

            this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            /**
             * Phone recorder settings.
             */
            //Voice Call
            Prefs.putInt(Constants.RECORDER_PHONE_RECORDER_SOURCE_OPTION, MediaRecorder.AudioSource.VOICE_CALL);

            /**
             * Audio recorder settings.
             */
            //Mic
            Prefs.putInt(Constants.RECORDER_AUDIO_RECORDER_SOURCE_OPTION, MediaRecorder.AudioSource.MIC);
            Prefs.putInt(Constants.RECORDER_AUDIO_RECORDER_QUALITY_OPTION, Constants.RECORDER_AUDIO_RECORDER_QUALITY_MEDIUM);

            // Put to internal storage the location of working directory

            Prefs.putString(Constants.PREF_WORKING_DIRECTORY_PATH, Environment.getExternalStorageDirectory().getPath());

            ExternalStorageManager.ifWorkingDirIsNotExitMakeIt(this);

            Prefs.putBoolean(Constants.PREF_IS_DEVICE_SMARTPHONE, isCallingSupported(MainActivity.this));

                Date updateTime = new Date();

                Date createdTime = null;

                int dbId = (int) (System.currentTimeMillis() / 1000);

                String title = "Welcome to Stamina!";
                String description= "";
                InputStream is = null;

                try {
                    is = getAssets().open("user_guide/first_text_note.txt");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    description = new String(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                realmNoteHelper = new RealmNoteHelper(MainActivity.this);

                realmNoteHelper.addNote(dbId, title, description, false, updateTime, createdTime, null, null, Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL, null, Constants.CONST_NOTETYPE_TEXT);
            }

        ExternalStorageManager.ifWorkingDirIsNotExitMakeIt(this);

    }

private void testFucntions(){




}

    private static boolean isCallingSupported(Context context) {

        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            return false;
        }else{
            return true;
        }

    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setPermisions(){
// The request code used in ActivityCompat.requestPermissions()
// and returned in the Activity's onRequestPermissionsResult()
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.CALL_PHONE,Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }
   private void populateUI() {

       System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        setContentView(R.layout.activity_main);

        myApplication = (MyApplication) getApplicationContext();

        myApplication.setLauncherDialogNotVisible(false);

        mContext = MainActivity.this;
       testFucntions();
       isDeviceSmartPhone = Prefs.getBoolean(Constants.PREF_IS_DEVICE_SMARTPHONE,false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNoteTakingList);
        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        noteInfoStructs = new ArrayList<>();
        realmNoteHelper = new RealmNoteHelper(mContext);
        notesGroupsDictionary = new NotesGroupsDictionary(mContext);

       imgBtnCleanGroupFilter = (ImageButton ) findViewById(R.id.imgBtnCleanGroupFilter);

       imgBtnCleanGroupFilter.setVisibility(View.INVISIBLE);

       imgBtnCleanGroupFilter.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               cleanFilters(true);
               setRecyclerView();
           }
       });

       swFilterAutoClose = (Switch) findViewById(R.id.swFilterAutoClose);

       swFilterAutoClose.setChecked(Prefs.getBoolean(Constants.PREF_AUTO_CLOSE_RIGHT_DRAWER, true));

       swFilterAutoClose.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(swFilterAutoClose.isChecked())
               {
                   Prefs.putBoolean(Constants.PREF_AUTO_CLOSE_RIGHT_DRAWER, true);
               }
               else
               {
                   Prefs.putBoolean(Constants.PREF_AUTO_CLOSE_RIGHT_DRAWER, false);
               }
           }
       });

       setPermisions();

        /*recyclerView.dele*/
        recyclerView = (RecyclerView) findViewById(R.id.rvNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT)  {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                if(swipeDir == ItemTouchHelper.LEFT) {
                    NoteInfoStruct noteInfoStruct = adapter.getItemAtPosition(viewHolder.getAdapterPosition());
                    Intent intent = new Intent(mContext, ActivityViewNotes.class);
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, noteInfoStruct.getId());

                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE, noteInfoStruct.getTitle());
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION, noteInfoStruct.getDescription());

                    startActivity(intent);
                    adapter.notifyDataSetChanged();

                }else {
                    //Remove swiped item from list and notify the RecyclerView
                    deleteItem(adapter.getItemAtPosition(viewHolder.getAdapterPosition()), true);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(recyclerView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
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
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationViewLeft = (NavigationView) findViewById(R.id.nav_view_left);
        navigationViewLeft.setNavigationItemSelectedListener(this);


       if(!isDeviceSmartPhone) {
           navigationViewLeft.getMenu().findItem(R.id.nav_phone_record).setVisible(false);
           navigationViewLeft.getMenu().findItem(R.id.nav_phone_ignorelist).setVisible(false);
       }else {
           if (Prefs.getBoolean(Constants.RECORDER_PHONE_IS_RECORD, false))
               navigationViewLeft.getMenu().findItem(R.id.nav_phone_record).setTitle("Record Calls  ✔");
           else
               navigationViewLeft.getMenu().findItem(R.id.nav_phone_record).setTitle("Record Calls   ");
       }

       NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
       navigationViewRight.setNavigationItemSelectedListener(this);



       GridLayout gridColorFiltersContainer = (GridLayout) findViewById(R.id.gridColorFiltersContainer);
       LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)gridColorFiltersContainer.getLayoutParams();
       layoutParams.setMargins(0, Utility.getStatusBarHeight(mContext), 0, 0);
       gridColorFiltersContainer.setLayoutParams(layoutParams);

        filterColorsStruct = new FilterColorsStruct(new int[] {
               ResourcesCompat.getColor(getResources(), R.color.rich_black, null),
               ResourcesCompat.getColor(getResources(), R.color.american_rose, null),
               ResourcesCompat.getColor(getResources(), R.color.azure, null),
               ResourcesCompat.getColor(getResources(), R.color.persian_pink, null),
               ResourcesCompat.getColor(getResources(), R.color.green_apple, null),
               ResourcesCompat.getColor(getResources(), R.color.orange, null),
               ResourcesCompat.getColor(getResources(), R.color.viola_purple, null),
               ResourcesCompat.getColor(getResources(), R.color.slate_gray, null),
               ResourcesCompat.getColor(getResources(), R.color.chocolate, null),
               Color.WHITE});

       populateRightDrawerFilters(notesGroupsDictionary.getTagsList());
       populateColorFilters();
       AppRating.app_launched(this);

       //AppRating.showRateDialog(this, null);

   }

    private void cleanFilters(boolean needColorFilterToDefault){

        imgBtnCleanGroupFilter.setVisibility(View.INVISIBLE);

        currentGroupFilter = null;
        currentColorFilter = Constants.CONST_NULL_ZERO;

        //mDrawerFiltersList.setItemChecked(position, true);

        if(mDrawerFiltersList!=null)
        for (int i = 0; i < mDrawerFiltersList.getChildCount(); i++) {
            mDrawerFiltersList.getChildAt(i).setBackgroundColor(mDrawerFiltersList.getSolidColor());
        }


        for(int i=0; i < buttons.length;i++)
            buttons[i].setImageResource(android.R.color.transparent);

        if(needColorFilterToDefault)
        buttons[9].setImageDrawable( ContextCompat.getDrawable(mContext, R.drawable.ic_colorpicker_swatch_selected));

        if(swFilterAutoClose.isChecked())
            mDrawerLayout.closeDrawers();

    }


    private void populateColorFilters(){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        buttons[0] = (ImageButton)findViewById(R.id.btnFilterColor0);
        buttons[1] = (ImageButton)findViewById(R.id.btnFilterColor1);
        buttons[2] = (ImageButton)findViewById(R.id.btnFilterColor2);
        buttons[3] = (ImageButton)findViewById(R.id.btnFilterColor3);
        buttons[4] = (ImageButton)findViewById(R.id.btnFilterColor4);
        buttons[5] = (ImageButton)findViewById(R.id.btnFilterColor5);
        buttons[6] = (ImageButton)findViewById(R.id.btnFilterColor6);
        buttons[7] = (ImageButton)findViewById(R.id.btnFilterColor7);
        buttons[8] = (ImageButton)findViewById(R.id.btnFilterColor8);
        buttons[9] = (ImageButton)findViewById(R.id.btnFilterColor9);

        setFilterButtonColor(buttons[0],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor0());
        setFilterButtonColor(buttons[1],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor1());
        setFilterButtonColor(buttons[2],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor2());
        setFilterButtonColor(buttons[3],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor3());
        setFilterButtonColor(buttons[4],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor4());
        setFilterButtonColor(buttons[5],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor5());
        setFilterButtonColor(buttons[6],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor6());
        setFilterButtonColor(buttons[7],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor7());
        setFilterButtonColor(buttons[8],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor8());
        setFilterButtonColor(buttons[9],new ShapeDrawable( new OvalShape() ), filterColorsStruct.getColor9());

        for (int i = 0; i < buttons.length; i++)
        {
            buttons[i].setOnClickListener(null);

            final ImageButton theButton = buttons[i];

            theButton.setOnClickListener(new View.OnClickListener()
            {

                public void onClick(View v) {

                    Drawable res =  ContextCompat.getDrawable(mContext, R.drawable.ic_colorpicker_swatch_selected);

                    cleanFilters(false);

                    switch (v.getId()){
                        case R.id.btnFilterColor0:
                            buttons[0].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor0();
                            break;
                        case R.id.btnFilterColor1:
                            buttons[1].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor1();
                            break;
                        case R.id.btnFilterColor2:
                            buttons[2].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor2();
                            break;
                        case R.id.btnFilterColor3:
                            buttons[3].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor3();
                            break;
                        case R.id.btnFilterColor4:
                            buttons[4].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor4();
                            break;
                        case R.id.btnFilterColor5:
                            buttons[5].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor5();
                            break;
                        case R.id.btnFilterColor6:
                            buttons[6].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor6();
                            break;
                        case R.id.btnFilterColor7:
                            buttons[7].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor7();
                            break;
                        case R.id.btnFilterColor8:
                            buttons[8].setImageDrawable(res);
                            currentColorFilter = filterColorsStruct.getColor8();
                            break;
                        case R.id.btnFilterColor9:
                            buttons[9].setImageDrawable(res);
                            currentColorFilter = Constants.CONST_NULL_ZERO;
                            break;
                    }
                    if(swFilterAutoClose.isChecked())
                    mDrawerLayout.closeDrawers();
                    currentGroupFilter=null;
                    setRecyclerView();
                }
            });
        }

    }


    private void populateRightDrawerFilters( String [] dictionary){


        if(dictionary!=null) {

            ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dictionary);

            mDrawerFiltersList = (ListView) findViewById(R.id.navList);

            mDrawerFiltersList.setAdapter(mAdapter);

            mDrawerFiltersList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                   final String oldGroupName = adapterView.getItemAtPosition(i).toString();

                    String [] dictionary  = notesGroupsDictionary.getTagsList();

                    if(dictionary.length==Constants.CONST_NULL_ZERO)
                        return true;

                    List<String> list = new ArrayList<String>(Arrays.asList(dictionary));

                    if(dictionary.length>1) {

                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).contentEquals(oldGroupName)) {
                                list.remove(j);
                                break;
                            }
                        }

                    }

                    String[] grpDic = new String[list.size()];
                    grpDic = list.toArray(grpDic);

                    GroupsListDialog groupsListDialog;

                    if(dictionary.length>1) {
                        groupsListDialog = new GroupsListDialog(mContext, "Groups Management", true, "<font color='red'> Rename or delete group:</font> " + oldGroupName, grpDic, oldGroupName, true);
                    }
                    else
                    {
                        groupsListDialog = new GroupsListDialog(mContext, "Groups Management", true, "<font color='red'> Rename or delete group:</font> " + oldGroupName, null, oldGroupName, true);
                    }

                    groupsListDialog.setResultsListener(new GroupsListDialog.ResultsListener() {
                        @Override
                        public void selectedGroup(String selectedGroupTitle) {
                            realmNoteHelper.updateAGroupTag(oldGroupName,selectedGroupTitle,false);
                            notesGroupsDictionary = new NotesGroupsDictionary(mContext);
                            populateRightDrawerFilters(notesGroupsDictionary.getTagsList());
                            cleanFilters(true);
                            setRecyclerView();

                        }

                        @Override
                        public void newGroupAdded(String newGroupTitle) {
                            realmNoteHelper.updateAGroupTag(oldGroupName,newGroupTitle,false);
                            notesGroupsDictionary = new NotesGroupsDictionary(mContext);
                            populateRightDrawerFilters(notesGroupsDictionary.getTagsList());
                            cleanFilters(true);
                            setRecyclerView();
                        }

                        @Override
                        public void removeGroupFromItem() {
                            realmNoteHelper.updateAGroupTag(oldGroupName,null,true);
                            notesGroupsDictionary = new NotesGroupsDictionary(mContext);
                            populateRightDrawerFilters(notesGroupsDictionary.getTagsList());
                            cleanFilters(true);
                            setRecyclerView();
                        }

                    });

                    return false;
                }
            });

            mDrawerFiltersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    currentGroupFilter = parent.getItemAtPosition(position).toString().toLowerCase();
                    currentColorFilter = Constants.CONST_NULL_ZERO;

                    //mDrawerFiltersList.setItemChecked(position, true);

                    for (int i = 0; i < mDrawerFiltersList.getChildCount(); i++) {
                        if (position == i)
                            mDrawerFiltersList.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContext, R.color.amber));
                       else
                            mDrawerFiltersList.getChildAt(i).setBackgroundColor(mDrawerFiltersList.getSolidColor());

                    }


                    for(int i=0; i < buttons.length;i++)
                        buttons[i].setImageResource(android.R.color.transparent);

                    buttons[9].setImageDrawable( ContextCompat.getDrawable(mContext, R.drawable.ic_colorpicker_swatch_selected));

                    imgBtnCleanGroupFilter.setVisibility(View.VISIBLE);

                    if(swFilterAutoClose.isChecked())
                        mDrawerLayout.closeDrawers();

                    setRecyclerView();
                }
            });
        }
        else
        {
            if(mDrawerFiltersList!=null)
            mDrawerFiltersList.setAdapter(null);
        }


    }
    private void setFilterButtonColor(ImageButton imageButton,ShapeDrawable circle,int color){
        circle.getPaint().setColor(color);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            imageButton.setBackgroundDrawable(circle);
        } else {

            imageButton.setBackground(circle);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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


    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(getApplication().getPackageName() + ".chathead.ChatHeadService")) {
                return true;
            }
        }

        return false;
    }

    private void startTheChatHead() {

        if (!blIsAlreadyAChatheadRequested) {

            try {

                if (isServiceRunning()) {
                    stopService(new Intent(MainActivity.this, ChatHeadService.class));
                }

            } catch (Exception e) {

            }

            Intent myIntent = new Intent(MainActivity.this, ChatHeadService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                }
                else
                {
                    startService(myIntent);
                    finish();
                }
            }
            else
            {
                startService(myIntent);
                finish();
            }



/*
            myIntent.putExtra(Constants.CHATHEAD_X, fab.getX());
            myIntent.putExtra(Constants.CHATHEAD_Y, fab.getY());
*/

        }


        if (!blIsAlreadyAChatheadRequested) {
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

        if (requestCode == 12345) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Heyy", "Administration enable FAILED!");
                setContentView(R.layout.device_admin_permission_alert);
                TextView txtInfoText = (TextView) findViewById(R.id.txtInfoText);
                txtInfoText.setText("In order to use the software you should active the admin permission. Please close the application and try again!");
            }
            if (resultCode == Activity.RESULT_OK) {
                populateUI();
            }
        } else if (requestCode == Constants.RESULT_CODE_REQUEST_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                setRecyclerView();
            }
        }

    }

    /*Button.OnClickListener lst_StartService = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //startService(new Intent(MainActivity.this, ChatHeadRecordService.class));
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
            wakeLock.acquire();
        }

    };*/

    @Override
    public void onBackPressed() {

        if (!mManager.isAdminActive(mComponent)) {
            finish();
            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
               drawer.closeDrawer(GravityCompat.START);
        }
        else if (drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }
        else {
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
        if(!isDeviceSmartPhone)
        {
            menuShowPhoneRecord.setVisible(false);
            menuShowPhoneRecord.setChecked(false);

        }

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
                        // Do something when collapsed
                        //contactsListAdapter.setFilter(mContactStruct);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

        setRecyclerView();
        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchString = newText.trim().toLowerCase();
        setRecyclerView();
        return false;
    }

    public void setRecyclerView() {
        try {

            if (searchString == null) {
                noteInfoStructs = realmNoteHelper.findAllNotes(null, searchFilter,currentColorFilter,currentGroupFilter);
            } else if (searchString.isEmpty()) {
                noteInfoStructs = realmNoteHelper.findAllNotes(null, searchFilter,currentColorFilter,currentGroupFilter);
            } else if (searchFilter == Constants.CONST_SEARCH_NOTE_CONTACTS) {
                String strContactNumberByName = getContactNumberByName(searchString);

                if (!strContactNumberByName.isEmpty()) {
                    noteInfoStructs = realmNoteHelper.findAllNotes(strContactNumberByName, Constants.CONST_SEARCH_NOTE_CONTACTS,currentColorFilter,currentGroupFilter);
                } else { // Again try to search probably user has entered number
                    noteInfoStructs = realmNoteHelper.findAllNotes(searchString, Constants.CONST_SEARCH_NOTE_CONTACTS,currentColorFilter,currentGroupFilter);
                }
            } else if (searchFilter == Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION) {

                noteInfoStructs = realmNoteHelper.findAllNotes(searchString, Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION,currentColorFilter,currentGroupFilter);
            }

            mSwipeRefreshLayout.setRefreshing(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter = new NotesAdapter(mContext, noteInfoStructs, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onClick(NoteInfoStruct item) {

                if (!item.getHasAudio()) {

                    Intent intent = new Intent(mContext, ActivityViewNotes.class);
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, item.getId());

                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE, item.getTitle());
                    intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION, item.getDescription());

                    startActivity(intent);

                } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {

                    if (doWeHaveRecordsInPath(item.getId())) {

                        if (myApplication.isRecordUnderGoing() == Constants.CONST_RECORDER_SERVICE_IS_FREE) {

                            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                                    && ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))) {
                                // your code using RemoteControlClient API here - is between 14-20

                                if (myApplication.isPlaying() || myApplication.getPlayerServiceCurrentState() == Constants.CONST_PLAY_SERVICE_STATE_PAUSED) {
                                    Intent intent = new Intent(mContext, PlayerServiceLegacy.class);
                                    intent.setAction(Constants.ACTION_STOP_LEGACY);
                                    startService(intent);
                                }

                                myApplication.setIndexSomethingIsPlaying(0);

                                Intent intent = new Intent(mContext, ActivityLegacyPlayer.class);
                                intent.putExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID, item.getId());
                                startActivity(intent);

                            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                                if (myApplication.isPlaying()) {
                                    Intent intent = new Intent(mContext, PlayerService.class);
                                    intent.setAction(Constants.ACTION_STOP);
                                    startService(intent);
                                }
                                myApplication.setPlaylistHasLoaded(false);

                                Intent intent = new Intent(mContext, ActivityRecordsPlayList.class);
                                intent.putExtra(Constants.EXTRA_FOLDER_TO_PLAY_ID, String.valueOf(item.getId()));
                                startActivity(intent);

                            }

                        }
                    }
                } else if (item.getHasAudio() && (item.getCallType() > Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL)) {


                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            && ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))) {
                        // your code using RemoteControlClient API here - is between 14-20

                        if (myApplication.isPlaying() || myApplication.getPlayerServiceCurrentState() == Constants.CONST_PLAY_SERVICE_STATE_PAUSED) {
                            Intent intent = new Intent(mContext, PlayerServiceLegacy.class);
                            intent.setAction(Constants.ACTION_STOP_LEGACY);
                            startService(intent);
                        }

                        Intent intent = new Intent(mContext, ActivityLegacyPlayerPhone.class);
                        intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, item.getId());
                        String filePath = ExternalStorageManager.getWorkingDirectory() +
                                Constants.CONST_PHONE_CALLS_DIRECTORY_NAME +
                                File.separator + String.valueOf(item.getId());
                        intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE, item.getTitle());
                        intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION, item.getDescription());

                        intent.putExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER, filePath + Constants.RECORDER_AUDIO_FORMAT_AAC);
                        startActivity(intent);

                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        if (myApplication.isPlaying()) {
                            Intent intent = new Intent(mContext, PlayerService.class);
                            intent.setAction(Constants.ACTION_STOP);
                            startService(intent);
                        }

                        Intent intent = new Intent(mContext, ActivityPlayerPhone.class);
                        intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, item.getId());
                        String filePath = ExternalStorageManager.getWorkingDirectory() +
                                Constants.CONST_PHONE_CALLS_DIRECTORY_NAME +
                                File.separator + String.valueOf(item.getId());
                        intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE, item.getTitle());
                        intent.putExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION, item.getDescription());

                        intent.putExtra(Constants.EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER, filePath + Constants.RECORDER_AUDIO_FORMAT_AAC);
                        startActivity(intent);

                    }


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
                final RealmContactHelper realmContactHelper = new RealmContactHelper(mContext);
                if (!item.getHasAudio()) {
                    items = new String[]{"Edit", "Color", "Manage Groups","Delete","Share"};
                } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {

                    items = new String[]{"Edit", "Color", "Manage Groups", "Delete","Share"};
                } else {
                    if (!realmContactHelper.checkIfExistsInIgnoreList(item.getPhoneNumber().trim()))
                        items = new String[]{"Edit", "Color", "Manage Groups", "Delete", "Share", "Call", "Add to ignore"};
                    else
                        items = new String[]{"Edit", "Color", "Manage Groups", "Delete", "Share", "Call", "Remove ignore"};
                }
                final int CONST_Edit=0, CONST_COLOR=1, CONST_GROUP=2, CONST_DELETE=3, CONST_SHARE=4, CONST_CALL=5,CONST_IGNORE=6;
                final AlertDialog myDialog;


                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(Utility.ellipsize(item.getTitle(), 50));
                builder.setIcon(R.drawable.ic_sun);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == CONST_Edit) {

                            if (!item.getHasAudio()) {

                                final Intent intent = new Intent(mContext, ActivityAddTextNote.class);

                                if (myApplication.getCurrentOpenedTextNoteId() > 0) {

                                    android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(mContext);

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

                                final Intent intent = new Intent(mContext, ActivityAddAudioNote.class);

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
                                    Toast.makeText(mContext, "Note: A phone recording is in progress. You can not take audio note.", Toast.LENGTH_LONG).show();
                                }


                            } else if (item.getHasAudio() && (item.getCallType() > Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL)) {

                                Intent intent = new Intent(mContext, ActivityEditPhoneRecordNote.class);

                                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                                intent.putExtra(Constants.EXTRA_EDIT_PHONE_RECORD_NOTE, item.getId());

                                startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_LIST);
                            }

                        } else if (which == CONST_COLOR) {

                            boolean remove_enable = realmNoteHelper.getNoteColor(item.getId()) != 0  ? true : false;

                            final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();

                            int defaultColor = Color.CYAN;

                            if(remove_enable)
                                defaultColor = realmNoteHelper.getNoteColor(item.getId());

                            colorPickerDialog.initialize(R.string.color_chooser_dialog_title,filterColorsStruct.getAllColors()
                                    , defaultColor, 3, 2,remove_enable);

                            colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                                @Override
                                public void onColorSelected(int color) {
                                    realmNoteHelper.updateColor(item.getId(),color);
                                    updateSingleItemInRecycleview(item.getId());
                                }

                                @Override
                                public void onRemoveColorSelected() {
                                    realmNoteHelper.updateColor(item.getId(),Constants.CONST_NULL_ZERO);
                                    updateSingleItemInRecycleview(item.getId());
                                }
                            });

                            colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");

                        } else if (which == CONST_GROUP) {

                            String [] dictionary  = notesGroupsDictionary.getTagsList();

                            GroupsListDialog groupsListDialog;

                            String strGroup = realmNoteHelper.getNoteGroupTag(item.getId());

                            if(strGroup!=null)
                                groupsListDialog = new GroupsListDialog(mContext, "Item Group Management", true, "<font color='#007fff'> Tagged in:</font> "  + strGroup, dictionary,strGroup,false);
                            else
                                groupsListDialog = new GroupsListDialog(mContext, "Item Group Management", true, "", dictionary, strGroup,false);

                                groupsListDialog.setResultsListener(new GroupsListDialog.ResultsListener() {
                                    @Override
                                    public void selectedGroup(String selectedGroupTitle) {
                                        realmNoteHelper.updateSingleNoteGroupTag(item.getId(),selectedGroupTitle);

                                       /* Snackbar.make(recyclerView,  Utility.fromHTMLVersionCompat("Item joined to " + "<font color='#ACE5EE'>"+
                                                Utility.ellipsize(selectedGroupTitle,20) +
                                                "</font> group!", Html.FROM_HTML_MODE_LEGACY),
                                                Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();*/

                                        updateSingleItemInRecycleview(item.getId());

                                        notesGroupsDictionary = new NotesGroupsDictionary(mContext);

                                        populateRightDrawerFilters(notesGroupsDictionary.getTagsList());
                                    }

                                    @Override
                                    public void newGroupAdded(String newGroupTitle) {
                                        realmNoteHelper.updateSingleNoteGroupTag(item.getId(),newGroupTitle);

                                    /*    Snackbar.make(recyclerView,  Utility.fromHTMLVersionCompat("Item joined to " + "<font color='#ACE5EE'>"+
                                                        Utility.ellipsize(newGroupTitle,20) +
                                                        "</font> group!", Html.FROM_HTML_MODE_LEGACY),
                                                Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();*/

                                        updateSingleItemInRecycleview(item.getId());

                                        notesGroupsDictionary = new NotesGroupsDictionary(mContext);

                                        populateRightDrawerFilters(notesGroupsDictionary.getTagsList());
                                    }

                                    @Override
                                    public void removeGroupFromItem() {
                                        realmNoteHelper.updateSingleNoteGroupTag(item.getId(),null);

                                        Snackbar.make(recyclerView, "Item removed from group!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        updateSingleItemInRecycleview(item.getId());

                                        notesGroupsDictionary = new NotesGroupsDictionary(mContext);

                                        populateRightDrawerFilters(notesGroupsDictionary.getTagsList());

                                    }

                                });


                    } else if (which == CONST_DELETE) {

                            deleteItem(item, false);

                        }
                     else if (which == CONST_SHARE) { //Share

                            if (!item.getHasAudio()) {

                                String shareBody = item.getDescription();

                                if (item.getDescription() == null)
                                    shareBody = "No description";
                                if (item.getDescription().trim().isEmpty())
                                    shareBody = "No description";

                                shareBody =  item.getTitle() + ":\n" + shareBody;
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, item.getTitle());

                                sharingIntent.putExtra(Intent.EXTRA_TITLE, item.getTitle());
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                sharingIntent.putExtra(Constants.EXTRA_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION);
                                sharingIntent.putExtra(Constants.EXTRA_APP_ID, Constants.YOUR_APP_ID);
                                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                startActivityForResult(Intent.createChooser(sharingIntent, "Share Text Note"), Constants.SHARE_TO_MESSENGER_REQUEST_CODE);

                            } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {


                                String audioNotesFilesPath = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(item.getId()));

                                File f = new File(audioNotesFilesPath);


                                if (f.listFiles().length == Constants.CONST_NULL_ZERO) {
                                    Toast.makeText(mContext, "No file to share.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (!doWeHaveEnoughSpaceForShare())
                                    return;


                                File copyfolder = new File(ExternalStorageManager.getTempWorkingDirectory(), String.valueOf(item.getId()));

                                removeDirectory(copyfolder);

                                copyfolder.mkdirs();


                                try {
                                    copyFolderForShareToTempFolder(f, copyfolder);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                File file[] = copyfolder.listFiles();
                                ArrayList<Uri> imageUris = new ArrayList<Uri>();

                                //Log.d("Files", "Size: "+ file.length);
                                for (int i = 0; i < file.length; i++) {
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

                                String txtTitl = item.getTitle();

                                if (txtTitl == null)
                                    txtTitl = Constants.CONST_STRING_NO_TITLE;

                                String txtDescr = item.getDescription();

                                if (txtDescr == null) {
                                    txtDescr = Utility.unixTimeToReadable(item.getId()) +
                                            "\n" + Constants.CONST_STRING_NO_DESCRIPTION;
                                } else {
                                    txtDescr = Utility.unixTimeToReadable(item.getId()) +
                                            "\n" + txtDescr;
                                }


                                share.putExtra(Intent.EXTRA_SUBJECT, txtTitl);
                                share.putExtra(Intent.EXTRA_TITLE, txtTitl);
                                share.putExtra(Intent.EXTRA_TEXT, txtDescr);
                                share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);

                                share.setType("audio/*");
                                share.putExtra(Constants.EXTRA_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION);
                                share.putExtra(Constants.EXTRA_APP_ID, Constants.YOUR_APP_ID);
                                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                startActivityForResult(Intent.createChooser(share, "Share Audio File"), Constants.SHARE_TO_MESSENGER_REQUEST_CODE);


                            } else if (item.getHasAudio() && (item.getCallType() > Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL)) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                                LinearLayout layout = new LinearLayout(mContext);
                                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.setLayoutParams(parms);

                                layout.setGravity(Gravity.CLIP_VERTICAL);
                                layout.setPadding(2, 2, 2, 2);

                                TextView tv = new TextView(mContext);
                                tv.setText("You are sharing " + Utility.ellipsize(item.getTitle(), 50) + " phone call!");
                                tv.setPadding(40, 40, 40, 40);
                                tv.setGravity(Gravity.LEFT);
                                tv.setTextSize(20);

                                final EditText et = new EditText(mContext);
                                //String etStr = et.getText().toString();
                                TextView tv1 = new TextView(mContext);
                                tv1.setPadding(20, 10, 20, 10);
                                tv1.setText(Utility.fromHTMLVersionCompat("Type <font color='RED'>ASD</font> (case insensitive)",Html.FROM_HTML_MODE_LEGACY));

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
                                        if (!et.getText().toString().trim().toLowerCase().contains("asd")) {

                                            Toast.makeText(mContext, "Wrong text!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        if (!doWeHaveEnoughSpaceForShare())
                                            return;

                                        String phoneCallFilePath = ExternalStorageManager.getWorkingDirectory() +
                                                Constants.CONST_PHONE_CALLS_DIRECTORY_NAME +
                                                File.separator + String.valueOf(item.getId()) + Constants.RECORDER_AUDIO_FORMAT_AAC;

                                        File f = new File(phoneCallFilePath);

                                        //Uri uri = Uri.parse("file://"+f.getAbsolutePath());

                                        String tempFile = String.valueOf((int) (System.currentTimeMillis() / 1000));

                                        File tmp = new File(ExternalStorageManager.getTempWorkingDirectory() + File.separator + tempFile + Constants.RECORDER_AUDIO_FORMAT_AAC);
                                        try {
                                            ExternalStorageManager.copy(f, tmp);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        Uri uri = Uri.fromFile(tmp);

                                        Intent share = new Intent(Intent.ACTION_SEND);

                                        String txtTitl = item.getTitle();

                                        if (txtTitl == null)
                                            txtTitl = Constants.CONST_STRING_NO_TITLE;

                                        String txtDescr = item.getDescription();

                                        if (txtDescr == null) {
                                            txtDescr = Utility.unixTimeToReadable(item.getId()) +
                                                    "\n" + Constants.CONST_STRING_NO_DESCRIPTION;
                                        } else {
                                            txtDescr = Utility.unixTimeToReadable(item.getId()) +
                                                    "\n" + txtDescr;
                                        }


                                        share.putExtra(Intent.EXTRA_SUBJECT, txtTitl);
                                        share.putExtra(Intent.EXTRA_TITLE, txtTitl);
                                        share.putExtra(Intent.EXTRA_TEXT, txtDescr);
                                        share.putExtra(Intent.EXTRA_STREAM, uri);

                                        share.setType("audio/*");
                                        share.putExtra(Constants.EXTRA_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION);
                                        share.putExtra(Constants.EXTRA_APP_ID, Constants.YOUR_APP_ID);
                                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                        startActivityForResult(Intent.createChooser(share, "Share Audio File"), Constants.SHARE_TO_MESSENGER_REQUEST_CODE);


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

                        } else if (which == CONST_CALL) { // Call
                            try {
                                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS)
                                        == PackageManager.PERMISSION_GRANTED) {

                                    String uri = "tel:" + item.getPhoneNumber().trim();
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse(uri));
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                            }
                        } else if (which == CONST_IGNORE) { //Add to ignore

                            String strContactName = Utility.getContactName(mContext, item.getPhoneNumber().trim());

                            if (!realmContactHelper.checkIfExistsInIgnoreList(item.getPhoneNumber().trim())) {
                                realmContactHelper.addIgnoreList(
                                        item.getPhoneNumber().trim(),
                                        strContactName);

                                Toast.makeText(mContext, "Number added to ignore list.", Toast.LENGTH_LONG).show();
                            } else {
                                realmContactHelper.deleteContactFromIgnoreList(
                                        item.getPhoneNumber().trim()
                                );
                                Toast.makeText(mContext, "Number removed from ignore list.", Toast.LENGTH_LONG).show();

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
    private void updateSingleItemInRecycleview(int itemId) {
        NoteInfoRealmStruct re = realmNoteHelper.getNoteById(itemId);

        NoteInfoStruct nIfStrc = new NoteInfoStruct(
                re.getId(), re.getTitle(), re.getDescription(),
                re.getHasAudio(), re.getUpdateTime(), re.getCreateTimeStamp(),
                re.getStartTime(), re.getEndTime(), re.getCallType(),
                re.getPhoneNumber(), re.getColor(), re.getOrder(),
                re.getNoteType(), re.getExtras(), re.getGroup(),
                re.isDel());
        adapter.updateItemAtPosition(nIfStrc);
    }
    private void deleteItem(final NoteInfoStruct item, final boolean isItFromSwipeFunction) {
        if (myApplication.getCurrentOpenedTextNoteId() == item.getId() || myApplication.getCurrentRecordingAudioNoteId() == item.getId()) {
            Utility.showMessage("This note is already open. Close it and try again.", "Note", mContext);
            return;
        }

        if (myApplication.stackPlaylist.size() > 0) {

            if (myApplication.stackPlaylist.get(0).getParentDbId() == item.getId() && myApplication.isPlaying()) {
                // Check if player is in pause mode stop it
                if (myApplication.getPlayerServiceCurrentState() == Constants.CONST_PLAY_SERVICE_STATE_PAUSED ||
                        myApplication.getPlayerServiceCurrentState() == Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {


                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                            && ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))) {
                        // your code using RemoteControlClient API here - is between 14-20

                            Intent intent = new Intent(mContext, PlayerServiceLegacy.class);
                            intent.setAction(Constants.ACTION_STOP_LEGACY);
                            startService(intent);

                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            Intent intent = new Intent(mContext, PlayerService.class);
                            intent.setAction(Constants.ACTION_STOP);
                            startService(intent);

                    }



                }

/*
                                Utility.showMessage("This is playing. Stop it and try again.", "Note", mContext);
                                return;
*/
            }

        }
        android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(mContext);

        adb.setMessage(Utility.fromHTMLVersionCompat("Are you sure want to delete " + "<strong>" + Utility.ellipsize(item.getTitle(), 50) + "</strong>" + "?",Html.FROM_HTML_MODE_LEGACY));

        adb.setTitle("Note");
        final NoteDeleteHelper noteDeleteHelper = new NoteDeleteHelper(mContext);

        //adb.setIcon(android.R.drawable.ic_dialog_alert);

        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!item.getHasAudio()) {

                    noteDeleteHelper.deleteTextNote(item.getId());
                    Toast.makeText(mContext, "Item deleted!", Toast.LENGTH_SHORT).show();

                } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {

                    noteDeleteHelper.deleteAudioNoteAndItsFiles(item.getId());
                    Toast.makeText(mContext, "Item deleted. Contents moved to trash folder.", Toast.LENGTH_SHORT).show();

                } else if (item.getHasAudio() && (item.getCallType() > Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL)) {

                    noteDeleteHelper.deletePhoneNoteAndItsFiles(item.getId());
                    Toast.makeText(mContext, "Item deleted. Contents moved to trash folder.", Toast.LENGTH_SHORT).show();

                }

                //setRecyclerView();
                for (int j = 0; j < noteInfoStructs.size(); j++) {
                    if (noteInfoStructs.get(j).getId() == item.getId()) {
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
                if (isItFromSwipeFunction)
                    adapter.notifyDataSetChanged();
                return;
            }
        });
        adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (isItFromSwipeFunction)
                    adapter.notifyDataSetChanged();
            }
        });
        adb.show();

    }

    private boolean doWeHaveRecordsInPath(int dbId) {

        String folderOfRecords = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(dbId));

        boolean foundAnyFile = false;

        File file = new File(folderOfRecords);

        if (file.exists() && file.isDirectory()) {
            for (File tmpFile : file.listFiles()) {
                if (tmpFile.isFile()) {
                    foundAnyFile = true;
                    break;
                }
            }

            //Folder is exist but it is empty empty.
            if (!foundAnyFile) {
                file.delete();
            }
        } else {
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
    private void copyFolderForShareToTempFolder(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            // int timeStampForShare = (int) (System.currentTimeMillis() / 1000);

            for (int i = 0; i < children.length; i++) {
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

    private boolean doWeHaveEnoughSpaceForShare() {

        boolean hasEnoughSpace = ExternalStorageManager.isThereEnoughSpaceOnStorage();

        if (hasEnoughSpace) {
            return true;
        } else {

            android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(mContext);


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

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    return true;
                }
            }

            if (!Prefs.getBoolean(Constants.PREF_FIRST_QUESTION_PHONERECORDING, false)) {
                String msg = "In some countries, even if both parties know about the recording the call " +
                        "conversation, that would still be violating the law and privacy. Record the phone " +
                        "calls on your own responsibility. Please for more details about the call recording " +
                        "refer to your local or national regulations and laws.";
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
            } else {

                phoneCallEnableDisable(item);

            }


        } else if (id == R.id.nav_backup_report) {

            Intent intent = new Intent(this, ActivityBackupHome.class);
            startActivityForResult(intent,123);

        } else if (id == R.id.nav_emptytrash) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

            LinearLayout layout = new LinearLayout(mContext);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(parms);

            layout.setGravity(Gravity.CLIP_VERTICAL);
            layout.setPadding(2, 2, 2, 2);

            TextView tv = new TextView(mContext);
            tv.setText("You are deleting all of trash files. This operation does not revert back!");
            tv.setPadding(40, 40, 40, 40);
            tv.setGravity(Gravity.LEFT);
            tv.setTextSize(20);

            final EditText et = new EditText(mContext);
           // String etStr = et.getText().toString();

            TextView tv1 = new TextView(mContext);
            tv1.setPadding(20, 10, 20, 10);

            tv1.setText(Utility.fromHTMLVersionCompat("Type <font color='RED'>ASD</font> (case insensitive)",Html.FROM_HTML_MODE_LEGACY));

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
                    if (!et.getText().toString().trim().toLowerCase().contains("asd")) {

                        Toast.makeText(mContext, "Wrong text!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    File trashDir = new File(ExternalStorageManager.getTrashDirectory());

                    removeDirectory(trashDir);
                    File tempDir = new File(ExternalStorageManager.getTempWorkingDirectory());

                    removeDirectory(tempDir);

                    Toast.makeText(mContext, "Trash is empty", Toast.LENGTH_SHORT).show();


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

            android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(mContext);

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
                    intent.putExtra(Intent.EXTRA_TEXT, "Android version (if possible): " + "\n\n" +
                            "Section of app you get error or problem (e.g Recorder): " + "\n\n" +
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
                    intent.putExtra(Intent.EXTRA_TEXT, "Section of app: " + "\n\n" +
                            "Or whatever is your idea: ");
                    startActivity(intent);
                }
            });

            adb.show();

        } else if (id == R.id.nav_take_todo) {

            Intent intent = new Intent(this, ActivityTodoParentRecyclerView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_rate) {

            Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
            }

        } else if (id == R.id.nav_help) {
            String url = Constants.CONST_URL_HELPS;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(mContext, ActivityAbout.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
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

    private void phoneCallEnableDisable(MenuItem item) {
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
