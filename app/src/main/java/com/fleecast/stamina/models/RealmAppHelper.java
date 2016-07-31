package com.fleecast.stamina.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fleecast.stamina.utility.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmAppHelper {


    private static final String TAG = "RealmAppHelper";


    private Realm realm;
    private RealmResults<AppDbRealmStruct> realmResult;
    public Context context;
    private boolean DEBUG = false;

    /**
     * constructor to create instances of realm
     *
     * @param context
     */
    public RealmAppHelper(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
        DEBUG = false;
    }


    /**
     * add data
     *
     * @param title
     * @param app_package_name
     * @param app_group
     * @param use_rank
     * @param last_use_time_stamp
     */

    public void add(String title, String app_package_name, int app_group, int use_rank, Date last_use_time_stamp) {

        //If not exist then add it to database except update it
        if (getAppIfoByPackageName(app_package_name) == null) {
            AppDbRealmStruct appDbRealmStruct = new AppDbRealmStruct();

            int uniqueIdentifier = (int) (System.currentTimeMillis() / 1000);

            appDbRealmStruct.setId(uniqueIdentifier);
            appDbRealmStruct.setTitle(title);
            appDbRealmStruct.setAppPackageName(app_package_name);
            appDbRealmStruct.setAppGroup(app_group);
            appDbRealmStruct.setUseRank(use_rank);
            appDbRealmStruct.setLastUseTimeStamp(last_use_time_stamp);

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(appDbRealmStruct);
            realm.commitTransaction();

            showLog("Added ; " + title);
        } else {
            updateAppGroup(app_package_name, app_group);
        }
    }

    /**
     * Update app group.
     *
     * @param app_package_name
     * @param app_group
     */

    private void updateAppGroup(String app_package_name, int app_group) {

        realm.beginTransaction();
        AppDbRealmStruct appDbRealmStruct = realm.where(AppDbRealmStruct.class).equalTo("app_package_name", app_package_name).findFirst();
        appDbRealmStruct.setAppGroup(app_group);
        realm.commitTransaction();
        showLog("Updated");
    }

    public void updateLastUsageApp(String title, String app_package_name) {

        realm.beginTransaction();
        AppDbRealmStruct appDbRealmStruct = realm.where(AppDbRealmStruct.class).equalTo("app_package_name", app_package_name).findFirst();
        Date now = new Date();
        if (appDbRealmStruct != null) {

            appDbRealmStruct.setLastUseTimeStamp(now);
            appDbRealmStruct.setUseRank(appDbRealmStruct.getUseRank() + 1);

        } else {
            appDbRealmStruct = new AppDbRealmStruct();

            int uniqueIdentifier = (int) (System.currentTimeMillis() / 1000);

            appDbRealmStruct.setId(uniqueIdentifier);
            appDbRealmStruct.setTitle(title);
            appDbRealmStruct.setAppPackageName(app_package_name);
            appDbRealmStruct.setAppGroup(Constants.ALL_APPS_DEFAULT_GROUP);
            appDbRealmStruct.setUseRank(1);
            appDbRealmStruct.setLastUseTimeStamp(now);

            realm.copyToRealm(appDbRealmStruct);
        }

        viewAllDB();

        realm.commitTransaction();

        showLog("Updated");
    }

    private void viewAllDB() {

        RealmResults<AppDbRealmStruct> query = realm.where(AppDbRealmStruct.class).findAll();

        for (int i = 0; i < query.size(); i++) {

            Log.e("Current Packages:", query.get(i).getTitle() + " " + query.get(i).getAppGroup());
        }
    }


    public RealmResults<AppDbRealmStruct> getAllAppsDatabaseInfo() {

        RealmResults<AppDbRealmStruct> query = realm.where(AppDbRealmStruct.class).findAll();

        return query;
    }


    /**
     * Check if package is listed before in the db
     *
     * @param app_package_name
     * @return
     */
    public boolean checkIfExists(String app_package_name) {

        RealmQuery<AppDbRealmStruct> query = realm.where(AppDbRealmStruct.class)
                .equalTo("app_package_name", app_package_name);

        return query.count() == 0 ? false : true;
    }


    public AppDbRealmStruct getAppIfoByPackageName(String app_package_name) {

        RealmQuery<AppDbRealmStruct> query = realm.where(AppDbRealmStruct.class)
                .equalTo("app_package_name", app_package_name);

        return query.findFirst();
    }

    /**
     * update data
     *
     * @param title
     * @param app_package_name
     * @param app_group
     * @param use_rank
     * @param last_use_time_stamp
     */

    public void update(String title, String app_package_name, int app_group, int use_rank, Date last_use_time_stamp) {
        realm.beginTransaction();

        AppDbRealmStruct appDbRealmStruct = realm.where(AppDbRealmStruct.class).equalTo("app_package_name", app_package_name).findFirst();
        appDbRealmStruct.setTitle(title);
        appDbRealmStruct.setAppPackageName(app_package_name);
        appDbRealmStruct.setAppGroup(app_group);
        appDbRealmStruct.setUseRank(use_rank);
        appDbRealmStruct.setLastUseTimeStamp(last_use_time_stamp);

        realm.commitTransaction();

        showLog("Updated : " + title);
    }

    public void updateAppGroupAfterDelete(int app_group) {
        /*realm.beginTransaction();

        RealmQuery<AppDbRealmStruct> query = realm.where(AppDbRealmStruct.class)
                .equalTo("app_group", app_package_name);

        AppDbRealmStruct appDbRealmStruct = realm.where(AppDbRealmStruct.class).equalTo("app_group", app_group);
        appDbRealmStruct.setAppGroup(app_group);

        realm.commitTransaction();
        showLog("Updated : " + app_group);

        showToast(app_group + " successfully updated.");*/
    }

    /**
     * method delete articles by id
     *
     * @param app_package_name
     */

    public void delete(String app_package_name) {

        RealmResults<AppDbRealmStruct> appToDeleteFromDb = realm.where(AppDbRealmStruct.class).equalTo("app_package_name", app_package_name).findAll();

        if (appToDeleteFromDb.size() > 0) {
            realm.beginTransaction();
            appToDeleteFromDb.deleteFirstFromRealm();
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
        if (DEBUG)
            Log.d(TAG, s);

    }

    /**
     * make Toast Information
     */
    private void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public List<MostUsedAndRecentAppsStruct> getAppListByMostUsed() {

        List<MostUsedAndRecentAppsStruct> mostUsedAppsStruct = new ArrayList<>();
        RealmResults<AppDbRealmStruct> query = realm.where(AppDbRealmStruct.class).greaterThan("use_rank", 0).findAllSorted("use_rank", Sort.DESCENDING);
        for (int i = 0; i < query.size(); i++) {
            mostUsedAppsStruct.add(new MostUsedAndRecentAppsStruct(query.get(i).getTitle(), query.get(i).getAppPackageName(), query.get(i).getAppGroup(), query.get(i).getUseRank()));
            //  Log.e("Most Used Packages", query.get(i).getAppPackageName());
        }
        return mostUsedAppsStruct;

    }

    public List<MostUsedAndRecentAppsStruct> getAppListByRecentUsed() {

        List<MostUsedAndRecentAppsStruct> mostUsedAppsStruct = new ArrayList<>();
        RealmResults<AppDbRealmStruct> query = realm.where(AppDbRealmStruct.class).findAllSorted("last_use_time_stamp", Sort.DESCENDING);
        for (int i = 0; i < query.size(); i++) {

            if(query.get(i).getUseRank()>Constants.CONST_NULL_ZERO) // we do this because drag & drop add zero to this field and makes mistake.
                mostUsedAppsStruct.add(new MostUsedAndRecentAppsStruct(query.get(i).getTitle(), query.get(i).getAppPackageName(), query.get(i).getAppGroup(), query.get(i).getUseRank()));
            //Log.e("Most Used Packages", query.get(i).getAppPackageName());
        }
        return mostUsedAppsStruct;

    }
}