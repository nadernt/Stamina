package com.fleecast.stamina.launcher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.support.v7.widget.PopupMenu;
import android.widget.ProgressBar;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.customgui.CustomRoundButton;
import com.fleecast.stamina.customgui.OnSwipeTouchListener;
import com.fleecast.stamina.models.AppDbRealmStruct;
import com.fleecast.stamina.models.GridViewAppItemStruct;
import com.fleecast.stamina.models.AppLauncherGridViewAdapter;
import com.fleecast.stamina.models.GroupsModel;
import com.fleecast.stamina.models.RealmAppHelper;
import com.fleecast.stamina.models.RealmGroupsHelper;
import com.fleecast.stamina.utility.ColoredSnackBar;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Prefs;
import com.fleecast.stamina.utility.Utility;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

public class AppLauncherFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,OnUserAppsUpdatesEventListener{

    private List<GridViewAppItemStruct> mItems;        // GridView items list
    private AppLauncherGridViewAdapter mAdapter;        // GridView adapter
    private GridViewAppInfoLoader mLoader;    // the application info loader
    private EditText filterText;
    private AppsUpdateReceiver mReceiver;
    private boolean blIsInitialFinished=false;
    //private GestureOverlayView gesturePad;
    //private GestureLibrary gLibrary;
    private MyApplication myApplication;
   // private Point szWindow = new Point();
    //private WindowManager windowManager;
    //private int gesturePadHeight = 0;
    private GridView gridView;
    private View fragmentView;

    private Snackbar snackbar;
    private LinearLayout linearLayoutCategoryButtonContainer;
    private ArrayList<CustomRoundButton> customRoundButton = new ArrayList<>();
    private boolean isDragStarted = false;
    private HorizontalScrollView hList;
    private PopupMenu popupMenuOptions;
    private GridViewAppItemStruct draggedItem;
    private RealmAppHelper realmAppHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("DBG", "onCreate ");

        myApplication = (MyApplication) getContext().getApplicationContext();


    /*    if(Prefs.getBoolean(Constants.PREF_SORT_IS_ALPHABETIC_OR_DATE, true))
            myApplication.setSortLauncherAppsByAlphabet(true);
        else
            myApplication.setSortLauncherAppsByAlphabet(false);*/

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        mReceiver = new AppsUpdateReceiver(this);
        LauncherDialogActivity.myActivityInstance.registerReceiver(mReceiver, intentFilter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("DBG", "onCreateView");

        // inflate the root view of the fragment
        fragmentView = inflater.inflate(R.layout.fragment_app_launcher, container, false);

        filterText = (EditText) fragmentView.findViewById(R.id.search_box);
        filterText.addTextChangedListener(filterTextWatcher);


        // Check if back key pressed and virtual keyboard is showing remove the soft keyboard and clean the text inside of textedit.
        filterText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    filterText.setText("");
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(fragmentView.getWindowToken(), 0);
                }

