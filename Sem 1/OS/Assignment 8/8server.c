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
	
	if(argc!=4){
		printf("Enter at exactly 3 arguments!");
		return -1;
	}

	int arr[SZ];
	int shmId;
	shared *mem;
	
	key_t key = ftok(".",'Z');

	
	//shmget call
	shmId = shmget(key,sizeof(shared), IPC_CREAT | 0666);
	if(shmId == -1){
		printf("Shmget failed!");
		return -1;
	}
	printf("\nShmget done");
	//attach the memory
	mem = (shared*) shmat(shmId,NULL,0);
	if(mem == NULL){
		printf("shmat failed!");
		return -1;
	}
	
	printf("\nshmat done");
	
	//write data set status
	mem->status = SERVER_START_SEND;
	
	int j = 1;
	for(int i=0;i<SZ;i++){
		mem->data[i] = atoi(argv[j++]);	
	}
	mem->status = SERVER_SENT;
	
	while(mem->status != CLIENT_RECIVED){
		printf("\nWaiting for client to read");
		sleep(1);
	}
	
	printf("Client read done");

	//mem->status = SERVER_START_SEND;
	
	while(mem->status != CLIENT_WROTE);
	
	for(int i=0;i<SZ;i++){
		arr[i] =  mem->data2[i];
		printf("\nClient wrote: %d",arr[i]);	
	}
	
	mem->status = SERVER_START_SEND;
	
	//detach 
	shmdt((void *)mem);
	
	int res = shmctl(shmId, IPC_RMID, NULL);
	
	if(res>0){
		printf("Error in shmctl!");
		return -1;
	}
	
	return 0;
}
