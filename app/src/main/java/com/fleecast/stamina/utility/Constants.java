package com.fleecast.stamina.utility;

/**
 * Created by nnt on 27/03/16.
 */
public class Constants {
    public static String Fist_Initial_Of_APP = "FistInitialOfAPP";
    public static final int LIST_FOR_GRID = 0;
    public static final long PLAYER_PROGRESS_UPDATE_TIME = 100;
    public static final int AUDIO_RECORDING_NOTIFICATION_ID = 0;

    public static final String SORT_IS_ALPHABETIC_OR_DATE = "SortIsAlphabeticOrDate";

    public static int LIST_FOR_MOST_USE =1;
    public static int LIST_FOR_RECENT_USED =2;

    public static final int SIZE_OF_DRAWABLE_OF_EDITTEXT_FILTER = 32;
    public static String IS_ADD_OR_EDIT = "ADD_EDIT_GROUP";
    public static String ADD_GROUP_CODE_TO_EDIT = "ADD_GROUP_CODE_TO_EDIT";
    public static String ICON_NAME_FROM_ASSETS = "ICON_NAME_FROM_ASSETS";
    public static int REQUEST_CODE_ICON_CHOOSE_ACTIVITY = 2;
    public static int REQUEST_ADD_EDIT_GROUP_ACTIVITY = 3;
    public static String DEFAULT_APPS_ICON = "DEFAULT_ICON";

    public static int OPTION_MENU_SHOW_HIDE = 0;
    public static int OPTION_MENU_ENABLE_DISABLE = 1;
    public static int OPTION_MENU_CHECKED_UNCHECKED = 2;

    public static int APP_IS_IN_DEFAULT_GROUP =-1;
    public static int INIT_EVERYTHING_FROM_SCRATCH =-1;

    public static int ALL_APPS_DEFAULT_GROUP =-1;
    public static int APP_GROUPS_SETTING =0;

    public static int  START_ACTIVITY_FOR_POWER_POLICY =12345;

    public static int ICONS_RENDER_QUALITY_LOW = 32;
    public static int ICONS_RENDER_QUALITY_NOT_BAD = 48;
    public static int ICONS_RENDER_QUALITY_MEDIUM = 64;
    public static int ICONS_RENDER_QUALITY_HIGH = 96;
    public static int ICONS_RENDER_QUALITY_VERY_HIGH = 128;
    public static boolean ICONS_RENDER_QUALITY_ALIAS = true;
    public static boolean ICONS_RENDER_QUALITY_NO_ALIAS = false;

    public static long MINIMUM_AVAILABLE_STORAGE_SPACE = 20;
    public static final String WORKING_DIRECTORY = "/stamina";

    /**
     * Call constants.
     */
    public static int PHONE_OUT_GOING_CALL_STARTED = 0;
    public static final int PHONE_INCOMING_CALL_RECEIVED = 1;
    public static final int PHONE_INCOMING_CALL_ANSWERED = 2;
    public static int PHONE_MISSING_CALL = 3;
    public static final int PHONE_INCOMING_CALL_ENDED = 4;
    public static int PHONE_OUTGOING_CALL_ENDED = 5;

    public static int RECORDS_IS_OUTGOING = 0;
    public static int RECORDS_IS_INCOMING = 1;
    public static int RECORDS_IS_AUDIO = 2;

    /**
     * Chat Head Record Service Intents
     */
    public static String CHAT_HEAD_RECORD_INTENTS_STOP = "STOP";
    public static String CHAT_HEAD_RECORD_INTENTS_RECORD = "RECORD";
    public static String CHAT_HEAD_RECORD_INTENTS_CANCEL = "CANCEL";
    public static String CHAT_HEAD_RECORD_INTENTS_NUMBER = "NUMBER";
    public static String CHAT_HEAD_RECORD_INTENTS_INGOING_OUTGOING = "INGOING_OUTGOING";
    public static String CHAT_HEAD_RECORD_INTENTS_START_TIME = "START_TIME";
    public static String CHAT_HEAD_RECORD_INTENTS_END_TIME = "END_TIME";

    /**
     * Recorders Initials
     */
    public static String RECORDER_PHONE_RECORDER_SOURCE_OPTION = "PhoneRecorderSource";
    public static String RECORDER_PHONE_RECORDER_FORMAT_OPTION = "PhoneRecorderFormt";
    public static String RECORDER_PHONE_IS_RECORD = "IsRecordPhoneCall";

    public static String RECORDER_AUDIO_RECORDER_SOURCE_OPTION = "AudioRecorderSource";
    public static String RECORDER_AUDIO_RECORDER_QUALITY_OPTION = "AudioRecorderQuality";
    public static int RECORDER_AUDIO_RECORDER_QUALITY_LOW = 0;
    public static int RECORDER_AUDIO_RECORDER_QUALITY_MEDIUM = 1;
    public static int RECORDER_AUDIO_RECORDER_QUALITY_HIGH = 2;

    public static String RECORDER_AUDIO_FORMAT_3GP = "3gp";
    public static String RECORDER_AUDIO_FORMAT_WAV = "wav";
    public static String RECORDER_AUDIO_FORMAT_AAC = "aac";
    public static String RECORDER_AUDIO_FORMAT_AMR = "amr";

}
