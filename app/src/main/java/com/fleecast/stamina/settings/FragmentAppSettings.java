package com.fleecast.stamina.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.customgui.CustomRoundButton;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Prefs;

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

        Button btnChangeDirectory = (Button) fragmentView.findViewById(R.id.btnChangeDirectory);
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

        return fragmentView;
    }

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
    private int calcPixelIndependent(int pixelToConvert){

//        float scale = (int) getResources().getDisplayMetrics().density;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelToConvert, getResources().getDisplayMetrics());
        //     return (int) (pixelToConvert * scale + 0.5f);

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
