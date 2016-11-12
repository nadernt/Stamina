package com.fleecast.stamina.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nnt on 27/03/16.
 */
public class ContactDbRealmStruct extends RealmObject{

    @PrimaryKey
    private String id;
    private String contact_name;
    private String contact_number;
    /*private boolean ignoreList ;*/


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getContactNumber() {
        return contact_number;
    }

    public void setContactNumber(String contact_number) {
        this.contact_number = contact_number;
    }

/*
    public boolean isIgnoreList() {
        return ignoreList;
    }

    public void setIgnoreList(boolean ignoreList) {
        this.ignoreList = ignoreList;
    }
*/

    public String getContactName() {
        return contact_name;
    }

    public void setContactName(String contact_name) {
        this.contact_name = contact_name;
    }

}
