package com.fleecast.stamina.models;

import android.content.Context;
import android.util.Log;

import com.fleecast.stamina.todo.TodoChildRealmStruct;
import com.fleecast.stamina.todo.TodoParentRealmStruct;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class RealmToDoHelper {

    private static final String TAG = "RealmNoteHelper";
    private Realm realm;
    public Context context;
    private boolean DEBUG = false;

    /**
     * constructor to create instances of realm
     *
     * @param context
     */
    public RealmToDoHelper(Context context) {
        realm = Realm.getDefaultInstance();
        this.context = context;
        DEBUG = false;

    }

    public void addTodoParent(int id, String title, boolean hasDone,
                              Date update_time) {

        if(!isParentExist(id)) {

            TodoParentRealmStruct todoParentRealmStruct = new TodoParentRealmStruct();
            todoParentRealmStruct.setId(id);
            todoParentRealmStruct.setTitle(title);
            todoParentRealmStruct.setHasDone(hasDone);
            Date now = new Date();
            todoParentRealmStruct.setCreateTimeStamp(now);
            realm.beginTransaction();
            realm.copyToRealm(todoParentRealmStruct);
            realm.commitTransaction();

        }
        else
        {
            updateParentTodo(id,title,update_time);
        }

    }

    public void addTodo(int id, String title, boolean hasDone,
                        Date update_time,int order) {

        if(!isExist(id)) {

            TodoChildRealmStruct todoRealmStruct = new TodoChildRealmStruct();
            todoRealmStruct.setId(id);
            todoRealmStruct.setTitle(title);
            todoRealmStruct.setHasDone(hasDone);
            Date now = new Date();
            todoRealmStruct.setCreateTimeStamp(now);
            realm.beginTransaction();
            realm.copyToRealm(todoRealmStruct);
            realm.commitTransaction();
        }
        else
        {
            updateTodo(id,title,update_time,order);
        }

    }

    /**
     * Check if note by id exists.
     *
     * @param id
     * @return
     */
    public boolean isParentExist(int id) {

        RealmQuery<TodoParentRealmStruct> query = realm.where(TodoParentRealmStruct.class)
                .equalTo("id", id);

        return query.count() == 0 ? false : true;
    }

    /**
     * Check if note by id exists.
     *
     * @param id
     * @return
     */
    public boolean isExist(int id) {

        RealmQuery<TodoChildRealmStruct> query = realm.where(TodoChildRealmStruct.class)
                .equalTo("id", id);

        return query.count() == 0 ? false : true;
    }

    /**
     * Read note by id
     *
     * @param id
     * @return
     */
    public TodoChildRealmStruct getTodoById(int id) {
        return realm.where(TodoChildRealmStruct.class).equalTo("id", id).findFirst();
    }

    public void updateParentTodo(int id, String title, Date update_time) {
        realm.beginTransaction();

        TodoChildRealmStruct todoRealmStruct = realm.where(TodoChildRealmStruct.class).equalTo("id", id).findFirst();
        todoRealmStruct.setTitle(title);
        todoRealmStruct.setCreateTimeStamp(update_time);
        realm.commitTransaction();

    }

    public void updateTodo(int id, String title, Date update_time, int order) {
        realm.beginTransaction();

        TodoChildRealmStruct todoRealmStruct = realm.where(TodoChildRealmStruct.class).equalTo("id", id).findFirst();
        todoRealmStruct.setTitle(title);
        todoRealmStruct.setCreateTimeStamp(update_time);
        todoRealmStruct.setOrder(order);

        realm.commitTransaction();

    }

    public ArrayList<TodoParentRealmStruct> getAllParentTodos() {

        RealmResults<TodoParentRealmStruct> query = realm.where(TodoParentRealmStruct.class).findAll();
        ArrayList<TodoParentRealmStruct> data = new ArrayList<>();

        for (int i=0; i<query.size();i++){

            data.add(i,new TodoParentRealmStruct(query.get(i).getId(),query.get(i).getTitle(),query.get(i).getCreateTimeStamp(),query.get(i).getHasDone()));
            Log.e("GGG", query.get(i).getTitle());

        }
        return data;
    }

    /**
     * method delete articles by id
     *
     * @param id
     */
    public void deleteSingleNote(int id) {
        RealmResults<TodoChildRealmStruct> todoToDelete = realm.where(TodoChildRealmStruct.class).equalTo("id", id).findAll();
        realm.beginTransaction();
        todoToDelete.deleteFirstFromRealm();
        realm.commitTransaction();

    }

}