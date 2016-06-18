package com.fleecast.stamina.notetaking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.utility.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

// Demonstration of using fragments to implement different activity layouts.
// This sample provides a different layout (and activity flow) when run in
// landscape.

public class ActivityRecordsPlayList extends Activity {

    private static Activity anInstance;
    private static Shakespeare sk;
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private TextView txtTotalTime;
    private WindowManager windowManager;
    private Point szWindow = new Point();
    private ImageButton btnPlay, btnStop,btnRewindTrack,btnNextTrack;
    private TextView txtTitlePlayer;
    private static TextView txtDetailsPlayer;
    private int oldMediaSeekPosition = 0;
    private MyApplication myApplication;
    private static boolean playlistHasLoaded =false;
    private static ImageButton btnRewindNote;
    private ImageButton btnNextNote;
    private static int notePointer =0;
    private ImageView imgNoNotePlaceHolder;
    private static RelativeLayout details;
    private static int parentDbId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_layout);
        myApplication =  (MyApplication)getApplicationContext();


        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime1);
        txtDetailsPlayer = (TextView) findViewById(R.id.txtDetailsPlayer);

        btnPlay = (ImageButton) findViewById(R.id.btnPlay); // Start
        btnStop = (ImageButton) findViewById(R.id.btnStop); // Stop
        btnNextTrack = (ImageButton) findViewById(R.id.btnNextTrack); // Next
        btnRewindTrack = (ImageButton) findViewById(R.id.btnRewindTrack); // Rewind
        btnRewindNote = (ImageButton) findViewById(R.id.btnRewindNote); // Rewind
        btnNextNote = (ImageButton) findViewById(R.id.btnNextNote); // Rewind
        imgNoNotePlaceHolder = (ImageView) findViewById(R.id.imgNoNotePlaceHolder);
        txtTitlePlayer = (TextView) findViewById(R.id.txtTitlePlayer);
        txtDetailsPlayer = (TextView) findViewById(R.id.txtDetailsPlayer);

        details = (RelativeLayout) findViewById(R.id.details);
        btnPlay.setImageResource(R.drawable.ic_action_playback_play);

        seekBar = (SeekBar) findViewById(R.id.seekBar2);

        if(!playlistHasLoaded) {
            loadPlayListForListViw("1465131201", false);
            parentDbId = Integer.valueOf("1465131201");
        }else {
            TitlesFragment.highlightSelectedNoteItem(notePointer);
        }

        fillTextNotesForUser();

        // Handle Intents & action
      //  handleIntents(getIntent().getAction());


        seekBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(myApplication.isPlaying())
                    updateSeekChange();
                else
                    return true;
                return false;
            }
        });

        // Start
        btnPlay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                if(!myApplication.isPlaying()) {
                    sendCommandToPlayerService(Constants.ACTION_PLAY,Constants.ACTION_NULL);
                    play();
                }
                else
                {
                    sendCommandToPlayerService(Constants.ACTION_PAUSE,Constants.ACTION_NULL);
                    pause();
                }

                myApplication.setIsPlaying(!myApplication.isPlaying());

            }
        });

        // Stop
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendCommandToPlayerService(Constants.ACTION_STOP,Constants.ACTION_NULL);
                stop();
            }
        });

        btnNextTrack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nextTrack();
            }
        });

        btnRewindTrack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rewindTrack();
            }
        });

        btnRewindNote.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(notePointer > 0){
                    notePointer--;
                    TitlesFragment.highlightSelectedNoteItem(notePointer);
                    fadeWidgets();

                }
                else {
                    notePointer=0;
                    TitlesFragment.highlightSelectedNoteItem(0);
                }
                fillTextNotesForUser();
            }
        });

        btnNextNote.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(notePointer <  myApplication.stackPlaylist.size()-1){
                    notePointer++;
                    TitlesFragment.highlightSelectedNoteItem(notePointer);
                    fadeWidgets();

                }
                else
                {
                    notePointer=myApplication.stackPlaylist.size()-1;
                    TitlesFragment.highlightSelectedNoteItem(myApplication.stackPlaylist.size()-1);
                }
                fillTextNotesForUser();
            }
        });

        txtDetailsPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details.setVisibility(View.GONE);
            }
        });

    }

    private void fillTextNotesForUser(){

        String txtTitl  = myApplication.stackPlaylist.get(notePointer).getTitle();

        if(txtTitl==null)
            txtTitlePlayer.setText(Constants.CONST_STRING_NO_NOTE);

        String txtDescr  = myApplication.stackPlaylist.get(notePointer).getDescription();
        if(txtDescr==null) {
            imgNoNotePlaceHolder.setVisibility(View.VISIBLE);
            txtDescr = unixTimeToReadable((long) getFilePostFixId(myApplication.stackPlaylist.get(notePointer).getFileName()));
        }
        txtDetailsPlayer.setText(txtDescr);

    }

    private void fadeWidgets(){

        details.setVisibility(View.VISIBLE);
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(500);
        txtDetailsPlayer.startAnimation(animation1);
        txtTitlePlayer.startAnimation(animation1);
    }
    final Timer timer = new Timer();

    public void textEffect(){
        txtTitlePlayer.setTypeface(null, Typeface.BOLD);
        txtDetailsPlayer.setTypeface(null, Typeface.BOLD);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtTitlePlayer.setTypeface(null, Typeface.NORMAL);
                            txtDetailsPlayer.setTypeface(null, Typeface.NORMAL);
                        }
                    });
                }
            };
            timer.schedule(task,  500);
    }

    private String unixTimeToReadable(long unixSeconds){

        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        // E, dd MMM yyyy HH:mm:ss z
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss a"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        //System.out.println(formattedDate);
        return formattedDate;

    }

    private int getFilePostFixId(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
            return Integer.valueOf(file_name.substring(file_name.lastIndexOf("_") + 1));
    }

    private void loadPlayListForListViw(String mFileDbUniqueId,boolean startAutoPlayeOnStart) {
        // Populate list with our static array of titles in list in the
        // Shakespeare class
        //Shakespeare  sk = new Shakespeare(getActivity(),"1465065298""1465131201");
        sk = new Shakespeare(this, mFileDbUniqueId);

        sk.loadAudioListForPlayerService();

        myApplication.setIndexSomethingIsPlaying(0);

        playlistHasLoaded =true;

        if(startAutoPlayeOnStart) {
            handleIntents(null);
//            btnPlay.performClick();
        }

    }

    private void handleIntents(String mAction){

        if(mAction==null){

            Intent intent = new Intent(this, PlayerService.class);
            intent.putExtra(Constants.EXTRA_PLAY_NEW_SESSION, true);
            startService(intent);

        }else{

            if(mAction.equals(Constants.ACTION_SHOW_PLAYER_NO_NEW)){
                if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
                    btnPlay.setImageResource(R.drawable.ic_action_playback_pause);
                    myApplication.setIsPlaying(true);
                }
                else{
                    btnPlay.setImageResource(R.drawable.ic_action_playback_play);
                    myApplication.setIsPlaying(false);
                }

                //We set here to be sure there is not any mistake from any control before and thread start correctly.
                oldMediaSeekPosition=0;
                seekBar.setMax(myApplication.getMediaDuration());
                startPlayProgressUpdater();
            }


        }

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void play()
    {
        myApplication.setIsPlaying(true);
        btnPlay.setImageResource(R.drawable.ic_action_playback_pause);
        seekBar.setMax(myApplication.getMediaDuration());
        startPlayProgressUpdater();
    }

    private void pause()
    {
        myApplication.setIsPlaying(false);
        btnPlay.setImageResource(R.drawable.ic_action_playback_play);
        txtTotalTime.setText("Pause");
    }

    private void stop()
    {
        myApplication.setIsPlaying(false);
        handler.removeCallbacksAndMessages(null);
        txtTotalTime.setText("Stop");
        seekBar.setProgress(0);
        btnPlay.setImageResource(R.drawable.ic_action_playback_play);
        btnPlay.setEnabled(true);
    }

    private void nextTrack()
    {
        // stop();
        sendCommandToPlayerService(Constants.ACTION_NEXT,Constants.ACTION_NULL);

    }

    private void rewindTrack()
    {
        sendCommandToPlayerService(Constants.ACTION_REWIND,Constants.ACTION_NULL);

    }

    private void sendCommandToPlayerService(String actionCommand, int seekTo) {

        Intent intent = new Intent(this,PlayerService.class);

        if(!actionCommand.equals(Constants.EXTRA_SEEK_TO) && !actionCommand.equals(Constants.EXTRA_UPDATE_SEEKBAR)) {
            intent.setAction(actionCommand);
        }else
        {
            if(actionCommand == Constants.EXTRA_SEEK_TO )
                intent.putExtra(Constants.EXTRA_SEEK_TO,seekTo);

            if(actionCommand == Constants.EXTRA_UPDATE_SEEKBAR)
                intent.putExtra(Constants.EXTRA_UPDATE_SEEKBAR,true);

        }
        startService(intent);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        int width = 0;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = (szWindow.x / 3) * 2;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            width = szWindow.x;
        }

        this.getWindow().setLayout(width,
                RelativeLayout.LayoutParams.WRAP_CONTENT);*/


    }

    private void updateSeekChange() {
        sendCommandToPlayerService(Constants.EXTRA_SEEK_TO,seekBar.getProgress());
        setProgressText();
    }

    public void startPlayProgressUpdater() {

        sendCommandToPlayerService(Constants.EXTRA_UPDATE_SEEKBAR,Constants.ACTION_NULL);

        if(oldMediaSeekPosition != myApplication.getCurrentMediaPosition()) {
            seekBar.setProgress(myApplication.getCurrentMediaPosition());
            setProgressText();
            oldMediaSeekPosition = myApplication.getCurrentMediaPosition();
        }
        if (myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, Constants.CONST_PLAYER_PROGRESS_UPDATE_TIME);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if(handler  != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    protected void setProgressText() {

        final int HOUR = 60 * 60 * 1000;
        final int MINUTE = 60 * 1000;
        final int SECOND = 1000;

        int durationInMillis = myApplication.getMediaDuration();
        int curVolume = myApplication.getCurrentMediaPosition();

        int durationHour = durationInMillis / HOUR;
        int durationMint = (durationInMillis % HOUR) / MINUTE;
        int durationSec = (durationInMillis % MINUTE) / SECOND;

        int currentHour = curVolume / HOUR;
        int currentMint = (curVolume % HOUR) / MINUTE;
        int currentSec = (curVolume % MINUTE) / SECOND;

        if (durationHour > 0) {
            txtTotalTime.setText(String.format("%02d:%02d:%02d/%02d:%02d:%02d",
                    currentHour, currentMint, currentSec, durationHour, durationMint, durationSec));
        } else {
            txtTotalTime.setText(String.format("%02d:%02d/%02d:%02d",
                    currentMint, currentSec, durationMint, durationSec));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Constants.INTENTFILTER_PLAYER_SERVICE)
        );

        if(myApplication.getPlayerServiceCurrentState()==Constants.CONST_PLAY_SERVICE_STATE_PLAYING) {
            btnPlay.setImageResource(R.drawable.ic_action_playback_pause);
            myApplication.setIsPlaying(true);
        }
        else{
            btnPlay.setImageResource(R.drawable.ic_action_playback_play);
            myApplication.setIsPlaying(false);
        }

        //We set here to be sure there is not any mistake from any control before and thread start correctly.
        oldMediaSeekPosition=0;
        seekBar.setMax(myApplication.getMediaDuration());
        startPlayProgressUpdater();

    }


    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENTFILTER_PLAYER_SERVICE)) {
                int statusCode = intent.getIntExtra(Constants.EXTRA_PLAYER_SERVICE_PLAY_STATUS, -1);
                switch (statusCode) {

                    case Constants.PLAYER_SERVICE_STATUS_ERROR:
                        Toast.makeText(ActivityRecordsPlayList.this, "Error playing the media file!", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_PLAYING:
                        TitlesFragment.highlightSelectedPlayItem(myApplication.getIndexSomethingIsPlaying());
                        play();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_TRACK_FINISHED:
                        //Log.e("DBG", "Command TRACK Finished or Stop Recieved");
                        stop();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_CLOSE_PLAYER:
                        //Log.e("DBG", "Command Close Player Recieved");
                        stop();
                        finish();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_PAUSE:
                        pause();
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_SEEK_BAR_UPDATED:
                        break;
                    case Constants.PLAYER_SERVICE_STATUS_TACK_CHANGED:

                        break;

                }

            }
        }

    };
    // This is a secondary activity, to show what the user has selected when the
    // screen is not large enough to show it all in one activity.

 /*   public static class DetailsActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Toast.makeText(this, "DetailsActivity", Toast.LENGTH_SHORT).show();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.

                // create fragment
                DetailsFragment details = new DetailsFragment();

                // get and set the position input by user (i.e., "index")
                // which is the construction arguments for this fragment
                details.setArguments(getIntent().getExtras());

                //
                getFragmentManager().beginTransaction()
                        .add(android.R.id.content, details).commit();
            }
        }
    }
*/
    // This is the "top-level" fragment, showing a list of items that the user
    // can pick. Upon picking an item, it takes care of displaying the data to
    // the user as appropriate based on the current UI layout.

    // Displays a list of items that are managed by an adapter similar to
    // ListActivity. It provides several methods for managing a list view, such
    // as the onListItemClick() callback to handle click events.

    public static class TitlesFragment extends ListFragment {
        boolean mDualPane;
        int mCurCheckPosition = 0;
        private MyApplication myApplication;
        private static ListView playlistListviewInstance;
        private static Context mContextTitlesFragment;
        private ArrayAdapter<Spanned> la;
        private AlertDialog myDialog;
        // onActivityCreated() is called when the activity's onCreate() method
        // has returned.

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }

       /* @Override
        public void setListShown(boolean shown) {
            super.setListShown(shown);
            Log.e("DBG","Honglar");
            highlightSelectedNoteItem(notePointer);

            highlightSelectedPlayItem(mCurCheckPosition);
            //for (int i = 0; i < playlistListviewInstance.getChildCount(); i++) {
            //  if (playlistListviewInstance.getCheckedItemPosition() == i)
            //    getListView().getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.radical_red));
            //else if (indexNoteItemToHighlight == i) {
            getListView().getChildAt(0).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.deep_sky_blue));
            la.notifyDataSetChanged();
        }*/

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            myApplication = (MyApplication) getActivity().getApplication();

            Log.e("YYYYYYYYYYYYYYY", "KAKDILA");
            // You can use getActivity(), which returns the activity associated
            // with a fragment.
            // The activity is a context (since Activity extends Context) .



            la = new ArrayAdapter<Spanned>(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    sk.loadAudioListForListViewAdapter());

           // setListAdapter(la);

            //if (!myApplication.isPlaying())
            setListAdapter(la);
            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            // R.id.details relates to the res/layout-land/fragment_layout.xml
            // This is first created when the phone is switched to landscape
            // mode

            mContextTitlesFragment = getActivity();
           // View detailsFrame = getActivity().findViewById(R.id.details);

         /*   Toast.makeText(getActivity(), "detailsFrame " + detailsFrame,
                    Toast.LENGTH_LONG).show();
*/
            // Check that a view exists and is visible
            // A view is visible (0) on the screen; the default value.
            // It can also be invisible and hidden, as if the view had not been
            // added.
            //
