//***********CITATION - MOST OF THE WEBSITES USED ARE CITED, BEEJ MATERIAL WAS USED TOO!!!!!!!!!!!!!!!*************

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <sys/time.h>
#include <sys/types.h>
#include <string.h>
#include <errno.h>
#include <arpa/inet.h>
#include <ctype.h>
#include <sys/time.h>
#include <sys/fcntl.h>
#include <time.h>
#include <sys/unistd.h>



struct sockaddr *from;
struct socklen_t *fromlen;

#define STDIN 0
int socket_desc;
struct sockaddr_in server;
char *myport[5];
int argc;
int portno=0;
char command2[50];
char command3[50];
char localip[100];
char iptemp[INET_ADDRSTRLEN];

struct node
{
	int socketfd;
	int portnum;
	char *ipaddress;
	char *hostname;
	struct node *next;

};
typedef struct node NODE;

NODE *newnode=NULL;
NODE *first=NULL;

void addSockfd(int sockfdval,char *hosttemp, int porttemp)
{
	NODE *temp=NULL;

	temp=(NODE *)malloc(sizeof(NODE));
	if(sockfdval == 0)
	{
		printf("No lists to print\n");
	}
	else
	{
		temp->socketfd = sockfdval;
		temp->ipaddress = iptemp;
		temp->portnum = porttemp;
		temp->hostname = hosttemp;
		temp->next=NULL;

		if(first == NULL)
		{
			first = newnode = temp;
		}
		else
		{
			newnode->next = temp;
			newnode = temp;
		}
	}

}

int dupliacteip(char temp_ip[])
{
	NODE *temp=NULL;
	temp=first;
	while(temp!=NULL)
	{
		if(strcmp(temp->ipaddress,temp_ip)==0)	
		{
			return 1;
		}
		temp=temp->next;	
	}
	return 0;
}

void delete(int tempfd)
{
	NODE *temp=NULL;
	NODE *prev = NULL;
	temp=prev=first;
	if(first == NULL)
		printf("No connections to delete\n");
	else
	{
		if(first->socketfd == tempfd)
		{
			first = temp->next;
			temp = NULL;
			free(temp);
		}
		else
		{
			while(temp->next!=NULL && temp->socketfd != tempfd)
			{
				prev = temp;
				temp= temp->next;

				if(temp->socketfd == tempfd)
				{
					prev->next = temp->next;
					temp->next = NULL;
					free(temp);
				}
			}
		}

	}
}

void broadcast()
{
	NODE *temp= NULL;
	int numbytes=0;
	temp = first;
	while(temp!=NULL)
	{
		if ((numbytes = send(temp->socketfd, "\nA New connection has been made\n", 33, 0)) == -1)
			perror("send");

		temp = temp->next;
	}
}

void display()
{
	NODE *temp = NULL;
	temp = first;

	if(first == NULL)
		printf("\nNO connections to be displayed\n");
	else
	{
		printf("\nFD	HOSTNAME				IP Address	PORT Number\n");
		if(first->next == NULL)
		{

		printf("%d	%s		%s	%d\n", temp->socketfd,temp->hostname,temp->ipaddress,temp->portnum);

		}
		else
		{
			while(temp!=NULL)
			{
		printf("%d	%s		%s	%d\n", temp->socketfd,temp->hostname,temp->ipaddress,temp->portnum);


				temp=temp->next;
			}
		}
	}
}

int ipcheck(char temp_ip[])
{
    	struct sockaddr_in sa;
	int result;

	result = inet_pton(AF_INET, temp_ip, &(sa.sin_addr));
	
	return result;
}

void help()
{
	printf("\nChoose one of the following options:\n1. HELP\n2. MYIP\n3. MYPORT\n4. REGISTER<server IP> <port_no>  \n5. CONNECT<destination> <port no> \n6. LIST \n7. TERMINATE<connection id.> \n8. EXIT\n9. DOWNLOAD <file_name> <file_chunk_size_in_bytes> \n10. CREATOR\n");
	return;

}

int myportfunc()
{
	printf("your port number is: %s\n", *myport);

}

