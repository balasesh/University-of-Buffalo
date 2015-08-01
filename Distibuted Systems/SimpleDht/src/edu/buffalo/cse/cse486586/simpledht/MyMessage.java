package edu.buffalo.cse.cse486586.simpledht;

import java.io.Serializable;

import android.database.Cursor;
import android.database.MatrixCursor;

public class MyMessage implements Serializable{
	
	public static int CONNECTION_REQUEST=0;
	public static int SET_REQUESTER=1;
	public static int SET_PS_REQUEST=2;
	public static int INSERT_REQUEST=3;
	public static int QUERY_REQUEST=4;
	public static int DELETE_REQUEST=5;
	
	public int type;
	public String portnum;
	public String remotePort;
	public String predecessor;
	public String successor;
	public String value;
	public String key;
	public String Startport;
	public boolean is_success;
	public String	mcursor;
	
	MyMessage(int type,String portnum,String remotePort,
			String predecessor,String successor,String key, String value, 
			String Startport,boolean is_success, String mcursor)
	{
		this.type=type;
		this.portnum = portnum;
		this.remotePort=remotePort;
		this.predecessor=predecessor;
		this.successor=successor;
		this.key=key;
		this.value=value;
		this.Startport = Startport;
		this.is_success=is_success;
		this.mcursor = mcursor;
		
	}
	
}