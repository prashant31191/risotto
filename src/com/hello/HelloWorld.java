package com.hello;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.risotto.controller.StatusBarNotification;
import com.risotto.controller.StatusBarNotificationManager;
import com.risotto.model.Drug;
import com.risotto.model.Patient;
import com.risotto.model.Prescription;
import com.risotto.storage.StorageProvider;

public class HelloWorld extends Activity {
	
	/*
	 * When the device is rotated, Android runs through the full lifecycle of your application.  It calls pause, stop, destroy
	 * and then create, start, resume every time the phone's orientation is changed.
	 */
	StatusBarNotification vicodin = null;
	StatusBarNotification advil = null;
    int vicID = -1;
    int adID = -1;
    Patient patient = new Patient();
    Drug drug = new Drug();
    Prescription prep = new Prescription(patient, drug, Prescription.DOSE_TYPE.EVERY_DAY, 2, 20);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        StatusBarNotificationManager notMgr = new StatusBarNotificationManager(this.getApplicationContext());
        
        vicodin = new StatusBarNotification(this.getApplicationContext(), prep, "Vicodin", "Remember to take 2 pills of vicodin", "Be sure to eat something first.");
        advil = new StatusBarNotification(this.getApplicationContext(), prep, "Advil", "Remember to take 2 pills of vicodin", "Be sure to eat something first.");
        
        /*
         * TO DO (what happens if we want to add vibrate to the notification, but we call the setVibrate method after adding
         * it to the manager?
         *  - still works because it's only a reference to the object, so when the object gets updated, there's no updating needed
         */
        
        vicID = notMgr.add(vicodin);
        adID = notMgr.add(advil);
        
        vicodin.setVibrate();
        
        System.out.println("vicID: " + vicID);
        System.out.println("adID: " + adID);
        
        notMgr.printAllNotifications();
        
        try {
			notMgr.sendMessage(vicID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("vicID = " + vicID);
        
	    //this.startActivity(new Intent(this, DialogActivity.class));
	    //this.startActivity(new Intent(this, HelloWorld.class));
   
        DialogActivity.showDialog(this);
        
        //this.startActivity(new Intent(this, AlarmView.class));
        
        StorageProvider sp = new StorageProvider(this.getApplicationContext());
        SQLiteDatabase db = sp.getDatabase();
        db.close();

    }

	/*@Override
	protected void onPause() {
		super.onPause();
		StatusBarNotification pause = new StatusBarNotification(this.getApplicationContext());
        pause.setNotificationContent("pause", "pause", "pause");
        pause.sendMessage();
		
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		StatusBarNotification restart = new StatusBarNotification(this.getApplicationContext());
        restart.setNotificationContent("restart", "restart", "restart");
        restart.sendMessage();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatusBarNotification resume = new StatusBarNotification(this.getApplicationContext());
        resume.setNotificationContent("resume", "resume", "resume");
        resume.sendMessage();
	}

	@Override
	protected void onStart() {
		super.onStart();
		StatusBarNotification start = new StatusBarNotification(this.getApplicationContext());
        start.setNotificationContent("start", "start", "start");
        start.sendMessage();
	}

	@Override
	protected void onStop() {
		super.onStop();
		StatusBarNotification stop = new StatusBarNotification(this.getApplicationContext());
        stop.setNotificationContent("stop", "stop", "stop");
        stop.sendMessage();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		StatusBarNotification destroy = new StatusBarNotification(this.getApplicationContext());
        destroy.setNotificationContent("destroy", "destroy", "destroy");
        destroy.sendMessage();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}*/
   
}