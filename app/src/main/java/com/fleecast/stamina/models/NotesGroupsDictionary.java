package com.fleecast.stamina.models;

import android.content.Context;

import java.util.Arrays;
import java.util.HashSet;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nnt on 17/12/16.
 */

public class NotesGroupsDictionary {

    private static final String TAG = "RealmNoteHelper";


    private Realm realm;
    public Context mContext;
    private String[] dictionaryOfGroups;


    /**
     * constructor to create instances of realm
     *
     * @param mContext
     */
    public NotesGroupsDictionary(Context mContext) {
        realm = Realm.getDefaultInstance();
        this.mContext = mContext;
        RealmResults<NoteInfoRealmStruct> realmResults = realm.where(NoteInfoRealmStruct.class).findAll();

        dictionaryOfGroups = new String[realmResults.size()];
        for (int i=0; i< realmResults.size() ; i++)
        {
            dictionaryOfGroups [i]=realmResults.get(i).getGroup();
        }

        // Trim from duplicated words.
        dictionaryOfGroups = new HashSet<String>(Arrays.asList(dictionaryOfGroups)).toArray(new String[0]);
    }

    public String [] getTagsList(){

        //return new String [] {"Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives"};
return null;
        //return  dictionaryOfGroups;
    }

}
