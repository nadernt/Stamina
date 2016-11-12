package com.fleecast.stamina.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by nnt on 10/08/16.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
           /* // Do something with the date chosen by the user
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(fragmentMgr, "timePicker");*/
        // String time = Integer.toString(hourOfDay) + " : " + Integer.toString(minute);
        this.mListener.onCompleteDatePicker(year,month,day);
    }

    public interface OnCompleteDatePickerListener {
        void onCompleteDatePicker(int year,int month,int day);
    }

    private OnCompleteDatePickerListener mListener;

    // make sure the Activity implemented it
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteDatePickerListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }
}