package com.fleecast.stamina.backup;

/**
 * Created by nnt on 9/12/16.
 */
public class NoteTypesAreInDatabase {
    private boolean weHaveAudio;
    private boolean weHaveText;
    private boolean weHaveTodo;
    private boolean weHavePhoneCall;

    public NoteTypesAreInDatabase(boolean weHaveText, boolean weHaveAudio, boolean weHaveTodo, boolean weHavePhoneCall) {
        this.weHaveText = weHaveText;
        this.weHaveAudio = weHaveAudio;
        this.weHaveTodo = weHaveTodo;
        this.weHavePhoneCall = weHavePhoneCall;
    }

    public boolean doWeHaveText() {
        return weHaveText;
    }

    public boolean doWeHaveAudio() {
        return weHaveAudio;
    }

    public boolean doWeHaveTodo() {
        return weHaveTodo;
    }

    public boolean doWeHavePhoneCall() {
        return weHavePhoneCall;
    }
}
