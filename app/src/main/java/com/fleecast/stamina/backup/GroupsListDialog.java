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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
        void removeGroupFromItem();
    }


    public void setResultsListener(ResultsListener listener) {
        this.listener = listener;
    }


    public GroupsListDialog(final Context mContext, String titleOfDialog, boolean showAddNewToList, String desceriptions, final String[] dictionaryOfGroups, String itemGroup) {

        this.listener = null;
        this.mContext = mContext;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.group_selection_dialog,
                null, false);

        ImageView dlgManageGroupsHelp = (ImageView) formElementsView.findViewById(R.id.dlgManageGroupsHelp);

        dlgManageGroupsHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strHelp =
                        "&#8226; Remove does not delete the note. It just removes note from allocated group.<br/>" +
                        "&#8226; If you tap on a group from the list then your note will be part of the selected group.<br/>" +
                        "&#8226; You can create a new group then your note will be automatically part of that group.<br/>";

                Utility.showMessage(Utility.fromHTMLVersionCompat(strHelp,Html.FROM_HTML_MODE_LEGACY),"Help",android.R.drawable.ic_dialog_info,mContext);
            }
        });

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


        if(desceriptions==null) {
            tv.setText("");
        }
        else {
            tv.setText(Utility.fromHTMLVersionCompat(desceriptions, Html.FROM_HTML_MODE_LEGACY));
            tv.setVisibility(View.VISIBLE);
        }


        if(dictionaryOfGroups!=null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_list_item_1,
                    dictionaryOfGroups);
            listView.setAdapter(adapter);
        }
        else{
            listView.setVisibility(View.GONE);
            relativeParams.addRule(RelativeLayout.BELOW, R.id.dlgTxtGroupsComment);
            et.setHint("Type new group name");
            et.setLayoutParams(relativeParams);
        }

        AlertDialog.Builder addNoteDialogBuilder = new AlertDialog.Builder(mContext);

        addNoteDialogBuilder.setView(formElementsView);
        addNoteDialogBuilder.setTitle(titleOfDialog);
        addNoteDialogBuilder.setCancelable(true);

        if (showAddNewToList) {

            addNoteDialogBuilder.setPositiveButton("New", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // don't write anything here as we override this function.
                }
            });

            if(itemGroup!=null && itemGroup.length()>0) {
                addNoteDialogBuilder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // don't write anything here as we override this function.
                    }
                });
            }
        }

        dialog = addNoteDialogBuilder.show();

        if (showAddNewToList) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newGroup = et.getText().toString().trim();
                    if (newGroup == null || newGroup.length() == 0) {
                        tv.setVisibility(View.VISIBLE);
                        tv.setText(Utility.fromHTMLVersionCompat("<font color='red'>Type something as group name for selected note.!</font>", Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        if(!groupAlreadyExist(dictionaryOfGroups,newGroup)) {
                            listener.newGroupAdded(newGroup);
                            dialog.dismiss();
                        }
                    }

                }
            });
            if(itemGroup!=null && itemGroup.length()>0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.removeGroupFromItem();
                        dialog.dismiss();
                    }
                });
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listener.selectedGroup(adapterView.getItemAtPosition(i).toString());
                dialog.dismiss();
            }
        });
    }

    private boolean groupAlreadyExist( String[] dictionaryOfGroups, String newGroup){

        if(dictionaryOfGroups==null)
            return false;

        for(int i=0; i< dictionaryOfGroups.length; i++)
        {
            if(dictionaryOfGroups[i].contentEquals(newGroup))
                return true;
        }

        return false;

    }
}

