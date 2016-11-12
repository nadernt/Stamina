package com.fleecast.stamina.launcher;

import android.content.pm.PackageManager;

/**
 * Created by nnt on 30/03/16.
 */
public class PackageManagerStruct {

    private PackageManager packageManager;
    private int command;

    public PackageManagerStruct(PackageManager packageManager, int command){

        this.packageManager = packageManager;

        this.command = command;

    }

    public int getCommand() {
        return command;
    }

    public PackageManager getPackageManager() {
        return packageManager;
    }

}
