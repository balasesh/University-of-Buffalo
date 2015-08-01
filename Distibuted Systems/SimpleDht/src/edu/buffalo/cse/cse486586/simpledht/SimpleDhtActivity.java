package edu.buffalo.cse.cse486586.simpledht;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SimpleDhtActivity extends Activity {
	
	public static TextView remotetextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_dht_main);
        
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        remotetextView = (TextView) findViewById(R.id.textView1);	// For onprogressupdate
        
        findViewById(R.id.button3).setOnClickListener(
                new OnTestClickListener(tv, getContentResolver()));
        
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {	//LDUMP
				OnTestClickListener.Task.testInsert();
				Cursor cursor = SimpleDhtProvider.mContentResolver.query
								(SimpleDhtProvider.mUri, null,"@", null, null);
				Log.v("LDUMP Button Success", String.valueOf(cursor.getCount()));
			}
		});
        
        
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {	//GDUMP
        		OnTestClickListener.Task.testInsert();
        		Cursor cursor = SimpleDhtProvider.mContentResolver.query
						(SimpleDhtProvider.mUri, null,"*", null, null);
				Log.v("GDUMP Button Success", String.valueOf(cursor.getCount()));
        	}
		});
        
        findViewById(R.id.button4).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OnTestClickListener.Task.testInsert();
        		Cursor cursor = SimpleDhtProvider.mContentResolver.query
						(SimpleDhtProvider.mUri, null,"key36", null, null);
        		cursor.moveToFirst();
				Log.v("Key Button Success", cursor.getString(0));			
				}
		});
        
      
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_simple_dht_main, menu);
        return true;
    }

}