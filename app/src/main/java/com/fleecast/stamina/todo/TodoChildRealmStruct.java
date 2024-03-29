package com.fleecast.stamina.todo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nnt on 3/05/16.
 */
public class TodoChildRealmStruct extends RealmObject {

    @PrimaryKey
    private int id;
    private int parent_id;
    private String title;
    private Date create_time_stamp;
    private boolean has_done;
    private int order;
    private int color;
    private String extras;
    private String group;
    private boolean del;

    public TodoChildRealmStruct() {

    }

    public TodoChildRealmStruct(int id, int parent_id, String title, Date create_time_stamp, boolean has_done, int order,int color,String group,String extras,boolean del) {

        this.id = id;
        this.parent_id = parent_id;
        this.title = title;
        this.create_time_stamp = create_time_stamp;
        this.has_done = has_done;
        this.order = order;
        this.color=color;
        this.group=group;
        this.extras=extras;
        this.del=del;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parent_id;
    }

    public void setParentId(int parent_id) {
        this.parent_id = parent_id;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

}
