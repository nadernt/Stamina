package com.fleecast.stamina.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmGroupsHelper {


    private static final String TAG = "RealmGroupHelper";


    private Realm realm;
    private RealmResults<GroupsDbRealmStruct> realmResult;
    public Context context;
    private boolean DEBUG = false;

    /**
     * constructor to create instances of realm
     *
     * @param context
     */
    public RealmGroupsHelper(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
        DEBUG = false;
    }


    /**
     * add data
     *
     * @param title
     */

    public void add(String title, String iconFilePath) {

        GroupsDbRealmStruct groupsDbRealmStruct = new GroupsDbRealmStruct();

        int uniqueIdentifier = (int) (System.currentTimeMillis() / 1000);

        groupsDbRealmStruct.setId(uniqueIdentifier);
        groupsDbRealmStruct.setTitle(title);
        groupsDbRealmStruct.setAppGroupCode(uniqueIdentifier);

        groupsDbRealmStruct.setAppGroupOrder(uniqueIdentifier);
        groupsDbRealmStruct.setIcon(iconFilePath);

        realm.beginTransaction();
        realm.copyToRealm(groupsDbRealmStruct);
        realm.commitTransaction();

    }

    /**
     * Check if package is listed before in the db
     *
     * @param id
     * @return
     */
    public boolean checkIfExistsById(int id) {

        RealmQuery<GroupsDbRealmStruct> query = realm.where(GroupsDbRealmStruct.class)
                .equalTo("id", id);
        return query.count() == 0 ? false : true;
    }


    public GroupsDbRealmStruct getGroupIfoByGroupCode(int id) {

        RealmQuery<GroupsDbRealmStruct> query = realm.where(GroupsDbRealmStruct.class)
                .equalTo("id", id);

        return query.findFirst();
    }


    public ArrayList<GroupsModel> findAllGroups() {
        ArrayList<GroupsModel> data = new ArrayList<>();


        realmResult = realm.where(GroupsDbRealmStruct.class).findAll();
        realmResult = realmResult.sort("id", Sort.DESCENDING);
        if (realmResult.size() > 0) {
            showLog("Size : " + realmResult.size());


            for (int i = 0; i < realmResult.size(); i++) {

                int id = realmResult.get(i).getId();
                String title = realmResult.get(i).getTitle();
                int groupCode = realmResult.get(i).getAppGroupCode();
                String icon_path = realmResult.get(i).getIcon();
                int groupCodeOrder = realmResult.get(i).getAppGroupOrder();
                data.add(new GroupsModel(id, title, groupCode, icon_path, groupCodeOrder));

            }

        } else {
            showLog("Size : 0");
        }

        return data;
    }

    public ArrayList<GroupsModel> findAllGroupsByOrder() {
        ArrayList<GroupsModel> data = new ArrayList<>();


        realmResult = realm.where(GroupsDbRealmStruct.class).findAll();
        realmResult = realmResult.sort("app_group_order", Sort.ASCENDING);
        if (realmResult.size() > 0) {
            for (int i = 0; i < realmResult.size(); i++) {

                int id = realmResult.get(i).getId();
                String title = realmResult.get(i).getTitle();
                int groupCode = realmResult.get(i).getAppGroupCode();
                String icon_path = realmResult.get(i).getIcon();
                int groupCodeOrder = realmResult.get(i).getAppGroupOrder();
                data.add(new GroupsModel(id, title, groupCode, icon_path, groupCodeOrder));

            }

        } else {
            showLog("Size : 0");
        }

        return data;
    }

    /**
     * update data
     *
     * @param title
     * @param app_group_code
     */

    public boolean update(int id, String title, String iconFilePath, int app_group_code, int app_group_code_order) {

        if (checkIfExistsById(id)) {
            GroupsDbRealmStruct groupsDbRealmStruct = new GroupsDbRealmStruct();

            groupsDbRealmStruct.setTitle(title);
            groupsDbRealmStruct.setAppGroupCode(app_group_code);
            groupsDbRealmStruct.setAppGroupOrder(app_group_code_order);
            groupsDbRealmStruct.setIcon(iconFilePath);

            realm.beginTransaction();
            realm.copyToRealm(groupsDbRealmStruct);
            realm.commitTransaction();

            showLog("Update ; " + title);
            return true;
        }

        return false;

    }

    /**
     * Set the order of groups
     *
     * @param id
     * @param app_group_code_order
     */
    public void updateGroupOrderById(int id, int app_group_code_order) {

        realm.beginTransaction();
        GroupsDbRealmStruct groupsDbRealmStruct = realm.where(GroupsDbRealmStruct.class).equalTo("id", id).findFirst();
        groupsDbRealmStruct.setAppGroupOrder(app_group_code_order);
        realm.commitTransaction();


    }

    /**
     * Rename group
     *
     * @param id
     * @param title
     * @param iconFilePath
     */
    public void renameGroupById(int id, String title, String iconFilePath) {

        realm.beginTransaction();

        GroupsDbRealmStruct groupsDbRealmStruct = realm.where(GroupsDbRealmStruct.class).equalTo("id", id).findFirst();
        groupsDbRealmStruct.setTitle(title);
        groupsDbRealmStruct.setIcon(iconFilePath);
        realm.commitTransaction();

    }

    /**
     * method delete group by id
     *
     * @param id
     */

    public void deleteGroupById(int id) {
        RealmResults<GroupsDbRealmStruct> dataResults = realm.where(GroupsDbRealmStruct.class).equalTo("id", id).findAll();
        realm.beginTransaction();
        dataResults.deleteFirstFromRealm();
        realm.commitTransaction();
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
}