package com.fleecast.stamina.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.NoteInfoStruct;
import com.fleecast.stamina.models.RealmToDoHelper;
import com.fleecast.stamina.notetaking.ActivityViewTextNote;
import com.fleecast.stamina.todo.logger.Log;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

import java.util.ArrayList;
import java.util.Date;

public class ActivityTodoParentRecyclerView extends AppCompatActivity {
    private MyApplication myApplication;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TodoParentAdapter adapter;
    private Context mContext;
    private ArrayList<TodoParentRealmStruct> todoParentRealmStructs;
    private RealmToDoHelper realmToDoHelper;
    private int dbId;
    private RecyclerView recyclerView;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_parent_recyclerview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("All Task Lists");
        this.mContext = ActivityTodoParentRecyclerView.this;

        todoParentRealmStructs = new ArrayList<>();

        realmToDoHelper = new RealmToDoHelper(mContext);

        recyclerView = (RecyclerView) findViewById(R.id.rvToDoParent);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabTodoParent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addNewTodoParent();
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT )  {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                if(swipeDir == ItemTouchHelper.RIGHT) {

                    deleteItem(adapter.getItemAtPosition(viewHolder.getAdapterPosition()));
                }

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(recyclerView);
        setRecyclerView();

    }

    private void deleteItem(final TodoParentRealmStruct item) {


        android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(mContext);

        adb.setMessage(Html.fromHtml("Are you sure want to delete " + "<strong>" + Utility.ellipsize(item.getTitle(), 50) + "</strong>" + "?"));

        adb.setTitle("Delete Todo List");

        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int idParent = item.getId();

                realmToDoHelper.deleteParentTodo(idParent);

                for (int j = 0; i < todoParentRealmStructs.size(); j++) {
                    if (todoParentRealmStructs.get(j).getId() == item.getId()) {
                        todoParentRealmStructs.remove(j);
                        adapter.removeItem(j);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }


            }
        });


        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyDataSetChanged();
            }
        });
        adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                adapter.notifyDataSetChanged();
            }
        });
        adb.show();

    }


    private void addNewTodoParent(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        LinearLayout layout = new LinearLayout(mContext);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);



        final EditText et = new EditText(mContext);

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setTitle("Todo Title");
        alertDialogBuilder.setIcon(R.drawable.list);
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!et.getText().toString().trim().isEmpty()){
                    dbId = (int) (System.currentTimeMillis());
                    realmToDoHelper.addTodoParent(dbId, et.getText().toString().trim(), false, null);
                    setRecyclerView();
                }else{
                    Utility.showMessage("You should add a title for Todo!","Note", mContext);
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RESULT_CODE_REQUEST_LIST){
                if(resultCode == Activity.RESULT_OK){
                    setRecyclerView();
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
        }
    }

    public void setRecyclerView() {
        todoParentRealmStructs = realmToDoHelper.getAllParentTodos();
        adapter = new TodoParentAdapter(mContext, todoParentRealmStructs, new TodoParentAdapter.OnItemClickListener() {

            @Override
            public void onClick(TodoParentRealmStruct todoParentRealmStruct) {
                Intent intent = new Intent(mContext, ActivityTodoChildRecyclerView.class);
                intent.putExtra(Constants.EXTRA_TODO_PARENT_DB_ID, todoParentRealmStruct.getId());
                startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_LIST);
            }


        }, new TodoParentAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(final TodoParentRealmStruct todoParentRealmStruct) {
                AlertDialog myDialog;

                String[] items = {"Edit title","Make all as finished","Make all unfinished"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Options");
                builder.setIcon(R.drawable.list);

                builder.setItems(items, new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which==0){

                            AlertDialog.Builder editTodoParentTitleDialogBuilder;


                            editTodoParentTitleDialogBuilder = new AlertDialog.Builder(mContext);
                            LinearLayout layout = new LinearLayout(mContext);
                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setLayoutParams(parms);

                            layout.setGravity(Gravity.CLIP_VERTICAL);
                            layout.setPadding(5, 5, 5, 5);

                            tvMessage = new TextView(mContext);

                            tvMessage.setTextColor(ContextCompat.getColor(mContext, R.color.american_rose));

                            layout.addView(tvMessage, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            final EditText et = new EditText(mContext);


                            layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


                            editTodoParentTitleDialogBuilder.setView(layout);

                            editTodoParentTitleDialogBuilder.setTitle("Add Todo");
                            editTodoParentTitleDialogBuilder.setIcon(R.drawable.list);
                            editTodoParentTitleDialogBuilder.setCancelable(true);

                            if (todoParentRealmStruct != null) {
                                et.setText(todoParentRealmStruct.getTitle());
                                et.setSelection(et.getText().length());
                            }

                                editTodoParentTitleDialogBuilder.setPositiveButton("SAVE",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Do nothing here because we override this button later to change the close behaviour.
                                                //However, we still need this because on older versions of Android unless we
                                                //pass a handler the button doesn't get instantiated
                                            }
                                        });


                                editTodoParentTitleDialogBuilder.setNegativeButton("CANCEL",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Do nothing here because we override this button later to change the close behaviour.
                                                //However, we still need this because on older versions of Android unless we
                                                //pass a handler the button doesn't get instantiated
                                            }
                                        });


                            final AlertDialog dialog1 = editTodoParentTitleDialogBuilder.create();

                            dialog1.show();

                            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean focused) {
                                    if (focused) {
                                        dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                    }
                                }
                            });

//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                            dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String enteredTodo = et.getText().toString().trim();

                                    if (enteredTodo == null) {
                                        tvMessage.setText("You should add some text!");
                                        return;
                                    }

                                    if (enteredTodo.isEmpty()) {
                                        tvMessage.setText("You should add some text!");
                                        return;
                                    }
                                    Date now = new Date();
                                        realmToDoHelper.updateParentTodo(todoParentRealmStruct.getId(),
                                                enteredTodo,
                                                now);
                                        setRecyclerView();
                                    dialog1.dismiss();

                                }

                            });



                            dialog1.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    dialog1.dismiss();

                                }
                            });

                        }
                        else if(which==1){
                            realmToDoHelper.updateAllChildOfThisDone(todoParentRealmStruct.getId());
                            setRecyclerView();
                        }
                        else if(which==2){
                            realmToDoHelper.updateAllChildOfThisUnfinished(todoParentRealmStruct.getId());
                            setRecyclerView();
                        }
                    }
                });


                builder.setCancelable(true);
                myDialog = builder.create();
                myDialog.show();
            }
        });

        recyclerView.setAdapter(adapter);

    }
}