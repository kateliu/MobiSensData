package edu.cmu.mobisensdata;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.util.Log;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
	private MobiSensDataHolder dbHolder;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dbHolder = new MobiSensDataHolder(this);
        dbHolder.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
    	dbHolder.open();
    	super.onResume();
    }

    @Override
    protected void onPause() {
    	dbHolder.close();
    	super.onPause();
    }    
    
    public void onClick(View view) {
    	switch (view.getId()) {
        case R.id.open:
        	dbHolder.open();
        	break;
        case R.id.close:
        	dbHolder.close();
            break;
        case R.id.add:
        {	
        	long now = System.currentTimeMillis();
        	if (!dbHolder.add(now, now+10, "i am programming", MobiSensData.TYPE_HUMAN))
        		Log.e(TAG, "failed to add data!");
        	break;
        }	
        case R.id.batchRemove:
        {
        	long now = System.currentTimeMillis();
        	if (0 == dbHolder.batchRemove(now))
        		Log.e(TAG, "failed to delete data!");
        	break;
        }
        case R.id.search:
        {
        	long now = System.currentTimeMillis();
        	String[] results = dbHolder.search(0, now);
        	if (results == null)
        		Log.e(TAG, "can't find anything");
        	int count = results.length;
        	for (int i=0; i<count; i++)
        		Log.v(TAG, results[i]);
        	break;
        }  
        case R.id.set:
        {
        	MobiSensData first = dbHolder.getFirst();
        	if (first != null) {
            	long now = System.currentTimeMillis();
        		if (dbHolder.set(first.getStartTime(), first.getEndTime(), String.valueOf(now), first.getType()))
        			Log.v(TAG, "modified the annotation of the first record.");
        		else
        			Log.e(TAG, "failed to modify the first record.");
        	}
        	break;
        }	
        case R.id.clear:
        	int numDeletedRows = dbHolder.clear();
        	Log.v(TAG, "clear table: deleted "+numDeletedRows+" records.");
        	break;
        default:
        	break;
    	}	
    }	
}
