package com.fleecast.stamina.todo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.NoteInfoStruct;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

import java.util.ArrayList;

public class TodoChildAdapter extends RecyclerView.Adapter<TodoChildAdapter.ViewHolder> {


    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;
    private final Context context;
    private ArrayList<TodoChildRealmStruct> todoChildRealmStructs;

    public TodoChildAdapter(Context context, ArrayList<TodoChildRealmStruct> todoChildRealmStructs, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.todoChildRealmStructs = new ArrayList(todoChildRealmStructs);
        this.clickListener = listener;
        this.longClickListener = longClickListener;
        this.context = context;
    }


    @Override
    public TodoChildAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_child, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(TodoChildAdapter.ViewHolder holder, int position) {
        holder.click(todoChildRealmStructs.get(position), clickListener);
        holder.longClick(todoChildRealmStructs.get(position), longClickListener);
        holder.tvId.setText(String.valueOf(todoChildRealmStructs.get(position).getId()));
        holder.title.setText(Utility.ellipsize(todoChildRealmStructs.get(position).getTitle(),50));
        holder.create_time.setText(Utility.unixTimeToReadable(todoChildRealmStructs.get(position).getCreateTimeStamp().getTime() / 1000L));

    }


    @Override
    public int getItemCount() {
        return todoChildRealmStructs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, title, create_time;

        public ViewHolder(View itemView) {
            super(itemView);
            tvId = (TextView) itemView.findViewById(R.id.tvIdTodoChild);
            title = (TextView) itemView.findViewById(R.id.tvTitleTodoChild);
            create_time = (TextView) itemView.findViewById(R.id.tvCreateTimeTodoChild);
        }


        public void click(final TodoChildRealmStruct todoChildRealmStruct, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(todoChildRealmStruct);
                }
            });
        }

        public void longClick(final TodoChildRealmStruct todoChildRealmStruct, final OnItemLongClickListener listener) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(todoChildRealmStruct);
                    return false;
                }
            });
        }

    }

    public TodoChildRealmStruct getItemAtPosition(int i) {
        return todoChildRealmStructs.get(i);
    }

    public void removeItem(int i) {
        todoChildRealmStructs.remove(i);
        this.notifyItemRemoved(i);
    }

    public interface OnItemClickListener {
        void onClick(TodoChildRealmStruct todoChildRealmStruct);
    }
    public interface OnItemLongClickListener {
        void onLongClick(TodoChildRealmStruct todoChildRealmStruct);
    }
}