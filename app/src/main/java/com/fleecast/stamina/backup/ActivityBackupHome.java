package com.fleecast.stamina.backup;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;
import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityBackupHome extends DropboxActivity {

    private Button dropbox_login_button;
    private Button btnCreatBackUp;
    private ArrayList <BackupFilesStruct> backupFilesStruct = new ArrayList<>();
    private ArrayList <String> cloudPaths = new ArrayList<>();
    private ArrayList <String> cloudPathsAudioFiles = new ArrayList<>();
    private ArrayList <String> cloudPathsJournalFiles = new ArrayList<>();
    private int indexFiles;
    MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private Button btnCopyToCloud;

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

        btnCreatBackUp = (Button) findViewById(R.id.btnCreateBackUp);
        btnCopyToCloud = (Button) findViewById(R.id.btnCopyToCloud);
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
                                        Utility.showMessage("Error to connect to service","Error",ActivityBackupHome.this);
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {

                                if(response.networkResponse().code() == 200)
                                    populateDropBoxUploadList();
                            }
                        });

            }
        });

        btnCreatBackUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {





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



    private void populateDropBoxUploadList(){
        final OkHttpClient client = new OkHttpClient();
        //Headers headers = new Headers()
        final Request request = new Request.Builder()
                .url(Constants.CONST_URL_DROPBOX + "/2/files/list_folder")
                .addHeader("Authorization", "Bearer PIcmea9okk4AAAAAAAAhBE-HQHoEU14hY_AtB02GJs0PsRdFARC3f_r4wTUvQ3zq")
                .post(RequestBody.create(JSON,"{\"path\": \"/stamina/\",\"recursive\": true,\"include_media_info\": false,\"include_deleted\": false,\"include_has_explicit_shared_members\": false}"))
                .build();

        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                displayIt(new File(ExternalStorageManager.getWorkingDirectory()));

                for(int i=0 ; i< backupFilesStruct.size() ; i++)
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
                        JSONObject  jsonRootObject = new JSONObject(s);

                        JSONArray jsonArray = jsonRootObject.optJSONArray("entries");

                        for(int i=0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String path_lower = jsonObject.optString("path_lower").toString();
                            //  String name = jsonObject.optString("name").toString();
                            String isFile = jsonObject.optString(".tag").toString();



                            if(isFile.contains("file") && path_lower.contains(".journal")){
                                cloudPathsJournalFiles.add(path_lower);
                                //getFinalList(loopIndex);
                            }
                            else if(isFile.contains("file") &&
                                    !path_lower.contains(Constants.CONST_RECYCLEBIN_DIRECTORY_NAME) &&
                                    !path_lower.contains(Constants.TEMP_FOLDER_NAME)) {
                                cloudPathsAudioFiles.add(path_lower);

                            }
                            //data += "Node"+i+" : \n path_lower= "+ path_lower +" \n Name= "+ name +" \n tag= "+ isFile +" \n ";
                        }



                        ArrayList<BackupFilesStruct> tmpStr = new ArrayList<BackupFilesStruct>();

                        for (int i = 0; i < backupFilesStruct.size(); i++){

                            String str  = Constants.CONST_WORKING_DIRECTORY_NAME + File.separator;
                            // Removing android system path.
                            int startIndex = backupFilesStruct.get(i).getFilePath().indexOf(str);

                            str = backupFilesStruct.get(i).getFilePath().substring(startIndex, backupFilesStruct.get(i).getFilePath().length());

                            boolean isFileExistInCloude=false;

                            for(int j=0; j < cloudPathsAudioFiles.size();j++) {

                                if (cloudPathsAudioFiles.get(j).equalsIgnoreCase(str)){
                                    isFileExistInCloude =true;
                                    //Remove from cloud list to make final list.
                                    cloudPathsAudioFiles.remove(j);
                                    break;
                                }
                            }

                            if(!isFileExistInCloude)
                                tmpStr.add(new BackupFilesStruct(backupFilesStruct.get(i).getFile()));

                        }


                        backupFilesStruct = new ArrayList<>(tmpStr);

                        for(int i=0 ; i< backupFilesStruct.size() ; i++)
                            System.out.println(backupFilesStruct.get(i).getFilePath() + "#");

                    } catch (JSONException e) {e.printStackTrace();}

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

    private int getDbIdFromFileName(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(0,file_name.lastIndexOf("_")));
    }

    public void displayIt(File node){
      //  System.out.println(node.getParent() +"*");
        //backupFilesStruct.add(new BackupFilesStruct(node));
        if(!node.getAbsolutePath().contains(Constants.CONST_RECYCLEBIN_DIRECTORY_NAME) &&
                !node.getAbsolutePath().contains(Constants.TEMP_FOLDER_NAME) &&
                /* Excluding the phonecall directory */
                !node.getAbsolutePath().equalsIgnoreCase(ExternalStorageManager.getWorkingDirectory()  + Constants.CONST_PHONE_CALLS_DIRECTORY_NAME) &&
                /* Excluding the working directory */
                !node.getAbsolutePath().equalsIgnoreCase(ExternalStorageManager.getWorkingDirectory())) {
            if(node.isFile()) {
                File f = new File(ExternalStorageManager.getWorkingDirectory()+ File.separator + node.getName());
                if(!f.exists()) {
                    //System.out.println(node.getAbsoluteFile());
                    backupFilesStruct.add(new BackupFilesStruct(node));
                }
            }
            /*else{
                //System.out.println(node.getAbsoluteFile());
                backupFilesStruct.add(new BackupFilesStruct(node));
            }*/


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
