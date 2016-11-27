package com.fleecast.stamina.backup;

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



    public static void writeBackUp(File outputFile, ArrayList<BackUpNotesStruct> noteInfoStructs) {

        FileOutputStream fos = null;
        ObjectOutputStream out = null;

        try {
            fos = new FileOutputStream(outputFile);
            out = new ObjectOutputStream(fos);
            out.writeObject(noteInfoStructs);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static ArrayList<BackUpNotesStruct> readBackUp(File inputFile) throws Exception {
        ArrayList<BackUpNotesStruct> q;

        FileInputStream fis = null;
        ObjectInputStream in = null;

        fis = new FileInputStream(inputFile);
        in = new ObjectInputStream(fis);

        q = (ArrayList) in.readObject();
        in.close();
        return q;
    }

}