int serverside(char *argv[])
{
	char command[50];
	fd_set readfds;
	fd_set master;				// don't care about writefds and exceptfds:
	int fdmax=0;
	int yes=1;
	int addrlen=0;
	int newfd=0;
	int cmdlen=0;
	int listener;
	char ipstr[INET_ADDRSTRLEN];
	int porttemp=0;
	int bytes=0;
	char buff[5];
	int i=0;
	int j,nbytes;
	int numbytes=0;
	int temp_sock=0;
	char buf[256];
	socklen_t len;
	struct sockaddr_storage addr;
	struct sockaddr_in clientaddr;
	struct sockaddr_in serveraddr;

	if((listener = socket(AF_INET, SOCK_STREAM, 0)) == -1)			//http://www.tenouk.com/Module41.html
	{
		perror("Server-socket() error!!!");
		return 0;
	}

	if(setsockopt(listener, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1)
	{
		perror("Server-setsockopt() error!!!\n");
		return 0;
	}
	

	fcntl(listener, F_SETFL, O_NONBLOCK); 

 	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = INADDR_ANY;
	serveraddr.sin_port = htons(portno);
	memset(&(serveraddr.sin_zero), '\0', 8);
 	if(bind(listener, (struct sockaddr *)&serveraddr, sizeof(serveraddr)) == -1)
	{
		perror("Server-bind() error!!!\n");
	        return 0;
	}


	if(listen(listener, 10) == -1)
	{
		perror("Server-listen() error\n");
		return 0;
	}


	addrlen=sizeof(clientaddr);
	FD_ZERO(&master);
	FD_ZERO(&readfds);
 	FD_SET(listener, &master);
	FD_SET(fileno(stdin), &master);

	fdmax=listener;
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
		printf("server>>");
		fflush(stdout);
		readfds=master;
		if((select(fdmax+1,&readfds,NULL,NULL,NULL))==-1)
		{
			perror("select");
			exit(4);
		}

		if(FD_ISSET(fileno(stdin),&readfds))
		{
			scanf("%s", command);

			if (strcmp(command, "MYIP")==0)
			{
				printf("get ip address\n");
				myip();
			}
			else if(strcmp(command,"HELP")==0)
			{
				help();
			}
			else if(strcmp(command,"EXIT")==0)
			{
					return(0);
			}
			else if(strcmp(command,"MYPORT")==0)
			{
				myportfunc();
			}
			else if(strcmp(command, "LIST")==0)
			{
				display();
			}
			else if(strcmp(command, "CREATOR")==0)
			{
				creator();
			}
			else
			{
				printf("invalid command\n");
			}

		}
		for(i = listener; i <= fdmax; i++)
		{
			if (FD_ISSET(i, &readfds))
			{ 	printf(" A new connection has been made!!\n");
				if (i == listener)
				{
				
					newfd = accept(listener,(struct sockaddr *)&clientaddr,&addrlen);
					if (newfd == -1)
					{
						perror("accept\n");
					}
					else
					{
						memset(buff,0,sizeof buff);
						if ((bytes=recv(newfd, buff, sizeof(buff), 0)) == -1)        // Receiving the server portno
							perror("recv\n");
						

						porttemp= atoi(buff);

						len = sizeof(addr);
						getpeername(newfd, (struct sockaddr*)&addr,&len);
						struct sockaddr_in *s = (struct sockaddr_in *)&addr;
						inet_ntop(AF_INET, &s->sin_addr, ipstr, sizeof ipstr);


						struct hostent *temphostname;
						struct in_addr ipv4addr;
						inet_pton(AF_INET, ipstr, &ipv4addr);
						temphostname=gethostbyaddr(&ipv4addr,sizeof ipv4addr, AF_INET);
						strcpy(iptemp,ipstr);
						addSockfd(newfd,temphostname->h_name, porttemp);	//sending server details
						broadcast();			//*********************TEMP FD BROADCAST**********

						FD_SET(newfd, &master); // add to master set
						if (newfd > fdmax)
						{
							// keep track of the max
							fdmax = newfd;
						}
					}
				}
				else						//CODE FROM BEEJ MULTI CLIENT CHAT
				{
					// handle data from a client
					memset(buf,0,sizeof(buf));
					if ((nbytes = recv(i, buf, sizeof buf, 0)) <= 0)
					{
						// got error or connection closed by client
						if (nbytes == 0)
						{
							// connection closed
							printf("selectserver: socket %d hung up\n", i);
							delete(i);
						}
						else
						{
							perror("recv\n");
						}
						close(i); // bye!
						FD_CLR(i, &master); // remove from master set
					}
					else
					{
						// we got some data from a client
						for(j = listener; j <= fdmax; j++)
						{
							// send to everyone!
							if (FD_ISSET(j, &master))
							{
								// except the listener and ourselves
								if (j != listener)
								{
									printf("Data Received\n");
								}
							}
						}
					}
				} // END handle data from client
			} // END got new incoming connection
		} // END looping through file descriptors

	}
	return 0;
}

