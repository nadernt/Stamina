package com.fleecast.stamina.notetaking.apis;

/**
 * Created by nnt on 5/06/16.
 */
public class AudioNoteInfoStruct {

    private String title;
    private String file_name;
    private String description;
    private int parent_db_id;
    private int tag;

    //public AudioNoteInfoStruct(int id,int parentDbId,String file_name, String title, String description,int tag) {
    public AudioNoteInfoStruct(String file_name, String title, String description) {
/*
        this.id = id;
        this. parent_db_id = parentDbId;
*/
        this.title = title;
        this.description = description;
        this.file_name = file_name;
        /*this.tag = tag;*/
    }

/*
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String audioFileTitle) {
        this.title = audioFileTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

   /* public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }*/

    public int getParentDbId() {
        return parent_db_id;
    }

    public void setParentDbId(int parent_db_id) {
        this.parent_db_id = parent_db_id;
    }

    public String getFileName() {
        return file_name;
    }

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

}
