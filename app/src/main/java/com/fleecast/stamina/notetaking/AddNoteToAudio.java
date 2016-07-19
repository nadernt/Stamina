package com.fleecast.stamina.notetaking;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.AudioNoteInfoRealmStruct;
import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.utility.Constants;

/**
 * A login screen that offers login via email/password.
 */
public class AddNoteToAudio extends AppCompatActivity{


    // UI references.
    private EditText mTxtTitle;
    private EditText mTxtDescription;
    private TextView mTxtAudioNoteTitleError;
    private RealmAudioNoteHelper realmAudioNoteHelper;
    private int idParentDb;
    private int dbIdFile;
    private AudioNoteInfoRealmStruct audioNoteInfoRealmStruct;
    private Toolbar mToolbar;
    private boolean weTypedSomethingNew;
    private boolean weNeedUpdate;
    private UndoRedoHelper undoRedoHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note_to_audio);



        realmAudioNoteHelper = new RealmAudioNoteHelper(this);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar_audio_note); // Attaching the layout to the toolbar object

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        mTxtTitle = (EditText) findViewById(R.id.txtTitleAudioNote);
        mTxtDescription = (EditText) findViewById(R.id.txtDescriptionAudioNote);
        mTxtAudioNoteTitleError = (TextView) findViewById(R.id.txtAudioNoteTitleError);



        Intent intent = getIntent();

        idParentDb = intent.getIntExtra(Constants.EXTRA_AUDIO_NOTE_PARENT_DB_ID,Constants.CONST_NULL_MINUS);
        dbIdFile =  intent.getIntExtra(Constants.EXTRA_AUDIO_NOTE_FILE_DB_ID,Constants.CONST_NULL_MINUS);

        if(realmAudioNoteHelper.isNoteExist(dbIdFile)){
            audioNoteInfoRealmStruct = realmAudioNoteHelper.getNoteById( dbIdFile);
            mTxtTitle.setText( audioNoteInfoRealmStruct.getTitle());
            mTxtDescription.setText(audioNoteInfoRealmStruct.getDescription());
            weNeedUpdate=true;
         }


        mTxtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                weTypedSomethingNew = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTxtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                weTypedSomethingNew = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        undoRedoHelper = new UndoRedoHelper(mTxtDescription);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_note_add, menu);

        MenuItem mnuItemPlayRecord = menu.findItem(R.id.action_play_record);

        mnuItemPlayRecord.setVisible(false);

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
            if(saveNote())
            {
                Intent intentMessage=new Intent();
                intentMessage.putExtra(Constants.EXTRA_AUDIO_NOTE_FILE_DB_ID,dbIdFile);
                setResult(Constants.RESULT_CODE_REQUEST_DIALOG,intentMessage);
                // finish The activity
                finish();

            }
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

    private boolean saveNote() {

        String title =  mTxtTitle.getText().toString().trim();
        String description = mTxtDescription.getText().toString().trim();

        if(title.isEmpty()) {
            mTxtAudioNoteTitleError.setText("Add something for title");
            mTxtAudioNoteTitleError.setVisibility(View.VISIBLE);
            return false;
        }

     /*   if(description==null)
        if(description.isEmpty()) {
            mTxtAudioNoteTitleError.setText("Add something for title");
            return;
        }
*/
        if(!weNeedUpdate)
            realmAudioNoteHelper.addAudioNote(dbIdFile,idParentDb,title,description,Constants.CONST_NULL_ZERO);
        else
            realmAudioNoteHelper.updateAudioNote(dbIdFile,title,description);

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(weTypedSomethingNew){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);


            adb.setMessage("Do you save changes?");


            adb.setTitle("Note");


            adb.setIcon(android.R.drawable.ic_dialog_alert);


            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    if(saveNote())
                    {
                        Intent intentMessage=new Intent();

                        setResult(Constants.RESULT_CODE_REQUEST_DIALOG,intentMessage);
                        // finish The activity
                        finish();

                    }


                }
            });


            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            adb.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            adb.show();

        }

    }


}