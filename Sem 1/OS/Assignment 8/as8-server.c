#include <stdio.h>
#include <stdlib.h>
#include <sys/shm.h>
#include <sys/stat.h>

//define macros
#define SZ 3
#define DATA_NOT_FILLED_BY_SERVER -1
#define DATA_FILLED_BY_SERVER 0
#define DATA_READ_BY_CLIENT 1

typedef struct st{
	int status;
	int data[3];
}SHARED_MEMORY;


int main(int argc, char *argv[]){

	int shmId;
	SHARED_MEMORY *mem_ptr;
	
	//arguments should be 4 we are using 3 data elements
	if(argc!=4)
	{
		printf("There should be exactly 3 parameters!");
		return -1;
	}
	
	//shmget call

	shmId = shmget(ftok(",",'A'), sizeof(SHARED_MEMORY), IPC_CREAT | 0666);
	//shmId is -1 if shmget fails
	if(shmId < 0){
		printf("Error in shmget!");
		return -1;
	}
	
	printf("SHMGET SUCCESS!");
	
	//attach the shared memory
	
	mem_ptr = (SHARED_MEMORY*) shmat(shmId,NULL,0);
	
	//mem_ptr will be null if shmat fails
	if(mem_ptr == NULL){
		printf("Error in shmat!");
		return -1;
	}
	
	printf("MEMORY ATTACHED SUCCESSFULLY!");
	
	//write data
	//set status
	mem_ptr->status = DATA_NOT_FILLED_BY_SERVER;
	
	int j=1;
	//write data
	for(int i=0; i<SZ; i++)
		mem_ptr->data[i] =atoi(argv[j++]);
	
	//set status
	mem_ptr->status = DATA_FILLED_BY_SERVER;

	//wait for client
	while(mem_ptr !=DATA_READ_BY_CLIENT)
	{
		sleep(1);
		printf("SERVER WAITING...");
	}	
	
	//after clients takes data
	printf("Client has recived the data");

	int ret;
	
	//detach the space
	shmdt((void*)mem_ptr);
	
	//call shmctl
	ret = shmctl(shmId , IPC_RMID, NULL);
	
	if(ret <0 ){
		printf("Error in shmctl!");
		return -1;
	}

	return 0;
}
