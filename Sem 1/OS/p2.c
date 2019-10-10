#include<stdio.h>

//binary search algorithm
int binSearch(int a[], int f, int l, int key)
{
        if(l >= f)
        {
                int mid = f+(l-f)/2;
                
                if(a[mid] == key)
                        return mid;
                if(a[mid] > key)
                        return binSearch(a, f, mid-1, key);
                return binSearch(a, mid+1, l, key);
        }
        return -1;
}

int main(int argc,char *argv[])
{
	int arr[argc],key,res;
	
//convert string array to int array
///and print the sorted array
	printf("Sorted array is :");

	for(int i=0;i<argc;i++)
	{
		arr[i] = atoi(argv[i]);
		printf("\t%d",arr[i]);
	}

	while(1)
	{
	//accept search key
		printf("\nEnter key to be searched (10001 for exit) :");
		scanf("%d",&key);  
		
		if(key == 10001)
		{
			printf("Exiting Program");
			break;
		}
		
	//binary search returns location of element if found else returns -1
		res = binSearch(arr, 0, argc-1, key);
		
		if(res == -1) 
		        printf("%d not found..",key); 
		else
		        printf("%d Found at location %d\n",key,res+1);
	}
	return 0;

}
