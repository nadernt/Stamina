package com.fleecast.stamina.notetaking;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.RealmNoteHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;
import com.fleecast.stamina.utility.Prefs;

import java.io.File;
import java.util.Date;

public class ChatHeadRecordService extends Service {

	private static final int OFFSET_OF_MOVE = 50;
	private WindowManager windowManager;
	private ImageView chatHeadRecord;
	private GestureDetector gestureDetector;
	private WindowManager.LayoutParams params;
	private LinearLayout popupRecordDialogView;
	private Button btnPopCancelRecord;
	private Button btnPopStopRecord;
	private Button btnPopAddNote;
	private RecorderPhone recorder;
	private final static String TEMP_FILE = "temp";

	private Handler myHandler = new Handler();
	private Point szWindow = new Point();
	private Point p1;
	private Point p2;
	private String pathToWorkingDirectory;
	private boolean ignoreIntentsWeHaveError=false;
	private MyApplication myApplication;
	private int dbId;
	private LayoutInflater inflater;
	private RealmNoteHelper realmNoteHelper;
	private String TAG = "ChatHeadRecordService";
	private Intent intentOfCallForEdit;
	//	private boolean isEditActivityNotRun;
	private boolean recordHasCanceled;
	private boolean recordStoppedByUser;
	private Date dateForStopByUser;
	private String callNumberForStopByUser;
	private int typeOfCallForStopByUser;

	@Override
	public void onCreate() {
		super.onCreate();

		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

		myApplication = (MyApplication) getApplicationContext();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			windowManager.getDefaultDisplay().getSize(szWindow);
		} else {

			int w = windowManager.getDefaultDisplay().getWidth();
			int h = windowManager.getDefaultDisplay().getHeight();
			szWindow.set(w, h);
		}

		pathToWorkingDirectory = ExternalStorageManager.prepareWorkingDirectory(this);


		if (pathToWorkingDirectory.length() == 0) {

			ignoreIntentsWeHaveError=true;

			showErrorsToUser(inflater, "<h5>Not enough space:</h5>\n<p><font color=\"gray\">Not enough storage space for recording!" +
					"Empty the storage and try again.</font></p>");

		} else if (myApplication.isRecordUnderGoing()>Constants.CONST_RECORDER_SERVICE_IS_FREE) {

			ignoreIntentsWeHaveError=true;

			showErrorsToUser(inflater, "<h5>Note:</h5>\n<p><font color=\"gray\">Another record is under progress by application." +
					"The app cannot record your call now.</font></p>");
			stopSelf();

		} else {

			realmNoteHelper = new RealmNoteHelper(getApplicationContext());

			recordHasCanceled = false;
			ignoreIntentsWeHaveError=false;
			//isEditActivityNotRun=true;
			recordStoppedByUser =false;
			dateForStopByUser = new Date();
			// Creating unique id for db as primary key
			dbId = (int) (System.currentTimeMillis() / 1000);

			gestureDetector = new GestureDetector(this, new SingleTapConfirm());

			chatHeadRecord = new ImageView(this);
			chatHeadRecord.setImageResource(R.drawable.phone);
			chatHeadRecord.setBackgroundResource(R.drawable.launchpad_icons_bg);

			params = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);

			params.gravity = Gravity.TOP | Gravity.LEFT;

			params.x = Prefs.getInt(Constants.PHONE_RECORDER_CHATHEAD_X,params.x);

			params.y = Prefs.getInt(Constants.PHONE_RECORDER_CHATHEAD_Y,params.y);

			windowManager.addView(chatHeadRecord, params);

