package com.fleecast.stamina.notetaking;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.BeautifyNoteText;
import com.fleecast.stamina.models.NoteInfoRealmStruct;
import com.fleecast.stamina.models.PlayListHelper;
import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.models.RealmContactHelper;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Utility;

import java.io.File;
import java.io.IOException;

public class ActivityViewTextNote extends AppCompatActivity {

    private TextView txtTitleViewTextNote;
    private TextView txtDescriptionViewTextNote;
    private ImageButton imgBtnShowViewNoteOptions;
    private int dbId;
    private RealmNoteHelper realmNoteHelper;
    private NoteInfoRealmStruct realmNoteItem;
    private BeautifyNoteText beautifyNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_text_note);
        realmNoteHelper = new RealmNoteHelper(this);

        imgBtnShowViewNoteOptions = (ImageButton) findViewById(R.id.imgBtnShowViewNoteOptions );

        txtTitleViewTextNote = (TextView) findViewById(R.id.txtTitleViewTextNote);


        txtDescriptionViewTextNote = (TextView) findViewById(R.id.txtDescriptionViewTextNote);
        handleIntents();
        imgBtnShowViewNoteOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                AlertDialog myDialog;

                String[] items;
                final RealmContactHelper realmContactHelper = new RealmContactHelper(ActivityViewTextNote.this);
/*
                if (!item.getHasAudio()) {
                    items = new String[]{"Edit", "Delete", "Share"};
                } else if (item.getHasAudio() && item.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {

                    items = new String[]{"Edit", "Delete", "Share"};
                } else {
                    if (!realmContactHelper.checkIfExistsInIgnoreList(item.getPhoneNumber().trim()))
                        items = new String[]{"Edit", "Delete", "Share", "Call", "Add to ignore"};
                    else
                        items = new String[]{"Edit", "Delete", "Share", "Call", "Remove ignore"};
                }
*/
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityViewTextNote.this);

                if (!realmNoteItem.getHasAudio()) {
                    builder.setIcon(R.drawable.text);
                    items = new String[]{"Details","Copy details","Copy all","Edit"};
                } else if (realmNoteItem.getHasAudio() && realmNoteItem.getCallType() == Constants.PHONE_THIS_IS_NOT_A_PHONE_CALL) {
                    builder.setIcon(R.drawable.audio_wave);
                    items = new String[]{"Details","Copy details","Copy all","Edit"};
                } else {

                    if(realmNoteItem.getCallType() == Constants.RECORDS_IS_OUTGOING) {
                        builder.setIcon(R.drawable.outcoming_call);
                    }else if (realmNoteItem.getCallType() == Constants.RECORDS_IS_INCOMING){
                        builder.setIcon(R.drawable.incoming_calls);
                    }

                    if (!realmContactHelper.checkIfExistsInIgnoreList(realmNoteItem.getPhoneNumber().trim()))
                        items = new String[]{"Details","Copy details","Copy all","Edit","Create contact", "Add to ignore"};
                    else
                        items = new String[]{"Details","Copy details","Copy all","Edit","Create contact", "Remove ignore"};

                }


                builder.setTitle("Options");


                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                             Utility.showMessage(beautifyNoteText.getHtmlFormatDetails(),"Details",ActivityViewTextNote.this,false,"OK");

                        } else if (which == 1) {

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Details", beautifyNoteText.getTextFormatDetails());
                            clipboard.setPrimaryClip(clip);

                        } else if (which == 2) {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Descriptions",beautifyNoteText.getTextFormatedAll());
                           clipboard.setPrimaryClip(clip);

                        } else if (which == 3) {

                        } else if (which == 4) {

                            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                    .putExtra(ContactsContract.Intents.Insert.PHONE, realmNoteItem.getPhoneNumber().trim())
                                    .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

                            startActivity(intent);
                        }

                    }
                });


                builder.setCancelable(true);
                myDialog = builder.create();
                myDialog.show();

            }
        });
    }


    private void handleIntents() {

            dbId = getIntent().getIntExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, Constants.CONST_NULL_ZERO);

        realmNoteItem = realmNoteHelper.getNoteById(dbId);

        beautifyNoteText = new BeautifyNoteText(ActivityViewTextNote.this,realmNoteItem);

        String title = realmNoteItem.getTitle();

                if (title == null)
                    title = "No title";
                if (title.isEmpty())
                    title = "No title";

                txtTitleViewTextNote.setText(title);
                txtTitleViewTextNote.setVisibility(View.VISIBLE);

                txtDescriptionViewTextNote.setText(Html.fromHtml(beautifyNoteText.getDetailsHtmlFormatedAll()));
                txtDescriptionViewTextNote.setVisibility(View.VISIBLE);

   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phone_note, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_redo_phone_edit) {
            return true;
        }
        if (id == R.id.action_undo_phone_edit) {

            return true;
        }
        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_save_phone_edit) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

  }
