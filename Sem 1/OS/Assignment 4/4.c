#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <semaphore.h>

#define MX 5

int buf[MX],in=0,out=0;
sem_t empty,full;
pthread_mutex_t lock;


void disp(){
	printf("\n\nBUFFER :: [ ");
		for(int i=0;i<MX;i++)
			printf("%d ",buf[i]);
		printf("] \n\n");
}


void *producer(int *pr){

	int num,i,p;
	p = (int *)pr;
	
	while(1){
		num = rand()%10;
		sleep(num);
		sem_wait(&empty);
		pthread_mutex_lock(&lock);
		
		//cs
		buf[in]=num;
		in = (in+1)%MX;
		printf("[PRODUCER] : %d [ADDED] : %d", p, num);
		disp();
		pthread_mutex_unlock(&lock);
		sem_post(&full);
	} 
}


void *consumer(int *con){

	int num,i,c;
	c = (int *)con;
	
	while(1){
		sem_wait(&full);
		pthread_mutex_lock(&lock);
		
		//cs
		num = buf[out];
		buf[out] = 0;
		out = (out+1)%MX;
		
		printf("[CONSUMER] : %d [CONSUMED] : %d", c, num);
		disp();
		pthread_mutex_unlock(&lock);
		sem_post(&empty);
	}
}

int main(){

	pthread_t prod[10], cons[10];
	int p, c, res,i;
	
	printf("\nEnter number of producers:");
	scanf("%d",&p);
	
	printf("\nEnter number of consumers:");
	scanf("%d",&c);
	
	//init
	sem_init(&empty,0,MX);
	sem_init(&full,0,0);
	pthread_mutex_init(&lock,NULL);
	
	for(int i=0;i<MX;i++)
		buf[i]=0;
		
	disp();
	
	//create prod and consumers
	
	for(i=0;i<p;i++){
		if(pthread_create(&prod[i],NULL,producer,i+1)!=0)
		{
			printf("\nError in creating %d producer",i);
			exit(EXIT_FAILURE);
		}
	}
	
	for(i=0;i<c;i++){
		if(pthread_create(&cons[i],NULL,consumer,i+1)  != 0 )
		{
			printf("\nError in creating %d consumer",i);
			exit(EXIT_FAILURE);
		}
	}
	
	for(i=0;i<p;i++)
	{
		res = pthread_join(prod[i], NULL);
		if(res != 0)
		{
			perror("Thread join failed");
			exit(EXIT_FAILURE);
		}
		printf("\nProducer %d joined",i+1);	
	}
	for(i=0;i<c;i++)
	{
		res = pthread_join(cons[i], NULL);	
		if(res != 0)
		{
			perror("Thread join failed");
			exit(EXIT_FAILURE);
		}	
		printf("\nConsumer %d joined",i+1);
	}
	
	return 0;
}