			//this code is for dragging the chat head
			chatHeadRecord.setOnTouchListener(new View.OnTouchListener() {
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// single tap
					if (gestureDetector.onTouchEvent(event)) {

						if (popupRecordDialogView.getVisibility() == View.GONE) {
							showPopUp();
						} else
							disableHandler();

						return true;
					} else {
						//Chathead control logic.
						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								p1 = new Point((int) event.getX(), (int) event.getY());
								initialX = params.x;
								initialY = params.y;
								initialTouchX = event.getRawX();
								initialTouchY = event.getRawY();
								return true;
							case MotionEvent.ACTION_UP:
								Prefs.putInt(Constants.PHONE_RECORDER_CHATHEAD_X,params.x);
								Prefs.putInt(Constants.PHONE_RECORDER_CHATHEAD_Y,params.y);
								return true;
							case MotionEvent.ACTION_MOVE:

								p2 = new Point((int) event.getX(), (int) event.getY());

								if (Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2)) > OFFSET_OF_MOVE)
									disableHandler();

								if (initialX + (int) (event.getRawX() - initialTouchX) <= szWindow.x)
									params.x = initialX + (int) (event.getRawX() - initialTouchX);
								else
									params.x = szWindow.x;

								if (initialY + (int) (event.getRawY() - initialTouchY) <= szWindow.y)
									params.y = initialY + (int) (event.getRawY() - initialTouchY);
								else
									params.y = szWindow.y;

								windowManager.updateViewLayout(chatHeadRecord, params);


								return true;
						}
					}

					return false;
				}
			});


			popupRecordDialogView = (LinearLayout) inflater.inflate(R.layout.popup_record_dialog_view, null);
			btnPopCancelRecord = (Button) popupRecordDialogView.findViewById(R.id.btnCancelRecord);
			btnPopStopRecord = (Button) popupRecordDialogView.findViewById(R.id.btnPopStopRecord);
			btnPopAddNote = (Button) popupRecordDialogView.findViewById(R.id.btnPopAddNote);

			btnPopCancelRecord.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					disableHandler();
					recordHasCanceled=true;
					chatHeadRecord.setVisibility(View.GONE);
				}
			});

			btnPopStopRecord.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					disableHandler();
					recordStoppedByUser=true;
					chatHeadRecord.setVisibility(View.GONE);

					recordAudio(false);

					try {

						Date end = new Date();

						/**
						 * Delete the entry from database. Before we check note still exist because sometimes
						 * user is crazy and just deleted the note from db before stop the call.
						 */

						if(realmNoteHelper.isExist(dbId)) {
							//Update the current finished call entry.
							realmNoteHelper.updateNotePhoneCallInfo(dbId, dateForStopByUser, end, typeOfCallForStopByUser, callNumberForStopByUser);
						}
						else{
							// Crazy user deleted message while in the phone call!
							String title = callNumberForStopByUser;
							String description = "";
							realmNoteHelper.addNote(dbId, title, description, true, null, null, dateForStopByUser, end, typeOfCallForStopByUser, callNumberForStopByUser, 0, Constants.CONST_NOTETYPE_PHONECALL);
						}

						File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

						File phoneCallsFolder = new File(pathToWorkingDirectory + File.separator +  Constants.CONST_PHONE_CALLS_DIRECTORY_NAME);
						phoneCallsFolder.mkdir();

						file.renameTo(new File(phoneCallsFolder.getPath() + File.separator + String.valueOf(dbId) + Constants.RECORDER_AUDIO_FORMAT_AAC));

						Log.e(TAG, "End Record");


						Toast.makeText(getApplicationContext(), "Phone call record stopped!", Toast.LENGTH_LONG);

					}catch (Exception e){

					}

				}
			});

			btnPopAddNote.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					disableHandler();

					if(!realmNoteHelper.isExist(dbId)) {
						// Crazy user deleted message while in the phone call!
						String title = callNumberForStopByUser;
						String description = "";
						realmNoteHelper.addNote(dbId, title, description, true, null, null, dateForStopByUser, null, typeOfCallForStopByUser, callNumberForStopByUser, 0, Constants.CONST_NOTETYPE_PHONECALL);
					}

					Intent intent = new Intent(getApplicationContext(), ActivityEditPhoneRecordNote.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(Constants.EXTRA_EDIT_PHONE_RECORD_NOTE, dbId);
					//intent.putExtra(Constants.EXTRA_EDIT_PHONE_RECORD_NOTE_PHONE_NUMBER, callNumberForStopByUser);

					startActivity(intent);

				}
			});

			WindowManager.LayoutParams paramsPopChatHeadRecordOptions = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSLUCENT);
			paramsPopChatHeadRecordOptions.gravity = Gravity.TOP | Gravity.LEFT;

			popupRecordDialogView.setVisibility(View.GONE);

			windowManager.addView(popupRecordDialogView, paramsPopChatHeadRecordOptions);
			recorder = new RecorderPhone(getApplicationContext(), pathToWorkingDirectory, TEMP_FILE);

		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("DBG", "New command to record phone service.");

		if(!ignoreIntentsWeHaveError) {
			intentHandler(intent);
		}
		return  START_NOT_STICKY; // Means we started the service, but don't want it to
		// restart in case it's killed.
	}


	private void intentHandler(Intent intent) {

		// we are after crash and we should stop the service
		if(intent==null) {
			Log.e("DBG", "Error null intent stop service.");
			stopSelf();
			return;
		}

		if(intent.hasExtra(Constants.CHAT_HEAD_RECORD_INTENTS_RECORD)) {
			if (intent.getBooleanExtra(Constants.CHAT_HEAD_RECORD_INTENTS_RECORD, false)) {

				callNumberForStopByUser = intent.getStringExtra(Constants.CHAT_HEAD_RECORD_INTENTS_NUMBER);
				typeOfCallForStopByUser = intent.getIntExtra(Constants.CHAT_HEAD_RECORD_INTENTS_INGOING_OUTGOING, -1);

				String title = callNumberForStopByUser;
				String description = "";
				Date end = new Date();

				realmNoteHelper.addNote(dbId, title, description, true, null, null, dateForStopByUser, null, typeOfCallForStopByUser, callNumberForStopByUser, 0, Constants.CONST_NOTETYPE_PHONECALL);

				//start record
				recordAudio(true);

			}
		}
		if(intent.hasExtra(Constants.CHAT_HEAD_RECORD_INTENTS_STOP)) {
			if (intent.getBooleanExtra(Constants.CHAT_HEAD_RECORD_INTENTS_STOP, false)) {

				if (recordStoppedByUser) {
					// We will wait up to conversation be finished then we stop service.
					stopSelf();
				} else if (recordHasCanceled) {
					try {

						/**
						 * Delete the entry from database. Before we check note still exist because sometimes
						 * user is crazy and just deleted the note from db before stop the call.
						 */

						if(realmNoteHelper.isExist(dbId)) {
							realmNoteHelper.deleteSingleNote(dbId);
						}

						recordAudio(false);

						File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

						if(file.exists())
							file.delete();

					} catch (Exception e) {

					} finally {
						stopSelf();
					}
				} else {

					//stop record
					recordAudio(false);

					Date start = new Date(intent.getLongExtra(Constants.CHAT_HEAD_RECORD_INTENTS_START_TIME, 0));
					Date end = new Date(intent.getLongExtra(Constants.CHAT_HEAD_RECORD_INTENTS_END_TIME, 0));
					String number = intent.getStringExtra(Constants.CHAT_HEAD_RECORD_INTENTS_NUMBER);

					int typeOfCall = intent.getIntExtra(Constants.CHAT_HEAD_RECORD_INTENTS_INGOING_OUTGOING, -1);

					//update the call info.
					realmNoteHelper.updateNotePhoneCallInfo(dbId, start, end, typeOfCall, number);

					File file = new File(pathToWorkingDirectory + File.separator + TEMP_FILE);

					File phoneCallsFolder = new File(pathToWorkingDirectory + File.separator +  Constants.CONST_PHONE_CALLS_DIRECTORY_NAME);

					phoneCallsFolder.mkdir();

					Log.e(TAG, phoneCallsFolder.getPath() + "  ddddd");

					file.renameTo(new File(phoneCallsFolder.getPath() + File.separator + String.valueOf(dbId) + Constants.RECORDER_AUDIO_FORMAT_AAC));

					Log.e(TAG, "End Record");


					Toast.makeText(getApplicationContext(), "New phone call record saved", Toast.LENGTH_LONG);

					stopSelf();
				}
			}
		}

	}

	private void recordAudio(boolean start_stop) {

		if (start_stop) {

			if (myApplication.isRecordUnderGoing()==Constants.CONST_RECORDER_SERVICE_IS_FREE)
			{
				myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_WORKS_FOR_PHONE);


				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

					audioManager.setSpeakerphoneOn(true);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

					recorder.recordMedia(true, MediaRecorder.AudioSource.MIC);
					Log.e("GGGGGGGg", "Honglaaaaaaaaaaaaaaaaa");

				}else{
					recorder.recordMedia(true, MediaRecorder.AudioSource.VOICE_CALL);
				}


			}
			else{
				showErrorsToUser(inflater, "<h5>Note:</h5>\n<p><font color=\"gray\">Another record is under progress by application." +
						"The app cannot record your call now.</font></p>");
				stopSelf();
			}

		} else {
			myApplication.setIsRecordUnderGoing(Constants.CONST_RECORDER_SERVICE_IS_FREE);
			// this is stop command so choosing record source is not matter.
			recorder.recordMedia(false, MediaRecorder.AudioSource.VOICE_CALL);
		}


	}

	private void showErrorsToUser(LayoutInflater parentInflater, String errorMessage)
	{

		WindowManager.LayoutParams paramsErrorShowWindow = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.OPAQUE);

		paramsErrorShowWindow.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

		final LinearLayout layoutSnackMessageToUser = (LinearLayout) parentInflater.inflate(R.layout.snack_user_messages, null);
		TextView txtSnackMessageToUser = (TextView) layoutSnackMessageToUser.findViewById(R.id.txtSnackMessageToUser);
		TextView txtViewCloseErrorWin = (TextView) layoutSnackMessageToUser.findViewById(R.id.txtViewCloseErrorWin);

		windowManager.addView(layoutSnackMessageToUser, paramsErrorShowWindow);

		txtSnackMessageToUser.setText(Html.fromHtml(errorMessage));

		txtViewCloseErrorWin.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				windowManager.removeView(layoutSnackMessageToUser);
				stopSelf();
				return false;
			}
		}) ;

	}

	private void disableHandler(){
		myHandler.removeCallbacks(myRunnable);
		if(popupRecordDialogView != null){
			popupRecordDialogView.setVisibility(View.GONE);
		}

	}

	Runnable myRunnable = new Runnable() {

		@Override
		public void run() {
			if(popupRecordDialogView != null){
				popupRecordDialogView.setVisibility(View.GONE);
			}
		}
	};

	private void showPopUp(){

		if(popupRecordDialogView != null && chatHeadRecord != null ){


			myHandler.removeCallbacks(myRunnable);

			WindowManager.LayoutParams param_chathead = (WindowManager.LayoutParams) chatHeadRecord.getLayoutParams();
			WindowManager.LayoutParams paramPopUp = (WindowManager.LayoutParams) popupRecordDialogView.getLayoutParams();

			if((param_chathead.x + chatHeadRecord.getWidth() + popupRecordDialogView.getWidth()) > szWindow.x)
			{
				if((param_chathead.x + chatHeadRecord.getWidth()) > szWindow.x)
					paramPopUp.x = szWindow.x - popupRecordDialogView.getWidth() - chatHeadRecord.getWidth();
				else
					paramPopUp.x = param_chathead.x - popupRecordDialogView.getWidth();

			}
			else
			{
				if(param_chathead.x<0)
					paramPopUp.x = chatHeadRecord.getWidth();
				else
					paramPopUp.x = param_chathead.x + chatHeadRecord.getWidth();

			}

			if((param_chathead.y + chatHeadRecord.getHeight() + popupRecordDialogView.getHeight()) > szWindow.y)
			{
				if((param_chathead.y + chatHeadRecord.getHeight()) > szWindow.y)
					paramPopUp.y = szWindow.y - popupRecordDialogView.getHeight() - chatHeadRecord.getHeight() -getStatusBarHeight();
				else
					paramPopUp.y = param_chathead.y - popupRecordDialogView.getHeight();

			}
			else
			{
				if(param_chathead.y < 0)
					paramPopUp.y = chatHeadRecord.getHeight();
				else
					paramPopUp.y = param_chathead.y + chatHeadRecord.getHeight();
			}

			popupRecordDialogView.setVisibility(View.VISIBLE);
			windowManager.updateViewLayout(popupRecordDialogView, paramPopUp);
			myHandler.postDelayed(myRunnable, 4000);
		}

	}

