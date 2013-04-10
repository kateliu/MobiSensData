package edu.cmu.mobisensdata;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MobiSensDataHolder {
	private static final String TAG = "MobiSensDataHolder";
	
    private SQLiteDatabase db;
    private MobiSensSQLiteHelper dbHelper;
	
    public MobiSensDataHolder (Context context) {
    	dbHelper = new MobiSensSQLiteHelper(context);
    }

    public void open() throws SQLException {
    	Log.v(TAG, "open");
    	db = dbHelper.getWritableDatabase();
    }
    
    public void close() {
    	Log.v(TAG, "close");
        dbHelper.close();
    }
    
    public boolean add(long startTime, long endTime, String annotation, int type) {
    	Log.v(TAG, "add");
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return false;
    	}
    	
    	if (!MobiSensData.isValid(startTime, endTime, annotation, type)) {
    		Log.e(TAG, "data are invalid!");
    		return false;
    	}	
    	
    	ContentValues values = new ContentValues();
    	values.put(MobiSensSQLiteHelper.COLUMN_START_TIME, startTime);
    	values.put(MobiSensSQLiteHelper.COLUMN_END_TIME, endTime);
    	values.put(MobiSensSQLiteHelper.COLUMN_ANNOTATION, annotation);
    	values.put(MobiSensSQLiteHelper.COLUMN_TYPE, type);
    	return (db.insert(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, null, values)>=0?true:false);
    }	
    
    public int batchRemove(long time) {
    	Log.v(TAG, "batchRemove");   
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return 0;
    	}
    	return db.delete(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, MobiSensSQLiteHelper.COLUMN_END_TIME+"<"+String.valueOf(time), null);
    }
    
    public String[] search(long s, long e) {
    	Log.v(TAG, "search");   
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return null;
    	}    	
    	String[] queryColumns = {MobiSensSQLiteHelper.COLUMN_ANNOTATION};
    	String selection = String.format("%s > %d and %s < %d", MobiSensSQLiteHelper.COLUMN_START_TIME, s, MobiSensSQLiteHelper.COLUMN_END_TIME, e);
    	Cursor cursor = db.query(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, queryColumns, selection, null, null, null, null);
    	int count = cursor.getCount();
    	Log.v(TAG, "found " + count + " records.");
    	if (count <= 0 ) {
    		return null;
    	}
    	
    	String[] results = new String[count];
    	cursor.moveToFirst();
    	for (int i=0; i<count; i++){
    		results[i] = cursor.getString(0);
    		cursor.moveToNext();
    	}  	
    	return results;
    }
    
    public boolean set(long startTime, long endTime, String annotation, int type) {
    	Log.v(TAG, "set");
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return false;
    	}
    	if (!MobiSensData.isValid(startTime, endTime, annotation, type)) {
    		Log.e(TAG, "data are invalid!");
    		return false;
    	}	
    	
    	ContentValues values = new ContentValues();
    	values.put(MobiSensSQLiteHelper.COLUMN_ANNOTATION, annotation);
    	values.put(MobiSensSQLiteHelper.COLUMN_TYPE, type);
    	String selection = String.format("%s = %d and %s = %d", MobiSensSQLiteHelper.COLUMN_START_TIME, startTime, MobiSensSQLiteHelper.COLUMN_END_TIME, endTime);
    	if (0==db.update(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, values, selection, null)) {
    		return add(startTime, endTime, annotation, type);
    	}
    	return true;
    }	
    
    public int clear() {
    	Log.v(TAG, "clear");
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return 0;
    	}
    	return db.delete(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, null, null);
    }

    public MobiSensData getFirst() {
    	Log.v(TAG, "getFirst");
    	if (!db.isOpen()) {
    		Log.e(TAG, "db is not open!");
    		return null;
    	}    	
    	
    	String[] queryColumns = {MobiSensSQLiteHelper.COLUMN_START_TIME, MobiSensSQLiteHelper.COLUMN_END_TIME, MobiSensSQLiteHelper.COLUMN_ANNOTATION, MobiSensSQLiteHelper.COLUMN_TYPE};
    	Cursor cursor = db.query(MobiSensSQLiteHelper.TABLE_MOBISENSDATA, queryColumns, null, null, null, null, null, "1");
    	if (cursor.getCount()<=0) {
    		Log.v(TAG, "table is empty");
    		return null;
    	}
    	cursor.moveToFirst();
    	return new MobiSensData(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getInt(3));    	   	
    }
}
