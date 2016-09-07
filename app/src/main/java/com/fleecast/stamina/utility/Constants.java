package com.fleecast.stamina.utility;

import android.content.Context;
import android.content.IntentFilter;

/**
 * Created by nnt on 27/03/16.
 */
public class Constants {
    public static final int CONST_NULL_MINUS =  -1;
    public static final int CONST_NULL_ZERO =  0;
    public static final String CONST_STRING_NO_DESCRIPTION =  "No description";
    public static final String CONST_STRING_NO_TITLE =  "No title";
    public static final int RESULT_CODE_REQUEST_DIRECTORY = 123;
    public static final String EXTRA_RESULT_SELECTED_DIR = "SelectedWorkingDirectoryPath";
    public static final String CONST_WORKING_DIRECTORY_NAME = "/stamina";
    public static final String CONST_RECYCLEBIN_DIRECTORY_NAME = "/trash";
    public static final String CONST_PHONE_CALLS_DIRECTORY_NAME = "/phonecalls"; // My birthday as folder name
    public static final String CHATHEAD_X = "ChatHeadX";
    public static final String CHATHEAD_Y = "ChatHeadY";
    public static final String CONST_URL_HELPS = "http://stamina.help.s3-website-ap-southeast-2.amazonaws.com/index.html";
    public static final int CONST_NOTETYPE_TEXT = 0;
    public static final int CONST_NOTETYPE_AUDIO = 1;
    public static final int CONST_NOTETYPE_PHONECALL = 2;
    public static final int CONST_NOTETYPE_TODO = 3;



    public static final String EXTRA_RECORD_FILENAME = "ExtraRecordFilename";
    public static final String EXTRA_NEW_RECORD = "ExetraNewRecord";
    public static final String EXTRA_RECORD_SOURCE = "ExtraRecordSource";
    public static final String EXTRA_STOP_RECORD = "ExtraStopRecord";
    public static final String EXTRA_STOP_RECORD_SERVICE = "ExtraStopRecordService";
    public static final String EXTRA_CURRENT_DBID_RECORD_SERVICE = "ExtraCurrentDbidRecordService";
    public static final String INTENTFILTER_RECORD_SERVICE = "IntentfilterRecordService";
    public static final String EXTRA_RECORD_SERVICE_REPORTS = "ExtraRecordServiceError";
    public static final int REPORT_RECORD_ERROR_TO_ACTIVITY = 0;
    public static final int REPORT_RECORDED_FILE_TO_ACTIVITY = 1;
    public static final int REPORT_RECORD_STOPPED_BY_NOTIFICATION_TO_ACTIVITY = 2;
    public static final String REPORT_RECORDED_FILE_TO_ACTIVITY_FILENAME = "ReportRecordedFileToActivityFilename";

    public static final String INTENTFILTER_PLAYER_SERVICE = "IntentfilterPlayerService";
    public static final String EXTRA_PLAY_MEDIA_FILE_PORTRAIT_PLAYER = "ExtraPlayMediaFilePortrait";
    public static final String EXTRA_PORTRAIT_PLAYER_DBID = "ExtraPortraitPlayerDBID";
    public static final String EXTRA_PORTRAIT_PLAYER_DESCRIPTION = "ExtraPortraitPlayerDescription";
    public static final String EXTRA_PORTRAIT_PLAYER_TITLE = "ExtraPortraitPlayerTitle";


    public static final String ACTION_SHOW_PLAYER_NO_NEW = "com.fleecast.stamina.action.SHOW_PLAYER_NO_NEW";

    // Main Player Actions
    public static final String ACTION_CLOSE = "com.fleecast.stamina.action.CLOSE";
    public static final String ACTION_PLAY = "com.fleecast.stamina.action.PLAY";
    public static final String ACTION_PAUSE =  "com.fleecast.stamina.action.PAUSE";
    public static final String ACTION_NEXT =  "com.fleecast.stamina.action.SKIP";
    public static final String ACTION_STOP =  "com.fleecast.stamina.action.STOP";
    public static final String ACTION_REWIND =  "com.fleecast.stamina.action.REWIND";
    public static final String ACTION_SKIP =  "com.fleecast.stamina.action.SKIP";
    public static final String ACTION_TOGGLE_PLAYBACK = "com.fleecast.stamina.action.TOGGLE_PLAYBACK";

    // Legacy Player Actions
    public static final String ACTION_CLOSE_LEGACY = "com.fleecast.stamina.action.legacy.CLOSE";
    public static final String ACTION_PLAY_LEGACY = "com.fleecast.stamina.action.legacy.PLAY";
    public static final String ACTION_PAUSE_LEGACY =  "com.fleecast.stamina.action.legacy.PAUSE";
    public static final String ACTION_NEXT_LEGACY =  "com.fleecast.stamina.action.legacy.SKIP";
    public static final String ACTION_STOP_LEGACY =  "com.fleecast.stamina.action.legacy.STOP";
    public static final String ACTION_REWIND_LEGACY =  "com.fleecast.stamina.action.legacy.REWIND";
    public static final String ACTION_SKIP_LEGACY =  "com.fleecast.stamina.action.legacy.SKIP";
    public static final String ACTION_TOGGLE_PLAYBACK_LEGACY = "com.fleecast.stamina.action.legacy.TOGGLE_PLAYBACK";

