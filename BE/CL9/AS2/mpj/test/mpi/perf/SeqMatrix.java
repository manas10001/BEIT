package mpi.perf; 

import mpi.*; 

public class SeqMatrix {

  public static int NUM = 3000 ; 
  public static int NRA=NUM; 		/* number of rows in matrix A */
  public static int NCA=NUM;		/* number of columns in matrix A */
  public static int NCB=NUM;   		/* number of columns in matrix B */

  int loops = 100 ;
  
  public static void main(String args[]) throws Exception {	
    SeqMatrix matrixCalc = new SeqMatrix(args) ; 	    
  }

  public SeqMatrix() { 
  }

  public SeqMatrix (String args[]) throws Exception { 
    
    byte[][] recvArray = new byte[loops][] ;
    byte[][] sendArray = new byte[loops][] ;

    int rank ; 
    int size ; 
    int partner ; 
    int tag = 998 ; 
    int l,m,n ;
    
    Request [] request = new Request[loops] ;

    for(l=0 ; l<loops ; l++) {
      sendArray[l] = new byte[1] ;
      recvArray[l] = new byte[1] ;
    }
    
    MPI.Init(args) ; 	  
    System.out.println("Name ="+MPI.Get_processor_name());
    
    rank = MPI.COMM_WORLD.Rank() ; 
    size = MPI.COMM_WORLD.Size() ; 
    partner = (rank == 0 ? 1 : 0) ; 


    for(l=0 ; l<loops ; l++) {
      request[l] = MPI.COMM_WORLD.Irecv(recvArray[l],0,1,MPI.BYTE,
                                        MPI.ANY_SOURCE , l);
    }
	  
    int numtasks,		/* number of tasks in partition */
        taskid,			/* a task identifier */
        numworkers,		/* number of worker tasks */
        source,			/* task id of message source */
        dest,			/* task id of message destination */
        nbytes,			/* number of bytes in message */
        mtype,			/* message type */
        rows,                   /* rows of matrix A sent to each worker */
        averow, extra, offset,  /* used to determine rows sent to each worker */
        i, j, k,		/* misc */
        count;
		
    double a[][] = new double[NRA][];//NCA 	/* matrix A to be multiplied */
    double b[][] = new double[NCA][];//NCB   	/* matrix B to be multiplied */
    double c[][] = new double[NRA][];//NCB	/* result matrix C */

    for (i=0; i<NRA; i++) {
      a[i] = new double[NCA];
      for (j=0; j<NCA; j++) {					
        a[i][j]= i+j;
      }
    }

    for (i=0; i<NCA; i++) {
      b[i] = new double[NCB];
      for (j=0; j<NCB; j++) {
        b[i][j]= i*j;
      }
    }
			
    for (i=0; i<NRA; i++) {
      c[i] = new double[NCB];
      for (j=0; j<NCB; j++) {
        c[i][j]= 0d;
      }
    }

    long start = System.nanoTime() ; 
    /* Compute */
    for (k=0; k<NCB; k++) {
      for (i=0; i<NRA; i++) {
        c[i][k] = 0.0;
        for (j=0; j<NCA; j++)
          c[i][k] = c[i][k] + a[i][j] * b[j][k];
      }
    }

    long stop = System.nanoTime() ; 

    System.out.println("time ="+ ((double)stop-start)/(1000*1000*1000));

    for(l=0 ; l<loops ; l++) {
      MPI.COMM_WORLD.Send(sendArray[l],0,1,MPI.BYTE,partner,l);
    }

    for(l=0 ; l<loops ; l++) {
      request[l].Wait() ;
    }

    MPI.Finalize() ; 

  } 
}//end of class
