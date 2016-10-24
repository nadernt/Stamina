package com.fleecast.stamina.backup;

import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;

import java.io.File;

/**
 * Created by nnt on 23/10/16.
 */
public class BackupFilesStruct {

    private int fileId;
    private int fileParentId;
    private String filePath;
    private File file;
    private boolean isFile;

    public BackupFilesStruct(File file) {
        this.file = file;
        this.filePath = file.getAbsolutePath();
        this.isFile = file.isFile();
        if(file.isFile()) {
//            !node.getAbsolutePath().equalsIgnoreCase(ExternalStorageManager.getWorkingDirectory()  + Constants.CONST_PHONE_CALLS_DIRECTORY_NAME) &&

                    //If it is not phonecall.
                    if(!file.getAbsolutePath().contains(Constants.CONST_PHONE_CALLS_DIRECTORY_NAME)) {
                        this.fileId = getDbIdFromFileName(file.getName());
                        this.fileParentId = getParentDbIdFromFileName(file.getName());
                    }
                    else
                    {
                        System.out.println(file.getName().substring(0,file.getName().lastIndexOf(".")));
                        this.fileId =  Integer.valueOf(file.getName().substring(0,file.getName().lastIndexOf(".")));
                    }
        }
        else
        {
            this.fileParentId = Integer.valueOf(file.getName());
        }
    }

    public File getFile() {
        return file;
    }

    public int getFileId() {
        return fileId;
    }

    public int getFileParentId() {
        return fileParentId;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isFile() {
        return isFile;
    }

    private int getDbIdFromFileName(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(file_name.lastIndexOf("_") + 1));
    }

    private int getParentDbIdFromFileName(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(0,file_name.lastIndexOf("_")));
    }
}
