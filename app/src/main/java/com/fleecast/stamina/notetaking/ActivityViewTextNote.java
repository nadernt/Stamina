package com.fleecast.stamina.notetaking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.PlayListHelper;
import com.fleecast.stamina.utility.Constants;

public class ActivityViewTextNote extends AppCompatActivity {

    private TextView txtTitleViewTextNote;
    private TextView txtDescriptionViewTextNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_text_note);

        txtTitleViewTextNote = (TextView) findViewById(R.id.txtTitleViewTextNote);

        txtDescriptionViewTextNote = (TextView) findViewById(R.id.txtDescriptionViewTextNote);
        handleIntents();
    }

    private void handleIntents() {

            int dbId = getIntent().getIntExtra(Constants.EXTRA_PORTRAIT_PLAYER_DBID, Constants.CONST_NULL_ZERO);

            if (getIntent().hasExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE)) {
                String title = getIntent().getStringExtra(Constants.EXTRA_PORTRAIT_PLAYER_TITLE);
                if (title == null)
                    title = "No title";
                if (title.isEmpty())
                    title = "No title";
                txtTitleViewTextNote.setText(title);
                txtTitleViewTextNote.setVisibility(View.VISIBLE);
            }

            if (getIntent().hasExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION)) {
                String description = getIntent().getStringExtra(Constants.EXTRA_PORTRAIT_PLAYER_DESCRIPTION);
                if (description == null)
                    description = "No description";
                if (description.isEmpty())
                    description = "No description";
                /*description = "Where does it come from?" +
                        "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32." +
                        "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham." +
                        "Where does it come from?" +
                        "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32." +
                        "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.";*/
                txtDescriptionViewTextNote.setText(description);
                txtDescriptionViewTextNote.setVisibility(View.VISIBLE);
            }

       }
}