int clientside(char *argv[])
{

	char command1[50];
	fd_set readfds;
	fd_set master;				// don't care about writefds and exceptfds:
	int fdmax=0;
	int yes=1;
	int addrlen=0;
	int newfd=0;
	int listener;
	char ipstr[INET_ADDRSTRLEN];
	int porttemp=0;
	int bytes=0;
	char buff[5];
	char newbuff[100];
	int numbytes=0;
	int tempfd=0;
	socklen_t len;
	int i,j,nbytes;
	int temp_sock=0;
	char buf[256];
	struct sockaddr_storage addr;
	struct sockaddr_in clientaddr;
	struct sockaddr_in serveraddr;

	if((listener = socket(AF_INET, SOCK_STREAM, 0)) == -1)			//http://www.tenouk.com/Module41.html
	{
		perror("Server-socket() error!!!\n");
		return 0;
	}
	
	if(setsockopt(listener, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1)
	{
		perror("Server-setsockopt() error!!!\n");
		return 0;
	}

	fcntl(listener, F_SETFL, O_NONBLOCK);  //check for error -1

 	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = INADDR_ANY;
	serveraddr.sin_port = htons(portno);
	memset(&(serveraddr.sin_zero), '\0', 8);
 	if(bind(listener, (struct sockaddr *)&serveraddr, sizeof(serveraddr)) == -1)
	{
		perror("Server-bind() error!!!\n");
	        return 0;
	}

	if(listen(listener, 10) == -1)
	{
		perror("Server-listen() error\n");
		return 0;
	}

	addrlen=sizeof(clientaddr);
	FD_ZERO(&master);
	FD_ZERO(&readfds);
 	FD_SET(listener, &master);
	FD_SET(fileno(stdin), &master);

	fdmax=listener;
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
		printf("client>>");
		fflush(stdout);
		readfds=master;
		if (newfd > fdmax)
		{
			fdmax = newfd;
		}
		if((select(fdmax+1,&readfds,NULL,NULL,NULL))==-1)
		{
			perror("select");
			exit(4);
		}

		for(i = listener; i <= fdmax; i++)
		{	
			if (FD_ISSET(i, &readfds))
			{ 	printf(" New connection has been made.!!\n");
				if (i == listener)
				{
					newfd = accept(listener,(struct sockaddr *)&clientaddr,&addrlen);
					if (newfd == -1)
					{
						perror("accept\n");
					}
					else
					{
						memset(buff,0,sizeof buff);
						if ((bytes=recv(newfd, buff, sizeof(buff), 0)) == -1)        // Receiving the server portno
							perror("recv\n");
					
						porttemp= atoi(buff);

						len = sizeof(addr);
						getpeername(newfd, (struct sockaddr*)&addr,&len);
						struct sockaddr_in *s = (struct sockaddr_in *)&addr;
						inet_ntop(AF_INET, &s->sin_addr, ipstr, sizeof ipstr);


						struct hostent *temphostname;
						struct in_addr ipv4addr;
						inet_pton(AF_INET, ipstr, &ipv4addr);
						temphostname=gethostbyaddr(&ipv4addr,sizeof ipv4addr, AF_INET);
						strcpy(iptemp,ipstr);
						addSockfd(newfd,temphostname->h_name, porttemp);	//sending Client as server details
						broadcast();			//*********************TEMP FD BROADCAST**********

						FD_SET(newfd, &master); // add to master set
						if (newfd > fdmax)
						{
							// keep track of the max
							fdmax = newfd;
						}
					}
				}
				else						//CODE FROM BEEJ MULTI CLIENT CHAT
				{
					// handle data from a client
					memset(buf,0,sizeof(buf));
					if ((nbytes = recv(i, buf, sizeof buf, 0)) <= 0)
					{
						// got error or connection closed by client
						if (nbytes == 0)
						{
							// connection closed
							printf("selectserver: socket %d hung up\n", i);
							delete(i);
						}
						else
						{
							perror("recv\n");
						}
						close(i); 
						FD_CLR(i, &master); 
					}
					else
					{
											// we got some data from a client
						for(j = listener; j <= fdmax; j++)
						{
											// send to everyone!
							if (FD_ISSET(j, &master))
							{
								if (j != listener)
								{
									printf("Data Received--> %d\n",nbytes);
								}
							}
						}
					}
				} 
			} 
		} 


		if (FD_ISSET(fileno(stdin), &readfds))
		{
			scanf("%s",command1);

			if (strcmp(command1, "MYIP")==0)
			{
				printf("Get ip address\n");
				myip();
				fflush(stdout);
			}
			else if(strcmp(command1,"HELP")==0)
			{
				help();
				fflush(stdout);
			}
			else if(strcmp(command1,"EXIT")==0)
			{
				return(0);
				fflush(stdout);
			}
			else if(strcmp(command1,"MYPORT")==0)
			{
				myportfunc();
				fflush(stdout);
			}
			else if(strcmp(command1,"CREATOR")==0)
			{
				creator();
			}
			else if(strcmp(command1, "LIST")==0)
			{
				display();
			}
			else if(strcmp(command1,"REGISTER")==0)
			{
				scanf("%s %s",command2,command3);
				if(strcmp(command2, localip)==0)
				{
					printf("You cannot REGISTER to youself\n");
				}
				else if(ipcheck(command2)!=1)
				{
					printf("Invalid IP\n");
				}
				else if(dupliacteip(command2)==1)
				{
					printf("This already has been registered!!\n");
				}
				else if(strcmp(command2,"")==0)
				{
					printf("IP address is needed to REGISTER\n");
				}
				else if(strcmp(command3,"")==0)
				{
					printf("PORT Number is needed to REGISTER\n");
				}
				else
				{
					temp_sock = regclient(command2, command3,argv);
					FD_SET(temp_sock, &master); // add to master set
					if (temp_sock > fdmax)
						{
							fdmax = temp_sock;
							temp_sock=0;
						}
				}
			}
			else if(strcmp(command1,"CONNECT")==0)
			{
				scanf("%s %s",command2,command3);
				if(strcmp(command2, localip)==0)
				{
					printf("You cannot CONNECT to youself\n");
				}
				else if(ipcheck(command2)!=1)
				{
					printf("Invalid IP\n");
				}
				else if(dupliacteip(command2)==1)
				{
					printf("The connection already has been established!!\n");
				}
				else if(strcmp(command2,"")==0)
				{
					printf("IP address is needed to CONNECT\n");
				}
				else if(strcmp(command3,"")==0)
				{
					printf("PORT Number is needed to CONNECT\n");
				}
				else
				{
					temp_sock = regclient(command2, command3,argv);
					FD_SET(temp_sock, &master); // add to master set
					if (temp_sock > fdmax)
					{
						fdmax = temp_sock;
						temp_sock=0;
					}
				}
			}

			else if(strcmp(command1, "LIST")==0)
			{
				display();
			}
			else if(strcmp(command1, "DOWNLOAD")==0)
			{
				printf("Under construction\n");
			}
			else if(strcmp(command1, "TERMINATE")==0)
			{
				scanf("%d", &tempfd);

				if (FD_ISSET(tempfd, &master))
				{
					for(i=3;i<=fdmax;i++)
					{
						close(tempfd);
						FD_CLR(tempfd, &master);
					}
					delete(tempfd);
				}
				else
					printf("Connection doesnot exist\n");
			}	
			else
			{
				printf("Please enter the correct input, NOTE: the input is case sensitive\n Type HELP for assistan\n");
			}
		}


	}
	return 0;
}

