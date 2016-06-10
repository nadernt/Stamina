package com.fleecast.stamina.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nnt on 3/05/16.
 */
public class AudioNoteInfoRealmStruct extends RealmObject {

    @PrimaryKey
    private int id;
    private int parent_db_id;
    private String title;
    private int tag;
    private String description;


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

    public int getParentDbId() {
        return parent_db_id;
    }

    public void setParentDbId(int parent_db_id) {
        this.parent_db_id = parent_db_id;
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
