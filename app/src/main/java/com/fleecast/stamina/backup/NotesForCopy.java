package com.fleecast.stamina.backup;

/**
 * Created by nnt on 28/11/16.
 */

public class NotesForCopy
{
    int id;
    int note_type;

    public NotesForCopy(int id, int note_type) {
        this.id = id;
        this.note_type = note_type;
    }

    public int getId() {
        return id;
    }

    public int getNote_type() {
        return note_type;
    }
}