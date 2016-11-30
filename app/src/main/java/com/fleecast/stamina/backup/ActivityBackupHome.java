package com.fleecast.stamina.backup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;
import com.fleecast.stamina.R;
import com.fleecast.stamina.models.NoteInfoRealmStruct;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.settings.ActivityChooseDirectory;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;
import com.fleecast.stamina.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.NoSuchPaddingException;

import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityBackupHome extends DropboxActivity {
    private RealmNoteHelper realmNoteHelper;

    private Button dropbox_login_button;
    private Button btnCreateBackUp;
    private ArrayList<BackupFilesStruct> backupFilesStruct = new ArrayList<>();
    private ArrayList<String> cloudPaths = new ArrayList<>();
    private ArrayList<String> cloudPathsAudioFiles = new ArrayList<>();
    private ArrayList<String> cloudPathsJournalFiles = new ArrayList<>();
    private int indexFiles;
    MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private Button btnCopyToCloud;
    private CheckBox chkEncrypt;
    private ImageView imgBtnRemoveKey;
    private Button btnImportBackup;
    private CheckBox chkExmportTextNotes;
    private CheckBox chkExportAudioNotes;
    private CheckBox chkExportPhoneCalls;
    private CheckBox chkExportTodos;
    private CheckBox chkRestoreTextNotes;
    private CheckBox chkRestoreAudioNotes;
    private CheckBox chkRestorPhonecalls;
    private CheckBox chkRestoreTodos;
    private CheckBox chkMergeDestination;
    private CheckBox chkReplaceLocalDB;
    private ImageView imgRestorHelp;
    private CheckBox chkReportAudioNotes;
    private CheckBox chkReportTextNotes;
    private CheckBox chkReportPhonecalls;
    private CheckBox chkReportTodos;
    private CheckBox chkCompressToZip;
    private CheckBox chkTabular;
    private Button btnChooseReportPath;
    private Button btnCreateReport;

    private EditText editTxtReportTitle;
    private EditText editTxtReportDescription;
    private RadioGroup rdoReportOption;
    private String strReportPath;
    private CheckBox chkReportTimeStamp;
    private EditText editTxtReportAuthorName;
    private RadioButton rdoCSVReport;
    private RadioButton rdoHtmlReport;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_home);

        realmNoteHelper = new RealmNoteHelper(ActivityBackupHome.this);

        dropbox_login_button = (Button) findViewById(R.id.dropbox_login_button);
        btnImportBackup = (Button) findViewById(R.id.btnImportBackup);
        btnCreateBackUp = (Button) findViewById(R.id.btnCreateBackUp);
        btnCopyToCloud = (Button) findViewById(R.id.btnCopyToCloud);
        btnChooseReportPath = (Button) findViewById(R.id.btnChooseReportPath);
        btnCreateReport = (Button) findViewById(R.id.btnCreateReport);

        chkEncrypt = (CheckBox) findViewById(R.id.chkEncrypt);
        chkExmportTextNotes = (CheckBox) findViewById(R.id.chkExportTextNotes);
        chkExportAudioNotes = (CheckBox) findViewById(R.id.chkExportAudioNotes);
        chkExportPhoneCalls = (CheckBox) findViewById(R.id.chkExortPhoneCalls);
        chkExportTodos = (CheckBox) findViewById(R.id.chkExportTodos);

        chkRestoreTextNotes = (CheckBox) findViewById(R.id.chkRestoreTextNotes);
        chkRestoreAudioNotes = (CheckBox) findViewById(R.id.chkRestoreAudioNotes);
        chkRestorPhonecalls = (CheckBox) findViewById(R.id.chkRestorPhonecalls);
        chkRestoreTodos = (CheckBox) findViewById(R.id.chkRestoreTodos);
        chkMergeDestination = (CheckBox) findViewById(R.id.chkMergeDestination);
        chkReplaceLocalDB = (CheckBox) findViewById(R.id.chkReplaceLocalDB);

        chkReportAudioNotes = (CheckBox) findViewById(R.id.chkReportAudioNotes);
        chkReportTextNotes = (CheckBox) findViewById(R.id.chkReportTextNotes);
        chkReportPhonecalls = (CheckBox) findViewById(R.id.chkReportPhonecalls);
        chkReportTodos = (CheckBox) findViewById(R.id.chkReportTodos);
        chkCompressToZip = (CheckBox) findViewById(R.id.chkCompressToZip);
        chkTabular = (CheckBox) findViewById(R.id.chkTabular);
        chkReportTimeStamp= (CheckBox) findViewById(R.id.chkReportTimeStamp);


        rdoCSVReport= (RadioButton) findViewById(R.id.rdoCSVReport);
        rdoHtmlReport= (RadioButton) findViewById(R.id.rdoHtmlReport);

        imgBtnRemoveKey = (ImageView) findViewById(R.id.imgBtnRemoveKey);
        imgRestorHelp = (ImageView) findViewById(R.id.imgRestorHelp);

        rdoReportOption = (RadioGroup) findViewById(R.id.rdoReportOption);

        /*// get selected radio button from radioGroup
        int selectedId = rdoReportOption.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        rdoReportOption = (RadioButton) findViewById(selectedId);*/

        editTxtReportTitle = (EditText) findViewById(R.id.editTxtReportTitle);
        editTxtReportTitle.setText("Report");

        editTxtReportDescription = (EditText) findViewById(R.id.editTxtReportDescription);
        editTxtReportAuthorName = (EditText) findViewById(R.id.editTxtReportAuthorName);

        if (BackupEncrypt.isThereEncryptKey(ActivityBackupHome.this)) {
            chkEncrypt.setChecked(true);

            Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, true);

        } else {
            chkEncrypt.setChecked(false);
            Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false);
            imgBtnRemoveKey.setVisibility(View.GONE);


        }

        imgRestorHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.showMessage("Important notes:\n-Delete and replace will not keep your device database information.\n-Backup process ignores the empty todo and audio notes.","Note",ActivityBackupHome.this);
            }
        });

        imgBtnRemoveKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAuthentications(EncryptionDialogOption.REMOVE_ENCRYPT_KEY, null);
            }
        });



        rdoCSVReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rdoCSVReport.isChecked()){
                    chkTabular.setChecked(false);
                    chkTabular.setEnabled(false);
                    editTxtReportAuthorName.setEnabled(false);
                    chkReportTimeStamp.setChecked(false);
                    chkReportTimeStamp.setEnabled(false);
                    editTxtReportTitle.setEnabled(false);
                    editTxtReportDescription.setEnabled(false);
                }
            }
        });

        rdoHtmlReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rdoHtmlReport.isChecked()){
                    chkTabular.setEnabled(true);
                    editTxtReportAuthorName.setEnabled(true);
                    chkReportTimeStamp.setEnabled(true);
                    editTxtReportTitle.setEnabled(true);
                    editTxtReportDescription.setEnabled(true);
                }
            }
        });

       /* imgBtnRemoveKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!BackupEncrypt.isThereEncryptKey(ActivityBackupHome.this)) {

                    doAuthentications(EncryptionDialogOption.NEW_PASSWORD, null);
                } else {
                    doAuthentications(EncryptionDialogOption.JUST_ENTER_PASS, null);
                }


            }
        });*/


        chkEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!BackupEncrypt.isThereEncryptKey(ActivityBackupHome.this)) {
                    doAuthentications(EncryptionDialogOption.NEW_PASSWORD, null);
                } else {
                    doAuthentications(EncryptionDialogOption.JUST_ENTER_PASS, null);
                }
            }
        });


        chkReplaceLocalDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chkReplaceLocalDB.isChecked()) {
                    chkMergeDestination.setEnabled(false);
                    chkMergeDestination.setChecked(false);
                }else {
                    chkMergeDestination.setEnabled(true);
                }
            }
        });

        chkMergeDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkReplaceLocalDB.setChecked(false);
            }
        });

        btnCopyToCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(Constants.CONST_URL_DROPBOX)
                        .build();

                client.newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(final Call call, IOException e) {
                                // Error

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utility.showMessage("Error to connect to service", "Error", ActivityBackupHome.this);
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {

                                if (response.networkResponse().code() == 200)
                                    populateDropBoxUploadList();
                            }
                        });

            }
        });

        btnCreateBackUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (!chkExportAudioNotes.isChecked() && !chkExportPhoneCalls.isChecked() && !chkExmportTextNotes.isChecked() && !chkExportTodos.isChecked()) {
                    Utility.showMessage("You should choose at least one type of note", "Note", ActivityBackupHome.this);
                    return;
                }

                try {

                    if (Prefs.getBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false)) {
                        doAuthentications(EncryptionDialogOption.AUTHENTICATE_ENCRYPT, null);


                    } else {
                        FileBackUpParameters fileBackUpParameters = new FileBackUpParameters(false, null);
                        new LongFileBackupOperation().execute(fileBackUpParameters);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        btnImportBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!chkRestoreAudioNotes.isChecked() && !chkRestorPhonecalls.isChecked() && !chkRestoreTextNotes.isChecked() && !chkRestoreTodos.isChecked()) {
                    Utility.showMessage("You should choose at least one type of note", "Note", ActivityBackupHome.this);
                    return;
                }

                Intent intent = new Intent(ActivityBackupHome.this, ActivityJurnalFiles.class);

                startActivityForResult(intent, 333);
            }
        });

        btnCreateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editTxtReportTitle==null || editTxtReportTitle.getText().toString().trim().length() == 0)
                {
                    Utility.showMessage("Type a title for report!", "Note", ActivityBackupHome.this);
                    return;
                }

                int simpleHtmlTabularHTMLOrCSV = 0;

                if(!chkTabular.isChecked() && rdoHtmlReport.isChecked())
                    simpleHtmlTabularHTMLOrCSV = 0;
                else if(chkTabular.isChecked() && rdoHtmlReport.isChecked())
                simpleHtmlTabularHTMLOrCSV = 1;
                else if(rdoCSVReport.isChecked())
                    simpleHtmlTabularHTMLOrCSV = 2;

                ReportParameters reportParameters = new ReportParameters(
                        chkReportTextNotes.isChecked(),
                        chkReportAudioNotes.isChecked(),
                        chkReportPhonecalls.isChecked(),
                        chkReportTodos.isChecked(),
                        chkReportTimeStamp.isChecked(),
                        editTxtReportAuthorName.getText().toString().trim(),
                        editTxtReportTitle.getText().toString().trim(),
                        editTxtReportDescription.getText().toString().trim(),
                        simpleHtmlTabularHTMLOrCSV);

                new LongReportOperation().execute(reportParameters);

            }
        });


        dropbox_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasToken()) {
                    Auth.startOAuth2Authentication(ActivityBackupHome.this, getString(R.string.app_key));
                } else {
                    SharedPreferences prefs = getSharedPreferences("stamina-dropbox", MODE_PRIVATE);
                    String accessToken = prefs.getString("access-token", null);
                    //if (accessToken == null) {
                    //    accessToken = Auth.getOAuth2Token();
                    //    if (accessToken != null) {
                    prefs.edit().remove("access-token").apply();
                }

            }
        });

        btnChooseReportPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityBackupHome.this, ActivityChooseDirectory.class);
                startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_DIRECTORY);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constants.RESULT_CODE_REQUEST_DIRECTORY == requestCode){
            if(data!=null) {
                strReportPath = data.getStringExtra(Constants.EXTRA_RESULT_SELECTED_DIR);
/*
                System.out.println(strReportPath + "  KKKKKKKKKK");
*/
            }
        }

        if (Constants.CONST_DILAOG_CHOOSE_BACKUPFILE == requestCode)
            if (resultCode == Activity.RESULT_OK) {
                final String s = data.getStringExtra(Constants.EXTRA_BACKUP_FILENAME);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                if (s.contains("_encrypted")) {
                                    doAuthentications(EncryptionDialogOption.AUTHENTICATE_DECRYPT, s);
                                } else {

                                    FileBackUpDepackParameters fileBackUpDepackParameters = new FileBackUpDepackParameters(false, null, s);

                                    new LongFileBackupDepackOperation().execute(fileBackUpDepackParameters);

                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityBackupHome.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
    }

    private class ReportParameters {

        private boolean textNotes;
        private boolean audioNotes;
        private boolean phoneCalls;
        private boolean toDoNote;
        private boolean includeTimeStamp;
        private String authorName;
        private int simpleHtmlTabularHTMLOrCSV;
        private String title;
        private String description;



        public ReportParameters(boolean textNotes, boolean audioNotes, boolean phoneCalls, boolean toDoNote, boolean includeTimeStamp, String authorName, String title, String description, int simpleHtmlTabularHTMLOrCSV) {
            this.textNotes = textNotes;
            this.audioNotes = audioNotes;
            this.phoneCalls = phoneCalls;
            this.toDoNote = toDoNote;
            this.includeTimeStamp = includeTimeStamp;
            this.authorName = authorName;
            this.title = title;
            this.description=description;
            this.simpleHtmlTabularHTMLOrCSV = simpleHtmlTabularHTMLOrCSV;
        }

        public boolean isTextNotes() {
            return textNotes;
        }

        public boolean isAudioNotes() {
            return audioNotes;
        }

        public boolean isPhoneCalls() {
            return phoneCalls;
        }

        public boolean isToDoNote() {
            return toDoNote;
        }

        public boolean isIncludeTimeStamp() {
            return includeTimeStamp;
        }

        public String getAuthorName() {
            return authorName;
        }

        public int getSimpleHtmlTabularHTMLOrCSV() {
            return simpleHtmlTabularHTMLOrCSV;
        }
        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }


    private class LongReportOperation extends AsyncTask<ReportParameters, Void, Boolean> {


        private ProgressDialog dialog;
        private String strReportSimpleHtml = "";
        private Report report;
        private RealmResults<NoteInfoRealmStruct> allNotes;
        private ArrayList<NotesForCopy> notesListForCopyFile;
        private String strOutFileDirectory;

        @Override
        protected Boolean doInBackground(ReportParameters... reportParameterses) {



            File f = new File(strOutFileDirectory);

            if(!f.exists()) {
                f.mkdir();
            }
            else{
                Utility.deleteRecursive(new File(strOutFileDirectory),new File(strOutFileDirectory));
            }

            try {
                File root = new File(strOutFileDirectory);
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "index.html");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(strReportSimpleHtml);
                writer.flush();
                writer.close();



                if(reportParameterses[0].isAudioNotes())
                    copyAudioNoteFilesForReport(strOutFileDirectory, notesListForCopyFile);

                if(reportParameterses[0].isPhoneCalls())
                    copyPhoneCallsFilesForReport(strOutFileDirectory);

                //Copy asset files of html like css,js etc.
                Utility.copyAssetFolder(ActivityBackupHome.this.getAssets(),Constants.CONST_TEMPLATE_DIRECTORY + File.separator + "assets",
                        strOutFileDirectory + File.separator +  "assets");

                return true;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }


        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                dialog.cancel();
                Utility.showMessage("Report was successful.", "Info", ActivityBackupHome.this);
            }
            else {
                dialog.cancel();
                Utility.showMessage("Report wasn't successful!", "Error", ActivityBackupHome.this);
            }

        }


        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ActivityBackupHome.this, "",
                    "Loading. Please wait...", true);

            strOutFileDirectory = ExternalStorageManager.getWorkingDirectory() + Constants.CONST_REPORT_DIRECTORY_NAME;

            report = new Report(ActivityBackupHome.this);

            strReportSimpleHtml = report.getReportFromDBSimpleHtml(chkReportTextNotes.isChecked(),
                    chkReportAudioNotes.isChecked(),
                    chkReportPhonecalls.isChecked(),
                    chkReportPhonecalls.isChecked());


            if(editTxtReportTitle!=null || editTxtReportTitle.getText().toString().trim().length() >0)
            {
                String strTitle = "<h4 class='title_author'>" + editTxtReportTitle.getText().toString() + "</h4>";
                strReportSimpleHtml = strReportSimpleHtml.replaceFirst("\\B#TITLE#\\B", strTitle);
            }
            else
            {
                strReportSimpleHtml = strReportSimpleHtml.replaceFirst("\\B#TITLE#\\B", "");
            }

            if(chkReportTimeStamp.isChecked()){
                String strDateStamp = "<h4 class='title_timestamp'>" + Utility.unixTimeToReadable(new Date().getTime()/1000) + "</h4>";
                strReportSimpleHtml = strReportSimpleHtml.replaceFirst("\\B#TIME_STAMP#\\B", strDateStamp);
            }
            else {
                strReportSimpleHtml = strReportSimpleHtml.replaceFirst("\\B#TIME_STAMP#\\B", "");
            }


            if(editTxtReportAuthorName!=null || editTxtReportAuthorName.getText().toString().trim().length() >0)
            {
                String strAuthorName = "<h4 class='title_author'>" + editTxtReportAuthorName.getText().toString() + "</h4>";
                strReportSimpleHtml = strReportSimpleHtml.replaceFirst("\\B#AUTHOR#\\B", strAuthorName);
            }
            else
            {
                strReportSimpleHtml = strReportSimpleHtml.replaceFirst("\\B#AUTHOR#\\B", "");
            }

            if(editTxtReportDescription!=null || editTxtReportDescription.getText().toString().trim().length() >0)
            {
                String strReportDescription = "<div class='title_description'>" + editTxtReportDescription.getText().toString() + "</div>";
                strReportSimpleHtml = strReportSimpleHtml.replaceFirst("\\B#DESCRIPTIONS#\\B", strReportDescription);
            }
            else
            {
                strReportSimpleHtml = strReportSimpleHtml.replaceFirst("\\B#DESCRIPTIONS#\\B", "");
            }

            allNotes = realmNoteHelper.getAllNotes();

            if(chkReportPhonecalls.isChecked() || chkReportAudioNotes.isChecked()) {
                notesListForCopyFile = new ArrayList<>();

                for (int i = 0; i < allNotes.size(); i++)
                    notesListForCopyFile.add(new NotesForCopy(allNotes.get(i).getId(), allNotes.get(i).getNoteType()));
            }


        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }



    public void copyAudioNoteFilesForReport(String strOutFileDirectory,ArrayList<NotesForCopy> notesForCopies ) throws IOException {

        if (notesForCopies.size() == 0) {
            throw new NegativeArraySizeException();
        } else {

            for (int i = 0; i < notesForCopies.size(); i++) {
                if ((notesForCopies.get(i).getNote_type() == Constants.CONST_NOTETYPE_AUDIO)) {
                    File f = new File(String.valueOf(ExternalStorageManager.getWorkingDirectory() +
                             File.separator  +   notesForCopies.get(i).getId()));
                    Utility.copyDirectory(f,new File(strOutFileDirectory+File.separator + "audio" ));
                }
            }
        }
    }

    public void copyPhoneCallsFilesForReport(String strOutFileDirectory ) throws IOException {
                    File f = new File(String.valueOf(ExternalStorageManager.getWorkingDirectory() +
                            File.separator  +   Constants.CONST_PHONE_CALLS_DIRECTORY_NAME));
                    Utility.copyDirectory(f,new File(strOutFileDirectory + File.separator + "phonecalls" ));

    }

    enum EncryptionDialogOption {
        NEW_PASSWORD, CHANGE_OLD_PASS, JUST_ENTER_PASS, REMOVE_ENCRYPT_KEY, AUTHENTICATE_ENCRYPT, AUTHENTICATE_DECRYPT
    }

    private boolean doAuthentications(final EncryptionDialogOption encryptionDialogOption, final String fileNameToUnbackUpAndDecrypt) {

        final boolean keepStateOfEncryptKeyOnCancel =  Prefs.getBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false);

       // chkEncrypt.setChecked(false);
        boolean returnResult = false;
        LayoutInflater inflater = LayoutInflater.from(ActivityBackupHome.this);

        View alertLayout = inflater.inflate(R.layout.password_dialog, null);

        final EditText txtFirstPassword = (EditText) alertLayout.findViewById(R.id.txtFirstPassword);
        final EditText txtSecondPassword = (EditText) alertLayout.findViewById(R.id.txtSecondPassword);
        final CheckBox cbShowPassword = (CheckBox) alertLayout.findViewById(R.id.chkShowPassword);
        final EditText txtOldPassword = (EditText) alertLayout.findViewById(R.id.txtOldPassword);
        final LinearLayout layoutOldPass = (LinearLayout) alertLayout.findViewById(R.id.layoutOldPass);
        final LinearLayout layoutNewPass = (LinearLayout) alertLayout.findViewById(R.id.layoutNewPass);
        final LinearLayout layoutNewPassRepeat = (LinearLayout) alertLayout.findViewById(R.id.layoutNewPassRepeat);
        final TextView txtViewPassDialogComments = (TextView) alertLayout.findViewById(R.id.txtViewPassDialogComments);

        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // to encode password in dots
                    txtSecondPassword.setTransformationMethod(null);
                    txtOldPassword.setTransformationMethod(null);
                    txtFirstPassword.setTransformationMethod(null);
                } else {
                    // to display the password in normal text
                    txtSecondPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtFirstPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(ActivityBackupHome.this);

        alert.setTitle("Enter Password");

        switch (encryptionDialogOption) {
            case AUTHENTICATE_ENCRYPT:
            case AUTHENTICATE_DECRYPT:
                layoutOldPass.setVisibility(View.GONE);
                layoutNewPassRepeat.setVisibility(View.GONE);
                txtViewPassDialogComments.setVisibility(View.GONE);
                break;
            case REMOVE_ENCRYPT_KEY:
                layoutOldPass.setVisibility(View.GONE);
                layoutNewPassRepeat.setVisibility(View.VISIBLE);
                layoutNewPassRepeat.setVisibility(View.GONE);
                txtViewPassDialogComments.setText("Enter current password in order to remove the key!");
                break;
            case JUST_ENTER_PASS:
                layoutOldPass.setVisibility(View.GONE);
                layoutNewPassRepeat.setVisibility(View.GONE);
                txtViewPassDialogComments.setVisibility(View.GONE);
                break;
            case CHANGE_OLD_PASS:
                txtViewPassDialogComments.setText("(Note: don't use any language except english! Passwords must " +
                        "not be more than 10 and less than 3 characters.)");
                alert.setTitle("Change Password");
                break;
            case NEW_PASSWORD:
                txtViewPassDialogComments.setText("You don't have any master password in this device. Please create a new password.\n" +
                        "If you have multiple devices use same password for all devices.\n" +
                        "(Note: don't use any language except english! Passwords must not be more than 10 and less than 3 characters.)");
                alert.setTitle("New Password");
                layoutOldPass.setVisibility(View.GONE);
                break;
        }


        alert.setView(alertLayout);

        alert.setCancelable(false);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!keepStateOfEncryptKeyOnCancel)
                    chkEncrypt.setChecked(false);
                else
                    chkEncrypt.setChecked(true);
            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        final AlertDialog dialog = alert.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;

                String strFirstPass = txtFirstPassword.getText().toString().trim();
                String strSecondPass = txtSecondPassword.getText().toString().trim();
                String strOldPass = txtOldPassword.getText().toString().trim();

                switch (encryptionDialogOption) {
                    case AUTHENTICATE_ENCRYPT:

                        chkEncrypt.setChecked(true);

                        if (empty(strFirstPass)) {
                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Empty field!</font>"));
                            return;
                        }

                        if (strFirstPass.length() < Constants.MIN_PASSWORD_LENGTH || strFirstPass.length() > Constants.MAX_PASSWORD_LENGTH) {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Password must not be more than 10 and less than 3 characters!</font>"));
                            return;

                        }


                        if (BackupEncrypt.testEncryptKey(ActivityBackupHome.this, strFirstPass)) {
                            wantToCloseDialog = true;
                            FileBackUpParameters fileBackUpParametersEncry = new FileBackUpParameters(true, strFirstPass);
                            new LongFileBackupOperation().execute(fileBackUpParametersEncry);

                        } else {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Wrong password!</font>"));

                        }


                        break;

                    case AUTHENTICATE_DECRYPT:

                        chkEncrypt.setChecked(true);

                        if (empty(strFirstPass)) {
                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Empty field!</font>"));
                            return;
                        }

                        if (strFirstPass.length() < Constants.MIN_PASSWORD_LENGTH || strFirstPass.length() > Constants.MAX_PASSWORD_LENGTH) {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Password must not be more than 10 and less than 3 characters!</font>"));
                            return;

                        }


                        if (BackupEncrypt.testEncryptKey(ActivityBackupHome.this, strFirstPass)) {

                            wantToCloseDialog = true;

                            FileBackUpDepackParameters fileBackUpDepackParameters = new FileBackUpDepackParameters(true, strFirstPass, fileNameToUnbackUpAndDecrypt);
                            new LongFileBackupDepackOperation().execute(fileBackUpDepackParameters);

                        } else {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Wrong password!</font>"));

                        }



                        break;
                    case REMOVE_ENCRYPT_KEY:

                        if (empty(strFirstPass)) {
                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Empty field!</font>"));
                            return;
                        }

                        if (strFirstPass.length() < Constants.MIN_PASSWORD_LENGTH || strFirstPass.length() > Constants.MAX_PASSWORD_LENGTH) {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Password must not be more than 10 and less than 3 characters!</font>"));
                            return;

                        }

                        if (BackupEncrypt.testEncryptKey(ActivityBackupHome.this, strFirstPass)) {

                            Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false);
                            chkEncrypt.setChecked(false);

                            wantToCloseDialog = true;

                        } else {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Wrong password!</font>"));

                        }

                        if (wantToCloseDialog) {
                            imgBtnRemoveKey.setVisibility(View.GONE);

                            if (BackupEncrypt.isThereEncryptKey(ActivityBackupHome.this))
                                BackupEncrypt.removeEncryptKey(ActivityBackupHome.this);
                        }

                        break;
                    case JUST_ENTER_PASS:

                        if (empty(strFirstPass)) {
                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Empty field!</font>"));
                            break;
                        }
                        if (strFirstPass.length() < Constants.MIN_PASSWORD_LENGTH || strFirstPass.length() > Constants.MAX_PASSWORD_LENGTH) {
                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Password must not be more than 10 and less than 3 characters!</font>"));
                            break;
                        }

                        if (BackupEncrypt.testEncryptKey(ActivityBackupHome.this, strFirstPass)) {

                            if (Prefs.getBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false)) {

                                Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false);

                                chkEncrypt.setChecked(false);

                            } else {

                                Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, true);

                                chkEncrypt.setChecked(true);


                            }

                            wantToCloseDialog = true;
                        } else {
                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Wrong password!</font>"));
                        }

                        break;
                    case CHANGE_OLD_PASS:
                        txtViewPassDialogComments.setText("(Note: don't use any language except english! Passwords must not be more than 10 and less than 3 characters.)");
                        alert.setTitle("Change Password");
                        break;
                    case NEW_PASSWORD:

                        Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false);

                        //chkEncrypt.setChecked(false);


                        if (empty(strFirstPass) || empty(strSecondPass)) {
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Empty fields!</font>"));
                            break;
                        }

                        if (!strFirstPass.contentEquals(strSecondPass)) {
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Passwords are not equal!</font>"));
                            break;
                        }

                        if (strFirstPass.length() < Constants.MIN_PASSWORD_LENGTH || strSecondPass.length() < Constants.MIN_PASSWORD_LENGTH || strFirstPass.length() > Constants.MAX_PASSWORD_LENGTH || strSecondPass.length() > Constants.MAX_PASSWORD_LENGTH) {
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Passwords must not be more than 10 and less than 3 characters!</font>"));
                            break;
                        }

                        if (BackupEncrypt.isThereEncryptKey(ActivityBackupHome.this))
                            BackupEncrypt.removeEncryptKey(ActivityBackupHome.this);

                        try {
                            BackupEncrypt.writeEncryptKey(ActivityBackupHome.this, strFirstPass);
                            Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, true);
                            chkEncrypt.setChecked(true);
                            imgBtnRemoveKey.setVisibility(View.VISIBLE);
                            wantToCloseDialog = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                if (wantToCloseDialog)
                    dialog.dismiss();

