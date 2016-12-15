package com.fleecast.stamina.backup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fleecast.stamina.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by nnt on 14/12/16.
 */

public class GroupsListDialog {

    private final Context mContext;
    private ResultsListener listener;

    public interface ResultsListener {
        void selectedGroup(String title);
    }


    public void setResultsListener(ResultsListener listener) {
        this.listener = listener;
    }


    public GroupsListDialog(Context mContext, String titleOfDialog, boolean showAddNewToList, String[] dictionaryOfGroups) {

        this.listener = null;
        this.mContext = mContext;

        final EditText et = new EditText(mContext);
        et.setHint("Or type new group name");
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //Filtering double eantries
        dictionaryOfGroups = new HashSet<String>(Arrays.asList(dictionaryOfGroups)).toArray(new String[0]);

        ArrayList<String> dictionaryOfGroupsList = new ArrayList<String>();
        dictionaryOfGroupsList.addAll(Arrays.asList(dictionaryOfGroups));

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, dictionaryOfGroupsList);


        ListView listView = new ListView(mContext);
        listView.setAdapter(listAdapter);

        AlertDialog.Builder addNoteDialogBuilder;


        addNoteDialogBuilder = new AlertDialog.Builder(mContext);
        LinearLayout layout = new LinearLayout(mContext);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(5, 5, 5, 5);


        TextView tvMessage = new TextView(mContext);

        tvMessage.setTextColor(ContextCompat.getColor(mContext, R.color.american_rose));

        layout.addView(tvMessage, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(listView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));




        addNoteDialogBuilder.setView(layout);

        addNoteDialogBuilder.setTitle(titleOfDialog);
        addNoteDialogBuilder.setIcon(R.drawable.list);
        addNoteDialogBuilder.setCancelable(true);

        final AlertDialog dialogListOfGroups = addNoteDialogBuilder.create();

        if (showAddNewToList) {
            addNoteDialogBuilder.setPositiveButton("ADD GROUP",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing here because we override this button later to change the close behaviour.
                            //However, we still need this because on older versions of Android unless we
                            //pass a handler the button doesn't get instantiated
                        }
                    });


            dialogListOfGroups.show();

            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focused) {
                    if (focused) {
                        dialogListOfGroups.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });


            dialogListOfGroups.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // This makes sure that the container activity has implemented
                    // the callback interface. If not, it throws an exception
                    try {
                    } catch (ClassCastException e) {
                        throw new ClassCastException(" must implement OnHeadlineSelectedListener");
                    }
                    dialogListOfGroups.dismiss();


                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listener.selectedGroup(adapterView.getItemAtPosition(i).toString());
                dialogListOfGroups.dismiss();
            }
        });


    }
}

