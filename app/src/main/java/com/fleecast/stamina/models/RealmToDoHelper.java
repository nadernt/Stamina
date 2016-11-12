package com.fleecast.stamina.models;

import android.content.Context;

import com.fleecast.stamina.todo.TodoChildRealmStruct;
import com.fleecast.stamina.todo.TodoParentRealmStruct;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


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

    public void addTodo(int id,int idParent, String title, boolean hasDone,
                        Date update_time,int order) {

        if(!isExist(id)) {

            TodoChildRealmStruct todoRealmStruct = new TodoChildRealmStruct();
            todoRealmStruct.setId(id);
            todoRealmStruct.setParentId(idParent);
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
            updateChildTodo(id,title,order,hasDone);
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

        TodoParentRealmStruct todoRealmStruct = realm.where(TodoParentRealmStruct.class).equalTo("id", id).findFirst();
        todoRealmStruct.setTitle(title);
        todoRealmStruct.setCreateTimeStamp(update_time);
        realm.commitTransaction();

    }

    public void updateAllChildOfThisDone(int parent_id) {
        realm.beginTransaction();
        RealmResults<TodoChildRealmStruct> todoChildRealmStructs = realm.where(TodoChildRealmStruct.class).equalTo("parent_id", parent_id).findAll();
        for(int i=0 ; i<todoChildRealmStructs.size();i++)
            todoChildRealmStructs.get(i).setHasDone(true);
        realm.commitTransaction();

    }
    public void updateAllChildOfThisUnfinished(int parent_id) {
        realm.beginTransaction();
        RealmResults<TodoChildRealmStruct> todoChildRealmStructs = realm.where(TodoChildRealmStruct.class).equalTo("parent_id", parent_id).findAll();
        for(int i=0 ; i<todoChildRealmStructs.size();i++)
            todoChildRealmStructs.get(i).setHasDone(false);
        realm.commitTransaction();

    }
    public void updateChildTodo(int id, String title, int order, boolean hasDone) {
        realm.beginTransaction();
        
        TodoChildRealmStruct todoRealmStruct = realm.where(TodoChildRealmStruct.class).equalTo("id", id).findFirst();
        todoRealmStruct.setTitle(title);
        todoRealmStruct.setOrder(order);
        todoRealmStruct.setHasDone(hasDone);

        realm.commitTransaction();

    }

    public void updateChildTodoOrders(int id, int order) {
        realm.beginTransaction();

        TodoChildRealmStruct todoRealmStruct = realm.where(TodoChildRealmStruct.class).equalTo("id", id).findFirst();
        todoRealmStruct.setOrder(order);
        realm.commitTransaction();
    }

    public ArrayList<TodoParentRealmStruct> getAllParentTodos() {

        RealmResults<TodoParentRealmStruct> query = realm.where(TodoParentRealmStruct.class).findAll();
        ArrayList<TodoParentRealmStruct> data = new ArrayList<>();

        for (int i=0; i<query.size();i++){

            data.add(i,new TodoParentRealmStruct(query.get(i).getId(),query.get(i).getTitle(),query.get(i).getCreateTimeStamp(),query.get(i).getHasDone()));

        }
        return data;
    }

    public TodoParentRealmStruct getParentTodoById(int id) {

        return realm.where(TodoParentRealmStruct.class).equalTo("id", id).findFirst();

    }

    public ArrayList<TodoChildRealmStruct> getAllChildTodos(int parent_id) {

        RealmResults<TodoChildRealmStruct> query = realm.where(TodoChildRealmStruct.class).equalTo("parent_id", parent_id).findAll();
        query = query.sort("order", Sort.ASCENDING);

        ArrayList<TodoChildRealmStruct> data = new ArrayList<>();

        for (int i=0; i<query.size();i++){

            data.add(i,new TodoChildRealmStruct(query.get(i).getId(),query.get(i).getParentId(),query.get(i).getTitle(),query.get(i).getCreateTimeStamp(),query.get(i).getHasDone(),query.get(i).getOrder()));

        }
        return data;
    }

    public ArrayList<TodoChildRealmStruct> getAllChildTodosAreDone(int parent_id) {

        RealmResults<TodoChildRealmStruct> query = realm.where(TodoChildRealmStruct.class).equalTo("parent_id", parent_id).findAll();

        ArrayList<TodoChildRealmStruct> data = new ArrayList<>();

        for (int i=0; i<query.size();i++){

            if(!query.get(i).getHasDone())
                data.add(new TodoChildRealmStruct(query.get(i).getId(),query.get(i).getParentId(),query.get(i).getTitle(),query.get(i).getCreateTimeStamp(),query.get(i).getHasDone(),query.get(i).getOrder()));
        }
        return data;
    }
    /**
     * method delete articles by id
     *
     * @param id
     */
    public void deleteSingleChildNote(int id) {
        RealmResults<TodoChildRealmStruct> todoToDelete = realm.where(TodoChildRealmStruct.class).equalTo("id", id).findAll();
        realm.beginTransaction();
        todoToDelete.deleteFirstFromRealm();
        realm.commitTransaction();

    }
    public void deleteParentTodo(int parent_id) {

        RealmResults<TodoParentRealmStruct> todoToDelete = realm.where(TodoParentRealmStruct.class).equalTo("id", parent_id).findAll();
        realm.beginTransaction();
        todoToDelete.deleteFirstFromRealm();
        realm.commitTransaction();

        RealmResults<TodoChildRealmStruct> todoChildToDelete = realm.where(TodoChildRealmStruct.class).equalTo("parent_id", parent_id).findAll();
        realm.beginTransaction();
        todoChildToDelete.deleteAllFromRealm();
        realm.commitTransaction();

    }
}