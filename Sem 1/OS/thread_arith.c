#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>


//CHILD THREAD FOR ADDITION
void * tAdd(void *arg)
{
	int *a,res;
	a = (int *) malloc(2*sizeof(int));
	a = (int *) arg;
	printf("IN CHILD THREAD (ADDITION)\n");
	res = a[0] + a[1];
	pthread_exit((void *)res);
}

//CHILD THREAD FOR SUBSTRACTION
void * tSub(void *arg)
{
	int *a,res;
	a = (int *) malloc(2*sizeof(int));
	a = (int *) arg;
	printf("IN CHILD THREAD (SUBSTRACTION)\n");
	res = a[0] - a[1];
	pthread_exit((void *)res);
}

//CHILD THREAD FOR MULTIPLITION
void * tMul(void *arg)
{
	int *a,res;
	a = (int *) malloc(2*sizeof(int));
	a = (int *) arg;
	printf("IN CHILD THREAD (MULTIPLICATION)\n");
	res = a[0] * a[1];
	pthread_exit((void *)res);
}

//CHILD THREAD FOR DIVISION
void * tDiv(void *arg)
{
	int *a,res;
	a = (int *) malloc(2*sizeof(int));
	a = (int *) arg;
	printf("IN CHILD THREAD (DIVISION)\n");
	res = a[0] / a[1];
	pthread_exit((void *)res);
}

int main()
{
	int res;
	int a[2];
	
	char* msg="HOW YOU DOIN?";
	pthread_t tid;
	void *tRes;
	
	printf("IN MAIN THREAD\n");
	
	printf("Enter two elements ");
	scanf("%d %d",&a[0],&a[1]);
	
//*************************ADDITION
	res = pthread_create(&tid,NULL,tAdd,(void *)a);
	
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
	
	printf("BACK IN MAIN\n");
	
	printf("Addition is: %d \n\n",(int *)tRes);	

//*************************SUBSTRACTION
	res = pthread_create(&tid,NULL,tSub,(void *)a);
	
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
	
	printf("BACK IN MAIN\n");
	
	printf("Substraction is: %d \n\n",(int *)tRes);	

//*************************Multiplication
	res = pthread_create(&tid,NULL,tMul,(void *)a);
	
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
	printf("BACK IN MAIN\n");

	printf("Multiplication is: %d \n\n",(int *)tRes);	

//*************************Dividion
	res = pthread_create(&tid,NULL,tDiv,(void *)a);
	
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
	
	printf("BACK IN MAIN\n");
	
	printf("Division is: %d \n\n",(int *)tRes);	
	
	exit(EXIT_SUCCESS);
}	
