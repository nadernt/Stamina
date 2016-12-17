package com.fleecast.stamina.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nnt on 17/12/16.
 */

public class NotesGroupsDictionary {

    private static final String TAG = "RealmNoteHelper";


    private Realm realm;
    public Context mContext;
    List<String> al = new ArrayList<>();


    /**
     * constructor to create instances of realm
     *
     * @param mContext
     */
    public NotesGroupsDictionary(Context mContext) {
        realm = Realm.getDefaultInstance();
        this.mContext = mContext;
        RealmResults<NoteInfoRealmStruct> realmResults = realm.where(NoteInfoRealmStruct.class).findAll();
        //String [] aa = {"Cheese", "Pepperoni", "Black Olives","1Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives","Cheese", "Pepperoni", "Black Olives"};
        //al.addAll(Arrays.asList(aa));

        for (int i=0; i< realmResults.size() ; i++)
        {
            String tmp = realmResults.get(i).getGroup();
            if(tmp!=null)
            al.add(tmp);
        }

        // Trim from duplicated words.
        Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(al);

        al = new ArrayList<String>(set);
        Collections.sort(al, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

    }

    public String [] getTagsList(){

        if(al.size()==0)
            return null;
        else
            return  al.toArray(new String[al.size()]);
    }

}
