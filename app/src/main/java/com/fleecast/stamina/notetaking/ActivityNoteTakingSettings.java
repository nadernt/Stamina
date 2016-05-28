package com.fleecast.stamina.notetaking;

import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;

public class ActivityNoteTakingSettings extends AppCompatActivity {

    private MediaRecorder mRecorder = null;
    private static final String TAG = "NoteTakingSettings";
    private LinearLayout linearLayAudioSources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking_settings);

        if(mRecorder!=null)
            mRecorder = null;

/*
        Field[] fields = MediaRecorder.AudioSource.class.getFields();
        Field[] fieldNames = MediaRecorder.AudioSource.class.getDeclaredFields();
*/

        mRecorder = new MediaRecorder();
        //pathToWorkingDirectory =  ExternalStorageManager.prepareWorkingDirectory(this);

/*
        pathToWorkingDirectory + File.separator + TEMP_FILE
*/

        RadioGroup radioGroupSources  = (RadioGroup) findViewById(R.id.radioGroupSources);
        RadioGroup radioGroupFormats  = (RadioGroup) findViewById(R.id.radioGroupFormats);


        // linearLayAudioSources = (LinearLayout) findViewById(R.id.linearLayAudioSources);
        for(int index=0; index<((RadioGroup)radioGroupSources).getChildCount(); ++index) {
            View nextChild = ((RadioGroup)radioGroupSources).getChildAt(index);

            if(nextChild != null && nextChild instanceof RadioButton){
                try {

                    // The default is microphone
                    if(nextChild.getTag().toString().equals(String.valueOf(Prefs.getInt("PhoneRecorderSource",MediaRecorder.AudioSource.MIC))))
                    {
                        Log.e(TAG, "Fuck Ya! " + index);
                        ((RadioButton) nextChild).setChecked(true);
                    }

                    mRecorder.setAudioSource(Integer.valueOf(nextChild.getTag().toString()));
                    mRecorder.setOutputFile(ExternalStorageManager.prepareWorkingDirectory(this) + "/enumeration");

                    Log.d(TAG, ((RadioButton) nextChild).getText().toString() + " Passed");
                }catch (Exception e)
                {
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

                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                Prefs.putInt(Constants.RECORDER_AUDIO_RECORDER_SOURCE_OPTION, Integer.valueOf(radioButton.getTag().toString()));

                Log.e(TAG, " " + radioButton.getTag().toString());
            }
        });

        radioGroupFormats.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                Prefs.putInt(Constants.RECORDER_PHONE_RECORDER_FORMAT_OPTION, Integer.valueOf(radioButton.getTag().toString()));

                Log.e(TAG, " " + radioButton.getTag().toString());
            }
        });

        /*mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(mediaRecorderSource);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            myApplication.setIsRecordIsUnderGoing(true);
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            myApplication.setIsRecordIsUnderGoing(false);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }*/

    }
/*

    @Override
    public void onClick(View v) {
        Log.d(TAG," Passed");
        if(v instanceof RadioButton) {
            if(v.isEnabled()) {
                for(int index=0; index<((ViewGroup)linearLayAudioSources).getChildCount(); ++index) {
                    View nextChild = ((ViewGroup)linearLayAudioSources).getChildAt(index);

                    if(nextChild != null && nextChild instanceof RadioButton){
                        if(((RadioButton) nextChild).getText().toString().equals(((RadioButton) v).getText()))
                        ((RadioButton) nextChild).setChecked(true);
                        }
                    }
                Prefs.putInt("MediaRecorderSource", Integer.valueOf(v.getTag().toString()));
                Toast.makeText(this, "Changes saved!", Toast.LENGTH_LONG);
            }
        }
    }
*/
}
