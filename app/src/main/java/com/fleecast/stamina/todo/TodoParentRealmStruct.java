package com.fleecast.stamina.todo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nnt on 3/05/16.
 */
public class TodoParentRealmStruct extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private Date create_time_stamp;
    private boolean has_done;

    public TodoParentRealmStruct() {
    }

    public TodoParentRealmStruct(int id, String title, Date create_time_stamp, boolean has_done) {
        this.id = id;
        this.title = title;
        this.create_time_stamp = create_time_stamp;
        this.has_done = has_done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateTimeStamp() {
        return create_time_stamp;
    }

    public void setCreateTimeStamp(Date create_time_stamp) {
        this.create_time_stamp = create_time_stamp;
    }

    public boolean getHasDone() {
        return has_done;
    }

    public void setHasDone(boolean has_done) {
        this.has_done = has_done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
