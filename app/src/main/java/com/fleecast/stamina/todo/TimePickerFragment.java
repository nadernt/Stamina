package com.fleecast.stamina.todo;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by nnt on 7/08/16.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

       // String time = Integer.toString(hourOfDay) + " : " + Integer.toString(minute);
        this.mListener.onCompleteTimePicker(hourOfDay,minute);
    }


    public interface OnCompleteTimePickerListener {
        void onCompleteTimePicker(int hourOfDay,int minute);
    }

    private OnCompleteTimePickerListener mListener;

    // make sure the Activity implemented it
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteTimePickerListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }
}