package com.fleecast.stamina.chathead;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
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
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.notetaking.ActivityIgnoreListManager;
import com.fleecast.stamina.notetaking.ActivityRecordsPlayList;
import com.fleecast.stamina.notetaking.NoteTakingRecyclerViewActivity;
import com.fleecast.stamina.notetaking.PhonecallReceiver;
import com.fleecast.stamina.settings.ActivitySettings;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DevicePolicyManager mManager;
    private ComponentName mComponent;
    private PowerManager.WakeLock wakeLock;
    private FloatingActionButton fab;
    private boolean blIsAlreadyAChatheadRequested=false;
    private MyApplication myApplication;
    private MenuItem mnuItemPhoneRecord;


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


//        if(1<2){
        if(!Prefs.getBoolean(Constants.PREF_FIRST_INITIAL_OF_APP, false)) {

            Prefs.putBoolean(Constants.PREF_FIRST_INITIAL_OF_APP, true);

            Prefs.putBoolean(Constants.RECORDER_PHONE_IS_RECORD, false);

            Prefs.putBoolean(Constants.PREF_SORT_IS_ALPHABETIC_OR_DATE, true);

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

        startActivity(new Intent(this,ActivityRecordsPlayList.class));

     //   startActivity(new Intent(this, AddActivity.class));
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

    setContentView(R.layout.activity_main);

    myFuckUp();

    myApplication =  (MyApplication)getApplicationContext();

    myApplication.setLauncherDialogNotVisible(false);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
    setSupportActionBar(toolbar);




    fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            myFuckUp();
            //myApplication.reInitEverything();
            startTheChatHead();
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
            myIntent.putExtra("fabX", fab.getX());
            myIntent.putExtra("fabY", fab.getY());

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

        switch (requestCode) {
            case 12345:
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_take_note) {

            Intent intent = new Intent(this, NoteTakingRecyclerViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_phone_record) {
            ComponentName component = new ComponentName(this, PhonecallReceiver.class);

            //Disable call recording BroadcastReceiver (class PhonecallReceiver)
            if(Prefs.getBoolean(Constants.RECORDER_PHONE_IS_RECORD,false)) {
                //Disable
                this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
                Prefs.putBoolean(Constants.RECORDER_PHONE_IS_RECORD, false);
                item.setTitle("Record Calls   ");
            }
            else { //Enable call recording BroadcastReceiver (class PhonecallReceiver)
                //Enable
                this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
                Prefs.putBoolean(Constants.RECORDER_PHONE_IS_RECORD, true);
                item.setTitle("Record Calls  ✔");
            }

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


}
