#include<stdio.h>
#include<pthread.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/wait.h>
#include<semaphore.h>

#define N 5
#define THINKING 0
#define HUNGRY 1
#define EATING 2
#define LEFT (num + 4)%N
#define RIGHT (num + 1)%N

sem_t S[N],mutex;
int phil[N]={0,1,2,3,4};
int state[N];

void test(int num){

	if(state[num]==HUNGRY && state[LEFT]!=EATING && state[RIGHT] != EATING){
		state[num] = EATING;
		sleep(1);
		
		printf("\n[PHILOSOPHER] %d  is eating:", num+1);
		sem_post(&S[num]);
	}
}


void putFork(int num){

	sem_wait(&mutex);
	
	state[num] = THINKING;
	
	printf("\n[PHILOSOPHER] %d is [THINKING]",num+1);
	
	test(LEFT);
	test(RIGHT);
	
	sem_post(&mutex);
}

void getFork(int num){

	sem_wait(&mutex);
	state[num] = HUNGRY;
	
	printf("\n[PHILOSOPTHER] : %d is [HUNGRY]", num+1);
	test(num);
	
	sem_post(&mutex);
	sem_wait(&S[num]);
	sleep(1);

}


void *philosopher(void *arg){
	while(1){
		int *i = arg;
		sleep(2);
		getFork(*i);
		sleep(2);
		putFork(*i);
	}
}


int main(){
	pthread_t th[N];
	
	sem_init(&mutex, 0, 1);
	
	for(int i=0;i<N;i++){
		sem_init(&S[i], 0, 0);
		state[i] = THINKING;
		printf("\n[PHILOSOPTHER] : %d is [THINKING]", i+1);
	}
	
	for(int i =0;i<N;i++)
		pthread_create(&th[i], NULL, philosopher, &phil[i]);
	
	for(int i=0;i<N;i++)
		pthread_join(th[i], NULL);
	return 0;
}
