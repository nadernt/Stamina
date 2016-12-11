package com.fleecast.stamina.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.RealmToDoHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ActivityTodoChildRecyclerView extends AppCompatActivity {

    private MyApplication myApplication;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean someThingUpdated=false;

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        if(someThingUpdated)
            setResult(Activity.RESULT_OK,returnIntent);
        else
            setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }

    private TodoChildAdapter adapter;
    private Context mContext;
    private ArrayList<TodoChildRealmStruct> todoChildRealmStructs;
    private RealmToDoHelper realmToDoHelper;
    private int dbIdParent;
    private RecyclerView recyclerView;
    private TextView txtTitleOfChildViewActivity;
    private boolean dragStarted = false;

    private TextView tvMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_child_recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Todo List");
        this.mContext = ActivityTodoChildRecyclerView.this;

        todoChildRealmStructs = new ArrayList<>();

        realmToDoHelper = new RealmToDoHelper(mContext);


        recyclerView = (RecyclerView) findViewById(R.id.rvToDoChild);
        txtTitleOfChildViewActivity = (TextView) findViewById(R.id.txtTitleOfChildViewActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabTodoChild);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditTodo(null);
            }
        });

        if (getIntent().hasExtra(Constants.EXTRA_TODO_PARENT_DB_ID)) {

            dbIdParent = getIntent().getIntExtra(Constants.EXTRA_TODO_PARENT_DB_ID, Constants.CONST_NULL_ZERO);

            final TodoParentRealmStruct tmpStruct = realmToDoHelper.getParentTodoById(dbIdParent);

            txtTitleOfChildViewActivity.setText(Utility.ellipsize(tmpStruct.getTitle(), 50));

            // Extend the Callback class
            ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.END, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                @Override
                public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                    dragStarted = true;

                }

                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    // get the viewHolder's and target's positions in your adapter data, swap them
                    Collections.swap(todoChildRealmStructs, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    // and notify the adapter that its dataset has changed
                    adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                    if (swipeDir == ItemTouchHelper.LEFT) {
                        TodoChildRealmStruct todoChildRealmStruct = adapter.getItemAtPosition(viewHolder.getAdapterPosition());
                        if (!todoChildRealmStruct.getHasDone()) {
                            realmToDoHelper.updateChildTodo(todoChildRealmStruct.getId(), todoChildRealmStruct.getTitle(), todoChildRealmStruct.getOrder(), true);
                            todoChildRealmStruct.setHasDone(true);
                            Snackbar.make(recyclerView, Utility.fromHTMLVersionCompat("<font color='red'>" + Utility.ellipsize(todoChildRealmStruct.getTitle(),30)  + "</font>" + " task done!",Html.FROM_HTML_MODE_LEGACY), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            realmToDoHelper.updateChildTodo(todoChildRealmStruct.getId(), todoChildRealmStruct.getTitle(), todoChildRealmStruct.getOrder(), false);
                            todoChildRealmStruct.setHasDone(false);
                        }
                        someThingUpdated=true;
                        updateRecyclerView();
                    } else {
                        //Remove swiped item from list and notify the RecyclerView
                        deleteItem(adapter.getItemAtPosition(viewHolder.getAdapterPosition()), true);
                    }
                }

            };

            // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
            ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
            ith.attachToRecyclerView(recyclerView);

            setRecyclerView();

            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (MotionEvent.ACTION_UP == motionEvent.getAction() && dragStarted) {
                        dragStarted = false;
                        for (int i = 0; i < adapter.getItemCount(); i++) {
                            realmToDoHelper.updateChildTodoOrders(todoChildRealmStructs.get(i).getId(), i);

                        }

                        updateRecyclerView();
                    }

                    return false;
                }
            });

        }

    }

