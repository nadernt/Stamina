package com.fleecast.stamina.todo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fleecast.stamina.utility.Constants;

import com.fleecast.stamina.R;
import com.fleecast.stamina.utility.Utility;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ActivityAddToEvent extends AppCompatActivity implements View.OnClickListener, TimePickerFragment.OnCompleteTimePickerListener, DatePickerFragment.OnCompleteDatePickerListener {

    private WindowManager windowManager;
    private Point szWindow = new Point();
    private ImageButton imgBtnEventStartAt;
    private ImageButton imgBtnEventEndDate;
    private TextView textViewEventLayout;
    private TextView txtEventStartTime;
    private TextView txtEventConfirm;
    private TextView txtEventCancel;
    private TextView txtEventEndTime;
    private CheckBox chkEventWillFinishAt;
    private CheckBox chkEventRepeatsEveryDay;
    private CheckBox chkEventPlayAlarm;
    private CheckBox chkEvenAtLocation;
    private TextView txtEventTitle;
    private ImageButton imgBtnEventLocation;
    private String mapExtraInfo = "";
    private String longitude = "";
    private String latitude = "";


    private boolean isItStartDay = false;
    private int startHourOfDay = 0;
    private int startMinute = 0;
    private int endHourOfDay = 0;
    private int endMinute = 0;
    private int startYear = 0;
    private int startMonth = 0;
    private int startDay = 0;
    private int endYear = 0;
    private int endMonth = 0;
    private int endDay = 0;
    private long endTime = 0;
    private long startTime = 0;
    private Context mContext;
    private String addressInfo;
    private TextView editTxtEventDecription;
    private TextView txtEventLocationDetails;
    private String addressOfLocation;
    private boolean weHaveAddress=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_event);

        mContext = ActivityAddToEvent.this;

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        int width = 0;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = (szWindow.x / 3) * 2;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = szWindow.x;
        }

        this.getWindow().setLayout(width,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        imgBtnEventStartAt = (ImageButton) findViewById(R.id.imgBtnEventStartAt);
        imgBtnEventEndDate = (ImageButton) findViewById(R.id.imgBtnEventEndDate);
        imgBtnEventLocation = (ImageButton) findViewById(R.id.imgBtnEventLocation);

        textViewEventLayout = (TextView) findViewById(R.id.textViewEventLayout);
        txtEventTitle = (TextView) findViewById(R.id.txtEventTitle);
        txtEventStartTime = (TextView) findViewById(R.id.txtEventStartTime);
        txtEventConfirm = (TextView) findViewById(R.id.txtEventLocationConfirm);
        txtEventCancel = (TextView) findViewById(R.id.txtEventLocationCancel);
        txtEventEndTime = (TextView) findViewById(R.id.txtEventEndTime);
        txtEventLocationDetails= (TextView) findViewById(R.id.txtEventLocationDetails);

        chkEventWillFinishAt = (CheckBox) findViewById(R.id.chkEventWillFinishAt);
        chkEventRepeatsEveryDay = (CheckBox) findViewById(R.id.chkEventRepeatsEveryDay);
        chkEventPlayAlarm = (CheckBox) findViewById(R.id.chkEventPlayAlarm);
        chkEvenAtLocation = (CheckBox) findViewById(R.id.chkEvenAtLocation);
        editTxtEventDecription = (TextView) findViewById(R.id.editTxtEventDecription);

        txtEventTitle.setText(getIntent().getStringExtra(Constants.EXTRA_ADDED_EVENT_TITLE));


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        imgBtnEventStartAt.setOnClickListener(this);
        imgBtnEventEndDate.setOnClickListener(this);
        imgBtnEventLocation.setOnClickListener(this);
        textViewEventLayout.setOnClickListener(this);
        txtEventTitle.setOnClickListener(this);
        txtEventStartTime.setOnClickListener(this);
        txtEventConfirm.setOnClickListener(this);
        txtEventCancel.setOnClickListener(this);
        txtEventEndTime.setOnClickListener(this);
        chkEventWillFinishAt.setOnClickListener(this);
        chkEventRepeatsEveryDay.setOnClickListener(this);
        chkEventPlayAlarm.setOnClickListener(this);
        chkEvenAtLocation.setOnClickListener(this);

        Calendar cal = Calendar.getInstance();
        startTime = cal.getTimeInMillis() + 60 * 60 * 1000; //one hour later
        txtEventStartTime.setText(currentTime(startTime));
        endTime = cal.getTimeInMillis()  + 60 * 60 * 1000;
        txtEventEndTime.setText(currentTime(endTime));
    }

    private String currentTime(long unixSeconds){
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm z"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    private boolean addEventToCalendar() {

        try {


            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType("vnd.android.cursor.item/event");

            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
            if (chkEventWillFinishAt.isChecked()) {
                if(endTime>0 && endTime< startTime)
                {
                    Utility.showMessage("The end time is smaller than start time of event","Note",this);
                    return false;
                }
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
            }

            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, chkEventRepeatsEveryDay.isChecked() ? true : false);

            intent.putExtra(CalendarContract.Events.TITLE, txtEventTitle.getText().toString());

            if(editTxtEventDecription.getText()!=null) {

                String descripToSend = editTxtEventDecription.getText().toString();
                if(addressInfo!=null) {
                    if (addressInfo.trim().length() > 0) {
                        if (editTxtEventDecription.getText().toString().length() > 0)
                            descripToSend += "\n\n" + addressInfo;
                        else
                            descripToSend += addressInfo;
                    }
                }
                intent.putExtra(CalendarContract.Events.DESCRIPTION, descripToSend);

            }


            if (chkEvenAtLocation.isChecked()) {

                if(weHaveAddress) {
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, addressOfLocation);
                }
                else {
                    if(latitude.length()>0) // check user if just click on ckeckbox
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, latitude + "," + longitude);
                }

            }

            //intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

