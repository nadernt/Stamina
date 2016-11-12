package com.fleecast.stamina.notetaking;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.ContactStruct;
import com.fleecast.stamina.models.RealmContactHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnIgnoreListFragmentInteractionListener}
 * interface.
 */
public class FragmentIgnoreContact extends Fragment  implements SearchView.OnQueryTextListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnIgnoreListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private RealmContactHelper realmContactHelper;
    private List<ContactStruct> mContactStruct;
    private IgnoreContactsRecyclerViewAdapter ignoreListAdapter;
    private View view;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentIgnoreContact() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentIgnoreContact newInstance(int columnCount) {
        FragmentIgnoreContact fragment = new FragmentIgnoreContact();
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
        Log.e("DBg", "Ignore onCreate called!");

         view = inflater.inflate(R.layout.list_ignore_contacts, container, false);

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

            mContactStruct = realmContactHelper.getIgnoreList();
            ignoreListAdapter = new IgnoreContactsRecyclerViewAdapter(mContactStruct, mListener);
            recyclerView.setAdapter(ignoreListAdapter);

        }

    }

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
        if (context instanceof OnIgnoreListFragmentInteractionListener) {

            mListener = (OnIgnoreListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnIgnoreListFragmentInteractionListener");
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

        inflater.inflate(R.menu.menu_ignore_list_manager, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        menu.findItem(R.id.action_add_ignore_number).setVisible(false);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        ignoreListAdapter.setFilter(mContactStruct);
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
        ignoreListAdapter.setFilter(filteredModelList);
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

    public interface OnIgnoreListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onIgnoreListFragmentInteraction(ContactStruct item);
    }
}
