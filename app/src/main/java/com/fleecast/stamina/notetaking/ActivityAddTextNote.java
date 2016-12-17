package com.fleecast.stamina.notetaking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.NoteInfoRealmStruct;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.models.TempNoteInfoStruct;
import com.fleecast.stamina.utility.Constants;
import java.util.Date;


public class ActivityAddTextNote extends AppCompatActivity {


    private RealmNoteHelper realmNoteHelper;
    private NoteInfoRealmStruct noteInfoRealmStruct;
    private EditText txtDescription;
    private EditText txtTitle;
    private Toolbar mToolbar;                              // Declaring the Toolbar Object
    private MenuItem mnuItemDeleteNote;
    private MenuItem mnuItemSaveNote;
    private int dbId;
    private MyApplication myApplication;
    private String TAG = "Add Activity";

    private WindowManager windowManager;
    private Point szWindow = new Point();
    //private RelativeLayout recorderControlsLayout;

    private int currentNoteType=0;
    private boolean weAreInEditMode=false;
    private boolean itIsTotallyNewNote =true;
    private boolean skipSaveOnPause =false;
/*    private MenuItem mnuItemRedo;
    private MenuItem mnuItemUndo;*/
    private UndoRedoHelper undoRedoHelper;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        intentHandler(intent);

