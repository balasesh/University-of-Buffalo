/* Code by Bala Seshadri Sura -- All citation have been provided as comments to the starting of the function */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#define BUFSIZE 1024

char localip[16];
char localid[5];
char localport[5];
int neighbours = 0;
int servers = 0;
int line =0;
char buffer[500];       //message buffer
int matrix[50][50];
int interval=0;
int routematrix[5][5];


struct node
{
	int linenum;
        int crashcount;
        int crashflag;
	char *word[3]; //word[50][3]
	struct node *next;

};

typedef struct node NODE;
NODE *serv=NULL;
NODE *neigh=NULL;
NODE *first=NULL;

void addline(char args[50][50]) // adds the file details to a list
{
	int p=0;
	NODE *cur=NULL;
	NODE *temp=NULL;
	temp=(NODE *)malloc(sizeof(NODE));
	line++;
	temp->linenum=line;
        
	for(p=0;p<3;p++)
	{
		temp->word[p]= (char *)malloc(sizeof(args[p]));
		strcpy(temp->word[p],args[p]);
	}

	if(!first)
	{
		first = temp; 
		first->next = NULL;					
	}
	else
	{
		cur = first;
		while(cur->next)
		{
			cur = cur->next;
		}
		cur->next = temp;
	}
}

int readline ( char filename[50] )		//http://www.daniweb.com/software-development/c/code/216411/reading-a-file-line-by-line
{
	FILE *file = fopen ( filename, "r" );
	if ( file != NULL )
   	{
      		char line [ 128 ]; // or other suitable maximum line size 
      		while( fgets ( line, sizeof line, file ) != NULL ) // read a line 
      		{
        		trim(line);
      		}
      		fclose ( file );
   	}
   	else
   	{
      		perror ( filename ); // why didn't the file open? 
                return;
   	}
	return 0;
}


trim(char input[])			//http://cboard.cprogramming.com/c-programming/128268-split-sentence-into-words.html
{

	char args[50][50];
	int a = 0;
	int i = 0;
	int index = 0;
	
	while (input[a] != '\0') 
	{
		if (input[a] == ' ' || input[a] == '\n' || input[a]=='\r') 
		{ 
			args[index][i] = '\0'; 
			i=0; a++; index++; 
		} // SPACE detected or newline detected
 
		args[index][i++] = input[a++];
	}
	args[index][i] = '\0';

	addline(args);
	return 0;
}

checkserver()
{
	NODE *temp = NULL;
	temp = first;
	int count=0;

	count =atoi(temp->word[0]);

	if((temp->linenum == 1)&&(temp->word!=NULL))
	{
		if(count == 0)
		{
			servers=0;
			printf("No Servers are available\n");
		}
		else if(count == 1)
		{
			servers=1;
		}
		else if(count == 2)
		{
			servers=2;
		}
		else if(count == 3)
		{
			servers=3;
		}
		else if(count == 4)
		{
			servers=4;
		}
		else if(count == 5)
		{
			servers=5;
		}
		else
			printf("Error Servers\n");
		
	}
	else
		printf("No Servers available\n");
}

checkneighbour()
{
	NODE *temp;
	temp=first;
	temp=temp->next;
	int count = 0;

	count =atoi(temp->word[0]);

	if((temp->linenum == 2)&&(temp->word!=NULL))
	{
		if(count == 0)
		{
			neighbours=0;
		}
		else if(count == 1)
		{
			neighbours=1;
		}
		else if(count == 2)
		{
			neighbours=2;
		}
		else if(count == 3)
		{
			neighbours=3;
		}
		else if(count == 4)
		{
			neighbours=4;
		}
		else 
			printf("Error Neighbours\n");
	}
	else
		printf("No neighbours\n");
}

