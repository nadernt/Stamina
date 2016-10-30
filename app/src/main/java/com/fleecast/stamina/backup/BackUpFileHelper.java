package com.fleecast.stamina.backup;

import android.os.Environment;

import com.fleecast.stamina.models.NoteInfoStruct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by nnt on 30/10/16.
 */

public class BackUpFileHelper {
    final boolean IS_ENCRYPTED = true;

public void  WriteBackUp(File outputFile, ArrayList <BackUpNotesStruct> noteInfoStructs,String encryptionKey) {

    FileOutputStream fos = null;
    ObjectOutputStream out = null;

    try {
        fos = new FileOutputStream(outputFile);
        out = new ObjectOutputStream(fos);
        if(encryptionKey!=null || !encryptionKey.isEmpty())
        out.writeBoolean(IS_ENCRYPTED);
        out.writeObject(noteInfoStructs);
        out.close();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
    public boolean isBackUpEncrypted(File backupFile) throws  Exception{
        FileInputStream fis = null;
        ObjectInputStream in = null;

            fis = new FileInputStream(backupFile);
            in = new ObjectInputStream(fis);

            boolean fileContent = in.readBoolean();
            in.close();
            return  fileContent;


    }
}
