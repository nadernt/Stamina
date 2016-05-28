package com.fleecast.stamina.notetaking;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.Toast;

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
 * Activities containing this fragment MUST implement the {@link OnContactListFragmentInteractionListener}
 * interface.
 */
public class FragmentContactsList extends Fragment implements SearchView.OnQueryTextListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnContactListFragmentInteractionListener mListener;
    private ContactsRecyclerViewAdapter contactsListAdapter;
    private List<ContactStruct> mContactStruct;
    private RecyclerView recyclerView;
    public RealmContactHelper realmContactHelper;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentContactsList() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentContactsList newInstance(int columnCount) {
        FragmentContactsList fragment = new FragmentContactsList();
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

        View view = inflater.inflate(R.layout.list_contacts_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
           /* List<ContactStruct> contactStructList = new ArrayList<>();

            for(int i=0; i< 10; i++){
                contactStructList.add(new ContactStruct(i, "Nader" + String.valueOf(i), String.valueOf(Math.round(1000))));
            }*/
            loadList();
        }
        return view;
    }

    public void loadList() {
        if(recyclerView != null) {
            mContactStruct = getContacts();
            contactsListAdapter = new ContactsRecyclerViewAdapter(mContactStruct, mListener);
            recyclerView.setAdapter(contactsListAdapter);
        }
    }


/*
    private void loadList(){
        if(recyclerView != null) {

            realmContactHelper = new RealmContactHelper(getActivity());
            recyclerView.setAdapter(new IgnoreContactsRecyclerViewAdapter(realmContactHelper.getIgnoreList(), mListener));

        }

    }
*/

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            loadList();
        }
    }

    private List <ContactStruct> getContacts(){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = getActivity().getContentResolver().query(uri, projection, null, null, null);

        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        people.moveToFirst();

        List <ContactStruct> mList = new ArrayList<>();
        do {
            mList.add(new ContactStruct("0", people.getString(indexNumber), people.getString(indexName)));
        } while (people.moveToNext());

        return mList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContactListFragmentInteractionListener) {
            mListener = (OnContactListFragmentInteractionListener) context;
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
        inflater.inflate(R.menu.menu_ignore_list_manager, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);


        MenuItem menuAddNumber = menu.findItem(R.id.action_add_ignore_number);
        menuAddNumber.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Context context = getContext();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText contactName = new EditText(getContext());
                final EditText contactNumber = new EditText(getContext());

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                contactName.setHint("Name of contact");
                contactNumber.setHint("Number");

                layout.addView(contactName);
                layout.addView(contactNumber);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int id) {

                        if(contactNumber.getText().toString().trim().length() > 0 && contactName.getText().toString().trim().length() > 0)
                        {
                            if(!realmContactHelper.checkIfExistsInIgnoreList( contactNumber.getText().toString().trim())) {
                                realmContactHelper.addIgnoreList(
                                        contactNumber.getText().toString().trim(),
                                        contactName.getText().toString().trim().replaceAll("[^A-Za-z0-9 ]", "")
                                );
                                Toast.makeText(getContext(),"Contact added.",Toast.LENGTH_LONG).show();
                                Log.e("FragmentContactLists",contactName.getText().toString().trim().replaceAll("[^A-Za-z0-9 ]", "") +
                                        "<===>" + getJustNumberOfPhone(contactNumber.getText().toString().trim().replaceAll("[^A-Za-z0-9 ]", "")));
                            }
                            else {
                                showMessage("Number already exists.", "Note");
                            }

                        }
                        else{
                            showMessage("You should fill all the fields.","Wrong input");
                        }
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.setTitle("Add Custom Ignore Number");
                dialog.setView(layout);

                dialog.show();

                return false;

            }
        });
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        contactsListAdapter.setFilter(mContactStruct);
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
        contactsListAdapter.setFilter(filteredModelList);
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
    public interface OnContactListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onContactsListFragmentInteraction(ContactStruct item);
    }

    //Filter the number by regex then just a consequence of numbers will make as phone number.
    private String getJustNumberOfPhone(String strNumber) {

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(strNumber);
        String returnedNumber = "";
        while (m.find()) {
            returnedNumber += m.group();
        }

        return returnedNumber;

    }

    private void showMessage(String messageToUser,String titleOfDialog){

        new AlertDialog.Builder(getContext())
                .setTitle(titleOfDialog)
                .setMessage(messageToUser)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

}
