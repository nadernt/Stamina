package com.fleecast.stamina.notetaking;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.ContactStruct;
import com.fleecast.stamina.models.RealmContactHelper;

public class ActivityIgnoreListManager extends AppCompatActivity implements
        FragmentContactsList.OnContactListFragmentInteractionListener,
        FragmentIgnoreContact.OnIgnoreListFragmentInteractionListener {

    private RealmContactHelper realmContactHelper;
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

    /*private OnRefreshContactListsListener sd;

    public void setInterface(OnRefreshContactListsListener sd) {
        this.sd = sd;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignore_list_manager);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        realmContactHelper = new RealmContactHelper(ActivityIgnoreListManager.this);

    }

    @Override
    public void onIgnoreListFragmentInteraction(ContactStruct item) {

        Log.e("DBG", item.getContactNumber());
        showListOfOptions(item,0);
    }

    @Override
    public void onContactsListFragmentInteraction(ContactStruct item) {

        Log.e("DBG", item.getContactNumber());
        showListOfOptions(item, 1);

    }

    private void showListOfOptions(ContactStruct contactStruct,final int fragmentNumber){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityIgnoreListManager.this);

        final ContactStruct cStr = contactStruct;

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                ActivityIgnoreListManager.this,
                android.R.layout.simple_list_item_1);

        //if(!realmContactHelper.checkIfExistsInIgnoreList(contactStruct.getContactNumber())) {
        if (fragmentNumber==0) {
            arrayAdapter.add("Remove from record ignore list");
        }
        else {
            arrayAdapter.add("Add to record ignore list");

        }
            arrayAdapter.add("Call this contact");



        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int itemNo) {
                            // If it is not last item.(last item is call)
                        if (itemNo != arrayAdapter.getCount()-1) {

                            if (fragmentNumber == 0) {
                                Log.e("DBG", "deleteContactFromIgnoreList");
                                realmContactHelper.deleteContactFromIgnoreList(
                                        cStr.getContactNumber()
                                );

                                mViewPager.setAdapter(mSectionsPagerAdapter);

                            } else if (fragmentNumber == 1) {
                                Log.e("DBG", "addIgnoreList");

                                realmContactHelper.addIgnoreList(
                                        cStr.getContactNumber(),
                                        cStr.getContactName()
                                );
                            }
                        }
                        else{
                            try {
                                String uri = "tel:" + cStr.getContactNumber().trim();
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse(uri));
                                startActivity(intent);
                            } catch (Exception e) {
                            }
                        }

                        dialog.dismiss();
                    }
                });
        builderSingle.show();

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
            if(position==0)
            {
                return FragmentIgnoreContact.newInstance(position);
            }
            else
            {
                return FragmentContactsList.newInstance(position);


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
                case 0:
                    return "RECORD IGNORED CONTACTS";
                case 1:
                    return "ALL CONTACTS";
            }
            return null;
        }
    }

}
