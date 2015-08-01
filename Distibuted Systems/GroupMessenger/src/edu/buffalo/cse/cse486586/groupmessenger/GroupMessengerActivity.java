package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import edu.buffalo.cse.cse486586.groupmessenger.R;
import edu.buffalo.cse.cse486586.groupmessenger.GroupMessengerActivity;
//import edu.buffalo.cse.cse486586.groupmessenger.GroupMessengerActivity.ClientTask;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class GroupMessengerActivity extends Activity {

	static final String TAG = GroupMessengerActivity.class.getSimpleName();
	static final int SERVER_PORT = 10000;
	static final int sequencer = 11108;
	String[] remotePort = { "11108", "11112", "11116", "11120", "11124" };
	Queue<MyMessage> holdback_queue = new LinkedList<MyMessage>();
	Queue<MyMessage> order_queue = new LinkedList<MyMessage>();
	private ContentResolver mContentResolver;
	private Uri mUri;
	// private GroupMessengerProvider mcontentprovider;
	private ContentValues mcontentValues;
	int idcounter = 0;
	int sg = 0;
	int rg = 0;
	String myPort = null;

	private Uri buildUri(String scheme, String authority) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_messenger);
		mUri = buildUri("content",
				"edu.buffalo.cse.cse486586.groupmessenger.provider");

		TelephonyManager tel = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(
				tel.getLine1Number().length() - 4);
		myPort = String.valueOf((Integer.parseInt(portStr) * 2));

		// TODO
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setMovementMethod(new ScrollingMovementMethod());

		// Registers OnPTestClickListener for "button1" in the layout, which is
		// the "PTest" button.
		// OnPTestClickListener demonstrates how to access a ContentProvider.

		findViewById(R.id.button1).setOnClickListener(
				new OnPTestClickListener(tv, getContentResolver()));

		// TODO: You need to register and implement an OnClickListener for the
		// "Send" button.
		// In your implementation you need to get the message from the input box
		// (EditText)
		// and send it to other AVDs in a total-causal order.

		try {
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					serverSocket);
		} catch (IOException e) {
			System.out.println("Can't create a ServerSocket");
			return;
		}

		final EditText editText = (EditText) findViewById(R.id.editText1);

		findViewById(R.id.button4).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				/*
				 * If the key is pressed (i.e., KeyEvent.ACTION_DOWN) and it is
				 * an enter key (i.e., KeyEvent.KEYCODE_ENTER), then we display
				 * the string. Then we create an AsyncTask that sends the string
				 * to the remote AVD.
				 */

				MyMessage msg = new MyMessage(editText.getText().toString()
						+ "\n", myPort + ":" + idcounter, "no");
				idcounter++;
				editText.setText(""); // This is one way to reset the input box.
				TextView localTextView = (TextView) findViewById(R.id.textView1);
				/*
				 * localTextView.append("\t" + msg.message); // This is one way
				 * to display a string. TextView remoteTextView = (TextView)
				 * findViewById(R.id.textView1); remoteTextView.append("\n");
				 */

				/*
				 * Note that the following AsyncTask uses
				 * AsyncTask.SERIAL_EXECUTOR, not AsyncTask.THREAD_POOL_EXECUTOR
				 * as the above ServerTask does. To understand the difference,
				 * please take a look at
				 * http://developer.android.com/reference/android
				 * /os/AsyncTask.html
				 */
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
						msg);
				return;
			}
		});

	}

	private class ClientTask extends AsyncTask<MyMessage, Void, Void> {

		@Override
		protected Void doInBackground(MyMessage... msgs) {
			try {

				// MyMessage my_msg = new MyMessage();
				for (int i = 0; i < 5; i++) {
					Socket socket = new Socket(
							InetAddress
									.getByAddress(new byte[] { 10, 0, 2, 2 }),
							Integer.parseInt(remotePort[i]));

					/*
					 * Here i am reading the input via the buffer and writing it
					 * to the buffer in the socket Then i am sending the data
					 * via the socket to the server
					 */
					ObjectOutputStream msgbuffer = null;
					msgbuffer = new ObjectOutputStream(socket.getOutputStream());
					msgbuffer.writeObject(msgs[0]);
					msgbuffer.flush();
					msgbuffer.close();
					socket.close();
				}
			} catch (Exception e) {
				System.out.println("ClientTask UnknownHostException");
			}

			return null;
		}
	}

	private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
		@Override
		protected Void doInBackground(ServerSocket... sockets) {
			ServerSocket serverSocket = sockets[0];
			/*
			 * Here i am accepting the socket connection and reading the data
			 * off the buffer in the socket and passing the data to the
			 * publishProgress() method to print the message on the UI
			 */
			while (true) {
				try {
					Socket temmpsock = serverSocket.accept();
					ObjectInputStream ois = new ObjectInputStream(
							temmpsock.getInputStream());
					MyMessage tempmsg = (MyMessage) ois.readObject();

					int tempport = Integer.parseInt(myPort);
					if (tempport == sequencer) {

						Log.v("XXXXXXXXXX", "I am the sequencer");

						if (tempmsg.message_order.equals("order message")) // Order
																			// message
																			// to
																			// the
																			// sequencer
						{
							// do Nothing
						} else // Normal message to the sequencer
						{
							String sg_str = Integer.toString(sg);
							mcontentValues = new ContentValues();
							mContentResolver = getContentResolver();
							mcontentValues.put("key", sg_str);
							mcontentValues.put("value", tempmsg.message);
							mContentResolver.insert(mUri, mcontentValues);
							publishProgress(sg_str + " : " + tempmsg.message_id
									+ " : " + tempmsg.message);
							MyMessage my_msg = new MyMessage(tempmsg.message,
									tempmsg.message_id, tempmsg.message_order);
							my_msg.counter = sg;
							my_msg.message_order = "order message"; // can edit
																	// for
																	// testing
							new ClientTask().executeOnExecutor(
									AsyncTask.SERIAL_EXECUTOR, my_msg);
							sg++;
						}
					} else // not a sequencer
					{

						Log.v("XXXXXXXXXXXX", "Not a sequencer");
						if (tempmsg.message_order.equals("order message")) // Non
																			// sequencer
																			// ordered
																			// message
						{
							order_queue.add(tempmsg); // Adding to the ordered
														// queue
							Log.v("XXXXXXXXX", "Got order message");
						} else // Normal message
						{
							holdback_queue.add(tempmsg); // adding to the
															// holdback queue
						}
						boolean check = true;
						Iterator<MyMessage> itr_order = order_queue.iterator();
						while (check && itr_order.hasNext()) {
							MyMessage first_order_msg = itr_order.next();
							Iterator<MyMessage> itr = holdback_queue.iterator();
							while (itr.hasNext()) {
								Log.v("XXXXXXXXX", "holdback");
								MyMessage holbackMessage = itr.next();
								if (((rg == first_order_msg.counter) && (first_order_msg.message_id
										.equals(holbackMessage.message_id))))

								{
									String rg_str = Integer.toString(rg);
									mcontentValues = new ContentValues();
									mContentResolver = getContentResolver();
									mcontentValues.put("key", rg_str);
									mcontentValues.put("value",
											holbackMessage.message); // Write
									// to
									// DB
									mContentResolver.insert(mUri,
											mcontentValues);
									publishProgress(rg_str + " : "
											+ holbackMessage.message_id + ":"
											+ holbackMessage.message);
									rg++;
									itr_order.remove();
									itr.remove();
									check = true;
									break;
								} else {
									check = false;
								}
							}
						}
					}

					ois.close();
					// temmpsock.close();

				} catch (IOException ex) {
					System.out.println("Message receive failed:");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		protected void onProgressUpdate(String... strings) {
			String strReceived = strings[0].trim();
			TextView remoteTextView = (TextView) findViewById(R.id.textView1);
			remoteTextView.append(strReceived + "\t\n");

			return;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
		return true;
	}
}