package com.fleecast.stamina.chathead;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.launcher.AddEditGroupItem;
import com.fleecast.stamina.launcher.IconChooserActivity;
import com.fleecast.stamina.launcher.LauncherDialogActivity;
import com.fleecast.stamina.notetaking.ActivityAddAudioNote;
import com.fleecast.stamina.notetaking.ActivityAddTextNote;
import com.fleecast.stamina.todo.ActivityTodoParentRecyclerView;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.Utility;


/**
 * Created by nader on 2/7/2016.
 */
public class ChatHeadService extends Service implements OnScreenChangesEventListener {

    private WindowManager windowManager;
    private RelativeLayout chatheadView, removeView, launchPadView;
    private LinearLayout txtView, txt_linearlayout;
    private ImageView chatheadImg, removeImg, screen_tweak_img, help_img, minimize_bubble_img, notelist_img, note_todo_img, note_take_audio_img;
    private TextView txtTimeLapse,txtInfoBubble;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = true;
    private String sMsg = "";
    private boolean blStopAnimate=false;
    private Handler handlerAnimation;
    private Runnable runnableAnimation;
    private Handler handlerTransparentTimer;
    private Runnable runnableTransparentTimer;
    private Handler handlerScreenTimeoutTimer;
    private Runnable runnableScreenTimeoutTimer;
    private float easingAmount = 0.15f;
    private static final int HOME_Y_ZERO =0;
    private static final int TRIANGULAR_DISTANCE = 10;
    private  int CHATHEAD_HOME_Y_OFFSET_IN_PIXEL=-48;
    private int chatHeadYFinalPos =0;
    private static final int OVERLAPPING_PERCENT = 50;
    private long timeToMakeChatheadTransparent = 2000;