creator()
{
	printf("\nThis program is created by Bala Seshadri Sura\nUBIT Name: balasesh\nEmail: balasesh@buffalo.edu\n");
}



myip()				//source code from Binary Clients and beej's Guide "http://www.binarytides.com/get-local-ip-c-linux/"
{
	const char* google_dns_server = "8.8.8.8";
	int dns_port = 53;
	char tempbuff[100];

	struct sockaddr_in serv;

	int sock = socket ( AF_INET, SOCK_DGRAM, 0);


	if(sock < 0)
	{
		perror("Socket error\n");
	}

	memset( &serv, 0, sizeof(serv) );
	serv.sin_family = AF_INET;
	serv.sin_addr.s_addr = inet_addr( google_dns_server );
	serv.sin_port = htons( dns_port );

	connect( sock , (const struct sockaddr*) &serv , sizeof(serv) );

	struct sockaddr_in name;
	socklen_t namelen = sizeof(name);
	int err = getsockname(sock, (struct sockaddr*) &name, &namelen);

	const char* p = inet_ntop(AF_INET, &name.sin_addr, tempbuff, 100);

	if(p != NULL)
	{
		printf("Local ip is : %s \n" , tempbuff);
	}
	else
	{

		printf ("Error number : %d . Error message : %s \n" , errno , strerror(errno));
	}
	close(sock);
	strcpy(localip,tempbuff);

	return 0;
}

