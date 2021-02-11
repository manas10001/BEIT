#include <stdio.h>
#include "mpi.h"
#define LEN 16777216

int main(int argc, char** argv) 
{  
  MPI_Init(&argc, &argv);				    
  double start=0.0, stop=0.0;
  double totalTime = 0.0;
  //char sbuf[LEN], rbuf[LEN];
  
  MPI_Status status;
  //char* sbuf     = (char *) malloc (LEN*sizeof(char));
  //char* rbuf     = (char *) malloc (LEN*sizeof(char));   	
  int WARM_UP = 10;
  int REPEAT = 10;			
  int size = 0;
  int j=1,i=0;   	       		   	
  int rank =0;
  int LOG2N_MAX = 1000000,log2nbyte=0,padding=0;	
  double timed = 0.0;		
  double latency = 0.0;
    /*
    for(i =0;i < LEN ;i++) {
      sbuf[i] = 's';
      //rbuf[i] = 'x';
    }*/
  
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);   
	/* Warm Up Loop */
	for(i=0;i < WARM_UP ;i++) {
 	  MPI_Barrier(MPI_COMM_WORLD);		  
	}
	
	/* Warm Up Loop */                
	start = MPI_Wtime();				
		
	/* Latency Calculation Loop */
	
	for (i = 0; i < REPEAT ; i++) {	   	
 	  MPI_Barrier(MPI_COMM_WORLD);		    
	}		
		
	stop = MPI_Wtime();
	timed = stop - start;			
	/* End latency calculation loop */
 	latency = ((timed*1000*1000)/(REPEAT));	
	if(rank == size-1) {
        printf("this time is in microseconds");		
        printf("%d\t%.2f\t%.2f\n", size , (latency), 
		            (( 8*j ) /( 1024*1024* (latency/(1000*1000)))) );
	}
   
    	 		
    MPI_Barrier(MPI_COMM_WORLD);    		
    MPI_Finalize();	  	
    return 0;
}
