package com.fleecast.stamina.backup;

import android.content.Context;

import com.fleecast.stamina.models.AudioNoteInfoRealmStruct;
import com.fleecast.stamina.models.NoteInfoRealmStruct;
import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.models.RealmToDoHelper;
import com.fleecast.stamina.todo.TodoChildRealmStruct;
import com.fleecast.stamina.todo.TodoParentRealmStruct;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nnt on 27/11/16.
 */

public class Report {

    private Realm realm;
    public Context mContext;
    private boolean DEBUG = false;

    /**
     * constructor to create instances of realm
     *
     * @param mContext
     */
    public Report(Context mContext) {
        realm = Realm.getDefaultInstance();
        this.mContext = mContext;
        DEBUG = false;
    }

    private class AudioData {

        private int id;
        private int parent_db_id;
        private String title;
        private int tag;
        private String description;

        public AudioData(int id, int parent_db_id, String title, String description, int tag) {
            this.id = id;
            this.parent_db_id = parent_db_id;
            this.title = title;
            this.tag = tag;
            this.description = description;
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getParentDbId() {
            return parent_db_id;
        }

        public void setParentDbId(int parent_db_id) {
            this.parent_db_id = parent_db_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

    public String getReportFromDBSimpleHtml(boolean textNotes, boolean audioNotes, boolean phoneCalls, boolean toDoNote) {


        String strOutPutReport = "";
        String strTextNotes = "";
        String strAudioNotes = "";
        String strPhoneCalls = "";
        String strTodo = "";

        boolean isTextNoteHit = false;
        boolean isPhoneCallHit = false;
        boolean isAudioNoteHit = false;
        boolean isTodoHit = false;

        int intTextNotes = 0;
        int intAudioNotes = 0;
        int intPhoneCalls = 0;
        int intTodo = 0;

        RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);
        RealmToDoHelper realmToDoHelper = new RealmToDoHelper(mContext);

        RealmResults<NoteInfoRealmStruct> realmResult = realm.where(NoteInfoRealmStruct.class).findAll();

        if (realmResult.size() == 0) {
            throw new NegativeArraySizeException();
        } else {

            for (int i = 0; i < realmResult.size(); i++) {
                int noteType = realmResult.get(i).getNoteType();

                if ((noteType == Constants.CONST_NOTETYPE_TEXT) && (textNotes)) {
                    isTextNoteHit = true;
                        /*"<div class='note_contents'><span class='note_title'>" + realmResult.get(i).getTitle() + "</span><span class='note_body'>"+ realmResult.get(i).getDescription() +"</span></div>" +*/
                    strTextNotes +=
                            "<div class='item'>" +
                                    "<div class='note_title'>" + realmResult.get(i).getTitle() + "</div>" +
                                    "<div class='note_contents'>" + realmResult.get(i).getDescription() + "</div>" +
                                    "<div class='note_timestamp'>" + Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "</div>" +
                                    "</div>";

                }

                if ((noteType == Constants.CONST_NOTETYPE_AUDIO) && audioNotes) {

                    RealmResults<AudioNoteInfoRealmStruct> audioNoteInfoRealmStructs = realmAudioNoteHelper.findAllAudioNotesByParentId(realmResult.get(i).getId());

                    strAudioNotes +=
                            "<div class='item'>" +
                                    "<div class='note_title'>" + realmResult.get(i).getTitle() + "</div>" +
                                    "<div class='note_contents'>" + realmResult.get(i).getDescription() + "</div>";

                    // Each record doesn't have entry in Stamina. They just get entry if user wants to add a note to them. Here I scan the folder for files without database entry.
                    String pathToAudioFiles = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(realmResult.get(i).getId()));

                    ArrayList<AudioData> audioDatas = new ArrayList<>();

                    ArrayList<Integer> filesIds = listAudioFilesInADirectoryByParentId(pathToAudioFiles);

                    if (filesIds.size() > 0) {
                        for (int a = 0; a < audioNoteInfoRealmStructs.size(); a++) {
                            int intId = audioNoteInfoRealmStructs.get(a).getId();
                            for (int b = 0; b < filesIds.size(); b++) {
                                if (filesIds.get(b) == intId) {
                                    filesIds.remove(b);
                                    audioDatas.add(new AudioData(audioNoteInfoRealmStructs.get(a).getId(),
                                            audioNoteInfoRealmStructs.get(a).getParentDbId(),
                                            audioNoteInfoRealmStructs.get(a).getTitle(),
                                            audioNoteInfoRealmStructs.get(a).getDescription(),
                                            audioNoteInfoRealmStructs.get(a).getTag()));
                                    break;
                                }
                            }
                        }


                        for (int b = 0; b < filesIds.size(); b++) {
                            audioDatas.add(new AudioData(filesIds.get(b),
                                    realmResult.get(i).getId(),
                                    "no title",
                                    "no description",
                                    Constants.CONST_NULL_ZERO));
                        }


                    }

                    isAudioNoteHit = true;

                    strAudioNotes += "<ul>";

                    for (int j = 0; j < audioDatas.size(); j++) {

                        strAudioNotes += "<li>" +
                                "<span class='sub_note_title'>" + audioDatas.get(j).getTitle() + "</span><br><span class='sub_note_body'>" + audioDatas.get(j).getDescription() + "</span>" +
                                "<audio controls>" +
                                "<source src='" + audioNoteFileName(audioDatas.get(j)) + "' type='audio/aac'>" +
                                "Browser does not support the audio element." +
                                "</audio>" +
                                "</li>";
                    }

                    strAudioNotes += "</ul>" +
                            "<div class='note_timestamp'>" + Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "</div>" +
                            "</div>";


                }

                if ((noteType == Constants.CONST_NOTETYPE_PHONECALL) && phoneCalls) {

                    isPhoneCallHit = true;

                    strPhoneCalls +=
                            "<div class='item'>" +
                                    "<div class='note_title'>" + realmResult.get(i).getTitle() + " (" + realmResult.get(i).getPhoneNumber() +  ")</div>" +
                                    "<div class='note_contents'>" + realmResult.get(i).getDescription() + "</div>" +
                                    "<audio controls>" +
                                    "<source src='phonecalls/" + realmResult.get(i).getId() + ".aac' type='audio/aac'>" +
                                    "Browser does not support the audio element." +
                                    "</audio>" +
                                    "<div class='note_timestamp'>" + Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "</div>" +
                                    "</div>";
                }

            }

            if (toDoNote) {

                RealmResults<TodoParentRealmStruct> realmResultTodo = realm.where(TodoParentRealmStruct.class).findAll();


                for (int i = 0; i < realmResultTodo.size(); i++) {
                    isTodoHit = true;
                    strTodo +=
                            "<div class='item'>" +
                                    "<div class='note_title'>" + realmResultTodo.get(i).getTitle() + "</div>";

                    ArrayList<TodoChildRealmStruct> todoChildRealmStructs = realmToDoHelper.getAllChildTodos(realmResultTodo.get(i).getId());

                    //If todo is just title and doesn't have any child.
                    if (todoChildRealmStructs.size() > 0) {

                        strTodo += "<ul>";

                        for (int j = 0; j < todoChildRealmStructs.size(); j++) {
                            strTodo += toDoBinder(todoChildRealmStructs.get(j));
                        }

                        strTodo += "</ul>";

                    }

                    strTodo +=
                            "<div class='note_timestamp'>" + Utility.unixTimeToReadable(realmResultTodo.get(i).getCreateTimeStamp().getTime()) + "</div>" +
                                    "</div>";

                }
            }

        }

        strOutPutReport = loadTemplate();

        String navBar = "";
        String bodyContents = "";

        if (textNotes && isTextNoteHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#text notes'>Text Notes</a></li>";
            bodyContents += "<h6 class='docs-header'>TEXT NOTES</h6>";
            bodyContents += strTextNotes;
        }
        if (audioNotes && isAudioNoteHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#audio notes'>Audio Notes</a></li>";
            bodyContents += "<h6 class='docs-header'>AUDIO NOTES</h6>";
            bodyContents += strAudioNotes;
        }
        if (phoneCalls && isPhoneCallHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#phone calls'>Phone Calls</a></li>";
            bodyContents += "<h6 class='docs-header'>PHONE CALLS</h6>";
            bodyContents += strPhoneCalls;
        }
        if (toDoNote && isTodoHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#todo'>Todo</a></li>";
            bodyContents += "<h6 class='docs-header'>Todo</h6>";
            bodyContents += strTodo;
        }

        strOutPutReport = strOutPutReport.replaceFirst("\\B#NAVBAR#\\B", navBar);
        strOutPutReport = strOutPutReport.replaceFirst("\\B#CONTENTS#\\B", bodyContents);

        return strOutPutReport;
    }

    private ArrayList<Integer> listAudioFilesInADirectoryByParentId(String pathToAudioFiles) {

        File f = new File(pathToAudioFiles);
        File[] fl = f.listFiles();
        ArrayList<Integer> returnArray = new ArrayList<>();
        for (int i = 0; i < fl.length; i++) {

            if (fl[i].isFile()) {
                returnArray.add(Utility.getDbIdFromFileName(String.valueOf(fl[i])));
            }
        }

        return returnArray;
    }

    private String loadTemplate() {
        try {
            InputStream is = mContext.getAssets().open(Constants.CONST_TEMPLATE_DIRECTORY + "/index.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String audioNoteFileName(AudioData audioData) {
        return "audio/" + audioData.getParentDbId() + "_" + audioData.getId();
    }

    private String toDoBinder(TodoChildRealmStruct todoChildRealmStruct) {
        if (todoChildRealmStruct.getHasDone())
            return "<li class='todo_done'>&#10003; " + todoChildRealmStruct.getTitle() + "</li>";
        else
            return "<li> " + todoChildRealmStruct.getTitle() + "</li>";

    }


}
