package com.fleecast.stamina.models;

/**
 * Created by nnt on 23/04/16.
 */
public class MostUsedAndRecentAppsStruct {

    private String title;
    private int appGroup;
    private String app_package_name;
    private int use_rank;

    public MostUsedAndRecentAppsStruct(String title,String app_package_name, int appGroup, int use_rank) {
        this.app_package_name = app_package_name;
        this.appGroup = appGroup;
        this.use_rank = use_rank;
        this.title =title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppPackageName() {
        return app_package_name;
    }

    public void setAppPackageName(String app_package_name) { this.app_package_name = app_package_name;}

    public int getUseRank() {
        return use_rank;
    }

    public void setUseRank(int use_rank) {

        this.use_rank = use_rank;
    }

    public int getAppGroup() {
        return appGroup;
    }

    public void setAppGroup(int appGroup) {
        this.appGroup = appGroup;
    }

}


