package com.risotto.view.patient;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import com.risotto.R;
import com.risotto.model.Patient;
import com.risotto.storage.StorageProvider;

public class PatientView extends ListActivity implements SimpleCursorAdapter.ViewBinder {

	public static final String LOG_TAG = "com.risotto.view.drug.DrugView";
	
	private static String[] PROJECTION = {
		StorageProvider.PatientColumns._ID,
		StorageProvider.PatientColumns.PATIENT_FIRST_NAME,
		StorageProvider.PatientColumns.PATIENT_LAST_NAME,
	};
		
	private static String[] VIEW_DB_COLUMN_MAPPING = {
		StorageProvider.PatientColumns.PATIENT_FIRST_NAME,
		StorageProvider.PatientColumns.PATIENT_LAST_NAME
	};
	private static int[] VIEW_ID_MAPPING = {
		R.id.patient_list_view_first_name,
		R.id.patient_list_view_last_name
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  
		  //sets default state of what happens when keys are pressed
		  //that are not handled by the application;
		  //multiple options here, see the javadoc
		  setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		  
		  //This method will display all of the drugs in the database
		  //	- will we expect something to be passed in through the intent?
		  Intent intent = getIntent();
		  
		  if(null == intent.getData()) {
			  intent.setData(StorageProvider.PatientColumns.CONTENT_URI);
		  }
		
		  getListView().setOnCreateContextMenuListener(this);
		  
		  Cursor cursor = this.getContentResolver().query(getIntent().getData(), PROJECTION, null, null, null);
		  
		  if(null != cursor) {
			  startManagingCursor(cursor);
			  
			  Log.d(LOG_TAG,"count: " + cursor.getCount());
		  Log.d(LOG_TAG,"cursor column count: " + cursor.getColumnCount());
		  
		  //note: the cursor originally points to a null row, needs to move before trying to print data
		  //cursor.moveToFirst();
		  
		  //Log.d(LOG_TAG,"cursor.toString(0)" + cursor.getString(1));
		  
		  //TextView drugListItem = (TextView)this.findViewById(R.layout.drug_list_item);
		  
		  SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				  this,						//context
			  R.layout.patient_list_item,	//layout
			  cursor,					//cursor
			  VIEW_DB_COLUMN_MAPPING, 
			  VIEW_ID_MAPPING); //mapping
		  
		  //adapter.setViewBinder(this);
			  setListAdapter(adapter);
		  }
		
		  registerForContextMenu(getListView());
	}
	
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		Patient patient = Patient.fromCursor(cursor);
		return true;
	}
	
}
