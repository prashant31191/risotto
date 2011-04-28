package com.risotto.service;

import java.util.Enumeration;
import java.util.Vector;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.risotto.controller.StatusBarNotification;
import com.risotto.controller.StatusBarNotificationManager;
import com.risotto.model.Drug;
import com.risotto.model.Patient;
import com.risotto.model.Prescription;
import com.risotto.storage.StorageProvider;

public class MainService extends Service {
	
	// A log tag used for debugging.
	public static final String LOG_TAG = "RISOTTO_SERVICE";
	
	// The intent used to start this service.
	public static final String ACTION_START_SERVICE = "com.risotto.service.START_SERVICE";
	// The intent used to schedule an alarm.
	public static final String ACTION_ALARM_TRIGGER = "com.risotto.service.ALARM_TRIGGER";
	// The intent used to schedule an alarm.
	public static final String ACTION_ALARM_SCHEDULE = "com.risotto.service.ALARM_SCHEDULE";
	
	private static boolean alarmScheduled = false;
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(LOG_TAG, "onCreate has been called...");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "onDestroy has been called...");
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		Log.d(LOG_TAG, "onLowMemory has been called...");
		super.onLowMemory();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// If this is NOT the BOOT_COMPLETE or USER_PRESENT action...
		if ( !(intent.getAction().equals(ACTION_START_SERVICE)) ) {
			// ...send the intent to the handler.
			handleCommand(intent);
		}	
		return Service.START_STICKY;
	}
	
	private void scheduleNewPrescription() {
		Log.d(LOG_TAG, "Scheduling a new prescription in the schedule table...");
	}
	
	private boolean isScheduleChanged() {
		Log.d(LOG_TAG, "The schedule has changed for this prescription.");
		return false;
	}
	
	private void updateScheduledPrescription() {
		Log.d(LOG_TAG, "Updating the schedule information for the prescription...");
	}
	
	private void schedulePrescriptions() {
		/* 1. Get a cursor to all of the prescriptions that have been 'scheduled' */
		
		// Create a where clause to only get the prescriptions that are scheduled
		String prescriptionWhereClause = StorageProvider.PrescriptionColumns.PRESCRIPTION_SCHEDULED + "=" + "'" + Prescription.SCHEDULED + "'";
		
		// We only care about the prescription id fields for this query.
		String[] prescriptionProjection = { StorageProvider.PrescriptionColumns._ID };
		
		Log.d(LOG_TAG, "Getting a cursor on the scheduled prescriptions...");
		
		// Get all of the prescriptions that are scheduled
		Cursor prescriptionCursor = this.getApplicationContext().getContentResolver().query(StorageProvider.PrescriptionColumns.CONTENT_URI, prescriptionProjection, prescriptionWhereClause, null, null);
		
		if ( prescriptionCursor == null || !prescriptionCursor.moveToFirst()) {
			// There are no prescriptions to schedule, we are done here.
			Log.i(LOG_TAG, "No prescriptions to schedule!");
		} else {
			
			/* 2. Run the list of prescription ids against the foreign key prescription ids that have been placed in the schedule table */
			
			Log.d(LOG_TAG, "Getting ready to look for prescription ids in the schdeule table...");
			
			do {
				// Get the prescription id
				int prescriptionId = prescriptionCursor.getInt(prescriptionCursor.getColumnIndex(StorageProvider.PrescriptionColumns._ID));
				
				Log.d(LOG_TAG,"Looking for prescription ID: " + prescriptionId );
				
				// The where clause for the schedule query
				String scheduleWhereClause = StorageProvider.ScheduleColumns.SCHEDULES_PRESCRIPTION + "=" + "'" + prescriptionId + "'";
				
				Cursor specificScheduleCursor = this.getApplicationContext().getContentResolver().query(StorageProvider.ScheduleColumns.CONTENT_URI, null, scheduleWhereClause, null, null);
				
				if ( specificScheduleCursor != null && specificScheduleCursor.moveToFirst()) {
					// There are entries for this prescription in the schedule table.
					Log.d(LOG_TAG, "There are entries in the schedule table for prescription ID: " + prescriptionId );
					
					/* 3. Check to see if this prescription has changed from it's original scheduling. */
					if ( this.isScheduleChanged() ) {
						/* 4. Update any changed prescriptions */
						Log.d(LOG_TAG, "The schedule for prescription ID: " + prescriptionId + " has changed!");
						this.updateScheduledPrescription();
					} else {
						// The schedule for this prescription has not changed.
						Log.d(LOG_TAG, "The schedule for prescription ID: " + prescriptionId  + " has not changed.");
					}
					
				} else {
					/* 5. Schedule any 'new' prescriptions */
					// There are no entries for this prescription in the schedule table.
					Log.d(LOG_TAG, "There are no entries for prescription ID: " + prescriptionId + " in the schedule table.");
					// Create a new schedule for this prescription.
					this.scheduleNewPrescription();
				}
				
				// Close the specific query for the schedules.
				specificScheduleCursor.close();
				
			} while (prescriptionCursor.moveToNext());
			
		}
		
		// Close the query for all the prescription ids
		prescriptionCursor.close();

		/* 6. Listen for any changes that may occur to the prescription URI table */
		this.getApplicationContext().getContentResolver().registerContentObserver(StorageProvider.DrugColumns.CONTENT_URI, true, new PrescriptionContentObserver(new Handler()));
	}
	
	
	private void scheduleAlarm() {
		
		// If we have not scheduled the alarm yet...
		if ( ! this.isAlarmScheduled() ) {
			// Get an instance of the AlarmManager
			AlarmManager am = (AlarmManager)this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			
			// Create an intent that will launch when the trigger is done.
			Intent newIntent = new Intent(ACTION_ALARM_TRIGGER);
			
			newIntent.putExtra("TOP_TEXT", "Tylenol");
			newIntent.putExtra("TITLE_TEXT", "Time to take 3 tylenol.");
			newIntent.putExtra("BODY_TEXT", "Drink a full glass of water.");
			
			newIntent.setClass(this, MainService.class);
			
			// Create a pending intent which will be sent when the alarm is done.
			PendingIntent pi = PendingIntent.getService(this, 0, newIntent, PendingIntent.FLAG_ONE_SHOT);
			
			// Have the AlarmManager schedule the alarm.
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pi );
			
			// Set the alarm flag to true, as we have scheduled it
			alarmScheduled = true;
			
		} else {
			Log.d(LOG_TAG, "No need to schedule an alarm, already done once...");
		}
		
	}
	
	private void notifyAlarmDone(Intent intent) {
		// Get an instance of the SBNM
		StatusBarNotificationManager sbnm = new StatusBarNotificationManager(this.getApplicationContext());
		
		// Create a new prescription.
		Prescription prep = new Prescription(new Patient("Nick", "McMillan", Patient.GENDER.MALE),new Drug("Ibuprofen", Drug.TYPE.PRESCRIPTION, 400, "mg"),Prescription.DOSE_TYPE_EVERY_DAY,10,10);
		
		// Create the new status bar notification.
		StatusBarNotification not = new StatusBarNotification(this, prep, intent.getStringExtra("TOP_TEXT"), intent.getStringExtra("TITLE_TEXT"), intent.getStringExtra("BODY_TEXT"));
		
		// Get the index of this notification
		int x = sbnm.add(not);
		try {
			// Send the message to the NM to display
			sbnm.sendMessage(x);
		}
		catch(Exception e) {
			Log.d(LOG_TAG, "THERE WAS AN EXCEPTION!!");
		}
		
		Log.d(LOG_TAG, "ALARM UP!!!!!");
	}
	
	private boolean isAlarmScheduled() {
		return alarmScheduled;
	}
	
	/**
	 * Handles the given intent by performing a class specific action.
	 * @param intent the intent to handle
	 */
	private void handleCommand(Intent intent) {
		Log.d(LOG_TAG, "handleCommand has been called...");
		Log.d(LOG_TAG, "Intent Info: " + intent.getAction());
		
		// Get the string action from the intent object.
		String intentAction = intent.getAction();
		
		if ( intentAction.equals(ACTION_ALARM_TRIGGER) ) {
			// An alarm has completed!
			notifyAlarmDone(intent);
		} else if ( intentAction.equals(ACTION_ALARM_SCHEDULE) ) {
			// Schedule the alarm...
			//this.scheduleAlarm();
			//this.scheduleTests();
			this.schedulePrescriptions();
		} else {
			// How did we get this action? What is this!?
			Log.d(LOG_TAG, "handleCommand() - Unknown intent: " + intentAction);
		}	
		
	}
	
	private void scheduleTests() {
		// Create a where clause to only get the prescriptions that are scheduled
		String whereClause = StorageProvider.PrescriptionColumns.PRESCRIPTION_SCHEDULED + "=" + "'" + Prescription.SCHEDULED + "'";
		
		Log.d(LOG_TAG, "Getting a cursor on the scheduled prescriptions...");
		
		// Get all of the prescriptions that are scheduled
		Cursor prescCursor = this.getApplicationContext().getContentResolver().query(StorageProvider.PrescriptionColumns.CONTENT_URI, null, whereClause, null, null);
		
		Log.d(LOG_TAG, "Got a cursor!");
		
		// Let's schedule some things...
		
		// Are there any prescription objects?
		if (prescCursor.moveToFirst()) {
			do {
				Log.d(LOG_TAG, "DOING!");
				
				// Get the list of valid day columns for this prescription
				Vector<String> validDayColumns = Prescription.getScheduledDays(prescCursor);
				
				Enumeration<String> daysEnum = validDayColumns.elements();
				
				while(daysEnum.hasMoreElements()) {
					// Get the valid column name
					String dayColumnName = daysEnum.nextElement();
					Log.d(LOG_TAG, "Getting the data at the column: " + dayColumnName);
					Log.d(LOG_TAG, "For Patient: " + prescCursor.getString(prescCursor.getColumnIndex(StorageProvider.PrescriptionColumns.PRESCRIPTION_PATIENT)));
					Log.d(LOG_TAG, "For Drug: " + prescCursor.getString(prescCursor.getColumnIndex(StorageProvider.PrescriptionColumns.PRESCRIPTION_DRUG)));

					// Schedule a timer based on the data in the day column
					//String dataValue = prescCursor.getString(prescCursor.getColumnIndex(dayColumnName));
					
					//Log.d(LOG_TAG, "Data @ " + dayColumnName + " : " + dataValue);
					
				}
				
				
			} while (prescCursor.moveToNext());
			
			// Close and release the cursor.
			prescCursor.close();
			
			// Register for any possible changes to this URI
			this.getApplicationContext().getContentResolver().registerContentObserver(StorageProvider.DrugColumns.CONTENT_URI, true, new PrescriptionContentObserver(new Handler()));
			
		}
		
	}
	
	
	
	class PrescriptionContentObserver extends ContentObserver {

		public PrescriptionContentObserver(Handler handler) {
			super(handler);
			
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.d(LOG_TAG, "ScheduleContentObserver onChange() called...");
			Log.d(LOG_TAG, "Updating schedules.");
			MainService.this.scheduleTests();
		}

	}
}