int recvconn()
{
	int sockfd;
	char command[50];
	fd_set readfds;
	fd_set master;
        char command1[50];
        char command2[50];
        char updateval[100];
	int fdmax=0;
        int temp_sock=0;
	int portno=0; 
 	int clientlen;
	struct sockaddr_in serveraddr;
	struct sockaddr_in clientaddr;
	struct hostent *hostp; 
        int packetcount=0;
	char buf[BUFSIZE]; 
	char *hostaddrp; 
        int timeout = 0;
	int optval; 
	int n;
        int disableid = 0;
        int selectvalue;
        struct timeval tv;
        tv.tv_sec = interval;
        tv.tv_usec = 0;
	portno = atoi(localport);
        
        sockfd = socket(AF_INET, SOCK_DGRAM, 0);
	if (sockfd < 0) 
		perror("ERROR opening socket");

	optval = 1;
	setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR,(const void *)&optval , sizeof(int));

  
	bzero((char *) &serveraddr, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = htonl(INADDR_ANY);
	serveraddr.sin_port = htons((unsigned short)portno);

	if (bind(sockfd, (struct sockaddr *) &serveraddr, sizeof(serveraddr)) < 0) 
		perror("ERROR on binding");

	clientlen = sizeof(clientaddr);
	FD_ZERO(&master);
	FD_ZERO(&readfds);
	FD_SET(fileno(stdin), &master);
	FD_SET(sockfd, &master);
	fdmax = sockfd;
        
        
        if(temp_sock!=0)
	{
		if(temp_sock>fdmax)
		{
			FD_SET(temp_sock,&master);
			fdmax=temp_sock;
			fflush(stdout);
			temp_sock=0;
		}
	}

	for(;;)
	{
		
                printf("Server%d>>",atoi(localid));
		fflush(stdout);
		readfds=master;
		selectvalue = select(fdmax+1,&readfds,NULL,NULL,&tv);
		if(selectvalue == -1)
		{
			perror("select");
			continue;
		}
                else if(selectvalue == 0)
                {
                        tv.tv_sec = interval;
                        tv.tv_usec = 0;         //{ interval, 0 };
                        messenger();            //send the packets periodically
                        timeout++;
                        if(timeout == 3)
                        {
                            timeout = 0;
                            disable();
                        }
                }
                else
                {
                   if(FD_ISSET(sockfd,&readfds))
		   {
			bzero(buf, BUFSIZE);			
                        n = recvfrom(sockfd, buf, BUFSIZE, 0,(struct sockaddr *) &clientaddr, &clientlen);
			if (n < 0)
				perror("ERROR in recvfrom");
                        
                        packetcount++;

			hostp = gethostbyaddr((const char *)&clientaddr.sin_addr.s_addr,sizeof(clientaddr.sin_addr.s_addr), AF_INET);
			if (hostp == NULL)
				perror("ERROR on gethostbyaddr");
			hostaddrp = inet_ntoa(clientaddr.sin_addr);
			if (hostaddrp == NULL)
				perror("ERROR on inet_ntoa\n");
			printf("Received packet from %s (%s)\n",hostp->h_name, hostaddrp);
                        messagereader(buf);
                        			
		   }
		   if(FD_ISSET(fileno(stdin),&readfds))
		   {
                        scanf("%s",command);
			if (strcmp(command, "update")==0)
			{
				scanf("%s %s %s",command1,command2,updateval);
                                //printf("The values of command are: %s\t %s\t %s\t",command1,command2,updateval);
                                temp_sock = update(command1,command2,updateval);
				if(temp_sock== -1)
                                {
                                    printf("Update failed\n");
                                    continue;
                                }
                                printf("Update: SUCCESS\n");
                                
                        }
			else if(strcmp(command, "step")==0)
			{
				temp_sock = step();
                                if (temp_sock == -1)
                                {
                                    printf("Step failed\n");
                                    continue;
				}
                                printf("Step: SUCCESS\n");
                                
			}
			else if(strcmp(command, "crash")==0)
			{
                            printf("Crash: SUCCESS\n");
                            printf("Your system has crashed......\n");
                            close(sockfd);
                            while(1){}
			}
			else if(strcmp(command, "packets")==0)
			{
                            printf("Packets: SUCCESS\n");
                            printf("The number of packets received: %d\n",packetcount);
                            packetcount = 0;
			}
			else if(strcmp(command, "disable")==0)
			{
                            scanf("%d",&disableid);
                            calldisable(disableid);
			    printf("Disable: SUCCESS\n");
                            
			}
                        else if(strcmp(command, "display")==0)
			{
				display();
                                printf("Display: SUCCESS\n");
			}
                        else
                            printf("Command does not exist please try again\n");
		   }
                }
	}
}

calldisable(int disableid)
{
    NODE *temp=NULL;
    temp = neigh;
    while(temp !=NULL)
    {
        if(atoi(temp->word[1])==disableid)
        {
            temp->crashflag = 0;
        }
        temp= temp->next;
    }
}

disable()
{
    NODE *temp = NULL;
    temp = neigh;
    while (temp!=NULL)
    {
        if(temp->crashcount == 0)
        {
                printf("Neighbor disconnected is:%s", temp->word[1]);
                update(localid,temp->word[1],"inf");
        }
        else if(temp->crashcount > 0)
        {
                temp->crashcount = 0;
        }
        
        temp= temp->next;   
    }
}

