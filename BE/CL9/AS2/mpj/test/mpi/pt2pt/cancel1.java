package mpi.pt2pt; 

import mpi.*;

public class cancel1 {
  static public void main(String[] args) throws Exception {
  }

  public cancel1() {
  }
  
  public cancel1(String[] args) throws Exception {
    int me,cnt=1,src=-1,tag;
    int data[] = new int[1];
    boolean flag;
    Intracomm comm;
    Status status = null;
    Request request = null; 
	
    MPI.Init(args);
    comm = MPI.COMM_WORLD;
    me = comm.Rank();

    if(me == 0) {
      data[0] = 7; 
      request = comm.Isend(data,0,1,MPI.INT,1,1);
      request.Cancel(); 
      status = request.Wait(); 

      if(status.Test_cancelled()) {
        System.out.println("Error(2): Should not be cancelled"); 	      
      }

    } 
    else if(me == 1)  {
      request = comm.Irecv(data, 0, 1, MPI.INT, 0, 1); 	    
     System.out.println("rank 1 calling Wait"); 
      status = request.Wait(); 
     System.out.println("rank 1 called Wait"); 

      if(data[0] != 7) {
        System.out.println("Error(1): Should be 7"); 	      
      }

    }


	
    comm.Barrier();
    System.out.println("Cancel1 TEST COMPLETE\n");
    MPI.Finalize();
    System.out.println("Even ..finalized");
  }
}
