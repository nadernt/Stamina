package com.fleecast.stamina.notetaking;

import android.content.Context;
import android.util.Log;

import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;

import java.io.File;

/**
 * Created by nnt on 13/07/16.
 */
public class NoteDeleteHelper {

    Context context;
    private RealmNoteHelper realmNoteHelper;
    private RealmAudioNoteHelper realmAudioNoteHelper;

    public NoteDeleteHelper(Context context) {

        this.context = context;
//        realmAudioNoteHelper = new RealmAudioNoteHelper(context);

    }

    public void deleteTextNote(int dbId) {
        realmNoteHelper = new RealmNoteHelper(context);
        realmNoteHelper.deleteSingleNote(dbId);
    }


    public void deleteAudioNoteAndItsFiles(int dbId) {
        realmNoteHelper = new RealmNoteHelper(context);
        realmAudioNoteHelper = new RealmAudioNoteHelper(context);
        realmNoteHelper.deleteSingleNote(dbId);
        realmAudioNoteHelper.deleteAllAudioNoteByParentId(dbId);
        moveAudioNoteToBin(String.valueOf(dbId));
    }

    public void deletePhoneNoteAndItsFiles(int dbId) {
        realmNoteHelper = new RealmNoteHelper(context);
        realmNoteHelper.deleteSingleNote(dbId);
        movePhoneNoteToBin(String.valueOf(dbId));
    }

    private void movePhoneNoteToBin(String dbId){

        File dir = new File(ExternalStorageManager.getWorkingDirectory() +  File.separator +
                Constants.CONST_PHONE_CALLS_DIRECTORY_NAME + File.separator + dbId + Constants.RECORDER_AUDIO_FORMAT_AAC);

        File createTrashFolder = new File(ExternalStorageManager.getWorkingDirectory()+
                Constants.CONST_RECYCLEBIN_DIRECTORY_NAME,
                Constants.CONST_PHONE_CALLS_DIRECTORY_NAME);

        createTrashFolder.mkdirs();

        dir.renameTo(new File(createTrashFolder + File.separator + dbId + Constants.RECORDER_AUDIO_FORMAT_AAC));
        /*if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                //new File(dir, children[i]).delete();

                if (new File(dir, children[i]).exists()) {
                    File moveTobin = new File(createTrashFolder + File.separator + new File(children[i]).getName());
                    new File(dir, children[i]).renameTo(moveTobin);
                }
            }

            dir.delete();
        }*/
    }


    private void moveAudioNoteToBin(String dbId){

        File dir = new File(ExternalStorageManager.getPathToAudioFilesFolderById(dbId));

        File createTrashFolder = new File(ExternalStorageManager.getWorkingDirectory()+ Constants.CONST_RECYCLEBIN_DIRECTORY_NAME, dbId);

        createTrashFolder.mkdirs();

        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                //new File(dir, children[i]).delete();
                if (new File(dir, children[i]).exists()) {
                    File moveTobin = new File(createTrashFolder + File.separator + new File(children[i]).getName());
                    new File(dir, children[i]).renameTo(moveTobin);
                }
            }

            dir.delete();
        }
    }
}
