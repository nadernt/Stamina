package com.fleecast.stamina.notetaking;

/**
 * Created by nnt on 14/05/16.
 */
        import java.util.Date;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.telephony.TelephonyManager;
        import android.util.Log;

        import com.fleecast.stamina.chathead.MyApplication;
        import com.fleecast.stamina.models.RealmContactHelper;
        import com.fleecast.stamina.models.RealmNoteHelper;
        import com.fleecast.stamina.utility.Constants;

public class PhonecallReceiver extends BroadcastReceiver {
    private static final String TAG = "PhonecallReceiver";

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing
    private RealmContactHelper realmContactHelper;

    // private CallReceiver mCallReceiver;
    private Context context;


    private MyApplication myApplication;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        myApplication =  (MyApplication)this.context.getApplicationContext();
        realmContactHelper = new RealmContactHelper(context);

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    public PhonecallReceiver (){



    }


    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;

                Log.e(TAG, "onIncomingCallReceived   " );
                sendCommandsToService(Constants.PHONE_INCOMING_CALL_RECEIVED,number,callStartTime,null);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();

                    Log.e(TAG, "onOutgoingCallStarted   " );
                    sendCommandsToService(Constants.PHONE_OUT_GOING_CALL_STARTED,savedNumber,callStartTime,null);

                }
                else
                {
                    isIncoming = true;
                    callStartTime = new Date();
                    Log.e(TAG, "onIncomingCallAnswered" );
                    sendCommandsToService(Constants.PHONE_INCOMING_CALL_ANSWERED,savedNumber,callStartTime,null);
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    //mCallReceiver.onMissedCall(context, savedNumber, callStartTime);
                    Log.e(TAG, "onMissedCall" );
                    sendCommandsToService(Constants.PHONE_MISSING_CALL,savedNumber,callStartTime,null);
                }
                else if(isIncoming){
                    Log.e(TAG, "onIncomingCallEnded" );
                    sendCommandsToService(Constants.PHONE_INCOMING_CALL_ENDED,savedNumber,callStartTime,new Date());
                }
                else{
                    Log.e(TAG, "onOutgoingCallEnded" );
                    sendCommandsToService(Constants.PHONE_OUTGOING_CALL_ENDED,savedNumber,callStartTime,new Date());
                }
                break;
        }
        lastState = state;
    }


    private void sendCommandsToService(int whatHappenedEvent, String number, Date start,Date end) {
    if(number != null){
            if (!realmContactHelper.checkIfExistsInIgnoreList(number)) {
                Intent intentOfCall = new Intent(context, ChatHeadRecordService.class);

                if (whatHappenedEvent == Constants.PHONE_OUT_GOING_CALL_STARTED || whatHappenedEvent == Constants.PHONE_INCOMING_CALL_RECEIVED) {

                    intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_NUMBER, number);

                    if (whatHappenedEvent == Constants.PHONE_OUT_GOING_CALL_STARTED) {
                        intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_INGOING_OUTGOING, Constants.RECORDS_IS_OUTGOING);
                    } else if (whatHappenedEvent == Constants.PHONE_INCOMING_CALL_ENDED) {
                        intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_INGOING_OUTGOING, Constants.RECORDS_IS_INCOMING);
                    }

                    intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_RECORD, true);

                    Log.e(TAG, "Start Record");

                } else if (whatHappenedEvent == Constants.PHONE_INCOMING_CALL_ENDED || whatHappenedEvent == Constants.PHONE_OUTGOING_CALL_ENDED || whatHappenedEvent == Constants.PHONE_MISSING_CALL) {
                    intentOfCall = new Intent(context, ChatHeadRecordService.class);

                    intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_STOP, true);

                    if (whatHappenedEvent == Constants.PHONE_OUTGOING_CALL_ENDED) {
                        intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_INGOING_OUTGOING, Constants.RECORDS_IS_OUTGOING);
                    } else if (whatHappenedEvent == Constants.PHONE_INCOMING_CALL_ENDED || whatHappenedEvent == Constants.PHONE_MISSING_CALL) {
                        intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_INGOING_OUTGOING, Constants.RECORDS_IS_INCOMING);
                    }

                    if (null != end)
                        intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_END_TIME, end.getTime());
                    else {
                        // Misscall
                        Date date = new Date();
                        intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_END_TIME, date.getTime());
                    }

                    intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_START_TIME, start.getTime());

                    intentOfCall.putExtra(Constants.CHAT_HEAD_RECORD_INTENTS_NUMBER, number);

                    Log.e(TAG, "End Record");

                }

                context.startService(intentOfCall);
            }
        }
    }

}