/*
            mDualPane = detailsFrame != null
                    && detailsFrame.getVisibility() == View.VISIBLE;
*/

        /*    Toast.makeText(getActivity(), "mDualPane " + mDualPane,
                    Toast.LENGTH_LONG).show();*/

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }

            playlistListviewInstance =getListView();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                mDualPane=true;
                // In dual-pane mode, the list view highlights the selected
                // item.
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
                //showDetails(mCurCheckPosition);
                details.setVisibility(View.VISIBLE);
            }
            else{

                // We also highlight in uni-pane just for fun
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                getListView().setItemChecked(mCurCheckPosition, true);
                details.setVisibility(View.GONE);
                //Log.e("FDDDDDDDDDDd", "FFFFFAAAAAAAAA");
                mDualPane=false;

            }

            // String txtTitl  = myApplication.stackPlaylist.get(notePointer).getTitle();

            /*final ListView lv = getListView();
            final ListViewSwipeDetector swipeDetector = new ListViewSwipeDetector();

            lv.setOnTouchListener(swipeDetector);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (swipeDetector.swipeDetected()){
                        if(swipeDetector.getAction()==ListViewSwipeDetector.Action.LR)
                        {
                            if(mDualPane) {
                                //Log.e("FDDDDDDDDDDd", "FFFFFAAAAAAAAA");
                                // BuildMyString.com generated code. Please enjoy your string responsibly.

                                String sb = " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent luctus justo ut posuere rhoncus. Aliquam sed fringilla dui. Nunc quis molestie urna. Morbi in orci vitae dolor hendrerit venenatis commodo at sem. Nunc ut imperdiet orci, at dictum est. Vestibulum vitae massa risus. Curabitur laoreet odio quis metus venenatis, non condimentum orci euismod. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla facilisi. Integer placerat neque libero, ut sollicitudin justo dapibus nec. Aenean in pharetra orci. Sed sagittis, lectus ultrices euismod maximus, ante lectus aliquet elit, egestas porttitor nulla nulla pretium turpis. Integer id pharetra velit, non condimentum neque." +
                                        "Donec id augue luctus, mattis tellus et, semper libero. Praesent faucibus ligula a risus condimentum maximus. Nunc accumsan varius ex quis luctus. Fusce efficitur eleifend commodo. Sed mattis tortor ut tellus suscipit vehicula. Praesent id odio vitae ligula porta tincidunt at id enim. Morbi vitae dictum orci. Proin interdum sed metus eu vulputate. Suspendisse tincidunt turpis a dignissim dignissim." +
                                        "Sed nec sapien semper, accumsan tellus at, faucibus elit. Ut non erat at tellus sodales mattis. Proin non varius tortor, vel mollis risus. Morbi eleifend enim nunc, sit amet malesuada nisl tincidunt nec. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Quisque mattis neque interdum aliquam pharetra. Pellentesque eget consequat justo, sit amet consequat tellus. Nullam faucibus semper ligula, eu pulvinar orci rutrum vitae. Donec malesuada convallis lorem. ";


                                txtDetailsPlayer.setText(Math.random() + sb);
                                txtDetailsPlayer.setMovementMethod(new ScrollingMovementMethod());

                            }
                        }

                        // do the onSwipe action
                    } else {
                        showDetails(position);
                    }
                }
            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                    if (swipeDetector.swipeDetected()){
                        // do the onSwipe action
                    } else {

                    }
                    return false;
                }
            });*/

            getListView().setLongClickable(true);

            getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    final int chosenItemIndex = pos;
                    String[] items = {"Add/Edit note","Delete note","Delete note and audio"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Options");
                    builder.setIcon(R.drawable.audio_wave);
                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which==0){
                                Intent intent = new Intent(getActivity(),AddNoteToAudio.class);
                                //myApplication.stackPlaylist.get(chosenItemIndex).
                                //Log.e("DBG",)
                                runAddAudioNoteActivity(parentDbId,myApplication.stackPlaylist.get(chosenItemIndex).getId());
                            }
                            else if(which==1){

                                RealmAudioNoteHelper realmAudioNoteHelper = new RealmAudioNoteHelper(getActivity());

                                realmAudioNoteHelper.deleteSingleAudioNote(myApplication.stackPlaylist.get(chosenItemIndex).getId());
                            }
                            else if (which==2){

                            }
                        }
                    });


                    builder.setCancelable(true);
                    myDialog = builder.create();
                    myDialog.show();
                    return true;
                }
            });

        }

        // If the user clicks on an item in the list (e.g., Henry V then the
        // onListItemClick() method is called. It calls a helper function in
        // this case.

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {

            Toast.makeText(getActivity(),
                    "onListItemClick position is" + position, Toast.LENGTH_LONG)
                    .show();

            showDetails(position);
        }


        // Method to handle the Click Event on GetMessage Button
        public void runAddAudioNoteActivity(int idParentDb,int dbIdFile)
        {
            // Create The  Intent and Start The Activity to get The message
            Intent intent=new Intent(getActivity(),AddNoteToAudio.class);

            intent.putExtra(Constants.EXTRA_AUDIO_NOTE_PARENT_DB_ID,idParentDb);

            intent.putExtra(Constants.EXTRA_AUDIO_NOTE_FILE_DB_ID, dbIdFile);

            startActivityForResult(intent, Constants.RESULT_CODE_REQUEST_DIALOG);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode==Constants.RESULT_CODE_REQUEST_DIALOG)
            {
                if(null!=data)
                {
                    // fetch the message String
                    String message=data.getStringExtra("MESSAGE");

                    Log.e("Chatanuga","Oh lala");
                    // Set the message string in textView
                    //textViewMessage.setText("Message from second Activity: " + message);
                }
            }

        }

        /*
        // Call Back method  to get the Message form other Activity
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);

            // check if the request code is same as what is passed  here it is 2
            if(requestCode==2)
            {
                if(null!=data)
                {
                    // fetch the message String
                    String message=data.getStringExtra("MESSAGE");
                    // Set the message string in textView
                    textViewMessage.setText("Message from second Activity: " + message);
                }
            }
        }*/

        private static void highlightSelectedPlayItem(int indexAudioItemToHighlight){

            playlistListviewInstance.setItemChecked(indexAudioItemToHighlight, true);

            for(int i=0 ; i< playlistListviewInstance.getChildCount();i++) {
                if (playlistListviewInstance.getCheckedItemPosition()== i) {
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.radical_red));
                }
                else if (notePointer == i)
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.deep_sky_blue));
                else
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(playlistListviewInstance.getSolidColor());

            }
        }

        private static void highlightSelectedNoteItem(int indexNoteItemToHighlight) {

            for (int i = 0; i < playlistListviewInstance.getChildCount(); i++) {
                if (playlistListviewInstance.getCheckedItemPosition() == i)
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.radical_red));
                 else if (indexNoteItemToHighlight == i) {
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(ContextCompat.getColor(mContextTitlesFragment, R.color.deep_sky_blue));
                    Log.e("SSSSSSSSs","GGGGGGGGGG");
                }
                else
                    playlistListviewInstance.getChildAt(i).setBackgroundColor(playlistListviewInstance.getSolidColor());

            }
        }

       /* private void fadeWidgets(){

            details.setVisibility(View.VISIBLE);
            AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
            animation1.setDuration(500);
            txtDetailsPlayer.startAnimation(animation1);
            txtTitlePlayer.startAnimation(animation1);
        }
        fadeWidgets();


        fillTextNotesForUser();
        }*/

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
          /*  Toast.makeText(getActivity(), "onSaveInstanceState",
                    Toast.LENGTH_LONG).show();
*/
            outState.putInt("curChoice", mCurCheckPosition);
        }


        // Helper function to show the details of a selected item, either by
        // displaying a fragment in-place in the current UI, or starting a whole
        // new activity in which it is displayed.

        void showDetails(int index) {

            //myApplication.setCurrentMediaPosition(10);


            mCurCheckPosition = index;

            // The basic design is mutli-pane (landscape on the phone) allows us
            // to display both fragments (titles and details) with in the same
            // activity; that is FragmentLayout -- one activity with two
            // fragments.
            // Else, it's single-pane (portrait on the phone) and we fire
            // another activity to render the details fragment - two activities
            // each with its own fragment .
            //

            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            // We keep highlighted the current selection

            getListView().setItemChecked(index, true);

            if (mDualPane) {


               /* //myApplication.setIndexSomethingIsPlaying(index);

                // Check what fragment is currently shown, replace if needed.
                DetailsFragment details = (DetailsFragment) getFragmentManager()
                        .findFragmentById(R.id.details);
                if (details == null || details.getShownIndex() != index) {
                    // Make new fragment to show this selection.

                    details = DetailsFragment.newInstance(index);

                    Toast.makeText(getActivity(),
                            "showDetails dual-pane: create and relplace fragment",
                            Toast.LENGTH_LONG).show();

                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
                    FragmentTransaction ft = getFragmentManager()
                            .beginTransaction();
                    ft.replace(R.id.details, details);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }*/

            } else {





               // sk.loadAudioListForPlayerService();

              /*  myApplication.setIndexSomethingIsPlaying(index);
                Intent intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra(Constants.EXTRA_PLAY_NEW_SESSION, true);
                getActivity().startService(intent);*/

                /*Intent intent = new Intent(getActivity(), ActivityPlayerPortrait.class);
                startActivity(intent);*/
            }
            myApplication.setIndexSomethingIsPlaying(index);
            Intent intent = new Intent(getActivity(), PlayerService.class);
            intent.putExtra(Constants.EXTRA_PLAY_NEW_SESSION, true);
            getActivity().startService(intent);
        }

    }

    private void killTheActivity() {

        finish();
    }
    // This is the secondary fragment, displaying the details of a particular
    // item.

    public static class DetailsFragment extends Fragment {

        // Create a new instance of DetailsFragment, initialized to show the
        // text at 'index'.

        public static DetailsFragment newInstance(int index) {
            DetailsFragment f = new DetailsFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
        }

        public int getShownIndex() {
            return getArguments().getInt("index", 0);
        }

        // The system calls this when it's time for the fragment to draw its
        // user interface for the first time. To draw a UI for your fragment,
        // you must return a View from this method that is the root of your
        // fragment's layout. You can return null if the fragment does not
        // provide a UI.

        // We create the UI with a scrollview and text and return a reference to
        // the scoller which is then drawn to the screen

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

           /* Toast.makeText(getActivity(), "DetailsFragment:onCreateView",
                    Toast.LENGTH_LONG).show();*/
            //
            // if (container == null) {
            // // We have different layouts, and in one of them this
            // // fragment's containing frame doesn't exist. The fragment
            // // may still be created from its saved state, but there is
            // // no reason to try to create its view hierarchy because it
            // // won't be displayed. Note this is not needed -- we could
            // // just run the code below, where we would create and return
            // // the view hierarchy; it would just never be used.
            // return null;
            // }

            // If non-null, this is the parent view that the fragment's UI
            // should be attached to. The fragment should not add the view
            // itself, but this can be used to generate the LayoutParams of
            // the view.
            //

            // programmatically create a scrollview and texview for the text in
            // the container/fragment layout. Set up the properties and add the
            // view.

            /*ScrollView scroller = new ScrollView(getActivity());
            TextView text = new TextView(getActivity());
            *//*int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, getActivity()
                            .getResources().getDisplayMetrics());
            text.setPadding(padding, padding, padding, padding);*//*

            scroller.addView(text);
            text.setText(Shakespeare.DIALOGUE[getShownIndex()]);
            return scroller;*/
            return null;

        }
    }

}