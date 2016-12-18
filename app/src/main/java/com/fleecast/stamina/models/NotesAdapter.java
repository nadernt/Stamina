package com.fleecast.stamina.models;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {


    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;
    private final Context context;

    private ArrayList<NoteInfoStruct> noteInfoStructs;


    public NotesAdapter(Context context, ArrayList<NoteInfoStruct> noteInfoStructs, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.noteInfoStructs = new ArrayList(noteInfoStructs);
        this.clickListener = listener;
        this.longClickListener = longClickListener;
        this.context = context;
    }


    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(NotesAdapter.ViewHolder holder, int position) {
        holder.click(noteInfoStructs.get(position), clickListener);
        holder.longClick(noteInfoStructs.get(position), longClickListener);
        holder.tvId.setText(String.valueOf(noteInfoStructs.get(position).getId()));
        holder.title.setText(Utility.ellipsize(noteInfoStructs.get(position).getTitle(),50));
        holder.description.setText(Utility.ellipsize(noteInfoStructs.get(position).getDescription(),150));
        holder.create_time.setText(Utility.unixTimeToReadable(noteInfoStructs.get(position).getCreateTimeStamp().getTime() / 1000L));

       // Log.e("DBG",noteInfoStructs.get(position).getHasAudio() + "");
        if(noteInfoStructs.get(position).getHasAudio()) {

            String contactNmae = Utility.ellipsize(Utility.getContactName(context,noteInfoStructs.get(position).getPhoneNumber()),20);

            if(noteInfoStructs.get(position).getCallType() == Constants.RECORDS_IS_OUTGOING) {
                holder.audioType.setBackgroundResource(R.drawable.outcoming_call);
                holder.phone_number.setVisibility(View.VISIBLE);
                holder.phone_number.setText(contactNmae);
            }
            else if(noteInfoStructs.get(position).getCallType() == Constants.RECORDS_IS_INCOMING) {
                holder.audioType.setBackgroundResource(R.drawable.incoming_calls);
                holder.phone_number.setVisibility(View.VISIBLE);
                holder.phone_number.setText(contactNmae);
            }
            else {
                holder.audioType.setBackgroundResource(R.drawable.audio_wave);
                holder.phone_number.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            holder.audioType.setBackgroundResource(R.drawable.text);
            holder.phone_number.setVisibility(View.INVISIBLE);
        }

        if(noteInfoStructs.get(position).getColor()==Constants.CONST_NULL_ZERO)
            holder.img_color_of_note.setBackgroundColor(Color.WHITE);
        else
            holder.img_color_of_note.setBackgroundColor(noteInfoStructs.get(position).getColor());

        if(noteInfoStructs.get(position).getGroup() != null) {
            holder.tvGroup.setText(Utility.ellipsize(noteInfoStructs.get(position).getGroup(),25));
            holder.tvGroup.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.tvGroup.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return noteInfoStructs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, title, description, create_time,phone_number,tvGroup;
        ImageView audioType,img_color_of_note;

        public ViewHolder(View itemView) {
            super(itemView);
            tvId = (TextView) itemView.findViewById(R.id.tvId);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            tvGroup = (TextView) itemView.findViewById(R.id.tvGroup);
            create_time = (TextView) itemView.findViewById(R.id.tvCreateTime);
            phone_number= (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            audioType = (ImageView) itemView.findViewById(R.id.audioType);
            img_color_of_note = (ImageView) itemView.findViewById(R.id.imgColorOfNote);
        }


        public void click(final NoteInfoStruct noteInfoStruct, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(noteInfoStruct);
                }
            });
        }

        public void longClick(final NoteInfoStruct noteInfoStruct, final OnItemLongClickListener listener) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(noteInfoStruct);
                    return false;
                }
            });
        }

    }

    public NoteInfoStruct getItemAtPosition(int i) {
        return noteInfoStructs.get(i);
    }

    public void removeItem(int i) {
        noteInfoStructs.remove(i);
        this.notifyItemRemoved(i);
    }

    public interface OnItemClickListener {
        void onClick(NoteInfoStruct noteInfoStruct);
    }
    public interface OnItemLongClickListener {
        void onLongClick(NoteInfoStruct noteInfoStruct);
    }


} 
