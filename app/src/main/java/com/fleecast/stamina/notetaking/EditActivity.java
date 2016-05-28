package com.fleecast.stamina.notetaking;

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

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {


    private int noteId;
   // private Button delete, save;
    private EditText inputTitle, inputDescription;
    private String noteTitle, description;
    private RealmNoteHelper realmNoteHelper;
    private String intentTitle, intentDescription;
    private ArrayList<NoteInfoStruct> noteInfoStructs;
    private Toolbar mtoolbar;
    private NoteInfoRealmStruct noteInfoRealmStruct;
    // private MediaRouteButton btnDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit_activity);

        mtoolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object

        setSupportActionBar(mtoolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        realmNoteHelper = new RealmNoteHelper(EditActivity.this);
        noteInfoStructs = new ArrayList<>();
        noteId = getIntent().getIntExtra("id", 0);

        if(noteId>0){

            noteInfoRealmStruct = realmNoteHelper.getNoteById(noteId);
            intentTitle = noteInfoRealmStruct.getTitle();
            intentDescription = noteInfoRealmStruct.getDescription();

        }



       /* delete = (Button) findViewById(R.id.delete);
        save = (Button) findViewById(R.id.save);*/


        inputTitle = (EditText) findViewById(R.id.inputTitle);
        inputDescription = (EditText) findViewById(R.id.inputDescription);


        inputTitle.setText(intentTitle);
        inputDescription.setText(intentDescription);


        //btnDelete.setVisibility(View.VISIBLE);
        //mtoolbar.getMenu().getItem(R.id.action_save).setVisible(false);
/*
        // Command to delete
       delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Command to update the noteInfoStructs
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieving text from EditText
                noteTitle = inputTitle.getText().toString();
                description = inputDescription.getText().toString();

                // Update articles
                realmNoteHelper.updateNote(noteId, noteTitle, description,true);

                //Go to MainActivity
                startActivity(new Intent(EditActivity.this, NoteTakingRecyclerViewActivity.class));
                finish();
            }
        });*/


    }
private void deleteNote(){

    // Delete noteInfoStructs from database
    realmNoteHelper.deleteSingleNote(noteId);

    // Go to MainActivity
    startActivity(new Intent(EditActivity.this, NoteTakingRecyclerViewActivity.class));
    finish();
}

    private void saveEditNote(){
        // Retrieving text from EditText
        noteTitle = inputTitle.getText().toString();
        description = inputDescription.getText().toString();

        // Update articles
        realmNoteHelper.updateNote(noteId, noteTitle, description, true);

        //Go to MainActivity
        startActivity(new Intent(EditActivity.this, NoteTakingRecyclerViewActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_save) {
            saveEditNote();
            return true;
        }
        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_delete) {
            deleteNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 