package com.fleecast.stamina.chathead;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class ActivityAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .setDescription("Stamina is an android application for taking your notes easy and fast. You can use it for managing daily usages like making audio books for your university, text note taking to organise and remember the important things and your clients or friends phone calls recording. ")
                .isRTL(false)
                .setImage(R.drawable.ic_sun)
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Links")
                .addEmail("fleecast@gmail.com")
                .addWebsite("http://fleecast.com/stamina")
                .addFacebook("Stamina")
                .addItem(Credits())
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);

    }

    private Element Credits() {

        Element credits = new Element();

        final String copyrights = String.format("Copyright Information", Calendar.getInstance().get(Calendar.YEAR));

        credits.setTitle(copyrights);

        credits.setColor(ContextCompat.getColor(this, R.color.ball_blue));
        credits.setGravity(Gravity.LEFT);
        credits.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    InputStream is = getAssets().open("legal/credits.txt");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();

                    createAletDialog(ActivityAbout.this,new String(buffer),"Copyright Information","Ok");

                   /* new AlertDialog.Builder(ActivityAbout.this)
                            .setTitle("Copyright Information")
                            .setMessage(Html.fromHtml(new String(buffer)))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();*/
                } catch (IOException e) {
                    // Should never happen!
                    throw new RuntimeException(e);
                }
            }
        });

        return credits;
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();

        final String copyrights = String.format("Legal", Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);

        copyRightsElement.setColor(ContextCompat.getColor(this, R.color.ball_blue));
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    InputStream is = getAssets().open("legal/eula.txt");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();

                    createAletDialog(ActivityAbout.this,new String(buffer),"Legal","Ok");
/*
                    new AlertDialog.Builder(ActivityAbout.this)
                            .setTitle("Legal")
                            .setMessage(Html.fromHtml(new String(buffer)))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();*/
                } catch (IOException e) {
                    // Should never happen!
                    throw new RuntimeException(e);
                }
            }
        });

        return copyRightsElement;
    }

    public static void createAletDialog(Context context, String dialogMessage, String dialogTitle,String buttonText) {
        final TextView message = new TextView(context);
        // i.e.: R.string.dialog_message =>
        // "Test this dialog following the link to dtmilano.blogspot.com"
        final SpannableString s =
                new SpannableString(dialogMessage);
        Linkify.addLinks(s, Linkify.ALL);
        message.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        message.setPadding(16,25,16,25);
        message.setText(Html.fromHtml(dialogMessage));
        message.setMovementMethod(LinkMovementMethod.getInstance());

        new AlertDialog.Builder(context)
                .setTitle(dialogTitle)
                .setCancelable(true)
                .setPositiveButton(buttonText, null)
                .setView(message)
                .create()
                .show();
    }
}