                return false;

            }
        });


        if(!myApplication.getIsAppsListLoading()) {

            // initialize the items list
            mItems = new ArrayList<GridViewAppItemStruct>();


            // initialize the adapter

            mAdapter = new AppLauncherGridViewAdapter(LauncherDialogActivity.myActivityInstance, mItems);


            // initialize the GridView
            gridView = (GridView) fragmentView.findViewById(R.id.gridView);
            gridView.setAdapter(mAdapter);
            gridView.setOnItemClickListener(this);
            gridView.setOnItemLongClickListener(this);
            gridView.setOnDragListener(new MyGridViewDragListener());
            realmAppHelper = new RealmAppHelper(fragmentView.getContext());

            loadGrid();



            filterText.setOnTouchListener(new OnSwipeTouchListener(LauncherDialogActivity.myActivityInstance) {

                /***** This is for bypassing the swipe of the fragment and the gesture *****/
                public boolean onTouch(View v, MotionEvent event) {
                    // To bypass the scroll of fragments.
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            int clickedArea = (filterText.getWidth() - filterText.getPaddingRight() - calcPixelIndependent(Constants.SIZE_OF_DRAWABLE_OF_EDITTEXT_FILTER));
                            if((int) event.getX() >  clickedArea)
                            {
                                customRoundButton.get(0).performClick();

                                return true;
                            }

                    }

                    return getGestureDetector().onTouchEvent(event);
                }


                public void onSwipeRight() {

                }

                public void onSwipeLeft() {
                    filterText.setText("");
                }

                public void onClick() {
                    filterText.requestFocus();

                    showHideVirtualKeyboard(filterText,LauncherDialogActivity.myActivityInstance,true);

                }

            });


        }

        hList =(HorizontalScrollView) fragmentView.findViewById(R.id.hList);

        linearLayoutCategoryButtonContainer=(LinearLayout) fragmentView.findViewById(R.id.layCategoryContainer);

        populateAppGroupsBar();

        return fragmentView;
    }

    private void populateAppGroupsBar(){

        if(linearLayoutCategoryButtonContainer.getChildCount() > 0)
            linearLayoutCategoryButtonContainer.removeAllViews();

        RealmGroupsHelper realmGroupsHelper = new RealmGroupsHelper(fragmentView.getContext());

        final ArrayList<GroupsModel> gpModel = realmGroupsHelper.findAllGroupsByOrder();

        customRoundButton.clear();


        int appsIconSize = 36; // small size
        if(Prefs.getBoolean(Constants.PREF_GROUP_ICON_SIZE,true)) //large size
            appsIconSize = 42;

        float buttonCircleSize =  28.0f;

        int circleCenterColor = ContextCompat.getColor(getActivity(),R.color.yellow_orange);
        int outerCirclesStorkColor = ContextCompat.getColor(getActivity(), R.color.aureolin);
        int textColor = ContextCompat.getColor(getActivity(), R.color.white);

        float buttonRectSizeWidth = calcPixelIndependent(88);
        float buttonRectSizeHeight = calcPixelIndependent(66);

        customRoundButton.add(new CustomRoundButton(fragmentView.getContext(),
                "All Apps",
                calcPixelIndependent(12),
                getResources().getDrawable(R.drawable.ic_action_tiles_large),
                appsIconSize,
                buttonCircleSize,
                circleCenterColor,
                outerCirclesStorkColor,
                textColor));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) buttonRectSizeWidth, (int)buttonRectSizeHeight
        );

        customRoundButton.get(0).setAppGroupCode(Constants.ALL_APPS_DEFAULT_GROUP);

        params.setMargins(20, 0, 30, 0);

        customRoundButton.get(0).setLayoutParams(params);

        linearLayoutCategoryButtonContainer.addView(customRoundButton.get(0));

        AssetManager assetManager = getActivity().getAssets();

        for (int i=0; i< gpModel.size(); i++) {

            Drawable drawable=null;

            try {

                String iconPath  = gpModel.get(i).getIcon();

                if(!iconPath.equals(Constants.DEFAULT_APPS_ICON)) {
                    InputStream is = assetManager.open("group_images/" + iconPath);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    drawable = new BitmapDrawable(getResources(), bitmap);
                }
                else
                {
                    drawable = getResources().getDrawable(R.drawable.ic_action_coffee);
                }

                customRoundButton.add (new CustomRoundButton(fragmentView.getContext(),
                        gpModel.get(i).getGroupName(),
                        calcPixelIndependent(12),
                        drawable,
                        appsIconSize,
                        buttonCircleSize,
                        circleCenterColor,
                        outerCirclesStorkColor,
                        textColor));

                params = new LinearLayout.LayoutParams(
                        (int) buttonRectSizeWidth, (int)buttonRectSizeHeight
                );

                customRoundButton.get(i+1).setAppGroupCode(gpModel.get(i).getAppGroupCode());

                customRoundButton.get(i+1).setGroupsModel(gpModel.get(i));

                params.setMargins(20, 0, 20, 0);

                customRoundButton.get(i+1).setLayoutParams(params);

                linearLayoutCategoryButtonContainer.addView(customRoundButton.get(i + 1));

                customRoundButton.get(i+1).setOnDragListener(new MyGridViewDragListener());

                customRoundButton.get(i+1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        applyFilter(v);
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        customRoundButton.add(new CustomRoundButton(fragmentView.getContext(),
                "Settings",
                calcPixelIndependent(12),
                getResources().getDrawable(R.drawable.ic_action_gear),
                appsIconSize,
                buttonCircleSize,
                circleCenterColor,
                outerCirclesStorkColor,
                textColor));


        params = new LinearLayout.LayoutParams(
                (int) buttonRectSizeWidth, (int)buttonRectSizeHeight
        );

        customRoundButton.get(customRoundButton.size()-1).setAppGroupCode(Constants.APP_GROUPS_SETTING);

        params.setMargins(30, 0, 20, 0);

        customRoundButton.get(customRoundButton.size()-1).setLayoutParams(params);

        linearLayoutCategoryButtonContainer.addView(customRoundButton.get(customRoundButton.size() - 1));


        customRoundButton.get(0).setOnDragListener(new MyGridViewDragListener());

        customRoundButton.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //myApplication.setIsUserTerminateApp(true);
                //myApplication.setIsAppListLoaded(true);
                applyFilter(v);
            }
        });

        customRoundButton.get(customRoundButton.size() - 1).setOnDragListener(new MyGridViewDragListener());

        customRoundButton.get(customRoundButton.size()-1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup(v);

                if (gpModel.size() > 0)
                    menuItemControl(popupMenuOptions, R.id.menu_manage_groups, Constants.OPTION_MENU_ENABLE_DISABLE, true);
                else
                    menuItemControl(popupMenuOptions, R.id.menu_manage_groups, Constants.OPTION_MENU_ENABLE_DISABLE, false);
            }
        });

    }

    private void applyFilter(View v){

        CustomRoundButton crb = (CustomRoundButton) v;
        filterText.removeTextChangedListener(filterTextWatcher);
        filterText.setText("");
        filterText.addTextChangedListener(filterTextWatcher);
        myApplication.setCurrentGroupFilter(crb.getAppGroupCode());

        Drawable db = resizeDrawable(crb.getIcon());
        // crb.redrawButton(true);

        filterText.setCompoundDrawablesWithIntrinsicBounds(null, null, db, null);
        //db = null;
        mAdapter.applyGroupFilterToAdapter();

    }

    private Drawable resizeDrawable(Drawable image) {

        Bitmap oldBitmap = ((BitmapDrawable)image).getBitmap();

        Bitmap newBitmap = Bitmap.createBitmap(oldBitmap.getWidth(), oldBitmap.getHeight(), oldBitmap.getConfig());

        Canvas canvas = new Canvas(newBitmap);

        canvas.drawColor(Color.LTGRAY);

        canvas.drawBitmap(oldBitmap, 0, 0, null);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(newBitmap,
                calcPixelIndependent(Constants.SIZE_OF_DRAWABLE_OF_EDITTEXT_FILTER),
                calcPixelIndependent(Constants.SIZE_OF_DRAWABLE_OF_EDITTEXT_FILTER),
                false);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmapResized);

        return bitmapDrawable;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Constants.REQUEST_ADD_EDIT_GROUP_ACTIVITY)
        {
            populateAppGroupsBar();
        }

    }

    /**
     * This method has makes otions for hide,visible,enable,disable, checked, unchecked items of a popup menu.
     *
     * @param popupMenu
     * @param resourceIdItem resource id of the item
     * @param optionHideEnableChecked chose we want to hide or show or enable or disable the menu
     * @param menuFlag boolean option to put in effect the menu command
     */
    private void menuItemControl(PopupMenu popupMenu, int resourceIdItem, int optionHideEnableChecked, boolean menuFlag){

        Menu popupMenuOptionsItems = popupMenu.getMenu();
        //popupMenuOptionsItems.

        if(optionHideEnableChecked==0)
            popupMenuOptionsItems.findItem(resourceIdItem).setVisible(menuFlag);
        else if(optionHideEnableChecked==1)
            popupMenuOptionsItems.findItem(resourceIdItem).setEnabled(menuFlag);
        else if (optionHideEnableChecked == 2)
            popupMenuOptionsItems.findItem(resourceIdItem).setChecked(menuFlag);

    }

    /**
     * Set the title of menu a menu
     *
     * @param popupMenu
     * @param resourceIdItem
     * @param setTitle
     */
    private void menuItemText(PopupMenu popupMenu, int resourceIdItem, String setTitle){

        Menu popupMenuOptionsItems = popupMenu.getMenu();
        MenuItem mi = popupMenuOptionsItems.findItem(resourceIdItem);
        mi.setTitle(setTitle);
    }

    // Display anchored popup menu based on view selected
    private void showPopup(View v) {
        popupMenuOptions = null;
       // if(popupMenuOptions == null) {
            popupMenuOptions = new PopupMenu(getContext(), v);
            MenuInflater inflater = popupMenuOptions.getMenuInflater();
            inflater.inflate(R.menu.popup_launcher_options, popupMenuOptions.getMenu());
        
        //popupMenuOptions.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
       // popupMenuOptions.getMenu().setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        //}

        if (Prefs.getBoolean(Constants.PREF_SORT_IS_ALPHABETIC_OR_DATE, true)) {
            menuItemText(popupMenuOptions, R.id.menu_sort_order, "Sort by: Time");
        } else {
            menuItemText(popupMenuOptions, R.id.menu_sort_order, "Sort by: A↓Z");
        }

        // Setup menu item selection
        popupMenuOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.menu_add_group:
                        intent = new Intent(getActivity(), AddEditGroupItem.class);
                        intent.putExtra(Constants.IS_ADD_OR_EDIT, true);
                        startActivityForResult(intent, Constants.REQUEST_ADD_EDIT_GROUP_ACTIVITY);
                        popupMenuOptions.dismiss();
                        return true;
                    case R.id.menu_manage_groups:
                        intent = new Intent(getActivity(), AddEditGroupItem.class);
                        intent.putExtra(Constants.IS_ADD_OR_EDIT, false);
                        startActivityForResult(intent, Constants.REQUEST_ADD_EDIT_GROUP_ACTIVITY);
                        popupMenuOptions.dismiss();
                        return true;
                    case R.id.menu_sort_order:
                        if (!myApplication.getIsAppsListLoading()) {

                            if (Prefs.getBoolean(Constants.PREF_SORT_IS_ALPHABETIC_OR_DATE, false))
                                sortOrder(false);
                            else
                                sortOrder(true);
                            popupMenuOptions.dismiss();
                        } else {
                            Utility.snackMaker(gridView,"Wait the load should finish!?", "Note", ContextCompat.getColor(getActivity(), R.color.yellow), Snackbar.LENGTH_LONG);
                        }


                        return true;
                    default:
                        return false;
                }
            }
        });

        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popupMenuOptions.show();
    }

    private class MyGridViewDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:

                    if(!isDragStarted) {
                        isDragStarted = true;
                        Vibrator vib = (Vibrator) LauncherDialogActivity.myActivityInstance.getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(30);
                    }
                   // hList.setVisibility(View.VISIBLE);
                    return true;
                case DragEvent.ACTION_DROP:

                    // We drag the item on top of the one which is at itemPosition
                    //int itemPosition = gridView.pointToPosition((int) event.getX(), (int) event.getY());
                    // We can even get the view at itemPosition thanks to get/setid
                    //View itemView = gridView.findViewById(itemPosition );
                    //Log.d("MMMMMMMM", v.getClass().getSimpleName()+ " \n" + v.getClass().toString());

                    if(v.getClass().getSimpleName().equals("CustomRoundButton")) {
                        CustomRoundButton cb = (CustomRoundButton) v;

                        if (cb.getAppGroupCode() != Constants.APP_GROUPS_SETTING) {

                        /*    WindowManager wm = (WindowManager) fragmentView.getContext().getSystemService(Context.WINDOW_SERVICE);

                            Display display = wm.getDefaultDisplay();

                            int scrollX = 0;
                            //Log.e("GGGGGGGGG",btnRC1.getLeft() + " == " + display.getWidth())

                                scrollX = (cb.getLeft() - (display.getWidth() / 2)) + (cb.getWidth() / 2);


                            hList.smoothScrollTo(scrollX, 0);*/

                            // Making date back to first android release date!
                            SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                            String strdate = "02-09-2008 11:11:11";

                            try {
                                Date newdate = dateformat.parse(strdate);
                                realmAppHelper.add(draggedItem.getTitle(), draggedItem.getActivityInfo().getPackageName(), cb.getAppGroupCode(), 0, newdate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            for(int i=0; i<mItems.size();i++){

                                mItems.set(i, matchGroupCodes(mItems.get(i)));
                            }

                            //mAdapter.notifyDataSetChanged();

                            Log.e("BABA", cb.getAppGroupCode() + " " + myApplication.getCurrentGroupFilter());

                            // Check if it is not ALL APPs option refresh the grid
                            if( myApplication.getCurrentGroupFilter() != Constants.ALL_APPS_DEFAULT_GROUP) {

                                applyFilter(v);
                            }


                        }
                        else if(cb.getAppGroupCode() == Constants.APP_GROUPS_SETTING)
                        {
                            final AlertDialog.Builder adb = new AlertDialog.Builder(fragmentView.getContext());
                            //LayoutInflater inflater = new LayoutInflater();
                            View dialogView = View.inflate(fragmentView.getContext(), R.layout.dialog_app_uninstall_info, null);
                            adb.setView(dialogView);
                            //adb.setMessage("Are you sure want to delete  group? \n Your apps will be safe!");


                            adb.setTitle("Options:");


                            adb.setIcon(android.R.drawable.ic_dialog_info);

                            final AlertDialog dialog = adb.create();

                            Button btnAppInfo = (Button) dialogView.findViewById(R.id.btnAppInfo);
                            //Button btnDlgAppUninstallInfoClose = (Button) dialogView.findViewById(R.id.btnDlgAppUninstallInfoClose);
                            Button btnUninstall = (Button) dialogView.findViewById(R.id.btnUninstall);

                            btnAppInfo.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.azure));
                            btnAppInfo.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_smoke));

                            btnUninstall.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_orange));
                            btnUninstall.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_smoke));



                                final Utility util = new Utility();

                            Intent intent = new Intent(Intent.ACTION_MAIN, null);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setAction(Intent.ACTION_MAIN);


                                intent.setPackage(draggedItem.getActivityInfo().getPackageName());


                            PackageManager packageManager = getActivity().getPackageManager(); // the PackageManager for loading the data
                            ResolveInfo rInfo = packageManager.resolveActivity(intent, 0);

                            final ActivityInfo aInfo = rInfo.activityInfo;

                            if(util.isSystemApp(rInfo.activityInfo.applicationInfo))
                                btnUninstall.setVisibility(View.GONE);

                            btnUninstall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Check if package is not system package.
                                    if(!util.isSystemApp(aInfo.applicationInfo)) {
                                        realmAppHelper.delete(draggedItem.getActivityInfo().getPackageName());
                                        Uri packageUri = Uri.parse("package:" + draggedItem.getActivityInfo().getPackageName());
                                        Intent uninstallIntent =
                                                new Intent(Intent.ACTION_DELETE, packageUri);
                                        startActivity(uninstallIntent);
                                    }
                                    dialog.dismiss();
                                }
                            });

                            btnAppInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utility.showInstalledAppDetails(fragmentView.getContext(), draggedItem.getActivityInfo().getPackageName());
                                    dialog.dismiss();
                                }
                            });

                            /*btnDlgAppUninstallInfoClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            });*/

                            dialog.show();
                        }

                        return true;
                    }
                    else
                    {
                        return false;
                    }
                /* If you try the same thing in ACTION_DRAG_LOCATION, itemView
                 * is sometimes null; if you need this view, just return if null.
                 * As the same event is then fired later, only process the event
                 * when itemView is not null.
                 * It can be more problematic in ACTION_DRAG_DROP but for now
                 * I never had itemView null in this event. */
                    // Handle the drop as you like
                    //return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    isDragStarted= false;
                    redrawGroupButtons();
                    return true;
            }

            return false;
        }

    }

    private void redrawGroupButtons(){

        for (int i=0; i< customRoundButton.size(); i++) {

            //Clear the highlights
            customRoundButton.get(i).redrawButton(false);

        }
    }

    private void blinkAfterClick(){
        hList.setVisibility(View.GONE);
        //linearLayoutCategoryButtonContainer.setVisibility(View.GONE);
        /*AlphaAnimation alpha = new AlphaAnimation(0.5F, 1.0F);
        alpha.setDuration(2000); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        linearLayoutCategoryButtonContainer.startAnimation(alpha);*/

    }

    private int calcPixelIndependent(int pixelToConvert){

//        float scale = (int) getResources().getDisplayMetrics().density;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelToConvert, getResources().getDisplayMetrics());
        //     return (int) (pixelToConvert * scale + 0.5f);

    }


    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }



    private void sortOrder(boolean sortIsAlphabeticOrDate){

           filterText.setText("");

            if (sortIsAlphabeticOrDate) {
                Prefs.putBoolean(Constants.PREF_SORT_IS_ALPHABETIC_OR_DATE, true);
                menuItemText(popupMenuOptions, R.id.menu_sort_order, "Sort by: Time");
            } else {
                Prefs.putBoolean(Constants.PREF_SORT_IS_ALPHABETIC_OR_DATE, false);
                menuItemText(popupMenuOptions, R.id.menu_sort_order, "Sort by: A↓Z");
            }

            loadGrid();

    }

    @Override
    public void onStart() {
        super.onStart();

        if(filterText!=null) {
            filterText.setText("");
            filterText.clearFocus();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        showHideVirtualKeyboard(filterText, LauncherDialogActivity.myActivityInstance, false);

        //gesturePad.setVisibility(View.GONE);
    }

    private void loadGrid() {

        Log.e("DBG", "Load Grid Called!");

        // start loading
        mLoader = new GridViewAppInfoLoader();

        // If We have new refreshing by event.
        if(myApplication.getIsAppsListLoading())
        {
            mLoader.cancel(true);
            myApplication.setIsAppsListLoading(false);
        }

        mLoader.execute(new PackageManagerStruct(LauncherDialogActivity.myActivityInstance.getPackageManager(), Constants.LIST_FOR_GRID));

        //mAdapter.notifyDataSetChanged();
    }


  /*  @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

        ArrayList<Prediction> predictions = gLibrary.recognize(gesture);

        if (predictions.size() > 0) {

            Prediction prediction = predictions.get(0);

            if (prediction.score > 3.0) {

                //Toast.makeText(LauncherDialogActivity.myActivityInstance, prediction.name,Toast.LENGTH_SHORT).show();
                if(prediction.name.equals("del")) {

                    if(filterText.length()>0)
                        filterText.setText(filterText.getText().subSequence(0, filterText.getText().length() - 1));


                }else {

                    filterText.setText(filterText.getText() + prediction.name.toLowerCase());
                }

                filterText.setSelection(filterText.getText().length());
            }else{

                snackMaker("What you mean?","Help",ContextCompat.getColor(getActivity(), R.color.yellow),Snackbar.LENGTH_SHORT);

            }
        }
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }



    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if (count < before) {
                // We're deleting char so we need to reset the adapter data
                mAdapter.resetData();
            }

            if(mAdapter!=null) {
                mAdapter.getFilter().filter(s);

                gridView.setSelection(0);
            }
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver!=null) {
            Log.e("DBG", "LauncherDialogActivity service unregister called!");
            LauncherDialogActivity.myActivityInstance.unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("DBG", "onDetach() called!");

        // cancel the loader if it is running
        if(mLoader != null) {
            mLoader.cancel(true);
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

            showHideVirtualKeyboard(filterText, LauncherDialogActivity.myActivityInstance, false);

        try {

            GridViewAppItemStruct item = (GridViewAppItemStruct) parent.getItemAtPosition(position);

            Utility utility = new Utility();

            if(!utility.isPackageInstalled(item.getActivityInfo().getPackageName(),getContext()))
            {
                Log.e("DBG", "App uninstalled!");
                realmAppHelper.delete(item.getActivityInfo().getPackageName());
                parent.removeViewAt(position);

            }
            else {


                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName(item.getActivityInfo().getPackageName(), item.getActivityInfo().getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                startActivity(intent);

                // Update app launch in database.
                realmAppHelper.updateLastUsageApp(item.getTitle(), item.getActivityInfo().getPackageName());

                Log.e("DBG", "Package exist.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void onAppsLauncherListChangedEvent() {
        loadGrid();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        showHideVirtualKeyboard(filterText,LauncherDialogActivity.myActivityInstance,false);

        draggedItem = (GridViewAppItemStruct) parent.getItemAtPosition(position);

        MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(view);

        view.startDrag(null, shadowBuilder, gridView.getItemAtPosition(position), 0);
        return true;

    }

    private void showHideVirtualKeyboard(View view,Context context , boolean showOrHide) {

        // Check if view has focus:
        if (view.hasFocus()) {

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (showOrHide) {

                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);

            } else {

                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }

        }
    }

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        private Point mScaleFactor;
        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            // Defines local variables
            int width;
            int height;

            // Sets the width of the shadow to half the width of the original View
            width = (int) (getView().getWidth() * 1.2);

            // Sets the height of the shadow to half the height of the original View
            height = (int) (getView().getHeight() * 1.2);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);
            // Sets size parameter to member that will be used for scaling shadow image.
            mScaleFactor = size;

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {

            // Draws the ColorDrawable in the Canvas passed in from the system.
            canvas.scale(mScaleFactor.x/(float)getView().getWidth(), mScaleFactor.y/(float)getView().getHeight());
            getView().draw(canvas);
        }

    }


    private class GridViewAppInfoLoader extends AsyncAppInfoLoader {
        private List<GridViewAppItemStruct> tmpItems  = new ArrayList<GridViewAppItemStruct>();        // GridView items list
        private ProgressBar pb ;

        @Override
        protected void onProgressUpdate(GridViewAppItemStruct... values) {
            if(myApplication.getIsUserTerminateApp()) {
                this.cancel(true);
            }
            // check that the fragment is still attached to activity
            if (LauncherDialogActivity.myActivityInstance != null) {
                // add the new item to the data set
                tmpItems.add(values[0]);

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            myApplication.setIsAppsListLoading(true);

            pb = (ProgressBar) fragmentView.findViewById(R.id.progressBarAppLoad);
            pb.setIndeterminate(true);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Integer result) {

                if (LauncherDialogActivity.myActivityInstance != null) {

                    for (int i = 0; i < tmpItems.size(); i++) {

                        tmpItems.set(i, matchGroupCodes(tmpItems.get(i)));
                    }

                    mItems.clear();
                    mItems = new ArrayList<GridViewAppItemStruct>(tmpItems);

                    // initialize the adapter
                    mAdapter = new AppLauncherGridViewAdapter(LauncherDialogActivity.myActivityInstance, mItems);
                    //gridView.setNumColumns(3);
                    gridView.setAdapter(mAdapter);

                    myApplication.setIsAppsListLoading(false);

                    pb.setVisibility(View.GONE);
                    customRoundButton.get(0).performClick();
                    Log.e("DBG", "Load Grid Finished!");

                }
        }
    }

    private GridViewAppItemStruct matchGroupCodes( GridViewAppItemStruct grdVwAppItm){

        realmAppHelper = new RealmAppHelper(getContext());

        RealmResults <AppDbRealmStruct>query = realmAppHelper.getAllAppsDatabaseInfo();

        for (int i=0; i<query.size(); i++){

            if(query.get(i).getAppPackageName().equals(grdVwAppItm.getActivityInfo().getPackageName())){

                return new GridViewAppItemStruct(grdVwAppItm.getIcon(),grdVwAppItm.getTitle(),grdVwAppItm.getActivityInfo(),query.get(i).getAppGroup());

            }


        }

        return grdVwAppItm;

    }

}