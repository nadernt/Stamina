package com.fleecast.stamina.settings;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ActivityChooseDirectory extends AppCompatActivity {
    private ListView lvExplorer;
    private ImageButton btnPrevDir;
    private String prevDir;
    private Button btnNewDirectory;
    private Button btnSelectDirectory;
    private Button btnSdCard;
    private String currentSelectedPath;
    private String strCreateFolderName;
    private Button btnExit;
    private TextView txtCurrentPath;
    private String primary_sd;
    private String secondary_sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_directory);


        btnPrevDir = (ImageButton) findViewById(R.id.prev_dir_imagebutton);
        lvExplorer = (ListView) findViewById(R.id.explorer_listview);
        btnNewDirectory = (Button) findViewById(R.id.btnNewDirectory);
        btnExit = (Button) findViewById(R.id.btnExit);
        btnSelectDirectory = (Button) findViewById(R.id.btnSelectDirectory);
        txtCurrentPath = (TextView) findViewById(R.id.txtCurrentPath);
        btnSdCard  = (Button) findViewById(R.id.btnSdCard);

        ExtStorageSearch();

        btnNewDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChooseDirectory.this);
                final EditText createFolderName = new EditText(ActivityChooseDirectory.this);

                LinearLayout layout = new LinearLayout(ActivityChooseDirectory.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                createFolderName.setHint("Directory name");

                // If user did mistake. again fill with old inputs.
                if (strCreateFolderName !=null)
                    createFolderName.setText(strCreateFolderName);

                layout.addView(createFolderName);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        strCreateFolderName = createFolderName.getText().toString().trim();

                        if (strCreateFolderName.matches("[a-zA-Z0-9]+")) {
                            if (strCreateFolderName.length() > 0) {
                                try {
                                    File directory = new File(currentSelectedPath +"/" + strCreateFolderName);
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                        if(!directory.exists()) {
                                            showMessage("You can't make folder in this path!", "Note");
                                        }else {
                                            strCreateFolderName = "";
                                            loadPath(currentSelectedPath);
                                        }
                                    } else {
                                        showMessage("Folder name exist.", "Note");
                                    }

                                } catch (Exception e) {
                                    showMessage("You can't make folder in this path: " + e.getMessage(),"Note");
                                }

                            } else {
                                showMessage("Folder name can't be empty.", "Note");
                            }
                        } else {
                            showMessage("You can use alphabet and number for name!", "Note");
                        }

                        dialog.dismiss();

                    }

                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        strCreateFolderName ="";
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.setTitle("Add Custom Ignore Number");
                dialog.setView(layout);

                dialog.show();





            }
        });

        btnSelectDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChooseDirectory.this);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                try {
                                    File directory = new File(currentSelectedPath + Constants.CONST_WORKING_DIRECTORY_NAME);
                                    if (!directory.exists()) {
                                        directory.mkdirs();
                                        if(!directory.exists()) {
                                            showMessage("You can't make folder in this path!", "Note");
                                            return;
                                        }
                                    }
                                } catch (Exception e) {
                                    showMessage("You can't make folder in this path: " + e.getMessage(),"Note");
                                }

                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(Constants.EXTRA_RESULT_SELECTED_DIR,currentSelectedPath);
                                setResult(Constants.RESULT_CODE_REQUEST_DIRECTORY,returnIntent);
                                 finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                builder.setMessage("You are choosing: \n" + currentSelectedPath)
                       .setPositiveButton("Yes", dialogClickListener)
                       .setNegativeButton("No", dialogClickListener).show();



            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPrevDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prevDir != null)
                    loadPath(prevDir);
            }
        });
        txtCurrentPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prevDir != null)
                    loadPath(prevDir);
            }
        });

        lvExplorer.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FolderChooseAdapter adapter = (FolderChooseAdapter) parent.getAdapter();
                File file = (File) adapter.getItem(position);

                if (file.isDirectory()) {
                    onDirectorySelected(file.getPath());
                }
                else {
                    onFileSelected(file.getPath());
                }

            }
        });

        btnSdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prevDir != null)
                    loadPath(primary_sd);
            }
        });

        //Check if SD Card exists set the initial path or set to default path.

        if(secondary_sd!= null) {
            btnSdCard.setEnabled(true);
            loadPath(secondary_sd);
        }
        else {
            btnSdCard.setEnabled(false);
            btnSdCard.setText("No\nSD Card");
            loadPath(Environment.getExternalStorageDirectory().getPath());
        }
    }

    @Override
    public void onBackPressed() {
        if (prevDir != null)
            loadPath(prevDir);

    }

    private void onDirectorySelected(String path) {
        loadPath(path);
    }

    private void onFileSelected(String path) {
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
    }

    private void loadPath(String path) {
        lvExplorer.removeAllViewsInLayout();

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(path);
        else
            txtCurrentPath.setText(path);

        currentSelectedPath = path;

        File myDirectory = new File(path);
        prevDir = myDirectory.getParent();
        File[] fileTmp = myDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // Kick off the hidden or system folders
                return (!pathname.isHidden() && pathname.canRead() && pathname.canWrite());
                //return pathname.canRead();
            }
        });
        Arrays.sort(fileTmp);

        List <File> files = new ArrayList<>();
    for (File inFile : fileTmp) {
            if (inFile.isDirectory()) {
                files.add(inFile);// is directory
            }
        }

        lvExplorer.setAdapter(new FolderChooseAdapter(this, files));
    }

    public void ExtStorageSearch(){
        String[] extStorlocs = {"/storage/sdcard1","/storage/extsdcard","/storage/sdcard0/external_sdcard","/mnt/extsdcard",
                "/mnt/sdcard/external_sd","/mnt/external_sd","/mnt/media_rw/sdcard1","/removable/microsd","/mnt/emmc",
                "/storage/external_SD","/storage/ext_sd","/storage/removable/sdcard1","/data/sdext","/data/sdext2",
                "/data/sdext3","/data/sdext4","/storage/sdcard0"};
//Log.e("AAAAAAAAAAAA",System.getenv("EXTERNAL_STORAGE") + "\n" + System.getenv("SECONDARY_STORAGE") + "\n" + MediaStore.MediaColumns.DATA);
       //First Attempt
        primary_sd = System.getenv("EXTERNAL_STORAGE");
        secondary_sd = System.getenv("SECONDARY_STORAGE");


        if(primary_sd == null) {
            primary_sd = Environment.getExternalStorageDirectory()+"";
        }
        if(secondary_sd == null) {//if fail, search among known list of extStorage Locations
            for(String string: extStorlocs){
                if((new File(string)).exists() && (new File(string)).isDirectory() ){
                    secondary_sd = string;
                    break;
                }
            }
        }

    }

    private void showMessage(String messageToUser,String titleOfDialog){

        new AlertDialog.Builder(this)
                .setTitle(titleOfDialog)
                .setMessage(messageToUser)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}