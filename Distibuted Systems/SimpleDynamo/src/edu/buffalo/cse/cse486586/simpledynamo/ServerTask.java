package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.buffalo.cse.cse486586.simpledynamo.SimpleDynamoProvider.DBHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.Settings.Global;
import android.util.Log;

public class ServerTask implements Runnable {

	ServerSocket sockets;
	String firstnode = "0";
	String secondnode = "0";
	String thirdnode = "0";
	String fourthnode = "0";
	String fifthnode = "0";

	public ServerTask(ServerSocket sockets)
	{
		this.sockets = sockets;
	}
	String Requestport = null;

	@Override
	public void run() 
	{
		joinnodes(SimpleDynamoProvider.portStr);
		Log.v("Pred: Succ", fifthnode+":"+secondnode);
		while(true)
		{
			try
			{
				Socket temmpsock = sockets.accept();
				Log.v("Message Recieved", "Serverside");
				ObjectInputStream ois = new ObjectInputStream(temmpsock.getInputStream());
				try 
				{
					MyMessage m = (MyMessage) ois.readObject();
					Requestport = m.portnum;
					Log.v("Recived Message from", m.portnum);
					if(m.type == MyMessage.INSERT_PUT)
					{
						insert_put(m);
					}
					else if(m.type == MyMessage.QUERY_REQUEST)
					{
						query(m);
					}
					else if(m.type == MyMessage.QUERY_7)
					{
						query_7(m);
					}

					else if(m.type == MyMessage.DELETE_REQUEST)
					{
						if(m.key.equals("*"))
						{
							delete(m);
						}
						else
						{
							keydelete(m);
						}
					}
					/*else if(m.type == 9)
					{
						if(m.msgcontent.equals("true"))
						{
							SimpleDynamoProvider.recoverwait = true;		// Making inserts and query wait
						}
						else if(m.msgcontent.equals("false"))
						{
							SimpleDynamoProvider.recoverwait = false;		// Resuming inserts and query
						}
					}*/
					else
					{
						Log.v("Invalid Type","Invalid Type");
					}

					Log.v("Pred: Succ", fifthnode+":"+secondnode);
				} 	
				catch (ClassNotFoundException e) {
					Log.v("Exception:", e.getMessage());
				}
				temmpsock.close();
			}catch(IOException ex){
				Log.v("Message receive failed:", ex.getMessage());
			}

		}
	}

	private void insert_put(MyMessage m)
	{
		long row;
		if(m.Coordinator .equals(SimpleDynamoProvider.myport))
		{
			if (m.is_success == true)
				Log.v(" Insert successful:"+m.key,"at: "+m.portnum);
			else
				Log.v("Insert Failed:"+m.key,"at: "+m.portnum);

			SimpleDynamoProvider.insertmap.put(m.key, false);
		}
		else
		{
			Log.v("Insert In: ",SimpleDynamoProvider.myport);
			if(!(SimpleDynamoProvider.keymap.containsKey(m.key)))		// Lock doesnt exists in the map
			{
				SimpleDynamoProvider.keymap.put(m.key, new Object());
			}
			synchronized (SimpleDynamoProvider.keymap.get(m.key)) 
			{
				ContentValues cv = new ContentValues();
				cv.put(SimpleDynamoProvider.KEY_FIELD, m.key);
				cv.put(SimpleDynamoProvider.VALUE_FIELD, m.value);
				SQLiteDatabase database = SimpleDynamoProvider.dbHelper.getWritableDatabase();
				row = database.insertWithOnConflict(SimpleDynamoProvider.DBHelper.TABLE_NAME,null,cv, SQLiteDatabase.CONFLICT_REPLACE);
				Log.v("M.Key Inserted["+String.valueOf(row)+"]:sucess", m.key);
			}
			if(row != 0)
			{
				MyMessage msg = new MyMessage(4, SimpleDynamoProvider.portStr, m.Coordinator, null, null, null, m.key, null, m.Coordinator, true, null);
				Thread t = new Thread(new ClientTask(msg));
				t.start();
			}
			else
			{
				MyMessage msg = new MyMessage(4, SimpleDynamoProvider.portStr, m.Coordinator, null, null, null, m.key, null, m.Coordinator, false, null);
				Thread t = new Thread(new ClientTask(msg));
				t.start();
			}
		}	
	}


	private void query_7(MyMessage m) 
	{
		if(m.Coordinator.equals(SimpleDynamoProvider.myport))
		{
			if(m.is_success == true)
			{
				String feild[]={SimpleDynamoProvider.KEY_FIELD,SimpleDynamoProvider.VALUE_FIELD};
				MatrixCursor mcursor= new MatrixCursor(feild);
				String str[] = m.msgcontent.split(",");
				mcursor.addRow(str);
				SimpleDynamoProvider.querywaitmap.put(m.key, mcursor);
				SimpleDynamoProvider.insertmap.put(m.key, false);

			}
			else
			{
				SimpleDynamoProvider.querywaitmap.put(m.key, null);
				SimpleDynamoProvider.insertmap.put(m.key, false);
			}
		}
		else
		{
			boolean check = SimpleDynamoProvider.checkForTables();
			Cursor cursor;
			if (check == true)
			{
				Log.v("Querying:", m.key);
				Log.v("I am:", SimpleDynamoProvider.myport);
				if(!(SimpleDynamoProvider.keymap.containsKey(m.key)))		// Lock doesnt exists in the map
				{
					SimpleDynamoProvider.keymap.put(m.key, new Object());
				}
				synchronized (SimpleDynamoProvider.keymap.get(m.key)) 
				{
					SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
					qb.setTables(SimpleDynamoProvider.DBHelper.TABLE_NAME);
					SQLiteDatabase db = SimpleDynamoProvider.dbHelper.getWritableDatabase();
					cursor = qb.query(db,null,DBHelper.KEY+"="+"'"+m.key+"'", null,null,null,null);
				}
				if(cursor.getCount()>0){	//success
					cursor.moveToFirst();    			
					m.msgcontent = cursor.getString(0)+","+cursor.getString(1);
					Log.v("Key Found and now passing to : " , m.Coordinator);
					MyMessage msg1 = new MyMessage(7, SimpleDynamoProvider.portStr, m.Coordinator, null, null,null, m.key, null, m.Coordinator, true,m.msgcontent);
					Thread t = new Thread( new ClientTask(msg1));
					t.start();

				}
				else	// Failure
				{
					MyMessage msg1 = new MyMessage(7, SimpleDynamoProvider.portStr, m.Coordinator, null, null,null, m.key, null, m.Coordinator, false,m.msgcontent);
					Thread t = new Thread( new ClientTask(msg1));
					t.start();
				}
			}
			else	//empty table
			{
				m.msgcontent += ""+m.msgcontent;
				MyMessage msg1 = new MyMessage(7, SimpleDynamoProvider.portStr, m.Coordinator, null, null,null, m.key, null, m.Coordinator, false,m.msgcontent);
				Thread t = new Thread( new ClientTask(msg1));
				t.start();
			}
		}
	}


