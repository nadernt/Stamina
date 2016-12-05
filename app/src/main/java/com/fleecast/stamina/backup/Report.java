package com.fleecast.stamina.backup;

import android.content.Context;

import com.fleecast.stamina.backup.csv.CsvEscape;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by nnt on 27/11/16.
 */

public class Report {

    private Realm realm;
    public Context mContext;
    private boolean DEBUG = false;
    private ArrayList<String> CSVNotesStrings = new ArrayList<>();

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

    private String getTypeAsString(int type) {

        switch (type) {
            case 0:
                return "Text";
            case 1:
                return "Audio";
            case 2:
                return "Phone";
            case 3:
                return "Todo";
        }
        return null;
    }

    private void addCSVline(int typeNumber, String titleParent, String descriptionParent,
                            long timeOfCreateParent, String childText, String phoneNumber, long startTime, long endTime, String duration, String incomingOutcoming, int totalSubNotes) {

        String strStartTime;
        String strEndTime;
        String strtimeOfCreateParent;

        if (timeOfCreateParent == Constants.CONST_NULL_ZERO)
            strtimeOfCreateParent = "";
        else
            strtimeOfCreateParent = CsvEscape.escapeCsv(Utility.unixTimeToReadable(startTime / 1000L));

        if (startTime == Constants.CONST_NULL_ZERO)
            strStartTime = "";
        else
            strStartTime = CsvEscape.escapeCsv(Utility.unixTimeToReadable(startTime / 1000L));

        if (endTime == Constants.CONST_NULL_ZERO)
            strEndTime = "";
        else
            strEndTime = CsvEscape.escapeCsv(Utility.unixTimeToReadable(startTime / 1000L));


        CSVNotesStrings.add(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d\n",
                typeNumber + 1, getTypeAsString(typeNumber), CsvEscape.escapeCsv(titleParent),
                CsvEscape.escapeCsv(descriptionParent), strtimeOfCreateParent, CsvEscape.escapeCsv(childText),
                CsvEscape.escapeCsv(phoneNumber), strStartTime, strEndTime,
                duration, incomingOutcoming, totalSubNotes));
    }

    public ArrayList<String> getReportCSV(boolean textNotes, boolean audioNotes, boolean phoneCalls, boolean toDoNote) {


        String strOutPutReport = "";


        RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);
        RealmToDoHelper realmToDoHelper = new RealmToDoHelper(mContext);

        RealmResults<NoteInfoRealmStruct> realmResult = realm.where(NoteInfoRealmStruct.class).findAll();
        realmResult = realmResult.sort("id", Sort.DESCENDING);

        String strHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", "Type Number",
                "Type in Text", "Title Parent", "Description Parent", "Time of Create Parent",
                "Sub Notes", "Phone Number",
                "Start Time", "End Time", "Duration", "Incoming", "Total Sub Notes");

        CSVNotesStrings.add(strHeader);

        if (realmResult.size() == 0) {
            throw new NegativeArraySizeException();
        } else {

            for (int i = 0; i < realmResult.size(); i++) {
                int noteType = realmResult.get(i).getNoteType();

                if ((noteType == Constants.CONST_NOTETYPE_TEXT) && textNotes) {
                    addCSVline(realmResult.get(i).getNoteType(),
                            realmResult.get(i).getTitle(), realmResult.get(i).getDescription(), realmResult.get(i).getCreateTimeStamp().getTime(), "",
                            "", Constants.CONST_NULL_ZERO, Constants.CONST_NULL_ZERO, "", "", Constants.CONST_NULL_ZERO);
                }

                if ((noteType == Constants.CONST_NOTETYPE_AUDIO) && audioNotes) {

                    RealmResults<AudioNoteInfoRealmStruct> audioNoteInfoRealmStructs = realmAudioNoteHelper.findAllAudioNotesByParentId(realmResult.get(i).getId());


                    // Each record doesn't have entry in Stamina. They just get entry if user wants to add a note to them. Here I scan the folder for files without database entry.
                    String pathToAudioFiles = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(realmResult.get(i).getId()));

                    ArrayList<AudioData> audioDatas = new ArrayList<>();

                    ArrayList<Integer> filesIds = listAudioFilesInDirectoryByParentId(pathToAudioFiles);

                    if (filesIds.size() > 0) {
                        for (int a = 0; a < audioNoteInfoRealmStructs.size(); a++) {
                            int intId = audioNoteInfoRealmStructs.get(a).getId();
                            for (int b = 0; b < filesIds.size(); b++) {
                                if (filesIds.get(b) == intId) {
                                    filesIds.remove(b);
                                    audioDatas.add(new AudioData(audioNoteInfoRealmStructs.get(a).getId(),
                                            audioNoteInfoRealmStructs.get(a).getParentDbId(),
                                            audioNoteInfoRealmStructs.get(a).getTitle(),
                                            Utility.convertNewLineCharToBrHtml(audioNoteInfoRealmStructs.get(a).getDescription()),
                                            audioNoteInfoRealmStructs.get(a).getTag()));
                                    break;
                                }
                            }
                        }


                        for (int b = 0; b < filesIds.size(); b++) {
                            audioDatas.add(new AudioData(filesIds.get(b),
                                    realmResult.get(i).getId(),
                                    "No title",
                                    "No description",
                                    Constants.CONST_NULL_ZERO));
                        }


                    }


                    String strSubAudioNotes = "";
                    int j = 0;
                    for (; j < audioDatas.size(); j++) {

                        strSubAudioNotes += "-" + audioDatas.get(j).getTitle() + ":\n" + audioDatas.get(j).getDescription() + "\n";

                    }

                    //strAudioNotes += "</ul>\n</td>\n<td>" + Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "<td>\n</tr>\n";
                    addCSVline(realmResult.get(i).getNoteType(),
                            realmResult.get(i).getTitle(), realmResult.get(i).getDescription(), realmResult.get(i).getCreateTimeStamp().getTime(), strSubAudioNotes,
                            "", Constants.CONST_NULL_ZERO, Constants.CONST_NULL_ZERO, "", "", j);
                }

                if ((noteType == Constants.CONST_NOTETYPE_PHONECALL) && phoneCalls) {

                    String incomOutCome = realmResult.get(i).getCallType() == Constants.RECORDS_IS_OUTGOING ? "Outgoing" : "Incoming";
                    if (realmResult.get(i).getStartTime() != null && realmResult.get(i).getEndTime() != null && realmResult.get(i).getCreateTimeStamp() != null) {
                        addCSVline(realmResult.get(i).getNoteType(),
                                realmResult.get(i).getTitle(), realmResult.get(i).getDescription(), realmResult.get(i).getCreateTimeStamp().getTime(), "",
                                realmResult.get(i).getPhoneNumber(), realmResult.get(i).getStartTime().getTime(), realmResult.get(i).getEndTime().getTime(),
                                Utility.calculateCallDuration(realmResult.get(i).getStartTime(), realmResult.get(i).getEndTime()), incomOutCome, Constants.CONST_NULL_ZERO);
                    }
                }

            }

            if (toDoNote) {

                RealmResults<TodoParentRealmStruct> realmResultTodo = realm.where(TodoParentRealmStruct.class).findAll();

                realmResultTodo = realmResultTodo.sort("id", Sort.DESCENDING);
                String strSubTodoNotes = "";

                for (int i = 0; i < realmResultTodo.size(); i++) {

                    ArrayList<TodoChildRealmStruct> todoChildRealmStructs = realmToDoHelper.getAllChildTodos(realmResultTodo.get(i).getId());
                    int j = 0;
                    //If todo is just title and doesn't have any child.
                    if (todoChildRealmStructs.size() > 0) {

                        for (; j < todoChildRealmStructs.size(); j++) {
                            strSubTodoNotes += "-" + todoChildRealmStructs.get(j).getTitle() + "\n";
                        }

                    }
                    addCSVline(Constants.CONST_NOTETYPE_TODO,
                            realmResultTodo.get(i).getTitle(), "", realmResultTodo.get(i).getCreateTimeStamp().getTime(), strSubTodoNotes,
                            "", Constants.CONST_NULL_ZERO, Constants.CONST_NULL_ZERO, "", "", j);
                }
            }

        }


        return CSVNotesStrings;
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

    public String getReportFromDBTabular(boolean textNotes, boolean audioNotes, boolean phoneCalls, boolean toDoNote) {


        String strOutPutReport = "";

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
        realmResult = realmResult.sort("id", Sort.DESCENDING);
        String strTextNotes = "<table class='u-full-width'><thead><tr><th></th><th>Title</th><th>Description</th><th>Created</th></tr></thead><tbody>\n";
        String strAudioNotes = "<table class='u-full-width'><thead><tr><th></th><th>Title</th><th>Description</th><th>Audio</th><th>Created</th></tr></thead><tbody>\n";
        String strPhoneCalls = "<table class='u-full-width'><thead><tr><th></th><th>Contact</th><th>Title</th><th>Description</th><th>Audio</th><th>Created</th></tr></thead><tbody>\n";
        String strTodo = "<table class='u-full-width'><thead><tr><th></th><th>Todo</th><th>Tasks</th><th>Created</th></tr></thead><tbody>\n";

        if (realmResult.size() == 0) {
            throw new NegativeArraySizeException();
        } else {

            for (int i = 0; i < realmResult.size(); i++) {
                int noteType = realmResult.get(i).getNoteType();
                if ((noteType == Constants.CONST_NOTETYPE_TEXT) && textNotes) {
                    isTextNoteHit = true;
                    intTextNotes++;
                    strTextNotes +=
                            "<tr>\n<td>" + String.valueOf(intTextNotes) + "</td>\n<td>" + realmResult.get(i).getTitle() + "</td>\n<td>" + Utility.convertNewLineCharToBrHtml(realmResult.get(i).getDescription()) + "</td>\n<td>" +
                                    Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "</td>\n</tr>";
                }

                if ((noteType == Constants.CONST_NOTETYPE_AUDIO) && audioNotes) {

                    RealmResults<AudioNoteInfoRealmStruct> audioNoteInfoRealmStructs = realmAudioNoteHelper.findAllAudioNotesByParentId(realmResult.get(i).getId());

                    intAudioNotes++;

                    strAudioNotes += "<tr>\n<td>" + String.valueOf(intAudioNotes) + "</td>\n<td>" + realmResult.get(i).getTitle() + "</td>\n<td>\n" + realmResult.get(i).getDescription() + "</td>\n";

                    // Each record doesn't have entry in Stamina. They just get entry if user wants to add a note to them. Here I scan the folder for files without database entry.
                    String pathToAudioFiles = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(realmResult.get(i).getId()));

                    ArrayList<AudioData> audioDatas = new ArrayList<>();

                    ArrayList<Integer> filesIds = listAudioFilesInDirectoryByParentId(pathToAudioFiles);

                    if (filesIds.size() > 0) {
                        for (int a = 0; a < audioNoteInfoRealmStructs.size(); a++) {
                            int intId = audioNoteInfoRealmStructs.get(a).getId();
                            for (int b = 0; b < filesIds.size(); b++) {
                                if (filesIds.get(b) == intId) {
                                    filesIds.remove(b);
                                    audioDatas.add(new AudioData(audioNoteInfoRealmStructs.get(a).getId(),
                                            audioNoteInfoRealmStructs.get(a).getParentDbId(),
                                            audioNoteInfoRealmStructs.get(a).getTitle(),
                                            Utility.convertNewLineCharToBrHtml(audioNoteInfoRealmStructs.get(a).getDescription()),
                                            audioNoteInfoRealmStructs.get(a).getTag()));
                                    break;
                                }
                            }
                        }


                        for (int b = 0; b < filesIds.size(); b++) {
                            audioDatas.add(new AudioData(filesIds.get(b),
                                    realmResult.get(i).getId(),
                                    "No title",
                                    "No description",
                                    Constants.CONST_NULL_ZERO));
                        }


                    }

                    isAudioNoteHit = true;


                    strAudioNotes += "<td>\n<ul class='items_ul'>\n";


                    for (int j = 0; j < audioDatas.size(); j++) {

                        strAudioNotes += "<li class='todo_notdone'>\n" +
                                "<span class='sub_note_title'>" + audioDatas.get(j).getTitle() + "</span><br><span class='sub_note_body'>" + Utility.convertNewLineCharToBrHtml(audioDatas.get(j).getDescription()) + "</span><br>\n" +
                                "<audio controls>" +
                                "<source src='" + audioNoteFileName(audioDatas.get(j)) + "' type='audio/aac'>" +
                                "Browser does not support the audio element." +
                                "</audio>" +
                                "</li>\n";

                    }

                    strAudioNotes += "</ul>\n</td>\n<td>" + Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "<td>\n</tr>\n";
                }

                if ((noteType == Constants.CONST_NOTETYPE_PHONECALL) && phoneCalls) {

                    isPhoneCallHit = true;
                    intPhoneCalls++;
                    strPhoneCalls +=
                            "<tr>\n<td>" + String.valueOf(intPhoneCalls) + "</td>\n<td>" + realmResult.get(i).getPhoneNumber() + "</td>\n<td>" +
                                    realmResult.get(i).getTitle() + "</td>\n<td>" +
                                    Utility.convertNewLineCharToBrHtml(realmResult.get(i).getDescription()) + "</td>\n<td>" +
                                    "<audio controls>" +
                                    "<source src='phonecalls/" + realmResult.get(i).getId() + ".aac' type='audio/aac'>" +
                                    "Browser does not support the audio element." +
                                    "</audio></td>\n<td>" +
                                    Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "</td>\n</tr>";
                }

            }

            if (toDoNote) {

                RealmResults<TodoParentRealmStruct> realmResultTodo = realm.where(TodoParentRealmStruct.class).findAll();

                realmResultTodo = realmResultTodo.sort("id", Sort.DESCENDING);

                for (int i = 0; i < realmResultTodo.size(); i++) {
                    isTodoHit = true;
                    intTodo++;

                    strTodo += "<tr>\n<td>" + String.valueOf(intTodo) + "</td>\n<td>" + realmResultTodo.get(i).getTitle() + "</td>\n";

                    ArrayList<TodoChildRealmStruct> todoChildRealmStructs = realmToDoHelper.getAllChildTodos(realmResultTodo.get(i).getId());

                    //If todo is just title and doesn't have any child.
                    if (todoChildRealmStructs.size() > 0) {

                        strTodo += "<td>\n<ul class='items_ul'>\n";

                        for (int j = 0; j < todoChildRealmStructs.size(); j++) {
                            strTodo += toDoBinder(todoChildRealmStructs.get(j));
                        }

                        strTodo += "</ul>\n";

                    }
                    strTodo += "</td>\n<td>" + Utility.unixTimeToReadable(realmResultTodo.get(i).getCreateTimeStamp().getTime() / 1000) + "</td>\n</tr>\n";

                }
            }

        }

        strOutPutReport = loadTemplate();

        String bodyContents = "";
        String navBar = "";


        if (textNotes && isTextNoteHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#text_notes'>Text Notes</a></li>\n";
            bodyContents += "<h6 class='docs-header' id='text_notes'>TEXT NOTES</h6>\n";
            strTextNotes += "\n</tbody>\n</table>";
            bodyContents += strTextNotes;
        }
        if (audioNotes && isAudioNoteHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#audio_notes'>Audio Notes</a></li>\n";
            bodyContents += "<h6 class='docs-header' id='audio_notes'>AUDIO NOTES</h6>\n";
            strAudioNotes += "\n</tbody>\n</table>";
            bodyContents += strAudioNotes;
        }
        if (phoneCalls && isPhoneCallHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#phone_calls'>Phone Calls</a></li>\n";
            bodyContents += "<h6 class='docs-header' id='phone_calls'>PHONE CALLS</h6>\n";
            strPhoneCalls += "\n</tbody>\n</table>";
            bodyContents += strPhoneCalls;
        }
        if (toDoNote && isTodoHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#todo_notes'>Todo</a></li>\n";
            bodyContents += "<h6 class='docs-header' id='todo_notes'>TODO</h6>\n";
            strTodo += "\n</tbody>\n</table>";
            bodyContents += strTodo;
        }

        strOutPutReport = strOutPutReport.replaceFirst("\\B#NAVBAR#\\B", navBar);
        strOutPutReport = strOutPutReport.replaceFirst("\\B#CONTENTS#\\B", bodyContents);

        return strOutPutReport;
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

        RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);
        RealmToDoHelper realmToDoHelper = new RealmToDoHelper(mContext);

        RealmResults<NoteInfoRealmStruct> realmResult = realm.where(NoteInfoRealmStruct.class).findAll();
        realmResult = realmResult.sort("id", Sort.DESCENDING);

        if (realmResult.size() == 0) {
            throw new NegativeArraySizeException();
        } else {

            for (int i = 0; i < realmResult.size(); i++) {
                int noteType = realmResult.get(i).getNoteType();

                if ((noteType == Constants.CONST_NOTETYPE_TEXT) && textNotes) {
                    isTextNoteHit = true;
                        /*"<div class='note_contents'><span class='note_title'>" + realmResult.get(i).getTitle() + "</span><span class='note_body'>"+ realmResult.get(i).getDescription() +"</span></div>" +*/
                    strTextNotes +=
                            "<div class='item'>\n" +
                                    "<div class='note_title'>" + realmResult.get(i).getTitle() + "</div>\n" +
                                    "<div class='note_contents'>" + Utility.convertNewLineCharToBrHtml(realmResult.get(i).getDescription()) + "</div>\n" +
                                    "<div class='note_timestamp'>" + Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "</div>" +
                                    "</div>\n";

                }

                if ((noteType == Constants.CONST_NOTETYPE_AUDIO) && audioNotes) {

                    RealmResults<AudioNoteInfoRealmStruct> audioNoteInfoRealmStructs = realmAudioNoteHelper.findAllAudioNotesByParentId(realmResult.get(i).getId());

                    strAudioNotes +=
                            "<div class='item'>\n" +
                                    "<div class='note_title'>" + realmResult.get(i).getTitle() + "</div>\n" +
                                    "<div class='note_contents'>" + Utility.convertNewLineCharToBrHtml(realmResult.get(i).getDescription()) + "</div>\n";

                    // Each record doesn't have entry in Stamina. They just get entry if user wants to add a note to them. Here I scan the folder for files without database entry.
                    String pathToAudioFiles = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(realmResult.get(i).getId()));

                    ArrayList<AudioData> audioDatas = new ArrayList<>();

                    ArrayList<Integer> filesIds = listAudioFilesInDirectoryByParentId(pathToAudioFiles);

                    if (filesIds.size() > 0) {
                        for (int a = 0; a < audioNoteInfoRealmStructs.size(); a++) {
                            int intId = audioNoteInfoRealmStructs.get(a).getId();
                            for (int b = 0; b < filesIds.size(); b++) {
                                if (filesIds.get(b) == intId) {
                                    filesIds.remove(b);
                                    audioDatas.add(new AudioData(audioNoteInfoRealmStructs.get(a).getId(),
                                            audioNoteInfoRealmStructs.get(a).getParentDbId(),
                                            audioNoteInfoRealmStructs.get(a).getTitle(),
                                            Utility.convertNewLineCharToBrHtml(audioNoteInfoRealmStructs.get(a).getDescription()),
                                            audioNoteInfoRealmStructs.get(a).getTag()));
                                    break;
                                }
                            }
                        }


                        for (int b = 0; b < filesIds.size(); b++) {
                            audioDatas.add(new AudioData(filesIds.get(b),
                                    realmResult.get(i).getId(),
                                    "No title",
                                    "No description",
                                    Constants.CONST_NULL_ZERO));
                        }


                    }

                    isAudioNoteHit = true;

                    strAudioNotes += "<ul class='items_ul'>\n";

                    for (int j = 0; j < audioDatas.size(); j++) {

                        strAudioNotes += "<li class='todo_notdone'>\n" +
                                "<span class='sub_note_title'>" + audioDatas.get(j).getTitle() + "</span><br><span class='sub_note_body'>" + Utility.convertNewLineCharToBrHtml(audioDatas.get(j).getDescription()) + "</span><br>\n" +
                                "<audio controls>" +
                                "<source src='" + audioNoteFileName(audioDatas.get(j)) + "' type='audio/aac'>" +
                                "Browser does not support the audio element." +
                                "</audio>" +
                                "</li>\n";
                    }

                    strAudioNotes += "</ul>\n" +
                            "<div class='note_timestamp'>" + Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "</div>\n" +
                            "</div>\n";


                }

                if ((noteType == Constants.CONST_NOTETYPE_PHONECALL) && phoneCalls) {

                    isPhoneCallHit = true;

                    strPhoneCalls +=
                            "<div class='item'>\n" +
                                    "<div class='note_title'>" + realmResult.get(i).getTitle() + " (" + realmResult.get(i).getPhoneNumber() + ")</div>\n" +
                                    "<div class='note_contents'>" + Utility.convertNewLineCharToBrHtml(realmResult.get(i).getDescription()) + "</div><br>\n" +
                                    "<audio controls>" +
                                    "<source src='phonecalls/" + realmResult.get(i).getId() + ".aac' type='audio/aac'>" +
                                    "Browser does not support the audio element." +
                                    "</audio>" +
                                    "<div class='note_timestamp'>" + Utility.unixTimeToReadable(realmResult.get(i).getCreateTimeStamp().getTime() / 1000) + "</div>\n" +
                                    "</div>\n";
                }

            }

            if (toDoNote) {

                RealmResults<TodoParentRealmStruct> realmResultTodo = realm.where(TodoParentRealmStruct.class).findAll();


                for (int i = 0; i < realmResultTodo.size(); i++) {
                    isTodoHit = true;
                    strTodo +=
                            "<div class='item'>\n" +
                                    "<div class='note_title'>" + realmResultTodo.get(i).getTitle() + "</div>\n";

                    ArrayList<TodoChildRealmStruct> todoChildRealmStructs = realmToDoHelper.getAllChildTodos(realmResultTodo.get(i).getId());

                    //If todo is just title and doesn't have any child.
                    if (todoChildRealmStructs.size() > 0) {

                        strTodo += "<ul class='items_ul'>\n";

                        for (int j = 0; j < todoChildRealmStructs.size(); j++) {
                            strTodo += toDoBinder(todoChildRealmStructs.get(j));
                        }

                        strTodo += "</ul>\n";

                    }

                    strTodo +=
                            "<div class='note_timestamp'>" + Utility.unixTimeToReadable(realmResultTodo.get(i).getCreateTimeStamp().getTime()) + "</div>\n" +
                                    "</div>\n";

                }
            }

        }

        strOutPutReport = loadTemplate();

        String navBar = "";
        String bodyContents = "";

        if (textNotes && isTextNoteHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#text_notes'>Text Notes</a></li>\n";
            bodyContents += "<h6 class='docs-header' id='text_notes'>TEXT NOTES</h6>\n";
            bodyContents += strTextNotes;
        }
        if (audioNotes && isAudioNoteHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#audio_notes'>Audio Notes</a></li>\n";
            bodyContents += "<h6 class='docs-header' id='audio_notes'>AUDIO NOTES</h6>\n";
            bodyContents += strAudioNotes;
        }
        if (phoneCalls && isPhoneCallHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#phone_calls'>Phone Calls</a></li>\n";
            bodyContents += "<h6 class='docs-header' id='phone_calls'>PHONE CALLS</h6>\n";
            bodyContents += strPhoneCalls;
        }
        if (toDoNote && isTodoHit) {
            navBar += "<li class='navbar-item'><a class='navbar-link' href='#todo_notes'>Todo</a></li>\n";
            bodyContents += "<h6 class='docs-header' id='todo_notes'>TODO</h6>\n";
            bodyContents += strTodo;
        }

        strOutPutReport = strOutPutReport.replaceFirst("\\B#NAVBAR#\\B", navBar);
        strOutPutReport = strOutPutReport.replaceFirst("\\B#CONTENTS#\\B", bodyContents);

        return strOutPutReport;
    }

    private ArrayList<Integer> listAudioFilesInDirectoryByParentId(String pathToAudioFiles) {

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
            return "<li class='todo_done'>" + todoChildRealmStruct.getTitle() + "</li>\n";
        else
            return "<li class='todo_notdone'> " + todoChildRealmStruct.getTitle() + "</li>\n";

    }

}