package com.fleecast.stamina.models;

/**
 * Created by nnt on 30/04/16.
 */
public class ActivityInfoStruct {

    private final String packageName;
    private final String name;


    public ActivityInfoStruct(String packageName,String name ){
        this.packageName = packageName;
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

}
