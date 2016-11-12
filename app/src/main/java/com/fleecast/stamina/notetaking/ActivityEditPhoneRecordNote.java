package com.fleecast.stamina.notetaking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.NoteInfoRealmStruct;
import com.fleecast.stamina.models.NoteInfoStruct;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.utility.Constants;

import java.util.ArrayList;
import java.util.Date;

public class ActivityEditPhoneRecordNote extends AppCompatActivity {


    private int noteId;
    private EditText inputTitle, inputDescription;
    private String noteTitle, description;
    private RealmNoteHelper realmNoteHelper;
    private String intentTitle, intentDescription;
    private Toolbar mtoolbar;
    private NoteInfoRealmStruct noteInfoRealmStruct;
    private UndoRedoHelper undoRedoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_record_add_edit);

        mtoolbar = (Toolbar) findViewById(R.id.tool_bar_phone_record_note); // Attaching the layout to the toolbar object

        setSupportActionBar(mtoolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        realmNoteHelper = new RealmNoteHelper(ActivityEditPhoneRecordNote.this);

        noteId = getIntent().getIntExtra(Constants.EXTRA_EDIT_PHONE_RECORD_NOTE, 0);
        //noteId = getIntent().getStringExtra(Constants.EXTRA_EDIT_PHONE_RECORD_NOTE_PHONE_NUMBER);



        if(noteId>0){

            noteInfoRealmStruct = realmNoteHelper.getNoteById(noteId);
            intentTitle = noteInfoRealmStruct.getTitle();
            intentDescription = noteInfoRealmStruct.getDescription();

        }



       /* delete = (Button) findViewById(R.id.delete);
        save = (Button) findViewById(R.id.save);*/


        inputTitle = (EditText) findViewById(R.id.inputPhoneTextNoteTitle);
        inputDescription = (EditText) findViewById(R.id.inputPhoneTextNoteDescription);


        inputTitle.setText(intentTitle);
        inputDescription.setText(intentDescription);

        undoRedoHelper = new UndoRedoHelper(inputDescription);


    }
/*
private void deleteNote(){

    realmNoteHelper.deleteSingleNote(noteId);

    startActivity(new Intent(ActivityEditPhoneRecordNote.this, ActivityTakenNotesList.class));
    finish();
}
*/

    private void saveEditNote(){
        noteTitle = inputTitle.getText().toString();
        description = inputDescription.getText().toString();

        Date now = new Date();

        realmNoteHelper.updateNotes(noteId, noteTitle, description,now, 0,Constants.CONST_NOTETYPE_PHONECALL);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phone_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_redo_phone_edit) {
            undoRedoHelper.redo();
            return true;
        }
        if (id == R.id.action_undo_phone_edit) {
            undoRedoHelper.undo();
            return true;
        }
        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_save_phone_edit) {
            saveEditNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 