/*	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}*/

	/*	private void resetPosition(int x_cord_now) {
            int w = chatHeadRecord.getWidth();

            if (x_cord_now == 0 || x_cord_now == szWindow.x - w) {

            } else if (x_cord_now + w / 2 <= szWindow.x / 2) {
                isLeft = true;

            } else if (x_cord_now + w / 2 > szWindow.x / 2) {
                isLeft = false;

            }
        }*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.e("DBGGGGGGGGGG","onConfigurationChanged");

		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			windowManager.getDefaultDisplay().getSize(szWindow);
		} else {
			int w = windowManager.getDefaultDisplay().getWidth();
			int h = windowManager.getDefaultDisplay().getHeight();
			szWindow.set(w, h);
		}
		if(popupRecordDialogView != null){
			popupRecordDialogView.setVisibility(View.GONE);
		}

	/*	WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatHeadRecord.getLayoutParams();

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.d(Utility.LogTag, "ChatHeadService.onConfigurationChanged -> landscap");

				if((layoutParams.y + chatHeadRecord.getHeight())  > szWindow.y){
				layoutParams.y = szWindow.y- chatHeadRecord.getHeight();
				windowManager.updateViewLayout(chatHeadRecord, layoutParams);
			}

			if(layoutParams.x != 0 && layoutParams.x < szWindow.x){
				resetPosition(szWindow.x);
			}

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			Log.d(Utility.LogTag, "ChatHeadService.onConfigurationChanged -> portrait");


			if(layoutParams.x > szWindow.x){
				resetPosition(szWindow.x);
			}

		}*/

	}
	private int getStatusBarHeight() {
		int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
		// Log.e("DBG", "statusBarHeight " + statusBarHeight);

		return statusBarHeight;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("ChatHeadRecordService", "destroyed");

		if (chatHeadRecord != null) {
			disableHandler();
			windowManager.removeView(chatHeadRecord);
			windowManager.removeView(popupRecordDialogView);

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			return true;
		}
	}
}