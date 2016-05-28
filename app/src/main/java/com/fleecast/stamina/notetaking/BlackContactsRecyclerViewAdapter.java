package com.fleecast.stamina.notetaking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.ContactStruct;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactStruct} and makes a call to the
 * specified {@link BlackContactFragment.OnBlackListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BlackContactsRecyclerViewAdapter extends RecyclerView.Adapter<BlackContactsRecyclerViewAdapter.ViewHolder> {

    private List<ContactStruct> mValues;
    private final BlackContactFragment.OnBlackListFragmentInteractionListener mListener;

    public BlackContactsRecyclerViewAdapter(List<ContactStruct> items, BlackContactFragment.OnBlackListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_blacklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(mValues.get(position).getId()));
        holder.mContactNumber.setText(mValues.get(position).getContactNumber());
        holder.mContactName.setText(mValues.get(position).getContactName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onBlackListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void setFilter(List<ContactStruct> contactStructs) {
        mValues = new ArrayList<>();
        mValues.addAll(contactStructs);
        notifyDataSetChanged();
    }
public void noo(List<ContactStruct> contactStructs){
    mValues = new ArrayList<>();
    mValues.addAll(contactStructs);
    notifyDataSetChanged();
}
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContactName;
        public final TextView mContactNumber;
        public ContactStruct mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id_block);
            mContactName = (TextView) view.findViewById(R.id.contact_name_block);
            mContactNumber = (TextView) view.findViewById(R.id.contact_number_block);

        }
        @Override
        public String toString() {
            return super.toString() + " '" + mContactNumber.getText() + "'";
        }
    }
}
