package com.fleecast.stamina.models;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by nnt on 31/07/16.
 */
public class BeautifyNoteText {

    private final Context mContext;
    private NoteInfoRealmStruct noteInfoRealmStruct;
    private RealmContactHelper realmContactHelper;
    private RealmResults<AudioNoteInfoRealmStruct> tmpRealmAudio;

    private String detailsTextFormat;
    private String detailsTextDescriptions;
    private String detailsHtmlDescriptions;
    private String detailsHtmlFormat;



    public BeautifyNoteText(Context mContext, NoteInfoRealmStruct noteInfoRealmStruct) {

        this.noteInfoRealmStruct = noteInfoRealmStruct;
        this.mContext = mContext;
        realmContactHelper  = new RealmContactHelper(mContext);
        composeNoteData(noteInfoRealmStruct);
        setHtmlFormatDetails(noteInfoRealmStruct);
    }

    private void composeNoteData(NoteInfoRealmStruct noteInfo){

         detailsTextFormat = "Title: " + noteInfo.getTitle() +"\n";

        if (noteInfo.getDescription() == null) {
            detailsTextDescriptions = "No description in note.";
        }
        else if (noteInfo.getDescription().isEmpty()) {
            detailsTextDescriptions = "No description in note.";
        }
        else {
            detailsTextDescriptions = noteInfo.getDescription();
        }

        if ((noteInfo.getNoteType() == Constants.CONST_NOTETYPE_AUDIO)) {
            detailsHtmlDescriptions = detailsTextDescriptions + "<br><br>" + loadAudioNoteDetailsInHtml(noteInfo);

            detailsTextDescriptions += "\n" + loadAudioNoteDetailsInText(noteInfo);
        }
        else{
            detailsHtmlDescriptions = detailsTextDescriptions + "<br><br>";

        }

        detailsTextFormat += "Created at: " + noteInfo.getCreateTimeStamp() +"\n";

        if (noteInfo.getNoteType() == Constants.CONST_NOTETYPE_PHONECALL) {

            detailsTextFormat += "Call Name: " + Utility.getContactName(mContext,noteInfo.getPhoneNumber() ) +"\n";

            detailsTextFormat += "Phone Number: " + noteInfo.getPhoneNumber() +"\n";

            if(noteInfo.getCallType() == Constants.RECORDS_IS_OUTGOING) {
                detailsTextFormat += "Call Type: I called." + "\n";
            }else if (noteInfo.getCallType() == Constants.RECORDS_IS_INCOMING){
                detailsTextFormat += "Call Type: Called me." + "\n";
            }

            detailsTextFormat += "Duration: " + Utility.calculateCallDuration(noteInfo.getStartTime(),noteInfo.getEndTime()) + "\n";

        } else if (noteInfo.getNoteType() == Constants.CONST_NOTETYPE_AUDIO) {


            File file = new File(ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(noteInfo.getId())));

            if(file.listFiles() != null)
                detailsTextFormat += "Number of Records: " + String.valueOf(file.listFiles().length) + "\n";
            else
                detailsTextFormat += "Number of Records: 0\n";
        }
    }

    public String getTextFormatDetails(){

        return detailsTextFormat;

    }

    public String getTextFormatedAll()
    {
        return getTextFormatDetails() + "\n" + detailsTextDescriptions;
    }

    public String getDetailsHtmlFormatedAll()
    {
        return detailsHtmlDescriptions;
    }

    private void setHtmlFormatDetails(NoteInfoRealmStruct noteInfo){

        detailsHtmlFormat = "<b>Title:</b> " + noteInfo.getTitle() +"<br>";
        detailsHtmlFormat += "<b>Created at:</b> " + noteInfo.getCreateTimeStamp() +"<br>";

        if (noteInfo.getNoteType() == Constants.CONST_NOTETYPE_PHONECALL) {

            detailsHtmlFormat += "<b>Call Name:</b> " + Utility.getContactName(mContext,noteInfo.getPhoneNumber()) +"<br>";

            detailsHtmlFormat += "<b>Phone Number:</b> " + noteInfo.getPhoneNumber() +"<br>";

            if(noteInfo.getCallType() == Constants.RECORDS_IS_OUTGOING) {
                detailsHtmlFormat += "<b>Call Type:</b> I called." + "<br>";
            }else if (noteInfo.getCallType() == Constants.RECORDS_IS_INCOMING){
                detailsHtmlFormat += "<b>Call Type:</b> Called me." + "<br>";
            }

            detailsHtmlFormat += "<b>Duration:</b> " + Utility.calculateCallDuration(noteInfo.getStartTime(),noteInfo.getEndTime()) + "<br>";

        } else if (noteInfo.getNoteType() == Constants.CONST_NOTETYPE_AUDIO) {

            File file = new File(ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(noteInfo.getId())));


            if(file.listFiles() != null)
                detailsHtmlFormat += "<b>Number of Records:</b> " + String.valueOf(file.listFiles().length) + "<br>";
            else
                detailsHtmlFormat += "<b>Number of Records:</b> 0 <br>";
        }

    }

    public Spanned getHtmlFormatDetails(){
        return Utility.fromHTMLVersionCompat(detailsHtmlFormat,Html.FROM_HTML_MODE_LEGACY);
    }

    public  String loadAudioNoteDetailsInHtml(NoteInfoRealmStruct noteInfo) {


        String folderToPlay = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(noteInfo.getId()));

        File folder = new File(folderToPlay);

        File[] listOfFiles = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //String name = pathname.getName().toLowerCase();
                //return name.endsWith(".xml") && pathname.isFile();
                return pathname.isFile() && !pathname.isHidden();
            }
        });

        if (listOfFiles == null)
            return "";

        Arrays.sort(listOfFiles,new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i = 0;
                try {
                    int s = name.indexOf('_')+1;
                    int e = name.lastIndexOf('.');
                    String number = name.substring(s, name.length());
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    i = 0; // if filename does not match the format
                    // then default to 0
                }
                return i;
            }
        });


        RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);

        List<AudioNoteInfoRealmStruct> audioNoteInfoStruct = new ArrayList<>(realmAudioNoteHelper.findAllAudioNotesByParentId(noteInfo.getId()));

        String htmlComposed="";

        for (int i = 0; i < listOfFiles.length; i++) {

            boolean hasHitInDatabase = false;

            int dbId = getDbIdFromFileName(listOfFiles[i].getName());

            if (audioNoteInfoStruct.size() > 0) {

                int indexInDbStruct = lookInsideListForDbKey(audioNoteInfoStruct, dbId);

                if (indexInDbStruct>Constants.CONST_NULL_MINUS) {

                    htmlComposed += "\u2022 <font color='" + getHexStringFromInt(R.color.gray_asparagus) + "'>"+audioNoteInfoStruct.get(indexInDbStruct).getTitle()+ "</font><br>" +
                            "<small><font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" + Utility.unixTimeToReadable((long) dbId) + "</font></small><br>" +
                            "<font color='" + getHexStringFromInt(R.color.gray_wolf) + "'>"+ audioNoteInfoStruct.get(indexInDbStruct).getDescription() + "</font><br><br>";


                    hasHitInDatabase = true;
                }

            }

            if (!hasHitInDatabase) {

                htmlComposed += "\u2022 <font color='" + getHexStringFromInt(R.color.gray_asparagus) + "'>"+Constants.CONST_STRING_NO_TITLE + "</font><br>" +
                        "<small><font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" + Utility.unixTimeToReadable((long) dbId) + "</font></small><br>" +
                "<font color='" + getHexStringFromInt(R.color.gray_wolf) + "'>"+Constants.CONST_STRING_NO_DESCRIPTION + "</font><br><br>";

            }


        }

        return htmlComposed;
    }

    public  String loadAudioNoteDetailsInText(NoteInfoRealmStruct noteInfo) {


        String folderToPlay = ExternalStorageManager.getPathToAudioFilesFolderById(String.valueOf(noteInfo.getId()));

        File folder = new File(folderToPlay);

        File[] listOfFiles = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && !pathname.isHidden();
            }
        });

        if (listOfFiles == null)
            return "";

       Arrays.sort(listOfFiles,new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i = 0;
                try {
                    int s = name.indexOf('_')+1;
                    String number = name.substring(s, name.length());
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    i = 0; // if filename does not match the format
                    // then default to 0
                }
                return i;
            }
        });

        RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);

        List<AudioNoteInfoRealmStruct> audioNoteInfoStruct = new ArrayList<>(realmAudioNoteHelper.findAllAudioNotesByParentId(noteInfo.getId()));

        String textComposed="";

        for (int i = 0; i < listOfFiles.length; i++) {

            boolean hasHitInDatabase = false;

            int dbId = getDbIdFromFileName(listOfFiles[i].getName());

            if (audioNoteInfoStruct.size() > 0) {

                int indexInDbStruct = lookInsideListForDbKey(audioNoteInfoStruct, dbId);

                if (indexInDbStruct>Constants.CONST_NULL_MINUS) {

                    textComposed += "\u2022 " + audioNoteInfoStruct.get(indexInDbStruct).getTitle() + "\n" +
                            Utility.unixTimeToReadable((long) dbId) + "\n" + audioNoteInfoStruct.get(indexInDbStruct).getDescription() + "\n\n";
                    hasHitInDatabase = true;
                }

            }

            if (!hasHitInDatabase) {

                textComposed += "\u2022 " + Constants.CONST_STRING_NO_TITLE + "\n" + Utility.unixTimeToReadable((long) dbId) + "\n" +
                        Constants.CONST_STRING_NO_DESCRIPTION + "\n\n";

            }


        }

        return textComposed;
    }


    private int lookInsideListForDbKey(List<AudioNoteInfoRealmStruct> adNFo, int filePostFixId){

        int foundedDbIdKeyIndex=Constants.CONST_NULL_MINUS;
        for(int i=0 ; i < adNFo.size() ; i++){

            if(adNFo.get(i).getId()==filePostFixId){
                foundedDbIdKeyIndex=i;
                break;
            }


        }
        return foundedDbIdKeyIndex ;

    }

    private String getHexStringFromInt(int resourceColorId){
        int intColor = ContextCompat.getColor(mContext, resourceColorId);
        return "#" + String.valueOf(Integer.toHexString(intColor)).substring(2);
    }

    private int getDbIdFromFileName(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(file_name.lastIndexOf("_") + 1));
    }



}
