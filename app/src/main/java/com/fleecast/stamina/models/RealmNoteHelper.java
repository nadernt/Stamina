package com.fleecast.stamina.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fleecast.stamina.backup.BackUpNotesStruct;
import com.fleecast.stamina.backup.NoteTypesAreInDatabase;
import com.fleecast.stamina.todo.TodoChildRealmStruct;
import com.fleecast.stamina.todo.TodoParentRealmStruct;
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
    public Context mContext;
    private boolean DEBUG = false;


    /**
     * constructor to create instances of realm
     *
     * @param mContext
     */
    public RealmNoteHelper(Context mContext) {
        realm = Realm.getDefaultInstance();
        this.mContext = mContext;
        DEBUG = false;
    }


    /**
     * add data
     *
     * @param title
     * @param description
     */
    public void addNote(int id, String title, String description, boolean has_audio,
                        Date update_time, Date create_time_stamp, Date start_time,
                        Date end_time, int call_type, String phone_number,
                        int note_type) {

        if (!isExist(id)) {
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
            noteInfoRealmStruct.setColor(0);
            noteInfoRealmStruct.setNoteType(note_type);
            realm.beginTransaction();
            realm.copyToRealm(noteInfoRealmStruct);
            realm.commitTransaction();
        } else {
            updateNotes(id, title, description, update_time, note_type);
        }

    }


    /**
     * Check if note by id exists.
     *
     * @param id
     * @return
     */
    public boolean isExist(int id) {

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

    public void updateNotes(int id, String title, String description, Date update_time, int note_type) {
        realm.beginTransaction();
        NoteInfoRealmStruct noteInfoRealmStruct = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();
        noteInfoRealmStruct.setTitle(title);
        noteInfoRealmStruct.setDescription(description);
        noteInfoRealmStruct.setUpdateTime(update_time);
        noteInfoRealmStruct.setNoteType(note_type);
        realm.commitTransaction();
    }


    public void updateNotePhoneCallInfo(int id, Date start_time,
                                        Date end_time, int call_type, String phone_number) {
        realm.beginTransaction();

        NoteInfoRealmStruct noteInfoRealmStruct = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();
//        noteInfoRealmStruct.setId(id);
        noteInfoRealmStruct.setStartTime(start_time);
        noteInfoRealmStruct.setEndTime(end_time);
        noteInfoRealmStruct.setCallType(call_type);
        noteInfoRealmStruct.setPhoneNumber(phone_number);
        realm.commitTransaction();

    }

    public void updateColor(int id, int color) {
        realm.beginTransaction();
        NoteInfoRealmStruct noteInfoRealmStruct = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();
        noteInfoRealmStruct.setColor(color);
        realm.commitTransaction();
    }

    public void updateGroupTag(int id, String group) {
        realm.beginTransaction();
        NoteInfoRealmStruct noteInfoRealmStruct = realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst();
        noteInfoRealmStruct.setGroup(group);
        realm.commitTransaction();
    }

    public int getNoteColor(int id){
        return realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst().getColor();
    }

    public String getNoteGroupTag(int id){
        return realm.where(NoteInfoRealmStruct.class).equalTo("id", id).findFirst().getGroup();
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

                    data.add(i, new NoteInfoStruct(id, title, description, has_audio, update_time,
                            create_time_stamp, realmResult.get(i).getStartTime(),
                            realmResult.get(i).getEndTime(), realmResult.get(i).getCallType(),
                            phoneNumber, realmResult.get(i).getColor(), realmResult.get(i).getOrder(),realmResult.get(i).getNoteType(),
                            realmResult.get(i).getExtras(),realmResult.get(i).getGroup(),
                            realmResult.get(i).isDel()));
                } catch (Exception e) {
                    Log.e("Err: ", e.getMessage());
                }
            }

        } else {
            showLog("Size : 0");
        }

        return data;
    }

    private RealmResults<NoteInfoRealmStruct> filterQueryResults(RealmResults<NoteInfoRealmStruct> realmResult) {

// Filtering by text, audio or phone call

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
                    equalTo("note_type", Constants.CONST_NOTETYPE_TEXT).
                    endGroup().
                    or().
                    beginGroup().
                    equalTo("note_type", Constants.CONST_NOTETYPE_AUDIO).
                    endGroup().
                    findAll();
        else if (Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().
                    beginGroup().
                    equalTo("note_type", Constants.CONST_NOTETYPE_TEXT).
                    endGroup().
                    or().
                    beginGroup().
                    equalTo("note_type", Constants.CONST_NOTETYPE_PHONECALL).
                    endGroup().
                    findAll();
        else if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().
                    beginGroup().
                    equalTo("note_type", Constants.CONST_NOTETYPE_AUDIO).
                    endGroup().
                    or().
                    beginGroup().
                    equalTo("note_type", Constants.CONST_NOTETYPE_PHONECALL).
                    endGroup().
                    findAll();
        else if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().equalTo("note_type", Constants.CONST_NOTETYPE_PHONECALL).findAll();
        else if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().equalTo("note_type", Constants.CONST_NOTETYPE_AUDIO).findAll();
        else if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_TEXT_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_AUDIO_NOTE, true) && !Prefs.getBoolean(Constants.PREF_NOTELIST_SHOW_PHONE_RECORD, true))
            realmResult = realmResult.where().equalTo("note_type", fakeSkipQuery).findAll();

        // Sort values
        if (!Prefs.getBoolean(Constants.PREF_NOTELIST_SEARCH_SORT_OPTION, Constants.CONST_NOTELIST_ACCEDING))
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
        if (DEBUG)
            Log.d(TAG, s);

    }

    /**
     * make Toast Information
     */
    private void showToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
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

    public ArrayList<BackUpNotesStruct> backupNotes(boolean textNotes, boolean audioNotes, boolean phoneCalls, boolean toDoNote) throws Exception {

        ArrayList<BackUpNotesStruct> backupNotes = new ArrayList<>();

        RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);
        RealmToDoHelper realmToDoHelper = new RealmToDoHelper(mContext);


        RealmResults<NoteInfoRealmStruct> realmResult = realm.where(NoteInfoRealmStruct.class).findAll();
        RealmResults<TodoParentRealmStruct> realmResultTodo = realm.where(TodoParentRealmStruct.class).findAll();

        if ((realmResult.size() == 0) && (realmResultTodo.size()==0) ) {
            throw new NegativeArraySizeException();
        } else {

            for (int i = 0; i < realmResult.size(); i++) {
                int noteType = realmResult.get(i).getNoteType();
                if (noteType == Constants.CONST_NOTETYPE_TEXT && textNotes) {
                    backupNotes.add(new BackUpNotesStruct(realmResult.get(i).getId(), realmResult.get(i).getTitle(),
                            realmResult.get(i).getDescription(), realmResult.get(i).getHasAudio(),
                            realmResult.get(i).getUpdateTime(), realmResult.get(i).getCreateTimeStamp(),
                            realmResult.get(i).getStartTime(), realmResult.get(i).getEndTime(),
                            realmResult.get(i).getCallType(), realmResult.get(i).getPhoneNumber(),
                            realmResult.get(i).getColor(), 0, Constants.CONST_NOTETYPE_TEXT, Constants.CONST_NULL_MINUS, null, null, null, false, Constants.CONST_NULL_MINUS));
                }

                if (noteType == Constants.CONST_NOTETYPE_AUDIO && audioNotes) {

                    RealmResults<AudioNoteInfoRealmStruct> audioNoteInfoRealmStructs = realmAudioNoteHelper.findAllAudioNotesByParentId(realmResult.get(i).getId());

                    if (audioNoteInfoRealmStructs.size() > 0) {

                        System.out.println("HGHGHGHGHGHGHGH");

                        for (int j = 0; j < audioNoteInfoRealmStructs.size(); j++) {

                            backupNotes.add(new BackUpNotesStruct(realmResult.get(i).getId(), realmResult.get(i).getTitle(),
                                    realmResult.get(i).getDescription(), realmResult.get(i).getHasAudio(),
                                    realmResult.get(i).getUpdateTime(), realmResult.get(i).getCreateTimeStamp(),
                                    realmResult.get(i).getStartTime(), realmResult.get(i).getEndTime(),
                                    realmResult.get(i).getCallType(), realmResult.get(i).getPhoneNumber(),
                                    realmResult.get(i).getColor(), 0, Constants.CONST_NOTETYPE_AUDIO, audioNoteInfoRealmStructs.get(j).getId(),
                                    audioNoteInfoRealmStructs.get(j).getTitle(), audioNoteInfoRealmStructs.get(j).getDescription(),
                                    new Date(audioNoteInfoRealmStructs.get(j).getId()), false, Constants.CONST_NULL_ZERO));
                        }

                    }
                    else {
                        backupNotes.add(new BackUpNotesStruct(realmResult.get(i).getId(), realmResult.get(i).getTitle(),
                                realmResult.get(i).getDescription(), realmResult.get(i).getHasAudio(),
                                realmResult.get(i).getUpdateTime(), realmResult.get(i).getCreateTimeStamp(),
                                realmResult.get(i).getStartTime(), realmResult.get(i).getEndTime(),
                                realmResult.get(i).getCallType(), realmResult.get(i).getPhoneNumber(),
                                realmResult.get(i).getColor(), 0, Constants.CONST_NOTETYPE_AUDIO, Constants.CONST_NULL_MINUS,
                                null, null,
                                null, false, Constants.CONST_NULL_ZERO));
                    }
                }

                if (noteType == Constants.CONST_NOTETYPE_PHONECALL && phoneCalls) {

                    backupNotes.add(new BackUpNotesStruct(realmResult.get(i).getId(), realmResult.get(i).getTitle(),
                            realmResult.get(i).getDescription(), realmResult.get(i).getHasAudio(),
                            realmResult.get(i).getUpdateTime(), realmResult.get(i).getCreateTimeStamp(),
                            realmResult.get(i).getStartTime(), realmResult.get(i).getEndTime(),
                            realmResult.get(i).getCallType(), realmResult.get(i).getPhoneNumber(),
                            realmResult.get(i).getColor(), 0, Constants.CONST_NOTETYPE_PHONECALL, Constants.CONST_NULL_MINUS, null, null, null, false, Constants.CONST_NULL_MINUS));
                }

            }

            if (toDoNote) {

                for (int i = 0; i < realmResultTodo.size(); i++) {

                    ArrayList<TodoChildRealmStruct> todoChildRealmStructs = realmToDoHelper.getAllChildTodos(realmResultTodo.get(i).getId());

                    //If todo is just title and doesn't have any child.
                    if (todoChildRealmStructs.size() > 0) {

                        for (int j = 0; j < todoChildRealmStructs.size(); j++) {

                            backupNotes.add(new BackUpNotesStruct(realmResultTodo.get(i).getId(), realmResultTodo.get(i).getTitle(),
                                    null, false,
                                    realmResultTodo.get(i).getCreateTimeStamp(), realmResultTodo.get(i).getCreateTimeStamp(),
                                    realmResultTodo.get(i).getCreateTimeStamp(), realmResultTodo.get(i).getCreateTimeStamp(), Constants.CONST_NULL_MINUS,
                                    null, Constants.CONST_NULL_ZERO, 0, Constants.CONST_NOTETYPE_TODO, todoChildRealmStructs.get(j).getId(),
                                    todoChildRealmStructs.get(j).getTitle(), null, todoChildRealmStructs.get(j).getCreateTimeStamp(), todoChildRealmStructs.get(j).getHasDone(), todoChildRealmStructs.get(j).getOrder()));
                        }

                    }
                }
            }

        }

        return backupNotes;

    }

    public void applyBackupToDB(ArrayList<BackUpNotesStruct> backUpNotesStructs, boolean textNotes, boolean audioNotes, boolean phoneCalls, boolean toDoNote, boolean overwrite_localdb, boolean delete_localdb) {

        ArrayList<AudioNoteInfoRealmStruct> audioNoteChildRealmStruct = new ArrayList<>();
        ArrayList<TodoChildRealmStruct> todoChildRealmStruct = new ArrayList<>();

        ArrayList<NoteInfoRealmStruct> noteInfoTextAudioPhonecalls = new ArrayList<>();
        ArrayList<TodoParentRealmStruct> noteInfoParentTodo = new ArrayList<>();

        // Remove the fields we shouldn't overwrite (if user requested).
        if (!overwrite_localdb && !delete_localdb) {
            for (int j = 0; j < backUpNotesStructs.size(); j++) {
                if (isExist(backUpNotesStructs.get(j).getId())) {
                    backUpNotesStructs.remove(j);
                    j = 0;
                }
            }
        }

        ArrayList<Integer> weDidProcessOfThisBefore = new ArrayList<>();
        int index = 0;
        int index_audio_child = 0;
        int index_parent_todo = 0;
        int index_todo_child = 0;


        for (int i = 0; i < backUpNotesStructs.size(); i++) {

            int lookInFinished = 0;

            boolean weDid = false;

            if (weDidProcessOfThisBefore.size() > 0) {
                while (true) {

                    if (weDidProcessOfThisBefore.size() == lookInFinished) {
                        break;
                    }

                    if (weDidProcessOfThisBefore.get(lookInFinished) == backUpNotesStructs.get(i).getId() || weDidProcessOfThisBefore.size() == lookInFinished) {
                        weDid = true;
                        break;
                    }

                    lookInFinished++;

                }
            }

            if (!weDid) {

                weDidProcessOfThisBefore.add(backUpNotesStructs.get(i).getId());

                int noteType = backUpNotesStructs.get(i).getNoteType();

                if ((noteType == Constants.CONST_NOTETYPE_TEXT && textNotes) || (noteType == Constants.CONST_NOTETYPE_PHONECALL && phoneCalls)) {

                    noteInfoTextAudioPhonecalls.add(new NoteInfoRealmStruct());

                    noteInfoTextAudioPhonecalls.get(index).setId(backUpNotesStructs.get(i).getId());
                    noteInfoTextAudioPhonecalls.get(index).setTitle(backUpNotesStructs.get(i).getTitle());
                    noteInfoTextAudioPhonecalls.get(index).setDescription(backUpNotesStructs.get(i).getDescription());
                    noteInfoTextAudioPhonecalls.get(index).setHasAudio(backUpNotesStructs.get(i).getHasAudio());

                    noteInfoTextAudioPhonecalls.get(index).setUpdateTime(backUpNotesStructs.get(i).getUpdate_time());
                    noteInfoTextAudioPhonecalls.get(index).setCreateTimeStamp(backUpNotesStructs.get(i).getCreateTimeStamp());
                    noteInfoTextAudioPhonecalls.get(index).setStartTime(backUpNotesStructs.get(i).getStartTime());
                    noteInfoTextAudioPhonecalls.get(index).setEndTime(backUpNotesStructs.get(i).getEndTime());
                    noteInfoTextAudioPhonecalls.get(index).setCallType(backUpNotesStructs.get(i).getCallType());
                    noteInfoTextAudioPhonecalls.get(index).setPhoneNumber(backUpNotesStructs.get(i).getPhoneNumber());
                    noteInfoTextAudioPhonecalls.get(index).setColor(backUpNotesStructs.get(i).getTag());
                    noteInfoTextAudioPhonecalls.get(index).setNoteType(backUpNotesStructs.get(i).getNoteType());

                    index++;

                } else if (noteType == Constants.CONST_NOTETYPE_AUDIO && audioNotes) {

                    int parent_id = backUpNotesStructs.get(i).getId();

                    noteInfoTextAudioPhonecalls.add(new NoteInfoRealmStruct());

                    noteInfoTextAudioPhonecalls.get(index).setId(backUpNotesStructs.get(i).getId());
                    noteInfoTextAudioPhonecalls.get(index).setTitle(backUpNotesStructs.get(i).getTitle());
                    noteInfoTextAudioPhonecalls.get(index).setDescription(backUpNotesStructs.get(i).getDescription());
                    noteInfoTextAudioPhonecalls.get(index).setHasAudio(backUpNotesStructs.get(i).getHasAudio());

                    noteInfoTextAudioPhonecalls.get(index).setUpdateTime(backUpNotesStructs.get(i).getUpdate_time());
                    noteInfoTextAudioPhonecalls.get(index).setCreateTimeStamp(backUpNotesStructs.get(i).getCreateTimeStamp());
                    noteInfoTextAudioPhonecalls.get(index).setStartTime(backUpNotesStructs.get(i).getStartTime());
                    noteInfoTextAudioPhonecalls.get(index).setEndTime(backUpNotesStructs.get(i).getEndTime());
                    noteInfoTextAudioPhonecalls.get(index).setCallType(backUpNotesStructs.get(i).getCallType());
                    noteInfoTextAudioPhonecalls.get(index).setPhoneNumber(backUpNotesStructs.get(i).getPhoneNumber());
                    noteInfoTextAudioPhonecalls.get(index).setColor(backUpNotesStructs.get(i).getTag());
                    noteInfoTextAudioPhonecalls.get(index).setNoteType(backUpNotesStructs.get(i).getNoteType());

                    index++;

                    if(backUpNotesStructs.get(i).getId_child()!= Constants.CONST_NULL_MINUS) { // If the Audio folder has db child entry ecxceptit is some Audio notes folder with some audio files

                        for (int k = 0; k < backUpNotesStructs.size(); k++) {
                            if (backUpNotesStructs.get(k).getId() == parent_id) {
                                audioNoteChildRealmStruct.add(new AudioNoteInfoRealmStruct());
                                audioNoteChildRealmStruct.get(index_audio_child).setId(backUpNotesStructs.get(k).getId_child());
                                audioNoteChildRealmStruct.get(index_audio_child).setParentDbId(parent_id);
                                audioNoteChildRealmStruct.get(index_audio_child).setDescription(backUpNotesStructs.get(k).getChildDescription());
                                audioNoteChildRealmStruct.get(index_audio_child).setTitle(backUpNotesStructs.get(k).getChildTitle());
                                audioNoteChildRealmStruct.get(index_audio_child).setTag(0);
                                index_audio_child++;
                            }
                        }
                    }

                } else if (noteType == Constants.CONST_NOTETYPE_TODO && toDoNote) {

                    int parent_id = backUpNotesStructs.get(i).getId();

                    noteInfoParentTodo.add(new TodoParentRealmStruct());

                    noteInfoParentTodo.get(index_parent_todo).setId(backUpNotesStructs.get(i).getId());
                    noteInfoParentTodo.get(index_parent_todo).setTitle(backUpNotesStructs.get(i).getTitle());
                    noteInfoParentTodo.get(index_parent_todo).setHasDone(backUpNotesStructs.get(i).getTodoDone());
                    noteInfoParentTodo.get(index_parent_todo).setCreateTimeStamp(backUpNotesStructs.get(i).getCreateTimeStamp());

                    if (backUpNotesStructs.get(i).getChildTitle() != null) { // It has a child

                        for (int k = 0; k < backUpNotesStructs.size(); k++) {

                            if (backUpNotesStructs.get(k).getId() == parent_id) {

                                todoChildRealmStruct.add(new TodoChildRealmStruct());
                                todoChildRealmStruct.get(index_todo_child).setId(backUpNotesStructs.get(k).getId_child());
                                todoChildRealmStruct.get(index_todo_child).setParentId(parent_id);
                                todoChildRealmStruct.get(index_todo_child).setTitle(backUpNotesStructs.get(k).getChildTitle());
                                //Date childCreateDate = new Date((long) backUpNotesStructs.get(k).getId_child() * 1000);
                                todoChildRealmStruct.get(index_todo_child).setCreateTimeStamp(backUpNotesStructs.get(k).getChild_create_time_stamp());
                                todoChildRealmStruct.get(index_todo_child).setOrder(0);
                                todoChildRealmStruct.get(index_todo_child).setHasDone(backUpNotesStructs.get(k).getTodoDone());

                                index_todo_child++;
                            }
                        }
                    }

                    index_parent_todo++;

                }

            }
        }

        realm.beginTransaction();

        if (delete_localdb)
            realm.deleteAll();

        if (noteInfoTextAudioPhonecalls.size() > 0)
            realm.copyToRealmOrUpdate(noteInfoTextAudioPhonecalls);

        if (toDoNote && (todoChildRealmStruct.size() > 0)) {
            realm.copyToRealmOrUpdate(noteInfoParentTodo);
            realm.copyToRealmOrUpdate(todoChildRealmStruct);
        }

        if (audioNotes && (audioNoteChildRealmStruct.size() > 0)) {
            realm.copyToRealmOrUpdate(audioNoteChildRealmStruct);
        }

        realm.commitTransaction();

    }
    public RealmResults<NoteInfoRealmStruct> getAllNotes()

    {
        return realm.where(NoteInfoRealmStruct.class).findAll();
    }

    public NoteTypesAreInDatabase analyseDatabaseForTypeOfNotesWeHave() {

        RealmResults<NoteInfoRealmStruct> realmResultsNotes = realm.where(NoteInfoRealmStruct.class).findAll();
        RealmResults<TodoParentRealmStruct> realmResultTodo = realm.where(TodoParentRealmStruct.class).findAll();

        boolean weHaveTextNotes=false;
        boolean weHaveAudioNotes=false;
        boolean weHaveTodo=false;
        boolean weHavePhoneCalls=false;

        if(realmResultsNotes.size()>Constants.CONST_NULL_ZERO )
        {
            for (int i=0; i< realmResultsNotes.size();i++) {

                int noteType = realmResultsNotes.get(i).getNoteType();

                if (noteType == Constants.CONST_NOTETYPE_TEXT)
                    weHaveTextNotes = true;
                if (noteType == Constants.CONST_NOTETYPE_PHONECALL)
                    weHavePhoneCalls = true;
                if (noteType == Constants.CONST_NOTETYPE_AUDIO)
                    weHaveAudioNotes = true;
            }
        }

        if (realmResultTodo.size() > Constants.CONST_NULL_ZERO){
            weHaveTodo=true;
        }

        return new NoteTypesAreInDatabase(weHaveTextNotes,weHaveAudioNotes,weHaveTodo,weHavePhoneCalls);
    }

}