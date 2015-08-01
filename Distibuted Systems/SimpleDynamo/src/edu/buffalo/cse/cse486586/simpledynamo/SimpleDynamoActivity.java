package edu.buffalo.cse.cse486586.simpledynamo;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SimpleDynamoActivity extends Activity {
	
	public static TextView remotetextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_dynamo);
    
		TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        remotetextView = (TextView) findViewById(R.id.textView1);	// For onprogressupdate
        
        findViewById(R.id.button3).setOnClickListener(new OnTestClickListener(tv, getContentResolver()));
        
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {	//LDUMP
				OnTestClickListener.Task.testInsert();
				Cursor cursor = SimpleDynamoProvider.mContentResolver.query
								(SimpleDynamoProvider.mUri, null,"@", null, null);
				Log.v("LDUMP Button Success", String.valueOf(cursor.getCount()));
			}
		});
        
        
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {	//GDUMP
        		OnTestClickListener.Task.testInsert();
        		Cursor cursor = SimpleDynamoProvider.mContentResolver.query
						(SimpleDynamoProvider.mUri, null,"*", null, null);
				Log.v("GDUMP Button Success", String.valueOf(cursor.getCount()));
        	}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.simple_dynamo, menu);
		return true;
	}

}
