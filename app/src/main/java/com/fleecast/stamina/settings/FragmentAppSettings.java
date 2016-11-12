package com.fleecast.stamina.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.backup.ActivityBackupHome;
import com.fleecast.stamina.backup.BackupEncrypt;
import com.fleecast.stamina.customgui.CustomRoundButton;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Prefs;
import com.fleecast.stamina.utility.Utility;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentAppSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAppSettings extends Fragment {


    private static final String TAG = "FragmentAppSettings";
    private LinearLayout linearLayAudioSources;

    private View fragmentView;
    private TextView txtWorkingPath;
    private CheckBox chkIconGroupSize;
/*
    private Button btnDeletePassKey;
*/

    public FragmentAppSettings() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param columnCount
     * @return
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAppSettings newInstance(int columnCount) {
        FragmentAppSettings fragment = new FragmentAppSettings();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_app_settings, container, false);

       /* btnDeletePassKey = (Button) fragmentView.findViewById(R.id.btnDeletePassKey);

        if(!BackupEncrypt.isThereEncryptKey(getActivity()))
            btnDeletePassKey.setVisibility(View.GONE);*/

        /*btnDeletePassKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               doEncryption();

            }
        });*/

        /*Button btnChangeDirectory = (Button) fragmentView.findViewById(R.id.btnChangeDirectory);
        txtWorkingPath = (TextView) fragmentView.findViewById(R.id.txtWorkingPath);

        if(Prefs.getString(Constants.PREF_WORKING_DIRECTORY_PATH,"").length()>0) {
            txtWorkingPath.setText(Prefs.getString(Constants.PREF_WORKING_DIRECTORY_PATH, ""));
        }
        else
        {
            txtWorkingPath.setText(Environment.getExternalStorageDirectory().getPath() + Constants.CONST_WORKING_DIRECTORY_NAME);

        }

        btnChangeDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragmentView.getContext(), ActivityChooseDirectory.class);
                startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_DIRECTORY);
                //fragmentView.resu
            }
        });

        int appsIconSize = 32;
        float buttonCircleSize =  28.0f;

        int circleCenterColor = ContextCompat.getColor(getActivity(),R.color.yellow_orange);
        int outerCirclesStorkColor = ContextCompat.getColor(getActivity(), R.color.aureolin);
        int textColor = ContextCompat.getColor(getActivity(), R.color.white);

        chkIconGroupSize = (CheckBox) fragmentView.findViewById(R.id.chkIconGroupSize);


        if (Prefs.getBoolean(Constants.PREF_GROUP_ICON_SIZE, false))
            chkIconGroupSize.setChecked(true);
        else
            chkIconGroupSize.setChecked(false);

        chkIconGroupSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.putBoolean(Constants.PREF_GROUP_ICON_SIZE, chkIconGroupSize.isChecked());
                chkIconGroupSize.setText(chkIconGroupSize.getText() + " (Restart float control again)");

            }
        });
        */

        return fragmentView;
    }

   /* private void doEncryption() {

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View alertLayout = inflater.inflate(R.layout.password_dialog, null);

        final EditText txtFirstPassword = (EditText) alertLayout.findViewById(R.id.txtFirstPassword);
        final EditText txtSecondPassword = (EditText) alertLayout.findViewById(R.id.txtSecondPassword);
        final CheckBox cbShowPassword = (CheckBox) alertLayout.findViewById(R.id.chkShowPassword);
        final EditText txtOldPassword = (EditText) alertLayout.findViewById(R.id.txtOldPassword);
        final LinearLayout layoutOldPass = (LinearLayout) alertLayout.findViewById(R.id.layoutOldPass);
        final LinearLayout layoutNewPass = (LinearLayout) alertLayout.findViewById(R.id.layoutNewPass);
        final LinearLayout layoutNewPassRepeat = (LinearLayout) alertLayout.findViewById(R.id.layoutNewPassRepeat);
        final TextView txtViewPassDialogComments = (TextView) alertLayout.findViewById(R.id.txtViewPassDialogComments);

        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // to encode password in dots
                    txtSecondPassword.setTransformationMethod(null);
                    txtOldPassword.setTransformationMethod(null);
                    txtFirstPassword.setTransformationMethod(null);
                } else {
                    // to display the password in normal text
                    txtSecondPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtFirstPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                layoutOldPass.setVisibility(View.GONE);
                layoutNewPassRepeat.setVisibility(View.VISIBLE);
                layoutNewPassRepeat.setVisibility(View.GONE);
                txtViewPassDialogComments.setText("Enter current key password in order to remove the key!");
                alert.setTitle("Enter Password");

        alert.setView(alertLayout);

        alert.setCancelable(false);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        final AlertDialog dialog = alert.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean wantToCloseDialog = false;

                String strFirstPass = txtFirstPassword.getText().toString().trim();

                      if (empty(strFirstPass)) {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Empty field!</font>"));
                            return;

                        }

                        if (strFirstPass.length() < Constants.MIN_PASSWORD_LENGTH || strFirstPass.length() > Constants.MAX_PASSWORD_LENGTH) {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Password must not be more than 10 and less than 3 characters!</font>"));
                            return;

                        }

                        if (BackupEncrypt.testEncryptKey(getActivity(), strFirstPass)) {

                            Prefs.putBoolean(Constants.PREF_USER_HAS_MASTER_PASSWORD, false);

                            wantToCloseDialog = true;

                        } else {

                            txtViewPassDialogComments.setVisibility(View.VISIBLE);
                            txtViewPassDialogComments.setText(Utility.fixedHtmlFrom("<font color='RED'>Error:</font><br><font color='black'>Wrong password!</font>"));

                        }

                if (wantToCloseDialog) {
                    btnDeletePassKey.setVisibility(View.GONE);
                    if (BackupEncrypt.isThereEncryptKey(getActivity()))
                        BackupEncrypt.removeEncryptKey(getActivity());
                    dialog.dismiss();
                }

            }
        });
    }

    public static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }
*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RESULT_CODE_REQUEST_DIRECTORY) {

            // check because when we press exit button in folder picker then we wont have any returned result from activity.
            if (data != null) {
                if (data.getStringExtra(Constants.EXTRA_RESULT_SELECTED_DIR) != null) {
                    Prefs.putString(Constants.PREF_WORKING_DIRECTORY_PATH, data.getStringExtra(Constants.EXTRA_RESULT_SELECTED_DIR));
                    txtWorkingPath.setText(Prefs.getString(Constants.PREF_WORKING_DIRECTORY_PATH, ""));

                }
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.e(TAG, " onDetach FragmentAppSettings");


    }

}
