package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.content.UriMatcher;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
	public class GroupMessengerProvider extends ContentProvider {
	//Creating the URI with the Authority and the source
	public static final String AUTHORITY = "edu.buffalo.cse.cse486586.groupmessenger.provider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
//	private static final UriMatcher newmatch = new UriMatcher(UriMatcher.NO_MATCH);
    private SQLiteDatabase database;
    DBHelper dbHelper;
    
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, "Database_CP", null, 1);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL( " CREATE TABLE Content_Provider_DB (key TEXT NOT NULL PRIMARY KEY, value TEXT NOT NULL);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(DBHelper.class.getName(),"New Database to be inserted");
			db.execSQL("DROP TABLE IF EXISTS key,value");
			onCreate(db);
		}
	}
	
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	
    	long row = database.insertWithOnConflict("Content_Provider_DB",null,values, SQLiteDatabase.CONFLICT_REPLACE);
        	//Uri mUri = ContentUris.withAppendedId(CONTENT_URI, row);
        	//getContext().getContentResolver().notifyChange(mUri, null);
            return uri;
    }
    	

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        if(database == null)
            return false;
        else
        	return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
    	
    	String [] selection_array = {selection};
    	
    	Cursor cursor = database.rawQuery("SELECT * FROM Content_Provider_DB WHERE key = ?",selection_array);
//        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
}