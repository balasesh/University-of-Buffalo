package edu.buffalo.cse.cse486586.simpledht;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimpleDhtProvider extends ContentProvider {

	String TAG = SimpleDhtActivity.class.getSimpleName();
	int SERVER_PORT = 10000;
	public static Uri mUri;
	public static ContentResolver mContentResolver;
	public static Context context;
	public static String portStr;				//my port
	public static String predecessor = "0";	//my predecessor
	public static String successor = "0";		//my successor
	public static final String KEY_FIELD = "key";
	public static final String VALUE_FIELD = "value";
	public static String myport;
	public static int wait = 0;
	public static int gdwait = 0;
	public static Cursor finalcursor;
	public static Cursor gdfinalcursor;
	public static String mfinalstring = "";
	public static boolean deletecheck = true;
	public static int finaldelete = 0;
	public static int keydelete = 0;

	public static String AUTHORITY = "edu.buffalo.cse.cse486586.simpledht.provider";
	public static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	private SQLiteDatabase database;
	public static DBHelper dbHelper;
	public static boolean flag = true;

	@Override
	public boolean onCreate() {
		//getContext().deleteDatabase(dbHelper.DATABASE_NAME);
		context=getContext();
		mUri = buildUri("content","edu.buffalo.cse.cse486586.simpledht.provider");
		mContentResolver = context.getContentResolver();
		dbHelper = new DBHelper(getContext());
		TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		myport = getPortnum(portStr);
		Log.v("Enter Server", "Simple DHT Provider");

		//predecessor = successor = String.valueOf((Integer.parseInt(portStr) * 2));

		try
		{
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,serverSocket);

			//calling server to listen on socket
		}catch(Exception e){
			Log.v("ERROR", "In server creation");
		}

		MyMessage msg = new MyMessage(0,portStr,"11108",null,null,null,null,null,false,null);

		try {
			if(!(genHash(portStr).equals(genHash("5554")))){
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg);
				Log.v("Sent message to:", msg.remotePort);
				//Join Request msg
			}
		}catch(Exception e){
			System.out.println("Simple DHT onCreate Error");
		}

		return true;

	}

	public static class ClientTask extends AsyncTask<MyMessage, Void, Void>
	{
		@Override
		protected Void doInBackground(MyMessage... msgs)
		{
			try
			{

				System.out.println("Enter Client task");
				Socket socket = new Socket(InetAddress.getByAddress(new byte[] 
						{ 10, 0, 2, 2 }),Integer.parseInt(msgs[0].remotePort));
				ObjectOutputStream msgbuffer = null;
				msgbuffer = new ObjectOutputStream(socket.getOutputStream()); 
				msgbuffer.writeObject(msgs[0]);	//Send Request to Remote port
				msgbuffer.flush();
				msgbuffer.close();
				socket.close();
			}catch(Exception e){
				System.out.println("Client Task error: "+e.getMessage());
			}
			return null;
		}

	}

	static class DBHelper extends SQLiteOpenHelper {

		public static String KEY="key";
		public static String VALUE="value";
		public static final String DATABASE_NAME="Database_CP";
		public static final String TABLE_NAME="Content_Provider_DB";

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
			// TODO Auto-generated constructor stub
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

	private Uri buildUri(String scheme, String authority) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}

	public static String getPortnum(String no){
		if(no.equalsIgnoreCase("5554"))
			return "11108";
		else if(no.equalsIgnoreCase("5556"))
			return "11112";
		else if(no.equalsIgnoreCase("5558"))
			return "11116";
		else if(no.equalsIgnoreCase("5560"))
			return "11120";
		else if(no.equalsIgnoreCase("5562"))
			return "11124";
		else{
			System.out.println("UNABLE TO GET PORT");
			return null;
		}	
	}

	public static String genHash(String input) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = sha1.digest(input.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : sha1Hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) 
	{
		boolean check;
		check = checkForTables();		//if table is empty

		if(predecessor == "0" && successor == "0" && check == true)
		{

			if(selection.equals("@") || selection.equals("*"))
			{
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				int row = db.delete(dbHelper.TABLE_NAME,null,null);
				return row;
			}
			else
			{
				SQLiteDatabase db = dbHelper.getWritableDatabase(); 
				int row = db.delete(dbHelper.TABLE_NAME,"key = '"+selection+"'",null);
				return row;

			}



		}
		else if(selection != null)
		{
			if(selection.equals("@") && check == true)
			{
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				int row = db.delete(dbHelper.TABLE_NAME,null,null);
				Log.v("@ Rows Deleted:",String.valueOf(row));
				return row;
				//Delete Local Table 
			}
			else if(selection.equals("*"))
			{
				if (check == true)	// Not Emtpy table
				{
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					int row = db.delete(dbHelper.TABLE_NAME,null,null);
					String mcursor = String.valueOf(row); 
					MyMessage msg1 = new MyMessage(5,portStr,successor,null,null,"*",null,null,false,mcursor);
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
				}
				else		// Empty Table
				{
					MyMessage msg1 = new MyMessage(5,portStr,successor,null,null,"*",null,null,false,"0");
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);						
				}
				while(deletecheck);
				deletecheck = true;
				return finaldelete;

			} 
			else 
			{
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				int row = db.delete(dbHelper.TABLE_NAME,"key = '"+selection+"'",null);
				if(row == 0)
				{
					MyMessage msg3 = new MyMessage(5,portStr,successor,null,null,selection,null,myport,false,"0");
					System.out.println("Key not found to dlete : to succ");
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);
					while(flag == true);
					flag = true;
					return keydelete;
				}
				else
					return 0;
			}
		}
		else 
			return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String RequestorKey = null;
		String pred = null;
		String me = null;


		try{
			if(predecessor == "0" || successor == "0")
			{
				long row = 0;
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				row = db.insertWithOnConflict(DBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				Log.v("Key Inserted[]:sucess", String.valueOf(row));
				return uri;
			}
			else
			{
				String key=values.getAsString(dbHelper.KEY);
				String val=values.getAsString(dbHelper.VALUE);
				RequestorKey = genHash(key);
				me = genHash(portStr);
				pred = genHash(String.valueOf(Integer.parseInt(predecessor)/2));

				if(((RequestorKey.compareTo(pred)>0) && (RequestorKey.compareTo(me)<= 0))
						||
						((me.compareTo(pred)<0) && ((RequestorKey.compareTo(pred)>0) ||(RequestorKey.compareTo(me)<0))))
				{

					long row = 0;
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					row = db.insert(DBHelper.TABLE_NAME,null,values);
					Log.v("Key Inserted["+String.valueOf(row)+"]:sucess", key);
					return uri;
				}
				else
				{
					MyMessage msg3 = new MyMessage(3,portStr,successor,null,null,key,val,null,false,null);
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);

				}
			}

		}catch(Exception e)
		{
			System.out.println("Insert Error");
		}
		return uri;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {

		boolean check;
		check = checkForTables();
		if (selection.equals("@"))
		{
			if(check == true){
				if(predecessor == "0" || successor == "0" )
				{
					Log.v("Enter @ Single node", "doing query");
					SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
					qb.setTables(DBHelper.TABLE_NAME);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = qb.query(db,null,null, null,null,null,null);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					Log.v("Self Query @", String.valueOf(cursor.getPosition()));
					return cursor;

				}
				else
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
			}
			else
			{
				return null;
			}
		}
		else if (selection.equals("*"))
		{
			if(predecessor == "0" && successor == "0")
			{
				if(check == true)
				{
					Log.v("Enter * Single Node", "doing query");
					SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
					qb.setTables(DBHelper.TABLE_NAME);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = qb.query(db,null,null, null,null,null,null);
					//Cursor cursor = db.rawQuery("SELECT * FROM "+dbHelper.TABLE_NAME, null);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					//db.close();
					Log.v("Self Query *", String.valueOf(cursor.getPosition()));
					return cursor;
				}
				else
				{
					return null;
				}
			}
			else
			{
				if(check == true)
				{
					Log.v("Enter * Many nodes", "doing query");

					SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
					qb.setTables(DBHelper.TABLE_NAME);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = qb.query(db,null,null, null,null,null,null);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);

					String mcursor = "";
					for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
					{
						mcursor += cursor.getString(0)+","+cursor.getString(1)+",";
					}
					MyMessage msg3 = new MyMessage(4,portStr,successor,null,null,"*",null,myport,false,mcursor);
					//cursor.close();
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);
				}
				else
				{
					Log.v("Check is false","htrht");
					MyMessage msg3 = new MyMessage(4,portStr,successor,null,null,"*",null,myport,false,"");
					//cursor.close();
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);
				}

				while(gdwait == 0);
				gdwait = 0;
				Log.v("Query *:", "Success");
				return gdfinalcursor;

			}
		}
		else
		{
			if(predecessor == "0" && successor == "0" )
			{
				if(check == true)
				{
					Log.v("Enter key query 1 node", "doing query");
					SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
					qb.setTables(DBHelper.TABLE_NAME);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = qb.query(db,null,dbHelper.KEY+"="+"'"+selection+"'", null,null,null,null);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					//db.close();
					return cursor;
				}
				else
					return null;
			}
			else
			{
				if (check == true)
				{
					Log.v("Enter key query multiple node", "doing query");
					SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
					qb.setTables(DBHelper.TABLE_NAME);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = qb.query(db,null,dbHelper.KEY+"="+"'"+selection+"'", null,null,null,null);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					if(cursor.getCount() == 0)
					{
						Log.v("Query unavailable in current node:", "Key Unavailable");
						MyMessage msg3 = new MyMessage(4,portStr,successor,null,null,selection,null,myport,false,"");
						//cursor.close();
						Log.v("Send message to:",successor);
						new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);
						while(wait == 0);
						wait = 0;
						return finalcursor;
					}
					else
					{
						return cursor;
					}
				}
				else	// if DB empty send empty string to next Node
				{
					MyMessage msg3 = new MyMessage(4,portStr,successor,null,null,selection,null,myport,false,"");
					Log.v("Send message to:",successor);
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);
					while(wait == 0);
					wait = 0;
					return finalcursor;
				}
			}
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
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

}

