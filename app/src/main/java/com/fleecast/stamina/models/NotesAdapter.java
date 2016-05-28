package com.fleecast.stamina.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import com.fleecast.stamina.R;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {


    private final OnItemClickListener listener;
    private ArrayList<NoteInfoStruct> noteInfoStructs;


    public NotesAdapter(ArrayList<NoteInfoStruct> noteInfoStructs, OnItemClickListener listener) {
        this.noteInfoStructs = noteInfoStructs;
        this.listener = listener;
    }


    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(NotesAdapter.ViewHolder holder, int position) {
        holder.click(noteInfoStructs.get(position), listener);
        holder.tvId.setText(String.valueOf(noteInfoStructs.get(position).getId()));
        holder.title.setText(noteInfoStructs.get(position).getTitle());
        holder.description.setText(noteInfoStructs.get(position).getDescription());
        holder.update_time.setText(noteInfoStructs.get(position).getUpdate_time().toString());
    }


    @Override
    public int getItemCount() {
        return noteInfoStructs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, title, description,update_time;


        public ViewHolder(View itemView) {
            super(itemView);
            tvId = (TextView) itemView.findViewById(R.id.tvId);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            update_time = (TextView) itemView.findViewById(R.id.tvUpdateTime);
        }


        public void click(final NoteInfoStruct noteInfoStruct, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(noteInfoStruct);
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onClick(NoteInfoStruct noteInfoStruct);
    }


} 