	private void query(MyMessage m)
	{
		if(m.Coordinator.equals(SimpleDynamoProvider.myport))
		{
			String str[] = m.msgcontent.split(",");
			for(int i=0;i<str.length-1;i=i+2)
			{
				SimpleDynamoProvider.querymap.put(str[i],str[i+1]);
				SimpleDynamoProvider.insertmap.put(m.key, false);
			}
		}

		else
		{
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(DBHelper.TABLE_NAME);
			SQLiteDatabase db = SimpleDynamoProvider.dbHelper.getWritableDatabase();
			Cursor cursor = qb.query(db,null,null, null,null,null,null);

			String mcursor = "";
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
			{
				mcursor += cursor.getString(0)+","+cursor.getString(1)+",";
			}

			MyMessage msg3 = new MyMessage(5,SimpleDynamoProvider.portStr,m.Coordinator,null,null,null,"*",null,m.Coordinator,true,mcursor);
			Thread t = new Thread( new ClientTask(msg3));
			t.start();
		}
	}

	private void keydelete(MyMessage m) {

		if(m.Coordinator.equals(SimpleDynamoProvider.myport))
		{
			SimpleDynamoProvider.tempkeydelete = Integer.parseInt(m.msgcontent);
			SimpleDynamoProvider.insertmap.put(m.key, false);
		}
		else if(m.is_success == false)
		{
			boolean check = SimpleDynamoProvider.checkForTables();
			if(check == true)
			{
				// check and delete from data base
				SQLiteDatabase db = SimpleDynamoProvider.dbHelper.getWritableDatabase(); 
				int row = db.delete(DBHelper.TABLE_NAME,"key = '"+m.key+"'",null);
				Log.v("Deleted key:", String.valueOf(row));
				if(row>0)
				{
					m.msgcontent = String.valueOf(row);
					Log.v("Key Found and deleted:","Returning to coordinator");
					MyMessage msg1 = new MyMessage(6, SimpleDynamoProvider.portStr, m.Coordinator, 
							null, null,null, m.key, null, m.Coordinator, true,m.msgcontent);
					Thread t = new Thread( new ClientTask(msg1));
					t.start();
				}
				else			//send msg to coordinator that deleted rows = 0
				{
					m.msgcontent = String.valueOf("0");
					Log.v("Key not Found", "returning to coordinator");
					MyMessage msg1 = new MyMessage(6, SimpleDynamoProvider.portStr, m.Coordinator, 
							null, null,null, m.key, null, m.Coordinator, true,m.msgcontent);
					Thread t = new Thread( new ClientTask(msg1));
					t.start();
				}
			}
			else			// empty database nothing to delete
			{
				m.msgcontent = String.valueOf("0");
				Log.v("Key not Found", "returning to coordinator");
				MyMessage msg1 = new MyMessage(6, SimpleDynamoProvider.portStr, m.Coordinator, 
						null, null,null, m.key, null, m.Coordinator, true,m.msgcontent);
				Thread t = new Thread( new ClientTask(msg1));
				t.start();
			}
		}
	}


	private void delete(MyMessage m) {

		if(m.Coordinator.equals(SimpleDynamoProvider.myport))
		{
			SimpleDynamoProvider.finaldelete = Integer.parseInt(m.msgcontent);
			SimpleDynamoProvider.deletecheck = false;
		}
		else
		{	
			if(SimpleDynamoProvider.checkForTables() == true)
			{
				SQLiteDatabase db = SimpleDynamoProvider.dbHelper.getWritableDatabase();
				int row = db.delete(DBHelper.TABLE_NAME,null,null);
				Log.v("Deleted *:", String.valueOf(row));
				int totaldeleted =	row + Integer.parseInt(m.msgcontent);
				String mcursor = String.valueOf(totaldeleted);
				MyMessage msg3 = new MyMessage(6,SimpleDynamoProvider.portStr,secondnode,null,null,null,"*",null,m.Coordinator,false,mcursor);
				Thread t = new Thread( new ClientTask(msg3));
				t.start();
			}
			else
			{
				MyMessage msg3 = new MyMessage(6,SimpleDynamoProvider.portStr,secondnode,null,null,null,"*",null,m.Coordinator,false,m.msgcontent);
				Thread t = new Thread( new ClientTask(msg3));
				t.start();
			}
		}
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
			Log.v("Error in Join Nodes at:", SimpleDynamoProvider.myport);
		}
	}
}
