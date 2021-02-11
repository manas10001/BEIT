package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

/**
 * This checks the sending/receiving of the java objects.
 * @author Aamir Shafi 
 */ 
public class BufferTest6 {
   static public void main(String[] args) throws Exception {
	  try{
		BufferTest6 c = new BufferTest6(args);
	  }catch(Exception e){
	  }
  }

    public BufferTest6() {
    }

    public BufferTest6(String[] args) throws Exception {   

	    MPI.Init(args);

	    int me = MPI.COMM_WORLD.Rank();


	    java.util.Vector vector1 = null;
	    java.util.Vector vector = new java.util.Vector();
	    
	    for(int i=0 ; i<10 ; i++) {
		    vector.add(i+"");
	    }
	    
	    Object [] source = new Object[5];
	    source[0] = vector;
	    source[1] = vector; 
	    source[2] = vector; 
	    source[3] = vector; 
	    source[4] = vector;
	    
	    Object [] dest = new Object[5];
	    dest[0] = null; dest[1] = null; dest[2] = null; dest[3] = null; dest[4] = null;		    	   
	    
	    if(MPI.COMM_WORLD.Rank() == 0) {	 
	      MPI.COMM_WORLD.Send(source,0,5,MPI.OBJECT,1,10);
	    } else if (MPI.COMM_WORLD.Rank() == 1) {
	    //System.out.println("Receiving the messages");
	    MPI.COMM_WORLD.Recv(dest,0,5,MPI.OBJECT,0,10);   

	    
	    if (Arrays.equals(source,dest)) {

                System.out.println("(mpi)BufferTest6 TEST Completed");
	    }else {
		    System.out.println("\n****");
		    System.out.println("FAIL");
		    System.out.println("****");
	    }
	 }

  
    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }    
}
