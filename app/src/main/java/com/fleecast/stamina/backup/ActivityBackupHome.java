package com.fleecast.stamina.backup;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.DropBoxManager;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityBackupHome extends DropboxActivity {

    private Button dropbox_login_button;
    private Button btnCreatBackUp;
    private ArrayList <BackupFilesStruct> backupFilesStructArraylist = new ArrayList<>();
    private ArrayList <String> cloudPaths = new ArrayList<>();
    private ArrayList <String> cloudPathsAudioFiles = new ArrayList<>();
    private ArrayList <String> cloudPathsJournalFiles = new ArrayList<>();
    private int indexFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_home);

        dropbox_login_button = (Button) findViewById(R.id.dropbox_login_button);

       /* if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/

        Button loginButton = (Button)findViewById(R.id.dropbox_login_button);

        btnCreatBackUp = (Button) findViewById(R.id.btnCreatBackUp);

        btnCreatBackUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

/*
                for(int i=0 ; i< backupFilesStructArraylist.size();i++){
                    System.out.println(backupFilesStructArraylist.get(i).getFilePath());
                }

*/

                displayIt(new File(ExternalStorageManager.getWorkingDirectory()));

                new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
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

                        for (int i=0; i< cloudPaths.size(); i++) {

                            if(!cloudPaths.get(i).contains(".") &&
                                    !cloudPaths.get(i).contains(Constants.CONST_RECYCLEBIN_DIRECTORY_NAME) &&
                                    !cloudPaths.get(i).contains(Constants.TEMP_FOLDER_NAME)) {

                                new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
                                    @Override
                                    public void onDataLoaded(ListFolderResult result) {

                                        while (true) {

                                            for (Metadata metadata : result.getEntries()) {
                                                System.out.println(metadata.getPathDisplay());
                                                cloudPathsAudioFiles.add(metadata.getPathDisplay());
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
                                        Utility.showMessage(e.getMessage(), "Error", ActivityBackupHome.this);
                                    }
                                }).execute(cloudPaths.get(i));
                            }
                            else{

                                if(cloudPaths.get(i).contains(".journal"))
                                    cloudPathsJournalFiles.add(cloudPaths.get(i));

                            }
                        }

                    }

                    @Override
                    public void onError(Exception e) {
                        Utility.showMessage("Error to finish process. Please try again.","Error",ActivityBackupHome.this);
                    }
                }).execute("/stamina");


                for (int i=0 ; i<cloudPathsAudioFiles.size();i++){
                    for(int j=0; j < backupFilesStructArraylist.size();j++) {
                        if (cloudPathsAudioFiles.get(i).contains(ExternalStorageManager.getWorkingDirectory())) {

                        }
                    }
                }

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

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasToken()) {
                    Auth.startOAuth2Authentication(ActivityBackupHome.this, getString(R.string.app_key));
                }
                else
                {
                    SharedPreferences prefs = getSharedPreferences("stamina-dropbox", MODE_PRIVATE);
                    String accessToken = prefs.getString("access-token", null);
                    //if (accessToken == null) {
                    //    accessToken = Auth.getOAuth2Token();
                    //    if (accessToken != null) {
                    prefs.edit().remove("access-token").apply();
                }

            }
        });

    }

    private int getDbIdFromFileName(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(0,file_name.lastIndexOf("_")));
    }

    public void displayIt(File node){
      //  System.out.println(node.getParent() +"*");
        //backupFilesStructArraylist.add(new BackupFilesStruct(node));
        if(!node.getAbsolutePath().contains(Constants.CONST_RECYCLEBIN_DIRECTORY_NAME) &&
                !node.getAbsolutePath().contains(Constants.TEMP_FOLDER_NAME) &&
                /* Excluding the phonecall directory */
                !node.getAbsolutePath().equalsIgnoreCase(ExternalStorageManager.getWorkingDirectory()  + Constants.CONST_PHONE_CALLS_DIRECTORY_NAME) &&
                /* Excluding the working directory */
                !node.getAbsolutePath().equalsIgnoreCase(ExternalStorageManager.getWorkingDirectory())) {
            if(node.isFile()) {
                File f = new File(ExternalStorageManager.getWorkingDirectory()+ File.separator + node.getName());
                if(!f.exists()) {
                    System.out.println(node.getAbsoluteFile());
                    backupFilesStructArraylist.add(new BackupFilesStruct(node));
                }
            }
            else{
                System.out.println(node.getAbsoluteFile());
                backupFilesStructArraylist.add(new BackupFilesStruct(node));
            }


        }

        if(node.isDirectory() ){
            String[] subNote = node.list();
            for(String filename : subNote){
                displayIt(new File(node, filename));
            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

        if (hasToken() ) {

            dropbox_login_button .setText("Unlink");
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
