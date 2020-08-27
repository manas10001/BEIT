#include<stdio.h>
#include<stdlib.h>
#include<pthread.h>
#include<semaphore.h>
#include<unistd.h>
#define sz 5

pthread_mutex_t mutex, wrt;
int rc = 0, wc = 0, item, readcnt = 0, buf[sz];

void disp(){
	printf("\tBUFFER :: [ ");
		for(int i=0;i<sz;i++)
			printf("%d ",buf[i]);
		printf("]");
}

void *writer(void * arg){
	int w,n;
	w = (int*)arg;
	
	while(1){
		n = rand()%20+1;
		sleep(n);
		
		pthread_mutex_lock(&wrt);
			
			//cs
			printf("\nWriter %d written %d", w, n);
			buf[wc] = n;
			wc = (wc+1)%sz;
			disp();
		pthread_mutex_unlock(&wrt);
	}
}

void *reader(void *arg){
	int r,n;
	r = (int*)arg;
	
	while(1){
		n = rand()%10+1;
		sleep(n);
		pthread_mutex_lock(&mutex);
		readcnt++;
		if(readcnt==1)
			pthread_mutex_lock(&wrt);
		pthread_mutex_unlock(&mutex);
		
		//cs
		n = buf[rc];
		//buf[rc]=0;
		printf("\nReader %d read %d\t", r, n);
		rc = (rc+1)%sz;
		disp();
		pthread_mutex_lock(&mutex);
		readcnt--;
		if(readcnt==0)
			pthread_mutex_unlock(&wrt);
		pthread_mutex_unlock(&mutex);
	}
}


int main(){
	int r, w, res,i;
	pthread_t rd[10], wr[10];
	printf("\nEnter number of readers:");
	scanf("%d",&r);
	
	printf("\nEnter number of writers:");
	scanf("%d",&w);
	
	pthread_mutex_init(&mutex,NULL);
	pthread_mutex_init(&wrt,NULL);
	
	for(i=0;i<sz;i++)
		buf[i]=0;
	
	disp();
	
	for(i=0;i<w;i++){
		if(pthread_create(&wr[i],NULL,writer,i+1) != 0){
			printf("Error in creating writer ");
			exit(EXIT_FAILURE);
		}
	}	
	for(i=0;i<r;i++){
		if(pthread_create(&rd[i],NULL,reader,i+1) != 0){
			printf("Error in creating reader ");
			exit(EXIT_FAILURE);
		}
	}
	
	//joined
	
	for(int i=0;i<w;i++)
	{
		res = pthread_join(wr[i], NULL);
		if(res != 0)
		{
			printf("\nError while joining threads..");
			exit(EXIT_FAILURE);
		}
		printf("\n[WRITER] %d joined", i+1);
	}
	
	for(int i=0;i<r;i++)
	{
		res = pthread_join(rd[i], NULL);
		if(res != 0)
		{
			printf("\nError while joining threads..");
			exit(EXIT_FAILURE);
		}
		printf("\n[READER] %d joined..",i+1);
	}	
}