        Log.e(TAG, "Magic " + dbId);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.text_note_add_activity);

        myApplication =  (MyApplication)getApplicationContext();

        realmNoteHelper = new RealmNoteHelper(ActivityAddTextNote.this);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar_add_text_note); // Attaching the layout to the toolbar object

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        txtTitle = (EditText) findViewById(R.id.inputTextNoteTitle);
        txtDescription = (EditText) findViewById(R.id.inputTextNoteDescription);

        txtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtDescription.getText()!=null)
                    myApplication.tmpCurrentTextNoteInfoStruct.setDescription(txtDescription.getText().toString());
                myApplication.setTextNoteSaved(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtTitle.getText().toString()!=null)
                    myApplication.tmpCurrentTextNoteInfoStruct.setTitle(txtTitle.getText().toString());
                myApplication.setTextNoteSaved(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        populateGUI();

    }

    private void populateUserInterface(Menu menu, boolean populateForAddOrEdit){

       // mnuItemDeleteNote = menu.findItem(R.id.action_delete);
        //mnuItemDeleteNote.setVisible(false);
        mnuItemSaveNote = menu.findItem(R.id.action_save);
     /*   mnuItemUndo = menu.findItem(R.id.action_undo);
        mnuItemRedo = menu.findItem(R.id.action_redo);*/
/*        recorderControlsLayout = (RelativeLayout) findViewById(R.id.recorderControlsLayout);
        recorderControlsLayout.setVisibility(View.INVISIBLE);*/
        undoRedoHelper = new UndoRedoHelper(txtDescription);

        intentHandler(getIntent());
    }


    private void intentHandler(Intent intent){

        if(intent.hasExtra(Constants.EXTRA_EDIT_NOTE_AND_NO_RECORD)){

            dbId = intent.getIntExtra(Constants.EXTRA_EDIT_NOTE_AND_NO_RECORD,Constants.CONST_NULL_ZERO);
            Log.e("DBG","CC " + dbId);

            if(dbId>0 && realmNoteHelper.isExist(dbId)){


                weAreInEditMode=true;
                noteInfoRealmStruct = realmNoteHelper.getNoteById(dbId);

                myApplication.setCurrentOpenedTextNoteId(dbId);

                myApplication.tmpCurrentTextNoteInfoStruct = new TempNoteInfoStruct();
                myApplication.tmpCurrentTextNoteInfoStruct.setId(dbId);
                myApplication.tmpCurrentTextNoteInfoStruct.setDescription(noteInfoRealmStruct.getDescription());
                myApplication.tmpCurrentTextNoteInfoStruct.setTitle(noteInfoRealmStruct.getTitle());
                myApplication.tmpCurrentTextNoteInfoStruct.setTag(noteInfoRealmStruct.getColor());


                txtTitle.setText(noteInfoRealmStruct.getTitle());
                txtDescription.setText(noteInfoRealmStruct.getDescription());
                txtDescription.setSelection(txtDescription.getText().length());

                mnuItemSaveNote.setVisible(true);
                itIsTotallyNewNote=false;

                currentNoteType = Constants.CONST_IS_EDIT_ONLY_TEXT;
            }



        } else if(intent.hasExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_NO_RECORD)){


                Log.e("DBG","BB");

                initVariables();

                mnuItemSaveNote.setVisible(true);
                currentNoteType = Constants.CONST_IS_ONLY_TEXT;

        }
        setIntent(new Intent());

    }


    private void saveNote(boolean showPrompt,boolean killAfterSave) {
        String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();

        if((itIsTotallyNewNote && title.isEmpty() && description.isEmpty()) || myApplication.isTextNoteSaved()){
            //Release resources
            myApplication.setTextNoteSaved(true);
            myApplication.setCurrentOpenedTextNoteId(Constants.CONST_NULL_ZERO);
            myApplication.tmpCurrentTextNoteInfoStruct = null;
            skipSaveOnPause =true;
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }else {

            if (!myApplication.isTextNoteSaved() || title.length() == 0) {

                // If user didn't add any title
                if (title.length() == 0) {
                    title = "Untitled";
                    txtTitle.setText(title);
                    txtTitle.setSelection(txtTitle.getText().length());
                    myApplication.tmpCurrentTextNoteInfoStruct.setTitle(title);
                } else {
                    title = myApplication.tmpCurrentTextNoteInfoStruct.getTitle();
                    description = myApplication.tmpCurrentTextNoteInfoStruct.getDescription();
                }

                Date updateTime = new Date();

                Date createdTime = null;

                if (weAreInEditMode)
                    createdTime = noteInfoRealmStruct.getCreateTimeStamp();

                if (!weAreInEditMode)
                    realmNoteHelper.addNote(dbId, title, description, false, updateTime, createdTime, null, null, Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL, null, Constants.CONST_NOTETYPE_TEXT);
                else
                    realmNoteHelper.updateNotes(dbId, title, description, updateTime, Constants.CONST_NOTETYPE_TEXT);
                myApplication.setTextNoteSaved(true);
                myApplication.setCurrentOpenedTextNoteId(dbId);

                if (showPrompt)
                    Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();

                if(killAfterSave) {
                    Log.e("DBG","Killed");
                    myApplication.setCurrentOpenedTextNoteId(Constants.CONST_NULL_ZERO);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result","sd");
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        }
    }

    private void initVariables(){
        itIsTotallyNewNote=true;
        skipSaveOnPause =false;
        myApplication.tmpCurrentTextNoteInfoStruct = new TempNoteInfoStruct();
        // Creating unique id for db as primary key
        dbId = (int) (System.currentTimeMillis() / 1000);
        myApplication.tmpCurrentTextNoteInfoStruct.setId(dbId);
        myApplication.setCurrentOpenedTextNoteId(dbId);
        txtDescription.setText("");
        txtTitle.setText("");
        currentNoteType= Constants.CONST_NULL_ZERO;
        weAreInEditMode=false;
        currentNoteType=0;

        // Flashing title
        ColorDrawable[] color = {new ColorDrawable(Color.RED), new ColorDrawable(ContextCompat.getColor(this, R.color.blue_eyes))};
        TransitionDrawable trans = new TransitionDrawable(color);
        mToolbar.setBackground(trans);
        trans.startTransition(Constants.TRANSIATION_TIME);
    }


    private void populateGUI(){

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {

            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        //myApplication = (MyApplication)getApplicationContext();


  /*      btnDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddTextNote.this);


                adb.setMessage("Are you sure want to delete your latest record?");


                adb.setTitle("Note");

                //adb.setIcon(android.R.drawable.ic_dialog_alert);

                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        File file = new File(latestRecordFileName);

                        if (file.exists()) {

                            file.delete();

                            mnuItemPlayRecord.setVisible(false);
                            btnDeleteRecord.setVisibility(View.INVISIBLE);

                            if(isThereAnyRecordInPath(latestRecordFileName))
                            {
                                btnRecordsListPlayer.setVisibility(View.VISIBLE);
                            }else
                            {
                                btnRecordsListPlayer.setVisibility(View.INVISIBLE);
                            }

                            latestRecordFileName="";
                            Log.e(TAG, "Record deleted!");
                        }

                    }
                });


                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                adb.show();


            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!skipSaveOnPause) {
            Log.e("DBG","A onPause()");
            saveNote(true, false);
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Log.e("DBG","B onBackPressed()");
        skipSaveOnPause=true;
        saveNote(true,true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_note_add, menu);

        populateUserInterface(menu, true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveNote(true, true);
            return true;
        }
        else if (id == R.id.action_undo) {
            undoRedoHelper.undo();
            return true;

        }
        else if (id == R.id.action_redo) {
            undoRedoHelper.redo();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}