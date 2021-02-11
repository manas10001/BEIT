package mpi.perf ; 
import mpi.*; 

public class WaitAnyKiller {

  public static int NUM = 1000 ; 
  public static int NRA=NUM; 		/* number of rows in matrix A */
  public static int NCA=NUM;		/* number of columns in matrix A */
  public static int NCB=NUM;   		/* number of columns in matrix B */

  static final int loops = 500 ; 
  Thread threadA = null ;
    final byte[][] recvArray = new byte[loops][1] ; 
    final byte[][] sendArray = new byte[loops][1] ; 
  
  public static void main(String args[]) throws Exception {	
    WaitAnyKiller matrixCalc = new WaitAnyKiller(args) ; 	    
  }

  public WaitAnyKiller() { 
  }

  public WaitAnyKiller (String args[]) throws Exception { 
    System.out.println("starting wait any killler" );	  
    //System.setProperty("ibis.name","nio") ; 
    
    int rank ; 
    int size ; 
    int partner ; 
    int tag = 998 ; 
    int l,m,n ; 
    

    //for(l=0 ; l<loops ; l++) { 
      //sendArray[l] = new byte[1] ; 
      //recvArray[l] = new byte[1] ; 
    //} 
    
    MPI.Init(args) ; 	  
    System.out.println("Name ="+MPI.Get_processor_name());
    
    rank = MPI.COMM_WORLD.Rank() ; 
    size = MPI.COMM_WORLD.Size() ; 
    //partner = (rank == 0 ? 1 : 0) ; 

    if(rank == 0) { 

      Runnable waitAnyThread = new Runnable() {
        public void run() {
          try {
            Request [] request = new Request[loops] ; 
	    int l=0 ;
            //for(int l=0 ; l<loops ; l++) { 
              request[l] = MPI.COMM_WORLD.Irecv(recvArray[l],0,1,MPI.BYTE,1,l);
            //}
            System.out.println(" Process 0 calling waitAny") ; 
	    //request[0].Wait() ;
            Request.Waitany(request) ; 
            System.out.println(" Process 1 called waitAny") ; 
          } catch(MPIException e){
            e.printStackTrace() ;		   
	  }
        }
      };
    
      threadA = new   Thread(waitAnyThread) ; 
      threadA.start() ; 
	  
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
      MPI.COMM_WORLD.Barrier() ; 

      threadA.join() ; 

    } else if(rank == 1) { 

      MPI.COMM_WORLD.Barrier() ; 
      l =0 ; 
      //for(l=0 ; l<loops ; l++) { 
      MPI.COMM_WORLD.Send(sendArray[l],0,1,MPI.BYTE,0,l);
      //} 

    } 


    MPI.Finalize() ; 

  } 
}//end of class
