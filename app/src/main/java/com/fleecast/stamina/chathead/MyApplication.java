package com.fleecast.stamina.chathead;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import com.fleecast.stamina.models.MostUsedAndRecentAppsStruct;
import com.fleecast.stamina.models.AudioNoteInfoStruct;
import com.fleecast.stamina.models.NoteInfoRealmStruct;
import com.fleecast.stamina.models.TempNoteInfoStruct;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Prefs;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.internal.Table;

/**
 * Created by nnt on 2/03/16.
 */
public class MyApplication extends Application{

    private static MyApplication singleton;
    private static Context context;
    private boolean blLauncherDlgVisibility =false;
    private int appJustLaunchedByUser = Constants.CONST_NULL_ZERO ;
    private int currentGroupFilter = Constants.CONST_NULL_MINUS;
    private List<MostUsedAndRecentAppsStruct> mostUsedAppsStruct = new ArrayList<>();
    private List<MostUsedAndRecentAppsStruct> mostRecentUsedAppsStruct = new ArrayList<>();
    private boolean isAppsListIsLoading = false;
    private boolean isUserTerminateApp =false;
    private int aRecordIsUnderGoing = 0;
    private boolean userWantsRecordPhoneCalls = false;
    public List <AudioNoteInfoStruct> stackPlaylist = new ArrayList<>();
    public TempNoteInfoStruct tmpCurrentAudioNoteInfoStruct;
    public TempNoteInfoStruct tmpCurrentTextNoteInfoStruct;
    private boolean byePassRecordBroadcastReceiverForOnce = false;

    private int indexSomethingIsPlaying = Constants.CONST_NULL_MINUS;
    private int currentMediaPosition;
    private int mediaDuration;
    private boolean isPlaying=false;
    private int playerServiceCurrentState =  Constants.CONST_NULL_MINUS;
    private boolean weHaveAnOpenNote = false;
    private int currentOpenedTextNoteId =  Constants.CONST_NULL_ZERO;
    private int currentRecordingAudioNoteId =  Constants.CONST_NULL_ZERO;
    private int lastOpenedNoteId =  Constants.CONST_NULL_ZERO;

    private boolean textNoteSaved =false;
    private boolean audioNoteSaved =false;
    private boolean playlistHasLoaded =false;
    private long recordTimeTick;

    public MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("DBG" ,"MyApplication");

        singleton = this;

        context = getApplicationContext();
        /*DataMigration dataMigration = new DataMigration()
                .migrate()
                .migration() // Migration to run instead of throwing an exception
                .build();*/
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        Realm.init(this);
        //Realm configuration code
        RealmConfiguration configRealm = new RealmConfiguration.Builder()
                // Version of the database
                .name("megan.realm")
                .schemaVersion(1)
                .migration(new DataMigration())
                .build();

