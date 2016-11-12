package com.fleecast.stamina.models;

/**
 * Created by nnt on 5/04/16.
 */
public class GroupsModel {

    private int id;
    private String title;
    private int app_group_code;
    private String icon_path;
    private int app_group_order;

    public GroupsModel(int id, String title, int app_group_code,String icon_path,int app_group_order) {
        this.app_group_code = app_group_code;
        this.id = id;
        this.title = title;
        this.icon_path = icon_path;
        this.app_group_order = app_group_order;
    }


    public int getAppGroupCode() {
        return app_group_code;
    }

    public void setAppGroupCode(int app_group_code) {
        this.app_group_code = app_group_code;
    }

    public String getGroupName() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon_path;
    }

    public void setIcon(String icon_path) {
        this.icon_path = icon_path;
    }

    public int getAppGroupOrder() {
        return app_group_order;
    }

    public void setAppGroupOrder(int app_group_order) {
        this.app_group_order = app_group_order;
    }
}
