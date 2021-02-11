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
  char* sbuf     = (char *) malloc (LEN*sizeof(char));
 // char* rbuf     = (char *) malloc (LEN*sizeof(char));   	
  int WARM_UP = 0;
  int REPEAT = 1;			
  int j=1,i=0;   	       		   	
  int rank =0;
  int LOG2N_MAX = 1000000,log2nbyte=0,padding=0;	
  double timed = 0.0;		
  double latency = 0.0;
    
    for(i =0;i < LEN ;i++) {
      sbuf[i] = 's';
      //rbuf[i] = 'x';
    }
  
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);    

    /* Logrithmic Loop */
    for (log2nbyte = 0; (log2nbyte <= LOG2N_MAX) && (j <= LEN); ++log2nbyte) { 

	j = (1 << log2nbyte);

	/* Warm Up Loop */
	for(i=0;i < WARM_UP ;i++) {
	  if(rank == 0) {	
MPI_Recv(sbuf, j, MPI_CHAR, 1, 998, MPI_COMM_WORLD, &status);		  
MPI_Send(sbuf, j, MPI_CHAR, 1, 998, MPI_COMM_WORLD);		  
	  }
	  else if(rank == 1) {
MPI_Send(sbuf, j, MPI_CHAR, 0, 998, MPI_COMM_WORLD);		  
MPI_Recv(sbuf, j, MPI_CHAR, 0, 998, MPI_COMM_WORLD , &status);		  
	  }		
	}
	
	/* Warm Up Loop */                
	start = MPI_Wtime();				
		
	/* Latency Calculation Loop */
	
	for (i = 0; i < REPEAT ; i++) {	   	
	  if(rank == 0) {
MPI_Send(sbuf, j, MPI_CHAR, 1, 998, MPI_COMM_WORLD);		  
MPI_Recv(sbuf, j, MPI_CHAR, 1, 998, MPI_COMM_WORLD , &status);		  
	  } else if (rank == 1) {
MPI_Recv(sbuf, j, MPI_CHAR, 0, 998, MPI_COMM_WORLD, &status);		  
MPI_Send(sbuf, j, MPI_CHAR, 0, 998, MPI_COMM_WORLD);		  
	  }
	}		
		
	stop = MPI_Wtime();
	timed = stop - start;			
	/* End latency calculation loop */
 	latency = ( ((timed)/(2*REPEAT) )*1000*1000);	
	if(rank == 0) {
        printf("%d\t%.2f\t%.2f\n", j , (latency), 
		            (( 8*j ) /( 1024*1024* (latency/(1000*1000)))) );
	}
    }//end logrithmic loop
    	 		
    MPI_Barrier(MPI_COMM_WORLD);    		
    MPI_Finalize();	  	
    return 0;
}
