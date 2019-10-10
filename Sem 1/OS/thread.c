#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>

void * tDemo(void *arg)
{
	printf("IN CHILD THREAD\n");
	printf("ARG = %s \n",(char *)arg);
}

int main()
{
	int res;
	char* msg="HOW YOU DOIN?";
	pthread_t tid;
	void *tRes;
	
	printf("IN MAIN THREAD\n");
	
	res = pthread_create(&tid,NULL,tDemo,(void *)msg);
	
	if(res!=0)
	{
		printf("ERROR IN THREAD CREATION\n");
		exit(EXIT_FAILURE);
	}
	
	printf("JOINING CHILD THREAD\n");
	
	res = pthread_join(tid,&tRes);
	
	if(res != 0)
	{
		printf("ERROR IN THREAD JOINING\n");
		exit(EXIT_FAILURE);
	}
	
	printf("BACK IN MAIN");

	exit(EXIT_SUCCESS);
}