private void updateRecyclerView(){

    Handler handler1 = new Handler();
    handler1.postDelayed(new Runnable() {

        @Override
        public void run() {
            todoChildRealmStructs.clear();
            todoChildRealmStructs.addAll(realmToDoHelper.getAllChildTodos(dbIdParent));
            adapter.swapItems(todoChildRealmStructs);

        }
    }, 20);


}

    private void addEditTodo(final TodoChildRealmStruct todoChildRealmStructToEdit) {

        AlertDialog.Builder addNoteDialogBuilder;


        addNoteDialogBuilder = new AlertDialog.Builder(mContext);
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


        addNoteDialogBuilder.setView(layout);

        addNoteDialogBuilder.setTitle("Add Todo");
        addNoteDialogBuilder.setIcon(R.drawable.list);
        addNoteDialogBuilder.setCancelable(true);

        if (todoChildRealmStructToEdit != null) {
            et.setText(todoChildRealmStructToEdit.getTitle());
            et.setSelection(et.getText().length());
        }

        if (todoChildRealmStructToEdit == null) {
            addNoteDialogBuilder.setPositiveButton("ADD",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing here because we override this button later to change the close behaviour.
                            //However, we still need this because on older versions of Android unless we
                            //pass a handler the button doesn't get instantiated
                        }
                    });

            addNoteDialogBuilder.setNegativeButton("NEXT",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing here because we override this button later to change the close behaviour.
                            //However, we still need this because on older versions of Android unless we
                            //pass a handler the button doesn't get instantiated
                        }
                    });
            addNoteDialogBuilder.setNeutralButton("CLOSE",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing here because we override this button later to change the close behaviour.
                            //However, we still need this because on older versions of Android unless we
                            //pass a handler the button doesn't get instantiated
                        }
                    });

        } else {
            addNoteDialogBuilder.setPositiveButton("SAVE",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing here because we override this button later to change the close behaviour.
                            //However, we still need this because on older versions of Android unless we
                            //pass a handler the button doesn't get instantiated
                        }
                    });
            addNoteDialogBuilder.setNeutralButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing here because we override this button later to change the close behaviour.
                            //However, we still need this because on older versions of Android unless we
                            //pass a handler the button doesn't get instantiated
                        }
                    });

        }


        final AlertDialog dialog = addNoteDialogBuilder.create();

        dialog.show();

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredTodo = et.getText().toString().trim();

                if (enteredTodo == null) {
                    tvMessage.setText("You should add some text as todo!");
                    return;
                }

                if (enteredTodo.isEmpty()) {
                    tvMessage.setText("You should add some text as todo!");
                    return;
                }


                if (todoChildRealmStructToEdit == null) {
                    int dbIdChild = (int) (System.currentTimeMillis());

                    Date now = new Date();

                    realmToDoHelper.addTodo(dbIdChild, dbIdParent, enteredTodo, false, now, dbIdChild);
                    setRecyclerView();

                } else {
                    realmToDoHelper.updateChildTodo(todoChildRealmStructToEdit.getId(),
                            enteredTodo,
                            todoChildRealmStructToEdit.getOrder(),
                            todoChildRealmStructToEdit.getHasDone());
                    updateRecyclerView();
                }

                someThingUpdated=true;

                dialog.dismiss();


                }
        });

        if (todoChildRealmStructToEdit == null) {

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;

                    String enteredTodo = et.getText().toString().trim();
                    tvMessage.setTextColor(ContextCompat.getColor(mContext, R.color.american_rose));

                    if (enteredTodo == null) {
                        tvMessage.setText("You should add some text as todo!");
                        return;
                    }

                    if (enteredTodo.isEmpty()) {
                        tvMessage.setText("You should add some text as todo!");
                        return;
                    }

                    int dbIdChild = (int) (System.currentTimeMillis());

                    Date now = new Date();

                    realmToDoHelper.addTodo(dbIdChild, dbIdParent, enteredTodo, false, now, dbIdChild);
                    tvMessage.setTextColor(ContextCompat.getColor(mContext, R.color.ball_blue));
                    tvMessage.setText(Utility.ellipsize(enteredTodo, 50) + " added.");
                    et.setText("");
                    someThingUpdated=true;
                    setRecyclerView();
                }
            });

        }

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

    }

    private void deleteItem(final TodoChildRealmStruct item, final boolean isItFromSwipeFunction) {


        android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(mContext);

        adb.setMessage(Utility.fromHTMLVersionCompat("Are you sure want to delete " + "<strong>" + Utility.ellipsize(item.getTitle(), 50) + "</strong>" + "?",Html.FROM_HTML_MODE_LEGACY));

        adb.setTitle("Delete Todo");

        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                someThingUpdated=true;
                realmToDoHelper.deleteSingleChildNote(item.getId());
                //setRecyclerView();
                for (int j = 0; i < todoChildRealmStructs.size(); j++) {
                    if (todoChildRealmStructs.get(j).getId() == item.getId()) {
                        todoChildRealmStructs.remove(j);
                        adapter.removeItem(j);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }


            }
        });


        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (isItFromSwipeFunction)
                    adapter.notifyDataSetChanged();
                return;
            }
        });
        adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (isItFromSwipeFunction)
                    adapter.notifyDataSetChanged();
            }
        });
        adb.show();

    }


    public void setRecyclerView() {

        todoChildRealmStructs = realmToDoHelper.getAllChildTodos(dbIdParent);
        adapter = new TodoChildAdapter(mContext, todoChildRealmStructs, new TodoChildAdapter.OnItemClickListener() {

            @Override
            public void onClick(TodoChildRealmStruct todoChildRealmStruct) {


            }

        }, new TodoChildAdapter.OnItemLongClickListener() {

            @Override
            public void onLongClick(TodoChildRealmStruct todoChildRealmStruct) {
                AlertDialog myDialog;
                final TodoChildRealmStruct tmpStruct = todoChildRealmStruct;
                String[] items = {"Edit", "Add as event to calendar"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Options");
                builder.setIcon(R.drawable.list);

                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {

                            addEditTodo(tmpStruct);

                        } else if (which == 1) {

                            Intent intent = new Intent(mContext, ActivityAddToEvent.class);
                            intent.putExtra(Constants.EXTRA_ADDED_EVENT_TITLE,tmpStruct.getTitle());
                            startActivity(intent);

                        } else if (which == 2) {

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
