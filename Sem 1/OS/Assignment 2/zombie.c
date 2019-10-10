//zombie process simulation
#include<stdio.h>

void main()
{
	int pid;
	pid=fork();
	
	if(pid==0)
	{
		printf("\nChild is running\n");
		printf("\nChild pid is :%d\n",getpid());
		printf("\nold parents pid is :%d\n",getppid());
		sleep(1);
		//printf("\nnew parent: %d\n",getppid());
	}
	if(pid>0)
	{
		printf("\n Parent is running\n");
		sleep(10);
		printf("\n in parent pid is :%d\n",getpid());
		wait(NULL);
	
	}
	

}
