package com.fleecast.stamina.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.RealmToDoHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ActivityTodoParentRecyclerView extends AppCompatActivity {
    private MyApplication myApplication;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TodoParentAdapter adapter;
    private Context context;
    private ArrayList<TodoParentRealmStruct> todoParentRealmStructs;
    private RealmToDoHelper realmToDoHelper;
    private int dbId;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_parent_recyclerview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.context = ActivityTodoParentRecyclerView.this;

        todoParentRealmStructs = new ArrayList<>();
        realmToDoHelper = new RealmToDoHelper(context);

        recyclerView = (RecyclerView) findViewById(R.id.rvToDoParent);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabTodoParent);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                addNewTodoParent();

            }
        });

        setRecyclerView();

    }

    private void addNewTodoParent(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);



        final EditText et = new EditText(context);
        String etStr = et.getText().toString();

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setTitle("Todo Title");
        alertDialogBuilder.setIcon(R.drawable.ic_action_list);
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dbId = (int) (System.currentTimeMillis() / 1000);
                Date now = new Date();
                realmToDoHelper.addTodoParent(dbId,et.getText().toString().trim(),false,null);
                setRecyclerView();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would
            // not display the 'Force Close' message
            e.printStackTrace();
        }
    }

    public void setRecyclerView() {
        todoParentRealmStructs = realmToDoHelper.getAllParentTodos();
        adapter = new TodoParentAdapter(context, todoParentRealmStructs, new TodoParentAdapter.OnItemClickListener() {

            @Override
            public void onClick(TodoParentRealmStruct todoParentRealmStruct) {

            }


        }, new TodoParentAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(TodoParentRealmStruct todoParentRealmStruct) {

            }
        });

        recyclerView.setAdapter(adapter);

    }
}