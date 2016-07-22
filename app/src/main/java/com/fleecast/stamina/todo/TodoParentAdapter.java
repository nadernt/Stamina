package com.fleecast.stamina.todo;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Utility;

import java.util.ArrayList;

public class TodoParentAdapter extends RecyclerView.Adapter<TodoParentAdapter.ViewHolder> {

    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;
    private final Context context;

    private ArrayList<TodoParentRealmStruct> todoParentRealmStructs;


    public TodoParentAdapter(Context context, ArrayList<TodoParentRealmStruct> todoParentRealmStructs, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.todoParentRealmStructs = new ArrayList(todoParentRealmStructs);
        Log.e("FFFFFFFFf", todoParentRealmStructs.size() + "");
        this.clickListener = listener;
        this.longClickListener = longClickListener;
        this.context = context;
    }


    @Override
    public TodoParentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_parent, null,true);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(TodoParentAdapter.ViewHolder holder, int position) {

        holder.click(todoParentRealmStructs.get(position), clickListener);
        holder.longClick(todoParentRealmStructs.get(position), longClickListener);
        holder.tvId.setText(String.valueOf(todoParentRealmStructs.get(position).getId()));

        if(todoParentRealmStructs.get(position).getHasDone())
            holder.title.setPaintFlags(  holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.title.setText(Utility.ellipsize(todoParentRealmStructs.get(position).getTitle(),50));

        Log.e("FFFFFFFFf", todoParentRealmStructs.size() + " " + todoParentRealmStructs.get(position).getTitle());

        holder.create_time.setText(Utility.unixTimeToReadable(todoParentRealmStructs.get(position).getCreateTimeStamp().getTime() / 1000L));

    }


    @Override
    public int getItemCount() {
        return todoParentRealmStructs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, title, create_time;

        public ViewHolder(View itemView) {
            super(itemView);
            tvId = (TextView) itemView.findViewById(R.id.tvIdTodoParent);
            title = (TextView) itemView.findViewById(R.id.tvTitleTodoParent);
            create_time = (TextView) itemView.findViewById(R.id.tvCreateTimeTodoParent);
        }

        public void click(final TodoParentRealmStruct todoParentRealmStruct, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(todoParentRealmStruct);
                }
            });
        }

        public void longClick(final TodoParentRealmStruct todoParentRealmStruct, final OnItemLongClickListener listener) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(todoParentRealmStruct);
                    return false;
                }
            });
        }

    }

    public TodoParentRealmStruct getItemAtPosition(int i) {
        return todoParentRealmStructs.get(i);
    }

    public void removeItem(int i) {
        todoParentRealmStructs.remove(i);
        this.notifyItemRemoved(i);
    }

    public interface OnItemClickListener {
        void onClick(TodoParentRealmStruct todoParentRealmStruct);
    }
    public interface OnItemLongClickListener {
        void onLongClick(TodoParentRealmStruct todoParentRealmStruct);
    }


} 
