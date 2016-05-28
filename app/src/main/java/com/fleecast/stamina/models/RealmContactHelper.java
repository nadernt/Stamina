package com.fleecast.stamina.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class RealmContactHelper {


    private static final String TAG = "RealmContactHelper";


    private Realm realm;
    public Context context;

    /**
     * constructor to create instances of realm
     *
     * @param context
     */
    public RealmContactHelper(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
    }


    public void addBlackList(String contact_number, String contact_name) {

        ContactDbRealmStruct contactDbRealmStruct = new ContactDbRealmStruct();

        String uniqueIdentifier = getJustNumberOfPhone(contact_number);

        contactDbRealmStruct.setId(uniqueIdentifier);
        contactDbRealmStruct.setContactName(contact_name);
        contactDbRealmStruct.setContactNumber(contact_number);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(contactDbRealmStruct);
        realm.commitTransaction();
    }


    public boolean checkIfExistsInBlockList(String contact_number) {

        RealmQuery<ContactDbRealmStruct> query = realm.where(ContactDbRealmStruct.class)
                .equalTo("id", getJustNumberOfPhone(contact_number));
        return query.count() == 0 ? false : true;
    }


    private void viewAllDB() {

        RealmResults<ContactDbRealmStruct> query = realm.where(ContactDbRealmStruct.class).findAll();

        for (int i = 0; i < query.size(); i++) {

            Log.e("Blacklisted:", query.get(i).getContactName() + " " + query.get(i).getContactNumber());
        }
    }

    public List<ContactStruct> getBlockList() {

        RealmResults<ContactDbRealmStruct> query = realm.where(ContactDbRealmStruct.class).findAll();
        List<ContactStruct> contactStructList = new ArrayList<>();
        for (int i = 0; i < query.size(); i++) {

            contactStructList.add(new ContactStruct(query.get(i).getId(), query.get(i).getContactNumber(), query.get(i).getContactName()));
        }
        return contactStructList;
    }

/*
    public RealmResults<AppDbRealmStruct> getAllAppsDatabaseInfo(){

        RealmResults<AppDbRealmStruct> query = realm.where(AppDbRealmStruct.class).findAll();

       *//* for(int i=0; i<query.size();i++){
            GridViewAppItemStruct
            getAllAppsDatabaseInfo().add()
            Log.e("Current Packages:", query.get(i).getTitle() + " " + query.get(i).getAppGroup());
        }*//*
        return  query;
    }*/


    public void deleteContactFromBlockList(String contact_number) {

        RealmResults<ContactDbRealmStruct> contactToDeleteFromDbBlacklist = realm.where(ContactDbRealmStruct.class).equalTo("id", getJustNumberOfPhone(contact_number)).findAll();

        if (contactToDeleteFromDbBlacklist.size() > 0) {
            realm.beginTransaction();
            contactToDeleteFromDbBlacklist.deleteFirstFromRealm();
            realm.commitTransaction();
        }

//        showToast("Clear data successfully.");
    }


    /**
     * make log
     *
     * @param s
     */
    private void showLog(String s) {
        Log.d(TAG, s);

    }

    /**
     * make Toast Information
     */
    private void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    //Filter the number by regex then just a consequence of numbers will make as phone number.
    private String getJustNumberOfPhone(String strNumber) {

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(strNumber);
        String returnedNumber = "";
        while (m.find()) {
            returnedNumber += m.group();
        }

        return returnedNumber;

    }

}