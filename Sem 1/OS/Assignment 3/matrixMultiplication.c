/*To implement C program for Matrix Multiplication using POSIX pthreads*/
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>


//structure to store some temp data
struct data{
	int a,b;
};

//the function to be threaded
void * matmul(void *arg)
{
	struct data *d =arg;
	int *mul = malloc(sizeof(int));
	*mul=d->a*d->b;
	pthread_exit( (void *) mul );
}

//accept matrix
int* accept(int *mat,int r,int c)
{
	for(int i=0;i<r;i++)
	{
		for(int j=0;j<c;j++)
		{
			printf("Enter element[%d][%d]: ",i,j);
			scanf("%d",(mat + i*c + j));	
		}
	}
	return mat;
}

//display matrix
void display(int *mat,int r,int c)
{
	for(int i=0;i<r;i++)
	{
		printf("|\t");
		for(int j=0;j<c;j++)
			printf("%d\t",*(mat + i*c +j));
		printf("|\n");
	}
}

int main()
{
	int r1=0,r2=0,c1=0,c2=0,flag=0,i,j,k;
	
	while(flag==0)
	{
		printf("Enter no of rows and columns of matrix 1: ");
		scanf("%d %d",&r1,&c1);
	
		printf("Enter no of rows and columns of matrix 2: ");
		scanf("%d %d",&r2,&c2);
		
		flag = 1;
		//validation for multiplication
		if(c1!=r2)
		{
			printf("The matrix you are trying to use are incompatible for multiplication\nEnter again\n");
			flag=0;
		}
	}
	
	
//allocate memory	
	int *mat1 = (int *) malloc(r1*c1*sizeof(int));		//matrix1
	int *mat2 = (int *) malloc(r2*c2*sizeof(int));		//matrix2
	int *res = (int *) malloc(r1*c2*sizeof(int));		//resultant matrix

//accept matrix and display 
	printf("Enter elements of matrix 1: ");
	
	mat1=accept(mat1,r1,c1);
	printf("Matrix 1 is:\n");
	display(mat1,r1,c1);
	
	mat2=accept(mat2,r2,c2);
	printf("Matrix 2 is:\n");
	display(mat2,r2,c2);

//threading
	
	pthread_t tid;
	void *tRes;
	
	
	//matrix multiplication logic loops el(ij)=(((ik)*(kj))+((ik)*(kj)))
	for(i=0;i<r1;i++)
	{
		for(j=0;j<c2;j++)
		{
			for(k=0;k<c1;k++)
			{
				//temporary structure pointer which will be used to pass data to thread
				struct data *d;
				
				d->a=*(mat1 + i*c1 + k);
				d->b=*(mat2 + k*c2 + j);		
				
				pthread_create(&tid,NULL,matmul,d);
				pthread_join(tid,&tRes);
				
				//get result back and add the 2 multipliaction values according to matrix multiplication rules
				*(res + i*c2 +j) += *(int *)tRes;
				
 				//printf("\t %d \t %d \n",*(int *)tRes,*(res + i*c2 +j));
			}
		}
	}
	
	printf("Multiplication is:\n");
	display(res,r1,c2);
	
	return 0;
}

