#include <stdio.h>
#include <unistd.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/stat.h>

#define SZ 3
#define SERVER_START_SEND -1
#define SERVER_SENT 0
#define CLIENT_RECIVED 1
#define CLIENT_WRITING 2
#define CLIENT_WROTE 3

typedef struct sh{
	int status;
	int data[SZ];
	int data2[SZ];
}shared;

int main(int argc, char* argv[]){

	if(argc>4){
		printf("3 arguments required");
		return -1;
	}
	
	int shmId,ar[SZ];
	shared *mem;
	
	key_t key = ftok(".",'Z');
	
	
	//shmget call
	shmId = shmget(key,sizeof(shared), IPC_CREAT | 0666);
	if(shmId == -1){
		printf("Shmget failed!");
		return -1;
	}
	printf("Shmget done\n");
	
	//attach the memory
	mem = (shared*) shmat(shmId,NULL,0);
	if(mem == NULL){
		printf("shmat failed!");
		return -1;
	}
	
	printf("shmat done\n");
	
	mem->status = SERVER_START_SEND;
	
	while(mem->status == SERVER_START_SEND){
		printf("Waiting for server to write\n");
		sleep(1);
	}
	
	for(int i=0;i<SZ;i++){
		ar[i] = mem->data[i];
	}
	
	mem->status = CLIENT_RECIVED;
	sleep(1);
	mem->status = CLIENT_WRITING;
	
	int j = 1;
	for(int i=0;i<SZ;i++){
		mem->data2[i] = atoi(argv[j++]);	
	}
	
	mem->status = CLIENT_WROTE;
	
	
	printf("Client read done\n Client read this: ");

	for(int i = 0;i<SZ;i++)
		printf("no[%d] = %d\n",i,ar[i]);


	//detach 
	shmdt((void *)mem);
	
	int res = shmctl(shmId, IPC_RMID, NULL);
	
	if(res>0){
		printf("Error in shmctl!");
		return -1;
	}
	
	return 0;
}