    private int chosenNumberToTurnOffScreen =0;
    private int defaultTimeStepsToTurnOffScreen = 10; // 10 Second
    private UserPowerPolicies userPowerPolicies;
    private ScreenReceiver mReceiver;
    private final static int FOREGROUND_ID = 999;
    private Notification notification;
    private MyApplication myApplication;
    private boolean ismReceiverRegistered=false;
    private boolean chatheadSize=false;
    private ImageView note_take_text_img;
    private boolean lockPositionOnButtomCorner =false;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }
    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private Notification createNotificationCompat(PendingIntent intent) {
        return  new NotificationCompat.Builder(this)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.notificationText))
                .setSmallIcon(R.drawable.ic_sun)
                .setContentIntent(intent)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .build();
    }

    private void handleStart(){

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        userPowerPolicies = new UserPowerPolicies(windowManager);

        PendingIntent pendingIntent = createPendingIntent();

        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new ScreenReceiver(this);
        registerReceiver(mReceiver, filter);
        ismReceiverRegistered=true;


        notification = createNotificationCompat(pendingIntent);

        startForeground(FOREGROUND_ID, notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        myApplication = (MyApplication)getApplicationContext();

        myApplication.setIsAppsListLoading(false);
        myApplication.setIsUserTerminateApp(false);

        myApplication.setCurrentGroupFilter(Constants.CONST_NULL_MINUS);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);



        /* LAUNCHPAD */
        launchPadView = (RelativeLayout)inflater.inflate(R.layout.launch_pad, null,false);
        WindowManager.LayoutParams paramOnoff = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        launchPadView.setVisibility(View.GONE);
        windowManager.addView(launchPadView, paramOnoff);

        screen_tweak_img = (ImageView) launchPadView.findViewById(R.id.screen_tweak_img);
        note_take_audio_img = (ImageView) launchPadView.findViewById(R.id.note_take_audio_img);
        minimize_bubble_img = (ImageView) launchPadView.findViewById(R.id.minimize_bubble_img);
        notelist_img = (ImageView) launchPadView.findViewById(R.id.note_list_img);
        note_todo_img = (ImageView) launchPadView.findViewById(R.id.note_todo_img);
        note_take_text_img = (ImageView) launchPadView.findViewById(R.id.note_take_text_img);
        help_img = (ImageView) launchPadView.findViewById(R.id.help_img);

        //positionMiddleLaunchpadButtons();

        /* REMOVE */
        removeView = (RelativeLayout)inflater.inflate(R.layout.remove, null);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        removeView.setVisibility(View.GONE);
        removeImg = (ImageView)removeView.findViewById(R.id.remove_img);
        windowManager.addView(removeView, paramRemove);




        /* CHATHEAD */
        chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
        chatheadImg = (ImageView)chatheadView.findViewById(R.id.chathead_img);
        txtInfoBubble = (TextView) chatheadView.findViewById(R.id.txtInfoBubble);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                calcPixelIndependent(48),
                calcPixelIndependent(48),
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;

        params.x = xFabIcon;
        params.y = yFabIcon;


        windowManager.addView(chatheadView, params);

        chatHeadYFinalPos = chatheadView.getHeight() -CHATHEAD_HOME_Y_OFFSET_IN_PIXEL;


        runTransparentTimer();


        chatheadView.setOnTouchListener(new View.OnTouchListener() {

            int pullBubbleXDest =0;
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.d(Utility.LogTag, "Into runnable_longClick");

                    isLongclick = true;
                    removeView.setVisibility(View.VISIBLE);
                    chathead_longclick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        pullBubbleXDest=0;
                        stopTransparentTimer();

                        userPowerPolicies.setChatHeadTransparent(100.0f, chatheadView, false);

                        time_start = System.currentTimeMillis();

                        //If user requested for locking the chathead then we don't have long click.
                        if (!lockPositionOnButtomCorner) {
                            handler_longClick.postDelayed(runnable_longClick, 600);
                        }

                        remove_img_width = removeImg.getLayoutParams().width;
                        remove_img_height = removeImg.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        break;
                    case MotionEvent.ACTION_MOVE:

                        //If user requested for locking the chathead then we don't want to move the chathead.
                        if (!lockPositionOnButtomCorner) {
                            checkWantsAddScreenTimeout(chatheadView);


                            int x_diff_move = x_cord - x_init_cord;
                            int y_diff_move = y_cord - y_init_cord;

                            x_cord_Destination = x_init_margin + x_diff_move;
                            y_cord_Destination = y_init_margin + y_diff_move;

                            if (isLongclick) {
                                int x_bound_left = (szWindow.x - removeView.getWidth()) / 2 - 250;
                                int x_bound_right = (szWindow.x + removeView.getWidth()) / 2 + 100;

                                int y_bound_top = szWindow.y - (removeView.getHeight() + Utility.getStatusBarHeight(getApplicationContext())) - 200;

                                if ((x_cord_Destination >= x_bound_left && x_cord_Destination <= x_bound_right) && y_cord_Destination >= y_bound_top) {
                                    inBounded = true;

                                    layoutParams.x = (szWindow.x - chatheadView.getWidth()) / 2;
                                    layoutParams.y = szWindow.y - (remove_img_width + Utility.getStatusBarHeight(getApplicationContext())) - chatheadImg.getHeight();

                                    if (removeImg.getLayoutParams().height == remove_img_height) {
                                        removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                        removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();

                                        int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                        int differencesInSizeOfIcons = (removeImg.getLayoutParams().height - chatheadView.getHeight()) / 2;
                                        int y_cord_remove = ((WindowManager.LayoutParams) chatheadView.getLayoutParams()).y - Utility.getStatusBarHeight(getApplicationContext()) - differencesInSizeOfIcons;
                                        param_remove.x = x_cord_remove;

                                        param_remove.y = y_cord_remove;

                                        windowManager.updateViewLayout(removeView, param_remove);
                                    }

                                    windowManager.updateViewLayout(chatheadView, layoutParams);
                                    break;

                                } else {
                                    inBounded = false;
                                    removeImg.getLayoutParams().height = remove_img_height;
                                    removeImg.getLayoutParams().width = remove_img_width;

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
                                    int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
                                    int y_cord_remove = szWindow.y - (removeView.getHeight() + Utility.getStatusBarHeight(getApplicationContext()));

                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeView, param_remove);
                                }

                            }

                            if (launchPadView.getVisibility() == View.GONE)
                                launchPadView.setVisibility(View.VISIBLE);

                            layoutParams.x = x_cord_Destination;
                            layoutParams.y = y_cord_Destination;

                            windowManager.updateViewLayout(chatheadView, layoutParams);
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        if(launchPadView.getVisibility() == View.VISIBLE)
                            launchPadView.setVisibility(View.GONE);

                        isLongclick = false;
                        removeView.setVisibility(View.GONE);
                        removeImg.getLayoutParams().height = remove_img_height;
                        removeImg.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if (inBounded) {


                            myApplication.setIsUserTerminateApp(true);


                            if(AddEditGroupItem.myActivityInstance!=null){
                                AddEditGroupItem.myActivityInstance.finish();
                            }

                            if(IconChooserActivity.myActivityInstance!=null){
                                IconChooserActivity.myActivityInstance.finish();
                            }

                            if( LauncherDialogActivity.active){
                                LauncherDialogActivity.myActivityInstance.finish();
                            }

                            cancelEverything();

                            stopService(new Intent(ChatHeadService.this, ChatHeadService.class));
                            inBounded = false;


                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (x_diff < 50 && y_diff < 50) {
                            time_end = System.currentTimeMillis();
                            if ((time_end - time_start) < 300) {
                                Vibrator vib = (Vibrator) ChatHeadService.this.getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 500 milliseconds
                                vib.vibrate(30);
                                //If user requested for locking the chathead then we don't have call the app launcher dialog.
                                if (!lockPositionOnButtomCorner) {
                                    chathead_click();
                                }
                                else // user wants restore the home position.
                                {
                                    lockPositionOnButtomCorner =false;

                                    int sizeofChathead=0;

                                    //Here the condition is different for chatheadSize we need to act reversed in order to avoid change the icon to wronge similar what it was before.
                                    if(!chatheadSize) {
                                        sizeofChathead = calcPixelIndependent(48);
                                    }
                                    else{
                                        sizeofChathead = calcPixelIndependent(32);
                                    }

                                   // updateUserViewSize();

                                    layoutParams.width = sizeofChathead;
                                    layoutParams.height = sizeofChathead;
                                    windowManager.updateViewLayout(chatheadView, layoutParams);                                }
                            }
                            inBounded = false;
                        }else {

                            inBounded = false;


                            /*if (checkWantsAddScreenTimeout(chatheadView)) {

                                //Stop past timer
                                timeoutCounter(0);


                                chosenNumberToTurnOffScreen += 1;
                                userPowerPolicies.setScreenAlwaysON(chatheadView, true);
                                updateUserView(String.valueOf(chosenNumberToTurnOffScreen), false);

                                timeoutCounter(1);
                            } else */
                            if (isViewOverlapping(screen_tweak_img, chatheadImg)) {

                                //initValuesToZeroForNewPolicies();

                                if (!userPowerPolicies.getCurrentScreenTweak()) {
                                    Log.e("DBG","A");
                                    updateUserView(CurrentUserOption.power_tweek_on);
                                    userPowerPolicies.setCurrentScreenTweak(true);
                                    userPowerPolicies.setScreenAlwaysON(chatheadView, true);
                                }
                                else
                                {
                                    Log.e("DBG","B");
                                    userPowerPolicies.setCurrentScreenTweak(false);
                                    userPowerPolicies.setScreenAlwaysON(chatheadView, false);
                                    updateUserView(CurrentUserOption.no_image);
                                }





                            }else if (isViewOverlapping(note_take_audio_img, chatheadImg)) {

                                // If we do not have any on going record.
                                if(myApplication.isRecordUnderGoing()!=Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE) {
                                    updateUserView(CurrentUserOption.add_audio_note);

                                    Intent intent = new Intent(ChatHeadService.this, ActivityAddAudioNote.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    if(myApplication.getCurrentRecordingAudioNoteId()>0)
                                        intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_RECORD,myApplication.getCurrentRecordingAudioNoteId());
                                    else
                                        intent.putExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_START_RECORD, true);

                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(ChatHeadService.this,"Note: A phone recording is in progress. You can not take audio note.",Toast.LENGTH_LONG).show();
                                }

                            }
                            else if (isViewOverlapping(note_take_text_img, chatheadImg)) {

                                updateUserView(CurrentUserOption.add_text_note);

                                Intent intent = new Intent(ChatHeadService.this, ActivityAddTextNote.class);
                                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                                if(myApplication.getCurrentOpenedTextNoteId()>0)
                                    intent.putExtra(Constants.EXTRA_EDIT_NOTE_AND_NO_RECORD,myApplication.getCurrentOpenedTextNoteId());
                                else
                                    intent.putExtra(Constants.EXTRA_TAKE_NEW_NOTE_AND_NO_RECORD, true);

                                //   updateChatHeadSize(1);
                                startActivity(intent);

                            }
                            else if (isViewOverlapping(notelist_img, chatheadImg)) {
                                updateUserView(CurrentUserOption.view_note_list);
                                //initValuesToZeroForNewPolicies();
                                Intent intent = new Intent(ChatHeadService.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                            else if (isViewOverlapping(note_todo_img, chatheadImg)) {

                                updateUserView(CurrentUserOption.add_todo_note);

                                Intent intent = new Intent(ChatHeadService.this, ActivityTodoParentRecyclerView.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }  else if (isViewOverlapping(minimize_bubble_img, chatheadImg)) {

                                updateUserView(CurrentUserOption.minimize_bubble);
                                //initValuesToZeroForNewPolicies();
                                lockPositionOnButtomCorner = true;

                                layoutParams.x = 0;
                                layoutParams.y = 0;

                                int sizeofChathead=calcPixelIndependent(20);
                                txtInfoBubble.setTextSize(12);
                                layoutParams.width = sizeofChathead;
                                layoutParams.height = sizeofChathead;
                                windowManager.updateViewLayout(chatheadView, layoutParams);
                                //Toast.makeText(ChatHeadRecordService.this, "app_settings_img", Toast.LENGTH_SHORT).show();
                            } else if (isViewOverlapping(help_img, chatheadImg)) {
                                updateUserView(CurrentUserOption.get_help);

                                String url = Constants.CONST_URL_HELPS;
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                            }


                        }

                        //If user requested for locking the chathead then we don't have reset to the home.
                        if (!lockPositionOnButtomCorner)
                            resetPosition();

                        break;
                    default:
                        Log.d(Utility.LogTag, "chatheadView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }
                return true;
            }
        });



        txtView = (LinearLayout)inflater.inflate(R.layout.time_laps, null);
        txtTimeLapse = (TextView) txtView.findViewById(R.id.txtTimeLaps);
        txt_linearlayout = (LinearLayout)txtView.findViewById(R.id.txt_linearlayout);

        WindowManager.LayoutParams paramsTxt = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramsTxt.gravity = Gravity.TOP | Gravity.LEFT;

        txtView.setVisibility(View.GONE);
        windowManager.addView(txtView, paramsTxt);

        resetPosition();

    }



    private void positionMiddleLaunchpadButtons() {

        float yPosOfMidButtons  = (float)(((szWindow.y/4)-Utility.getStatusBarHeight(getApplicationContext())/2) - 32);
        note_take_text_img.setY(yPosOfMidButtons) ;
        help_img.setY(yPosOfMidButtons);

    }

  /*  private void updateChatHeadSize(int iWantSmallChatHead)
    {

        WindowManager.LayoutParams params = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
*//*        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                calcPixelIndependent(48),
                calcPixelIndependent(48),
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;*//*

        int sizeofChathead=0;

        if(iWantSmallChatHead==1)
            chatheadSize = false;

        if(chatheadSize) {
            sizeofChathead = calcPixelIndependent(48);
        }
        else{
            sizeofChathead = calcPixelIndependent(32);
        }
        chatheadSize= !chatheadSize;
        updateUserViewSize();
        params.width = sizeofChathead;
        params.height = sizeofChathead;
        windowManager.updateViewLayout(chatheadView, params);

    }*/

    private int calcPixelIndependent(int pixelToConvert){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelToConvert, getResources().getDisplayMetrics());
    }
    /**
     * Initiate all values to Zero for other policies to apply new one
     */
    private void initValuesToZeroForNewPolicies(){
        timeoutCounter(0);
        chosenNumberToTurnOffScreen =0;
    }

    private void cancelEverything(){
        timeoutCounter(0);
        chosenNumberToTurnOffScreen =0;
        userPowerPolicies.setCurrentScreenTweak(!userPowerPolicies.getCurrentScreenTweak());
        userPowerPolicies.setScreenAlwaysON(chatheadView, false);
        updateUserView(CurrentUserOption.no_image);
    }

   /* private void updateUserViewSize(){

        if(!chatheadSize){
            txtInfoBubble.setTextSize(40);
        }
        else
        {
            txtInfoBubble.setTextSize(20);
        }
    }*/

    enum CurrentUserOption{
        power_tweek_on,
        add_text_note,
        add_audio_note,
        add_todo_note,
        minimize_bubble,
        view_note_list,
        get_help,
        no_image
    }
    private void updateUserView(CurrentUserOption currentUserOption){

        if(currentUserOption==CurrentUserOption.add_audio_note)
        {
            chatheadImg.setImageResource(R.drawable.note_audio);
        }
        else if(currentUserOption==CurrentUserOption.add_text_note)
        {
            chatheadImg.setImageResource(R.drawable.note_text);
        }
        else if(currentUserOption==CurrentUserOption.add_todo_note)
        {
            chatheadImg.setImageResource(R.drawable.note_todo);
        }
        else if(currentUserOption==CurrentUserOption.minimize_bubble)
        {
            chatheadImg.setImageResource(R.drawable.minimize_bubble);
        }
        else if(currentUserOption==CurrentUserOption.power_tweek_on)
        {
            chatheadImg.setImageResource(R.drawable.screen_tweak_sun);

        }
        else if(currentUserOption==CurrentUserOption.view_note_list)
        {
            chatheadImg.setImageResource(R.drawable.note_list);

        }
        else if(currentUserOption==CurrentUserOption.get_help)
        {
            chatheadImg.setImageResource(R.drawable.help_img);

        }
        else if(currentUserOption==CurrentUserOption.no_image)
        {
            chatheadImg.setImageResource(R.drawable.empty_pic);

        }


    }

    private void timeoutCounter(long timeout_milisec) {

        if(timeout_milisec==0)
        {
            if(handlerScreenTimeoutTimer!=null)
                handlerScreenTimeoutTimer.removeCallbacks(runnableScreenTimeoutTimer);

            userPowerPolicies.setScreenAlwaysON(chatheadView,false);

        }
        else {



            handlerScreenTimeoutTimer = new Handler();

            runnableScreenTimeoutTimer = new Runnable() {

                @Override
                public void run() {

                    chosenNumberToTurnOffScreen--;
                    if(chosenNumberToTurnOffScreen >= 0) {

                        long timeIntervals=0;

                        // Normal count
                        if(chosenNumberToTurnOffScreen!=0){
                            //updateUserView(String.valueOf(chosenNumberToTurnOffScreen), false);
                            timeIntervals=defaultTimeStepsToTurnOffScreen * 1000;
                        } else { // Time finished
                          //  updateUserView(String.valueOf(chosenNumberToTurnOffScreen), false);
                            // Time to dim the screen.
                            timeIntervals=3000;
                        }

                        handlerScreenTimeoutTimer.postDelayed(runnableScreenTimeoutTimer, timeIntervals);


                    }
                    else
                    {
                        userPowerPolicies.setScreenAlwaysON(chatheadView, false);
                        userPowerPolicies.turnOFFScreen(ChatHeadService.this);
                        chosenNumberToTurnOffScreen=0;
                        updateUserView(CurrentUserOption.no_image);
                        handlerScreenTimeoutTimer.removeCallbacks(runnableScreenTimeoutTimer);
                    }
                }
            };

            handlerScreenTimeoutTimer.postDelayed(runnableScreenTimeoutTimer, defaultTimeStepsToTurnOffScreen*1000);
        }

    }

    private boolean checkWantsAddScreenTimeout(View viewOfChathead){

        int[] chatheadViewPosition = new int[2];
        int xOffsetAccptableTriger =  (int)(viewOfChathead.getWidth()/1.5) *2;
        int paddingOffset = 50;

        viewOfChathead.getLocationOnScreen(chatheadViewPosition);

        int yDistance = chatheadViewPosition[1] - 0;
        int xDistance = chatheadViewPosition[0] - (int) (double)(szWindow.x-(viewOfChathead.getWidth()*2))/2 ;

        int buttomAccptableOffsetToTriger =  note_todo_img.getTop() - paddingOffset;
        int topAccptableOffsetToTriger =  viewOfChathead.getHeight() + paddingOffset;


        double distance = Math.sqrt((double) (xDistance * xDistance + yDistance * yDistance));

        int distanceFromZeroXAxis =  Math.abs((chatheadViewPosition[0]+ (viewOfChathead.getWidth()/2)) - (szWindow.x/2)) ;

        return (distanceFromZeroXAxis < xOffsetAccptableTriger) && (topAccptableOffsetToTriger < distance) && (distance < buttomAccptableOffsetToTriger);

    }

    private void runTransparentTimer() {

        handlerTransparentTimer = new Handler();
        runnableTransparentTimer = new Runnable() {

            @Override
            public void run() {

                userPowerPolicies.setChatHeadTransparent(25.0f,chatheadView,true);
                stopTransparentTimer();
            }
        };

        handlerTransparentTimer.postDelayed(runnableTransparentTimer, timeToMakeChatheadTransparent);
    }

    private void stopTransparentTimer() {

        handlerTransparentTimer.removeCallbacks(runnableTransparentTimer);
    }



    private boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        //firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int x11,y11,x12,y12,x21,y21,x22,y22;
        x11 = firstPosition[0];
        y11 = firstPosition[1];
        x12 = firstPosition[0] + firstView.getMeasuredWidth();
        y12 = firstPosition[1] + firstView.getMeasuredHeight();
        x21 = secondPosition[0];
        y21 = secondPosition[1];

        int chatheadScaleFactor=1;

        if(chatheadSize)
            chatheadScaleFactor=2;

        x22 = secondPosition[0] + (secondView.getMeasuredWidth() * chatheadScaleFactor);
        y22 = secondPosition[1] + (secondView.getMeasuredHeight() * chatheadScaleFactor);


        int x_overlap = Math.max(0, Math.min(x12,x22) - Math.max(x11,x21));
        int y_overlap = Math.max(0, Math.min(y12,y22) - Math.max(y11,y21));

        double i = ((double)(x_overlap * y_overlap)/(double)(firstView.getMeasuredWidth() * firstView.getMeasuredHeight())) * 100.0 ;

        Log.e("Views Intersections:", "OverLap: " + (int)i);

        return (int) i  >= OVERLAPPING_PERCENT;
    }

    /**
     * Screen on/off BroadcastReceiver event
     * @param screen_state
     */
    @Override
    public void onScreenOnOffEvent(boolean screen_state) {
        if (screen_state) {
            cancelEverything();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        windowManager    = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(Utility.LogTag, "ChatHeadService.onConfigurationChanged -> landscap");

            if(txtView != null){
                txtView.setVisibility(View.GONE);
            }

            if(layoutParams.y + (chatheadView.getHeight() + Utility.getStatusBarHeight(getApplicationContext())) > szWindow.y){
                layoutParams.y = szWindow.y- (chatheadView.getHeight() + Utility.getStatusBarHeight(getApplicationContext()));
                windowManager.updateViewLayout(chatheadView, layoutParams);
            }

            //If user requested for locking the chathead then we don't have call the app launcher dialog.
            if (!lockPositionOnButtomCorner) {
                if(layoutParams.x != 0 && layoutParams.x < szWindow.x){
                    resetPosition();
                }
            }
            else
            {
                layoutParams.x = 0;
                layoutParams.y = 0;
                windowManager.updateViewLayout(chatheadView, layoutParams);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d(Utility.LogTag, "ChatHeadService.onConfigurationChanged -> portrait");

            if(txtView != null){
                txtView.setVisibility(View.GONE);
            }

            //If user requested for locking the chathead then we don't have call the app launcher dialog.
            if (!lockPositionOnButtomCorner) {
                resetPosition();
            }
            else
            {
                layoutParams.x = 0;
                layoutParams.y = 0;
                windowManager.updateViewLayout(chatheadView, layoutParams);
            }


        }

      //  positionMiddleLaunchpadButtons();


    }

    private void resetPosition() {

        blStopAnimate=false;

        moveToHome();
    }


    private void moveToHome(){

        handlerAnimation = new Handler();


        //blStopAnimate=true;

        runnableAnimation = new Runnable() {
            public void run() {
                if (!blStopAnimate){
                    WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

                    float xDistance =((szWindow.x-chatheadView.getWidth())/2)- mParams.x;
                    float yDistance =  HOME_Y_ZERO - mParams.y - chatHeadYFinalPos;

                    double distance = Math.sqrt((double) (xDistance * xDistance + yDistance * yDistance));

                    if (distance > TRIANGULAR_DISTANCE) {
                        mParams.x= (int) (mParams.x + (xDistance * easingAmount));
                        mParams.y= (int) (mParams.y + (yDistance * easingAmount));
                        windowManager.updateViewLayout(chatheadView, mParams);
                        //Log.e("Tag:","X:" + mParams.x + " " + chatheadView.getX() + " Y:" + mParams.y + " Distance:" + distance);
                        handlerAnimation.postDelayed(this, 10);
                        return;
                    }
                    else
                    {
                        blStopAnimate=true;
                        runTransparentTimer();
                        return;
                    }


                }

            }


        };
        handlerAnimation.postDelayed(runnableAnimation, 10);


    }

    private void runAppListActivity(){
        Intent it = new Intent(this,LauncherDialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
        myApplication.setLauncherDialogNotVisible(false);
        startActivity(it);
    }

    // private void show() {
    //Animation fadeIn = (Animation) AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
    // this.startAnimation(fadeIn);
    // this.bannerRelativeLayout.setVisibility(VISIBLE);
    // this.setActive(true); mPopupLayout.setVisibility(VISIBLE);
    // final Animation in = new TranslateAnimation(0, 0, -1000, 0 );
    // in.setDuration(700);
    // AnimationSet animation = new AnimationSet(false);
    // animation.addAnimation(in);
    // mPopupLayout.startAnimation(animation);
    // }

    /*  public void replace(int xTo, int yTo, float xScale, float yScale) {
          // create set of animations
          replaceAnimation = new AnimationSet(false);
          // animations should be applied on the finish line
          replaceAnimation.setFillAfter(true);

          // create scale animation
          ScaleAnimation scale = new ScaleAnimation(1.0f, xScale, 1.0f, yScale);
          scale.setDuration(1000);

          // create translation animation
          TranslateAnimation trans = new TranslateAnimation(0, 0,
                  TranslateAnimation.ABSOLUTE, xTo - getLeft(), 0, 0,
                  TranslateAnimation.ABSOLUTE, yTo - getTop());
          trans.setDuration(1000);

          // add new animations to the set
          replaceAnimation.addAnimation(scale);
          replaceAnimation.addAnimation(trans);

          // start our animation
          startAnimation(replaceAnimation);
      }*/



    private void chathead_click(){
        if(LauncherDialogActivity.active){

            Log.e("DBG", "getLauncherDialogNotVisible() " + myApplication.getLauncherDialogNotVisible());

            if(LauncherDialogActivity.lostFocus) {
                LauncherDialogActivity.lostFocus=false;
                myApplication.setLauncherDialogNotVisible(true);
            }

            /**
             * Check here if user press suddenly the home screen before or
             * taapped on chathead then we close the icon choosing activities in order to free memory.
             */
            if(AddEditGroupItem.myActivityInstance!=null){
                AddEditGroupItem.myActivityInstance.finish();
            }

            if(IconChooserActivity.myActivityInstance!=null){
                IconChooserActivity.myActivityInstance.finish();
            }

            //LauncherDialogActivity.myActivityInstance.get
            /****** Set Visible ***********/
            if(myApplication.getLauncherDialogNotVisible()) {
                myApplication.setLauncherDialogNotVisible(false);
                //LauncherDialogActivity.myActivityInstance.setVisible(true);  ////
                //LauncherDialogActivity.myActivityInstance.moveTaskToBack(false); /////

                Log.e("DBG", "A");

                Intent it = new Intent(this,LauncherDialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );


                startActivity(it);


            } else {
                Log.e("DBG", "B");
                myApplication.setLauncherDialogNotVisible(true);


                //LauncherDialogActivity.myActivityInstance.setVisible(false); /////

                LauncherDialogActivity.myActivityInstance.moveTaskToBack(true);
                LauncherDialogActivity.myActivityInstance.overridePendingTransition(0, R.anim.scale_down);
            }


        }else{

            runAppListActivity();
        }

    }

    private void chathead_longclick(){
        Log.d(Utility.LogTag, "Into ChatHeadService.chathead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + Utility.getStatusBarHeight(getApplicationContext()) );

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }


    Handler myHandler = new Handler();
    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(txtView != null){
                txtView.setVisibility(View.GONE);
            }
        }
    };
    private int xFabIcon;
    private int yFabIcon;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        Log.d(Utility.LogTag, "ChatHeadService.onStartCommand() -> startId=" + startId);

        if (intent != null) {
            Bundle bd = intent.getExtras();

            if (bd != null) {
                if (bd.containsKey(Constants.CHATHEAD_X)) {
                    xFabIcon = (int)bd.getFloat(Constants.CHATHEAD_X);
                    yFabIcon = (int)bd.getFloat(Constants.CHATHEAD_Y);
                }
                else
                {
                    xFabIcon = 0;
                    yFabIcon = 0;

                }
               /* if (bd.containsKey(Utility.EXTRA_MSG))
                    sMsg = bd.getString(Utility.EXTRA_MSG);

                if (sMsg != null && sMsg.length() > 0) {
                    if (startId == Service.START_STICKY) {
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                showMsg(sMsg);
                            }
                        }, 300);

                    } else {
                        showMsg(sMsg);
                    }

                }*/

            }
        }
        if (startId == Service.START_STICKY) {
            handleStart();
            return super.onStartCommand(intent, flags, startId);
        } else {
            return Service.START_NOT_STICKY;
        }


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("TAG:", "Fart!");
        //return false;
    }

 /*   @Override
    public boolean onUnbind(Intent intent) {
        Log.e("TAG:", "on Unbinddddddddddddd");
        return super.onUnbind(intent);

    }

    @Override
    public void onRebind(Intent intent) {
        Log.e("TAG:", "on Rebinddddddddddddd");
        super.onRebind(intent);
    }*/

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        Log.e("TAG:", "I am gono to destroy!");

        if(ismReceiverRegistered)
            unregisterReceiver(mReceiver);



        if(chatheadView != null){
            // Move this line here because if app has crashed and we try to remove the service then this method makes crash again!
            cancelEverything();
            windowManager.removeView(chatheadView);
        }

        if(txtView != null){
            windowManager.removeView(txtView);
        }

        if(removeView != null){
            windowManager.removeView(removeView);
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(Utility.LogTag, "ChatHeadService.onBind()");
        return null;
    }


}