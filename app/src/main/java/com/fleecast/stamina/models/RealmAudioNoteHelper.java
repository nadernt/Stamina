package com.fleecast.stamina.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class RealmAudioNoteHelper {


    private static final String TAG = "RealmAudioNoteHelper";


    private Realm realm;
    private RealmResults<AudioNoteInfoRealmStruct> realmResult;
    public Context context;
    private boolean DEBUG = false;


    /**
     * constructor to create instances of realm
     *
     * @param context
     */
    public RealmAudioNoteHelper(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
        DEBUG = false;

    }


    /**
     * add data
     *
     * @param title
     * @param description
     */
    public void addAudioNote(int id,int parent_db_id, String title, String description, int tag) {
        AudioNoteInfoRealmStruct audioNoteInfoRealmStruct = new AudioNoteInfoRealmStruct();
        audioNoteInfoRealmStruct.setId(id);
        audioNoteInfoRealmStruct.setParentDbId(parent_db_id);
        audioNoteInfoRealmStruct.setTitle(title);
        audioNoteInfoRealmStruct.setDescription(description);
        audioNoteInfoRealmStruct.setTag(0);
        realm.beginTransaction();
        realm.copyToRealm(audioNoteInfoRealmStruct);
        realm.commitTransaction();
        showLog("Added ; " + title);
    }

    /**
     *
     * @param id
     * @param title
     * @param description
     * @return
     */
    public void updateAudioNote(int id, String title, String description) {

        realm.beginTransaction();
        AudioNoteInfoRealmStruct audioNoteInfoRealmStruct = realm.where(AudioNoteInfoRealmStruct.class).equalTo("id", id).findFirst();
        audioNoteInfoRealmStruct.setId(id);
        audioNoteInfoRealmStruct.setTitle(title);
        audioNoteInfoRealmStruct.setDescription(description);
        realm.commitTransaction();
    }

    /**
     * Check if audio note by id exists.
     *
     * @param id
     * @return
     */
    public boolean isNoteExist(int id) {

        RealmQuery<AudioNoteInfoRealmStruct> query = realm.where(AudioNoteInfoRealmStruct.class)
                .equalTo("id", id);

        return query.count() == 0 ? false : true;
    }

    /**
     * Read note by id
     *
     * @param id
     * @return
     */
    public AudioNoteInfoRealmStruct getNoteById(int id) {

        AudioNoteInfoRealmStruct audioNoteInfoRealmStruct = realm.where(AudioNoteInfoRealmStruct.class).equalTo("id", id).findFirst();


        return audioNoteInfoRealmStruct;
    }

     /**
     * method search all audio note by parent_db_id
     * @param parent_db_id
     * @return
     */
    public RealmResults<AudioNoteInfoRealmStruct> findAllAudioNotesByParentId(int parent_db_id) {

        return realm.where(AudioNoteInfoRealmStruct.class).equalTo("parent_db_id", parent_db_id).findAll();
    }

/*

    public ArrayList<AudioNoteInfoStruct> findAllAudioNotes() {
        ArrayList<AudioNoteInfoStruct> data = new ArrayList<>();


        realmResult = realm.where(AudioNoteInfoRealmStruct.class).findAll();
        realmResult.sort("id", Sort.DESCENDING);
        if (realmResult.size() > 0) {

            //String title, description,audioFileName;

            for (int i = 0; i < realmResult.size(); i++) {

                int id = realmResult.get(i).getId();
                String title = realmResult.get(i).getTitle();
                String description = realmResult.get(i).getDescription();
                int parentDbId = realmResult.get(i).getParentDbId();
                int tag = realmResult.get(i).getTag();

                data.add(new AudioNoteInfoStruct(id, parentDbId,title, description,0));
            }

        } else {
            showLog("Size : 0");
        }

        return data;
    }
*/


    /**
     * method update article
     *
     * @param id
     * @param title
     * @param description
     *//*
    public void updateNote(int id, String title, String description, boolean has_audio)  {
        Date now = new Date();

        realm.beginTransaction();

        AudioNoteInfoRealmStruct audioNoteInfoRealmStruct = realm.where(AudioNoteInfoRealmStruct.class).equalTo("id", id).findFirst();

        audioNoteInfoRealmStruct.setTitle(title);
        audioNoteInfoRealmStruct.setDescription(description);

        realm.commitTransaction();
        showLog("Updated : " + title);
    }
*/

    /**
     * method delete articles by id
     *
     * @param id
     */
    public void deleteSingleAudioNote(int id) {
        RealmResults<AudioNoteInfoRealmStruct> audioNotesToDelete = realm.where(AudioNoteInfoRealmStruct.class).equalTo("id", id).findAll();
        realm.beginTransaction();
        audioNotesToDelete.deleteFirstFromRealm();
        realm.commitTransaction();

    }

    public void deleteAllAudioNoteByParentId(int ParentId) {
        final RealmResults<AudioNoteInfoRealmStruct> audioNotesToDelete = realm.where(AudioNoteInfoRealmStruct.class).equalTo("parent_db_id", ParentId).findAll();
        realm.beginTransaction();
        audioNotesToDelete.deleteAllFromRealm();
        realm.commitTransaction();
        // All changes to data must happen in a transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Delete all matches
                audioNotesToDelete.deleteAllFromRealm();
            }
        });
    }

    /**
     * make log
     *
     * @param s
     */
    private void showLog(String s) {
        if(DEBUG)
        Log.d(TAG, s);

    }

    /**
     * make Toast Information
     */
    private void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

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