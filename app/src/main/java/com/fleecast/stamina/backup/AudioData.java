package com.fleecast.stamina.backup;

/**
 * Created by nnt on 7/12/16.
 */

public class AudioData {

    private int id;
    private int parent_db_id;
    private String title;
    private int tag;
    private String description;

    public AudioData(int id, int parent_db_id, String title, String description, int tag) {
        this.id = id;
        this.parent_db_id = parent_db_id;
        this.title = title;
        this.tag = tag;
        this.description = description;
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