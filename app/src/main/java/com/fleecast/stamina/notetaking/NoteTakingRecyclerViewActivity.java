package com.fleecast.stamina.notetaking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.NoteInfoStruct;
import com.fleecast.stamina.models.NotesAdapter;
import com.fleecast.stamina.models.RealmNoteHelper;

import java.util.ArrayList;

public class NoteTakingRecyclerViewActivity extends AppCompatActivity {

    private static final String TAG = "NoteTakingList";


    private RecyclerView recyclerView;
    private RealmNoteHelper realmNoteHelper;
    private ArrayList<NoteInfoStruct> noteInfoStructs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notetaking_activity_main);

        Log.e(TAG, "NoteTakingRecyclerViewActivity");
        noteInfoStructs = new ArrayList<>();
        realmNoteHelper = new RealmNoteHelper(NoteTakingRecyclerViewActivity.this);


        recyclerView = (RecyclerView) findViewById(R.id.rvNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_notetaking);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ActivityAddAudioNote.class));
                ///finish();
            }
        });


        setRecyclerView();
    }


    /**
     * set recyclerview with try get noteInfoStructs from realm
     */
    public void setRecyclerView() {
        try {
            noteInfoStructs = realmNoteHelper.findAllNotes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotesAdapter adapter = new NotesAdapter(noteInfoStructs, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onClick(NoteInfoStruct item) {
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                i.putExtra("id", item.getId());
                i.putExtra("title", item.getTitle());
                i.putExtra("description", item.getDescription());
                startActivity(i);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            noteInfoStructs = realmNoteHelper.findAllNotes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //noteInfoStructs = helper.findAllArticle();
        setRecyclerView();
    }
}
