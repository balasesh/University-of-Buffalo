This program is created by Bala Seshadri Sura (UBID 50097470) Email: balasesh@buffalo.edu. 

All external references have been cited with the code function. This programs gives a demonstration of how a distace vector algorithim works.

The program starts once we have the "./a.out" command executed. Once we enter the program, a "$" symbol appears. We need to type the server command into the prompt in the follwoing format, server -t <topology-file-name> -i <routing-update-interval>. Once this is given, the program starts running. The filename should be readable text file. The file should be present in the location of the ".c file" program itself as the default path is specified to be the same place as the code.

For eg: server -t abc.txt -i 50

The first function to run in the program is the readline function[LINE NUMBER 69] that reads the data from the file line by line and divides each line in words(using the trim function, on line 90). from here the data is saved into a globally declared singly linked list[LINE NUMBER 39]. Once this is done the program checks the number of servers and neighbors and saves them into variables. Also 2 list pointers are made to point at the start of the server and the neighbor nodes. Then the program checks the ID that has been assigned to the local system and retrives its IP and port number. 

We now have all the information we need to start off the calculations and DV algorithim. To save these calculation 2 matrices have been created name "matrix"[LINE NUMBER 497] and "routematrix"[LINE NUMBER 636] and the data from the list and file are added to the respective matrices. Using the routematrix a packet has been created[using the "createmessage" function on LINE NUMBER 442] that will be circulated to all the neighbours. The message is a charector string that has all the details of the IP ID Cost and Port of the neighbours and the neighbors' neighbors. The message is used to update the routing table. The table is updated using the Distance Vector algorithim and the Bellmanford equation[LINE NUMBER 612].

After all this has successfully executed the recv conn will be called which holds the select statement. Here the initial interval given in the 'server' command, will be used to send the created packets in time intervals. In the recvconn function, a file descriptor is created and waiting for any incoming message or any input from the keyboard. Here all the command are handled and waiting for execution.

COMMANDS:

1) update <server-ID1> <server-ID2> <cost> : The update command takes the local id as argument 1, the argument 2 must be a neighbour, the cost is specified in the third argument. If any of the 3 above conditions are not satisfied necessary error will be thrown.

2) step : The Step command send the packets to all its neighbours and produces a response as to who all it has sent the data to.

3) packets : The packets command tell us how many packets have been received by the server. Once this command is called the counter is set back to 0 and the count starts again.

4) display : The display command shows the data in the routing table. It shows the latest updated routing table and as and when the input data message changes the routing table values change and if the table values change so will the message as the messages packets are created using the routing table.

5) disable <server-ID> : This disables the function of sending and recieving the packets of the specified serverID and updates the routing table accordingly. 

6) crash : This command is used to completly crash the system. This is done by calling a infite while loop so that the program crashes as intended.

All command have been updated with appropriate success messages and error messages.
