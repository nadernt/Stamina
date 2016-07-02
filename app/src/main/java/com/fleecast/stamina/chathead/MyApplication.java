package com.fleecast.stamina.chathead;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import com.fleecast.stamina.models.MostUsedAndRecentAppsStruct;
import com.fleecast.stamina.models.AudioNoteInfoStruct;
import com.fleecast.stamina.models.TempNoteInfoStruct;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Prefs;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

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
    public TempNoteInfoStruct tmpTempNoteOnlyInfoStruct;
    private boolean byePassRecordBroadcastReceiverForOnce = false;

    private int indexSomethingIsPlaying = Constants.CONST_NULL_MINUS;
    private int currentMediaPosition;
    private int mediaDuration;
    private boolean isPlaying=false;
    private int playerServiceCurrentState =  Constants.CONST_NULL_MINUS;
    private boolean weHaveAnOpenNote = false;
    private int currentOpenedNoteId =  Constants.CONST_NULL_ZERO;
    private int lastOpenedNoteId =  Constants.CONST_NULL_ZERO;

    private boolean audioNoteSaved =false;
    private boolean playlistHasLoaded =false;

    public MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("DBG" ,"MyApplication");

        singleton = this;

        this.context = getApplicationContext();

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        //Realm configuration code
        RealmConfiguration configRealm = new RealmConfiguration.Builder(this)
                // Version of the database
                .name("megan.realm")
                .schemaVersion(0)
                .migration(new DataMigration())
                .build();

        Realm.setDefaultConfiguration(configRealm);


    }

     public Context getAppContext() {
        return this.context;
    }



    public void reInitEverything(){
        blLauncherDlgVisibility =false;
        appJustLaunchedByUser = 0 ;
        currentGroupFilter = -1;
        isUserTerminateApp =false;
        isAppsListIsLoading = false;
        mostUsedAppsStruct = new ArrayList<>();
        mostRecentUsedAppsStruct = new ArrayList<>();

    }

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

    public int getCurrentOpenedNoteId() {
        return currentOpenedNoteId;
    }

    public void setCurrentOpenedNoteId(int currentOpenedNoteId) {
        this.currentOpenedNoteId = currentOpenedNoteId;
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

    public boolean isPlaylistHasLoaded() {
        return playlistHasLoaded;
    }

    public void setPlaylistHasLoaded(boolean playlistHasLoaded) {
        this.playlistHasLoaded = playlistHasLoaded;
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
/*            private int id;
            private String title;
            private boolean has_audio;
            private Date create_time_stamp;
            private String description;
            private Date update_time;
            private Date start_time;
            private Date end_time;
            private int call_type;
            private int tag;
            private String phone_number;
            private int order;*/
            // Create a new schema if the version 0
            if (oldVersion == 0) {
                schema.create("NoteInfoRealmStruct")
                        .addField("id", Integer.class)
                        .addField("title", String.class)
                        .addField("description", String.class)
                        .addField("has_audio",boolean.class)
                        .addField("start_time", Date.class)
                        .addField("create_time_stamp", Date.class)
                        .addField("update_time", Date.class)
                        .addField("end_time", Date.class)
                        .addField("phone_number", String.class)
                        .addField("call_type", Integer.class)
                        .addField("tag", Integer.class)
                        .addField("order", Integer.class);
                oldVersion++;
            }

        }
    }
}