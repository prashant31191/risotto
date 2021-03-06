package com.risotto.storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.risotto.model.Drug;
import com.risotto.model.Patient;
import com.risotto.model.Prescription;

public class StorageProvider extends ContentProvider {
	
    protected static final String LOG_TAG = "StorageProvider";
    
    public static final String AUTHORITY = StorageProvider.class.getPackage().getName() + ".provider";
    
    private static final String DATABASE_NAME = "risotto.db";
    private static final int DATABASE_VERSION = 3;
    public static final String DRUGS_TABLE_NAME = "drugs";
    public static final String PATIENTS_TABLE_NAME = "patients";
    public static final String PRESCRIPTIONS_TABLE_NAME = "prescriptions";
    public static final String SCHEDULES_TABLE_NAME = "schedules";
    public static final String NOTIFICATION_EVENTS_TABLE_NAME = "notification_events";
    public static final String SYSTEM_EVENTS_TABLE_NAME = "system_events";
    public static final String SYNC_EVENTS_TABLE_NAME = "sync_events";
    
    // URI Matching ID's
    private static final int URI_TYPE_DRUGS = 0;
    private static final int URI_TYPE_DRUG_ID = 1;
    private static final int URI_TYPE_PATIENTS = 2;
    private static final int URI_TYPE_PATIENT_ID = 3;
    private static final int URI_TYPE_PRESCRIPTIONS = 4;
    private static final int URI_TYPE_PRESCRIPTION_ID = 5;
    private static final int URI_TYPE_SCHEDULES = 6;
    private static final int URI_TYPE_SCHEDULE_ID  = 7;
    private static final int URI_TYPE_NOTIFICATION_EVENTS = 8;
    private static final int URI_TYPE_NOTIFICATION_EVENT_ID = 9;
    private static final int URI_TYPE_SYSTEM_EVENTS = 10;
    private static final int URI_TYPE_SYSTEM_EVENT_ID = 11;
    private static final int URI_TYPE_SYNC_EVENTS = 12;
    private static final int URI_TYPE_SYNC_EVENT_ID = 13;
    