    public static final String ACTION_STOP_RECORD =  "com.fleecast.stamina.action.STOP_RECORD";


    public static final String EXTRA_SEEK_TO = "ExtraSeekTo";
    public static final String EXTRA_UPDATE_SEEKBAR = "ExtraUpdateSeekBar";


    public static final String EXTRA_PLAY_NEW_SESSION =  "ExtraPlayNewSession";
    public static final String EXTRA_PLAY_REQUEST_ISـFROM_PORTRATE_PLAYER =  "ExtraPlayRequestIsFromPortratePlayer";
    public static final int ACTION_NULL =  -1;
    public static final String EXTRA_PLAYER_SERVICE_PLAY_STATUS = "ExtraPlayerServicePlayStarted";
    public static final int PLAYER_SERVICE_STATUS_ERROR = 0;
    public static final int PLAYER_SERVICE_STATUS_PLAYING = 1;
    public static final int PLAYER_SERVICE_STATUS_TRACK_FINISHED = 2;
    public static final int PLAYER_SERVICE_STATUS_PAUSE = 3;
    public static final int PLAYER_SERVICE_STATUS_TACK_CHANGED = 4;
    public static final int PLAYER_SERVICE_STATUS_SEEK_BAR_UPDATED = 5;
    public static final int PLAYER_SERVICE_STATUS_CLOSE_PLAYER = 6;
    public static final String EXTRA_PLAYER_SERVICE_TRACK_NUMBER = "ExtraPlayerServiceTrackNumber";

    public static final int CONST_PLAY_SERVICE_STATE_NOT_ALIVE = -1;
    public static final int CONST_PLAY_SERVICE_STATE_STOPPED = 0;
    public static final int CONST_PLAY_SERVICE_STATE_PAUSED = 1;
    public static final int CONST_PLAY_SERVICE_STATE_PLAYING = 2;
    // The volume we set the media player to when we lose audio focus, but are allowed to reduce
    // the volume instead of stopping playback.
    public static final float DUCK_VOLUME = 0.1f;
    public static final int RESULT_CODE_REQUEST_DIALOG = 123;
    public static final String EXTRA_AUDIO_NOTE_PARENT_DB_ID = "ExtraAudioNoteParentDbId";
    public static final String EXTRA_TODO_PARENT_DB_ID = "ExtraTodoParentDbId";
    public static final String EXTRA_AUDIO_NOTE_FILE_DB_ID = "ExtraAudioNoteFileDbId";
    public static final int CONST_PLAYER_LIST_TEXT_ELLLIPSIZE = 30;
    public static final String EXTRA_FOLDER_TO_PLAY_ID = "ExtraFolderToPlayId";
    public static final String EXTRA_TAKE_NEW_NOTE_AND_NO_RECORD = "ExtraTakeNoteAndNoRecord";
    public static final String EXTRA_TAKE_NEW_NOTE_AND_START_RECORD = "ExtraTakeNoteAndStartRecord";
    public static final String EXTRA_EDIT_NOTE_AND_RECORD = "ExtraEditNoteAndRecord";
    public static final String EXTRA_EDIT_NOTE_AND_NO_RECORD = "ExtraEditNoteAndNoRecord";
    public static final String EXTRA_EDIT_PHONE_RECORD_NOTE = "ExtraEditPhoneRecordNote";
    public static final String EXTRA_EDIT_PHONE_RECORD_NOTE_PHONE_NUMBER = "ExtraEditPhoneRecordNotePhoneNumber";

    public static final int CONST_IS_ONLY_TEXT = 0;
    public static final int CONST_IS_TEXT_AND_RECORD = 1;
    public static final int CONST_IS_EDIT_TEXT_AND_RECORD = 2;
    public static final int CONST_IS_EDIT_ONLY_TEXT = 3;
    public static final int CONST_RECORDER_SERVICE_IS_FREE = 0;
    public static final int CONST_RECORDER_SERVICE_WORKS_FOR_PHONE = 1;
    public static final int CONST_RECORDER_SERVICE_WORKS_FOR_NOTE = 2;
    public static final String EXTRA_NOTE_DB_ID_FOR_EDIT = "ExtraNoteDbIdForEdit";

    /**
     * Share and Social
     */
    public static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    public static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    public static final int PROTOCOL_VERSION = 20150314;
    public static final String YOUR_APP_ID = "280710328947930";
    public static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;
    public static final int TRANSIATION_TIME = 5000;

