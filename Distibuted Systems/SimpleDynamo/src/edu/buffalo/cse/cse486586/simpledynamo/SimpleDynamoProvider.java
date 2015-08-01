package edu.buffalo.cse.cse486586.simpledynamo;

import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimpleDynamoProvider extends ContentProvider {

	public static Context context;
	int SERVER_PORT = 10000;
	public static Uri mUri;
	public static ContentResolver mContentResolver;
	public static DBHelper dbHelper;
	public static String portStr;
	public static String myport;
	public boolean recoverwait = false;


	String firstnode = "0";
	String secondnode = "0";
	String thirdnode = "0";
	String fourthnode = "0";
	String fifthnode = "0";

	public static String KEY_FIELD = "key";
	public static String VALUE_FIELD = "value";
	public static int wait = 0;
	public static int gdwait = 0;
	//public Cursor finalcursor;		// Key Insert Cursor
	public static boolean deletecheck = true;
	public static int finaldelete = 0;
	public static int keydelete = 0;
	public static int tempkeydelete = 0;

	static HashMap<String, Object> keymap = new HashMap<String, Object>();
	static HashMap<String, Boolean> insertmap = new HashMap<String,Boolean>();
	static HashMap<String, Cursor> querywaitmap = new HashMap<String,Cursor>();
	static HashMap<String, String> querymap = new HashMap<String,String>();

	@Override
	public boolean onCreate() 
	{

		context = getContext();
		mUri = buildUri("content","edu.buffalo.cse.cse486586.simpledynamo.provider");
		mContentResolver = context.getContentResolver();
		dbHelper = new DBHelper(getContext());
		TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		myport = getPortnum(portStr);	// 11108 format
		Log.v("Enter Server", "Simple Dynamo Provider");

		try
		{
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

			Thread tserver = new Thread(new ServerTask(serverSocket));
			tserver.start();
		}catch(Exception e){
			Log.v("ERROR In server creation", e.getMessage());
		}

		joinnodes(portStr);
		Log.v("Local port is: ", portStr);

		if(databaseExist())
		{
			recovernode();
		}

		return true;
	}

	public boolean databaseExist()			//http://www.itsalif.info/content/check-if-database-exist-android-sqlite3openv2-failed
	{
		SQLiteDatabase checkDB = null;
		String path = "/data/data/edu.buffalo.cse.cse486586.simpledynamo/databases/Database_CP";
		try {
			checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
			return true;
		}
		catch(SQLiteException e) {
			Log.v("Database Non Existant Error:","False");
			return false;	
		}
	}

	static class DBHelper extends SQLiteOpenHelper 
	{
		public static String KEY="key";
		public static String VALUE="value";
		public static final String DATABASE_NAME="Database_CP";
		public static final String TABLE_NAME="Content_Provider_DB";

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL( " CREATE TABLE "+TABLE_NAME+" ("+KEY+" TEXT NOT NULL PRIMARY KEY, "+VALUE+" TEXT NOT NULL);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(DBHelper.class.getName(),"New Database to be inserted");
			db.execSQL("DROP TABLE IF EXISTS "+KEY+","+VALUE+"");
			onCreate(db);
		}
	}

	private Uri buildUri(String scheme, String authority) 
	{
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}

	public static String getPortnum(String no){
		if(no.equals("5554"))
			return "11108";
		else if(no.equals("5556"))
			return "11112";
		else if(no.equals("5558"))
			return "11116";
		else if(no.equals("5560"))
			return "11120";
		else if(no.equals("5562"))
			return "11124";
		else{
			Log.v("UNABLE TO GET PORT","NULL Return");
			return null;
		}	
	}

	private String[] Connection_table(String tempPortstr) 
	{
		String[] portArray = new String[3];
		if(tempPortstr.equals("5554"))
		{
			portArray[0] = "11112";
			portArray[1] = "11116";
			portArray[2] = "11120";
		}
		else if(tempPortstr.equals("5556"))
		{
			portArray[0] = "11124";
			portArray[1] = "11108";
			portArray[2] = "11116";
		}
		else if(tempPortstr.equals("5558"))
		{
			portArray[0] = "11108";
			portArray[1] = "11120";
			portArray[2] = "11124";
		}
		else if(tempPortstr.equals("5560"))
		{
			portArray[0] = "11116";
			portArray[1] = "11124";
			portArray[2] = "11112";
		}
		else if(tempPortstr.equals("5562"))
		{
			portArray[0] = "11120";
			portArray[1] = "11112";
			portArray[2] = "11108";
		}
		else
		{
			Log.v("Error in Conection table at:",myport);
			return null;
		}
		return portArray;
	}

	private void joinnodes(String tempPortstr)
	{
		if(tempPortstr.equals("5554"))
		{
			firstnode = "11108";
			secondnode = "11116";
			thirdnode = "11120";
			fourthnode = "11124";
			fifthnode = "11112";
		}
		else if(tempPortstr.equals("5556"))
		{
			firstnode = "11112";
			secondnode = "11108";
			thirdnode = "11116";
			fourthnode = "11120";
			fifthnode = "11124";
		}
		else if(tempPortstr.equals("5558"))
		{
			firstnode = "11116";
			secondnode = "11120";
			thirdnode = "11124";
			fourthnode = "11112";
			fifthnode = "11108";
		}
		else if(tempPortstr.equals("5560"))
		{
			firstnode = "11120";
			secondnode = "11124";
			thirdnode = "11112";
			fourthnode = "11108";
			fifthnode = "11116";
		}
		else if(tempPortstr.equals("5562"))
		{
			firstnode = "11124";
			secondnode = "11112";
			thirdnode = "11108";
			fourthnode = "11116";
			fifthnode = "11120";
		}
		else
		{
			Log.v("Error in Join Nodes at:",myport);
		}
	}

	public static boolean checkForTables(){
		boolean hasTables = false;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DBHelper.TABLE_NAME);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = qb.query(db,null,null, null,null,null,null);
		if(cursor.getCount() == 0){
			hasTables=false;
		}
		if(cursor.getCount() > 0){
			hasTables=true;
		}
		cursor.close();
		return hasTables;
	}

	boolean insertwaitcode(String key)
	{
		boolean timeoutReached = false;
		long timeout = 6000;		// Timeout duration
		long time = System.currentTimeMillis();
		if(!(insertmap.containsKey(key)))
		{
			insertmap.put(key, true);
		}

		while ((!timeoutReached) && (insertmap.get(key))) 	// The severtask will set get(key) to false
		{
			if((System.currentTimeMillis() - time) > timeout) 
			{
				timeoutReached = true;		// Timeout occurred
				Log.v("Time Out Occured for Key:", key);
				break;
			}
		}
		insertmap.put(key, true);
		return timeoutReached;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		boolean check;
		check = checkForTables();		//if table is empty

		if(selection.equals("@") && check == true)
		{
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			int row = db.delete(DBHelper.TABLE_NAME,null,null);
			Log.v("@ Rows Deleted:",String.valueOf(row));
			return row;
			//Delete Local Table 
		}
		else if(selection.equals("*"))
		{
			if (check == true)	// Not Emtpy table
			{
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				int row = db.delete(DBHelper.TABLE_NAME,null,null);
				String mcursor = String.valueOf(row); 
				MyMessage msg1 = new MyMessage(6,portStr,secondnode,null,null,null,"*",null,firstnode,false,mcursor);
				Thread t = new Thread( new ClientTask(msg1));
				t.start();
			}
			else		// Empty Table
			{
				MyMessage msg1 = new MyMessage(6,portStr,secondnode,null,null,null,"*",null,firstnode,false,"0");
				Thread t = new Thread( new ClientTask(msg1));
				t.start();
			}
			while(deletecheck);
			// Check timeout
			deletecheck = true;
			return finaldelete;

		} 
		else 				// Key delete
		{
			MyMessage msg1 = new MyMessage(6,portStr,thirdnode,null,null,null,selection,null,myport,false,"0");
			Thread t = new Thread( new ClientTask(msg1));
			t.start();

			insertwaitcode(selection);			// Check timeout
			keydelete = keydelete + tempkeydelete; 

			//Send msg to successor to delete
			MyMessage msg3 = new MyMessage(6,portStr,secondnode,null,null,null,selection,null,myport,false,"0");
			Thread t1 = new Thread( new ClientTask(msg3));
			t1.start();
			insertwaitcode(selection);
			keydelete = keydelete + tempkeydelete;

			if (check == true)
			{
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				int row = db.delete(DBHelper.TABLE_NAME,"key = '"+selection+"'",null);
				if(row == 0)
					return 0;	// local key delete fail

				else			// key deleted locally
				{
					keydelete = keydelete + row;
					Log.v("Numer of rows deleted is: ",String.valueOf(keydelete));
					return (keydelete);
				}
			}
			else 					// empty table
				return 0;
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}	

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		while(recoverwait);		// wait for recovery
		String RequestorKey = null;
		String pred = null;
		String me = null;
		String predecessor = "0";
		String successor = "0";
		String successor_succ = "0";
		String key=values.getAsString(DBHelper.KEY);
		String val=values.getAsString(DBHelper.VALUE);
		Log.v("Inserting",key);
		try
		{
			//these are the node configurations of me hardcoded
			String[] tempportstr = {firstnode, secondnode, thirdnode, fourthnode, fifthnode};
			for (int i = 0; i < 5;i++) 
			{
				// Join_table gives the succ pred and succ_succ of the parameter
				String[] portArray = Connection_table(String.valueOf((Integer.parseInt(tempportstr[i]))/2));	
				predecessor = portArray[0];
				successor = portArray[1];
				successor_succ = portArray[2];

				Log.v("PRED:ME:SUCC:SUCC_SUCC ",predecessor+":"+tempportstr[i]+":"+successor+":"+successor_succ);
				RequestorKey = genHash(key);
				me = genHash(String.valueOf((Integer.parseInt(tempportstr[i]))/2));
				pred = genHash(String.valueOf((Integer.parseInt(predecessor))/2));
				if(((RequestorKey.compareTo(pred)>0) && (RequestorKey.compareTo(me)<= 0))
						||
						((me.compareTo(pred)<0) && ((RequestorKey.compareTo(pred)>0) ||(RequestorKey.compareTo(me)<0))))
				{
					Log.v("I AM Coordinator for Key: ", tempportstr[i]+":"+key);
					if(!(keymap.containsKey(key)))		// Lock doesnt exists in the map
					{
						keymap.put(key, new Object());
					}

					if(tempportstr[i].equals(firstnode))			// If I am Coordinator
					{
						synchronized(keymap.get(key))
						{
							Log.v("Insert:", "Entering Self Insert");
							SQLiteDatabase database = dbHelper.getWritableDatabase();
							long row = database.insertWithOnConflict(DBHelper.TABLE_NAME,null,values, SQLiteDatabase.CONFLICT_REPLACE);
							Log.v("Self Inserted["+String.valueOf(row)+"]:sucess", key);
						}

						MyMessage msg2 = new MyMessage(4,portStr,successor,null,null,null,key,val,firstnode,false,null);
						Thread t2 = new Thread( new ClientTask(msg2));
						t2.start();
						insertwaitcode(key);

						MyMessage msg3 = new MyMessage(4,portStr,successor_succ,null,null,null,key,val,firstnode,false,null);
						Thread t3 = new Thread( new ClientTask(msg3));
						t3.start();
						insertwaitcode(key);
					}
					else		// Not Coordinator
					{
						// Send msg to Coordinator
						MyMessage msg1 = new MyMessage(4,portStr,tempportstr[i],null,null,null,key,val,firstnode,false,null);
						Thread t1 = new Thread( new ClientTask(msg1));
						t1.start();
						insertwaitcode(key);

						if(successor.equals(firstnode))	// If i am Successor
						{
							synchronized(keymap.get(key))
							{
								Log.v("Insert:","Entering  Successor Self Insert");
								SQLiteDatabase database = dbHelper.getWritableDatabase();
								long row = database.insertWithOnConflict(DBHelper.TABLE_NAME,null,values, SQLiteDatabase.CONFLICT_REPLACE);
								Log.v("Self Successcor Inserted["+String.valueOf(row)+"]:sucess", key);
							}

							MyMessage msg3 = new MyMessage(4,portStr,successor_succ,null,null,null,key,val,firstnode,false,null);
							Thread t3 = new Thread( new ClientTask(msg3));
							t3.start();
							insertwaitcode(key);


						}
						else	// I am not Successor or coordinator not successor
						{

							MyMessage msg2 = new MyMessage(4,portStr,successor,null,null,null,key,val,firstnode,false,null);
							Thread t2 = new Thread( new ClientTask(msg2));
							t2.start();
							insertwaitcode(key);

							if(successor_succ.equals(firstnode))//	I am Successor_Succ
							{
								synchronized(keymap.get(key))
								{ 
									Log.v("Insert:","Entering Successcor_succ Self Insert");
									SQLiteDatabase database = dbHelper.getWritableDatabase();
									long row = database.insertWithOnConflict(DBHelper.TABLE_NAME,null,values, SQLiteDatabase.CONFLICT_REPLACE);
									Log.v("Self Successcor_succ Inserted["+String.valueOf(row)+"]:sucess", key);
								}
							}
							else			//	I am not Successor_Succ, successor or coordinator
							{
								MyMessage msg3 = new MyMessage(4,portStr,successor_succ,null,null,null,key,val,firstnode,false,null);
								Thread t3 = new Thread( new ClientTask(msg3));
								t3.start();
								insertwaitcode(key);
							}
						}
					}

					Log.v("Insert 3:","Success");
					return uri;						
				}
			}
		}catch(Exception e)
		{
			Log.v("Insert Error", e.getMessage());
		}
		return uri;
	}



	@Override
	public Cursor  query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		boolean check;
		check = checkForTables();
		if (selection.equals("@"))
		{
			if(check == true)
			{
				Log.v("Enter @ Many nodes", "doing query");

				//Search key in local
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(DBHelper.TABLE_NAME);
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				Cursor cursor = qb.query(db,null,null, null,null,null,null);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				//db.close();
				Log.v("Query @:", "Success");
				return cursor;
			}
			else
			{
				return null;
			}
		}
		else if (selection.equals("*"))
		{
			querymap.clear();
			Log.v("Enter * Many nodes", "doing query");

			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(DBHelper.TABLE_NAME);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = qb.query(db,null,null, null,null,null,null);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);


			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
			{
				querymap.put(cursor.getString(0), cursor.getString(1));
			}

			String[] brdcst_wait1 = {secondnode, thirdnode, fourthnode, fifthnode};
			for (int i = 0; i < 4;i++) 
			{
				MyMessage msg3 = new MyMessage(5,portStr,brdcst_wait1[i],null,null,null,"*",null,myport,false,"");
				Thread t = new Thread(new ClientTask(msg3));
				t.start();
				insertwaitcode("*");
			}

			Log.v("Query *:", "Success");
			Set<java.util.Map.Entry<String,String>> mapSet = querymap.entrySet();
			Iterator<java.util.Map.Entry<String,String>> mapIterator = mapSet.iterator();
			String feild[]={KEY_FIELD,VALUE_FIELD};
			MatrixCursor mcursor= new MatrixCursor(feild);
			while (mapIterator.hasNext()) 
			{
				java.util.Map.Entry<String,String> mapEntry = mapIterator.next();
				String keyValue = mapEntry.getKey();
				String value = mapEntry.getValue();
				String row[]={keyValue,value};
				mcursor.addRow(row);
			}
			return mcursor;
		}
		else			// Key query
		{
			while(recoverwait);			// wait for recovery
			String RequestorKey = null;
			String pred = null;
			String me = null;
			String predecessor = "0";
			String successor = "0";
			String successor_succ = "0";
			Log.v("Entering into Query","Entering into Query");
			Log.v("Querying",selection);
			try
			{
				String[] tempportstr = {firstnode, secondnode, thirdnode, fourthnode, fifthnode};

				for (int i = 0; i < 5;i++) 
				{
					String[] portArray = Connection_table(String.valueOf((Integer.parseInt(tempportstr[i]))/2));
					predecessor = portArray[0];
					successor = portArray[1];
					successor_succ = portArray[2];

					Log.v("I am: ",tempportstr[i]);
					Log.v("PRED:SUCC ",predecessor+":"+successor);
					RequestorKey = genHash(selection);
					me = genHash(String.valueOf((Integer.parseInt(tempportstr[i]))/2));
					Log.v("I am====",me);
					pred = genHash(String.valueOf(Integer.parseInt(predecessor)/2));
					Log.v("Firstnode is=====",firstnode);
					if(((RequestorKey.compareTo(pred)>0) && (RequestorKey.compareTo(me)<= 0))
							||
							((me.compareTo(pred)<0) && ((RequestorKey.compareTo(pred)>0) ||(RequestorKey.compareTo(me)<0))))
					{
						if(!(keymap.containsKey(selection)))		// Lock doesnt exists in the map
						{
							keymap.put(selection, new Object());
						}

						Log.v("Entered Lock COordinator:",tempportstr[i]);
						if(tempportstr[i].equals(firstnode)) //if its self port Query
						{
							synchronized(keymap.get(selection))
							{

								Log.v("Insert","Entered Self Query");
								SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
								qb.setTables(DBHelper.TABLE_NAME);
								SQLiteDatabase db = dbHelper.getWritableDatabase();
								Cursor cursor = qb.query(db,null,DBHelper.KEY+"="+"'"+selection+"'", null,null,null,null);
								cursor.moveToFirst();
								Log.v("Key Success is:",cursor.getString(0));
								return cursor;
							}

						}
						else	//send message to query
						{
							querywaitmap.clear();
							Log.v("Send msg to Coordinator: ", selection);
							MyMessage msg3 = new MyMessage(7,portStr,tempportstr[i],null,null,null,selection,null,firstnode,false,"");
							Thread t = new Thread( new ClientTask(msg3));
							t.start();
							Log.v("Send message to:",tempportstr[i]);
							boolean timeout = insertwaitcode(selection);
							if(timeout == true)
							{
								if(successor.equals(firstnode))
								{
									synchronized(keymap.get(selection))
									{

										Log.v("Insert","Entered Self Query");
										SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
										qb.setTables(DBHelper.TABLE_NAME);
										SQLiteDatabase db = dbHelper.getWritableDatabase();
										Cursor cursor = qb.query(db,null,DBHelper.KEY+"="+"'"+selection+"'", null,null,null,null);
										cursor.moveToFirst();
										Log.v("Key Successor Success is:",cursor.getString(0));
										return cursor;
									}
								}
								else
								{
									//send message to succ to query
									MyMessage msg4 = new MyMessage(7,portStr,successor,null,null,null,selection,null,firstnode,false,"");
									Thread t1 = new Thread( new ClientTask(msg4));
									t1.start();
									boolean timeout1 = insertwaitcode(selection);
									if (timeout1 == true)	// if successor also fails
									{
										if(successor_succ.equals(firstnode))
										{
											synchronized(keymap.get(selection))
											{

												Log.v("Insert","Entered Self Query");
												SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
												qb.setTables(DBHelper.TABLE_NAME);
												SQLiteDatabase db = dbHelper.getWritableDatabase();
												Cursor cursor = qb.query(db,null,DBHelper.KEY+"="+"'"+selection+"'", null,null,null,null);
												cursor.moveToFirst();
												Log.v("Key Successor_succ Success is:",cursor.getString(0));
												return cursor;
											}

										}
										//send message to succ_succ
										MyMessage msg5 = new MyMessage(7,portStr,successor_succ,null,null,null,selection,null,firstnode,false,"");
										Thread t2 = new Thread( new ClientTask(msg5));
										t2.start();
										insertwaitcode(selection);
									}
								}

							}
							querywaitmap.get(selection).moveToFirst();
							Log.v("Query Complete at: "+myport,querywaitmap.get(selection).getString(0) );
							/*finalcursor = querywaitmap.get(selection);
							finalcursor.moveToFirst();
							Log.v("Final KeyQuery Returned is: ",finalcursor.getString(0));*/
							return querywaitmap.get(selection);
						}
					}
				}
			}catch(Exception e)
			{
				Log.v("Key Query Error: ", e.getMessage());
			}
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs) 
	{
		return 0;
	}

	static String genHash(String input) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = sha1.digest(input.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : sha1Hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	private void recovernode() 
	{		
		recoverwait = true;
		/*String[] brdcst_wait = {firstnode, secondnode, thirdnode, fourthnode, fifthnode};
		for (int i = 1; i < 5;i++) 
		{
			MyMessage msg5 = new MyMessage(9,portStr,brdcst_wait[i],null,null,null,"true",null,firstnode,false,"true");
			Thread t2 = new Thread( new ClientTask(msg5));
			t2.start();			
		}*/
		Log.v("Recovery section","Recover");
		Log.v("PRED: SUCC:SUCC_SUCC === ", fifthnode+":"+secondnode+":"+thirdnode);
		// Delete @
		int rows = delete(mUri, "@", null);
		Log.v("Deleted rows are", String.valueOf(rows));

		// Query *
		Cursor cursor = query(mUri, null, "*", null, null);

		String mcursor = "";
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			/*mcursor += cursor.getString(0)+","+cursor.getString(1)+",";
		}

		//call function for inserting
		String str[] = mcursor.split(",");
		for(int i=0;i<str.length-1;i=i+2)
		{
			String key1 = str[i];
			String value1 = str[i+1];*/
			ContentValues cv = new ContentValues();
			cv.put(KEY_FIELD, cursor.getString(0));
			cv.put(VALUE_FIELD, cursor.getString(1));
			Log.v("No of keys are:", String.valueOf(cursor.getCount()));
			if(portStr.equals("5554"))
				Insertfunc(cv,"5554","5560");
			else if(portStr.equals("5556"))
				Insertfunc(cv,"5556","5558");
			else if(portStr.equals("5558"))
				Insertfunc(cv, "5558", "5562");
			else if(portStr.equals("5560"))
				Insertfunc(cv, "5560", "5556");
			else if(portStr.equals("5562"))
				Insertfunc(cv, "5562", "5554");
		}

		//key insert in coordinator
		/*{insertmethod(cv,firstnode);	// Insert my values
		insertmethod(cv,fifthnode);	// insert my predecessor values in me
		insertmethod(cv,fourthnode);}*/	// Insert my pred_pred values in me	

		/*String[] brdcst_wait1 = {firstnode, secondnode, thirdnode, fourthnode, fifthnode};
		for (int i = 1; i < 5;i++) 
		{
			MyMessage msg6 = new MyMessage(9,portStr,brdcst_wait1[i],null,null,null,"false",null,firstnode,false,"false");
			Thread t3 = new Thread( new ClientTask(msg6));
			t3.start();			
		}*/
		Log.v("**********RecoverWAIT***********", "cOMPLETE");
		recoverwait = false;
	}

	/*void insertmethod(ContentValues values, String node) {

		String RequestorKey = null;
		String pred = null;
		String me = null;

		String predecessor = "0";
		String successor = "0";
		String successor_succ = "0";

		String key=values.getAsString(KEY_FIELD);
		//String val=values.getAsString(DBHelper.VALUE);
		//Log.v("Inserting InsertMethod",key);
		try
		{
			// Join_table gives the succ pred and succ_succ of the parameter
			String[] portArray = Connection_table(String.valueOf((Integer.parseInt(node))/2));	
			predecessor = portArray[0];
			successor = portArray[1];
			successor_succ = portArray[2];

			RequestorKey = genHash(key);
			me = genHash(String.valueOf((Integer.parseInt(node))/2));
			pred = genHash(String.valueOf((Integer.parseInt(predecessor))/2));
			if(((RequestorKey.compareTo(pred)>0) && (RequestorKey.compareTo(me)<= 0))
					||
					((me.compareTo(pred)<0) && ((RequestorKey.compareTo(pred)>0) ||(RequestorKey.compareTo(me)<0))))
			{
				if(!(keymap.containsKey(key)))		// Lock doesnt exists in the map
				{
					keymap.put(key, new Object());
				}
				synchronized (keymap.get(key)) {
					Log.v("Insert Method: PRED:ME:SUCC:SUCC_SUCC ",predecessor+":"+node+":"+successor+":"+successor_succ);
					SQLiteDatabase database = dbHelper.getWritableDatabase();
					long row = database.insertWithOnConflict(DBHelper.TABLE_NAME,null,values, SQLiteDatabase.CONFLICT_REPLACE);
					Log.v("Self Inserted["+String.valueOf(row)+"]:sucess", key);
				}

			}
		}catch(Exception e){
			Log.v("Insertmethod",e.getMessage());
		}
	}*/


	private void Insertfunc(ContentValues cv,String node, String prd)
	{
		String RequestorKey = null;
		String pred = null;
		String me = null;

		String key=cv.getAsString(KEY_FIELD);

		try {
			RequestorKey = genHash(key);
			me = genHash(node);
			pred = genHash(prd);
			if(((RequestorKey.compareTo(pred)>0) && (RequestorKey.compareTo(me)<= 0))
					||
					((me.compareTo(pred)<0) && ((RequestorKey.compareTo(pred)>0) ||(RequestorKey.compareTo(me)<0))))
			{
				Log.v("I am Coordinator:",node);
				SQLiteDatabase database = dbHelper.getWritableDatabase();
				long row = database.insertWithOnConflict(DBHelper.TABLE_NAME,null,cv, SQLiteDatabase.CONFLICT_REPLACE);
				Log.v("Self Inserted["+String.valueOf(row)+"]:sucess", key);
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
