package com.fleecast.stamina.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Prefs;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmNoteHelper {


    private static final String TAG = "RealmNoteHelper";


    private Realm realm;
    public Context context;
    private boolean DEBUG = false;


    /**
     * constructor to create instances of realm
     *
     * @param context
     */
    public RealmNoteHelper(Context context) {
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
    public void addNote(int id, String title, String description, boolean has_audio,
                        Date update_time,Date create_time_stamp, Date start_time,
                        Date end_time, int call_type, String phone_number, int tag,
                        int order) {

        if(!isExist(id)) {
            NoteInfoRealmStruct noteInfoRealmStruct = new NoteInfoRealmStruct();
            noteInfoRealmStruct.setId(id);
            noteInfoRealmStruct.setTitle(title);
            noteInfoRealmStruct.setDescription(description);
            noteInfoRealmStruct.setHasAudio(has_audio);
            Date now = new Date();
            noteInfoRealmStruct.setUpdateTime(now);
            noteInfoRealmStruct.setCreateTimeStamp(now);
            //data.add(new NoteInfoStruct(id, title, description,has_audio,update_time,create_time_stamp,null,null,-1,null,0,0));
            noteInfoRealmStruct.setStartTime(null);
            noteInfoRealmStruct.setEndTime(null);
            noteInfoRealmStruct.setCallType(call_type);
            noteInfoRealmStruct.setPhoneNumber(phone_number);
            noteInfoRealmStruct.setTag(0);
            noteInfoRealmStruct.setOrder(0);
            realm.beginTransaction();
            realm.copyToRealm(noteInfoRealmStruct);
            realm.commitTransaction();
            showLog("Added ; " + title);
        }
        else
        {
            updateNotes(id,title,description,update_time,tag,order);
        }

    }


    /**
     * Check if note by id exists.
     *
     * @param id
     * @return
     */
    public boolean isExist(int id) {

/*
        NoteInfoRealmStruct noteInfoRealmStruct = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();
*/

        RealmQuery<NoteInfoRealmStruct> query = realm.where(NoteInfoRealmStruct.class)
                .equalTo("id", id);

        return query.count() == 0 ? false : true;
    }

    /**
     * Read note by id
     *
     * @param id
     * @return
     */
    public NoteInfoRealmStruct getNoteById(int id) {


        return realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();
    }

    public void updateNotes(int id, String title, String description,  Date update_time, int tag,  int order) {
        realm.beginTransaction();

        NoteInfoRealmStruct noteInfoRealmStruct = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();
        noteInfoRealmStruct.setTitle(title);
        noteInfoRealmStruct.setDescription(description);
        noteInfoRealmStruct.setUpdateTime(update_time);
        noteInfoRealmStruct.setTag(tag);
        noteInfoRealmStruct.setOrder(order);
        realm.commitTransaction();
    }


    public void updateNotePhoneCallInfo(int id, Date start_time,
                      Date end_time, int call_type, String phone_number) {
        realm.beginTransaction();

        NoteInfoRealmStruct noteInfoRealmStruct = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();
        noteInfoRealmStruct.setId(id);
        noteInfoRealmStruct.setStartTime(start_time);
        noteInfoRealmStruct.setEndTime(end_time);
        noteInfoRealmStruct.setCallType(call_type);
        noteInfoRealmStruct.setPhoneNumber(phone_number);
        realm.commitTransaction();

    }

    /**
     * method search all notes
     */
    public ArrayList<NoteInfoStruct> findAllNotes(String searchString, int searchOption) {
        ArrayList<NoteInfoStruct> data = new ArrayList<>();
        RealmResults<NoteInfoRealmStruct> realmResult = null;
        if (searchString != null) {
            switch (searchOption) {
                case Constants.CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION:

                    realmResult = realm.where(NoteInfoRealmStruct.class).contains("title", searchString, Case.INSENSITIVE).or().contains("description", searchString, Case.INSENSITIVE).findAll();

                    break;
                case Constants.CONST_SEARCH_NOTE_CONTACTS:
                    realmResult = realm.where(NoteInfoRealmStruct.class).contains("phone_number", searchString, Case.INSENSITIVE).or().contains("description", searchString, Case.INSENSITIVE).findAll();

                    break;
            }

        } else {
            realmResult = realm.where(NoteInfoRealmStruct.class).findAll();
        }



        if (realmResult.size() > 0) {

            realmResult = filterQueryResults(realmResult);

            for (int i = 0; i < realmResult.size(); i++) {
                try {
                    int id = realmResult.get(i).getId();

                    String title = realmResult.get(i).getTitle();

                    String description = realmResult.get(i).getDescription();

                    boolean has_audio = realmResult.get(i).getHasAudio();

                    Date create_time_stamp = realmResult.get(i).getCreateTimeStamp();

                    Date update_time = realmResult.get(i).getUpdateTime();
                    String phoneNumber = realmResult.get(i).getPhoneNumber();
                    //if(phoneNumber==null)Log.e("GGG",phoneNumber);
                    data.add(i, new NoteInfoStruct(id, title, description, has_audio, update_time, create_time_stamp, realmResult.get(i).getStartTime(), realmResult.get(i).getEndTime(), realmResult.get(i).getCallType(), phoneNumber, 0, 0));
                } catch (Exception e) {
                    Log.e("GGG", e.getMessage());
                }
            }

        } else {
            showLog("Size : 0");
        }

        return data;
    }

    private  RealmResults<NoteInfoRealmStruct> filterQueryResults( RealmResults<NoteInfoRealmStruct> realmResult){

// filtering by text, audio or phone call

/**  TEXT AUDIO PHONE
 *  ____________________
 *          TFF
 *          TTF
 *          TFT
 *          FTT
 *          FFT
 *          FTF
 *          FFF
 *          TTT
 */

        // Just added for when all options are False-False-False then empty the query
        int fakeSkipQuery = 1000;

        if (Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().equalTo("has_audio", false).findAll();
        else if (Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().
                    beginGroup().
                    equalTo("has_audio", false).
                    equalTo("call_type", Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL).
                    endGroup().
                    or().
                    beginGroup().
                    equalTo("has_audio", true).
                    equalTo("call_type", Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL).
                    endGroup().
                    findAll();
        else if (Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().
                    beginGroup().
                    equalTo("has_audio", false).
                    equalTo("call_type", Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL).
                    endGroup().
                    or().
                    beginGroup().
                    equalTo("has_audio", true).
                    greaterThan("call_type", Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL).
                    endGroup().
                    findAll();
        else if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().
                    beginGroup().
                    equalTo("has_audio", true).
                    equalTo("call_type", Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL).
                    endGroup().
                    or().
                    beginGroup().
                    equalTo("has_audio", true).
                    greaterThan("call_type", Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL).
                    endGroup().
                    findAll();
        else if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().equalTo("has_audio", true).greaterThan("call_type", Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL).findAll();

        else if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().equalTo("has_audio", true).equalTo("call_type", Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL).findAll();
        else if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().equalTo("call_type", fakeSkipQuery).findAll();

        // Sort values
        if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SEARCH_SORT_OPTION,Constants.CONST_NOTELIST_ACCEDING))
            realmResult = realmResult.sort("id", Sort.ASCENDING);
        else
            realmResult = realmResult.sort("id", Sort.DESCENDING);
        return realmResult;
    }

    /**
     * method search all notes
     */
    public boolean lookupPhoneNumber(String searchString) {
        RealmQuery<NoteInfoRealmStruct> query = realm.where(NoteInfoRealmStruct.class)
                .equalTo("phone_number", searchString);

        return query.count() == 0 ? false : true;
    }

   /* *//**
     * method update article
     *
     * @param id
     * @param title
     * @param description
     *//*
    public void updateNote(int id, String title, String description, boolean has_audio)  {
        Date now = new Date();

        realm.beginTransaction();

        NoteInfoRealmStruct noteInfoRealmStruct = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();

        noteInfoRealmStruct.setTitle(title);
        noteInfoRealmStruct.setDescription(description);
        noteInfoRealmStruct.setHasAudio(has_audio);
        noteInfoRealmStruct.setUpdateTime(now);

        realm.commitTransaction();
        showLog("Updated : " + title);
    }*/


    /**
     * method delete articles by id
     *
     * @param id
     */
    public void deleteSingleNote(int id) {
        RealmResults<NoteInfoRealmStruct> notesToDelete = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findAll();
        realm.beginTransaction();
        notesToDelete.deleteFirstFromRealm();
        realm.commitTransaction();

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