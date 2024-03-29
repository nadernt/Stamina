package com.fleecast.stamina.models;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.AudioNoteInfoRealmStruct;
import com.fleecast.stamina.models.AudioNoteInfoStruct;
import com.fleecast.stamina.models.RealmAppHelper;
import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public final class PlayListHelper {

    private final String folderToPlay;
    private final String mFileDbUniqueToken;
    private final  RealmAudioNoteHelper realmAudioNoteHelper;
    private final  RealmNoteHelper realmNoteHelper;

    private final Context mContext;
    private MyApplication myApplication;

    //public List <AudioNoteInfoStruct> stackPlaylist = new ArrayList<>();

    public PlayListHelper(Context mContext, String mFileDbUniqueToken) {

        this.mContext = mContext;
        this.mFileDbUniqueToken = mFileDbUniqueToken;
        this.folderToPlay = ExternalStorageManager.getPathToAudioFilesFolderById(mFileDbUniqueToken);

        realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);
        myApplication = (MyApplication) mContext.getApplicationContext();
        realmNoteHelper = null;
    }

    public PlayListHelper(Context mContext) {

        this.folderToPlay=null;
        this.mFileDbUniqueToken=null;
        realmAudioNoteHelper = null;
        realmNoteHelper = new RealmNoteHelper(mContext);
        this.mContext = mContext;

        myApplication = (MyApplication) mContext.getApplicationContext();


    }

    public void loadJustSingleFileForPlay(String fileName,int dbId)
    {
        List <AudioNoteInfoStruct> tmpStackPlaylist = new ArrayList<>();

        NoteInfoRealmStruct tmpNoteInfo = realmNoteHelper.getNoteById(dbId);


        tmpStackPlaylist.add(0,new AudioNoteInfoStruct(
                dbId,
                dbId,
                fileName,
                tmpNoteInfo.getTitle(),tmpNoteInfo.getDescription(),Constants.CONST_NULL_ZERO));

        myApplication.stackPlaylist = new ArrayList<AudioNoteInfoStruct>(tmpStackPlaylist);


    }

    /**
     * Our data, part 1.
     */
    public Spanned[] loadAudioListForListViewAdapter() {


        List <AudioNoteInfoStruct> tmpStackPlaylist = new ArrayList<>();

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
            return new Spanned[] {Utility.fromHTMLVersionCompat(Constants.CONST_STRING_NO_DESCRIPTION,Html.FROM_HTML_MODE_LEGACY)};


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

        Spanned[] htmlArrayForList = new Spanned[listOfFiles.length];

        List<AudioNoteInfoRealmStruct> audioNoteInfoStruct = new ArrayList<>(realmAudioNoteHelper.findAllAudioNotesByParentId(Integer.valueOf(mFileDbUniqueToken)));
        String htmlComposeListView;

        for (int i = 0; i < listOfFiles.length; i++) {

            boolean hasHitInDatabase = false;

            int dbId = Utility.getDbIdFromFileName(listOfFiles[i].getName());

            if (audioNoteInfoStruct.size() > 0) {

            int indexInDbStruct = lookInsideListForDbKey(audioNoteInfoStruct, dbId);

            if (indexInDbStruct>Constants.CONST_NULL_MINUS) {

                htmlComposeListView = "<font color='" + getHexStringFromInt(R.color.gray_asparagus) + "'>"+Utility.ellipsize(audioNoteInfoStruct.get(indexInDbStruct).getTitle(),Constants.CONST_PLAYER_LIST_TEXT_ELLLIPSIZE)+ "</font><br>"+
                        "<small><font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" + Utility.unixTimeToReadable((long) dbId) + "</font></small>" ;

                htmlArrayForList[i] = Utility.fromHTMLVersionCompat(htmlComposeListView,Html.FROM_HTML_MODE_LEGACY);

                tmpStackPlaylist.add(i,new AudioNoteInfoStruct(
                        dbId,
                        audioNoteInfoStruct.get(indexInDbStruct).getParentDbId(),
                        folderToPlay +File.separator + listOfFiles[i].getName(),
                        audioNoteInfoStruct.get(indexInDbStruct).getTitle(),
                        audioNoteInfoStruct.get(indexInDbStruct).getDescription(),
                        audioNoteInfoStruct.get(indexInDbStruct).getTag() ));

                hasHitInDatabase = true;
            }

            }

            if (!hasHitInDatabase) {

                htmlComposeListView = "<font color='" + getHexStringFromInt(R.color.gray_asparagus) + "'>"+Constants.CONST_STRING_NO_TITLE + "</font><br>"+
                        "<small><font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" + Utility.unixTimeToReadable((long) dbId) + "</font></small>" ;

                htmlArrayForList[i] = Utility.fromHTMLVersionCompat(htmlComposeListView,Html.FROM_HTML_MODE_LEGACY);

                tmpStackPlaylist.add(i,new AudioNoteInfoStruct(
                        dbId,
                        Integer.valueOf(mFileDbUniqueToken),
                        folderToPlay +File.separator + listOfFiles[i].getName(),
                        null,null,Constants.CONST_NULL_ZERO));

            }


        }

        myApplication.stackPlaylist = new ArrayList<AudioNoteInfoStruct>(tmpStackPlaylist);

        return htmlArrayForList;
    }




    private String getHexStringFromInt(int resourceColorId){
        int intColor = ContextCompat.getColor(mContext, resourceColorId);
        return "#" + String.valueOf(Integer.toHexString(intColor)).substring(2);
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

}
