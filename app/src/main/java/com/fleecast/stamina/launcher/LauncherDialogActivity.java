package com.fleecast.stamina.launcher;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;


public class LauncherDialogActivity extends AppCompatActivity {
	public static boolean active = false;
	public static boolean lostFocus = false;
	public static Activity myActivityInstance;
	public MyApplication myApplication;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private Bundle savedInstanceState;
	private ViewPager mViewPager;

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		Log.e("DBG", "onBackPressed");

		myApplication.setLauncherDialogNotVisible(true);
		//this.setVisible(false);
		moveTaskToBack(true);
		overridePendingTransition(0, R.anim.scale_down);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("ALA","MALA " + Math.random());
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

//		getSupportActionBar().hide();

		setContentView(R.layout.dialog);


		myApplication = (MyApplication) getApplication();

		Log.e("DBG","LauncherDialogActivity onCreate called");

		myActivityInstance = LauncherDialogActivity.this;
		this.savedInstanceState= savedInstanceState;

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);


		/*RelativeLayout fragment_container = (RelativeLayout) findViewById(R.id.fragment_container);
		FragmentManager fragmentManager = getFragmentManager();
		if (savedInstanceState == null) {
			Log.e("DBG","NNNNNNNNNNNNNNNNNNNNN");

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			AppLauncherFragment appLauncherFragment = new AppLauncherFragment();
			fragmentTransaction.add(R.id.fragment_container, appLauncherFragment, "Async_Task_Demo_Fragment");
			fragmentTransaction.commit();

		} else {
			AppLauncherFragment appLauncherFragment = (AppLauncherFragment) fragmentManager.findFragmentByTag("Async_Task_Demo_Fragment");
		}*/


	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("DBG", "onResume: " + active);
		active = true;
		lostFocus=false;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.e("DBG", "onRestoreInstanceState: " + active);

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e("DBG", "onStop: " + active);
		lostFocus=true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// Check if search box in not focused:
		View view = getCurrentFocus();
		if (view != null && view instanceof EditText) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		//onTrimMemory(TRIM_MEMORY_MODERATE);
		Log.e("DBG", "onPause: " + active);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("DBG", "onDestroysssssssssssssssssssssssssssssssssss: " + active);
		active = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
			overridePendingTransition(R.anim.scale_up,R.anim.scale_none);

		Log.e("DBG", "onStart: " + active);

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
		public android.support.v4.app.Fragment getItem(int position) {

			if(myApplication.getAppJustLaunchedByUser()==0)
				mViewPager.setCurrentItem(0);


			if (position == 0) {
				AppLauncherFragment appLauncherFragment = new AppLauncherFragment();
				return  appLauncherFragment;
			} else {
				RecentMostUsedFragment recentMostUsedFragment = new RecentMostUsedFragment();
				return  recentMostUsedFragment;
			}

/*
			if (position == 0) {
				CustomListLauncherFragment customListLauncherFragment = new CustomListLauncherFragment();
				return  customListLauncherFragment;
			} else if (position == 1) {
				AppLauncherFragment appLauncherFragment = new AppLauncherFragment();
				return  appLauncherFragment;
			} else {
				RecentMostUsedFragment recentMostUsedFragment = new RecentMostUsedFragment();
				return  recentMostUsedFragment;
			}
*/



		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
			//return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return "SECTION 1";
				case 1:
					return "SECTION 2";
				/*case 2:
					return "SECTION 3";*/
			}
			return null;
		}
	}

}
