package com.fleecast.stamina.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nnt on 3/05/16.
 */
public class NoteInfoRealmStruct extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private boolean has_audio;
    private Date create_time_stamp;
    private String description;
    private Date update_time;
    private Date start_time;
    private Date end_time;
    private int call_type;
    private int tag;
    private String phone_number;
    private int note_type;
    private String extras;

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public int getCallType() {
        return call_type;
    }

    public void setCallType(int call_type) {
        this.call_type = call_type;
    }

    public int getNoteType() {
        return note_type;
    }

    public void setNoteType(int note_type) {
        this.note_type = note_type;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public Date getStartTime() {
        return start_time;
    }

    public void setStartTime(Date start_time) {
        this.start_time = start_time;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Date getEndTime() {
        return end_time;
    }

    public void setEndTime(Date end_time) {
        this.end_time = end_time;
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
    public Date getCreateTimeStamp() {
        return create_time_stamp;
    }

    public void setCreateTimeStamp(Date create_time_stamp) {
        this.create_time_stamp = create_time_stamp;
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

    public Date getUpdateTime() {
        return update_time;
    }

    public void setUpdateTime(Date update_time) {
        this.update_time = update_time;
    }

}
