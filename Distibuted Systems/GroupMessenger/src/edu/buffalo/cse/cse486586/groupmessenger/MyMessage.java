package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

public class MyMessage implements Serializable{
	
	String message;
	String message_id;
	String message_order;
	int counter=0;
	
	public MyMessage(String message, String message_id, String message_order) {
		this.message = message;
		this.message_id = message_id;
		this.message_order = message_order;
	}
}
