package com.fleecast.stamina.backup;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

import android.widget.LinearLayout;
import android.widget.ListView;

import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ActivityJurnalFiles extends ListActivity {
    private File pathToWorkingDir;
    private ArrayList<File> fileList = new ArrayList<File>();
    private boolean manageJournalFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        manageJournalFiles = intent.getBooleanExtra(Constants.EXTRA_MANAGE_JOURNAL_FILES,false);

        pathToWorkingDir = new File(ExternalStorageManager.getWorkingDirectory());


        ArrayList<File> fList = getFilesList(pathToWorkingDir);

        setListAdapter(new ArrayAdapterJournalBackups(this, fList));

    }

    @Override
    public void onBackPressed() {
        if(!manageJournalFiles) {
            Intent data = new Intent();
            data.putExtra(Constants.EXTRA_BACKUP_FILENAME, 0);
            setResult(Activity.RESULT_CANCELED, data);
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onListItemClick(final ListView l, View v, final int position, long id) {
        if(!manageJournalFiles) {
            File selectedValue = (File) getListAdapter().getItem(position);

            Intent data = new Intent();
            data.putExtra(Constants.EXTRA_BACKUP_FILENAME, selectedValue.getPath());
            setResult(Activity.RESULT_OK, data);
            finish();
        }
        else
        {
            final CharSequence[] items = {
                    "Delete", "Share"
            };

            final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityJurnalFiles.this);
            builder.setTitle("Make your selection");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {

                        String sourceFile = getListView().getItemAtPosition(position).toString();

                        final File fSource = new File(sourceFile);

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        try {
                                            File fBin = new File(ExternalStorageManager.getTrashDirectory() + File.separator + fSource.getName());

                                            ExternalStorageManager.copy(fSource, fBin);
                                            fSource.delete();

                                            ArrayAdapterJournalBackups arrayAdapterJournalBackups = (ArrayAdapterJournalBackups) l.getAdapter();
                                            arrayAdapterJournalBackups.myRemove(position);
                                            arrayAdapterJournalBackups.notifyDataSetChanged();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };


                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityJurnalFiles.this);

                        builder.setMessage("Are you sure want to move to trash? " + fSource.getName()).setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    } else if (item == 1) {
                        String sourceFile = getListView().getItemAtPosition(position).toString();

                        final File fSource = new File(sourceFile);


                        File f = new File(sourceFile);

                        File fTmp = new File(ExternalStorageManager.getTempWorkingDirectory() + File.separator + fSource.getName());

                        try {
                            ExternalStorageManager.copy(f, fTmp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Uri uri = Uri.fromFile(fTmp);

                        Intent share = new Intent(Intent.ACTION_SEND);

                        String txtTitle = "Share journal file " + fSource.getName();

                        share.putExtra(Intent.EXTRA_SUBJECT, txtTitle);
                        share.putExtra(Intent.EXTRA_TITLE, txtTitle);
                        share.putExtra(Intent.EXTRA_STREAM, uri);

                        share.setType("text/*");
                        share.putExtra(Constants.EXTRA_PROTOCOL_VERSION, Constants.PROTOCOL_VERSION);
                        share.putExtra(Constants.EXTRA_APP_ID, Constants.YOUR_APP_ID);
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivityForResult(Intent.createChooser(share, "Share backup file"), Constants.SHARE_TO_MESSENGER_REQUEST_CODE);

                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }

    }

    public ArrayList<File> getFilesList(File dir) {
        File listFile[] = dir.listFiles();

        Arrays.sort(listFile, new Comparator() {
            public int compare(Object o1, Object o2) {

                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }

        });

        if (listFile != null && listFile.length > 0) {

            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isFile()) {
                    if (listFile[i].getName().endsWith(".journal")) {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
    }

}