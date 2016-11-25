package com.fleecast.stamina.backup;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ActivityJurnalFiles extends ListActivity {
    private File pathToWorkingDir;
    private ArrayList<File> fileList = new ArrayList<File>();
    private LinearLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view = (LinearLayout) findViewById(R.id.view);


        pathToWorkingDir = new File(ExternalStorageManager.getWorkingDirectory());


        ArrayList<File> fList = getFilesList(pathToWorkingDir);

        setListAdapter(new ArrayAdapterJournalBackups(this,fList));




    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_BACKUP_FILENAME,0);
        setResult(Activity.RESULT_CANCELED, data);
        finish();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        File selectedValue = (File) getListAdapter().getItem(position);

        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_BACKUP_FILENAME,selectedValue.getPath());
         setResult(Activity.RESULT_OK, data);
        finish();


    }

    public ArrayList<File> getFilesList(File dir) {
        File listFile[] = dir.listFiles();

        Arrays.sort( listFile, new Comparator()
        {
            public int compare(Object o1, Object o2) {

                if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                    return -1;
                } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }

        });

        if (listFile != null && listFile.length > 0) {

            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isFile()) {
                    if (listFile[i].getName().endsWith(".journal"))
                    {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
    }


}