//            intent.putExtra(CalendarContract.Events.HAS_ALARM, chkEventPlayAlarm.isChecked() ? 1 : 0);
            intent.putExtra(CalendarContract.Events.HAS_ALARM, chkEventPlayAlarm.isChecked() ? true : false);

            startActivity(intent);
            return true;


        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RESULT_CODE_REQUEST_MAP_ACTIVITY) {
            if (resultCode == RESULT_OK) {

                if(data.hasExtra(Constants.EXTRA_MAP_ADDRESS)) {
                    weHaveAddress = true;
                    addressOfLocation = data.getStringExtra(Constants.EXTRA_MAP_ADDRESS);
                }else{
                    weHaveAddress = false;
                }
                    latitude = data.getStringExtra(Constants.EXTRA_MAP_LAT);

                    longitude = data.getStringExtra(Constants.EXTRA_MAP_LNG);

                if(data.hasExtra(Constants.EXTRA_MAP_FULL_INFO))
                    addressInfo = data.getStringExtra(Constants.EXTRA_MAP_FULL_INFO);

                chkEvenAtLocation.setChecked(true);

                txtEventLocationDetails.setText( addressInfo + " \n" + latitude + "," + longitude);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Utility.showMessage("Error get location!","Error",this);
                chkEvenAtLocation.setChecked(false);
            } else if (resultCode == RESULT_CANCELED) {
                chkEvenAtLocation.setChecked(false);

            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        int width = 0;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = (szWindow.x / 3) * 2;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = szWindow.x;
        }

        this.getWindow().setLayout(width,
                RelativeLayout.LayoutParams.WRAP_CONTENT);


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case  R.id.imgBtnEventStartAt:
                isItStartDay=true;
                DialogFragment newFragmentStart = new DatePickerFragment();
                newFragmentStart.show(getSupportFragmentManager(), "datePicker");
                break;
            case  R.id.imgBtnEventEndDate :
                isItStartDay=false;
                chkEventWillFinishAt.setChecked(false);
                DialogFragment newFragmentEnd = new DatePickerFragment();
                newFragmentEnd.show(getSupportFragmentManager(), "datePicker");
                break;
            case  R.id.imgBtnEventLocation :
                Intent intent = new Intent(this,MapsActivity.class);
                startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_MAP_ACTIVITY);
            break;
            case  R.id.textViewEventLayout :
            break;
            case  R.id.txtEventTitle :
            break;
            case  R.id.txtEventStartTime :
            break;
            case  R.id.txtEventLocationConfirm:
                Log.e("DBG","A");
                if(addEventToCalendar())
                finish();
            break;
            case  R.id.txtEventLocationCancel:
                Log.e("DBG","B");
                finish();
            break;
            case  R.id.txtEventEndTime :
            break;
            case  R.id.chkEventWillFinishAt :
            break;
            case  R.id.chkEventRepeatsEveryDay :
            break;
            case  R.id.chkEventPlayAlarm :
                break;
            case  R.id.chkEvenAtLocation :
                break;


        }

    }

    @Override
    public void onCompleteTimePicker(int hourOfDay, int minute) {

        if(isItStartDay) {
            startHourOfDay = hourOfDay;
            startMinute = minute;
            startTime = componentTimeToTimestamp(startYear,startMonth,startDay,hourOfDay,minute);
            txtEventStartTime.setText(currentTime(startTime));
        }else{
            endHourOfDay = hourOfDay;
            endMinute = minute;
            endTime = componentTimeToTimestamp(endYear,endMonth,endDay,hourOfDay,minute);
            txtEventEndTime.setText(currentTime(endTime));
            chkEventWillFinishAt.setChecked(true);
        }

    }

    @Override
    public void onCompleteDatePicker(int year, int month, int day) {

        if(isItStartDay) {
            startYear = year;
            startMonth = month;
            startDay = day;
            startTime = componentTimeToTimestamp(year,month,day,0,0);
            txtEventStartTime.setText(currentTime(startTime));
        }else{
            endHourOfDay=0;
            endMinute = 0;
            endYear = year;
            endMonth = month;
            endDay = day;
            chkEventWillFinishAt.setChecked(true);
            endTime = componentTimeToTimestamp(year,month,day,0,0);
            txtEventEndTime.setText(currentTime(endTime));

        }
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timefragment");

    }
    private long componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, hour, minute);
        return beginTime.getTimeInMillis();
    }
}
