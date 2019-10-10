//orphan process simulation
#include<stdio.h>

void main()
{
	int pid,i,j,temp,min;
	int ar[5];
	printf("Enter 5 array elements: \n");
	for(i=0;i<5;i++)
		scanf("%d",&ar[i]);
	
	
	printf("Elements are: ");
	for(i=0;i<5;i++)
		printf(" %d ",ar[i]);
		
	printf("\nExecuting fork now \n");
	pid=fork();
	
	if(pid==0)
	{
		printf("\nChild is running\n");
		
		//sort the array elements 
		//BUBBLE SORT
		for(i=0;i<5;i++)
		{
			for(j=0;j<5;j++)
			{
				if(ar[j]>ar[j+1])
				{
					temp = ar[j];
					ar[j] = ar[j+1];
					ar[j+1] = temp;
				}
			}
		}
		
		printf("\nElements after sorting in child are:\n ");
		for(i=0;i<5;i++)
			printf(" %d ",ar[i]);
		
		printf("\n\nChild pid is :%d\n",getpid());
		printf("\nold parents pid is :%d\n",getppid());
		sleep(5);
		printf("\nnew parent: %d\n",getppid());
	}
	if(pid>0)
	{
	
		printf("\nParent is running\n");
		sleep(1);
		//sorting again with different algo
		//SELECTION SORT
		for(i = 0;i<5;i++)
		{
			min=ar[i];
			
			for(j=i+1;j<5;j++)
			{
				if(ar[j]<min)
				{
					temp = min;
					min = ar[j];
					ar[j] = temp;
				}
			}
			ar[i]=min;
		}
		
		printf("\nElements after sorting in parent are:\n ");
		for(i=0;i<5;i++)
			printf(" %d ",ar[i]);
		
		printf("\n\nin parent pid is :%d\n",getpid());
		printf("\n in parent ppid is :%d\n",getppid());
	}	
}
