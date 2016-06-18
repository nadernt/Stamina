package com.fleecast.stamina.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentNoteTakingSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNoteTakingSettings extends Fragment {


    private MediaRecorder mRecorder = null;
    private static final String TAG = "NoteTakingSettings";
    private LinearLayout linearLayAudioSources;
    private MyApplication myApplication;

    private View fragmentView;

    public FragmentNoteTakingSettings() {
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
    public static FragmentNoteTakingSettings newInstance(int columnCount) {
        FragmentNoteTakingSettings fragment = new FragmentNoteTakingSettings();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_note_taking_settings, container, false);

        final CheckBox chkStopPlayerOnListFinish = (CheckBox) fragmentView.findViewById(R.id.chkStopPlayerOnListFinish);
        final CheckBox chkShowFullPlayerNotification = (CheckBox) fragmentView.findViewById(R.id.chkShowFullPlayerNotification);


        if (mRecorder != null)
            mRecorder = null;

        mRecorder = new MediaRecorder();

        RadioGroup radioGroupSources = (RadioGroup) fragmentView.findViewById(R.id.radioGroupSources);
        RadioGroup radioGroupQuality = (RadioGroup) fragmentView.findViewById(R.id.radioGroupQuality);


        // initial radio buttons
        for (int index = 0; index < ((RadioGroup) radioGroupSources).getChildCount(); ++index) {
            View nextChild = ((RadioGroup) radioGroupSources).getChildAt(index);

            if (nextChild != null && nextChild instanceof RadioButton) {
                try {

                    // The default is microphone
                    if (nextChild.getTag().toString().equals(String.valueOf(Prefs.getInt(Constants.RECORDER_AUDIO_RECORDER_SOURCE_OPTION, MediaRecorder.AudioSource.MIC)))) {
                        ((RadioButton) nextChild).setChecked(true);
                    }

                    mRecorder.setAudioSource(Integer.valueOf(nextChild.getTag().toString()));
                    mRecorder.setOutputFile(ExternalStorageManager.prepareWorkingDirectory(getActivity()) + "/enumeration");
                } catch (Exception e) {
                    nextChild.setEnabled(false);
                    ((RadioButton) nextChild).setText(((RadioButton) nextChild).getText().toString() + " not support by device!");
                }
            }
            mRecorder.reset();

        }


        for (int index = 0; index < ((RadioGroup) radioGroupQuality).getChildCount(); ++index) {
            View nextChild = ((RadioGroup) radioGroupQuality).getChildAt(index);

            if (nextChild != null && nextChild instanceof RadioButton) {
                    // The default is medium quality
                    if (nextChild.getTag().toString().equals(String.valueOf(Prefs.getInt(Constants.RECORDER_AUDIO_RECORDER_QUALITY_OPTION, Constants.RECORDER_AUDIO_RECORDER_QUALITY_MEDIUM)))) {
                        ((RadioButton) nextChild).setChecked(true);
                    }

            }

        }

        radioGroupSources.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton) fragmentView.findViewById(checkedId);
                Prefs.putInt(Constants.RECORDER_AUDIO_RECORDER_SOURCE_OPTION, Integer.valueOf(radioButton.getTag().toString()));

                Log.e(TAG, " " + radioButton.getTag().toString());
            }
        });

        radioGroupQuality.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton) fragmentView.findViewById(checkedId);
                Prefs.putInt(Constants.RECORDER_AUDIO_RECORDER_QUALITY_OPTION, Integer.valueOf(radioButton.getTag().toString()));

                Log.e(TAG, " " + radioButton.getTag().toString());
            }
        });

        if(Prefs.getBoolean(Constants.PREF_ON_FINISH_PLAYLIST_CLOSE_PLAYER_REMOTE,false))
            chkStopPlayerOnListFinish.setChecked(true);
        else
            chkStopPlayerOnListFinish.setChecked(false);

        if(Prefs.getBoolean(Constants.PREF_SHOW_PLAYER_FULL_NOTIFICATION,false))
            chkShowFullPlayerNotification.setChecked(true);
        else
            chkShowFullPlayerNotification.setChecked(false);

        chkStopPlayerOnListFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.putBoolean(Constants.PREF_ON_FINISH_PLAYLIST_CLOSE_PLAYER_REMOTE,chkStopPlayerOnListFinish.isChecked());
            }
        });

        chkShowFullPlayerNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.putBoolean(Constants.PREF_SHOW_PLAYER_FULL_NOTIFICATION,chkShowFullPlayerNotification.isChecked());
            }
        });


        return fragmentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (null != mRecorder) {
            mRecorder.release();
            mRecorder = null;
        }
        Log.e(TAG, " onDetach FragmentNoteTakingSettings");


    }


}
