package com.fleecast.stamina.backup;

import java.io.Serializable;
import java.util.Date;

public class BackUpNotesStruct implements Serializable {


    private int id;
    private int id_child;
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
    private int order;
    private String child_title;
    private boolean todo_done;
    private int note_type;
    private String child_description;
    private int child_order;
    private Date child_create_time_stamp;
    private String group;
    private String extras;
    private boolean del;

    public BackUpNotesStruct(int id, String title, String description, boolean has_audio,
                             Date update_time, Date create_time_stamp, Date start_time,
                             Date end_time, int call_type, String phone_number,
                             int color, String group, String extras, boolean del,
                             int order, int note_type, int id_child,
                             String child_title, String child_description, Date child_create_time_stamp,
                             boolean todo_done, int child_order) {
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
        this.child_title = child_title;
        this.child_description = child_description;
        this.todo_done = todo_done;
        this.id_child = id_child;
        this.child_order = child_order;
        this.child_create_time_stamp = child_create_time_stamp;
        this.group = group;
        this.extras = extras;
        this.del = del;

    }


    public int getId_child() {
        return id_child;
    }

    public void setId_child(int id_child) {
        this.id_child = id_child;
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

    public int getNoteType() {
        return note_type;
    }

    public void setNoteType(int note_type) {
        this.note_type = note_type;
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

    public boolean getTodoDone() {
        return todo_done;
    }

    public void setTodoDone(boolean todo_done) {
        this.todo_done = todo_done;
    }

    public String getChildTitle() {
        return child_title;
    }

    public void setChildTitle(String child_title) {
        this.child_title = child_title;
    }

    public String getChildDescription() {
        return child_description;
    }

    public void setChildDescription(String child_description) {
        this.child_description = child_description;
    }

    public int getChild_order() {
        return child_order;
    }

    public void setChild_order(int child_order) {
        this.child_order = child_order;
    }

    public Date getChild_create_time_stamp() {
        return child_create_time_stamp;
    }

    public void setChild_create_time_stamp(Date child_create_time_stamp) {
        this.child_create_time_stamp = child_create_time_stamp;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

}