/*                String firstPass = txtFirstPassword.getText().toString();
                String secondPass = txtSecondPassword.getText().toString();
                Toast.makeText(getBaseContext(), "Username: " + firstPass + " Password: " + secondPass, Toast.LENGTH_SHORT).show();  */
            }
        });
        return returnResult;
    }

    private class FileBackUpDepackParameters {

        private boolean doEncryption = false;
        private String passWord = null;
        private String fileName;

        public FileBackUpDepackParameters(boolean doEncryption, String passWord, String fileName) {
            this.doEncryption = doEncryption;
            this.passWord = passWord;
            this.fileName = fileName;

        }

        public boolean isDoEncryption() {
            return doEncryption;
        }

        public String getPassWord() {
            return passWord;
        }

        public String getFileName() {
            return fileName;
        }

    }

    private class LongFileBackupDepackOperation extends AsyncTask<FileBackUpDepackParameters, Void, Boolean> {
        ArrayList<BackUpNotesStruct> backUpNotesStructs = null;
        private ProgressDialog dialog;

        @Override
        protected Boolean doInBackground(FileBackUpDepackParameters... fileBackUpDepackParameterses) {

            File tmpFolder = new File(ExternalStorageManager.getTempWorkingDirectory() + File.separator);

            if (!tmpFolder.exists())
                tmpFolder.mkdir();


            File tmpFile = new File(tmpFolder.getPath() + File.separator + "temp_decrypt" + Constants.CONST_BACKUPFILE_EXTENSION);

            if (tmpFile.exists())
                tmpFile.delete();

            try {

                if (fileBackUpDepackParameterses[0].isDoEncryption()) {
                    BackupEncrypt.decryptFile(fileBackUpDepackParameterses[0].getFileName(), fileBackUpDepackParameterses[0].getPassWord(), tmpFile.getPath());

                    backUpNotesStructs = BackUpFileHelper.readBackUp(tmpFile);

                } else {
                    BackupEncrypt.decryptFile(fileBackUpDepackParameterses[0].getFileName(), null, tmpFile.getPath());

                    backUpNotesStructs = BackUpFileHelper.readBackUp(tmpFile);
                }

                if (tmpFile.exists())
                    tmpFile.delete();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);


            if (aBoolean) {

                realmNoteHelper.applyBackupToDB(backUpNotesStructs,chkRestoreTextNotes.isChecked(),chkRestoreAudioNotes.isChecked(),chkRestorPhonecalls.isChecked(),chkRestoreTodos.isChecked(),chkMergeDestination.isChecked(),chkReplaceLocalDB.isChecked());
                dialog.cancel();

                Utility.showMessage("Restore was successful.", "Info", ActivityBackupHome.this);
            }
            else {
                dialog.cancel();
                Utility.showMessage("Restore wasn't successful!", "Error", ActivityBackupHome.this);
            }


        }


        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ActivityBackupHome.this, "",
                    "Loading. Please wait...", true);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class FileBackUpParameters {

        private boolean doEncryption = false;
        private String passWord = null;

        public FileBackUpParameters(boolean doEncryption, String passWord) {
            this.doEncryption = doEncryption;
            this.passWord = passWord;
        }

        public boolean isDoEncryption() {
            return doEncryption;
        }

        public String getPassWord() {
            return passWord;
        }

    }

    private class LongFileBackupOperation extends AsyncTask<FileBackUpParameters, Void, Boolean> {
        ArrayList<BackUpNotesStruct> backUpNotesStructs = null;
        private ProgressDialog dialog;

        @Override
        protected Boolean doInBackground(FileBackUpParameters... fileBackUpParameterses) {

            String timeStamp = new SimpleDateFormat("dd_MM_yyyy_hh_mm_a").format(new Date());


            File outputFile;

            File tmpFolder = new File(ExternalStorageManager.getTempWorkingDirectory() + File.separator);

            if (!tmpFolder.exists())
                tmpFolder.mkdir();


            File tmpFile = new File(tmpFolder.getPath() + File.separator + "temp" + Constants.CONST_BACKUPFILE_EXTENSION);

            if (tmpFile.exists())
                tmpFile.delete();
            //Constants.TEMP_FOLDER_NAME
            //BackUpFileHelper.writeBackUp(ExternalStorageManager.getWorkingDirectory() + "temp" +  Constants.CONST_BACKUPFILE_EXTENSION);

            BackUpFileHelper.writeBackUp(tmpFile, backUpNotesStructs);

            if (fileBackUpParameterses[0].isDoEncryption()) {
                try {
                    outputFile = new File(ExternalStorageManager.getWorkingDirectory() + File.separator + Constants.CONST_BACKUPFILE_PREFIX + timeStamp + "_encrypted" + Constants.CONST_BACKUPFILE_EXTENSION);

                    BackupEncrypt.encryptFile(outputFile, fileBackUpParameterses[0].getPassWord(), tmpFile);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    outputFile = new File(ExternalStorageManager.getWorkingDirectory() + File.separator + Constants.CONST_BACKUPFILE_PREFIX + timeStamp + Constants.CONST_BACKUPFILE_EXTENSION);
                    BackupEncrypt.encryptFile(outputFile, null, tmpFile);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            dialog.cancel();

            if (aBoolean)
                Utility.showMessage("Backup was successful.", "Info", ActivityBackupHome.this);
            else
                Utility.showMessage("Backup wasn't successful!", "Error", ActivityBackupHome.this);

        }


        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ActivityBackupHome.this, "",
                    "Loading. Please wait...", true);
            try {
                backUpNotesStructs = realmNoteHelper.backupNotes(chkExmportTextNotes.isChecked(), chkExportAudioNotes.isChecked(), chkExportPhoneCalls.isChecked(), chkExportTodos.isChecked());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }

    private void populateDropBoxUploadList() {
        final OkHttpClient client = new OkHttpClient();
        //Headers headers = new Headers()
        final Request request = new Request.Builder()
                .url(Constants.CONST_URL_DROPBOX + "/2/files/list_folder")
                .addHeader("Authorization", "Bearer PIcmea9okk4AAAAAAAAhBE-HQHoEU14hY_AtB02GJs0PsRdFARC3f_r4wTUvQ3zq")
                .post(RequestBody.create(JSON, "{\"path\": \"/stamina/\",\"recursive\": true,\"include_media_info\": false,\"include_deleted\": false,\"include_has_explicit_shared_members\": false}"))
                .build();

        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                displayIt(new File(ExternalStorageManager.getWorkingDirectory()));

                for (int i = 0; i < backupFilesStruct.size(); i++)
                    System.out.println(backupFilesStruct.get(i).getFilePath() + "*");
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        return null;
                    }
                    return response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {

                    try {
                        JSONObject jsonRootObject = new JSONObject(s);

                        JSONArray jsonArray = jsonRootObject.optJSONArray("entries");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String path_lower = jsonObject.optString("path_lower").toString();
                            //  String name = jsonObject.optString("name").toString();
                            String isFile = jsonObject.optString(".tag").toString();


                            if (isFile.contains("file") && path_lower.contains(".journal")) {
                                cloudPathsJournalFiles.add(path_lower);
                                //getFinalList(loopIndex);
                            } else if (isFile.contains("file") &&
                                    !path_lower.contains(Constants.CONST_RECYCLEBIN_DIRECTORY_NAME) &&
                                    !path_lower.contains(Constants.TEMP_FOLDER_NAME)) {
                                cloudPathsAudioFiles.add(path_lower);

                            }
                            //data += "Node"+i+" : \n path_lower= "+ path_lower +" \n Name= "+ name +" \n tag= "+ isFile +" \n ";
                        }


                        ArrayList<BackupFilesStruct> tmpStr = new ArrayList<BackupFilesStruct>();

                        for (int i = 0; i < backupFilesStruct.size(); i++) {

                            String str = Constants.CONST_WORKING_DIRECTORY_NAME + File.separator;
                            // Removing android system path.
                            int startIndex = backupFilesStruct.get(i).getFilePath().indexOf(str);

                            str = backupFilesStruct.get(i).getFilePath().substring(startIndex, backupFilesStruct.get(i).getFilePath().length());

                            boolean isFileExistInCloude = false;

                            for (int j = 0; j < cloudPathsAudioFiles.size(); j++) {

                                if (cloudPathsAudioFiles.get(j).equalsIgnoreCase(str)) {
                                    isFileExistInCloude = true;
                                    //Remove from cloud list to make final list.
                                    cloudPathsAudioFiles.remove(j);
                                    break;
                                }
                            }

                            if (!isFileExistInCloude)
                                tmpStr.add(new BackupFilesStruct(backupFilesStruct.get(i).getFile()));

                        }


                        backupFilesStruct = new ArrayList<>(tmpStr);

                        for (int i = 0; i < backupFilesStruct.size(); i++)
                            System.out.println(backupFilesStruct.get(i).getFilePath() + "#");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        asyncTask.execute();

    }

    private class LongOperation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private int getDbIdFromFileName(String file_name) {

        if (file_name == null || file_name.length() == 0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(0, file_name.lastIndexOf("_")));
    }

    public void displayIt(File node) {
        //  System.out.println(node.getParent() +"*");
        //backupFilesStruct.add(new BackupFilesStruct(node));
        if (!node.getAbsolutePath().contains(Constants.CONST_RECYCLEBIN_DIRECTORY_NAME) &&
                !node.getAbsolutePath().contains(Constants.TEMP_FOLDER_NAME) &&
                /* Excluding the phonecall directory */
                !node.getAbsolutePath().equalsIgnoreCase(ExternalStorageManager.getWorkingDirectory() + Constants.CONST_PHONE_CALLS_DIRECTORY_NAME) &&
                /* Excluding the working directory */
                !node.getAbsolutePath().equalsIgnoreCase(ExternalStorageManager.getWorkingDirectory())) {
            if (node.isFile()) {
                File f = new File(ExternalStorageManager.getWorkingDirectory() + File.separator + node.getName());
                if (!f.exists()) {
                    //System.out.println(node.getAbsoluteFile());
                    backupFilesStruct.add(new BackupFilesStruct(node));
                }
            }
            /*else{
                //System.out.println(node.getAbsoluteFile());
                backupFilesStruct.add(new BackupFilesStruct(node));
            }*/


        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                displayIt(new File(node, filename));
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasToken()) {

            dropbox_login_button.setText("Unlink");
            findViewById(R.id.name_text).setVisibility(View.VISIBLE);
            //findViewById(R.id.files_button).setEnabled(true);
        } else {
            dropbox_login_button.setText("Login with Dropbox");
            findViewById(R.id.name_text).setVisibility(View.GONE);
            //findViewById(R.id.files_button).setEnabled(false);
        }
    }

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                ((TextView) findViewById(R.id.name_text)).setText("Account name: " + result.getName().getDisplayName());
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
            }
        }).execute();
    }
}

