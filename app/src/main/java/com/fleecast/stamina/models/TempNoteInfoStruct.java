package com.fleecast.stamina.models;

import java.util.Date;

/**
 * Created by nnt on 3/05/16.
 */
public class TempNoteInfoStruct {

    private int id;
    private String title;
    private String description;
    private int tag;

   public TempNoteInfoStruct(int id, String title, String description, boolean has_audio,
                              int tag) {
        this.description = description;
        this.id = id;
        this.tag = tag;
        this.title = title;

    }

    public TempNoteInfoStruct() {
        this.description = null;
        this.id = 0;
        this.tag = 0;
        this.title = null;
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
