#include <sys/wait.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//swap two elements
void swap(int* a, int* b) 
{ 
    int t = *a;
    *a = *b; 
    *b = t; 
}


int partition (int arr[], int low, int high) 
{ 
    int pivot = arr[high];	//assume last index element as pivote

    int i = (low - 1);
    for (int j = low; j <= high- 1; j++)	//iterate thr array
    { 
        if (arr[j] <= pivot) 		//arrange elements according to pivote
        { 
            i++;
            swap(&arr[i], &arr[j]); 
        } 
    } 
    swap(&arr[i + 1], &arr[high]); 
    return (i + 1); 
}
 //quiksort funcrtion
void quickSort(int arr[], int low, int high) 
{ 
    if (low < high) 
    { 
        int pi = partition(arr, low, high); 
        quickSort(arr, low, pi - 1); 
        quickSort(arr, pi + 1, high); 
    } 
} 


int main(){
	
	int n,i;
	char *ar[10];
	char str[sizeof(int)];
	
	printf("Enter no of array elements(max 10): ");
	scanf("%d",&n);

	int nos[n];
//arr ele to be max 10
	while(n>10 || n<1)
	{
		printf("Enter valid no of array elements(max 10): ");
		scanf("%d",&n);
	}
//accept array elements	
	printf("Enter array elements: \n");
	for(i=0;i<n;i++)
		scanf("%d",&nos[i]);

	//call quicksort on array		
	quickSort(nos, 0, n-1);	

	pid_t pid=fork();
	
	if(pid<0){
		printf("ERROR IN FORK\n");
	}
	if(pid==0)
	{
		printf("Child is running\nCalling execv\n");

//convert the sorted integer array to char * array
		for(i=0;i<n;i++)
		{
			sprintf(str,"%d",nos[i]);
			ar[i] = malloc(sizeof(str));
			strcpy(ar[i],str);
		}

		ar[i] = NULL;
		
//pass the converted array to next code
		execv("./2b2",ar);
				
		//printf("\n\t\tTHIS SHOUDNT EXECUTE\n");
	}
	if(pid>0)
	{
		printf("Parent is running\n"); 	
		
		wait(NULL);
		exit(0);
	}

	return 0;
}
