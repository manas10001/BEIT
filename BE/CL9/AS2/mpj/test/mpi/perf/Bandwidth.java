package mpi.perf; 
import mpi.*;
import java.nio.ByteBuffer ;
import java.io.* ; 

public class Bandwidth {
  public Bandwidth() {
  }
  public Bandwidth(String[] args) throws Exception {  
    int WARM_UP = 10000 ;
    int REPEAT = 20000 ;
    long[] delays = new long[REPEAT];
    long[] latency = new long[REPEAT];
    long start= 0L, stop=0L, init = 0L;
    MPI.Init(args);				
    int me = MPI.COMM_WORLD.Rank(); 
    System.out.println("Name ="+MPI.Get_processor_name());
    byte byteArray [] = null;
    int j=1, i=0;   	       		   	
    int LOG2N_MAX = 1000000,log2nbyte=0,padding=0;	
    long timed = 0L;		
    byteArray = new byte[16777216];
    PrintStream out = null ;
    FileOutputStream fos = null ;

    
    for(i =0;i < byteArray.length;i++) {
      byteArray[i] = 's';
    }

    /* Can do a warm-up here */
    if(me == 1) {
      fos = new FileOutputStream("bw_delays.out");
      out = new PrintStream (fos);
    }
    else {
      fos = new FileOutputStream("bw.out"); 
      out = new PrintStream (fos);
    } 

    for(i=0;i < 100 ;i++) {
      if(me == 0) {
        MPI.COMM_WORLD.Recv(byteArray,0,1,MPI.BYTE,1,998);
        MPI.COMM_WORLD.Send(byteArray,0,1,MPI.BYTE,1,998);
      }
      else if(me == 1) {
        MPI.COMM_WORLD.Send(byteArray,0,1,MPI.BYTE,0,998);
        MPI.COMM_WORLD.Recv(byteArray,0,1,MPI.BYTE,0,998);
      }
    }

    /* Logrithmic Loop */
    for (log2nbyte = 0; log2nbyte<= LOG2N_MAX && j<16*1024*1024; ++log2nbyte) { 

        //MPI.COMM_WORLD.Barrier();

	j = (1 << log2nbyte);

	/* Latency Calculation Loop */
	for (i = 0; i < REPEAT ; i++) {	   			
	  if(me == 0) {
	    myDelay( (int) (Math.random() * 1000)) ;
	    init = System.nanoTime() ; 
            MPI.COMM_WORLD.Send(byteArray,0,j,MPI.BYTE,1,998);	   		  	    MPI.COMM_WORLD.Recv(byteArray,0,j,MPI.BYTE,1,998);
	    latency[i] = ((System.nanoTime() - init)/1000) ; 
	  } else if(me == 1) {
            MPI.COMM_WORLD.Recv(byteArray,0,j,MPI.BYTE,0,998);
            start = System.nanoTime() ;
	    myDelay( (int)(Math.random() * 1000));
	    delays[i] = (System.nanoTime() - start)/1000 ;
            MPI.COMM_WORLD.Send(byteArray,0,j,MPI.BYTE,0,998);			
	  }
	}

        if(me == 0) {
          for(int k=WARM_UP; k<REPEAT ; k++) {
            out.println(latency[k]+"   ");
          }
        }

        else {
          for (i = WARM_UP ; i < REPEAT ; i++) {
            out.println(delays[i]+"   ");
          }
        }
        
        MPI.COMM_WORLD.Barrier() ;   

	//if(me == 0) { 
        //System.out.println(j+"           "+(latency*1000)+
	//	"           "+(( 8*j ) /( 1024*1024* (latency/(1000)))) );
	//}
    }//end logrithmic loop

    fos.close() ;
    out.close() ;  	 		

    MPI.Finalize();	  	
  }//end args constructor.
  
  static double tripsPerMS = 1000000 ;
  static int dummy ;

  static void myDelay(int us) {

    int trips = (int) (tripsPerMS * us) ;
    long start = System.nanoTime() ;

    for(int i = 0 ; i < trips ; i++) {
      dummy ++ ;
    }

    long actualDelay = System.nanoTime() - start ;

    if(actualDelay > 0 ) {
      long newTripsPerMS = (trips * 1000 ) / actualDelay ;

      if(newTripsPerMS > 0) {
        tripsPerMS = newTripsPerMS ;
      }
    }

  }
 
}//end class.
