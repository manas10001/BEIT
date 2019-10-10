//orphan process simulation
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
		sleep(5);
		printf("\nnew parent: %d\n",getppid());
	}
	if(pid>0)
	{
		printf("\nParent is running\n");
		sleep(1);
		printf("\nin parent pid is :%d\n",getpid());
		printf("\n in parent ppid is :%d\n",getppid());
	
	}
	

}
