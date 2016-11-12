package com.fleecast.stamina.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nnt on 27/03/16.
 */
public class AppDbRealmStruct extends RealmObject{

    @PrimaryKey
    private int id;
    private String title;
    private String app_package_name;
    private int app_group;
    private int use_rank;
    private Date last_use_time_stamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppGroup() {
        return app_group;
    }

    public void setAppGroup(int app_group) {
        this.app_group = app_group;
    }

    public String getAppPackageName() {
        return app_package_name;
    }

    public void setAppPackageName(String app_package_name) { this.app_package_name = app_package_name;}

    public Date getLastUseTimeStamp() {
        return last_use_time_stamp;
    }

    public void setLastUseTimeStamp(Date last_use_time_stamp) {this.last_use_time_stamp = last_use_time_stamp;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUseRank() {
        return use_rank;
    }

    public void setUseRank(int use_rank) {
        this.use_rank = use_rank;
    }



}
