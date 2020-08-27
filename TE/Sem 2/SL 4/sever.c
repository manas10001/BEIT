#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<unistd.h>
#include<errno.h>

#define PORT 5561
#define BUF_SIZE 2000
#define CLADDR_LEN 100

char *itoaa(int val, int base);

int main(){
	struct sockaddr_in addr, cl_addr;
	int sockfd, len, ret, newsockfd;
	char buffer[BUF_SIZE];
	pid_t childpid;
	char clientAddr[CLADDR_LEN];
	int num, rem, sum;
	char *str;
	
			//domain, type, protocol
	sockfd = socket(AF_INET, SOCK_STREAM, 0);

	if (sockfd < 0)
	{
		printf("Error creating socket!\n");
		exit(1);
	}
	
	printf("Socket created...\n");
	memset(&addr, 0, sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = INADDR_ANY;
	addr.sin_port = PORT;
	ret = bind(sockfd, (struct sockaddr *) &addr, sizeof(addr));
	
	if (ret < 0)
	{
		printf("Error binding!\n");
		exit(1);
	}
	else
		printf("Binding done...\n");
	
	printf("Waiting for a connection...@ port no : %d\n",PORT );listen(sockfd, 5);
	
	for (;;) //infinite loop
	{
		len = sizeof(struct sockaddr_in);
		newsockfd = accept(sockfd, (struct sockaddr*)&cl_addr,(socklen_t *)&len);
		if (newsockfd < 0)
		{
			printf("Error accepting connection!\n");
			exit(1);
		}
		else
			printf("Connection accepted from ");
		
		inet_ntop(AF_INET, &(cl_addr.sin_addr), clientAddr, CLADDR_LEN);
		printf("Port %d of %sClient\n",ntohs(cl_addr.sin_port),inet_ntoa(cl_addr.sin_addr));
		
		if ((childpid = fork()) == 0) //creating a child process
		{
			close(sockfd);
		//stop listening for new connections by the main process.
		//the child will continue to listen.
		//the main process now handles the connected client.
			for (;;)
			{
				memset(buffer, 0, BUF_SIZE);
				ret = recvfrom(newsockfd, buffer, BUF_SIZE, 0,
				(struct sockaddr *) &cl_addr, (socklen_t *)&len);
				if(ret < 0)
				{
					printf("Error receiving data!\n");
					exit(1);
				}
				else
					printf("Received data from Port No %d of Client %s : %s\n ", ntohs(cl_addr.sin_port),clientAddr, buffer);
				num=atoi(buffer);
				sum=0;
				while(num>0)
				{
					sum = sum + (num % 10);
					num = num / 10;
				}
				strcat(buffer,"= sum of digits = ");
				str=itoaa(sum,10); 
				strcat(buffer,str);
				ret = sendto(newsockfd, buffer, BUF_SIZE, 0,(struct sockaddr *) &cl_addr, len);
				if (ret < 0)
				{
					printf("Error sending data!\n");
					exit(1);
				}
				else
					printf("\tSent data to %s on Port No %d :%s\n", clientAddr,ntohs(cl_addr.sin_port), buffer);
				printf("-----------------------------------------\n");
			}
		}
		close(newsockfd);
	}
	return(0);
}
char *itoaa(int val, int base)
{
	static char buf[32] = {0};
	int i = 30;
	for( ; val && i ; --i, val /= base)
		buf[i]="0123456789abcdef"[val % base];
	return &buf[i+1];
}