        Realm.setDefaultConfiguration(configRealm);


    }

     public Context getAppContext() {
        return context;
    }



    /*public void reInitEverything(){
        blLauncherDlgVisibility =false;
        appJustLaunchedByUser = 0 ;
        currentGroupFilter = -1;
        isUserTerminateApp =false;
        isAppsListIsLoading = false;
        mostUsedAppsStruct = new ArrayList<>();
        mostRecentUsedAppsStruct = new ArrayList<>();

    }*/

    public boolean getIsAppsListLoading() {
        return isAppsListIsLoading;
    }

    public void setIsAppsListLoading(boolean isAppsListIsLoading) {
        this.isAppsListIsLoading = isAppsListIsLoading;
    }

    public List<MostUsedAndRecentAppsStruct> getMostUsedAppsStruct() {
        return mostUsedAppsStruct;
    }

    public void setMostUsedAppsStruct(List<MostUsedAndRecentAppsStruct> mostUsedAppsStruct) {
        this.mostUsedAppsStruct = mostUsedAppsStruct;
    }
    public List<MostUsedAndRecentAppsStruct> getRecentUsedAppsStruct() {
        return mostRecentUsedAppsStruct;
    }

    public void setRecentUsedAppsStruct(List<MostUsedAndRecentAppsStruct> mostRecentUsedAppsStruct) {
        this.mostRecentUsedAppsStruct = mostRecentUsedAppsStruct;
    }


    public boolean getLauncherDialogNotVisible() {
        return blLauncherDlgVisibility;
    }

    public void setLauncherDialogNotVisible(boolean blLauncherDlgVisibility) {
        this.blLauncherDlgVisibility = blLauncherDlgVisibility;
    }

    public boolean getIsUserTerminateApp() {
        return isUserTerminateApp;
    }

    public void setIsUserTerminateApp(boolean isUserTerminateApp) {
        this.isUserTerminateApp = isUserTerminateApp;
    }

    public int getAppJustLaunchedByUser() {
        return appJustLaunchedByUser++;
    }

    public int getCurrentGroupFilter() {
        return currentGroupFilter;
    }

    public int isRecordUnderGoing() {
        return aRecordIsUnderGoing;
    }

    public void setIsRecordUnderGoing(int aRecordIsUnderGoing) {
        this.aRecordIsUnderGoing = aRecordIsUnderGoing;
    }

    public void setCurrentGroupFilter(int currentGroupFilter) {
        this.currentGroupFilter = currentGroupFilter;
    }

    public boolean isUserWantsRecordPhoneCalls() {
        return userWantsRecordPhoneCalls;
    }

    public void setUserWantsRecordPhoneCalls(boolean userWantsRecordPhoneCalls) {
        this.userWantsRecordPhoneCalls = userWantsRecordPhoneCalls;
    }

    public int getIndexSomethingIsPlaying() {
        return indexSomethingIsPlaying;
    }

    public void setIndexSomethingIsPlaying(int indexSomethingIsPlaying) {
        this.indexSomethingIsPlaying = indexSomethingIsPlaying;
    }

    public int getCurrentMediaPosition() {
        return currentMediaPosition;
    }

    public void setCurrentMediaPosition(int mediaPosition) {
        currentMediaPosition = mediaPosition;
    }

    public int getMediaDuration() {
        return mediaDuration;
    }

    public void setMediaDuration(int mediaDuration) {
        this.mediaDuration = mediaDuration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public int getPlayerServiceCurrentState() {
        return playerServiceCurrentState;
    }

    public boolean isWeHaveAnOpenNote() {
        return weHaveAnOpenNote;
    }

    public void setWeHaveAnOpenNote(boolean weHaveAnOpenNote) {
        this.weHaveAnOpenNote = weHaveAnOpenNote;
    }

    public int getCurrentOpenedTextNoteId() {
        return currentOpenedTextNoteId;
    }

    public void setCurrentOpenedTextNoteId(int currentOpenedTextNoteId) {
        this.currentOpenedTextNoteId = currentOpenedTextNoteId;
    }

    public int getLastOpenedNoteId() {
        return lastOpenedNoteId;
    }

    public void setLastOpenedNoteId(int lastOpenedNoteId) {
        this.lastOpenedNoteId = lastOpenedNoteId;
    }

    public boolean isByePassRecordBroadcastReceiverForOnce() {
        return byePassRecordBroadcastReceiverForOnce;
    }

    public void setByePassRecordBroadcastReceiverForOnce(boolean byePassRecordBroadcastReceiverForOnce) {
        this.byePassRecordBroadcastReceiverForOnce = byePassRecordBroadcastReceiverForOnce;
    }

    public boolean isAudioNoteSaved() {
        return audioNoteSaved;
    }

    public void setAudioNoteSaved(boolean audioNoteSaved) {
        this.audioNoteSaved = audioNoteSaved;
    }

    public boolean isTextNoteSaved() {
        return textNoteSaved;
    }

    public void setTextNoteSaved(boolean textNoteSaved) {
        this.textNoteSaved = textNoteSaved;
    }


    public boolean isPlaylistHasLoaded() {
        return playlistHasLoaded;
    }

    public void setPlaylistHasLoaded(boolean playlistHasLoaded) {
        this.playlistHasLoaded = playlistHasLoaded;
    }

    public long getRecordTimeTick() {
        return recordTimeTick;
    }

    public void setRecordTimeTick(long recordTimeTick) {
        this.recordTimeTick = recordTimeTick;
    }

    public int getCurrentRecordingAudioNoteId() {
        return currentRecordingAudioNoteId;
    }

    public void setCurrentRecordingAudioNoteId(int currentRecordingAudioNoteId) {
        this.currentRecordingAudioNoteId = currentRecordingAudioNoteId;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("DBG" , "Application Class Destroyed." );
    }

    public void setPlayerServiceCurrentState(int playerServiceCurrentState) {
        this.playerServiceCurrentState = playerServiceCurrentState;
    }


    private class DataMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            Log.e("DBG", "Felta force!");

            // Take schema
            RealmSchema schema = realm.getSchema();
           /* if ( newVersion ==1) {
                RealmObjectSchema personSchema = schema.get("NoteInfoRealmStruct");
                personSchema
                        .addField("extras", String.class);

                oldVersion++;
            }


            // Create a new schema if the version 0
            if (newVersion==2) {


                RealmObjectSchema personSchema1 = schema.get("NoteInfoRealmStruct");
                personSchema1
                        .renameField("tag", "color");
                personSchema1
                        .addField("group", String.class);
                personSchema1
                        .addField("order", int.class);
                personSchema1
                        .addField("del", boolean.class);

                oldVersion++;
            }

            if (newVersion ==3) {

                RealmObjectSchema personSchema2 = schema.get("TodoParentRealmStruct");
                personSchema2
                        .addField("color", int.class);
                personSchema2
                        .addField("group", String.class);
                personSchema2
                        .addField("extras", String.class);
                personSchema2
                        .addField("del", boolean.class);
                personSchema2
                        .addField("order", int.class);

                RealmObjectSchema personSchema3 = schema.get("TodoChildRealmStruct");
                personSchema3
                        .addField("color", int.class);
                personSchema3
                        .addField("group", String.class);
                personSchema3
                        .addField("extras", String.class);
                personSchema3
                        .addField("del", boolean.class);

                oldVersion++;
            }*/
        }
    }
}