	// Set up the URI matcher
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher.addURI(AUTHORITY, "drugs", URI_TYPE_DRUGS);
		sUriMatcher.addURI(AUTHORITY, "drugs/#", URI_TYPE_DRUG_ID);
		sUriMatcher.addURI(AUTHORITY, "patients", URI_TYPE_PATIENTS);
		sUriMatcher.addURI(AUTHORITY, "patients/#", URI_TYPE_PATIENT_ID);
		sUriMatcher.addURI(AUTHORITY, "prescriptions", URI_TYPE_PRESCRIPTIONS);
		sUriMatcher.addURI(AUTHORITY, "prescriptions/#", URI_TYPE_PRESCRIPTION_ID);
		sUriMatcher.addURI(AUTHORITY, "schedules", URI_TYPE_SCHEDULES);
		sUriMatcher.addURI(AUTHORITY, "schedules/#", URI_TYPE_SCHEDULE_ID);
		sUriMatcher.addURI(AUTHORITY, "notification_events", URI_TYPE_NOTIFICATION_EVENTS);
		sUriMatcher.addURI(AUTHORITY, "notification_events/#", URI_TYPE_NOTIFICATION_EVENT_ID);
		sUriMatcher.addURI(AUTHORITY, "system_events", URI_TYPE_SYSTEM_EVENTS);
		sUriMatcher.addURI(AUTHORITY, "system_events/#", URI_TYPE_SYSTEM_EVENT_ID);
		sUriMatcher.addURI(AUTHORITY, "sync_events", URI_TYPE_SYNC_EVENTS);
		sUriMatcher.addURI(AUTHORITY, "sync_events/#", URI_TYPE_SYNC_EVENT_ID);
	}
    
	private class StorageDatabaseHelper extends SQLiteOpenHelper {

		public StorageDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		 @Override
		  public void onOpen(SQLiteDatabase db)
		  {
		    super.onOpen(db);
		    if (!db.isReadOnly())
		    {
		      // Enable foreign key constraints
		      db.execSQL("PRAGMA foreign_keys=ON;");
		    }
		  }

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(LOG_TAG, "Creating the SQLite DB...");
			this.createDrugsTable(db);
			this.createPatientsTable(db);
			this.createPrescriptionsTable(db);
			this.createSchedulesTable(db);
			this.createNotificationEventsTable(db);
			this.createSystemEventsTable(db);
			this.createSyncEventsTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOG_TAG, "Dropping all tables from version " + oldVersion + " to migrate to version " + newVersion);
			db.execSQL("DROP TABLE IF EXISTS " + DRUGS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + PATIENTS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + PRESCRIPTIONS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + SCHEDULES_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_EVENTS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + SYSTEM_EVENTS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + SYNC_EVENTS_TABLE_NAME);
            onCreate(db);
		}
		
		private void createDrugsTable(SQLiteDatabase db) {		
			Log.d(LOG_TAG, "Creating the " + DRUGS_TABLE_NAME + " table...");
			db.execSQL("CREATE TABLE " + DRUGS_TABLE_NAME + " ("
					+ DrugColumns._ID + " INTEGER PRIMARY KEY,"
					+ DrugColumns.DRUG_BRAND_NAME + " TEXT NOT NULL,"
					+ DrugColumns.DRUG_TYPE + " TEXT,"
					+ DrugColumns.DRUG_STRENGTH + " INTEGER,"
					+ DrugColumns.DRUG_STRENGTH_LABEL + " TEXT,"
					+ DrugColumns.DRUG_COMPOUND_NAME + " TEXT,"
					+ DrugColumns.DRUG_MANUFACTURER + " TEXT,"
					+ DrugColumns.DRUG_INTERACTIONS + " BLOB,"
					+ DrugColumns.DRUG_NICK_NAME + " TEXT,"
					+ DrugColumns.DRUG_FORM + " TEXT,"
					+ DrugColumns.DRUG_COLOR + " INTEGER,"
					+ DrugColumns.DRUG_SHAPE + " TEXT,"
					+ DrugColumns.DRUG_SIZE + " TEXT"
					+ ");");	
		}
		
		private void createPatientsTable(SQLiteDatabase db) {
			Log.d(LOG_TAG, "Creating the " + PATIENTS_TABLE_NAME + " table...");
			db.execSQL("CREATE TABLE " + PATIENTS_TABLE_NAME + " ("
					+ PatientColumns._ID + " INTEGER PRIMARY KEY,"
					+ PatientColumns.PATIENT_FIRST_NAME + " TEXT NOT NULL,"
					+ PatientColumns.PATIENT_LAST_NAME + " TEXT,"
					+ PatientColumns.PATIENT_GENDER + " TEXT,"
					+ PatientColumns.PATIENT_AGE + " INTEGER,"
					+ PatientColumns.PATIENT_RELATIONS + " BLOB"
					+ ");");
		}
		
		private void createPrescriptionsTable(SQLiteDatabase db) {
			Log.d(LOG_TAG, "Creating the " + PRESCRIPTIONS_TABLE_NAME + " table...");
			db.execSQL("CREATE TABLE " + PRESCRIPTIONS_TABLE_NAME + " ("
					+ PrescriptionColumns._ID + " INTEGER PRIMARY KEY,"
					+ PrescriptionColumns.PRESCRIPTION_PATIENT + " INTEGER NOT NULL,"
					+ PrescriptionColumns.PRESCRIPTION_DRUG + " INTEGER NOT NULL,"
					+ PrescriptionColumns.PRESCRIPTION_DOSE_TYPE + " INTEGER NOT NULL,"
					+ PrescriptionColumns.PRESCRIPTION_DOSE_SIZE + " INTEGER,"
					+ PrescriptionColumns.PRESCRIPTION_TOTAL_UNITS + " INTEGER,"
					+ PrescriptionColumns.PRESCRIPTION_DATE_FILLED + " INTEGER,"
					+ PrescriptionColumns.PRESCRIPTION_DR_NAME + " TEXT,"
					+ PrescriptionColumns.PRESCRIPTION_UNIQUE_ID + " TEXT,"
					+ PrescriptionColumns.PRESCRIPTION_COST + " INTEGER,"
					+ PrescriptionColumns.PRESCRIPTION_NUM_REFILLS + " INTEGER,"
					+ PrescriptionColumns.PRESCRIPTION_NUM_DAYS_SUPPLIED + " INTEGER,"
					+ PrescriptionColumns.PRESCRIPTION_DATE_EXPIRATION + " INTEGER,"
					+ PrescriptionColumns.PRESCRIPTION_SCHEDULE_TYPE + " INTEGER,"
					+ PrescriptionColumns.PRESCRIPTION_SCHEDULED + " INTEGER NOT NULL,"
					+ PrescriptionColumns.PRESCRIPTION_DAY_SUNDAY + " BLOB,"
					+ PrescriptionColumns.PRESCRIPTION_DAY_MONDAY + " BLOB,"
					+ PrescriptionColumns.PRESCRIPTION_DAY_TUESDAY + " BLOB,"
					+ PrescriptionColumns.PRESCRIPTION_DAY_WEDNESDAY + " BLOB,"
					+ PrescriptionColumns.PRESCRIPTION_DAY_THURSDAY + " BLOB,"
					+ PrescriptionColumns.PRESCRIPTION_DAY_FRIDAY + " BLOB,"
					+ PrescriptionColumns.PRESCRIPTION_DAY_SATURDAY + " BLOB,"
					
					// FOREIGN KEY(patient) REFERENCES patients(_id), 
					+ "FOREIGN KEY(" + PrescriptionColumns.PRESCRIPTION_PATIENT + ") REFERENCES " + PATIENTS_TABLE_NAME + "(" + PatientColumns._ID + "),"
					// FOREIGN KEY(drug) REFERENCES drugs(_id),
					+ "FOREIGN KEY(" + PrescriptionColumns.PRESCRIPTION_DRUG + ") REFERENCES " + DRUGS_TABLE_NAME + "(" + DrugColumns._ID + ")"
					+ ");");
		}
		
		private void createSchedulesTable(SQLiteDatabase db) {
			Log.d(LOG_TAG, "Creating the " + SCHEDULES_TABLE_NAME + " table...");
			db.execSQL("CREATE TABLE " + SCHEDULES_TABLE_NAME + " ("
					+ ScheduleColumns._ID + " INTEGER PRIMARY KEY,"
					+ ScheduleColumns.SCHEDULES_PRESCRIPTION + " INTEGER NOT NULL,"
					+ ScheduleColumns.SCHEDULES_FIRST_TIME + " INTEGER NOT NULL,"
					+ ScheduleColumns.SCHEDULES_INTERVAL + " INTEGER,"
					+ ScheduleColumns.SCHEDULES_NEXT_TIME + " INTEGER,"
					+ ScheduleColumns.SCHEDULES_COUNT_REMAIN + " INTEGER,"
					
					// FOREIGN KEY(prescription) REFERENCES prescriptions(_id)
					+ "FOREIGN KEY(" + ScheduleColumns.SCHEDULES_PRESCRIPTION + ") REFERENCES " + PRESCRIPTIONS_TABLE_NAME + "(" + PrescriptionColumns._ID + ")"
					+ ");");
		}
		
		private void createNotificationEventsTable(SQLiteDatabase db) {
			Log.d(LOG_TAG, "Creating the " + NOTIFICATION_EVENTS_TABLE_NAME + " table...");
			db.execSQL("CREATE TABLE " + NOTIFICATION_EVENTS_TABLE_NAME + " ("
					+ NotificationEventColumns._ID + " INTEGER PRIMARY KEY,"
					+ NotificationEventColumns.NOTIFICATION_EVENTS_TIMESTAMP + " INTEGER NOT NULL,"
					+ NotificationEventColumns.NOTIFICATION_EVENTS_EVENT_TYPE + " INTEGER NOT NULL,"
					+ NotificationEventColumns.NOTIFICATION_EVENTS_PRESCRIPTION + " INTEGER NOT NULL,"
					
					// FOREIGN KEY(prescription) REFERENCES prescriptions(_id)
					+ "FOREIGN KEY(" + NotificationEventColumns.NOTIFICATION_EVENTS_PRESCRIPTION + ") REFERENCES " + PRESCRIPTIONS_TABLE_NAME + "(" + PrescriptionColumns._ID + ")"
					+ ");");
		}
		
		private void createSystemEventsTable(SQLiteDatabase db) {
			Log.d(LOG_TAG, "Creating the " + SYSTEM_EVENTS_TABLE_NAME + " table...");
			db.execSQL("CREATE TABLE " + SYSTEM_EVENTS_TABLE_NAME + " ("
					+ SystemEventColumns._ID + " INTEGER PRIMARY KEY,"
					+ SystemEventColumns.SYSTEM_EVENTS_TIMESTAMP + " INTEGER NOT NULL,"
					+ SystemEventColumns.SYSTEM_EVENTS_EVENT_TYPE + " INTEGER NOT NULL,"
					+ SystemEventColumns.SYSTEM_EVENTS_EVENT_SUBTYPE + " INTEGER NOT NULL,"
					+ SystemEventColumns.SYSTEM_EVENTS_EVENT_DATA + " BLOB"
					+ ");");
		}
		
		private void createSyncEventsTable(SQLiteDatabase db) {
			Log.d(LOG_TAG, "Creating the " + SYNC_EVENTS_TABLE_NAME + " table...");
			db.execSQL("CREATE TABLE " + SYNC_EVENTS_TABLE_NAME + " ("
					+ SyncEventColumns._ID + " INTEGER PRIMARY KEY,"
					+ SyncEventColumns.SYNC_EVENTS_TIMESTAMP + " INTEGER NOT NULL,"
					+ SyncEventColumns.SYNC_EVENTS_DIRECTION + " INTEGER NOT NULL,"
					+ SyncEventColumns.SYNC_EVENTS_EVENT_TYPE + " INTEGER NOT NULL,"
					+ SyncEventColumns.SYNC_EVENTS_EVENT_STATUS + " INTEGER NOT NULL,"
					+ SyncEventColumns.SYNC_EVENTS_EVENT_DATA + " INTEGER"
					+ ");");
		}
	
	}
	
	public static final class DrugColumns implements BaseColumns {
		// This class cannot be instantiated
		private DrugColumns() {}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/drugs");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.risotto.drugs";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.risotto.drug";
		
		public static final String DRUG_BRAND_NAME = "brand_name";
		
		public static final String DRUG_TYPE = "type";
		
		public static final String DRUG_STRENGTH = "strength";
		
		public static final String DRUG_STRENGTH_LABEL = "strength_label";
		
		public static final String DRUG_COMPOUND_NAME = "compound_name";	
		
		public static final String DRUG_MANUFACTURER = "manufacturer";
		
		public static final String DRUG_INTERACTIONS = "interactions";
		
		public static final String DRUG_NICK_NAME = "nick_name";
		
		public static final String DRUG_FORM = "form";
		
		public static final String DRUG_COLOR = "color";
		
		public static final String DRUG_SHAPE = "shape";
		
		public static final String DRUG_SIZE = "size";
	    
	    public static final String DEFAULT_SORT_ORDER = DRUG_BRAND_NAME + " DESC";	
	}
	
	public static final class PatientColumns implements BaseColumns {
		// This class cannot be instantiated
		private PatientColumns() {}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/patients");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.risotto.patients";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.risotto.patient";
		
		public static final String PATIENT_FIRST_NAME = "first_name";
		
	    public static final String PATIENT_LAST_NAME = "last_name";
	    
	    public static final String PATIENT_GENDER = "gender";
	    
	    public static final String PATIENT_AGE = "age";
	    
	    public static final String PATIENT_RELATIONS = "relations";
	    
	    public static final String DEFAULT_SORT_ORDER = PATIENT_LAST_NAME + " DESC";	
	}
	
	public static final class PrescriptionColumns implements BaseColumns {
		// This class cannot be instantiated
		private PrescriptionColumns() {}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/prescriptions");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.risotto.prescriptions";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.risotto.prescription";
		
		public static final String PRESCRIPTION_PATIENT = "patient";
	    
	    public static final String PRESCRIPTION_DRUG = "drug";
		
	    public static final String PRESCRIPTION_DOSE_TYPE = "dose_type";
	    
	    public static final String PRESCRIPTION_DOSE_SIZE = "dose_size";
	    
	    public static final String PRESCRIPTION_TOTAL_UNITS = "total_units";
	    
	    public static final String PRESCRIPTION_DATE_FILLED = "date_filled";
	    
	    public static final String PRESCRIPTION_DR_NAME = "dr_name";
	    
	    public static final String PRESCRIPTION_UNIQUE_ID = "prescription_id";
	    
	    public static final String PRESCRIPTION_COST = "cost";
	    
	    public static final String PRESCRIPTION_NUM_REFILLS = "num_refills";
	    
	    public static final String PRESCRIPTION_NUM_DAYS_SUPPLIED = "num_days_supplied";
	    
	    public static final String PRESCRIPTION_DATE_EXPIRATION ="date_expiration";
	    
	    public static final String PRESCRIPTION_SCHEDULE_TYPE = "schedule_type";
	    
	    public static final String PRESCRIPTION_SCHEDULED = "scheduled";
	    
	    public static final String PRESCRIPTION_DAY_SUNDAY = "day_sunday";
	    
	    public static final String PRESCRIPTION_DAY_MONDAY = "day_monday";
	    
	    public static final String PRESCRIPTION_DAY_TUESDAY = "day_tuesday";
	    
	    public static final String PRESCRIPTION_DAY_WEDNESDAY = "day_wednesday";
	    
	    public static final String PRESCRIPTION_DAY_THURSDAY = "day_thursday";
	    
	    public static final String PRESCRIPTION_DAY_FRIDAY = "day_friday";
	    
	    public static final String PRESCRIPTION_DAY_SATURDAY = "day_saturday";
	    
	    public static final String DEFAULT_SORT_ORDER = PrescriptionColumns._ID + " DESC";	
	}
	
	public static final class ScheduleColumns implements BaseColumns {
		// This class cannot be instantiated
		private ScheduleColumns() {}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/schedules");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.risotto.schedules";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.risotto.schedule";
		
		public static final String SCHEDULES_PRESCRIPTION = "prescription";
		
	    public static final String SCHEDULES_FIRST_TIME = "first_time";
	    
	    public static final String SCHEDULES_INTERVAL = "interval";
	    
	    public static final String SCHEDULES_NEXT_TIME = "next_time";
	    
	    public static final String SCHEDULES_COUNT_REMAIN = "count_remain";
	    
	    public static final String DEFAULT_SORT_ORDER = SCHEDULES_NEXT_TIME + " DESC";	
	}
	
	public static final class NotificationEventColumns implements BaseColumns {
		// This class cannot be instantiated
		private NotificationEventColumns() {}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notification_events");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.risotto.notification_events";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.risotto.notification_event";
		
		public static final String NOTIFICATION_EVENTS_TIMESTAMP = "timestamp";
		
		public static final String NOTIFICATION_EVENTS_PRESCRIPTION = "prescription";
		
		public static final String NOTIFICATION_EVENTS_EVENT_TYPE = "event_type";
		
		// Larger values (times) will be returned first
	    public static final String DEFAULT_SORT_ORDER = NOTIFICATION_EVENTS_TIMESTAMP + " DESC";
		
	}
	
	public static final class SystemEventColumns implements BaseColumns {
		// This class cannot be instantiated
		private SystemEventColumns() {}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/system_events");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.risotto.system_events";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.risotto.system_event";
		
		public static final String SYSTEM_EVENTS_TIMESTAMP = "timestamp";
		
		public static final String SYSTEM_EVENTS_EVENT_TYPE = "event_type";
		
		public static final String SYSTEM_EVENTS_EVENT_SUBTYPE = "event_subtype";
		
		public static final String SYSTEM_EVENTS_EVENT_DATA = "event_data";
		
		// Larger values (times) will be returned first.
		public static final String DEFAULT_SORT_ORDER = SYSTEM_EVENTS_TIMESTAMP + " DESC";
		
	}
	
	public static final class SyncEventColumns implements BaseColumns {
		// This class cannot be instantiated
		private SyncEventColumns() {}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sync_events");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.risotto.sync_events";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.risotto.sync_event";
		
		public static final String SYNC_EVENTS_TIMESTAMP = "timestamp";
		
		public static final String SYNC_EVENTS_DIRECTION = "direction";
		
		public static final String SYNC_EVENTS_EVENT_TYPE = "event_type";
		
		public static final String SYNC_EVENTS_EVENT_STATUS = "event_status";
		
		public static final String SYNC_EVENTS_EVENT_DATA = "event_data";

		// Larger values (times) will be returned first
		public static final String DEFAULT_SORT_ORDER = SYNC_EVENTS_TIMESTAMP + " DESC";
		
	}

	private static StorageDatabaseHelper mOpenHelper;
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new StorageDatabaseHelper(getContext());
		return true;
	}

	/**
	 * Returns the MIME type of the data at the given URI
	 * 
	 * @param uri The URI to query
	 * @return The MIME type string, or null if there is no type
	 */
	@Override
	public String getType(Uri uri) {
		switch(sUriMatcher.match(uri)) {
		case URI_TYPE_DRUGS:
			return DrugColumns.CONTENT_TYPE;
		case URI_TYPE_DRUG_ID:
			return DrugColumns.CONTENT_ITEM_TYPE;
		case URI_TYPE_PATIENTS:
			return PatientColumns.CONTENT_TYPE;
		case URI_TYPE_PATIENT_ID:
			return PatientColumns.CONTENT_ITEM_TYPE;
		case URI_TYPE_PRESCRIPTIONS:
			return PrescriptionColumns.CONTENT_TYPE;
		case URI_TYPE_PRESCRIPTION_ID:
			return PrescriptionColumns.CONTENT_ITEM_TYPE;
		case URI_TYPE_SCHEDULES:
			return ScheduleColumns.CONTENT_TYPE;
		case URI_TYPE_SCHEDULE_ID:
			return ScheduleColumns.CONTENT_ITEM_TYPE;
		case URI_TYPE_NOTIFICATION_EVENTS:
			return NotificationEventColumns.CONTENT_TYPE;
		case URI_TYPE_NOTIFICATION_EVENT_ID:
			return NotificationEventColumns.CONTENT_ITEM_TYPE;
		case URI_TYPE_SYSTEM_EVENTS:
			return SystemEventColumns.CONTENT_TYPE;
		case URI_TYPE_SYSTEM_EVENT_ID:
			return SystemEventColumns.CONTENT_ITEM_TYPE;
		case URI_TYPE_SYNC_EVENTS:
			return SyncEventColumns.CONTENT_TYPE;
		case URI_TYPE_SYNC_EVENT_ID:
			return SyncEventColumns.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/**
	 * Convenience method for determining whether the object already exists in the database.
	 * 
	 * Patient is the only object type implemented thus far.
	 * 
	 * @param Object to check
	 * @return whether or not the object already exists in the db.
	 */
	public static boolean isInDatabase(Object o) {
		String query = "";
		String[] args = null;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		boolean isPresent = false;
		
		if(o instanceof Drug) {
			return false;
		} else if(o instanceof Patient) {
			args = new String[2];
			args[0] = ((Patient)o).getFirstName();
			args[1] = ((Patient)o).getLastName();
			//ensure we do a case insensitive search (i.e JOE BLACK is the same as Joe Black)
			query = "SELECT first_name, last_name FROM patients WHERE first_name=? AND last_name=? COLLATE NOCASE";
			Cursor result = db.rawQuery(query, args);
			isPresent = result.moveToFirst();
			result.close();
			return isPresent;
		} else if(o instanceof Prescription) {
			return false;
		} else
			return false;
	}
	
	public static Cursor prescriptionJoinQuery(String[] projection) {
		Log.d(LOG_TAG,"prescriptionJoinQuery(before)");
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		String proj = "";
		String sqlQuery = "";
		int count = 0;
		//"SELECT * FROM table_a INNER JOIN table_b ON a.id=b.other_id WHERE b.property_id=?";
		
		// Prescription Query
		// select * from prescriptions, drugs, patients WHERE prescriptions.drug = drugs._id and prescriptions.patient = patients._id;
		try {
			for(count = 0;count < projection.length - 1;count++) {
				proj += projection[count] + ",";
			}
			proj += projection[count];

			Log.d(LOG_TAG,"prescirptionJoinQuery(after)");
		}catch (Exception e) {
			proj = "*";
			e.printStackTrace();
		}
		
		sqlQuery = "select " + proj + " from prescriptions, drugs, patients WHERE prescriptions.drug = drugs._id and prescriptions.patient = patients._id";
		return db.rawQuery(sqlQuery, null);
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		// A reference to the SQLite database.
		SQLiteDatabase db;
		// The id of the row that was inserted.
		long rowId;
		
		switch(sUriMatcher.match(uri)) {
		case URI_TYPE_DRUGS:
			Log.d(LOG_TAG, "Insert into the drugs table...");
			// Open the database.
			db = mOpenHelper.getWritableDatabase();
			// Call for the insert into the database.
			rowId = db.insert(DRUGS_TABLE_NAME, null, values);
			
			// Check to make sure that the insert was successful
			if (rowId > 0) {
				// Append the row ID to the content uri
				Uri drugUri = ContentUris.withAppendedId(DrugColumns.CONTENT_URI, rowId);
				// Notify the application that the content has changed
				getContext().getContentResolver().notifyChange(drugUri, null);
				// Return the uri to the caller
	            return drugUri;
			} else {
				// If the row ID was -1 the insert did not happen...
				throw new SQLException("Failed to insert row into " + uri);
			}
		case URI_TYPE_DRUG_ID:
			Log.d(LOG_TAG, "Use update to modify a row in the drug table...");
			throw new IllegalArgumentException("Invalid URI: " + uri);			
		case URI_TYPE_PATIENTS:
			Log.d(LOG_TAG, "Insert into the patients table...");
			// Open the database.
			db = mOpenHelper.getWritableDatabase();
			// Call for the insert into the database.
			rowId = db.insert(PATIENTS_TABLE_NAME, null, values);
			
			// Check to make sure that the insert was successful
			if (rowId > 0) {
				// Append the row ID to the content uri
				Uri patientUri = ContentUris.withAppendedId(PatientColumns.CONTENT_URI, rowId);
				// Notify the application that the content has changed
				getContext().getContentResolver().notifyChange(patientUri, null);
				// Return the uri to the caller
	            return patientUri;
			} else {
				// If the row ID was -1 the insert did not happen...
				throw new SQLException("Failed to insert row into " + uri);
			}
		case URI_TYPE_PATIENT_ID:
			Log.d(LOG_TAG, "Use update to modify a row in the patients table...");
			throw new IllegalArgumentException("Invalid URI: " + uri);
		case URI_TYPE_PRESCRIPTIONS:
			Log.d(LOG_TAG, "Insert into the prescriptions table...");
			// Open the database.
			db = mOpenHelper.getWritableDatabase();
			// Call for the insert into the database.
			rowId = db.insert(PRESCRIPTIONS_TABLE_NAME, null, values);
			
			// Check to make sure that the insert was successful
			if (rowId > 0) {
				// Append the row ID to the content uri
				Uri prescriptionUri = ContentUris.withAppendedId(PrescriptionColumns.CONTENT_URI, rowId);
				// Notify the application that the content has changed
				getContext().getContentResolver().notifyChange(prescriptionUri, null);
				// Return the uri to the caller
	            return prescriptionUri;
			} else {
				// If the row ID was -1 the insert did not happen...
				throw new SQLException("Failed to insert row into " + uri);
			}
		case URI_TYPE_PRESCRIPTION_ID:
			Log.d(LOG_TAG, "Use update to modify a row in the prescriptions table...");
			throw new IllegalArgumentException("Invalid URI: " + uri);
		case URI_TYPE_SCHEDULES:
			Log.d(LOG_TAG, "Insert into the schedules table...");
			// Open the database.
			db = mOpenHelper.getWritableDatabase();
			// Call for the insert into the database.
			rowId = db.insert(SCHEDULES_TABLE_NAME, null, values);
			
			// Check to make sure that the insert was successful
			if (rowId > 0) {
				// Append the row ID to the content uri
				Uri schedulesUri = ContentUris.withAppendedId(ScheduleColumns.CONTENT_URI, rowId);
				// Notify the application that the content has changed
				getContext().getContentResolver().notifyChange(schedulesUri, null);
				// Return the uri to the caller
	            return schedulesUri;
			} else {
				// If the row ID was -1 the insert did not happen...
				throw new SQLException("Failed to insert row into " + uri);
			}
		case URI_TYPE_SCHEDULE_ID:
			Log.d(LOG_TAG, "Use update to modify a row in the schedules table...");
			throw new IllegalArgumentException("Invalid URI: " + uri);
			
		case URI_TYPE_NOTIFICATION_EVENTS:
			Log.d(LOG_TAG, "Insert into the notification events table...");
			// Open the database.
			db = mOpenHelper.getWritableDatabase();
			// Call for the insert into the database.
			rowId = db.insert(NOTIFICATION_EVENTS_TABLE_NAME, null, values);
			
			// Check to make sure that the insert was successful
			if (rowId > 0) {
				// Append the row ID to the content uri
				Uri notificationEventUri = ContentUris.withAppendedId(NotificationEventColumns.CONTENT_URI, rowId);
				// Notify the application that the content has changed
				getContext().getContentResolver().notifyChange(notificationEventUri, null);
				// Return the uri to the caller
	            return notificationEventUri;
			} else {
				// If the row ID was -1 the insert did not happen...
				throw new SQLException("Failed to insert row into " + uri);
			}
		case URI_TYPE_NOTIFICATION_EVENT_ID:
			Log.d(LOG_TAG, "Use update to modify a row in the notification events table...");
			throw new IllegalArgumentException("Invalid URI: " + uri);	
			
		case URI_TYPE_SYSTEM_EVENTS:
			Log.d(LOG_TAG, "Insert into the system events table...");
			// Open the database.
			db = mOpenHelper.getWritableDatabase();
			// Call for the insert into the database.
			rowId = db.insert(SYSTEM_EVENTS_TABLE_NAME, null, values);
			
			// Check to make sure that the insert was successful
			if (rowId > 0) {
				// Append the row ID to the content uri
				Uri systemEventUri = ContentUris.withAppendedId(SystemEventColumns.CONTENT_URI, rowId);
				// Notify the application that the content has changed
				getContext().getContentResolver().notifyChange(systemEventUri, null);
				// Return the uri to the caller
	            return systemEventUri;
			} else {
				// If the row ID was -1 the insert did not happen...
				throw new SQLException("Failed to insert row into " + uri);
			}
		case URI_TYPE_SYSTEM_EVENT_ID:
			Log.d(LOG_TAG, "Use update to modify a row in the system events table...");
			throw new IllegalArgumentException("Invalid URI: " + uri);
			
		case URI_TYPE_SYNC_EVENTS:
			Log.d(LOG_TAG, "Insert into the sync events table...");
			// Open the database.
			db = mOpenHelper.getWritableDatabase();
			// Call for the insert into the database.
			rowId = db.insert(SYNC_EVENTS_TABLE_NAME, null, values);
			
			// Check to make sure that the insert was successful
			if (rowId > 0) {
				// Append the row ID to the content uri
				Uri syncEventUri = ContentUris.withAppendedId(SyncEventColumns.CONTENT_URI, rowId);
				// Notify the application that the content has changed
				getContext().getContentResolver().notifyChange(syncEventUri, null);
				// Return the uri to the caller
	            return syncEventUri;
			} else {
				// If the row ID was -1 the insert did not happen...
				throw new SQLException("Failed to insert row into " + uri);
			}
		case URI_TYPE_SYNC_EVENT_ID:
			Log.d(LOG_TAG, "Use update to modify a row in the sync events table...");
			throw new IllegalArgumentException("Invalid URI: " + uri);					
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri); 
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
		
		switch(sUriMatcher.match(uri)) {
		case URI_TYPE_DRUGS:
			Log.d(LOG_TAG, "Deleting the drug table...");
			count = db.delete(DRUGS_TABLE_NAME, where, whereArgs);
			break;
		case URI_TYPE_DRUG_ID:
			Log.d(LOG_TAG, "Deleting one drug entry...");
			String drugId = uri.getPathSegments().get(1);
            count = db.delete(DRUGS_TABLE_NAME, DrugColumns._ID + "=" + drugId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;					
		case URI_TYPE_PATIENTS:
			Log.d(LOG_TAG, "Deleting the patients table...");
			count = db.delete(PATIENTS_TABLE_NAME, where, whereArgs);
			break;
		case URI_TYPE_PATIENT_ID:
			Log.d(LOG_TAG, "Deleting one patient entry...");
			String patientId = uri.getPathSegments().get(1);
            count = db.delete(PATIENTS_TABLE_NAME, PatientColumns._ID + "=" + patientId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case URI_TYPE_PRESCRIPTIONS:
			Log.d(LOG_TAG, "Deleting the prescriptions table...");
			count = db.delete(PRESCRIPTIONS_TABLE_NAME, where, whereArgs);
			break;
		case URI_TYPE_PRESCRIPTION_ID:
			Log.d(LOG_TAG, "Deleting one prescription entry...");
			String prescriptionId = uri.getPathSegments().get(1);
            count = db.delete(PRESCRIPTIONS_TABLE_NAME, PrescriptionColumns._ID + "=" + prescriptionId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case URI_TYPE_SCHEDULES:
			Log.d(LOG_TAG, "Deleting the schedules table...");
			count = db.delete(SCHEDULES_TABLE_NAME, where, whereArgs);
			break;
		case URI_TYPE_SCHEDULE_ID:
			Log.d(LOG_TAG, "Deleting one schedule entry...");
			String scheduleId = uri.getPathSegments().get(1);
            count = db.delete(SCHEDULES_TABLE_NAME, ScheduleColumns._ID + "=" + scheduleId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		case URI_TYPE_NOTIFICATION_EVENTS:
			Log.d(LOG_TAG, "Deleting the notification events table...");
			count = db.delete(NOTIFICATION_EVENTS_TABLE_NAME, where, whereArgs);
			break;
		case URI_TYPE_NOTIFICATION_EVENT_ID:
			Log.d(LOG_TAG, "Deleting one notification event entry...");
			String notificationEventId = uri.getPathSegments().get(1);
            count = db.delete(NOTIFICATION_EVENTS_TABLE_NAME, NotificationEventColumns._ID + "=" + notificationEventId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;		
		case URI_TYPE_SYSTEM_EVENTS:
			Log.d(LOG_TAG, "Deleting the system events table...");
			count = db.delete(SYSTEM_EVENTS_TABLE_NAME, where, whereArgs);
			break;
		case URI_TYPE_SYSTEM_EVENT_ID:
			Log.d(LOG_TAG, "Deleting one system event entry...");
			String systemEventId = uri.getPathSegments().get(1);
            count = db.delete(SYSTEM_EVENTS_TABLE_NAME, ScheduleColumns._ID + "=" + systemEventId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;		
		case URI_TYPE_SYNC_EVENTS:
			Log.d(LOG_TAG, "Deleting the sync events table...");
			count = db.delete(SYNC_EVENTS_TABLE_NAME, where, whereArgs);
			break;
		case URI_TYPE_SYNC_EVENT_ID:
			Log.d(LOG_TAG, "Deleting one sync event entry...");
			String syncEventId = uri.getPathSegments().get(1);
            count = db.delete(SYNC_EVENTS_TABLE_NAME, ScheduleColumns._ID + "=" + syncEventId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri); 
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		Cursor c;
		SQLiteDatabase db;
		String orderBy;
		
		switch(sUriMatcher.match(uri)) {
		case URI_TYPE_DRUGS:
			
			Log.d(LOG_TAG, "Query for all drugs...");
			qb.setTables(DRUGS_TABLE_NAME);
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = DrugColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
			
		case URI_TYPE_DRUG_ID:
			
			Log.d(LOG_TAG, "Query for one drug...");
			qb.setTables(DRUGS_TABLE_NAME);
			qb.appendWhere(DrugColumns._ID + "=" + uri.getPathSegments().get(1));
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = DrugColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
	               
		case URI_TYPE_PATIENTS:
			
			Log.d(LOG_TAG, "Query for all patients...");
			qb.setTables(PATIENTS_TABLE_NAME);
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = PatientColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
	        
		case URI_TYPE_PATIENT_ID:
			
			Log.d(LOG_TAG, "Query for one patient...");
			qb.setTables(PATIENTS_TABLE_NAME);
			qb.appendWhere(PatientColumns._ID + "=" + uri.getPathSegments().get(1));
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = PatientColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
	        
		case URI_TYPE_PRESCRIPTIONS:
			
			Log.d(LOG_TAG, "Query for all prescriptions...");
			qb.setTables(PRESCRIPTIONS_TABLE_NAME);
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = PrescriptionColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
	        
		case URI_TYPE_PRESCRIPTION_ID:
			
			Log.d(LOG_TAG, "Query for one prescription...");
			qb.setTables(PRESCRIPTIONS_TABLE_NAME);
			qb.appendWhere(PatientColumns._ID + "=" + uri.getPathSegments().get(1));
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = PrescriptionColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
			
		case URI_TYPE_SCHEDULES:
			
			Log.d(LOG_TAG, "Query for all schedules...");
			qb.setTables(SCHEDULES_TABLE_NAME);
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = ScheduleColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
			
		case URI_TYPE_SCHEDULE_ID:
			
			Log.d(LOG_TAG, "Query for one schedule...");
			qb.setTables(SCHEDULES_TABLE_NAME);
			qb.appendWhere(PatientColumns._ID + "=" + uri.getPathSegments().get(1));
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = ScheduleColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
		
		case URI_TYPE_NOTIFICATION_EVENTS:
			
			Log.d(LOG_TAG, "Query for all notification events...");
			qb.setTables(NOTIFICATION_EVENTS_TABLE_NAME);
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = NotificationEventColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
			
		case URI_TYPE_NOTIFICATION_EVENT_ID:
			
			Log.d(LOG_TAG, "Query for one notification event...");
			qb.setTables(NOTIFICATION_EVENTS_TABLE_NAME);
			qb.appendWhere(NotificationEventColumns._ID + "=" + uri.getPathSegments().get(1));
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = NotificationEventColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
	        
		case URI_TYPE_SYSTEM_EVENTS:
			
			Log.d(LOG_TAG, "Query for all system events...");
			qb.setTables(SYSTEM_EVENTS_TABLE_NAME);
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = SystemEventColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
			
		case URI_TYPE_SYSTEM_EVENT_ID:
			
			Log.d(LOG_TAG, "Query for one system event...");
			qb.setTables(SYSTEM_EVENTS_TABLE_NAME);
			qb.appendWhere(SystemEventColumns._ID + "=" + uri.getPathSegments().get(1));
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = SystemEventColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;     
	   
		case URI_TYPE_SYNC_EVENTS:
			
			Log.d(LOG_TAG, "Query for all sync events...");
			qb.setTables(SYNC_EVENTS_TABLE_NAME);
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = SyncEventColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
			
		case URI_TYPE_SYNC_EVENT_ID:
			
			Log.d(LOG_TAG, "Query for one sync event...");
			qb.setTables(SYNC_EVENTS_TABLE_NAME);
			qb.appendWhere(SyncEventColumns._ID + "=" + uri.getPathSegments().get(1));
			
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = SyncEventColumns.DEFAULT_SORT_ORDER;
	        } else {
	            orderBy = sortOrder;
	        }
	        
	        // Get the database and run the query
	        db = mOpenHelper.getReadableDatabase();
	        c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;     
	        
	        
		default:
			throw new IllegalArgumentException("Unknown URI " + uri); 
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
		
		switch(sUriMatcher.match(uri)) {
		case URI_TYPE_DRUGS:
			Log.d(LOG_TAG, "Update called on drugs table...");
			count = db.update(DRUGS_TABLE_NAME, values, where, whereArgs);
            break;
		case URI_TYPE_DRUG_ID:
			Log.d(LOG_TAG, "Update called for one drug...");
			String drugId = uri.getPathSegments().get(1);
            count = db.update(DRUGS_TABLE_NAME, values, DrugColumns._ID + "=" + drugId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;                  
		case URI_TYPE_PATIENTS:
			Log.d(LOG_TAG, "Update called on patients table...");
			count = db.update(PATIENTS_TABLE_NAME, values, where, whereArgs);
            break;
		case URI_TYPE_PATIENT_ID:
			Log.d(LOG_TAG, "Update called for one patient...");
			String patientId = uri.getPathSegments().get(1);
            count = db.update(PATIENTS_TABLE_NAME, values, PatientColumns._ID + "=" + patientId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
		case URI_TYPE_PRESCRIPTIONS:
			Log.d(LOG_TAG, "Updates called on prescriptions table...");
			count = db.update(PRESCRIPTIONS_TABLE_NAME, values, where, whereArgs);
            break;
		case URI_TYPE_PRESCRIPTION_ID:
			Log.d(LOG_TAG, "Update called for one prescription...");
			String prescriptionId = uri.getPathSegments().get(1);
            count = db.update(PRESCRIPTIONS_TABLE_NAME, values, PrescriptionColumns._ID + "=" + prescriptionId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
		case URI_TYPE_SCHEDULES:
			Log.d(LOG_TAG, "Updates called on schedules table...");
			count = db.update(SCHEDULES_TABLE_NAME, values, where, whereArgs);
            break;
		case URI_TYPE_SCHEDULE_ID:
			Log.d(LOG_TAG, "Update called for one schedule...");
			String scheduleId = uri.getPathSegments().get(1);
            count = db.update(SCHEDULES_TABLE_NAME, values, ScheduleColumns._ID + "=" + scheduleId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;       
		case URI_TYPE_NOTIFICATION_EVENTS:
			Log.d(LOG_TAG, "Updates called on notification events table...");
			count = db.update(NOTIFICATION_EVENTS_TABLE_NAME, values, where, whereArgs);
            break;
		case URI_TYPE_NOTIFICATION_EVENT_ID:
			Log.d(LOG_TAG, "Update called for one notification event...");
			String notificationEventId = uri.getPathSegments().get(1);
            count = db.update(NOTIFICATION_EVENTS_TABLE_NAME, values, ScheduleColumns._ID + "=" + notificationEventId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break; 
		case URI_TYPE_SYSTEM_EVENTS:
			Log.d(LOG_TAG, "Updates called on system events table...");
			count = db.update(SYSTEM_EVENTS_TABLE_NAME, values, where, whereArgs);
            break;
		case URI_TYPE_SYSTEM_EVENT_ID:
			Log.d(LOG_TAG, "Update called for one system event...");
			String systemEventId = uri.getPathSegments().get(1);
            count = db.update(SYSTEM_EVENTS_TABLE_NAME, values, ScheduleColumns._ID + "=" + systemEventId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;       
		case URI_TYPE_SYNC_EVENTS:
			Log.d(LOG_TAG, "Updates called on sync events table...");
			count = db.update(SYNC_EVENTS_TABLE_NAME, values, where, whereArgs);
            break;
		case URI_TYPE_SYNC_EVENT_ID:
			Log.d(LOG_TAG, "Update called for one sync event...");
			String syncEventId = uri.getPathSegments().get(1);
            count = db.update(SYNC_EVENTS_TABLE_NAME, values, ScheduleColumns._ID + "=" + syncEventId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;  
            
		default:
			throw new IllegalArgumentException("Unknown URI " + uri); 
		}
		
		return count;
	}
	
}
