package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.Serializable;

public class MyMessage implements Serializable{

	public static int INSERT_REQUEST=3;
	public static int INSERT_PUT=4;
	public static int QUERY_REQUEST=5;
	public static int DELETE_REQUEST=6;
	public static int QUERY_7 = 7;

	public int type;
	public String portnum;
	public String remotePort;
	public String predecessor;
	public String successor;
	public String successor_succ;
	public String value;
	public String key;
	public String Coordinator;
	public boolean is_success;
	public String msgcontent;

	MyMessage(int type,String portnum,String remotePort,
			String predecessor,String successor,String successor_succ, String key, String value, 
			String Coordinator,boolean is_success, String msgcontent)
			{
		this.type=type;
		this.portnum = portnum;
		this.remotePort=remotePort;
		this.predecessor=predecessor;
		this.successor=successor;
		this.successor_succ = successor_succ;
		this.key=key;
		this.value=value;
		this.Coordinator = Coordinator;
		this.is_success=is_success;
		this.msgcontent = msgcontent;

			}

}
