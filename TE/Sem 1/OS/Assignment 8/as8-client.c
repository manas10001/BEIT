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

int main(){
	
	int shmId;
	SHARED_MEMORY *mem_ptr;
	int cdata[SZ];
	
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
		
	//WAIT FOR SERVER TO WRITE DATA
	
	while(mem_ptr->status != DATA_FILLED_BY_SERVER);
	
	//read data once it is ready
	
	for(int i=0;i<SZ;i++)
		cdata[i] = mem_ptr->data[i];
	
	//print data
	
	for(int j = 0;j<SZ;j++)
		printf("data[%d] = %d\n",j,cdata[j]);
		
	mem_ptr->status = DATA_READ_BY_CLIENT;	
	
	return 0;
}
