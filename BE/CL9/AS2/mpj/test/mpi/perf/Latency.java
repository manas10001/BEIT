package mpi.perf; 
import mpi.*;
import java.nio.ByteBuffer ;
import java.io.FileOutputStream ;
import java.io.PrintStream ;
import java.io.Serializable ; 

public class Latency {
  public Latency() {
  }
  public Latency(String[] args) throws Exception {  

    int OBJ = 1 ; //Integer.parseInt(args[0]) ; 
    
    int REPEAT = 1 ; 
    int WARM_UP = 0 ; 
    long ST = 30000000 ;
    int SENSIBLE = 0;
    long start= 0L, stop;
    long totalTime = 0L;
    int j,i,me ;
    long timed = 0L;		
    long[] latency = new long[REPEAT] ;
    long[] delays = new long[REPEAT] ;
    byte [] byteArray = new byte [1*1024*1024];
    Long min = Long.MAX_VALUE ;
    Long max = Long.MIN_VALUE ;
    long init = 0L; 
    PrintStream out = null ;
    FileOutputStream fos = null ;
    
    MPI.Init(args);				
    me = MPI.COMM_WORLD.Rank(); 
    j=1;

    Message [] msg = new Message[1] ;
    for(int e=0 ; e<msg.length ; e++) { 
      msg[e] = new Message() ; 
    }
    
    for(i =0;i < byteArray.length;i++) {
      byteArray[i] = 's';
    }

    if(me == 1) {
      fos = new FileOutputStream("mpj_delays.out");
      out = new PrintStream (fos);
    }
    
    for(i=0;i < WARM_UP ;i++) {
      if(me == 0) {
	if(OBJ == 1) { 
          MPI.COMM_WORLD.Recv(msg,0,msg.length,MPI.OBJECT,1,998);
	  MPI.COMM_WORLD.Send(msg,0,msg.length,MPI.OBJECT,1,998);
	}
	else {
          MPI.COMM_WORLD.Recv(byteArray,0,byteArray.length,MPI.BYTE,1,998);
	  MPI.COMM_WORLD.Send(byteArray,0,byteArray.length,MPI.BYTE,1,998);
	}
      }
      else if(me == 1) {
	if(OBJ == 1) { 
          MPI.COMM_WORLD.Send(msg,0,msg.length,MPI.OBJECT,0,998);
	  MPI.COMM_WORLD.Recv(msg,0,msg.length,MPI.OBJECT,0,998);
	}
	else {
          MPI.COMM_WORLD.Send(byteArray,0,byteArray.length,MPI.BYTE,0,998);
	  MPI.COMM_WORLD.Recv(byteArray,0,byteArray.length,MPI.BYTE,0,998);
	}
      }
    }

    /* Logrithmic Loop */
    for(int k=0; k<REPEAT ;k++) {
	
	/* Latency Calculation Loop */
        if(me == 0) {
	  //myDelay( (int)(1000* ((double)rand() / 
            //                   ((double)(RAND_MAX)+(double)(1)))));	
          myDelay( (int) (Math.random() * 1000));
          init = System.nanoTime() ;	  
	  if(OBJ == 1)  {
	    MPI.COMM_WORLD.Send(msg,0,msg.length,MPI.OBJECT,1,998);	 
	    MPI.COMM_WORLD.Recv(msg,0,msg.length,MPI.OBJECT,1,998);
	  }
	  else {
	    MPI.COMM_WORLD.Send(byteArray,0,byteArray.length,MPI.BYTE,1,998);
	    MPI.COMM_WORLD.Recv(byteArray,0,byteArray.length,MPI.BYTE,1,998);
	  }
 	  latency[k] = ( (System.nanoTime() -init)/1000 );	
	} else if(me == 1) {
	  if(OBJ == 1) {
	    MPI.COMM_WORLD.Recv(msg,0,msg.length,MPI.OBJECT,0,998);
	  }
	  else {
	    MPI.COMM_WORLD.Recv(byteArray,0,byteArray.length,MPI.BYTE,0,998);
	  }
          start = System.nanoTime() ;		
          myDelay( (int)(Math.random() * 1000));
	  delays[k] = (System.nanoTime() - start)/1000 ;
	  if(OBJ == 1) {
	    MPI.COMM_WORLD.Send(msg,0,msg.length,MPI.OBJECT,0,998);
	  }
	  else {
	    MPI.COMM_WORLD.Send(byteArray,0,byteArray.length,MPI.BYTE,0,998);
	  }
	}
		
	/* End latency calculation loop */

	
    }//end logrithmic loop

    if(me == 0) {
      long total = 0L ;
      long sensibleTotal = 0L ;
      
      for(int k=0 ; k<REPEAT ; k++) {
        System.out.println(latency[k] );
	total += latency[k] ; 
	
	if ((new Long(latency[k])).compareTo(min) < 0) {
          min = latency[k] ;		
	}
	
	if ((new Long(latency[k])).compareTo(max) > 0) {
          max = latency[k] ;		
	}
	
	/*
	if ((new Long(latency[k])).compareTo(new Long(ST)) < 0) {
          SENSIBLE++ ;
	  sensibleTotal += latency[k] ;
	}
	*/

      }
      
      System.out.println("# min <"+min.longValue()+">");
      System.out.println("# max <"+max.longValue()+">");
      System.out.println("# avg1 (insane)<"+total/REPEAT+">");
      //System.out.println("# avg2 (sane)<"+sensibleTotal/SENSIBLE+">");
      //System.out.println("# outrageous counts "+(REPEAT - SENSIBLE));
    }

    else {
      for (i = 0; i < REPEAT ; i++) {
        out.println(delays[i]);
      }
      
      out.close() ;
      fos.close();
    }
    	 		
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

class Message implements Serializable { 
  byte[] msg = new byte[1] ; 	
}
