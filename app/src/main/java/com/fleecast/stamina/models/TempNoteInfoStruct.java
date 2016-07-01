package com.fleecast.stamina.models;

import java.util.Date;

/**
 * Created by nnt on 3/05/16.
 */
public class TempNoteInfoStruct {

    private int id;
    private String title;
    private boolean has_audio;
    private String description;
    private int tag;
    private boolean is_new_or_updating;

    public TempNoteInfoStruct(int id, String title, String description, boolean has_audio,
                              int tag) {
        this.description = description;
        this.has_audio = has_audio;
        this.id = id;
        this.tag = tag;
        this.title = title;

    }

    public TempNoteInfoStruct() {
        this.description = null;
        this.has_audio = false;
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

    public boolean getHasAudio() {
        return has_audio;
    }

    public void setHasAudio(boolean has_audio) {
        this.has_audio = has_audio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getIsNeworUpdate()
    {
        return is_new_or_updating;
    }

    public void setIsNewOrUpdate(boolean is_new_or_updating)
    {
        this.is_new_or_updating = is_new_or_updating;

    }
}
