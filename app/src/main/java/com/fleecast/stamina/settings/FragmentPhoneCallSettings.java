package com.fleecast.stamina.settings;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentPhoneCallSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPhoneCallSettings extends Fragment {
    private MediaRecorder mRecorder = null;
    private static final String TAG = "NoteTakingSettings";
    private LinearLayout linearLayAudioSources;

    private View fragmentView;

    public FragmentPhoneCallSettings() {
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
    public static FragmentPhoneCallSettings newInstance(int columnCount) {
        FragmentPhoneCallSettings fragment = new FragmentPhoneCallSettings();
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
        fragmentView = inflater.inflate(R.layout.fragment_phone_call_settings, container, false);

        if (mRecorder != null)
            mRecorder = null;


        mRecorder = new MediaRecorder();


        RadioGroup radioGroupSources = (RadioGroup) fragmentView.findViewById(R.id.radioGroupSources);


        // linearLayAudioSources = (LinearLayout) fragmentView.findViewById(R.id.linearLayAudioSources);
        for (int index = 0; index < radioGroupSources.getChildCount(); ++index) {
            View nextChild = radioGroupSources.getChildAt(index);

            if (nextChild != null && nextChild instanceof RadioButton) {
                try {

                    // The default is microphone
                    if (nextChild.getTag().toString().equals(String.valueOf(Prefs.getInt(Constants.RECORDER_PHONE_RECORDER_SOURCE_OPTION, MediaRecorder.AudioSource.VOICE_CALL)))) {
                        Log.e(TAG, "Fuck Ya! " + index);
                        ((RadioButton) nextChild).setChecked(true);
                    }

                    mRecorder.setAudioSource(Integer.valueOf(nextChild.getTag().toString()));
                    mRecorder.setOutputFile(ExternalStorageManager.prepareWorkingDirectory(getActivity()) + "/enumeration");

                    Log.d(TAG, ((RadioButton) nextChild).getText().toString() + " Passed");
                } catch (Exception e) {
                    nextChild.setEnabled(false);
                    ((RadioButton) nextChild).setText(((RadioButton) nextChild).getText().toString() + " not support by device!");
                    Log.e(TAG, ((RadioButton) nextChild).getText().toString() + " No Passed " + e.getMessage());
                }
            }
            mRecorder.reset();

        }


        radioGroupSources.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton) fragmentView.findViewById(checkedId);
                Prefs.putInt(Constants.RECORDER_PHONE_RECORDER_SOURCE_OPTION, Integer.valueOf(radioButton.getTag().toString()));


                Log.e(TAG, " " + radioButton.getTag().toString());
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
        Log.e(TAG, " onDetach FragmentPhoneCallSettings");

    }

}
