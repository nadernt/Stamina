package com.fleecast.stamina.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.customgui.DragableDynamicListView;
import com.fleecast.stamina.listviewdragginganimation.CheesesSortingStruct;
import com.fleecast.stamina.listviewdragginganimation.StableArrayAdapter;
import com.fleecast.stamina.models.GroupsDbRealmStruct;
import com.fleecast.stamina.models.GroupsModel;
import com.fleecast.stamina.models.RealmAppHelper;
import com.fleecast.stamina.models.RealmGroupsHelper;
import com.fleecast.stamina.utility.ColoredSnackBar;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

import java.io.InputStream;
import java.util.ArrayList;

public class AddEditGroupItem extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private ImageView imgViewerGroupIcon;
    private Button btnAddGroup;
    private Button btnDeleteGroup;
    private Button btnSaveGroup;
    private String iconName="";
    private Button btnCancelChangeGroup;
    private RealmGroupsHelper realmGroupsHelper;
    private EditText txtAddEditGroupTitle;
    private TextView txtErrGroupName;
    private DragableDynamicListView listviewGroupOrders;
    private int currentSelectedItemId=-1;
    private StableArrayAdapter adapter;
    private Button btnSaveRename;
    private boolean isAddOrEdit;
    public static Activity myActivityInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppLauncherFragment.returnFromFragmentForResult = false;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_add_edit_group_item);

        RealmAppHelper realmAppHelper = new RealmAppHelper(this);
       // realmAppHelper.updateAppGroupAfterDelete(Constants.APP_IS_IN_DEFAULT_GROUP);

        myActivityInstance = AddEditGroupItem.this;

        realmGroupsHelper = new RealmGroupsHelper(this);

        btnAddGroup = (Button) findViewById(R.id.btnAddGroup);
        btnDeleteGroup = (Button) findViewById(R.id.btnDeleteGroup);
        btnSaveGroup = (Button) findViewById(R.id.btnSaveGroupOrder);
        btnCancelChangeGroup = (Button) findViewById(R.id.btnCancelChangeGroup);
        btnSaveRename = (Button) findViewById(R.id.btnSaveRename);

        listviewGroupOrders = (DragableDynamicListView) findViewById(R.id.listviewGroupOrders);
        txtAddEditGroupTitle = (EditText) findViewById(R.id.txtAddEditGroupTitle);
        txtErrGroupName = (TextView) findViewById(R.id.txtErrGroupName);


        btnAddGroup.setBackgroundColor(ContextCompat.getColor(this, R.color.yale_blue));
        btnDeleteGroup.setBackgroundColor(ContextCompat.getColor(this, R.color.red_orange));
        btnSaveGroup.setBackgroundColor(ContextCompat.getColor(this, R.color.azure));
        btnCancelChangeGroup.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_cloud));
        btnSaveRename.setBackgroundColor(ContextCompat.getColor(this, R.color.yale_blue));
        imgViewerGroupIcon = (ImageView) findViewById(R.id.imgViewerGroupIcon);

        Intent intent = getIntent();

        if(intent != null) {

             isAddOrEdit = intent.getBooleanExtra(Constants.IS_ADD_OR_EDIT, true);

            // user wants add mode.
            if (isAddOrEdit) {
                listviewGroupOrders.setVisibility(View.GONE);
                imgViewerGroupIcon.setImageResource(R.drawable.ic_action_coffee);

                btnAddGroup.setVisibility(View.VISIBLE);
                btnDeleteGroup.setVisibility(View.GONE);
                btnSaveGroup.setVisibility(View.GONE);
                btnSaveRename.setVisibility(View.GONE);

            } else {

                //int appGroupCode = intent.getIntExtra(Constants.ADD_GROUP_CODE_TO_EDIT,0);


                populateActivityContentsForGroups();


                listviewGroupOrders.setVisibility(View.VISIBLE);
                btnAddGroup.setVisibility(View.GONE);
                btnDeleteGroup.setVisibility(View.GONE);
                btnSaveRename.setVisibility(View.GONE);
                btnSaveGroup.setVisibility(View.VISIBLE);

                listviewGroupOrders.setOnItemClickListener(this);


            }

            imgViewerGroupIcon.setOnClickListener(this);
            btnAddGroup.setOnClickListener(this);
            btnDeleteGroup.setOnClickListener(this);
            btnSaveGroup.setOnClickListener(this);
            btnCancelChangeGroup.setOnClickListener(this);
            btnSaveRename.setOnClickListener(this);

        }
        else {
            finish();
        }
    }


    private void populateActivityContentsForEditName(int appGroupId) {

        if(appGroupId == Constants.INIT_EVERYTHING_FROM_SCRATCH){
            txtErrGroupName.setText("");
            txtAddEditGroupTitle.setText("");
            btnDeleteGroup.setVisibility(View.GONE);
            btnSaveRename.setVisibility(View.GONE);
            currentSelectedItemId=Constants.INIT_EVERYTHING_FROM_SCRATCH;
            imgViewerGroupIcon.setImageResource(0);
            iconName="";
            return;
        }
        else
        {
            currentSelectedItemId = appGroupId;
            GroupsDbRealmStruct gp = realmGroupsHelper.getGroupIfoByGroupCode(appGroupId);

            txtAddEditGroupTitle.setText(gp.getTitle());


            if(gp.getIcon().equals(Constants.DEFAULT_APPS_ICON))
            {
                imgViewerGroupIcon.setImageResource(R.drawable.ic_action_coffee);
                //iconName=Constants.DEFAULT_APPS_ICON;
            }
            else{
                fetchImage(gp.getIcon());
            }

        }

    }

    private void populateActivityContentsForGroups(){

            ArrayList<CheesesSortingStruct> mCheeseList = new ArrayList<CheesesSortingStruct>();

            ArrayList<GroupsModel> gpModel = realmGroupsHelper.findAllGroupsByOrder();

            for (int i = 0; i < gpModel.size(); ++i) {
                mCheeseList.add(new CheesesSortingStruct(gpModel.get(i).getGroupName(), gpModel.get(i).getAppGroupOrder(), gpModel.get(i).getId()));
            }

            adapter = new StableArrayAdapter(this, R.layout.groups_ordering_text_view, mCheeseList);

            listviewGroupOrders.setCheeseList(mCheeseList);
            listviewGroupOrders.setAdapter(adapter);
            listviewGroupOrders.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Constants.REQUEST_CODE_ICON_CHOOSE_ACTIVITY){

            if(data!=null){
                fetchImage(data.getStringExtra(Constants.ICON_NAME_FROM_ASSETS));
            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e("DBG", "AddEditGroupItem onPause");
    }

    private void fetchImage(String iconFileName){

        iconName = iconFileName;

        AssetManager assetManager = getAssets();

        try {
            InputStream is = assetManager.open("group_images/" + iconName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imgViewerGroupIcon.setImageBitmap(bitmap);
        }
        catch (Exception e){

        }

    }

    @Override
    public void onBackPressed() {
        AppLauncherFragment.returnFromFragmentForResult= true;
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        String str = txtAddEditGroupTitle.getText().toString().trim();
        switch (v.getId()){
            case R.id.imgViewerGroupIcon:
                if(isAddOrEdit || currentSelectedItemId > Constants.INIT_EVERYTHING_FROM_SCRATCH)
                {
                    Intent intent = new Intent(this, IconChooserActivity.class);
                    startActivityForResult(intent, Constants.REQUEST_CODE_ICON_CHOOSE_ACTIVITY);
                }
                break;
            case R.id.btnAddGroup:

                if(str.length() > 0) {

                    if(iconName.length()==0)
                        iconName=Constants.DEFAULT_APPS_ICON;

                    realmGroupsHelper.add(str, iconName);

                    onBackPressed();

               }
                else{
                    showError("Enter something as group name!");
                }

                break;
            case R.id.btnDeleteGroup:
                if(str.length()>0) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);


                    adb.setMessage("Are you sure want to delete " + txtAddEditGroupTitle.getText().toString() + " group? \n Your apps will be safe!");


                    adb.setTitle("Note");


                    adb.setIcon(android.R.drawable.ic_dialog_alert);


                    adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        RealmAppHelper realmAppHelper = new RealmAppHelper(AddEditGroupItem.this);

                       // realmAppHelper.updateAppGroupAfterDelete(currentSelectedItemId);

                            realmGroupsHelper.deleteGroupById(currentSelectedItemId);

                            btnDeleteGroup.setVisibility(View.GONE);
                            btnSaveRename.setVisibility(View.GONE);

                            populateActivityContentsForEditName(Constants.INIT_EVERYTHING_FROM_SCRATCH);
                            populateActivityContentsForGroups();
                            Utility.snackMaker(txtAddEditGroupTitle,"Group removed!", "Info", ContextCompat.getColor(AddEditGroupItem.this, R.color.yellow), Snackbar.LENGTH_SHORT);

                        }
                    });


                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    adb.show();
                }
                else
                {
                    showError("Select a group from the list!");

                }
                break;
            case R.id.btnSaveGroupOrder:

                //If list is empty
                if(listviewGroupOrders.getAdapter().getCount()==0) {
                    onBackPressed();
                    return;
                }

                for(int i=0 ; i< listviewGroupOrders.getAdapter().getCount();i++){

                        CheesesSortingStruct item = (CheesesSortingStruct) listviewGroupOrders.getItemAtPosition(i);

                        realmGroupsHelper.updateGroupOrderById(item.getGroupId(), i);
                    }

                populateActivityContentsForGroups();
                Utility.snackMaker(txtAddEditGroupTitle,"Order of groups saved.", "Info", ContextCompat.getColor(AddEditGroupItem.this, R.color.yellow), Snackbar.LENGTH_SHORT);
                break;
            case R.id.btnCancelChangeGroup:
                onBackPressed();
                break;
            case R.id.btnSaveRename:
                if(str.length()>0) {

                    if(iconName.length()==0)
                        iconName = Constants.DEFAULT_APPS_ICON;

                    realmGroupsHelper.renameGroupById(currentSelectedItemId, str, iconName);

                    populateActivityContentsForEditName(Constants.INIT_EVERYTHING_FROM_SCRATCH);
                    populateActivityContentsForGroups();
                    Utility.snackMaker(txtAddEditGroupTitle,"Group renamed", "Info", ContextCompat.getColor(AddEditGroupItem.this, R.color.yellow), Snackbar.LENGTH_SHORT);

                }
                else
                {
                    if(currentSelectedItemId>Constants.INIT_EVERYTHING_FROM_SCRATCH)
                        showError("Enter something as group name!");
                    else
                        showError("Select a group from the list!");

                }
                break;

        }


    }

    private void showError(String errorMessage){

        txtErrGroupName.setTextColor(ContextCompat.getColor(this, R.color.red));

        AlphaAnimation alpha = new AlphaAnimation(0F, 1.0F);
        alpha.setDuration(300);
        alpha.setRepeatCount(3);
        alpha.setFillAfter(true);
        txtErrGroupName.setText(errorMessage);
        txtErrGroupName.startAnimation(alpha);
        txtErrGroupName.setVisibility(View.VISIBLE);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheesesSortingStruct item = (CheesesSortingStruct) parent.getItemAtPosition(position);
        btnDeleteGroup.setVisibility(View.VISIBLE);
        btnSaveRename.setVisibility(View.VISIBLE);
        populateActivityContentsForEditName(item.getGroupId());

        Log.e("DBG", item.getGroupName());

    }

    }
