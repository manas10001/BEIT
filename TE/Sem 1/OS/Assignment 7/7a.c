/*
Pipes: Full duplex communication between parent and child processes. Parent process writes a
pathname of a file (the contents of the file are desired) on one pipe to be read by child process
and child process writes the contents of the file on second pipe to be read by parent process and
displays on standard output.
*/
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>

int main(){

	int pip1[2],pip2[2];
	char path[50],data[1500],temp[20];
	pid_t pid;
	
	//create 2 pipes
	pipe(pip1);
	pipe(pip2);
	
	//fork to create child
	pid = fork();
	
	memset(&data[0],0,sizeof(data));
	
	if(pid<0){
		printf("Fork error!");
		return -1;
	}else if(pid==0){
		printf("\nChild process!");
		
		if(!read(pip1[0],path,sizeof(path))){
			printf("Error in reading path!");
			return -1;
		}
		close(pip1[0]);
		FILE *fp;
		fp = fopen(path,"r");
		
		//read file data in a variable
		while(fscanf(fp,"%s",temp)!=EOF){
			strcat(data,temp);
			strcat(data," ");
		}
		
		//write data to a pipes
		
		if(!write(pip2[1],data,sizeof(data))){
			printf("error in write data!");
			return -1;
		}
		close(pip2[1]);
		
	}else{
		printf("\nparent!");
		printf("\nEnter file path to read file!");
		scanf("%s",path);
		
		if(!write(pip1[1],path,sizeof(path))){
			printf("error in write path!");
			return -1;
		}
		close(pip1[1]);
		
		sleep(1);
		
		read(pip2[0],data,sizeof(data));
		
		printf("\nData of file in parent is: %s\n",data);
	}
	
	
	return 0;
}
