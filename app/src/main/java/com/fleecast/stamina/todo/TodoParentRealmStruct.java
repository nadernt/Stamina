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
    private int color;
    private String extras;
    private String group;
    private boolean del;
    private int order;

    public TodoParentRealmStruct() {
    }

    public TodoParentRealmStruct(int id, String title, Date create_time_stamp, boolean has_done,int color,String group,String extras,boolean del,int order) {
        this.id = id;
        this.title = title;
        this.create_time_stamp = create_time_stamp;
        this.has_done = has_done;
        this.color=color;
        this.group=group;
        this.extras=extras;
        this.del=del;
        this.order=order;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }
}
