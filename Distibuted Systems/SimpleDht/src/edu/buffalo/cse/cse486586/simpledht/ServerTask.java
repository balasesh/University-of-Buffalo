package edu.buffalo.cse.cse486586.simpledht;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import edu.buffalo.cse.cse486586.simpledht.SimpleDhtProvider.ClientTask;
import edu.buffalo.cse.cse486586.simpledht.SimpleDhtProvider.DBHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class ServerTask extends AsyncTask<ServerSocket, String, Void> {

	String Requestport = null;	
	@Override
	protected Void doInBackground(ServerSocket... sockets) {
		ServerSocket serverSocket = sockets[0];

		while(true)
		{
			try
			{
				Socket temmpsock = serverSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(temmpsock.getInputStream());
				try {
					MyMessage m = (MyMessage) ois.readObject();
					Requestport = m.portnum;
					Log.v("Recived Message from", m.portnum);

					if(m.type == MyMessage.CONNECTION_REQUEST)
					{
						try {
							joinsection(m);
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(m.type == MyMessage.SET_REQUESTER)
					{
						setrequester(m);
					}
					else if(m.type == MyMessage.SET_PS_REQUEST)
					{
						set_ps(m);
					}
					else if(m.type == MyMessage.INSERT_REQUEST)
					{
						insert(m);
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

						//SimpleDhtProvider.mContentResolver.delete(SimpleDhtProvider.mUri, m.key, null);
					}
					else if(m.type == MyMessage.QUERY_REQUEST)
					{
						if(m.key.equals("*"))
						{
							query(m);
						}
						else
						{
							keyquery(m);
						}
					}
					else{
						System.out.println("Invalid Type");
					}
					publishProgress("< "+SimpleDhtProvider.predecessor+" : "+SimpleDhtProvider.successor+" >\n");
					Log.v("Pred: Succ", SimpleDhtProvider.predecessor+":"+SimpleDhtProvider.successor);

				} 	
				catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}catch(IOException ex){
				System.out.println("Message receive failed:");
			}

		}
	}


	private void keydelete(MyMessage m) {

		if(m.Startport.equals(SimpleDhtProvider.myport))
		{
			SimpleDhtProvider.keydelete = Integer.parseInt(m.mcursor);
			SimpleDhtProvider.flag = false;
		}
		else if(m.is_success == false)
		{

			SQLiteDatabase db = SimpleDhtProvider.dbHelper.getWritableDatabase(); 
			int row = db.delete(SimpleDhtProvider.dbHelper.TABLE_NAME,"key = '"+m.key+"'",null);
			Log.v("Deleted key:", String.valueOf(row));

			if(row>0)
			{
				m.mcursor = String.valueOf(row);
				System.out.println("Key Found and deleted moving to succ");
				MyMessage msg1 = new MyMessage(5, SimpleDhtProvider.portStr, SimpleDhtProvider.successor, 
						null, null, m.key, null, m.Startport, true,m.mcursor);
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
			}
			else
			{
				System.out.println("Key $Not$ Found and now passing to succ");
				MyMessage msg1 = new MyMessage(5, SimpleDhtProvider.portStr, SimpleDhtProvider.successor, 
						null, null, m.key, null, m.Startport, false,m.mcursor);
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
			}
		}
		else
		{
			System.out.println("Is_success is true so passing to Succ");
			MyMessage msg1 = new MyMessage(5,SimpleDhtProvider.portStr, SimpleDhtProvider.successor, 
					null, null, m.key, null, m.Startport,true,m.mcursor);
			new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
		}

	}


	private void delete(MyMessage m) {
		// TODO Auto-generated method stub
		if(m.Startport.equals(SimpleDhtProvider.myport))
		{
			SimpleDhtProvider.finaldelete = Integer.parseInt(m.mcursor);
			SimpleDhtProvider.deletecheck = false;
		}
		else
		{	
			if(SimpleDhtProvider.checkForTables() == true)
			{
				SQLiteDatabase db = SimpleDhtProvider.dbHelper.getWritableDatabase();
				int row = db.delete(SimpleDhtProvider.dbHelper.TABLE_NAME,null,null);
				Log.v("Deleted *:", String.valueOf(row));
				int totaldeleted =	row + Integer.parseInt(m.mcursor);
				String mcursor = String.valueOf(totaldeleted);
				MyMessage msg3 = new MyMessage(5,SimpleDhtProvider.portStr,SimpleDhtProvider.successor,null,null,"*",null,m.Startport,false,mcursor);
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);	
			}
			else
			{
				//send message as it is
				MyMessage msg3 = new MyMessage(5,SimpleDhtProvider.portStr,SimpleDhtProvider.successor,null,null,"*",null,m.Startport,false,m.mcursor);
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);				
			}

		}

	}


	private void keyquery(MyMessage m) {
		// TODO Auto-generated method stub

		if(m.Startport.equals(SimpleDhtProvider.myport))
		{
			//write m.cursor to finalcursor
			String feild[]={SimpleDhtProvider.KEY_FIELD,SimpleDhtProvider.VALUE_FIELD};
			MatrixCursor mcursor= new MatrixCursor(feild);
			String str[] = m.mcursor.split(",");
			mcursor.addRow(str);
			SimpleDhtProvider.finalcursor = mcursor;
			SimpleDhtProvider.wait = 1;
		}
		else if(m.is_success == false)
		{
			boolean check = SimpleDhtProvider.checkForTables();
			if (check == true)
			{
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
				qb.setTables(DBHelper.TABLE_NAME);
				SQLiteDatabase db = SimpleDhtProvider.dbHelper.getWritableDatabase();
				Cursor cursor = qb.query(db,null,SimpleDhtProvider.dbHelper.KEY+"="+"'"+m.key+"'", null,null,null,null);
				if(cursor.getCount()>0)	//success
				{
					cursor.moveToFirst();    			
					m.mcursor = cursor.getString(0)+","+cursor.getString(1);
					System.out.println("Key Found and now passing to succ");
					MyMessage msg1 = new MyMessage(4, SimpleDhtProvider.portStr, SimpleDhtProvider.successor, 
							null, null, m.key, null, m.Startport, true,m.mcursor);
					//cursor.close();
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
				}
				else	// Failure
				{
					System.out.println("Key $Not$ Found and now passing to succ");
					MyMessage msg1 = new MyMessage(4, SimpleDhtProvider.portStr, SimpleDhtProvider.successor, 
							null, null, m.key, null, m.Startport, false,m.mcursor);
					//cursor.close();
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
				}
			}
			else
			{
				m.mcursor += ""+m.mcursor;
				MyMessage msg1 = new MyMessage(4, SimpleDhtProvider.portStr, SimpleDhtProvider.successor, 
						null, null, m.key, null, m.Startport, false,m.mcursor);
				//cursor.close();
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
			}

		}
		else
		{
			System.out.println("Is_success is true so passing to Succ");
			MyMessage msg1 = new MyMessage(4,SimpleDhtProvider.portStr, SimpleDhtProvider.successor, 
					null, null, m.key, null, m.Startport, true,m.mcursor);
			new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
		}
	}


	private void insert(MyMessage m) {
		ContentValues cv = new ContentValues();
		cv.put(SimpleDhtProvider.KEY_FIELD, m.key);
		cv.put(SimpleDhtProvider.VALUE_FIELD, m.value);
		SimpleDhtProvider.mContentResolver.insert(SimpleDhtProvider.mUri, cv);
	}

	private void query(MyMessage m)
	{
		if(m.Startport.equals(SimpleDhtProvider.myport))
		{
			if(m.mcursor.length()>=1)
			{
			String feild[]={SimpleDhtProvider.KEY_FIELD,SimpleDhtProvider.VALUE_FIELD};
			MatrixCursor mcursor= new MatrixCursor(feild);
			String str[] = m.mcursor.split(",");
			for(int i=0;i<str.length;i=i+2)
			{
				String entry[]={str[i],str[i+1]};
				mcursor.addRow(entry);
				mcursor.moveToLast();
				Log.e("1 Row Added",String.valueOf(mcursor.getPosition()));
			}
			//str = null;
			SimpleDhtProvider.gdfinalcursor = mcursor;
			SimpleDhtProvider.gdwait = 1;
			}
			else
			{
				SimpleDhtProvider.gdfinalcursor = null;
				SimpleDhtProvider.gdwait = 1;
			}
		}
		else
		{
			boolean check = SimpleDhtProvider.checkForTables();
			if(check == true)
			{
				Log.v("Enter * Server Query Search", "Check:true");
				SQLiteDatabase db = SimpleDhtProvider.dbHelper.getReadableDatabase();
				Cursor cursor = db.rawQuery("SELECT * FROM "+SimpleDhtProvider.dbHelper.TABLE_NAME, null);
				String mcursor = "";
				for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
				{
					mcursor += cursor.getString(0)+","+cursor.getString(1)+",";
				}
				m.mcursor += mcursor;
				MyMessage msg3 = new MyMessage(4,SimpleDhtProvider.portStr,SimpleDhtProvider.successor,
						null,null,"*",null,m.Startport,false,m.mcursor);
				//cursor.close();
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);
			}
			else
			{
				m.mcursor += ""+m.mcursor;
				Log.v("Enter * Server Query Search", "Check:false");
				MyMessage msg3 = new MyMessage(4,SimpleDhtProvider.portStr,SimpleDhtProvider.successor,
						null,null,"*",null,m.Startport,false,m.mcursor);
				//cursor.close();
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);
			}
		}
	}

	private void joinsection (MyMessage m) throws NoSuchAlgorithmException
	{
		/*String succ = null;*/
		String pred = null;
		String me = null;
		String requestor = null;
		String requestport = null;
		String successor = null;
		String pmyportno = null;
		String predecessor = null;
		String myportno = null;

		requestor = SimpleDhtProvider.genHash(m.portnum);
		pred = SimpleDhtProvider.genHash(String.valueOf(((Integer.parseInt
				(SimpleDhtProvider.predecessor))/2)));// Hashed predecessor Port number
		me = SimpleDhtProvider.genHash(SimpleDhtProvider.portStr);

		myportno = SimpleDhtProvider.portStr;
		pmyportno = SimpleDhtProvider.getPortnum(myportno);		//11108 format
		requestport = SimpleDhtProvider.getPortnum(m.portnum);		// Req Port 11108 format
		successor = SimpleDhtProvider.successor;					// Successor 11108 Format
		predecessor = SimpleDhtProvider.predecessor;				// Predecessor 11108 Format

		/*case1*/
		if(SimpleDhtProvider.predecessor.equals("0") && SimpleDhtProvider.successor.equals("0") 
				&& SimpleDhtProvider.portStr.equals("5554"))
		{
			//First Avd connection
			Log.v("Enter", "Case1");
			String portnum = SimpleDhtProvider.getPortnum(SimpleDhtProvider.portStr);
			Log.v("Portnum", portnum);
			MyMessage msg1 = new MyMessage(1,myportno,requestport,portnum,portnum,null,null,null,false,null);
			new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);
			SimpleDhtProvider.predecessor = requestport;
			SimpleDhtProvider.successor = requestport;
			//break;
		}

		else if(((requestor.compareTo(pred)>0) && (requestor.compareTo(me)<= 0))
				||
				((me.compareTo(pred)<0) && ((requestor.compareTo(pred)>0) ||(requestor.compareTo(me)<0))))
		{
			//join between pred and me
			//Send Requestor message to join P = Pred and s = me
			//Send message to pred s = req
			//me.pred = req
			Log.v("Enter", "Case 2");
			MyMessage msg1 = new MyMessage(1,myportno,requestport,predecessor,pmyportno,null,null,null,false,null);
			new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg1);

			MyMessage msg2 = new MyMessage(2,myportno,predecessor,null,requestport,null,null,null,false,null);
			new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg2);

			SimpleDhtProvider.predecessor = requestport;
		}

		else
		{
			//send message to successor for join
			MyMessage msg3 = new MyMessage(0,Requestport,successor,null,null,null,null,null,false,null);
			new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg3);
		}
	}

	private void setrequester(MyMessage m) {
		SimpleDhtProvider.successor = m.successor;
		SimpleDhtProvider.predecessor = m.predecessor;
	}

	private void set_ps(MyMessage m) {
		SimpleDhtProvider.successor = m.successor;
	}

	protected void onProgressUpdate(String... strings ){
		super.onProgressUpdate(strings[0]);
		SimpleDhtActivity.remotetextView.append(strings[0] + "\n");
		Log.v("Server reading message", strings[0]);
		return;
	}
}