package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTask implements Runnable{
	
	private MyMessage msgs;
	
	public ClientTask(MyMessage msgs)
	{
		this.msgs = msgs;
	}
	
	@Override
	public void run() {
		try
		{
				System.out.println("Enter Client task key: "+msgs.key+" to: "+msgs.remotePort); //+msgs.key+"to
				Socket socket = new Socket(InetAddress.getByAddress(new byte[] 
						{ 10, 0, 2, 2 }),Integer.parseInt(msgs.remotePort));
				ObjectOutputStream msgbuffer = null;
				msgbuffer = new ObjectOutputStream(socket.getOutputStream()); 
				msgbuffer.writeObject(msgs);	//Send Request to Remote port
				msgbuffer.flush();
				msgbuffer.close();
				socket.close();
		}catch(Exception e){
			
			System.out.println("Client Task error: "+e.getMessage());
		}
		return;
	}
}