    //Notes List Constants
    public static final int CONST_SEARCH_NOTE_TITLE_AND_DESCRIPTION = 0;
    public static final int CONST_SEARCH_NOTE_TIME = 1;
    public static final int CONST_SEARCH_NOTE_CONTACTS = 2;
    public static final boolean CONST_NOTELIST_ACCEDING = false;
    public static final boolean CONST_NOTELIST_DESCENDING = true;
    public static final String PREF_NOTELIST_SEARCH_FILTER = "PrefNotelistSearchFilter";
    public static final String PREF_NOTELIST_SEARCH_SORT_OPTION = "PrefNotelistSearchSortOption";
    public static final String PREF_NOTELIST_SHOW_TEXT_NOTE = "PrefNotelistShowTextNote";
    public static final String PREF_NOTELIST_SHOW_AUDIO_NOTE = "PrefNotelistShowAudioNote";
    public static final String PREF_NOTELIST_SHOW_PHONE_RECORD = "PrefNotelistShowPhoneRecords";
    public static final int RESULT_CODE_REQUEST_LIST = 777;
    public static final String PHONE_RECORDER_CHATHEAD_Y = "PhoneRecorderChatheadY";
    public static final String PHONE_RECORDER_CHATHEAD_X = "PhoneRecorderChatheadX";



    //public static final String EXTRA_KILL_AND_RESTART_ME = "ExtraKillAndRestartMe";


    public static String PREF_FIRST_INITIAL_OF_APP = "FistInitialOfAPP";
    public static final String PREF_AUTO_RUN_PLAYER_ON_START = "PrefAutoRunPlayerOnStart";
    public static final String PREF_AUTO_RUN_RECORDER_ON_AUDIO_NOTES = "AutoRunRecorderOnAudioNotes";
    //public static final String PREF_ON_FINISH_PLAYLIST_CLOSE_PLAYER_REMOTE = "OnFinishListClosePlayerRemote";
    public static final String PREF_SHOW_PLAYER_FULL_NOTIFICATION = "ShowPlayerFullNnotification";
    public static final String PREF_WORKING_DIRECTORY_PATH = "WorkingDirectoryPath";
    public static final String PREF_SORT_IS_ALPHABETIC_OR_DATE = "SortIsAlphabeticOrDate";
    public static final String PREF_GROUP_ICON_SIZE = "PrefGroupIconSize";



    public static final int LIST_FOR_GRID = 0;
    public static final long CONST_PLAYER_PROGRESS_UPDATE_TIME = 100;
    public static final int AUDIO_RECORDING_NOTIFICATION_ID = 0;


    public static final String PREF_FIRST_QUESTION_PHONERECORDING = "PrefFirstQuestionPhonerecording";


    public static int LIST_FOR_MOST_USE =1;
    public static int LIST_FOR_RECENT_USED =2;

    public static final int SIZE_OF_DRAWABLE_OF_EDITTEXT_FILTER = 32;
    public static String IS_ADD_OR_EDIT = "AddEditGroup";
    public static String ADD_GROUP_CODE_TO_EDIT = "AddGroupCodeToEdit";
    public static String ICON_NAME_FROM_ASSETS = "IconNameFromAssets";
    public static int REQUEST_CODE_ICON_CHOOSE_ACTIVITY = 2;
    public static int REQUEST_ADD_EDIT_GROUP_ACTIVITY = 3;
    public static String DEFAULT_APPS_ICON = "DefaultIcon";

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


    /**
     * Call constants.
     */
    public static int PHONE_THIS_IS_NOT_A_PHONE_CALL = -1;
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

    public static String RECORDER_AUDIO_FORMAT_3GP = ".3gp";
    public static String RECORDER_AUDIO_FORMAT_WAV = ".wav";
    public static String RECORDER_AUDIO_FORMAT_AAC = ".aac";
    public static String RECORDER_AUDIO_FORMAT_AMR = ".amr";

    public static String CONST_SEPARATOR_OF_AUDIO_FILE = "_";
    public static int RECORD_INFINIT_UP_STOP = 1;
    public static int RECORD_STOP = 2;
    public static int RECORD_TAP_KEEP = 3;

    public static String TEMP_FOLDER_NAME ="temporary";

    public static final int RESULT_CODE_REQUEST_MAP_ACTIVITY = 111;
    public static final String EXTRA_MAP_LNG = "ExtraMapLatlng";
    public static final String EXTRA_MAP_LAT = "ExtraMapLat";
    public static final String EXTRA_MAP_ADDRESS = "ExtraMapAddress";
    public static final String EXTRA_MAP_PHONENUMBER = "ExtraMapPhoneNumber";
    public static final String EXTRA_MAP_WEBSITE = "ExtraMapWebSite";
    public static final String EXTRA_MAP_PLACENAME = "ExtraMapName";
    public static final String EXTRA_MAP_FULL_INFO = "ExtraMapFullInfo";
    public static final String EXTRA_ADDED_EVENT_TITLE = "ExtraAddedEventTitle";



}