regclient(char cmd1[], char cmd2[],char *argv[])
{
	int sockfd = 0,numbytes=0;
	char ipstr[INET_ADDRSTRLEN];
	int porttemp=0;
	int bytes=0;
	char buff[100];

	char str[80];
	socklen_t len;
	struct addrinfo hints, *res, *p;
	struct sockaddr_storage addr;


	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;

	int flag = getaddrinfo(cmd1, cmd2,&hints,&res);


	for(p = res; p!= NULL; p = p->ai_next)				//Beej connect command
	{
		if((sockfd = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) < 0)
		{
			printf("\n Error : Could not create socket \n");
			return 0;
		}


		if(connect(sockfd, p->ai_addr, p->ai_addrlen)== -1)
		{
			close(sockfd);
			perror("client:connect");
			continue;
		}
		else
		{


			if ((numbytes = send(sockfd, argv[2], strlen(argv[2]), 0)) == -1)
				perror("send\n");
			

			if ((bytes=recv(sockfd, buff, sizeof(buff), 0)) == -1)
				perror("recv\n");
			porttemp= atoi(cmd2);
			buff[100]='\0';
			memset(&ipstr,0,INET_ADDRSTRLEN);
			len = sizeof(addr);
			getpeername(sockfd, (struct sockaddr*)&addr,&len);
			struct sockaddr_in *s = (struct sockaddr_in *)&addr;

			//porttemp = ntohs(s->sin_port);
			inet_ntop(AF_INET, &s->sin_addr, ipstr, sizeof ipstr);
			struct hostent *temphostname;
			struct in_addr ipv4addr;
			inet_pton(AF_INET, ipstr, &ipv4addr);
			temphostname=gethostbyaddr(&ipv4addr,sizeof ipv4addr, AF_INET);
			//printf("IP Address -> %s\n", ipstr);
			strcpy(iptemp,ipstr);
			addSockfd(sockfd,temphostname->h_name, porttemp);			//adding local client details to the list


		}
		break;

	}
	return sockfd;
}

int main(int argc, char *argv[])
{
	int i=0;

	//printf("\nAnd So it Begins\n");
	for(i=0;i<sizeof (argv[2]);i++)
	{
		myport[i]=argv[2];
	}
	portno = atoi(*myport);

	if(strcmp(argv[1],"s")==0)
	{
		printf("\nYou have entered server side\n");
		serverside(argv);
		return 0;					//going to server side of the program
	}
	else if(strcmp(argv[1], "c")==0)
	{
		printf("\nYou have entered client side\n");
		clientside(argv); 	//going to client side of the program
		return 0;
	}
	else
	{
		printf("Invalid Input\n");
		return 0;
	}

	return 0;

}

