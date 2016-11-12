package com.fleecast.stamina.settings;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

public class ActivitySettings extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String TAG = "ActivitySettings";
    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarIgnoreListManager);
        setSupportActionBar(toolbar);

        myApplication =  (MyApplication) getApplicationContext();

        if(myApplication.isRecordUnderGoing()>Constants.CONST_RECORDER_SERVICE_IS_FREE)
        {
            Utility.showMessage("There is an under going record please stop it and try again.","Note",this);
        }else {

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
 }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, " onPause ActivitySettings!" );
       // finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            /*if(position==0)
            {
                return FragmentAppSettings.newInstance(position);
            }
            else if(position==1)*/
            if(position==0)
            {
                return FragmentNoteTakingSettings.newInstance(position);
            }
            else
            {
                return FragmentPhoneCallSettings.newInstance(position);


            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
               /* case 0:
                    return "Application";*/
                case 0:
                    return "Notes";
                case 1:
                    return "Phone Recorder";
            }
            return null;
        }

    }
}