int sendconn(char *ipadr, char MYPORT[], char *buff)		//http://stackoverflow.com/questions/13316775/udp-client-server-program-c
{
	int sockfd, rc;
        struct sockaddr_in serveraddr;
	struct hostent *hostp;
        int serveraddrlen = sizeof(serveraddr);
	if((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) 
	{
	        perror("UDP Client - socket() error");
	        exit(-1);
	}

	memset(&serveraddr, 0x00, sizeof(struct sockaddr_in));
	serveraddr.sin_family = AF_INET;
        serveraddr.sin_addr.s_addr = inet_addr(ipadr);
	serveraddr.sin_port = htons(atoi(MYPORT));

	hostp = gethostbyname(ipadr);
	if(hostp == (struct hostent *)NULL) 
	{
	        printf("HOST NOT FOUND --> ");
	        printf("h_errno = %d\n", h_errno);
	        exit(-1);
	}
	
	memcpy(&serveraddr.sin_addr, hostp->h_addr, sizeof(serveraddr.sin_addr));
	rc = sendto(sockfd, buff, 1024, 0, (struct sockaddr *)&serveraddr, serveraddrlen);
	if(rc < 0) 
	{
	        perror("Sendto() error");
	        close(sockfd);
	        exit(-1);
	}
        close(sockfd);
}

checklocalid()
{    
    if(strcmp(neigh->word[0],"1")==0)
        strcpy(localid,neigh->word[0]);
    else if(strcmp(neigh->word[0],"2")==0)
        strcpy(localid,neigh->word[0]);
    else if(strcmp(neigh->word[0],"3")==0)
        strcpy(localid,neigh->word[0]);
    else if(strcmp(neigh->word[0],"4")==0)
        strcpy(localid,neigh->word[0]);
    else if(strcmp(neigh->word[0],"5")==0)
        strcpy(localid,neigh->word[0]);
}

checklocalinfo()
{
    NODE *temp= NULL;
    temp=serv;
    while(temp!= neigh)// the temp node is at the start of the servers where id = 1
    {
        if(strcmp(temp->word[0],localid)==0)
        {
            strcpy(localip, temp->word[1]);
            strcpy(localport, temp->word[2]);
            printf("Local Server Details are:%s\t%s\t%s\n",localid,localport,localip);
            temp=temp->next;
        }              
        else
            temp=temp->next;
    }
}

markserv()
{
    serv=first;
    serv=serv->next;
    serv=serv->next;//serv pointing at server 1 node
}

markneighbour()
{
    int i=0;
    NODE *temp = NULL;
    neigh=first;
    neigh=neigh->next;
    neigh=neigh->next;//pointing to starting of servers
    for(i=0;i<servers;i++)//traversing to end of servers
    {
        neigh=neigh->next;//pointing to starting of neighbour 1 node
    }
    temp = neigh;
    while(temp!=NULL)
    {
        temp->crashcount =1;
        temp->crashflag = 1;
        temp = temp->next;
    }
    
}

createmessage()
{
        NODE *temp = NULL;
        NODE *current = NULL;
        current = neigh;     
        int i=0;
        int j=0;
        char tempbuff[500];
        char tempcost[5];
        bzero(buffer,sizeof(buffer));
        bzero(tempbuff,sizeof(tempbuff));
        printf("\n");
        sprintf(tempbuff,"%d",neighbours);
        strcpy(buffer,tempbuff);
        strcat(buffer," ");
        strcat(buffer, localid);
        strcat(buffer," ");
        strcat(buffer,localport);
        strcat(buffer," ");
        strcat(buffer,localip);
        strcat(buffer," ");
        temp=serv;
        for(i=0;i<servers;i++)
        {
            if(i == atoi(localid) -1)
            {
                for(j=0;j<servers;j++)
                {
                    while(temp!=neigh)
                    {
                        if(routematrix[j][0] == atoi(localid))
                            temp = temp->next;
                        else if(routematrix[j][0] == atoi(temp->word[0]))
                        {
                                sprintf(tempcost,"%d",routematrix[j][2]);//atoi(localid)-1][atoi(temp->word[0])-1]);
                                strcat(buffer,temp->word[0]);
                                strcat(buffer," ");
                                strcat(buffer, temp->word[2]);
                                strcat(buffer," ");
                                strcat(buffer, temp->word[1]);
                                strcat(buffer," ");
                                strcat(buffer,tempcost);
                                strcat(buffer," ");
                                temp=temp->next;
                        }
                        else
                            temp=temp->next;
                    }
                    temp=serv;
                    }
                }
        } 
        
}

matrixinitialization()
{
    int p=0;
    int q=0;
	
        for(p=0;p<servers;p++)
	{
            for(q=0;q<servers;q++)
		{
                if(p==q)
                    matrix[p][q]=0;
                else
                    matrix[p][q]=99;
		}
        }
}

initialmatrix()
{
    int i=0;
    NODE *temp;
    for(i=1;i<=servers;i++)
    {
        temp = neigh;
        if(atoi(temp->word[0]) == i)
        {
            matrix[atoi(temp->word[0])][i]= 0;
            while(temp!=NULL)
            {
                matrix[i-1][atoi(temp->word[1])-1]=atoi(temp->word[2]);
                
                matrix[atoi(temp->word[1])-1][i-1]=atoi(temp->word[2]);
                temp= temp->next;
                
            }
        }
        
    }
}

messenger()             // function to send message periodically
{
    int i=0;
    char tempid[5];
    char *tempport;
    char *tempip;
    char *tempbuff;
    tempip = (char *)malloc(16);
    tempport = (char *)malloc(5);
    tempbuff= (char *)malloc(500);
    strcpy(tempbuff, buffer);
    NODE *temp = NULL;
    NODE *current = NULL;
    current = neigh;
    
    for (i=0;i<neighbours;i++)
    {
        bzero(tempid,sizeof(tempid));
        strcpy(tempid, current->word[1]);  //ID of current neighbour
        temp = serv;
        while(temp!= neigh)// the temp node is at the start of the servers where id = 1
        {
            if(strcmp(temp->word[0],tempid)==0)
            {
                strcpy(tempip, temp->word[1]);
                strcpy(tempport, temp->word[2]);
                temp=temp->next;
            }              
            else
                temp=temp->next;
        }
        if(temp->crashflag == 1)
                sendconn(tempip,tempport,tempbuff);//send message to all neighbours
        current=current->next;
    }
}

messagereader(char input[])
{
        char args[50][50];
	int a = 0;
	int i = 0;
        int j = 0;
	int index = 0;
        int count = 0;
        int flag = 0;
        int recvfrmid = 0;
        NODE *temp = NULL;
	
	while (input[a] != '\0') 
	{
		if (input[a] == ' ' || input[a] == '\n' || input[a]=='\r')
		{ 
                    args[index][i] = '\0'; 
                    i=0; a++; index++; 
		} // SPACE detected or newline detected
 		else
		args[index][i++] = input[a++];

	}
	args[index][i] = '\0';
       
        recvfrmid = atoi(args[1]);
        while(temp!=NULL)
        {
            if(strcmp(temp->word[1],args[1])==0)
            {
                temp->crashcount = temp->crashcount++;
            }
            temp = temp->next;
        }
        
        flag = 4;
        count = 7;
        //read add values from the neighbours and its neighbours
        while(flag <= (servers-1)*4)
        {
            matrix[recvfrmid - 1][atoi(args[flag])-1]=atoi(args[count]);
            count = count+4;
            flag = flag+4;
        }
        
        for(i=0;i<servers;i++)                  //DV Algorithim
        {
            for(j=0;j<servers;j++)
            {
                int initial_cost=routematrix[j][2];
                int cost_via=matrix[atoi(localid)-1][i]+matrix[i][j];
                if(initial_cost>cost_via)               // Bellmanford equation
                {
                    routematrix[j][2]=cost_via;         //assigning minimum cost
                    routematrix[j][1]=i+1;              //next hop 
                    createmessage();
                }
                
            }
        }     
        display();
}

routermatrixinitialization()
{
    int p=0;
        for(p=0;p<servers;p++)
	{
            routematrix[p][0]=p+1;
            routematrix[p][1]= 0;
            routematrix[p][2]=99;
        }
}

routerinitialmatrix()
{
    int i=0;
    NODE *temp = NULL;
    temp = neigh;

    for(i=0;i<servers;i++)
    {
            if(atoi(localid)==routematrix[i][0])
            {
                routematrix[i][1]=i+1;
                routematrix[i][2]=0;
            }
            else
            {
                temp = neigh;
                while(temp!=NULL)
                {
                    
                    if(atoi(temp->word[1])==routematrix[i][0])
                    {
                        routematrix[i][1]= atoi(temp->word[1]);
                        routematrix[i][2]= matrix[atoi(localid)-1][atoi(temp->word[1])-1];
                        temp = temp->next;
                    }
                    else
                        temp=temp->next;
                }
            }
    }
    createmessage();
}

display()
{
    int i=0;
    int j=0;
    printf("\nDestination\tNext Hop\tCost\n");
    for(i=0;i<servers;i++)
	{
            for(j=0;j<3;j++)
		{
                    printf("%d\t\t",routematrix[i][j]);
		}
            printf("\n");
        }
}

step()
{
    int i=0;
    char tempid[5];
    char *tempport;
    char *tempip;
    char *tempbuff;
    tempip = (char *)malloc(16);
    tempport = (char *)malloc(5);
    tempbuff= (char *)malloc(500);
    strcpy(tempbuff, buffer);
    NODE *temp = NULL;
    NODE *current = NULL;
    current = neigh;
    
    for (i=0;i<neighbours;i++)
    {
        bzero(tempid,sizeof(tempid));
        strcpy(tempid, current->word[1]);  //ID of current neighbour
        temp = serv;
        while(temp!= neigh)// the temp node is at the start of the servers where id = 1
        {
            if(strcmp(temp->word[0],tempid)==0)
            {
                strcpy(tempip, temp->word[1]);
                strcpy(tempport, temp->word[2]);
                temp=temp->next;
            }              
            else
                temp=temp->next;
        }
        printf("Sending message to:%s\t%s\t%s\n",tempid,tempport,tempip);
        sendconn(tempip,tempport,tempbuff);//send message to all neighbours
        current=current->next;
    }
}


update(char *cmd1, char cmd2[], char updateval[])
{
    if(strcmp(cmd1,localid)==0)
    {
        if(strcmp(cmd2,localid)!=0)
        {
          if(atoi (cmd2)== 1 ||atoi (cmd2)== 2 ||atoi (cmd2)== 3 ||atoi (cmd2)== 4 ||atoi(cmd2) == 5 || strcmp(cmd2,"inf")==0)
          {
            if(strcmp(updateval,"inf")==0)
            {
                routematrix[atoi(cmd2) -1][2] = 99;
                matrix[atoi(localid) -1][atoi(cmd2)- 1]=99;
                createmessage();
            }
            else if(atoi(updateval)<99)
            {
                routematrix[atoi(cmd2) -1][2] = atoi(updateval);
                matrix[atoi(localid) -1][atoi(cmd2)- 1]=atoi(updateval);
                createmessage();
            }
            else
            {
                printf("The cost must be below 99\n");
                return -1;
            }
            
          }
          else{
               printf("Invalid Argument 2\n");
               return -1;}
        }
        else{
            printf("Cannot update self\n");
            return -1;}
    }
    else
    {
        printf("The argument 1 should be localid: %s\n",localid);
        return -1;
    }
}

void main()
{
    char command[50];
    char command1[50];
    char command2[100];
    char command3[100];
    char *tempfile;
    
    for(;;)
    {
        printf("$");
        scanf("%s %s %s %s %d",command, command1, command2, command3, &interval);
        if(strcmp(command, "server")==0)
        {
            if(strcmp(command1,"-t")==0)
            {
                if(strcmp(command3, "-i")==0)
                {
                    tempfile=(char *)malloc(50);        
                    strcpy(tempfile, command2);
                    readline(tempfile);         //file reads here 
                    checkserver();              // verifies server position in list and details
                    checkneighbour();           // verifies neighbour position in list and details
                    markserv();                 //pointing to servers list starting
                    markneighbour();            //pointing to neighbour list starting
                    checklocalid();             // verifies local ID
                    checklocalinfo();           // verifies local IP and Port number
                    matrixinitialization();     // creating and initializing the DV matrix
                    routermatrixinitialization();       // creating and initializing the routing table
                    initialmatrix();
                    routerinitialmatrix();
                    recvconn();                 //command goes to the select statement and FD's and STDIN
                }
                else 
                {
                        printf("Format must be: server -t <topology-file-name> -i <routing-update-interval>\n");
                        return;
                }
            }
            else 
            {
                printf("Format must be: server -t <topology-file-name> -i <routing-update-interval>\n");
                return;
            }
        }
        else 
        {
            printf("Format must be: server -t <topology-file-name> -i <routing-update-interval>\n");
            return;
        }
    }       	
}
