package mpi.threads ;

import mpi.*;
import java.util.Arrays;

public class SimulSendRecv {
	
  int DATA_SIZE = 100;	
  
  public SimulSendRecv() {
  }
  
  public SimulSendRecv(String args[]) throws Exception {

    MPI.Init(args);
    Thread threadA = null ; 
    Thread threadB = null; 
    int me = MPI.COMM_WORLD.Rank() ;
    int size = MPI.COMM_WORLD.Size() ; 
    int numOfProcessors = MPI.NUM_OF_PROCESSORS ; 
      
    final int intArray [] = new int[DATA_SIZE];
    final int intArray2 [] = new int[DATA_SIZE];

    final int intReadArray [] = new int[DATA_SIZE];
    final int intReadArray2 [] = new int[DATA_SIZE];
		
    for(int i =0 ; i<DATA_SIZE ; i++) {
      intArray[i] =  (i+1) + 1000 ;
      intArray2[i] = (i+1) + 2000 ;
    }    	
		
    if(me == 0) { 
      
      Runnable senderThreadA = new Runnable() { 
        public void run() { 
          int tag = 1000 ; 		
	  for(int j=0 ; j<500 ; j++) { 
            MPI.COMM_WORLD.Send(intArray,0,DATA_SIZE,MPI.INT,1,tag++);
	  }
	}
      }; 
      

      Runnable senderThreadB = new Runnable() { 
        public void run() {
          int tag = 2000;		
	  for(int j=0 ; j<500 ; j++) { 
            for(int k=0 ; k<DATA_SIZE ; k++) { 		 
              intReadArray2[k] = 0; 
	    }
            MPI.COMM_WORLD.Recv(intReadArray2,0,DATA_SIZE,MPI.INT,1,tag++);
	    if(Arrays.equals(intArray2, intReadArray2)) {
	      System.out.println(" Thread B passed "); 	    
	    }
	    else {
              System.out.println(" Thread B failed "); 		    
	    }
	  }
	}
      };

      threadA = new Thread(senderThreadA); 
      threadB = new Thread(senderThreadB); 
      threadA.start(); 
      threadB.start(); 
      threadA.join(); 
      threadB.join(); 

    } else if (me == 1) { 
	    
      Runnable receiverThreadA = new Runnable() { 
        public void run() { 
          int tag = 1000 ;  		
	  for(int j=0 ; j<500 ; j++) { 
            for(int k=0 ; k<DATA_SIZE ; k++) { 		 
              intReadArray[k] = 0; 
	    }
            MPI.COMM_WORLD.Recv(intReadArray,0,DATA_SIZE,MPI.INT,0,tag++);
	    if(Arrays.equals(intArray, intReadArray)) {
	      //System.out.println(" Thread A passed "); 	    
	    }
	    else {
              System.out.println(" Thread A failed "); 		    
	    }
	  }
	}
      }; 

      Runnable receiverThreadB = new Runnable() { 
        public void run() { 
          int tag = 2000 ; 		
	  for(int j=0 ; j<500 ; j++) { 
            MPI.COMM_WORLD.Send(intArray2,0,DATA_SIZE,MPI.INT,0,tag++);
	  }
	}
      };

      threadA = new Thread(receiverThreadA); 
      threadB = new Thread(receiverThreadB); 
      threadA.start(); 
      threadB.start(); 
      threadA.join(); 
      threadB.join(); 

    }
       	
    MPI.COMM_WORLD.Barrier();
    if(MPI.COMM_WORLD.Rank() == 0) {
      System.out.println("SimulSendRecv TEST Completed");	
    }
    MPI.Finalize();

  } 
    
  public static void main(String args[]) throws Exception {
    SimulSendRecv test = new SimulSendRecv(args);
  }
}