/*
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityBackupHome.this);
builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
        .setNegativeButton("No", dialogClickListener).show();
*/





                /*client.newCall()post("http://www.roundsapp.com/post", "", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Something went wrong
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseStr = response.body().string();
                            // Do what you want to do with the response.
                        } else {
                            // Request not successful
                        }
                    }
                });*/
/*
                for(int i=0 ; i< backupFilesStruct.size();i++){
                    System.out.println(backupFilesStruct.get(i).getFilePath());
                }

*/




              /*  new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
                    public int loopIndex;

                    @Override
                    public void onDataLoaded(ListFolderResult result) {

                        while (true) {

                            for (Metadata metadata : result.getEntries()) {
                              //  System.out.println(metadata.getPathDisplay());
                                cloudPaths.add(metadata.getPathDisplay());
                            }

                            if (!result.getHasMore()) {
                                break;
                            }

                            try {
                                result = DropboxClientFactory.getClient().files().listFolderContinue(result.getCursor());
                            } catch (DbxException e) {
                                e.printStackTrace();
                            }
                        }
*/

                        /*new ListFileTask(cloudPaths, new ListFileTask.Callback() {
                            @Override
                            public void onDataLoaded(List<String> result) {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });*/

                      /*  new LongOperation().execute();
                        for (loopIndex=0; loopIndex< cloudPaths.size(); loopIndex++) {
                            System.out.println(loopIndex + "yyyyyyyyyyyyyyyyyyyyyyyyyyy" + cloudPaths.size());

                            if(!cloudPaths.get(loopIndex).contains(".") &&
                                    !cloudPaths.get(loopIndex).contains(Constants.CONST_RECYCLEBIN_DIRECTORY_NAME) &&
                                    !cloudPaths.get(loopIndex).contains(Constants.TEMP_FOLDER_NAME)) {

                                new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
                                    @Override
                                    public void onDataLoaded(ListFolderResult result) {

                                        while (true) {

                                            for (Metadata metadata : result.getEntries()) {
                                                System.out.println(metadata.getPathDisplay() + " :");
                                                cloudPathsAudioFiles.add(metadata.getPathDisplay());
                                            }

                                            if (!result.getHasMore()) {
                                                getFinalList(loopIndex);
                                                break;
                                            }

                                            try {
                                                result = DropboxClientFactory.getClient().files().listFolderContinue(result.getCursor());
                                            } catch (DbxException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Utility.showMessage(e.getMessage(), "Error", ActivityBackupHome.this);
                                    }
                                }).execute(cloudPaths.get(loopIndex));
                            }
                            else{

                                if(cloudPaths.get(loopIndex).contains(".journal"))
                                    cloudPathsJournalFiles.add(cloudPaths.get(loopIndex));
                                getFinalList(loopIndex);
                            }


                        }



                    }

                    @Override
                    public void onError(Exception e) {
                        Utility.showMessage("Error to finish process. Please try again.","Error",ActivityBackupHome.this);
                    }
                }).execute("/stamina");

*/


             /*   new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
                    @Override
                    public void onDataLoaded(ListFolderResult result) {
                        //dialog.dismiss();

                        result.getEntries();
                        while (true) {
                            for (Metadata metadata : result.getEntries()) {

                                System.out.println(metadata.getPathLower());
                            }

                            if (!result.getHasMore()) {
                                break;
                            }

                            try {
                                result = DropboxClientFactory.getClient().files().listFolderContinue(result.getCursor());
                            } catch (DbxException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(Exception e) {
                        // dialog.dismiss();

                        Log.e("FFFF", "Failed to list folder.", e);
                        Toast.makeText(ActivityBackupHome.this,
                                "An error has occurred",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }).execute("/stamina/phonecalls");
*/
// System.out.println(getDbIdFromFileName("1234_5678"));
/*
                ProgressDialog dialog = ProgressDialog.show(ActivityBackupHome.this, "",
                        "Loading. Please wait...", true);
*/