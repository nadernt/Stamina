package com.fleecast.stamina.notetaking;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.ContactStruct;
import com.fleecast.stamina.models.RealmContactHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnBlackListFragmentInteractionListener}
 * interface.
 */
public class BlackContactFragment extends Fragment  implements SearchView.OnQueryTextListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnBlackListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private RealmContactHelper realmContactHelper;
    private List<ContactStruct> mContactStruct;
    private BlackContactsRecyclerViewAdapter blockListAdapter;
    private View view;
    static boolean bool = false;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BlackContactFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BlackContactFragment newInstance(int columnCount) {
        BlackContactFragment fragment = new BlackContactFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        realmContactHelper = new RealmContactHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Log.e("DBg", "Block onCreate called!");

         view = inflater.inflate(R.layout.list_black_contacts, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            loadList();
        }
        return view;
    }

    public void loadList() {

        if (recyclerView != null) {

            if(null != mContactStruct)
                mContactStruct.clear();

            mContactStruct = realmContactHelper.getBlockList();
            blockListAdapter = new BlackContactsRecyclerViewAdapter(mContactStruct, mListener);
            recyclerView.setAdapter(blockListAdapter);

        }

    }

  /*  public void doodk() {

        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(blockListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

       *//* if(null != mContactStruct)
            mContactStruct.clear();
        RealmContactHelper realmContactHelper1 = new RealmContactHelper(getActivity());
        mContactStruct = new ArrayList<>(realmContactHelper1.getBlockList());*//*
       // mContactStruct = mContactStruct1;
*//*        RealmContactHelper realmContactHelper1 = new RealmContactHelper(getActivity());
        mContactStruct = realmContactHelper1.getBlockList();*//*
        *//*blockListAdapter = new BlackContactsRecyclerViewAdapter(mContactStruct, mListener);
        blockListAdapter.notifyItemRemoved(0);
        blockListAdapter.notifyDataSetChanged();*//*
        //blockListAdapter = new BlackContactsRecyclerViewAdapter(mContactStruct, mListener);
        //blockListAdapter.noo(mContactStruct);
       *//* recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(blockListAdapter);
        recyclerView.setLayoutManager(myLayoutManager);
        myAdapter.notifyDataSetChanged();*//*


        // Set the adapter
// Set the adapter
        *//*if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
            }
            loadList();
        }*//*

        //recyclerView.
        *//*new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Log.e("DDDDssss", "dfdfsdf");

                        blockListAdapter = new BlackContactsRecyclerViewAdapter(realmContactHelper.getBlockList(), mListener);
                        blockListAdapter.notifyDataSetChanged();

                        //contactsListAdapter = new ContactsRecyclerViewAdapter(mContactStruct,mListener);
                        recyclerView.setAdapter(blockListAdapter);
                    }
                },
                5000
        );*//*

    *//*new Timer().schedule(new TimerTask() {
        @Override
        public void run () {

        }
    }

    ,0,1000);*//*
}
*/

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
                loadList();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBlackListFragmentInteractionListener) {

            mListener = (OnBlackListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBlackListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        setHasOptionsMenu(true);

        inflater.inflate(R.menu.menu_black_list_manager, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        blockListAdapter.setFilter(mContactStruct);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<ContactStruct> filteredModelList = filter(mContactStruct, newText);
        blockListAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<ContactStruct> filter(List<ContactStruct> models, String query) {
        query = query.toLowerCase();

        final List<ContactStruct> filteredModelList = new ArrayList<>();
        for (ContactStruct model : models) {
            final String text = model.getContactName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

  /*  @Override
    public void onResume() {
        super.onResume();
        Log.e("DDDDssss", "dfdfsdf");

        loadList();
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBlackListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onBlackListFragmentInteraction(ContactStruct item);
    }
}
