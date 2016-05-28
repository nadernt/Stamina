package com.fleecast.stamina.models;

/**
 * Created by nnt on 27/03/16.
 */
public class ContactStruct {

    private String id;
    private String contact_name;
    private String contact_number;
    /*private boolean blackList ;*/

    public ContactStruct( String id, String contact_number, String contact_name) {
        this.contact_name = contact_name;
        this.contact_number = contact_number;
        this.id = id;
    }

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

    public String getContactName() {
        return contact_name;
    }

    public void setContactName(String contact_name) {
        this.contact_name = contact_name;
    }

/*
    public boolean isBlackList() {
        return blackList;
    }

    public void setBlackList(boolean blackList) {
        this.blackList = blackList;
    }
*/


}
