package com.fleecast.stamina.models;

import java.util.Date;

/**
 * Created by nnt on 3/05/16.
 */
public class NoteInfoStruct {

    private int id;
    private String title;
    private boolean has_audio;
    private Date create_time_stamp;
    private Date update_time;
    private String description;
    private Date start_time;
    private Date end_time;
    private int call_type;
    private int color;
    private String phone_number;
    private int note_type;
    private String extras;
    private String group;
    private boolean del;
    private int order;


    public NoteInfoStruct(int id, String title, String description, boolean has_audio,
                          Date update_time,Date create_time_stamp, Date start_time,
                          Date end_time, int call_type, String phone_number, int color,
                          int order,int note_type,String extras, String group, boolean del) {

        this.call_type = call_type;
        this.create_time_stamp = create_time_stamp;
        this.description = description;
        this.end_time = end_time;
        this.has_audio = has_audio;
        this.id = id;
        this.order = order;
        this.phone_number = phone_number;
        this.start_time = start_time;
        this.color = color;
        this.title = title;
        this.update_time = update_time;
        this.note_type = note_type;
        this.extras = extras;
        this.group=group;
        this.del=del;

    }

    public int getNoteType() {
        return note_type;
    }

    public void setNotetype(int note_type) {
        this.note_type = note_type;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public int getCallType() {
        return call_type;
    }

    public void setCallType(int call_type) {
        this.call_type = call_type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
