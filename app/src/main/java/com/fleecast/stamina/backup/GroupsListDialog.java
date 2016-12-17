package com.fleecast.stamina.backup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Utility;

/**
 * Created by nnt on 14/12/16.
 */

public class GroupsListDialog {

    private final Context mContext;
    private final AlertDialog dialog;
    private ResultsListener listener;

    public interface ResultsListener {
        void selectedGroup(String selectedGroupTitle);
        void newGroupAdded(String newGroupTitle);
    }


    public void setResultsListener(ResultsListener listener) {
        this.listener = listener;
    }


    public GroupsListDialog(final Context mContext, String titleOfDialog, boolean showAddNewToList,String desceriptions ,final String[] dictionaryOfGroups) {

        this.listener = null;
        this.mContext = mContext;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.group_selection_dialog,
                null, false);

        final EditText et = (EditText) formElementsView
                .findViewById(R.id.dlgEdtxtGroupsTitle);
        final TextView tv = (TextView) formElementsView
                .findViewById(R.id.dlgTxtGroupsComment);

        et.setHint("Or type new group name");
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        ListView listView = (ListView) formElementsView.findViewById(R.id.dlgListGroupsItems);

        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if(!showAddNewToList) {

            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_START, R.id.groups_layout);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_END, R.id.groups_layout);
            listView.setLayoutParams(relativeParams);
            et.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
        }
        else{
            tv.setVisibility(View.INVISIBLE);
        }


        if(desceriptions==null)
            tv.setText("");
        else
            tv.setText(desceriptions);

        if(dictionaryOfGroups!=null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_list_item_2,
                    dictionaryOfGroups);
            listView.setAdapter(adapter);
        }
        else{
            listView.setVisibility(View.GONE);
            relativeParams.addRule(RelativeLayout.BELOW, R.id.dlgTxtGroupsComment);
            //relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, R.id.groups_layout);

            et.setLayoutParams(relativeParams);
        }
        AlertDialog.Builder addNoteDialogBuilder = new AlertDialog.Builder(mContext);

        addNoteDialogBuilder.setView(formElementsView);
        addNoteDialogBuilder.setTitle(titleOfDialog);

        if (showAddNewToList) {

            addNoteDialogBuilder.setPositiveButton("Add New Group", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

        }



        dialog = addNoteDialogBuilder.show();

        if (showAddNewToList) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newGroup = et.getText().toString().trim();
                    if (newGroup == null || newGroup.length() == 0) {
                        tv.setVisibility(View.VISIBLE);
                        tv.setText(Utility.fromHTMLVersionCompat("<font color='red'>Add a tag!</font>", Html.FROM_HTML_MODE_LEGACY));
                        return;

                    } else {
                        listener.newGroupAdded(newGroup);
                        dialog.dismiss();
                    }

                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listener.selectedGroup(adapterView.getItemAtPosition(i).toString());
                dialog.dismiss();
            }
        });
    }
}

